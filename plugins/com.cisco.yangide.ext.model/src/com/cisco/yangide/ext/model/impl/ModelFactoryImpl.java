/**
 */
package com.cisco.yangide.ext.model.impl;

import com.cisco.yangide.ext.model.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ModelFactoryImpl extends EFactoryImpl implements ModelFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ModelFactory init() {
        try {
            ModelFactory theModelFactory = (ModelFactory)EPackage.Registry.INSTANCE.getEFactory(ModelPackage.eNS_URI);
            if (theModelFactory != null) {
                return theModelFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new ModelFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModelFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
            case ModelPackage.MODULE: return createModule();
            case ModelPackage.GROUPING: return createGrouping();
            case ModelPackage.LEAF: return createLeaf();
            case ModelPackage.CONTAINER: return createContainer();
            case ModelPackage.SUBMODULE: return createSubmodule();
            case ModelPackage.TYPEDEF: return createTypedef();
            case ModelPackage.CHOICE: return createChoice();
            case ModelPackage.LEAF_LIST: return createLeafList();
            case ModelPackage.ANYXML: return createAnyxml();
            case ModelPackage.IMPORT: return createImport();
            case ModelPackage.INCLUDE: return createInclude();
            case ModelPackage.REVISION: return createRevision();
            case ModelPackage.BELONGS_TO: return createBelongsTo();
            case ModelPackage.LIST: return createList();
            case ModelPackage.TAG: return createTag();
            case ModelPackage.USES: return createUses();
            case ModelPackage.RPC: return createRpc();
            case ModelPackage.RPC_IO: return createRpcIO();
            case ModelPackage.NOTIFICATION: return createNotification();
            case ModelPackage.AUGMENT: return createAugment();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Module createModule() {
        ModuleImpl module = new ModuleImpl();
        return module;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Grouping createGrouping() {
        GroupingImpl grouping = new GroupingImpl();
        return grouping;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Leaf createLeaf() {
        LeafImpl leaf = new LeafImpl();
        return leaf;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public com.cisco.yangide.ext.model.Container createContainer() {
        ContainerImpl container = new ContainerImpl();
        return container;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Submodule createSubmodule() {
        SubmoduleImpl submodule = new SubmoduleImpl();
        return submodule;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Typedef createTypedef() {
        TypedefImpl typedef = new TypedefImpl();
        return typedef;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Choice createChoice() {
        ChoiceImpl choice = new ChoiceImpl();
        return choice;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public LeafList createLeafList() {
        LeafListImpl leafList = new LeafListImpl();
        return leafList;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Anyxml createAnyxml() {
        AnyxmlImpl anyxml = new AnyxmlImpl();
        return anyxml;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Import createImport() {
        ImportImpl import_ = new ImportImpl();
        return import_;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Include createInclude() {
        IncludeImpl include = new IncludeImpl();
        return include;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Revision createRevision() {
        RevisionImpl revision = new RevisionImpl();
        return revision;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public BelongsTo createBelongsTo() {
        BelongsToImpl belongsTo = new BelongsToImpl();
        return belongsTo;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public List createList() {
        ListImpl list = new ListImpl();
        return list;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Tag createTag() {
        TagImpl tag = new TagImpl();
        return tag;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Uses createUses() {
        UsesImpl uses = new UsesImpl();
        return uses;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Rpc createRpc() {
        RpcImpl rpc = new RpcImpl();
        return rpc;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RpcIO createRpcIO() {
        RpcIOImpl rpcIO = new RpcIOImpl();
        return rpcIO;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Notification createNotification() {
        NotificationImpl notification = new NotificationImpl();
        return notification;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Augment createAugment() {
        AugmentImpl augment = new AugmentImpl();
        return augment;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModelPackage getModelPackage() {
        return (ModelPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static ModelPackage getPackage() {
        return ModelPackage.eINSTANCE;
    }

} //ModelFactoryImpl
