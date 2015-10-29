/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.model;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Containing Node</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.cisco.yangide.ext.model.ContainingNode#getChildren <em>Children</em>}</li>
 * </ul>
 *
 * @see com.cisco.yangide.ext.model.ModelPackage#getContainingNode()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface ContainingNode extends Node {
    /**
     * Returns the value of the '<em><b>Children</b></em>' containment reference list.
     * The list contents are of type {@link com.cisco.yangide.ext.model.Node}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Children</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Children</em>' containment reference list.
     * @see com.cisco.yangide.ext.model.ModelPackage#getContainingNode_Children()
     * @model containment="true"
     * @generated
     */
    EList<Node> getChildren();

} // ContainingNode
