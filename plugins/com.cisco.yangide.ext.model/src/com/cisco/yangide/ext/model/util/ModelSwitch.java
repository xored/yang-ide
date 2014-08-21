/**
 */
package com.cisco.yangide.ext.model.util;

import com.cisco.yangide.ext.model.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see com.cisco.yangide.ext.model.ModelPackage
 * @generated
 */
public class ModelSwitch<T> extends Switch<T> {
    /**
     * The cached model package
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static ModelPackage modelPackage;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModelSwitch() {
        if (modelPackage == null) {
            modelPackage = ModelPackage.eINSTANCE;
        }
    }

    /**
     * Checks whether this is a switch for the given package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @parameter ePackage the package in question.
     * @return whether this is a switch for the given package.
     * @generated
     */
    @Override
    protected boolean isSwitchFor(EPackage ePackage) {
        return ePackage == modelPackage;
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    @Override
    protected T doSwitch(int classifierID, EObject theEObject) {
        switch (classifierID) {
            case ModelPackage.MODULE: {
                Module module = (Module)theEObject;
                T result = caseModule(module);
                if (result == null) result = caseNamedContainingNode(module);
                if (result == null) result = caseTaggedNode(module);
                if (result == null) result = caseNamedNode(module);
                if (result == null) result = caseContainingNode(module);
                if (result == null) result = caseNode(module);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.GROUPING: {
                Grouping grouping = (Grouping)theEObject;
                T result = caseGrouping(grouping);
                if (result == null) result = caseNamedContainingNode(grouping);
                if (result == null) result = caseTaggedNode(grouping);
                if (result == null) result = caseNamedNode(grouping);
                if (result == null) result = caseContainingNode(grouping);
                if (result == null) result = caseNode(grouping);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.LEAF: {
                Leaf leaf = (Leaf)theEObject;
                T result = caseLeaf(leaf);
                if (result == null) result = caseNamedNode(leaf);
                if (result == null) result = caseTaggedNode(leaf);
                if (result == null) result = caseTypedNode(leaf);
                if (result == null) result = caseNode(leaf);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.CONTAINER: {
                Container container = (Container)theEObject;
                T result = caseContainer(container);
                if (result == null) result = caseNamedContainingNode(container);
                if (result == null) result = caseTaggedNode(container);
                if (result == null) result = caseNamedNode(container);
                if (result == null) result = caseContainingNode(container);
                if (result == null) result = caseNode(container);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.NODE: {
                Node node = (Node)theEObject;
                T result = caseNode(node);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.NAMED_NODE: {
                NamedNode namedNode = (NamedNode)theEObject;
                T result = caseNamedNode(namedNode);
                if (result == null) result = caseNode(namedNode);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.CONTAINING_NODE: {
                ContainingNode containingNode = (ContainingNode)theEObject;
                T result = caseContainingNode(containingNode);
                if (result == null) result = caseNode(containingNode);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.NAMED_CONTAINING_NODE: {
                NamedContainingNode namedContainingNode = (NamedContainingNode)theEObject;
                T result = caseNamedContainingNode(namedContainingNode);
                if (result == null) result = caseNamedNode(namedContainingNode);
                if (result == null) result = caseContainingNode(namedContainingNode);
                if (result == null) result = caseNode(namedContainingNode);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.SUBMODULE: {
                Submodule submodule = (Submodule)theEObject;
                T result = caseSubmodule(submodule);
                if (result == null) result = caseModule(submodule);
                if (result == null) result = caseNamedContainingNode(submodule);
                if (result == null) result = caseTaggedNode(submodule);
                if (result == null) result = caseNamedNode(submodule);
                if (result == null) result = caseContainingNode(submodule);
                if (result == null) result = caseNode(submodule);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.TYPEDEF: {
                Typedef typedef = (Typedef)theEObject;
                T result = caseTypedef(typedef);
                if (result == null) result = caseNamedNode(typedef);
                if (result == null) result = caseTaggedNode(typedef);
                if (result == null) result = caseNode(typedef);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.CHOICE: {
                Choice choice = (Choice)theEObject;
                T result = caseChoice(choice);
                if (result == null) result = caseNamedContainingNode(choice);
                if (result == null) result = caseTaggedNode(choice);
                if (result == null) result = caseNamedNode(choice);
                if (result == null) result = caseContainingNode(choice);
                if (result == null) result = caseNode(choice);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.LEAF_LIST: {
                LeafList leafList = (LeafList)theEObject;
                T result = caseLeafList(leafList);
                if (result == null) result = caseNamedNode(leafList);
                if (result == null) result = caseTaggedNode(leafList);
                if (result == null) result = caseTypedNode(leafList);
                if (result == null) result = caseNode(leafList);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.ANYXML: {
                Anyxml anyxml = (Anyxml)theEObject;
                T result = caseAnyxml(anyxml);
                if (result == null) result = caseNamedNode(anyxml);
                if (result == null) result = caseNode(anyxml);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.IMPORT: {
                Import import_ = (Import)theEObject;
                T result = caseImport(import_);
                if (result == null) result = caseNode(import_);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.INCLUDE: {
                Include include = (Include)theEObject;
                T result = caseInclude(include);
                if (result == null) result = caseNode(include);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.REVISION: {
                Revision revision = (Revision)theEObject;
                T result = caseRevision(revision);
                if (result == null) result = caseTaggedNode(revision);
                if (result == null) result = caseNamedNode(revision);
                if (result == null) result = caseNode(revision);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.BELONGS_TO: {
                BelongsTo belongsTo = (BelongsTo)theEObject;
                T result = caseBelongsTo(belongsTo);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.LIST: {
                List list = (List)theEObject;
                T result = caseList(list);
                if (result == null) result = caseNamedContainingNode(list);
                if (result == null) result = caseTaggedNode(list);
                if (result == null) result = caseNamedNode(list);
                if (result == null) result = caseContainingNode(list);
                if (result == null) result = caseNode(list);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.TAG: {
                Tag tag = (Tag)theEObject;
                T result = caseTag(tag);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.TAGGED_NODE: {
                TaggedNode taggedNode = (TaggedNode)theEObject;
                T result = caseTaggedNode(taggedNode);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.USES: {
                Uses uses = (Uses)theEObject;
                T result = caseUses(uses);
                if (result == null) result = caseNode(uses);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.RPC: {
                Rpc rpc = (Rpc)theEObject;
                T result = caseRpc(rpc);
                if (result == null) result = caseNamedContainingNode(rpc);
                if (result == null) result = caseNamedNode(rpc);
                if (result == null) result = caseContainingNode(rpc);
                if (result == null) result = caseNode(rpc);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.RPC_IO: {
                RpcIO rpcIO = (RpcIO)theEObject;
                T result = caseRpcIO(rpcIO);
                if (result == null) result = caseContainingNode(rpcIO);
                if (result == null) result = caseNode(rpcIO);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.NOTIFICATION: {
                Notification notification = (Notification)theEObject;
                T result = caseNotification(notification);
                if (result == null) result = caseNamedContainingNode(notification);
                if (result == null) result = caseNamedNode(notification);
                if (result == null) result = caseContainingNode(notification);
                if (result == null) result = caseNode(notification);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.AUGMENT: {
                Augment augment = (Augment)theEObject;
                T result = caseAugment(augment);
                if (result == null) result = caseNamedContainingNode(augment);
                if (result == null) result = caseNamedNode(augment);
                if (result == null) result = caseContainingNode(augment);
                if (result == null) result = caseNode(augment);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.EXTENSION: {
                Extension extension = (Extension)theEObject;
                T result = caseExtension(extension);
                if (result == null) result = caseNamedNode(extension);
                if (result == null) result = caseNode(extension);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.FEATURE: {
                Feature feature = (Feature)theEObject;
                T result = caseFeature(feature);
                if (result == null) result = caseNamedNode(feature);
                if (result == null) result = caseNode(feature);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.DEVIATION: {
                Deviation deviation = (Deviation)theEObject;
                T result = caseDeviation(deviation);
                if (result == null) result = caseNamedNode(deviation);
                if (result == null) result = caseNode(deviation);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.IDENTITY: {
                Identity identity = (Identity)theEObject;
                T result = caseIdentity(identity);
                if (result == null) result = caseNamedNode(identity);
                if (result == null) result = caseReferenceNode(identity);
                if (result == null) result = caseNode(identity);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.TYPED_NODE: {
                TypedNode typedNode = (TypedNode)theEObject;
                T result = caseTypedNode(typedNode);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.CHOICE_CASE: {
                ChoiceCase choiceCase = (ChoiceCase)theEObject;
                T result = caseChoiceCase(choiceCase);
                if (result == null) result = caseNamedContainingNode(choiceCase);
                if (result == null) result = caseTaggedNode(choiceCase);
                if (result == null) result = caseNamedNode(choiceCase);
                if (result == null) result = caseContainingNode(choiceCase);
                if (result == null) result = caseNode(choiceCase);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.LIST_KEY: {
                ListKey listKey = (ListKey)theEObject;
                T result = caseListKey(listKey);
                if (result == null) result = caseNamedNode(listKey);
                if (result == null) result = caseNode(listKey);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.TYPEREF: {
                Typeref typeref = (Typeref)theEObject;
                T result = caseTyperef(typeref);
                if (result == null) result = caseNamedNode(typeref);
                if (result == null) result = caseNode(typeref);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelPackage.REFERENCE_NODE: {
                ReferenceNode referenceNode = (ReferenceNode)theEObject;
                T result = caseReferenceNode(referenceNode);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            default: return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Module</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Module</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseModule(Module object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Grouping</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Grouping</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseGrouping(Grouping object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Leaf</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Leaf</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseLeaf(Leaf object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Container</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Container</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseContainer(Container object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Node</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Node</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseNode(Node object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Named Node</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Named Node</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseNamedNode(NamedNode object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Containing Node</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Containing Node</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseContainingNode(ContainingNode object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Named Containing Node</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Named Containing Node</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseNamedContainingNode(NamedContainingNode object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Submodule</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Submodule</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseSubmodule(Submodule object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Typedef</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Typedef</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseTypedef(Typedef object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Choice</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Choice</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseChoice(Choice object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Leaf List</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Leaf List</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseLeafList(LeafList object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Anyxml</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Anyxml</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseAnyxml(Anyxml object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Import</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Import</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseImport(Import object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Include</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Include</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseInclude(Include object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Revision</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Revision</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseRevision(Revision object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Belongs To</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Belongs To</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseBelongsTo(BelongsTo object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>List</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>List</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseList(List object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Tag</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Tag</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseTag(Tag object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Tagged Node</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Tagged Node</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseTaggedNode(TaggedNode object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Uses</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Uses</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseUses(Uses object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Rpc</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Rpc</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseRpc(Rpc object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Rpc IO</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Rpc IO</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseRpcIO(RpcIO object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Notification</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Notification</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseNotification(Notification object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Augment</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Augment</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseAugment(Augment object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Extension</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Extension</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseExtension(Extension object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Feature</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Feature</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseFeature(Feature object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Deviation</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Deviation</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseDeviation(Deviation object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Identity</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Identity</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseIdentity(Identity object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Typed Node</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Typed Node</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseTypedNode(TypedNode object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Choice Case</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Choice Case</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseChoiceCase(ChoiceCase object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>List Key</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>List Key</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseListKey(ListKey object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Typeref</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Typeref</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseTyperef(Typeref object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Reference Node</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Reference Node</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseReferenceNode(ReferenceNode object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch, but this is the last case anyway.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject)
     * @generated
     */
    @Override
    public T defaultCase(EObject object) {
        return null;
    }

} //ModelSwitch
