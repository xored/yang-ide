/**
 */
package com.cisco.yangide.ext.model;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Reference Node</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.cisco.yangide.ext.model.ReferenceNode#getReference <em>Reference</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.cisco.yangide.ext.model.ModelPackage#getReferenceNode()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface ReferenceNode extends EObject {
    /**
     * Returns the value of the '<em><b>Reference</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Reference</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Reference</em>' attribute.
     * @see #setReference(String)
     * @see com.cisco.yangide.ext.model.ModelPackage#getReferenceNode_Reference()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String"
     * @generated
     */
    String getReference();

    /**
     * Sets the value of the '{@link com.cisco.yangide.ext.model.ReferenceNode#getReference <em>Reference</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Reference</em>' attribute.
     * @see #getReference()
     * @generated
     */
    void setReference(String value);

} // ReferenceNode
