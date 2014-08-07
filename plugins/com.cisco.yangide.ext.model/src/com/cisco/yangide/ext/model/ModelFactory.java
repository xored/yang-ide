/**
 */
package com.cisco.yangide.ext.model;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see com.cisco.yangide.ext.model.ModelPackage
 * @generated
 */
public interface ModelFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    ModelFactory eINSTANCE = com.cisco.yangide.ext.model.impl.ModelFactoryImpl.init();

    /**
     * Returns a new object of class '<em>Module</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Module</em>'.
     * @generated
     */
    Module createModule();

    /**
     * Returns a new object of class '<em>Grouping</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Grouping</em>'.
     * @generated
     */
    Grouping createGrouping();

    /**
     * Returns a new object of class '<em>Leaf</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Leaf</em>'.
     * @generated
     */
    Leaf createLeaf();

    /**
     * Returns a new object of class '<em>Container</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Container</em>'.
     * @generated
     */
    Container createContainer();

    /**
     * Returns a new object of class '<em>Submodule</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Submodule</em>'.
     * @generated
     */
    Submodule createSubmodule();

    /**
     * Returns a new object of class '<em>Typedef</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Typedef</em>'.
     * @generated
     */
    Typedef createTypedef();

    /**
     * Returns a new object of class '<em>Choice</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Choice</em>'.
     * @generated
     */
    Choice createChoice();

    /**
     * Returns a new object of class '<em>Leaf List</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Leaf List</em>'.
     * @generated
     */
    LeafList createLeafList();

    /**
     * Returns a new object of class '<em>Anyxml</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Anyxml</em>'.
     * @generated
     */
    Anyxml createAnyxml();

    /**
     * Returns a new object of class '<em>Import</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Import</em>'.
     * @generated
     */
    Import createImport();

    /**
     * Returns a new object of class '<em>Include</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Include</em>'.
     * @generated
     */
    Include createInclude();

    /**
     * Returns a new object of class '<em>Revision</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Revision</em>'.
     * @generated
     */
    Revision createRevision();

    /**
     * Returns a new object of class '<em>Belongs To</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Belongs To</em>'.
     * @generated
     */
    BelongsTo createBelongsTo();

    /**
     * Returns a new object of class '<em>List</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>List</em>'.
     * @generated
     */
    List createList();

    /**
     * Returns a new object of class '<em>Tag</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Tag</em>'.
     * @generated
     */
    Tag createTag();

    /**
     * Returns a new object of class '<em>Uses</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Uses</em>'.
     * @generated
     */
    Uses createUses();

    /**
     * Returns a new object of class '<em>Rpc</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Rpc</em>'.
     * @generated
     */
    Rpc createRpc();

    /**
     * Returns a new object of class '<em>Rpc IO</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Rpc IO</em>'.
     * @generated
     */
    RpcIO createRpcIO();

    /**
     * Returns a new object of class '<em>Notification</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Notification</em>'.
     * @generated
     */
    Notification createNotification();

    /**
     * Returns a new object of class '<em>Augment</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Augment</em>'.
     * @generated
     */
    Augment createAugment();

    /**
     * Returns a new object of class '<em>Extension</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Extension</em>'.
     * @generated
     */
    Extension createExtension();

    /**
     * Returns a new object of class '<em>Feature</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Feature</em>'.
     * @generated
     */
    Feature createFeature();

    /**
     * Returns a new object of class '<em>Deviation</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Deviation</em>'.
     * @generated
     */
    Deviation createDeviation();

    /**
     * Returns a new object of class '<em>Identity</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Identity</em>'.
     * @generated
     */
    Identity createIdentity();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    ModelPackage getModelPackage();

} //ModelFactory
