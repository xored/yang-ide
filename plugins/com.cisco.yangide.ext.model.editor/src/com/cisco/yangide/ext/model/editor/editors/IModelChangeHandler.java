/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.model.editor.editors;

import org.eclipse.emf.ecore.EObject;

import com.cisco.yangide.ext.model.Node;

/**
 * @author Konstantin Zaitsev
 * @date Aug 11, 2014
 */
public interface IModelChangeHandler {
    /**
     * Fired when node created and added to parent element at specific position
     *
     * @param parent parent composite node
     * @param child child node
     * @param position position in <code>children</code> list
     */
    void nodeAdded(Node parent, Node child, int position);

    /**
     * Fired when node removed from model.
     *
     * @param node node
     */
    void nodeRemoved(Node node);

    /**
     * Fired when nodes attributes was changed
     *
     * @param node node
     * @param object object references
     * @param newValue new value
     */
    void nodeChanged(Node node, EObject object, Object newValue);
}
