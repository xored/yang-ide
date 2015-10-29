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
 * A representation of the model object '<em><b>Module</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.cisco.yangide.ext.model.Module#getNamespace <em>Namespace</em>}</li>
 *   <li>{@link com.cisco.yangide.ext.model.Module#getRevisions <em>Revisions</em>}</li>
 * </ul>
 *
 * @see com.cisco.yangide.ext.model.ModelPackage#getModule()
 * @model
 * @generated
 */
public interface Module extends NamedContainingNode, TaggedNode {
    /**
     * Returns the value of the '<em><b>Namespace</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Namespace</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Namespace</em>' attribute.
     * @see #setNamespace(String)
     * @see com.cisco.yangide.ext.model.ModelPackage#getModule_Namespace()
     * @model required="true"
     * @generated
     */
    String getNamespace();

    /**
     * Sets the value of the '{@link com.cisco.yangide.ext.model.Module#getNamespace <em>Namespace</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Namespace</em>' attribute.
     * @see #getNamespace()
     * @generated
     */
    void setNamespace(String value);

    /**
     * Returns the value of the '<em><b>Revisions</b></em>' containment reference list.
     * The list contents are of type {@link com.cisco.yangide.ext.model.Revision}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Revisions</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Revisions</em>' containment reference list.
     * @see com.cisco.yangide.ext.model.ModelPackage#getModule_Revisions()
     * @model containment="true"
     * @generated
     */
    EList<Revision> getRevisions();

} // Module
