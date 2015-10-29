/**
 */
package com.cisco.yangide.ext.model;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Containing Node</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.cisco.yangide.ext.model.ContainingNode#getChildren <em>Children</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.cisco.yangide.ext.model.ModelPackage#getContainingNode()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface ContainingNode extends Node {
    /**
     * Returns the value of the '<em><b>Children</b></em>' containment reference list.
     * The list contents are of type {@link com.cisco.yangide.ext.model.Node}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Children</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Children</em>' containment reference list.
     * @see com.cisco.yangide.ext.model.ModelPackage#getContainingNode_Children()
     * @model containment="true"
     * @generated
     */
    EList<Node> getChildren();

} // ContainingNode
