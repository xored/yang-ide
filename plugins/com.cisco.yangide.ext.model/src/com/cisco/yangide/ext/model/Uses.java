/**
 */
package com.cisco.yangide.ext.model;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Uses</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.cisco.yangide.ext.model.Uses#getGrouping <em>Grouping</em>}</li>
 *   <li>{@link com.cisco.yangide.ext.model.Uses#getQName <em>QName</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.cisco.yangide.ext.model.ModelPackage#getUses()
 * @model
 * @generated
 */
public interface Uses extends Node {
    /**
     * Returns the value of the '<em><b>Grouping</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Grouping</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Grouping</em>' reference.
     * @see #setGrouping(Grouping)
     * @see com.cisco.yangide.ext.model.ModelPackage#getUses_Grouping()
     * @model
     * @generated
     */
    Grouping getGrouping();

    /**
     * Sets the value of the '{@link com.cisco.yangide.ext.model.Uses#getGrouping <em>Grouping</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Grouping</em>' reference.
     * @see #getGrouping()
     * @generated
     */
    void setGrouping(Grouping value);

    /**
     * Returns the value of the '<em><b>QName</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>QName</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>QName</em>' attribute.
     * @see #setQName(String)
     * @see com.cisco.yangide.ext.model.ModelPackage#getUses_QName()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String"
     * @generated
     */
    String getQName();

    /**
     * Sets the value of the '{@link com.cisco.yangide.ext.model.Uses#getQName <em>QName</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>QName</em>' attribute.
     * @see #getQName()
     * @generated
     */
    void setQName(String value);

} // Uses
