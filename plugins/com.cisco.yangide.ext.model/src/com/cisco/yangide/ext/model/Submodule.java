/**
 */
package com.cisco.yangide.ext.model;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Submodule</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.cisco.yangide.ext.model.Submodule#getBelongsTo <em>Belongs To</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.cisco.yangide.ext.model.ModelPackage#getSubmodule()
 * @model
 * @generated
 */
public interface Submodule extends Module {
    /**
     * Returns the value of the '<em><b>Belongs To</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Belongs To</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Belongs To</em>' reference.
     * @see #setBelongsTo(BelongsTo)
     * @see com.cisco.yangide.ext.model.ModelPackage#getSubmodule_BelongsTo()
     * @model required="true"
     * @generated
     */
    BelongsTo getBelongsTo();

    /**
     * Sets the value of the '{@link com.cisco.yangide.ext.model.Submodule#getBelongsTo <em>Belongs To</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Belongs To</em>' reference.
     * @see #getBelongsTo()
     * @generated
     */
    void setBelongsTo(BelongsTo value);

} // Submodule
