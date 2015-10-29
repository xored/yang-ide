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
 * A representation of the model object '<em><b>Import</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.cisco.yangide.ext.model.Import#getPrefix <em>Prefix</em>}</li>
 *   <li>{@link com.cisco.yangide.ext.model.Import#getRevisionDate <em>Revision Date</em>}</li>
 *   <li>{@link com.cisco.yangide.ext.model.Import#getModule <em>Module</em>}</li>
 * </ul>
 *
 * @see com.cisco.yangide.ext.model.ModelPackage#getImport()
 * @model
 * @generated
 */
public interface Import extends Node {
    /**
     * Returns the value of the '<em><b>Prefix</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Prefix</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Prefix</em>' attribute.
     * @see #setPrefix(String)
     * @see com.cisco.yangide.ext.model.ModelPackage#getImport_Prefix()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
     * @generated
     */
    String getPrefix();

    /**
     * Sets the value of the '{@link com.cisco.yangide.ext.model.Import#getPrefix <em>Prefix</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Prefix</em>' attribute.
     * @see #getPrefix()
     * @generated
     */
    void setPrefix(String value);

    /**
     * Returns the value of the '<em><b>Revision Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Revision Date</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Revision Date</em>' attribute.
     * @see #setRevisionDate(String)
     * @see com.cisco.yangide.ext.model.ModelPackage#getImport_RevisionDate()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String"
     * @generated
     */
    String getRevisionDate();

    /**
     * Sets the value of the '{@link com.cisco.yangide.ext.model.Import#getRevisionDate <em>Revision Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Revision Date</em>' attribute.
     * @see #getRevisionDate()
     * @generated
     */
    void setRevisionDate(String value);

    /**
     * Returns the value of the '<em><b>Module</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Module</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Module</em>' attribute.
     * @see #setModule(String)
     * @see com.cisco.yangide.ext.model.ModelPackage#getImport_Module()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String"
     * @generated
     */
    String getModule();

    /**
     * Sets the value of the '{@link com.cisco.yangide.ext.model.Import#getModule <em>Module</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Module</em>' attribute.
     * @see #getModule()
     * @generated
     */
    void setModule(String value);

} // Import
