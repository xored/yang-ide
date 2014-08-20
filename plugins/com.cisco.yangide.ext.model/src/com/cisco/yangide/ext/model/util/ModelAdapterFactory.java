/**
 */
package com.cisco.yangide.ext.model.util;

import com.cisco.yangide.ext.model.*;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see com.cisco.yangide.ext.model.ModelPackage
 * @generated
 */
public class ModelAdapterFactory extends AdapterFactoryImpl {
    /**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static ModelPackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModelAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = ModelPackage.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc -->
     * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
     * <!-- end-user-doc -->
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
    @Override
    public boolean isFactoryForType(Object object) {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject)object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

    /**
     * The switch that delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ModelSwitch<Adapter> modelSwitch =
        new ModelSwitch<Adapter>() {
            @Override
            public Adapter caseModule(Module object) {
                return createModuleAdapter();
            }
            @Override
            public Adapter caseGrouping(Grouping object) {
                return createGroupingAdapter();
            }
            @Override
            public Adapter caseLeaf(Leaf object) {
                return createLeafAdapter();
            }
            @Override
            public Adapter caseContainer(Container object) {
                return createContainerAdapter();
            }
            @Override
            public Adapter caseNode(Node object) {
                return createNodeAdapter();
            }
            @Override
            public Adapter caseNamedNode(NamedNode object) {
                return createNamedNodeAdapter();
            }
            @Override
            public Adapter caseContainingNode(ContainingNode object) {
                return createContainingNodeAdapter();
            }
            @Override
            public Adapter caseNamedContainingNode(NamedContainingNode object) {
                return createNamedContainingNodeAdapter();
            }
            @Override
            public Adapter caseSubmodule(Submodule object) {
                return createSubmoduleAdapter();
            }
            @Override
            public Adapter caseTypedef(Typedef object) {
                return createTypedefAdapter();
            }
            @Override
            public Adapter caseChoice(Choice object) {
                return createChoiceAdapter();
            }
            @Override
            public Adapter caseLeafList(LeafList object) {
                return createLeafListAdapter();
            }
            @Override
            public Adapter caseAnyxml(Anyxml object) {
                return createAnyxmlAdapter();
            }
            @Override
            public Adapter caseImport(Import object) {
                return createImportAdapter();
            }
            @Override
            public Adapter caseInclude(Include object) {
                return createIncludeAdapter();
            }
            @Override
            public Adapter caseRevision(Revision object) {
                return createRevisionAdapter();
            }
            @Override
            public Adapter caseBelongsTo(BelongsTo object) {
                return createBelongsToAdapter();
            }
            @Override
            public Adapter caseList(List object) {
                return createListAdapter();
            }
            @Override
            public Adapter caseTag(Tag object) {
                return createTagAdapter();
            }
            @Override
            public Adapter caseTaggedNode(TaggedNode object) {
                return createTaggedNodeAdapter();
            }
            @Override
            public Adapter caseUses(Uses object) {
                return createUsesAdapter();
            }
            @Override
            public Adapter caseRpc(Rpc object) {
                return createRpcAdapter();
            }
            @Override
            public Adapter caseRpcIO(RpcIO object) {
                return createRpcIOAdapter();
            }
            @Override
            public Adapter caseNotification(Notification object) {
                return createNotificationAdapter();
            }
            @Override
            public Adapter caseAugment(Augment object) {
                return createAugmentAdapter();
            }
            @Override
            public Adapter caseExtension(Extension object) {
                return createExtensionAdapter();
            }
            @Override
            public Adapter caseFeature(Feature object) {
                return createFeatureAdapter();
            }
            @Override
            public Adapter caseDeviation(Deviation object) {
                return createDeviationAdapter();
            }
            @Override
            public Adapter caseIdentity(Identity object) {
                return createIdentityAdapter();
            }
            @Override
            public Adapter caseTypedNode(TypedNode object) {
                return createTypedNodeAdapter();
            }
            @Override
            public Adapter caseChoiceCase(ChoiceCase object) {
                return createChoiceCaseAdapter();
            }
            @Override
            public Adapter defaultCase(EObject object) {
                return createEObjectAdapter();
            }
        };

    /**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    @Override
    public Adapter createAdapter(Notifier target) {
        return modelSwitch.doSwitch((EObject)target);
    }


    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.Module <em>Module</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.Module
     * @generated
     */
    public Adapter createModuleAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.Grouping <em>Grouping</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.Grouping
     * @generated
     */
    public Adapter createGroupingAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.Leaf <em>Leaf</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.Leaf
     * @generated
     */
    public Adapter createLeafAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.Container <em>Container</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.Container
     * @generated
     */
    public Adapter createContainerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.Node <em>Node</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.Node
     * @generated
     */
    public Adapter createNodeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.NamedNode <em>Named Node</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.NamedNode
     * @generated
     */
    public Adapter createNamedNodeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.ContainingNode <em>Containing Node</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.ContainingNode
     * @generated
     */
    public Adapter createContainingNodeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.NamedContainingNode <em>Named Containing Node</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.NamedContainingNode
     * @generated
     */
    public Adapter createNamedContainingNodeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.Submodule <em>Submodule</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.Submodule
     * @generated
     */
    public Adapter createSubmoduleAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.Typedef <em>Typedef</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.Typedef
     * @generated
     */
    public Adapter createTypedefAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.Choice <em>Choice</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.Choice
     * @generated
     */
    public Adapter createChoiceAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.LeafList <em>Leaf List</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.LeafList
     * @generated
     */
    public Adapter createLeafListAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.Anyxml <em>Anyxml</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.Anyxml
     * @generated
     */
    public Adapter createAnyxmlAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.Import <em>Import</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.Import
     * @generated
     */
    public Adapter createImportAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.Include <em>Include</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.Include
     * @generated
     */
    public Adapter createIncludeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.Revision <em>Revision</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.Revision
     * @generated
     */
    public Adapter createRevisionAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.BelongsTo <em>Belongs To</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.BelongsTo
     * @generated
     */
    public Adapter createBelongsToAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.List <em>List</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.List
     * @generated
     */
    public Adapter createListAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.Tag <em>Tag</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.Tag
     * @generated
     */
    public Adapter createTagAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.TaggedNode <em>Tagged Node</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.TaggedNode
     * @generated
     */
    public Adapter createTaggedNodeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.Uses <em>Uses</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.Uses
     * @generated
     */
    public Adapter createUsesAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.Rpc <em>Rpc</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.Rpc
     * @generated
     */
    public Adapter createRpcAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.RpcIO <em>Rpc IO</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.RpcIO
     * @generated
     */
    public Adapter createRpcIOAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.Notification <em>Notification</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.Notification
     * @generated
     */
    public Adapter createNotificationAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.Augment <em>Augment</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.Augment
     * @generated
     */
    public Adapter createAugmentAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.Extension <em>Extension</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.Extension
     * @generated
     */
    public Adapter createExtensionAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.Feature <em>Feature</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.Feature
     * @generated
     */
    public Adapter createFeatureAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.Deviation <em>Deviation</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.Deviation
     * @generated
     */
    public Adapter createDeviationAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.Identity <em>Identity</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.Identity
     * @generated
     */
    public Adapter createIdentityAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.TypedNode <em>Typed Node</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.TypedNode
     * @generated
     */
    public Adapter createTypedNodeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.cisco.yangide.ext.model.ChoiceCase <em>Choice Case</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.cisco.yangide.ext.model.ChoiceCase
     * @generated
     */
    public Adapter createChoiceCaseAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc -->
     * This default implementation returns null.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @generated
     */
    public Adapter createEObjectAdapter() {
        return null;
    }

} //ModelAdapterFactory
