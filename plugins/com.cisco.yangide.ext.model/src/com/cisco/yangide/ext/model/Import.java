/**
 */
package com.cisco.yangide.ext.model;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Import</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.cisco.yangide.ext.model.Import#getModule <em>Module</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.cisco.yangide.ext.model.ModelPackage#getImport()
 * @model
 * @generated
 */
public interface Import extends Node {
    /**
     * Returns the value of the '<em><b>Module</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Module</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Module</em>' containment reference.
     * @see #setModule(Module)
     * @see com.cisco.yangide.ext.model.ModelPackage#getImport_Module()
     * @model containment="true" required="true"
     * @generated
     */
    Module getModule();

    /**
     * Sets the value of the '{@link com.cisco.yangide.ext.model.Import#getModule <em>Module</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Module</em>' containment reference.
     * @see #getModule()
     * @generated
     */
    void setModule(Module value);

} // Import
