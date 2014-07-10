/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.indexing;

import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaProject;

import com.cisco.yangide.core.CoreUtil;
import com.cisco.yangide.core.ElementChangedEvent;
import com.cisco.yangide.core.IYangElementChangedListener;
import com.cisco.yangide.core.IYangElementDelta;
import com.cisco.yangide.core.OpenableElementInfo;
import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.core.model.YangElement;
import com.cisco.yangide.core.model.YangElementType;
import com.cisco.yangide.core.model.YangModel;
import com.cisco.yangide.core.model.YangModelManager;
import com.cisco.yangide.core.model.YangProject;

/**
 * @author Konstantin Zaitsev
 * @date Jun 25, 2014
 */
@SuppressWarnings("restriction")
public class DeltaProcessor implements IResourceChangeListener {
    static class OutputsInfo {
        int outputCount;
        IPath[] paths;
        int[] traverseModes;

        OutputsInfo(IPath[] paths, int[] traverseModes, int outputCount) {
            this.paths = paths;
            this.traverseModes = traverseModes;
            this.outputCount = outputCount;
        }
    }

    /*
     * Collection of listeners for Java element deltas
     */
    public IYangElementChangedListener[] elementChangedListeners = new IYangElementChangedListener[5];
    public int[] elementChangedListenerMasks = new int[5];
    public int elementChangedListenerCount = 0;

    public int overridenEventType = -1;
    private YangModelManager manager;
    private boolean isFiring;
    public ArrayList<IYangElementDelta> yangModelDeltas = new ArrayList<IYangElementDelta>();
    private YangElementDelta currentDelta;

    private final static int IGNORE = 0;
    private final static int SOURCE = 1;
    private final static int BINARY = 2;

    /**
     * @param yangModelManager
     */
    public DeltaProcessor(YangModelManager manager) {
        this.manager = manager;
    }

    /*
     * Need to clone defensively the listener information, in case some listener is reacting to some
     * notification iteration by adding/changing/removing any of the other (for example, if it
     * deregisters itself).
     */
    public synchronized void addElementChangedListener(IYangElementChangedListener listener, int eventMask) {
        for (int i = 0; i < this.elementChangedListenerCount; i++) {
            if (this.elementChangedListeners[i] == listener) {

                // only clone the masks, since we could be in the middle of notifications and one
                // listener decide to change
                // any event mask of another listeners (yet not notified).
                int cloneLength = this.elementChangedListenerMasks.length;
                System.arraycopy(this.elementChangedListenerMasks, 0,
                        this.elementChangedListenerMasks = new int[cloneLength], 0, cloneLength);
                this.elementChangedListenerMasks[i] |= eventMask; // could be different
                return;
            }
        }
        // may need to grow, no need to clone, since iterators will have cached original arrays and
        // max boundary and we only add to the end.
        int length;
        if ((length = this.elementChangedListeners.length) == this.elementChangedListenerCount) {
            System.arraycopy(this.elementChangedListeners, 0,
                    this.elementChangedListeners = new IYangElementChangedListener[length * 2], 0, length);
            System.arraycopy(this.elementChangedListenerMasks, 0,
                    this.elementChangedListenerMasks = new int[length * 2], 0, length);
        }
        this.elementChangedListeners[this.elementChangedListenerCount] = listener;
        this.elementChangedListenerMasks[this.elementChangedListenerCount] = eventMask;
        this.elementChangedListenerCount++;
    }

    public synchronized void removeElementChangedListener(IYangElementChangedListener listener) {

        for (int i = 0; i < this.elementChangedListenerCount; i++) {

            if (this.elementChangedListeners[i] == listener) {

                // need to clone defensively since we might be in the middle of listener
                // notifications (#fire)
                int length = this.elementChangedListeners.length;
                IYangElementChangedListener[] newListeners = new IYangElementChangedListener[length];
                System.arraycopy(this.elementChangedListeners, 0, newListeners, 0, i);
                int[] newMasks = new int[length];
                System.arraycopy(this.elementChangedListenerMasks, 0, newMasks, 0, i);

                // copy trailing listeners
                int trailingLength = this.elementChangedListenerCount - i - 1;
                if (trailingLength > 0) {
                    System.arraycopy(this.elementChangedListeners, i + 1, newListeners, i, trailingLength);
                    System.arraycopy(this.elementChangedListenerMasks, i + 1, newMasks, i, trailingLength);
                }

                // update manager listener state (#fire need to iterate over original listeners
                // through a local variable to hold onto
                // the original ones)
                this.elementChangedListeners = newListeners;
                this.elementChangedListenerMasks = newMasks;
                this.elementChangedListenerCount--;
                return;
            }
        }
    }

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        try {
            int eventType = this.overridenEventType == -1 ? event.getType() : this.overridenEventType;
            IResource resource = event.getResource();
            IResourceDelta delta = event.getDelta();

            switch (eventType) {
            case IResourceChangeEvent.PRE_DELETE:
                if (resource.getType() == IResource.PROJECT && YangCorePlugin.isYangProject((IProject) resource)) {
                    deleting((IProject) resource);
                }
                return;

            case IResourceChangeEvent.PRE_REFRESH:
                // nothing to do on refresh
                return;

            case IResourceChangeEvent.POST_CHANGE:
                if (isAffectedBy(delta)) {
                    try {
                        stopDeltas();

                        // generate Yang deltas from resource changes
                        IYangElementDelta translatedDelta = processResourceDelta(delta);
                        if (translatedDelta != null) {
                            this.yangModelDeltas.add(translatedDelta);
                        }
                    } finally {
                        // necessary
                        startDeltas();
                    }
                    // TODO KOS: need implement notification for type hierarchy
                    fire(null, ElementChangedEvent.POST_CHANGE);
                }
                return;

            case IResourceChangeEvent.PRE_BUILD:
                // nothing to do on pre-build
                return;

            case IResourceChangeEvent.POST_BUILD:
                // nothing to do on post build
                return;
            }
        } finally {
            overridenEventType = -1;
        }
    }

    private void deleting(IProject project) {

        try {
            // discard indexing jobs that belong to this project so that the project can be
            // deleted without interferences from the index manager
            this.manager.indexManager.discardJobs(project.getName());

            YangProject yangProject = (YangProject) YangCorePlugin.create(project);
            yangProject.close();

            removeFromParentInfo(yangProject);
        } catch (YangModelException e) {
            // yang project doesn't exist: ignore
        }
    }

    /*
     * Turns the firing mode to on. That is, deltas that are/have been registered will be fired.
     */
    private void startDeltas() {
        this.isFiring = true;
    }

    /*
     * Turns the firing mode to off. That is, deltas that are/have been registered will not be fired
     * until deltas are started again.
     */
    private void stopDeltas() {
        this.isFiring = false;
    }

    /*
     * Converts a <code>IResourceDelta</code> rooted in a <code>Workspace</code> into the
     * corresponding set of <code>IJavaElementDelta</code>, rooted in the relevant
     * <code>JavaModel</code>s.
     */
    private IYangElementDelta processResourceDelta(IResourceDelta changes) {

        try {
            YangModel model = this.manager.getYangModel();
            if (!model.isOpen()) {
                // force opening of yang model so that java element delta are reported
                try {
                    model.open(null);
                } catch (YangModelException e) {
                    return null;
                }
            }
            // this.currentElement = null;

            // get the workspace delta, and start processing there.
            IResourceDelta[] deltas = changes.getAffectedChildren(IResourceDelta.ADDED | IResourceDelta.REMOVED
                    | IResourceDelta.CHANGED, IContainer.INCLUDE_HIDDEN);
            for (int i = 0; i < deltas.length; i++) {
                traverseDelta(deltas[i], null);
            }
            return this.currentDelta;
        } finally {
            this.currentDelta = null;
        }
    }

    private void removeFromParentInfo(YangElement child) {

        YangElement parent = (YangElement) child.getParent();
        if (parent != null && parent.isOpen()) {
            try {
                OpenableElementInfo info = (OpenableElementInfo) parent.getElementInfo(null);
                info.removeChild(child);
            } catch (YangModelException e) {
                // do nothing - we already checked if open
            }
        }
    }

    /*
     * Fire Java Model delta, flushing them after the fact after post_change notification. If the
     * firing mode has been turned off, this has no effect.
     */
    public void fire(IYangElementDelta customDelta, int eventType) {
        if (!this.isFiring) {
            return;
        }

        IYangElementDelta deltaToNotify;
        if (customDelta == null) {
            deltaToNotify = null; // TODO KOS: need merge delta for notify
            // mergeDeltas(this.yangModelDeltas);
        } else {
            deltaToNotify = customDelta;
        }

        // Notification
        // Important: if any listener reacts to notification by updating the listeners list or mask,
        // these lists will
        // be duplicated, so it is necessary to remember original lists in a variable (since field
        // values may change under us)
        IYangElementChangedListener[] listeners;
        int[] listenerMask;
        int listenerCount;
        synchronized (this) {
            listeners = this.elementChangedListeners;
            listenerMask = this.elementChangedListenerMasks;
            listenerCount = this.elementChangedListenerCount;
        }

        switch (eventType) {
        case ElementChangedEvent.POST_CHANGE:
            firePostChangeDelta(deltaToNotify, listeners, listenerMask, listenerCount);
            // fireReconcileDelta(listeners, listenerMask, listenerCount);
            break;
        }
    }

    private void firePostChangeDelta(IYangElementDelta deltaToNotify, IYangElementChangedListener[] listeners,
            int[] listenerMask, int listenerCount) {

        // post change deltas
        if (deltaToNotify != null) {
            // flush now so as to keep listener reactions to post their own deltas for subsequent
            // iteration
            this.yangModelDeltas = new ArrayList<IYangElementDelta>();

            notifyListeners(deltaToNotify, ElementChangedEvent.POST_CHANGE, listeners, listenerMask, listenerCount);
        }
    }

    private void notifyListeners(IYangElementDelta deltaToNotify, int eventType,
            IYangElementChangedListener[] listeners, int[] listenerMask, int listenerCount) {
        final ElementChangedEvent extraEvent = new ElementChangedEvent(deltaToNotify, eventType);
        for (int i = 0; i < listenerCount; i++) {
            if ((listenerMask[i] & eventType) != 0) {
                final IYangElementChangedListener listener = listeners[i];
                // wrap callbacks with Safe runnable for subsequent listeners to be called when some
                // are causing grief
                SafeRunner.run(new ISafeRunnable() {
                    @Override
                    public void handleException(Throwable exception) {
                        YangCorePlugin.log(exception,
                                "Exception occurred in listener of Java element change notification");
                    }

                    @Override
                    public void run() throws Exception {
                        listener.elementChanged(extraEvent);
                    }
                });
            }
        }
    }

    /*
     * Converts an <code>IResourceDelta</code> and its children into the corresponding
     * <code>IYangElementDelta</code>s.
     */
    private void traverseDelta(IResourceDelta delta, OutputsInfo outputsInfo) {
        // process current delta
        boolean processChildren = true;
        if (delta.getResource().getType() == IResource.PROJECT) {
            processChildren = updateCurrentDeltaAndIndex(delta);
        } else if (delta.getResource().getType() == IResource.FILE) {
            // skip non YANG files
            if (!CoreUtil.isYangLikeFileName(delta.getResource().getFullPath().toString())) {
                return;
            }
            processChildren = updateCurrentDeltaAndIndex(delta);
        }

        // process children if needed
        if (processChildren) {

            // get the project's output locations and traverse mode
            if (outputsInfo == null) {
                outputsInfo = outputsInfo(delta.getResource());
            }

            IResourceDelta[] children = delta.getAffectedChildren();
            int length = children.length;
            for (int i = 0; i < length; i++) {
                IResourceDelta child = children[i];
                IResource childRes = child.getResource();

                // is childRes in the output folder and is it filtered out ?
                boolean isResFilteredFromOutput = isResFilteredFromOutput(outputsInfo, childRes);

                if (!isResFilteredFromOutput) {
                    traverseDelta(child, outputsInfo);
                }
            }
        } // else resource delta will be added by parent

    }

    /*
     * Update the current delta (i.e. add/remove/change the given element) and update the
     * correponding index. Returns whether the children of the given delta must be processed.
     */
    public boolean updateCurrentDeltaAndIndex(IResourceDelta delta) {
        YangElement element;
        switch (delta.getKind()) {
        case IResourceDelta.ADDED:
            element = YangCorePlugin.create(delta.getResource());
            updateIndex(element, delta);
            elementAdded(element, delta);
            return false;
        case IResourceDelta.REMOVED:
            element = YangCorePlugin.create(delta.getResource());
            updateIndex(element, delta);
            elementRemoved(element, delta);
            return false;
        case IResourceDelta.CHANGED:
            int flags = delta.getFlags();
            if ((flags & IResourceDelta.CONTENT) != 0 || (flags & IResourceDelta.ENCODING) != 0) {
                // content or encoding has changed
                element = YangCorePlugin.create(delta.getResource());
                if (element == null) {
                    return false;
                }
                updateIndex(element, delta);
                contentChanged(element);
            } else if (delta.getResource().getType() == IResource.PROJECT) {
                if ((flags & IResourceDelta.OPEN) != 0) {
                    // project has been opened or closed
                    IProject res = (IProject) delta.getResource();
                    element = YangCorePlugin.create(res);

                    if (res.isOpen()) {
                        addToParentInfo(element);
                        this.manager.indexManager.indexAll(res);
                    } else {
                        close(element);
                        removeFromParentInfo(element);
                        this.manager.indexManager.discardJobs(element.getName());
                        this.manager.indexManager.removeIndexFamily(res);

                    }
                    return false; // when a project is open/closed don't process children
                } else {
                    IJavaProject javaProject = JavaCore.create((IProject) delta.getResource());
                    if (javaProject.isOpen()) {
                        try {
                            IClasspathEntry[] classpath = javaProject.getRawClasspath();
                            YangProject yangProject = (YangProject) YangCorePlugin.create(delta.getResource());
                            if (yangProject.isClasspathChanged(classpath)) {
                                yangProject.makeConsistent(null);
                                this.manager.indexManager.indexAll((IProject) delta.getResource());
                            }
                        } catch (YangModelException | JavaModelException e) {
                            YangCorePlugin.log(e);
                        }
                    }
                }
            }
            return true;
        }
        return true;
    }

    @SuppressWarnings("incomplete-switch")
    private void updateIndex(YangElement element, IResourceDelta delta) {

        IndexManager indexManager = this.manager.indexManager;
        if (indexManager == null) {
            return;
        }

        switch (element.getElementType()) {
        case YANG_PROJECT:
            switch (delta.getKind()) {
            case IResourceDelta.ADDED:
                indexManager.indexAll(element.getResource().getProject());
                break;
            case IResourceDelta.REMOVED:
                indexManager.removeIndexFamily(element.getResource().getProject());
                break;
            }
            break;
        case YANG_FILE:
            IFile file = (IFile) delta.getResource();
            switch (delta.getKind()) {
            case IResourceDelta.CHANGED:
                // no need to index if the content has not changed
                int flags = delta.getFlags();
                if ((flags & IResourceDelta.CONTENT) == 0 && (flags & IResourceDelta.ENCODING) == 0) {
                    break;
                }
            case IResourceDelta.ADDED:
                indexManager.addSource(file);
                break;
            case IResourceDelta.REMOVED:
                indexManager.remove(file);
                break;
            }
        }
    }

    /*
     * Processing for an element that has been added:<ul> <li>If the element is a project, do
     * nothing, and do not process children, as when a project is created it does not yet have any
     * natures - specifically a java nature. <li>If the elemet is not a project, process it as added
     * (see <code>basicElementAdded</code>. </ul> Delta argument could be null if processing an
     * external JAR change
     */
    private void elementAdded(YangElement element, IResourceDelta delta) {
        YangElementType elementType = element.getElementType();

        if (elementType == YangElementType.YANG_PROJECT) {
            // project add is handled by JavaProject.configure() because
            // when a project is created, it does not yet have a java nature
            if (delta != null) {
                IProject project = (IProject) delta.getResource();
                if (YangCorePlugin.isYangProject(project)) {
                    addToParentInfo(element);
                    close(element);
                    currentDelta().added(element);
                }
            }
        } else {
            if (delta == null || (delta.getFlags() & IResourceDelta.MOVED_FROM) == 0) {
                // regular element addition
                addToParentInfo(element);
                close(element);
                currentDelta().added(element);
            } else {
                // element is moved
                addToParentInfo(element);
                close(element);
                currentDelta().added(element);
            }
        }
    }

    /*
     * Generic processing for a removed element:<ul> <li>Close the element, removing its structure
     * from the cache <li>Remove the element from its parent's cache of children <li>Add a REMOVED
     * entry in the delta </ul> Delta argument could be null if processing an external JAR change
     */
    private void elementRemoved(YangElement element, IResourceDelta delta) {

        if (delta == null || (delta.getFlags() & IResourceDelta.MOVED_TO) == 0) {
            close(element);
            removeFromParentInfo(element);
            currentDelta().removed(element);
        } else {
            // element is moved
            close(element);
            removeFromParentInfo(element);
            currentDelta().removed(element);
        }
        if (element.getElementType() == YangElementType.YANG_MODEL) {
            this.manager.indexManager.reset();
        }
    }

    private void contentChanged(YangElement element) {
        close(element);
        currentDelta().changed(element, IYangElementDelta.F_CONTENT);
    }

    private void addToParentInfo(YangElement child) {
        YangElement parent = (YangElement) child.getParent();
        if (parent != null && parent.isOpen()) {
            try {
                OpenableElementInfo info = (OpenableElementInfo) parent.getElementInfo(null);
                info.addChild(child);
            } catch (YangModelException e) {
                // do nothing - we already checked if open
            }
        }
    }

    /*
     * Closes the given element, which removes it from the cache of open elements.
     */
    private void close(YangElement element) {
        try {
            element.close();
        } catch (YangModelException e) {
            // do nothing
        }
    }

    private YangElementDelta currentDelta() {
        if (this.currentDelta == null) {
            this.currentDelta = new YangElementDelta(this.manager.getYangModel());
        }
        return this.currentDelta;
    }

    /*
     * Returns whether a given delta contains some information relevant to the JavaModel, in
     * particular it will not consider SYNC or MARKER only deltas.
     */
    private boolean isAffectedBy(IResourceDelta rootDelta) {
        if (rootDelta != null) {
            // use local exception to quickly escape from delta traversal
            class FoundRelevantDeltaException extends RuntimeException {
                private static final long serialVersionUID = 7137113252936111023L;
            }
            try {
                rootDelta.accept(new IResourceDeltaVisitor() {
                    @Override
                    public boolean visit(IResourceDelta delta) /* throws CoreException */{
                        switch (delta.getKind()) {
                        case IResourceDelta.ADDED:
                        case IResourceDelta.REMOVED:
                            throw new FoundRelevantDeltaException();
                        case IResourceDelta.CHANGED:
                            // if any flag is set but SYNC or MARKER, this delta should be
                            // considered
                            if (delta.getAffectedChildren().length == 0 // only check leaf delta
                            // nodes
                            && (delta.getFlags() & ~(IResourceDelta.SYNC | IResourceDelta.MARKERS)) != 0) {
                                throw new FoundRelevantDeltaException();
                            }
                        }
                        return true;
                    }
                }, IContainer.INCLUDE_HIDDEN);
            } catch (FoundRelevantDeltaException e) {
                return true;
            } catch (CoreException e) { // ignore delta if not able to traverse
            }
        }
        return false;
    }

    private OutputsInfo outputsInfo(IResource res) {
        try {
            JavaProject proj = (JavaProject) JavaCore.create(res.getProject());
            if (proj != null) {
                IPath projectOutput = proj.getOutputLocation();
                int traverseMode = IGNORE;
                if (proj.getProject().getFullPath().equals(projectOutput)) { // case of
                    // proj==bin==src
                    return new OutputsInfo(new IPath[] { projectOutput }, new int[] { SOURCE }, 1);
                }
                IClasspathEntry[] classpath = proj.getResolvedClasspath();
                IPath[] outputs = new IPath[classpath.length + 1];
                int[] traverseModes = new int[classpath.length + 1];
                int outputCount = 1;
                outputs[0] = projectOutput;
                traverseModes[0] = traverseMode;
                for (int i = 0, length = classpath.length; i < length; i++) {
                    IClasspathEntry entry = classpath[i];
                    IPath entryPath = entry.getPath();
                    IPath output = entry.getOutputLocation();
                    if (output != null) {
                        outputs[outputCount] = output;
                        // check case of src==bin
                        if (entryPath.equals(output)) {
                            traverseModes[outputCount++] = (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) ? SOURCE
                                    : BINARY;
                        } else {
                            traverseModes[outputCount++] = IGNORE;
                        }
                    }

                    // check case of src==bin
                    if (entryPath.equals(projectOutput)) {
                        traverseModes[0] = (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) ? SOURCE : BINARY;
                    }
                }
                return new OutputsInfo(outputs, traverseModes, outputCount);
            }
        } catch (JavaModelException e) {
            // java project doesn't exist: ignore
        }
        return null;
    }

    /*
     * Returns whether the given resource is in one of the given output folders and if it is
     * filtered out from this output folder.
     */
    private boolean isResFilteredFromOutput(OutputsInfo info, IResource res) {
        if (info != null) {
            IPath resPath = res.getFullPath();
            for (int i = 0; i < info.outputCount; i++) {
                if (info.paths[i].isPrefixOf(resPath)
                        && (info.traverseModes[i] == IGNORE || info.traverseModes[i] == BINARY)) {
                    return true;
                }
            }
        }
        return false;
    }
}
