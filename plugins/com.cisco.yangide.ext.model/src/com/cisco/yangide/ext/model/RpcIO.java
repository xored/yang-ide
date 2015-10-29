/**
 */
package com.cisco.yangide.ext.model;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Rpc IO</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.cisco.yangide.ext.model.RpcIO#isInput <em>Input</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.cisco.yangide.ext.model.ModelPackage#getRpcIO()
 * @model
 * @generated
 */
public interface RpcIO extends ContainingNode {
    /**
     * Returns the value of the '<em><b>Input</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Input</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Input</em>' attribute.
     * @see #setInput(boolean)
     * @see com.cisco.yangide.ext.model.ModelPackage#getRpcIO_Input()
     * @model dataType="org.eclipse.emf.ecore.xml.type.Boolean"
     * @generated
     */
    boolean isInput();

    /**
     * Sets the value of the '{@link com.cisco.yangide.ext.model.RpcIO#isInput <em>Input</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Input</em>' attribute.
     * @see #isInput()
     * @generated
     */
    void setInput(boolean value);

} // RpcIO
