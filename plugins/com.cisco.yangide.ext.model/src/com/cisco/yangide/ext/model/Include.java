/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.model;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Include</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.cisco.yangide.ext.model.Include#getSubmodule <em>Submodule</em>}</li>
 * </ul>
 *
 * @see com.cisco.yangide.ext.model.ModelPackage#getInclude()
 * @model
 * @generated
 */
public interface Include extends Node {
    /**
     * Returns the value of the '<em><b>Submodule</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Submodule</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Submodule</em>' containment reference.
     * @see #setSubmodule(Submodule)
     * @see com.cisco.yangide.ext.model.ModelPackage#getInclude_Submodule()
     * @model containment="true" required="true"
     * @generated
     */
    Submodule getSubmodule();

    /**
     * Sets the value of the '{@link com.cisco.yangide.ext.model.Include#getSubmodule <em>Submodule</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Submodule</em>' containment reference.
     * @see #getSubmodule()
     * @generated
     */
    void setSubmodule(Submodule value);

} // Include
