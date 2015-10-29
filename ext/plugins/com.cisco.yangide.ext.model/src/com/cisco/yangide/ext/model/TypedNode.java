/**
 */
package com.cisco.yangide.ext.model;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Typed Node</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.cisco.yangide.ext.model.TypedNode#getType <em>Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.cisco.yangide.ext.model.ModelPackage#getTypedNode()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface TypedNode extends EObject {

    /**
     * Returns the value of the '<em><b>Type</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Type</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Type</em>' containment reference.
     * @see #setType(Typeref)
     * @see com.cisco.yangide.ext.model.ModelPackage#getTypedNode_Type()
     * @model containment="true"
     * @generated
     */
    Typeref getType();

    /**
     * Sets the value of the '{@link com.cisco.yangide.ext.model.TypedNode#getType <em>Type</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Type</em>' containment reference.
     * @see #getType()
     * @generated
     */
    void setType(Typeref value);
} // TypedNode
