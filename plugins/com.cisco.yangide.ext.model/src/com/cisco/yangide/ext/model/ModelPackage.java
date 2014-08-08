/**
 */
package com.cisco.yangide.ext.model;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see com.cisco.yangide.ext.model.ModelFactory
 * @model kind="package"
 * @generated
 */
public interface ModelPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "model";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.example.org/model";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "model";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    ModelPackage eINSTANCE = com.cisco.yangide.ext.model.impl.ModelPackageImpl.init();

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.Node <em>Node</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.Node
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getNode()
     * @generated
     */
    int NODE = 4;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NODE__PARENT = 0;

    /**
     * The feature id for the '<em><b>Ref Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NODE__REF_ID = 1;

    /**
     * The number of structural features of the '<em>Node</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NODE_FEATURE_COUNT = 2;

    /**
     * The number of operations of the '<em>Node</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NODE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.NamedNode <em>Named Node</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.NamedNode
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getNamedNode()
     * @generated
     */
    int NAMED_NODE = 5;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NAMED_NODE__PARENT = NODE__PARENT;

    /**
     * The feature id for the '<em><b>Ref Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NAMED_NODE__REF_ID = NODE__REF_ID;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NAMED_NODE__NAME = NODE_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Named Node</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NAMED_NODE_FEATURE_COUNT = NODE_FEATURE_COUNT + 1;

    /**
     * The number of operations of the '<em>Named Node</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NAMED_NODE_OPERATION_COUNT = NODE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.NamedContainingNode <em>Named Containing Node</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.NamedContainingNode
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getNamedContainingNode()
     * @generated
     */
    int NAMED_CONTAINING_NODE = 7;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NAMED_CONTAINING_NODE__PARENT = NAMED_NODE__PARENT;

    /**
     * The feature id for the '<em><b>Ref Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NAMED_CONTAINING_NODE__REF_ID = NAMED_NODE__REF_ID;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NAMED_CONTAINING_NODE__NAME = NAMED_NODE__NAME;

    /**
     * The feature id for the '<em><b>Children</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NAMED_CONTAINING_NODE__CHILDREN = NAMED_NODE_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Named Containing Node</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NAMED_CONTAINING_NODE_FEATURE_COUNT = NAMED_NODE_FEATURE_COUNT + 1;

    /**
     * The number of operations of the '<em>Named Containing Node</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NAMED_CONTAINING_NODE_OPERATION_COUNT = NAMED_NODE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.impl.ModuleImpl <em>Module</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.impl.ModuleImpl
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getModule()
     * @generated
     */
    int MODULE = 0;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODULE__PARENT = NAMED_CONTAINING_NODE__PARENT;

    /**
     * The feature id for the '<em><b>Ref Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODULE__REF_ID = NAMED_CONTAINING_NODE__REF_ID;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODULE__NAME = NAMED_CONTAINING_NODE__NAME;

    /**
     * The feature id for the '<em><b>Children</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODULE__CHILDREN = NAMED_CONTAINING_NODE__CHILDREN;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODULE__NAMESPACE = NAMED_CONTAINING_NODE_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Revisions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODULE__REVISIONS = NAMED_CONTAINING_NODE_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>Module</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODULE_FEATURE_COUNT = NAMED_CONTAINING_NODE_FEATURE_COUNT + 2;

    /**
     * The number of operations of the '<em>Module</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODULE_OPERATION_COUNT = NAMED_CONTAINING_NODE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.impl.GroupingImpl <em>Grouping</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.impl.GroupingImpl
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getGrouping()
     * @generated
     */
    int GROUPING = 1;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GROUPING__PARENT = NAMED_CONTAINING_NODE__PARENT;

    /**
     * The feature id for the '<em><b>Ref Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GROUPING__REF_ID = NAMED_CONTAINING_NODE__REF_ID;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GROUPING__NAME = NAMED_CONTAINING_NODE__NAME;

    /**
     * The feature id for the '<em><b>Children</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GROUPING__CHILDREN = NAMED_CONTAINING_NODE__CHILDREN;

    /**
     * The feature id for the '<em><b>Tags</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GROUPING__TAGS = NAMED_CONTAINING_NODE_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Grouping</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GROUPING_FEATURE_COUNT = NAMED_CONTAINING_NODE_FEATURE_COUNT + 1;

    /**
     * The number of operations of the '<em>Grouping</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GROUPING_OPERATION_COUNT = NAMED_CONTAINING_NODE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.impl.LeafImpl <em>Leaf</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.impl.LeafImpl
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getLeaf()
     * @generated
     */
    int LEAF = 2;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LEAF__PARENT = NAMED_NODE__PARENT;

    /**
     * The feature id for the '<em><b>Ref Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LEAF__REF_ID = NAMED_NODE__REF_ID;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LEAF__NAME = NAMED_NODE__NAME;

    /**
     * The feature id for the '<em><b>Tags</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LEAF__TAGS = NAMED_NODE_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Leaf</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LEAF_FEATURE_COUNT = NAMED_NODE_FEATURE_COUNT + 1;

    /**
     * The number of operations of the '<em>Leaf</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LEAF_OPERATION_COUNT = NAMED_NODE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.impl.ContainerImpl <em>Container</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.impl.ContainerImpl
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getContainer()
     * @generated
     */
    int CONTAINER = 3;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTAINER__PARENT = NAMED_CONTAINING_NODE__PARENT;

    /**
     * The feature id for the '<em><b>Ref Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTAINER__REF_ID = NAMED_CONTAINING_NODE__REF_ID;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTAINER__NAME = NAMED_CONTAINING_NODE__NAME;

    /**
     * The feature id for the '<em><b>Children</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTAINER__CHILDREN = NAMED_CONTAINING_NODE__CHILDREN;

    /**
     * The feature id for the '<em><b>Tags</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTAINER__TAGS = NAMED_CONTAINING_NODE_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Container</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTAINER_FEATURE_COUNT = NAMED_CONTAINING_NODE_FEATURE_COUNT + 1;

    /**
     * The number of operations of the '<em>Container</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTAINER_OPERATION_COUNT = NAMED_CONTAINING_NODE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.ContainingNode <em>Containing Node</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.ContainingNode
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getContainingNode()
     * @generated
     */
    int CONTAINING_NODE = 6;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTAINING_NODE__PARENT = NODE__PARENT;

    /**
     * The feature id for the '<em><b>Ref Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTAINING_NODE__REF_ID = NODE__REF_ID;

    /**
     * The feature id for the '<em><b>Children</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTAINING_NODE__CHILDREN = NODE_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Containing Node</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTAINING_NODE_FEATURE_COUNT = NODE_FEATURE_COUNT + 1;

    /**
     * The number of operations of the '<em>Containing Node</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONTAINING_NODE_OPERATION_COUNT = NODE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.impl.SubmoduleImpl <em>Submodule</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.impl.SubmoduleImpl
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getSubmodule()
     * @generated
     */
    int SUBMODULE = 8;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SUBMODULE__PARENT = MODULE__PARENT;

    /**
     * The feature id for the '<em><b>Ref Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SUBMODULE__REF_ID = MODULE__REF_ID;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SUBMODULE__NAME = MODULE__NAME;

    /**
     * The feature id for the '<em><b>Children</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SUBMODULE__CHILDREN = MODULE__CHILDREN;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SUBMODULE__NAMESPACE = MODULE__NAMESPACE;

    /**
     * The feature id for the '<em><b>Revisions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SUBMODULE__REVISIONS = MODULE__REVISIONS;

    /**
     * The feature id for the '<em><b>Belongs To</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SUBMODULE__BELONGS_TO = MODULE_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Submodule</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SUBMODULE_FEATURE_COUNT = MODULE_FEATURE_COUNT + 1;

    /**
     * The number of operations of the '<em>Submodule</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SUBMODULE_OPERATION_COUNT = MODULE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.impl.TypedefImpl <em>Typedef</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.impl.TypedefImpl
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getTypedef()
     * @generated
     */
    int TYPEDEF = 9;

    /**
     * The number of structural features of the '<em>Typedef</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TYPEDEF_FEATURE_COUNT = 0;

    /**
     * The number of operations of the '<em>Typedef</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TYPEDEF_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.impl.ChoiceImpl <em>Choice</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.impl.ChoiceImpl
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getChoice()
     * @generated
     */
    int CHOICE = 10;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CHOICE__PARENT = NAMED_CONTAINING_NODE__PARENT;

    /**
     * The feature id for the '<em><b>Ref Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CHOICE__REF_ID = NAMED_CONTAINING_NODE__REF_ID;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CHOICE__NAME = NAMED_CONTAINING_NODE__NAME;

    /**
     * The feature id for the '<em><b>Children</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CHOICE__CHILDREN = NAMED_CONTAINING_NODE__CHILDREN;

    /**
     * The number of structural features of the '<em>Choice</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CHOICE_FEATURE_COUNT = NAMED_CONTAINING_NODE_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>Choice</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CHOICE_OPERATION_COUNT = NAMED_CONTAINING_NODE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.impl.LeafListImpl <em>Leaf List</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.impl.LeafListImpl
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getLeafList()
     * @generated
     */
    int LEAF_LIST = 11;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LEAF_LIST__PARENT = NAMED_CONTAINING_NODE__PARENT;

    /**
     * The feature id for the '<em><b>Ref Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LEAF_LIST__REF_ID = NAMED_CONTAINING_NODE__REF_ID;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LEAF_LIST__NAME = NAMED_CONTAINING_NODE__NAME;

    /**
     * The feature id for the '<em><b>Children</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LEAF_LIST__CHILDREN = NAMED_CONTAINING_NODE__CHILDREN;

    /**
     * The number of structural features of the '<em>Leaf List</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LEAF_LIST_FEATURE_COUNT = NAMED_CONTAINING_NODE_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>Leaf List</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LEAF_LIST_OPERATION_COUNT = NAMED_CONTAINING_NODE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.impl.AnyxmlImpl <em>Anyxml</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.impl.AnyxmlImpl
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getAnyxml()
     * @generated
     */
    int ANYXML = 12;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ANYXML__PARENT = NAMED_NODE__PARENT;

    /**
     * The feature id for the '<em><b>Ref Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ANYXML__REF_ID = NAMED_NODE__REF_ID;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ANYXML__NAME = NAMED_NODE__NAME;

    /**
     * The number of structural features of the '<em>Anyxml</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ANYXML_FEATURE_COUNT = NAMED_NODE_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>Anyxml</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ANYXML_OPERATION_COUNT = NAMED_NODE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.impl.ImportImpl <em>Import</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.impl.ImportImpl
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getImport()
     * @generated
     */
    int IMPORT = 13;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int IMPORT__PARENT = NODE__PARENT;

    /**
     * The feature id for the '<em><b>Ref Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int IMPORT__REF_ID = NODE__REF_ID;

    /**
     * The feature id for the '<em><b>Module</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int IMPORT__MODULE = NODE_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Import</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int IMPORT_FEATURE_COUNT = NODE_FEATURE_COUNT + 1;

    /**
     * The number of operations of the '<em>Import</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int IMPORT_OPERATION_COUNT = NODE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.impl.IncludeImpl <em>Include</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.impl.IncludeImpl
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getInclude()
     * @generated
     */
    int INCLUDE = 14;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INCLUDE__PARENT = NODE__PARENT;

    /**
     * The feature id for the '<em><b>Ref Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INCLUDE__REF_ID = NODE__REF_ID;

    /**
     * The feature id for the '<em><b>Submodule</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INCLUDE__SUBMODULE = NODE_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Include</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INCLUDE_FEATURE_COUNT = NODE_FEATURE_COUNT + 1;

    /**
     * The number of operations of the '<em>Include</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INCLUDE_OPERATION_COUNT = NODE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.impl.RevisionImpl <em>Revision</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.impl.RevisionImpl
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getRevision()
     * @generated
     */
    int REVISION = 15;

    /**
     * The number of structural features of the '<em>Revision</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int REVISION_FEATURE_COUNT = 0;

    /**
     * The number of operations of the '<em>Revision</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int REVISION_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.impl.BelongsToImpl <em>Belongs To</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.impl.BelongsToImpl
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getBelongsTo()
     * @generated
     */
    int BELONGS_TO = 16;

    /**
     * The feature id for the '<em><b>Owner Module</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BELONGS_TO__OWNER_MODULE = 0;

    /**
     * The number of structural features of the '<em>Belongs To</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BELONGS_TO_FEATURE_COUNT = 1;

    /**
     * The number of operations of the '<em>Belongs To</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BELONGS_TO_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.impl.ListImpl <em>List</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.impl.ListImpl
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getList()
     * @generated
     */
    int LIST = 17;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LIST__PARENT = NAMED_CONTAINING_NODE__PARENT;

    /**
     * The feature id for the '<em><b>Ref Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LIST__REF_ID = NAMED_CONTAINING_NODE__REF_ID;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LIST__NAME = NAMED_CONTAINING_NODE__NAME;

    /**
     * The feature id for the '<em><b>Children</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LIST__CHILDREN = NAMED_CONTAINING_NODE__CHILDREN;

    /**
     * The number of structural features of the '<em>List</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LIST_FEATURE_COUNT = NAMED_CONTAINING_NODE_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>List</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LIST_OPERATION_COUNT = NAMED_CONTAINING_NODE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.impl.TagImpl <em>Tag</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.impl.TagImpl
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getTag()
     * @generated
     */
    int TAG = 18;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TAG__NAME = 0;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TAG__VALUE = 1;

    /**
     * The number of structural features of the '<em>Tag</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TAG_FEATURE_COUNT = 2;

    /**
     * The number of operations of the '<em>Tag</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TAG_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.TaggedNode <em>Tagged Node</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.TaggedNode
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getTaggedNode()
     * @generated
     */
    int TAGGED_NODE = 19;

    /**
     * The feature id for the '<em><b>Tags</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TAGGED_NODE__TAGS = 0;

    /**
     * The number of structural features of the '<em>Tagged Node</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TAGGED_NODE_FEATURE_COUNT = 1;

    /**
     * The number of operations of the '<em>Tagged Node</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TAGGED_NODE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.impl.UsesImpl <em>Uses</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.impl.UsesImpl
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getUses()
     * @generated
     */
    int USES = 20;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int USES__PARENT = NODE__PARENT;

    /**
     * The feature id for the '<em><b>Ref Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int USES__REF_ID = NODE__REF_ID;

    /**
     * The feature id for the '<em><b>Grouping</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int USES__GROUPING = NODE_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>QName</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int USES__QNAME = NODE_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>Uses</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int USES_FEATURE_COUNT = NODE_FEATURE_COUNT + 2;

    /**
     * The number of operations of the '<em>Uses</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int USES_OPERATION_COUNT = NODE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.impl.RpcImpl <em>Rpc</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.impl.RpcImpl
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getRpc()
     * @generated
     */
    int RPC = 21;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RPC__PARENT = NAMED_CONTAINING_NODE__PARENT;

    /**
     * The feature id for the '<em><b>Ref Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RPC__REF_ID = NAMED_CONTAINING_NODE__REF_ID;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RPC__NAME = NAMED_CONTAINING_NODE__NAME;

    /**
     * The feature id for the '<em><b>Children</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RPC__CHILDREN = NAMED_CONTAINING_NODE__CHILDREN;

    /**
     * The number of structural features of the '<em>Rpc</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RPC_FEATURE_COUNT = NAMED_CONTAINING_NODE_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>Rpc</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RPC_OPERATION_COUNT = NAMED_CONTAINING_NODE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.impl.RpcIOImpl <em>Rpc IO</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.impl.RpcIOImpl
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getRpcIO()
     * @generated
     */
    int RPC_IO = 22;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RPC_IO__PARENT = CONTAINING_NODE__PARENT;

    /**
     * The feature id for the '<em><b>Ref Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RPC_IO__REF_ID = CONTAINING_NODE__REF_ID;

    /**
     * The feature id for the '<em><b>Children</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RPC_IO__CHILDREN = CONTAINING_NODE__CHILDREN;

    /**
     * The feature id for the '<em><b>Input</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RPC_IO__INPUT = CONTAINING_NODE_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Rpc IO</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RPC_IO_FEATURE_COUNT = CONTAINING_NODE_FEATURE_COUNT + 1;

    /**
     * The number of operations of the '<em>Rpc IO</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RPC_IO_OPERATION_COUNT = CONTAINING_NODE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.impl.NotificationImpl <em>Notification</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.impl.NotificationImpl
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getNotification()
     * @generated
     */
    int NOTIFICATION = 23;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NOTIFICATION__PARENT = NAMED_CONTAINING_NODE__PARENT;

    /**
     * The feature id for the '<em><b>Ref Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NOTIFICATION__REF_ID = NAMED_CONTAINING_NODE__REF_ID;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NOTIFICATION__NAME = NAMED_CONTAINING_NODE__NAME;

    /**
     * The feature id for the '<em><b>Children</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NOTIFICATION__CHILDREN = NAMED_CONTAINING_NODE__CHILDREN;

    /**
     * The number of structural features of the '<em>Notification</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NOTIFICATION_FEATURE_COUNT = NAMED_CONTAINING_NODE_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>Notification</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NOTIFICATION_OPERATION_COUNT = NAMED_CONTAINING_NODE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.impl.AugmentImpl <em>Augment</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.impl.AugmentImpl
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getAugment()
     * @generated
     */
    int AUGMENT = 24;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AUGMENT__PARENT = NAMED_CONTAINING_NODE__PARENT;

    /**
     * The feature id for the '<em><b>Ref Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AUGMENT__REF_ID = NAMED_CONTAINING_NODE__REF_ID;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AUGMENT__NAME = NAMED_CONTAINING_NODE__NAME;

    /**
     * The feature id for the '<em><b>Children</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AUGMENT__CHILDREN = NAMED_CONTAINING_NODE__CHILDREN;

    /**
     * The number of structural features of the '<em>Augment</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AUGMENT_FEATURE_COUNT = NAMED_CONTAINING_NODE_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>Augment</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AUGMENT_OPERATION_COUNT = NAMED_CONTAINING_NODE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.impl.ExtensionImpl <em>Extension</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.impl.ExtensionImpl
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getExtension()
     * @generated
     */
    int EXTENSION = 25;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EXTENSION__PARENT = NAMED_NODE__PARENT;

    /**
     * The feature id for the '<em><b>Ref Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EXTENSION__REF_ID = NAMED_NODE__REF_ID;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EXTENSION__NAME = NAMED_NODE__NAME;

    /**
     * The number of structural features of the '<em>Extension</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EXTENSION_FEATURE_COUNT = NAMED_NODE_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>Extension</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EXTENSION_OPERATION_COUNT = NAMED_NODE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.impl.FeatureImpl <em>Feature</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.impl.FeatureImpl
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getFeature()
     * @generated
     */
    int FEATURE = 26;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FEATURE__PARENT = NAMED_NODE__PARENT;

    /**
     * The feature id for the '<em><b>Ref Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FEATURE__REF_ID = NAMED_NODE__REF_ID;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FEATURE__NAME = NAMED_NODE__NAME;

    /**
     * The number of structural features of the '<em>Feature</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FEATURE_FEATURE_COUNT = NAMED_NODE_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>Feature</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FEATURE_OPERATION_COUNT = NAMED_NODE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.impl.DeviationImpl <em>Deviation</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.impl.DeviationImpl
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getDeviation()
     * @generated
     */
    int DEVIATION = 27;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVIATION__PARENT = NAMED_NODE__PARENT;

    /**
     * The feature id for the '<em><b>Ref Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVIATION__REF_ID = NAMED_NODE__REF_ID;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVIATION__NAME = NAMED_NODE__NAME;

    /**
     * The number of structural features of the '<em>Deviation</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVIATION_FEATURE_COUNT = NAMED_NODE_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>Deviation</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVIATION_OPERATION_COUNT = NAMED_NODE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link com.cisco.yangide.ext.model.impl.IdentityImpl <em>Identity</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.cisco.yangide.ext.model.impl.IdentityImpl
     * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getIdentity()
     * @generated
     */
    int IDENTITY = 28;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int IDENTITY__PARENT = NAMED_NODE__PARENT;

    /**
     * The feature id for the '<em><b>Ref Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int IDENTITY__REF_ID = NAMED_NODE__REF_ID;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int IDENTITY__NAME = NAMED_NODE__NAME;

    /**
     * The feature id for the '<em><b>Base</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int IDENTITY__BASE = NAMED_NODE_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Identity</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int IDENTITY_FEATURE_COUNT = NAMED_NODE_FEATURE_COUNT + 1;

    /**
     * The number of operations of the '<em>Identity</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int IDENTITY_OPERATION_COUNT = NAMED_NODE_OPERATION_COUNT + 0;


    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.Module <em>Module</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Module</em>'.
     * @see com.cisco.yangide.ext.model.Module
     * @generated
     */
    EClass getModule();

    /**
     * Returns the meta object for the attribute '{@link com.cisco.yangide.ext.model.Module#getNamespace <em>Namespace</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Namespace</em>'.
     * @see com.cisco.yangide.ext.model.Module#getNamespace()
     * @see #getModule()
     * @generated
     */
    EAttribute getModule_Namespace();

    /**
     * Returns the meta object for the containment reference list '{@link com.cisco.yangide.ext.model.Module#getRevisions <em>Revisions</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Revisions</em>'.
     * @see com.cisco.yangide.ext.model.Module#getRevisions()
     * @see #getModule()
     * @generated
     */
    EReference getModule_Revisions();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.Grouping <em>Grouping</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Grouping</em>'.
     * @see com.cisco.yangide.ext.model.Grouping
     * @generated
     */
    EClass getGrouping();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.Leaf <em>Leaf</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Leaf</em>'.
     * @see com.cisco.yangide.ext.model.Leaf
     * @generated
     */
    EClass getLeaf();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.Container <em>Container</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Container</em>'.
     * @see com.cisco.yangide.ext.model.Container
     * @generated
     */
    EClass getContainer();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.Node <em>Node</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Node</em>'.
     * @see com.cisco.yangide.ext.model.Node
     * @generated
     */
    EClass getNode();

    /**
     * Returns the meta object for the reference '{@link com.cisco.yangide.ext.model.Node#getParent <em>Parent</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Parent</em>'.
     * @see com.cisco.yangide.ext.model.Node#getParent()
     * @see #getNode()
     * @generated
     */
    EReference getNode_Parent();

    /**
     * Returns the meta object for the attribute '{@link com.cisco.yangide.ext.model.Node#getRefId <em>Ref Id</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Ref Id</em>'.
     * @see com.cisco.yangide.ext.model.Node#getRefId()
     * @see #getNode()
     * @generated
     */
    EAttribute getNode_RefId();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.NamedNode <em>Named Node</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Named Node</em>'.
     * @see com.cisco.yangide.ext.model.NamedNode
     * @generated
     */
    EClass getNamedNode();

    /**
     * Returns the meta object for the attribute '{@link com.cisco.yangide.ext.model.NamedNode#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.cisco.yangide.ext.model.NamedNode#getName()
     * @see #getNamedNode()
     * @generated
     */
    EAttribute getNamedNode_Name();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.ContainingNode <em>Containing Node</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Containing Node</em>'.
     * @see com.cisco.yangide.ext.model.ContainingNode
     * @generated
     */
    EClass getContainingNode();

    /**
     * Returns the meta object for the containment reference list '{@link com.cisco.yangide.ext.model.ContainingNode#getChildren <em>Children</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Children</em>'.
     * @see com.cisco.yangide.ext.model.ContainingNode#getChildren()
     * @see #getContainingNode()
     * @generated
     */
    EReference getContainingNode_Children();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.NamedContainingNode <em>Named Containing Node</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Named Containing Node</em>'.
     * @see com.cisco.yangide.ext.model.NamedContainingNode
     * @generated
     */
    EClass getNamedContainingNode();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.Submodule <em>Submodule</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Submodule</em>'.
     * @see com.cisco.yangide.ext.model.Submodule
     * @generated
     */
    EClass getSubmodule();

    /**
     * Returns the meta object for the reference '{@link com.cisco.yangide.ext.model.Submodule#getBelongsTo <em>Belongs To</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Belongs To</em>'.
     * @see com.cisco.yangide.ext.model.Submodule#getBelongsTo()
     * @see #getSubmodule()
     * @generated
     */
    EReference getSubmodule_BelongsTo();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.Typedef <em>Typedef</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Typedef</em>'.
     * @see com.cisco.yangide.ext.model.Typedef
     * @generated
     */
    EClass getTypedef();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.Choice <em>Choice</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Choice</em>'.
     * @see com.cisco.yangide.ext.model.Choice
     * @generated
     */
    EClass getChoice();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.LeafList <em>Leaf List</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Leaf List</em>'.
     * @see com.cisco.yangide.ext.model.LeafList
     * @generated
     */
    EClass getLeafList();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.Anyxml <em>Anyxml</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Anyxml</em>'.
     * @see com.cisco.yangide.ext.model.Anyxml
     * @generated
     */
    EClass getAnyxml();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.Import <em>Import</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Import</em>'.
     * @see com.cisco.yangide.ext.model.Import
     * @generated
     */
    EClass getImport();

    /**
     * Returns the meta object for the containment reference '{@link com.cisco.yangide.ext.model.Import#getModule <em>Module</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Module</em>'.
     * @see com.cisco.yangide.ext.model.Import#getModule()
     * @see #getImport()
     * @generated
     */
    EReference getImport_Module();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.Include <em>Include</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Include</em>'.
     * @see com.cisco.yangide.ext.model.Include
     * @generated
     */
    EClass getInclude();

    /**
     * Returns the meta object for the containment reference '{@link com.cisco.yangide.ext.model.Include#getSubmodule <em>Submodule</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Submodule</em>'.
     * @see com.cisco.yangide.ext.model.Include#getSubmodule()
     * @see #getInclude()
     * @generated
     */
    EReference getInclude_Submodule();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.Revision <em>Revision</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Revision</em>'.
     * @see com.cisco.yangide.ext.model.Revision
     * @generated
     */
    EClass getRevision();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.BelongsTo <em>Belongs To</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Belongs To</em>'.
     * @see com.cisco.yangide.ext.model.BelongsTo
     * @generated
     */
    EClass getBelongsTo();

    /**
     * Returns the meta object for the reference '{@link com.cisco.yangide.ext.model.BelongsTo#getOwnerModule <em>Owner Module</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Owner Module</em>'.
     * @see com.cisco.yangide.ext.model.BelongsTo#getOwnerModule()
     * @see #getBelongsTo()
     * @generated
     */
    EReference getBelongsTo_OwnerModule();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.List <em>List</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>List</em>'.
     * @see com.cisco.yangide.ext.model.List
     * @generated
     */
    EClass getList();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.Tag <em>Tag</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Tag</em>'.
     * @see com.cisco.yangide.ext.model.Tag
     * @generated
     */
    EClass getTag();

    /**
     * Returns the meta object for the attribute '{@link com.cisco.yangide.ext.model.Tag#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.cisco.yangide.ext.model.Tag#getName()
     * @see #getTag()
     * @generated
     */
    EAttribute getTag_Name();

    /**
     * Returns the meta object for the attribute '{@link com.cisco.yangide.ext.model.Tag#getValue <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Value</em>'.
     * @see com.cisco.yangide.ext.model.Tag#getValue()
     * @see #getTag()
     * @generated
     */
    EAttribute getTag_Value();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.TaggedNode <em>Tagged Node</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Tagged Node</em>'.
     * @see com.cisco.yangide.ext.model.TaggedNode
     * @generated
     */
    EClass getTaggedNode();

    /**
     * Returns the meta object for the containment reference list '{@link com.cisco.yangide.ext.model.TaggedNode#getTags <em>Tags</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Tags</em>'.
     * @see com.cisco.yangide.ext.model.TaggedNode#getTags()
     * @see #getTaggedNode()
     * @generated
     */
    EReference getTaggedNode_Tags();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.Uses <em>Uses</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Uses</em>'.
     * @see com.cisco.yangide.ext.model.Uses
     * @generated
     */
    EClass getUses();

    /**
     * Returns the meta object for the reference '{@link com.cisco.yangide.ext.model.Uses#getGrouping <em>Grouping</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Grouping</em>'.
     * @see com.cisco.yangide.ext.model.Uses#getGrouping()
     * @see #getUses()
     * @generated
     */
    EReference getUses_Grouping();

    /**
     * Returns the meta object for the attribute '{@link com.cisco.yangide.ext.model.Uses#getQName <em>QName</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>QName</em>'.
     * @see com.cisco.yangide.ext.model.Uses#getQName()
     * @see #getUses()
     * @generated
     */
    EAttribute getUses_QName();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.Rpc <em>Rpc</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Rpc</em>'.
     * @see com.cisco.yangide.ext.model.Rpc
     * @generated
     */
    EClass getRpc();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.RpcIO <em>Rpc IO</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Rpc IO</em>'.
     * @see com.cisco.yangide.ext.model.RpcIO
     * @generated
     */
    EClass getRpcIO();

    /**
     * Returns the meta object for the attribute '{@link com.cisco.yangide.ext.model.RpcIO#isInput <em>Input</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Input</em>'.
     * @see com.cisco.yangide.ext.model.RpcIO#isInput()
     * @see #getRpcIO()
     * @generated
     */
    EAttribute getRpcIO_Input();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.Notification <em>Notification</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Notification</em>'.
     * @see com.cisco.yangide.ext.model.Notification
     * @generated
     */
    EClass getNotification();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.Augment <em>Augment</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Augment</em>'.
     * @see com.cisco.yangide.ext.model.Augment
     * @generated
     */
    EClass getAugment();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.Extension <em>Extension</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Extension</em>'.
     * @see com.cisco.yangide.ext.model.Extension
     * @generated
     */
    EClass getExtension();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.Feature <em>Feature</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Feature</em>'.
     * @see com.cisco.yangide.ext.model.Feature
     * @generated
     */
    EClass getFeature();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.Deviation <em>Deviation</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Deviation</em>'.
     * @see com.cisco.yangide.ext.model.Deviation
     * @generated
     */
    EClass getDeviation();

    /**
     * Returns the meta object for class '{@link com.cisco.yangide.ext.model.Identity <em>Identity</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Identity</em>'.
     * @see com.cisco.yangide.ext.model.Identity
     * @generated
     */
    EClass getIdentity();

    /**
     * Returns the meta object for the reference '{@link com.cisco.yangide.ext.model.Identity#getBase <em>Base</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Base</em>'.
     * @see com.cisco.yangide.ext.model.Identity#getBase()
     * @see #getIdentity()
     * @generated
     */
    EReference getIdentity_Base();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    ModelFactory getModelFactory();

    /**
     * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each operation of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals {
        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.impl.ModuleImpl <em>Module</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.impl.ModuleImpl
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getModule()
         * @generated
         */
        EClass MODULE = eINSTANCE.getModule();

        /**
         * The meta object literal for the '<em><b>Namespace</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MODULE__NAMESPACE = eINSTANCE.getModule_Namespace();

        /**
         * The meta object literal for the '<em><b>Revisions</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MODULE__REVISIONS = eINSTANCE.getModule_Revisions();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.impl.GroupingImpl <em>Grouping</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.impl.GroupingImpl
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getGrouping()
         * @generated
         */
        EClass GROUPING = eINSTANCE.getGrouping();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.impl.LeafImpl <em>Leaf</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.impl.LeafImpl
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getLeaf()
         * @generated
         */
        EClass LEAF = eINSTANCE.getLeaf();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.impl.ContainerImpl <em>Container</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.impl.ContainerImpl
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getContainer()
         * @generated
         */
        EClass CONTAINER = eINSTANCE.getContainer();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.Node <em>Node</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.Node
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getNode()
         * @generated
         */
        EClass NODE = eINSTANCE.getNode();

        /**
         * The meta object literal for the '<em><b>Parent</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference NODE__PARENT = eINSTANCE.getNode_Parent();

        /**
         * The meta object literal for the '<em><b>Ref Id</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute NODE__REF_ID = eINSTANCE.getNode_RefId();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.NamedNode <em>Named Node</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.NamedNode
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getNamedNode()
         * @generated
         */
        EClass NAMED_NODE = eINSTANCE.getNamedNode();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute NAMED_NODE__NAME = eINSTANCE.getNamedNode_Name();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.ContainingNode <em>Containing Node</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.ContainingNode
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getContainingNode()
         * @generated
         */
        EClass CONTAINING_NODE = eINSTANCE.getContainingNode();

        /**
         * The meta object literal for the '<em><b>Children</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CONTAINING_NODE__CHILDREN = eINSTANCE.getContainingNode_Children();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.NamedContainingNode <em>Named Containing Node</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.NamedContainingNode
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getNamedContainingNode()
         * @generated
         */
        EClass NAMED_CONTAINING_NODE = eINSTANCE.getNamedContainingNode();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.impl.SubmoduleImpl <em>Submodule</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.impl.SubmoduleImpl
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getSubmodule()
         * @generated
         */
        EClass SUBMODULE = eINSTANCE.getSubmodule();

        /**
         * The meta object literal for the '<em><b>Belongs To</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference SUBMODULE__BELONGS_TO = eINSTANCE.getSubmodule_BelongsTo();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.impl.TypedefImpl <em>Typedef</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.impl.TypedefImpl
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getTypedef()
         * @generated
         */
        EClass TYPEDEF = eINSTANCE.getTypedef();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.impl.ChoiceImpl <em>Choice</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.impl.ChoiceImpl
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getChoice()
         * @generated
         */
        EClass CHOICE = eINSTANCE.getChoice();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.impl.LeafListImpl <em>Leaf List</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.impl.LeafListImpl
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getLeafList()
         * @generated
         */
        EClass LEAF_LIST = eINSTANCE.getLeafList();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.impl.AnyxmlImpl <em>Anyxml</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.impl.AnyxmlImpl
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getAnyxml()
         * @generated
         */
        EClass ANYXML = eINSTANCE.getAnyxml();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.impl.ImportImpl <em>Import</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.impl.ImportImpl
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getImport()
         * @generated
         */
        EClass IMPORT = eINSTANCE.getImport();

        /**
         * The meta object literal for the '<em><b>Module</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference IMPORT__MODULE = eINSTANCE.getImport_Module();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.impl.IncludeImpl <em>Include</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.impl.IncludeImpl
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getInclude()
         * @generated
         */
        EClass INCLUDE = eINSTANCE.getInclude();

        /**
         * The meta object literal for the '<em><b>Submodule</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference INCLUDE__SUBMODULE = eINSTANCE.getInclude_Submodule();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.impl.RevisionImpl <em>Revision</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.impl.RevisionImpl
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getRevision()
         * @generated
         */
        EClass REVISION = eINSTANCE.getRevision();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.impl.BelongsToImpl <em>Belongs To</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.impl.BelongsToImpl
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getBelongsTo()
         * @generated
         */
        EClass BELONGS_TO = eINSTANCE.getBelongsTo();

        /**
         * The meta object literal for the '<em><b>Owner Module</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference BELONGS_TO__OWNER_MODULE = eINSTANCE.getBelongsTo_OwnerModule();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.impl.ListImpl <em>List</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.impl.ListImpl
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getList()
         * @generated
         */
        EClass LIST = eINSTANCE.getList();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.impl.TagImpl <em>Tag</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.impl.TagImpl
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getTag()
         * @generated
         */
        EClass TAG = eINSTANCE.getTag();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TAG__NAME = eINSTANCE.getTag_Name();

        /**
         * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TAG__VALUE = eINSTANCE.getTag_Value();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.TaggedNode <em>Tagged Node</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.TaggedNode
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getTaggedNode()
         * @generated
         */
        EClass TAGGED_NODE = eINSTANCE.getTaggedNode();

        /**
         * The meta object literal for the '<em><b>Tags</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TAGGED_NODE__TAGS = eINSTANCE.getTaggedNode_Tags();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.impl.UsesImpl <em>Uses</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.impl.UsesImpl
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getUses()
         * @generated
         */
        EClass USES = eINSTANCE.getUses();

        /**
         * The meta object literal for the '<em><b>Grouping</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference USES__GROUPING = eINSTANCE.getUses_Grouping();

        /**
         * The meta object literal for the '<em><b>QName</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute USES__QNAME = eINSTANCE.getUses_QName();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.impl.RpcImpl <em>Rpc</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.impl.RpcImpl
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getRpc()
         * @generated
         */
        EClass RPC = eINSTANCE.getRpc();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.impl.RpcIOImpl <em>Rpc IO</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.impl.RpcIOImpl
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getRpcIO()
         * @generated
         */
        EClass RPC_IO = eINSTANCE.getRpcIO();

        /**
         * The meta object literal for the '<em><b>Input</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute RPC_IO__INPUT = eINSTANCE.getRpcIO_Input();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.impl.NotificationImpl <em>Notification</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.impl.NotificationImpl
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getNotification()
         * @generated
         */
        EClass NOTIFICATION = eINSTANCE.getNotification();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.impl.AugmentImpl <em>Augment</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.impl.AugmentImpl
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getAugment()
         * @generated
         */
        EClass AUGMENT = eINSTANCE.getAugment();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.impl.ExtensionImpl <em>Extension</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.impl.ExtensionImpl
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getExtension()
         * @generated
         */
        EClass EXTENSION = eINSTANCE.getExtension();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.impl.FeatureImpl <em>Feature</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.impl.FeatureImpl
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getFeature()
         * @generated
         */
        EClass FEATURE = eINSTANCE.getFeature();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.impl.DeviationImpl <em>Deviation</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.impl.DeviationImpl
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getDeviation()
         * @generated
         */
        EClass DEVIATION = eINSTANCE.getDeviation();

        /**
         * The meta object literal for the '{@link com.cisco.yangide.ext.model.impl.IdentityImpl <em>Identity</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see com.cisco.yangide.ext.model.impl.IdentityImpl
         * @see com.cisco.yangide.ext.model.impl.ModelPackageImpl#getIdentity()
         * @generated
         */
        EClass IDENTITY = eINSTANCE.getIdentity();

        /**
         * The meta object literal for the '<em><b>Base</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference IDENTITY__BASE = eINSTANCE.getIdentity_Base();

    }

} //ModelPackage
