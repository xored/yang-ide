/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.model.editor.sync;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.ui.progress.UIJob;

import com.cisco.yangide.ext.model.ModelPackage;
import com.cisco.yangide.ext.model.Node;
import com.cisco.yangide.ext.model.editor.editors.IModelChangeHandler;

/**
 * @author Konstantin Zaitsev
 * @date Aug 13, 2014
 */
final class DiagramModelMergeAdapter extends EContentAdapter {
    private IModelChangeHandler modelChangeHandler;

    /**
     * @param modelSynchronizer
     */
    DiagramModelMergeAdapter(IModelChangeHandler modelChangeHandler) {
        this.modelChangeHandler = modelChangeHandler;
    }

    @Override
    public void notifyChanged(final Notification notification) {
        if (notification.getEventType() != Notification.REMOVING_ADAPTER
                && notification.getFeature() != ModelPackage.Literals.NODE__PARENT) {
            switch (notification.getEventType()) {
            case Notification.ADD:
                if (notification.getFeature() == ModelPackage.Literals.CONTAINING_NODE__CHILDREN) {
                    final Node node = (Node) notification.getNewValue();
                    final Node parent = (Node) notification.getNotifier();
                    new UIJob("Update diagram from source") {
                        @Override
                        public IStatus runInUIThread(IProgressMonitor monitor) {
                            modelChangeHandler.nodeAdded(parent, node, notification.getPosition());
                            return Status.OK_STATUS;
                        }
                    }.schedule();
                }
                break;
            case Notification.REMOVE:
                if (notification.getOldValue() instanceof Node && notification.getOldValue() != null) {
                    final Node node = (Node) notification.getOldValue();
                    new UIJob("Update diagram from source") {
                        @Override
                        public IStatus runInUIThread(IProgressMonitor monitor) {
                            modelChangeHandler.nodeRemoved(node);
                            return Status.OK_STATUS;
                        }
                    }.schedule();
                }
                break;
            case Notification.SET:
                if (notification.getNotifier() instanceof Node && notification.getFeature() instanceof EAttribute) {
                    final Node node = (Node) notification.getNotifier();
                    new UIJob("Update diagram from source") {
                        @Override
                        public IStatus runInUIThread(IProgressMonitor monitor) {
                            modelChangeHandler.nodeChanged(node, (EAttribute) notification.getFeature(),
                                    notification.getNewValue());
                            return Status.OK_STATUS;
                        }
                    }.schedule();
                }
                break;
            default:
                break;
            }
        }
        super.notifyChanged(notification);
    }
}