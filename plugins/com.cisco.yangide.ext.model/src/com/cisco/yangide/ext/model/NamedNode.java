/**
 */
package com.cisco.yangide.ext.model;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Named Node</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.cisco.yangide.ext.model.NamedNode#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.cisco.yangide.ext.model.ModelPackage#getNamedNode()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface NamedNode extends Node {
    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see com.cisco.yangide.ext.model.ModelPackage#getNamedNode_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link com.cisco.yangide.ext.model.NamedNode#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

} // NamedNode
