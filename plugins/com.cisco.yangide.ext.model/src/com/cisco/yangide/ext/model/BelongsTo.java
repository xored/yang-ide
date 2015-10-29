/**
 */
package com.cisco.yangide.ext.model;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Belongs To</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.cisco.yangide.ext.model.BelongsTo#getOwnerModule <em>Owner Module</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.cisco.yangide.ext.model.ModelPackage#getBelongsTo()
 * @model
 * @generated
 */
public interface BelongsTo extends EObject {
    /**
     * Returns the value of the '<em><b>Owner Module</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Owner Module</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Owner Module</em>' reference.
     * @see #setOwnerModule(Module)
     * @see com.cisco.yangide.ext.model.ModelPackage#getBelongsTo_OwnerModule()
     * @model required="true"
     * @generated
     */
    Module getOwnerModule();

    /**
     * Sets the value of the '{@link com.cisco.yangide.ext.model.BelongsTo#getOwnerModule <em>Owner Module</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Owner Module</em>' reference.
     * @see #getOwnerModule()
     * @generated
     */
    void setOwnerModule(Module value);

} // BelongsTo
