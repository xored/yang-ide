/**
 */
package com.cisco.yangide.ext.model;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Revision</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.cisco.yangide.ext.model.Revision#getDate <em>Date</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.cisco.yangide.ext.model.ModelPackage#getRevision()
 * @model
 * @generated
 */
public interface Revision extends TaggedNode {

    /**
     * Returns the value of the '<em><b>Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Date</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Date</em>' attribute.
     * @see #setDate(String)
     * @see com.cisco.yangide.ext.model.ModelPackage#getRevision_Date()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String"
     * @generated
     */
    String getDate();

    /**
     * Sets the value of the '{@link com.cisco.yangide.ext.model.Revision#getDate <em>Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Date</em>' attribute.
     * @see #getDate()
     * @generated
     */
    void setDate(String value);
} // Revision
