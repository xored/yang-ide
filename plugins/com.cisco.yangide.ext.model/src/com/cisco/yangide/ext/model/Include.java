/**
 */
package com.cisco.yangide.ext.model;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Include</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.cisco.yangide.ext.model.Include#getSubmodule <em>Submodule</em>}</li>
 * </ul>
 * </p>
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
