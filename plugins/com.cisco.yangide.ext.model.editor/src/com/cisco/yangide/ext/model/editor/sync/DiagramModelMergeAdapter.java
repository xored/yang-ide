/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.ext.model.editor.sync;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.util.EContentAdapter;

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
    public void notifyChanged(Notification notification) {
        if (notification.getEventType() != Notification.REMOVING_ADAPTER
                && notification.getFeature() != ModelPackage.Literals.NODE__PARENT) {
            switch (notification.getEventType()) {
            case Notification.ADD:
                if (notification.getFeature() == ModelPackage.Literals.CONTAINING_NODE__CHILDREN) {
                    Node node = (Node) notification.getNewValue();
                    Node parent = (Node) notification.getNotifier();
                    modelChangeHandler.nodeAdded(parent, node, notification.getPosition());
                }
                break;
            case Notification.REMOVE:
                if (notification.getOldValue() instanceof Node && notification.getOldValue() != null) {
                    Node node = (Node) notification.getOldValue();
                    modelChangeHandler.nodeRemoved(node);
                }
                break;
            case Notification.SET:
                if (notification.getNotifier() instanceof Node && notification.getFeature() instanceof EAttribute) {
                    Node node = (Node) notification.getNotifier();
                    modelChangeHandler.nodeChanged(node, (EAttribute) notification.getFeature(),
                            notification.getNewValue());
                }
                break;
            default:
                break;
            }
        }
        super.notifyChanged(notification);
    }
}