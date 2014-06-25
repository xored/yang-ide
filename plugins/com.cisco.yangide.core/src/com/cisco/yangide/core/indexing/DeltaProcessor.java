/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.indexing;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;

/**
 * @author Konstantin Zaitsev
 * @date Jun 25, 2014
 */
public class DeltaProcessor implements IResourceChangeListener {

    public int overridenEventType;

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        System.out.println(event);
//        int eventType = this.overridenEventType == -1 ? event.getType() : this.overridenEventType;
//        IResource resource = event.getResource();
//        IResourceDelta delta = event.getDelta();
//
//        switch (eventType) {
//        case IResourceChangeEvent.PRE_DELETE:
//            try {
//                if (resource.getType() == IResource.PROJECT && ((IProject) resource).hasNature(JavaCore.NATURE_ID)) {
//
//                    deleting((IProject) resource);
//                }
//            } catch (CoreException e) {
//                // project doesn't exist or is not open: ignore
//            }
//            return;
//
//        case IResourceChangeEvent.PRE_REFRESH:
//            IProject[] projects = null;
//            Object o = event.getSource();
//            if (o instanceof IProject) {
//                projects = new IProject[] { (IProject) o };
//            } else if (o instanceof IWorkspace) {
//                // https://bugs.eclipse.org/bugs/show_bug.cgi?id=261594. The single workspace
//                // refresh
//                // notification we see, implies that all projects are about to be refreshed.
//                projects = ((IWorkspace) o).getRoot().getProjects(IContainer.INCLUDE_HIDDEN);
//            }
//            // https://bugs.eclipse.org/bugs/show_bug.cgi?id=302295
//            // Refresh all project references together in a single job
//            JavaModelManager.getExternalManager().refreshReferences(projects, null);
//
//            IJavaProject[] javaElements = new IJavaProject[projects.length];
//            for (int index = 0; index < projects.length; index++) {
//                javaElements[index] = JavaCore.create(projects[index]);
//            }
//            try {
//                checkExternalArchiveChanges(javaElements, true, null);
//            } catch (JavaModelException e) {
//                if (!e.isDoesNotExist())
//                    Util.log(e, "Exception while updating external archives"); //$NON-NLS-1$
//            }
//            return;
//
//        case IResourceChangeEvent.POST_CHANGE:
//            HashSet elementsToRefresh = this.state.removeExternalElementsToRefresh();
//            if (isAffectedBy(delta) // avoid populating for SYNC or MARKER deltas
//                    || elementsToRefresh != null) {
//                try {
//                    try {
//                        stopDeltas();
//                        checkProjectsAndClasspathChanges(delta);
//
//                        // generate external archive change deltas
//                        if (elementsToRefresh != null) {
//                            createExternalArchiveDelta(elementsToRefresh, null);
//                        }
//
//                        // generate classpath change deltas
//                        HashMap classpathChanges = this.state.removeAllClasspathChanges();
//                        if (classpathChanges.size() > 0) {
//                            boolean hasDelta = this.currentDelta != null;
//                            JavaElementDelta javaDelta = currentDelta();
//                            Iterator changes = classpathChanges.values().iterator();
//                            while (changes.hasNext()) {
//                                ClasspathChange change = (ClasspathChange) changes.next();
//                                int result = change.generateDelta(javaDelta, false/*
//                                                                                   * don't add
//                                                                                   * classpath
//                                                                                   * change
//                                                                                   */);
//                                if ((result & ClasspathChange.HAS_DELTA) != 0) {
//                                    hasDelta = true;
//
//                                    // need to recompute root infos
//                                    this.state.rootsAreStale = true;
//
//                                    change.requestIndexing();
//                                    this.state.addClasspathValidation(change.project);
//                                }
//                                if ((result & ClasspathChange.HAS_PROJECT_CHANGE) != 0) {
//                                    this.state.addProjectReferenceChange(change.project, change.oldResolvedClasspath);
//                                }
//                                if ((result & ClasspathChange.HAS_LIBRARY_CHANGE) != 0) {
//                                    this.state.addExternalFolderChange(change.project, change.oldResolvedClasspath);
//                                }
//                            }
//                            // process late coming external elements to refresh (see
//                            // https://bugs.eclipse.org/bugs/show_bug.cgi?id=212769 )
//                            elementsToRefresh = this.state.removeExternalElementsToRefresh();
//                            if (elementsToRefresh != null) {
//                                hasDelta |= createExternalArchiveDelta(elementsToRefresh, null);
//                            }
//                            if (!hasDelta)
//                                this.currentDelta = null;
//                        }
//
//                        // generate Java deltas from resource changes
//                        IJavaElementDelta translatedDelta = processResourceDelta(delta);
//                        if (translatedDelta != null) {
//                            registerJavaModelDelta(translatedDelta);
//                        }
//                    } finally {
//                        this.sourceElementParserCache = null; // don't hold onto parser longer than
//                                                              // necessary
//                        startDeltas();
//                    }
//                    IElementChangedListener[] listeners;
//                    int listenerCount;
//                    synchronized (this.state) {
//                        listeners = this.state.elementChangedListeners;
//                        listenerCount = this.state.elementChangedListenerCount;
//                    }
//                    notifyTypeHierarchies(listeners, listenerCount);
//                    fire(null, ElementChangedEvent.POST_CHANGE);
//                } finally {
//                    // workaround for bug 15168 circular errors not reported
//                    this.state.resetOldJavaProjectNames();
//                    this.oldRoots = null;
//                }
//            }
//            return;
//
//        case IResourceChangeEvent.PRE_BUILD:
//            // force initialization of roots before builders run to avoid deadlock in another thread
//            // (note this is no-op if already initialized)
//            // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=241751
//            this.state.initializeRoots(false/* not initiAfterLoad */);
//
//            boolean isAffected = isAffectedBy(delta);
//            boolean needCycleValidation = isAffected && validateClasspaths(delta);
//
//            // update external folders if necessary
//            ExternalFolderChange[] folderChanges = this.state.removeExternalFolderChanges();
//            if (folderChanges != null) {
//                for (int i = 0, length = folderChanges.length; i < length; i++) {
//                    try {
//                        folderChanges[i].updateExternalFoldersIfNecessary(false/*
//                                                                                * do not refresh
//                                                                                * since we are not
//                                                                                * in the thread that
//                                                                                * added the external
//                                                                                * folder to the
//                                                                                * classpath
//                                                                                */, null);
//                    } catch (JavaModelException e) {
//                        if (!e.isDoesNotExist())
//                            Util.log(e, "Exception while updating external folders"); //$NON-NLS-1$
//                    }
//                }
//            }
//
//            // create classpath markers if necessary
//            ClasspathValidation[] validations = this.state.removeClasspathValidations();
//            if (validations != null) {
//                for (int i = 0, length = validations.length; i < length; i++) {
//                    ClasspathValidation validation = validations[i];
//                    validation.validate();
//                }
//            }
//
//            // update project references if necessary
//            ProjectReferenceChange[] projectRefChanges = this.state.removeProjectReferenceChanges();
//            if (projectRefChanges != null) {
//                for (int i = 0, length = projectRefChanges.length; i < length; i++) {
//                    try {
//                        projectRefChanges[i].updateProjectReferencesIfNecessary();
//                    } catch (JavaModelException e) {
//                        // project doesn't exist any longer, continue with next one
//                        if (!e.isDoesNotExist())
//                            Util.log(e, "Exception while updating project references"); //$NON-NLS-1$
//                    }
//                }
//            }
//
//            if (needCycleValidation || projectRefChanges != null) {
//                // update all cycle markers since the project references changes may have affected
//                // cycles
//                try {
//                    JavaProject.validateCycles(null);
//                } catch (JavaModelException e) {
//                    // a project no longer exists
//                }
//            }
//
//            if (isAffected) {
//                JavaModel.flushExternalFileCache();
//                JavaBuilder.buildStarting();
//            }
//
//            // does not fire any deltas
//            return;
//
//        case IResourceChangeEvent.POST_BUILD:
//            JavaBuilder.buildFinished();
//            return;
//        }
    }

}
