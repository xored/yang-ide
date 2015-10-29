/**
 */
package com.cisco.yangide.ext.model;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Tagged Node</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.cisco.yangide.ext.model.TaggedNode#getTags <em>Tags</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.cisco.yangide.ext.model.ModelPackage#getTaggedNode()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface TaggedNode extends EObject {
    /**
     * Returns the value of the '<em><b>Tags</b></em>' containment reference list.
     * The list contents are of type {@link com.cisco.yangide.ext.model.Tag}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Tags</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Tags</em>' containment reference list.
     * @see com.cisco.yangide.ext.model.ModelPackage#getTaggedNode_Tags()
     * @model containment="true"
     * @generated
     */
    EList<Tag> getTags();

} // TaggedNode
