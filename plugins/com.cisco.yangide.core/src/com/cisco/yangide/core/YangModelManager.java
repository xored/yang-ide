/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.cisco.yangide.core.indexing.DeltaProcessor;
import com.cisco.yangide.core.indexing.IndexManager;
import com.cisco.yangide.core.model.YangModel;

/**
 * @author Konstantin Zaitsev
 * @date Jun 24, 2014
 */
public final class YangModelManager implements ISaveParticipant {

    /** Singleton instance. */
    private static final YangModelManager MANAGER = new YangModelManager();

    /**
     * Infos cache.
     */
    private OpenableElementCache cache;
    private YangModel yangModel = new YangModel();

    public IndexManager indexManager = null;

    public DeltaProcessor deltaProcessor = new DeltaProcessor();

    protected HashSet<IOpenable> elementsOutOfSynchWithBuffers = new HashSet<IOpenable>(11);

    /**
     * @return singleton instance
     */
    public static YangModelManager getYangModelManager() {
        return MANAGER;
    }

    @SuppressWarnings("restriction")
    public void startup() throws CoreException {
        try {
            // initialize Yang model cache, 5000 is default value for JDT openable cache
            this.cache = new OpenableElementCache(5000);

            final IWorkspace workspace = ResourcesPlugin.getWorkspace();
            workspace.addResourceChangeListener(deltaProcessor,
            /*
             * update spec in JavaCore#addPreProcessingResourceChangedListener(...) if adding more
             * event types
             */
            IResourceChangeEvent.PRE_BUILD | IResourceChangeEvent.POST_BUILD | IResourceChangeEvent.POST_CHANGE
                    | IResourceChangeEvent.PRE_DELETE | IResourceChangeEvent.PRE_CLOSE
                    | IResourceChangeEvent.PRE_REFRESH);

            // start indexing
            if (this.indexManager != null) {
                this.indexManager.reset();
            }

            // init projects
            yangModel.getYangProjects();

            Job processSavedState = new Job("Processing Yang changes since last activation") {
                protected IStatus run(IProgressMonitor monitor) {
                    try {
                        workspace.run(new IWorkspaceRunnable() {
                            public void run(IProgressMonitor progress) throws CoreException {
                                ISavedState savedState = workspace.addSaveParticipant(YangCorePlugin.PLUGIN_ID,
                                        YangModelManager.this);
                                if (savedState != null) {
                                    // the event type coming from the saved state is always
                                    // POST_AUTO_BUILD
                                    // force it to be POST_CHANGE so that the delta processor can
                                    // handle it
                                    YangModelManager.this.deltaProcessor.overridenEventType = IResourceChangeEvent.POST_CHANGE;
                                    savedState.processResourceChangeEvents(YangModelManager.this.deltaProcessor);
                                }
                            }
                        }, monitor);
                    } catch (CoreException e) {
                        return e.getStatus();
                    }
                    return Status.OK_STATUS;
                }
            };
            processSavedState.setSystem(true);
            processSavedState.setPriority(Job.SHORT); // process asap
            processSavedState.schedule();
        } catch (RuntimeException e) {
            shutdown();
            throw e;
        }
    }

    @SuppressWarnings("restriction")
    public void shutdown() {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        workspace.removeResourceChangeListener(this.deltaProcessor);
        workspace.removeSaveParticipant(YangCorePlugin.PLUGIN_ID);

        // Stop indexing
        if (this.indexManager != null) {
            this.indexManager.shutdown();
        }

        // wait for the initialization job to finish
        try {
            Job.getJobManager().join(YangCorePlugin.PLUGIN_ID, null);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    /**
     * @param element
     * @return
     */
    public synchronized Object getInfo(IOpenable element) {
        return cache.get(element);
    }

    /*
     * Puts the infos in the given map (keys are IJavaElements and values are JavaElementInfos) in
     * the Java model cache in an atomic way.
     */
    protected synchronized void putInfos(IOpenable openedElement, Map<IOpenable, OpenableElementInfo> newElements) {
        Object existingInfo = this.cache.get(openedElement);
        closeChildren(existingInfo);

        Iterator<Entry<IOpenable, OpenableElementInfo>> iterator = newElements.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<IOpenable, OpenableElementInfo> entry = iterator.next();
            this.cache.put(entry.getKey(), entry.getValue());
        }
    }

    private void closeChildren(Object info) {
        // if (info instanceof JavaElementInfo) {
        // IJavaElement[] children = ((JavaElementInfo)info).getChildren();
        // for (int i = 0, size = children.length; i < size; ++i) {
        // JavaElement child = (JavaElement) children[i];
        // try {
        // child.close();
        // } catch (JavaModelException e) {
        // // ignore
        // }
        // }
        // }
    }

    /*
     * Removes all cached info for the given element (including all children) from the cache.
     * Returns the info for the given element, or null if it was closed.
     */
    public synchronized Object removeInfoAndChildren(IOpenable element) throws YangModelException {
        Object info = this.cache.get(element);
        if (info != null) {
            closeChildren(info);
            this.cache.remove(element);
            return info;
        }
        return null;
    }

    /**
     * @return the yangModel
     */
    public YangModel getYangModel() {
        return yangModel;
    }

    protected HashSet<IOpenable> getElementsOutOfSynchWithBuffers() {
        return this.elementsOutOfSynchWithBuffers;
    }

    // / methods from ISaveParticipant
    public void doneSaving(ISaveContext context) {
    }

    public void prepareToSave(ISaveContext context) throws CoreException {
    }

    public void rollback(ISaveContext context) {
    }

    public void saving(ISaveContext context) throws CoreException {
    }
    // / end of methods from ISaveParticipant
}
