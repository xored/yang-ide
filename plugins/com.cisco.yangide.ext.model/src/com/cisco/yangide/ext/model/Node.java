/**
 */
package com.cisco.yangide.ext.model;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Node</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.cisco.yangide.ext.model.Node#getParent <em>Parent</em>}</li>
 *   <li>{@link com.cisco.yangide.ext.model.Node#getRefId <em>Ref Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.cisco.yangide.ext.model.ModelPackage#getNode()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface Node extends EObject {
    /**
     * Returns the value of the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Parent</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Parent</em>' reference.
     * @see #setParent(Node)
     * @see com.cisco.yangide.ext.model.ModelPackage#getNode_Parent()
     * @model
     * @generated
     */
    Node getParent();

    /**
     * Sets the value of the '{@link com.cisco.yangide.ext.model.Node#getParent <em>Parent</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Parent</em>' reference.
     * @see #getParent()
     * @generated
     */
    void setParent(Node value);

    /**
     * Returns the value of the '<em><b>Ref Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Ref Id</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Ref Id</em>' attribute.
     * @see #setRefId(String)
     * @see com.cisco.yangide.ext.model.ModelPackage#getNode_RefId()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String" transient="true"
     * @generated
     */
    String getRefId();

    /**
     * Sets the value of the '{@link com.cisco.yangide.ext.model.Node#getRefId <em>Ref Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Ref Id</em>' attribute.
     * @see #getRefId()
     * @generated
     */
    void setRefId(String value);

} // Node
