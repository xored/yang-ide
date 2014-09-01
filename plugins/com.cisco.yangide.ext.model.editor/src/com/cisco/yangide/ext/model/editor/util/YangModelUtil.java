package com.cisco.yangide.ext.model.editor.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.cisco.yangide.core.dom.ASTCompositeNode;
import com.cisco.yangide.core.dom.ASTNamedNode;
import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.AnyXmlSchemaNode;
import com.cisco.yangide.core.dom.AugmentationSchema;
import com.cisco.yangide.core.dom.ChoiceCaseNode;
import com.cisco.yangide.core.dom.ChoiceNode;
import com.cisco.yangide.core.dom.ContrainerSchemaNode;
import com.cisco.yangide.core.dom.Deviation;
import com.cisco.yangide.core.dom.ExtensionDefinition;
import com.cisco.yangide.core.dom.FeatureDefinition;
import com.cisco.yangide.core.dom.GroupingDefinition;
import com.cisco.yangide.core.dom.IdentitySchemaNode;
import com.cisco.yangide.core.dom.LeafListSchemaNode;
import com.cisco.yangide.core.dom.LeafSchemaNode;
import com.cisco.yangide.core.dom.ListSchemaNode;
import com.cisco.yangide.core.dom.ModuleImport;
import com.cisco.yangide.core.dom.NotificationDefinition;
import com.cisco.yangide.core.dom.RevisionNode;
import com.cisco.yangide.core.dom.RpcDefinition;
import com.cisco.yangide.core.dom.RpcInputNode;
import com.cisco.yangide.core.dom.RpcOutputNode;
import com.cisco.yangide.core.dom.SimpleNamedNode;
import com.cisco.yangide.core.dom.SimpleNode;
import com.cisco.yangide.core.dom.TypeDefinition;
import com.cisco.yangide.core.dom.TypeReference;
import com.cisco.yangide.core.dom.UsesNode;
import com.cisco.yangide.ext.model.ContainingNode;
import com.cisco.yangide.ext.model.Grouping;
import com.cisco.yangide.ext.model.Identity;
import com.cisco.yangide.ext.model.Import;
import com.cisco.yangide.ext.model.ModelFactory;
import com.cisco.yangide.ext.model.ModelPackage;
import com.cisco.yangide.ext.model.Module;
import com.cisco.yangide.ext.model.NamedNode;
import com.cisco.yangide.ext.model.Node;
import com.cisco.yangide.ext.model.ReferenceNode;
import com.cisco.yangide.ext.model.RpcIO;
import com.cisco.yangide.ext.model.Tag;
import com.cisco.yangide.ext.model.TaggedNode;
import com.cisco.yangide.ext.model.Uses;

public class YangModelUtil {

    private YangModelUtil() {
        super();
    }

    public static Map<EClass, List<EClass>> compositeNodeMap = new HashMap<EClass, List<EClass>>();
    private static Map<EClass, List<YangTag>> taggedNodeMap = new HashMap<EClass, List<YangTag>>();
    // key has reference to value
    private static final Map<EClass, EClass> connections = new HashMap<EClass, EClass>();

    private static Map<Class<? extends ASTNode>, EClass> astNodes = new HashMap<Class<? extends ASTNode>, EClass>();
    public static final ModelPackage MODEL_PACKAGE = ModelPackage.eINSTANCE;

    static {
        compositeNodeMap.put(MODEL_PACKAGE.getAugment(), Arrays.asList(MODEL_PACKAGE.getAnyxml(),
                MODEL_PACKAGE.getChoice(), MODEL_PACKAGE.getContainer(), MODEL_PACKAGE.getLeaf(),
                MODEL_PACKAGE.getLeafList(), MODEL_PACKAGE.getList(), MODEL_PACKAGE.getUses()));
        compositeNodeMap.put(MODEL_PACKAGE.getChoice(), Arrays.asList(MODEL_PACKAGE.getAnyxml(),
                MODEL_PACKAGE.getChoiceCase(), MODEL_PACKAGE.getContainer(), MODEL_PACKAGE.getLeaf(),
                MODEL_PACKAGE.getLeafList(), MODEL_PACKAGE.getList()));
        compositeNodeMap.put(MODEL_PACKAGE.getChoiceCase(), Arrays.asList(MODEL_PACKAGE.getAnyxml(),
                MODEL_PACKAGE.getChoice(), MODEL_PACKAGE.getContainer(), MODEL_PACKAGE.getLeaf(),
                MODEL_PACKAGE.getLeafList(), MODEL_PACKAGE.getList(), MODEL_PACKAGE.getUses()));
        compositeNodeMap.put(MODEL_PACKAGE.getContainer(), Arrays.asList(MODEL_PACKAGE.getAnyxml(),
                MODEL_PACKAGE.getChoice(), MODEL_PACKAGE.getContainer(), MODEL_PACKAGE.getGrouping(),
                MODEL_PACKAGE.getLeaf(), MODEL_PACKAGE.getLeafList(), MODEL_PACKAGE.getList(),
                MODEL_PACKAGE.getTypedef(), MODEL_PACKAGE.getUses()));
        compositeNodeMap.put(MODEL_PACKAGE.getGrouping(), Arrays.asList(MODEL_PACKAGE.getAnyxml(),
                MODEL_PACKAGE.getChoice(), MODEL_PACKAGE.getContainer(), MODEL_PACKAGE.getGrouping(),
                MODEL_PACKAGE.getLeaf(), MODEL_PACKAGE.getLeafList(), MODEL_PACKAGE.getList(),
                MODEL_PACKAGE.getTypedef(), MODEL_PACKAGE.getUses()));
        compositeNodeMap.put(MODEL_PACKAGE.getList(), Arrays.asList(MODEL_PACKAGE.getAnyxml(),
                MODEL_PACKAGE.getChoice(), MODEL_PACKAGE.getContainer(), MODEL_PACKAGE.getGrouping(),
                MODEL_PACKAGE.getListKey(), MODEL_PACKAGE.getLeaf(), MODEL_PACKAGE.getLeafList(),
                MODEL_PACKAGE.getList(), MODEL_PACKAGE.getTypedef(), MODEL_PACKAGE.getUses()));
        compositeNodeMap.put(MODEL_PACKAGE.getModule(), Arrays.asList(MODEL_PACKAGE.getAnyxml(),
                MODEL_PACKAGE.getAugment(), MODEL_PACKAGE.getChoice(), MODEL_PACKAGE.getContainer(),
                MODEL_PACKAGE.getDeviation(), MODEL_PACKAGE.getExtension(), MODEL_PACKAGE.getFeature(),
                MODEL_PACKAGE.getGrouping(), MODEL_PACKAGE.getIdentity(), MODEL_PACKAGE.getImport(),
                MODEL_PACKAGE.getLeaf(), MODEL_PACKAGE.getLeafList(), MODEL_PACKAGE.getList(),
                MODEL_PACKAGE.getNotification(), MODEL_PACKAGE.getRpc(), MODEL_PACKAGE.getTypedef(),
                MODEL_PACKAGE.getUses()));
        compositeNodeMap.put(MODEL_PACKAGE.getNotification(), Arrays.asList(MODEL_PACKAGE.getAnyxml(),
                MODEL_PACKAGE.getChoice(), MODEL_PACKAGE.getContainer(), MODEL_PACKAGE.getGrouping(),
                MODEL_PACKAGE.getLeaf(), MODEL_PACKAGE.getLeaf(), MODEL_PACKAGE.getLeafList(), MODEL_PACKAGE.getList(),
                MODEL_PACKAGE.getTypedef(), MODEL_PACKAGE.getUses()));
        compositeNodeMap.put(MODEL_PACKAGE.getRpc(),
                Arrays.asList(MODEL_PACKAGE.getRpcIO(), MODEL_PACKAGE.getGrouping(), MODEL_PACKAGE.getTypedef()));
        compositeNodeMap.put(MODEL_PACKAGE.getRpcIO(), Arrays.asList(MODEL_PACKAGE.getAnyxml(),
                MODEL_PACKAGE.getChoice(), MODEL_PACKAGE.getContainer(), MODEL_PACKAGE.getGrouping(),
                MODEL_PACKAGE.getLeaf(), MODEL_PACKAGE.getLeafList(), MODEL_PACKAGE.getList(),
                MODEL_PACKAGE.getTypedef(), MODEL_PACKAGE.getUses()));

        taggedNodeMap.put(MODEL_PACKAGE.getAnyxml(), Arrays.asList(YangTag.CONFIG, YangTag.DESCRIPTION,
                YangTag.MANDATORY, YangTag.REFERENCE, YangTag.STATUS));
        taggedNodeMap.put(MODEL_PACKAGE.getChoice(), Arrays.asList(YangTag.CONFIG, YangTag.DEFAULT,
                YangTag.DESCRIPTION, YangTag.MANDATORY, YangTag.REFERENCE, YangTag.STATUS));
        taggedNodeMap.put(MODEL_PACKAGE.getChoiceCase(),
                Arrays.asList(YangTag.DESCRIPTION, YangTag.REFERENCE, YangTag.STATUS));
        taggedNodeMap
                .put(MODEL_PACKAGE.getContainer(), Arrays.asList(YangTag.CONFIG, YangTag.DESCRIPTION, YangTag.PRESENCE,
                        YangTag.REFERENCE, YangTag.STATUS));
        taggedNodeMap.put(MODEL_PACKAGE.getGrouping(),
                Arrays.asList(YangTag.DESCRIPTION, YangTag.REFERENCE, YangTag.STATUS));
        taggedNodeMap.put(MODEL_PACKAGE.getLeaf(), Arrays.asList(YangTag.CONFIG, YangTag.DEFAULT, YangTag.DESCRIPTION,
                YangTag.MANDATORY, YangTag.REFERENCE, YangTag.STATUS, YangTag.UNITS));
        taggedNodeMap.put(MODEL_PACKAGE.getModule(), Arrays.asList(YangTag.CONTACT, YangTag.DESCRIPTION,
                YangTag.NAMESPACE, YangTag.ORGANIZATION, YangTag.PREFIX, YangTag.REFERENCE, YangTag.YANG_VERSION));
        taggedNodeMap.put(MODEL_PACKAGE.getRevision(), Arrays.asList(YangTag.DESCRIPTION, YangTag.REFERENCE));
        taggedNodeMap.put(MODEL_PACKAGE.getLeafList(), Arrays.asList(YangTag.CONFIG, YangTag.DESCRIPTION,
                YangTag.MAX_ELEMENTS, YangTag.MIN_ELEMENTS, YangTag.ORDERED_BY, YangTag.REFERENCE, YangTag.STATUS,
                YangTag.UNITS));
        taggedNodeMap.put(MODEL_PACKAGE.getList(), Arrays.asList(YangTag.CONFIG, YangTag.DESCRIPTION,
                YangTag.MAX_ELEMENTS, YangTag.MIN_ELEMENTS, YangTag.ORDERED_BY, YangTag.REFERENCE, YangTag.STATUS,
                YangTag.UNITS));
        taggedNodeMap
                .put(MODEL_PACKAGE.getRpc(), Arrays.asList(YangTag.DESCRIPTION, YangTag.REFERENCE, YangTag.STATUS));
        taggedNodeMap.put(MODEL_PACKAGE.getTypedef(),
                Arrays.asList(YangTag.DEFAULT, YangTag.DESCRIPTION, YangTag.REFERENCE, YangTag.STATUS, YangTag.UNITS));

        astNodes.put(com.cisco.yangide.core.dom.Module.class, MODEL_PACKAGE.getModule());
        astNodes.put(GroupingDefinition.class, MODEL_PACKAGE.getGrouping());
        astNodes.put(ContrainerSchemaNode.class, MODEL_PACKAGE.getContainer());
        astNodes.put(LeafSchemaNode.class, MODEL_PACKAGE.getLeaf());
        astNodes.put(RpcDefinition.class, MODEL_PACKAGE.getRpc());
        astNodes.put(RpcOutputNode.class, MODEL_PACKAGE.getRpcIO());
        astNodes.put(RpcInputNode.class, MODEL_PACKAGE.getRpcIO());
        astNodes.put(UsesNode.class, MODEL_PACKAGE.getUses());
        astNodes.put(NotificationDefinition.class, MODEL_PACKAGE.getNotification());
        astNodes.put(AugmentationSchema.class, MODEL_PACKAGE.getAugment());
        astNodes.put(Deviation.class, MODEL_PACKAGE.getDeviation());
        astNodes.put(ExtensionDefinition.class, MODEL_PACKAGE.getExtension());
        astNodes.put(FeatureDefinition.class, MODEL_PACKAGE.getFeature());
        astNodes.put(IdentitySchemaNode.class, MODEL_PACKAGE.getIdentity());
        astNodes.put(ModuleImport.class, MODEL_PACKAGE.getImport());
        astNodes.put(RevisionNode.class, MODEL_PACKAGE.getRevision());
        astNodes.put(LeafListSchemaNode.class, MODEL_PACKAGE.getLeafList());
        astNodes.put(ListSchemaNode.class, MODEL_PACKAGE.getList());
        astNodes.put(ChoiceNode.class, MODEL_PACKAGE.getChoice());
        astNodes.put(ChoiceCaseNode.class, MODEL_PACKAGE.getChoiceCase());
        astNodes.put(TypeDefinition.class, MODEL_PACKAGE.getTypedef());
        astNodes.put(TypeReference.class, MODEL_PACKAGE.getTyperef());
        astNodes.put(AnyXmlSchemaNode.class, MODEL_PACKAGE.getAnyxml());

        connections.put(MODEL_PACKAGE.getUses(), MODEL_PACKAGE.getGrouping());
        connections.put(MODEL_PACKAGE.getIdentity(), MODEL_PACKAGE.getIdentity());
    }

    public static boolean canContain(Object parent) {
        return checkType(MODEL_PACKAGE.getContainingNode(), parent);
    }

    public static boolean canContain(Object parent, Object child) {
        return checkType(MODEL_PACKAGE.getContainingNode(), parent) && checkType(MODEL_PACKAGE.getNode(), child)
                && canContain(((ContainingNode) parent).eClass(), ((Node) child).eClass())
                && !hasCircles((ContainingNode) parent, (Node) child);
    }

    public static boolean canContain(EClass parent, EClass child) {
        if (checkType(MODEL_PACKAGE.getContainingNode(), parent)) {
            for (EClass c : getPossibleChildren(parent)) {
                if (c.equals(child)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int getPositionInParent(Object parent, Object child) {
        if (null != parent && null != child && checkType(MODEL_PACKAGE.getContainingNode(), parent)
                && checkType(MODEL_PACKAGE.getNode(), child)) {
            if (((ContainingNode) parent).getChildren().contains(child)) {
                return ((ContainingNode) parent).getChildren().indexOf(child);
            }
        }
        return -1;
    }

    public static void move(Object source, Object target, Object obj, int pos) {
        if (source != target) {
            remove(source, obj);
            add(target, obj, pos);
        } else {
            move(target, obj, pos);
        }

    }

    public static void move(Object target, Object obj, int pos) {
        if (null != target && null != obj && checkType(MODEL_PACKAGE.getContainingNode(), target)
                && checkType(MODEL_PACKAGE.getNode(), obj)) {
            ((Node) obj).setParent((ContainingNode) target);
            if (pos >= ((ContainingNode) target).getChildren().size()) {
                pos = ((ContainingNode) target).getChildren().size() - 1;
            }
            ((ContainingNode) target).getChildren().move(pos, (Node) obj);
        }
    }

    public static void add(Object target, Object obj, int pos) {
        if (null != target && null != obj && checkType(MODEL_PACKAGE.getContainingNode(), target)
                && checkType(MODEL_PACKAGE.getNode(), obj)) {
            ((Node) obj).setParent((ContainingNode) target);
            if (pos >= ((ContainingNode) target).getChildren().size()) {
                ((ContainingNode) target).getChildren().add((Node) obj);
            } else {
                ((ContainingNode) target).getChildren().add(pos, (Node) obj);
            }
        }
    }

    public static void remove(Object source, Object obj) {
        if (null != source && null != obj && checkType(MODEL_PACKAGE.getContainingNode(), source)) {
            ((ContainingNode) source).getChildren().remove(obj);
        }

        if (null != obj && checkType(MODEL_PACKAGE.getNode(), obj)) {
            ((Node) obj).setParent(null);
        }
    }

    public static boolean hasReference(EObject tested) {
        return null != getConnectionReferenceObjectClass(tested);
    }

    public static EClass getConnectionReferenceObjectClass(EObject tested) {
        if (null != tested) {
            if (null == connections.get(tested.eClass())) {
                for (Map.Entry<EClass, EClass> c : connections.entrySet()) {
                    if (checkType(c.getKey(), tested)) {
                        return c.getValue();
                    }
                }
            } else {
                return connections.get(tested.eClass());
            }
        }
        return null;
    }

    public static boolean canBeReferenced(Object tested) {
        return null != getConnectionReferenceSubjectClass(tested);
    }

    public static EClass getConnectionReferenceSubjectClass(Object tested) {
        for (Map.Entry<EClass, EClass> c : connections.entrySet()) {
            if (checkType(c.getValue(), tested)) {
                return c.getKey();
            }
        }
        return null;
    }

    public static EStructuralFeature getReference(EClass parent, EClass ref) {
        for (EStructuralFeature esf : parent.getEAllStructuralFeatures()) {
            if (esf.getEType().getClassifierID() == ref.getClassifierID()) {
                return esf;
            }
        }
        return null;
    }

    public static EObject getReferencedObject(IFeatureProvider fp, EObject obj) {
        EClass referencedClass = getConnectionReferenceObjectClass(obj);
        if (null != referencedClass) {
            Object module = fp.getBusinessObjectForPictogramElement(fp.getDiagramTypeProvider().getDiagram());
            for (EObject o : filter(getAllBusinessObjects((EObject) module, null), referencedClass)) {
                if (checkType(MODEL_PACKAGE.getUses(), obj) && null != module && module instanceof EObject) {
                    if (((Grouping) o).getName().equals(((Uses) obj).getQName())) {
                        return o;
                    }
                }
                if (checkType(MODEL_PACKAGE.getReferenceNode(), obj)
                        && checkType(MODEL_PACKAGE.getNamedNode(), referencedClass)) {
                    if (null != ((NamedNode) o).getName()
                            && ((NamedNode) o).getName().equals(((ReferenceNode) obj).getReference())) {
                        return o;
                    }
                }
            }
        }
        return null;
    }

    public static List<EObject> getAllBusinessObjects(EObject obj, List<EObject> list) {
        if (null == list) {
            list = new ArrayList<EObject>();
        }
        list.add(obj);
        if (checkType(MODEL_PACKAGE.getContainingNode(), obj)) {
            for (EObject o : ((ContainingNode) obj).getChildren()) {
                getAllBusinessObjects(o, list);
            }
        }
        return list;
    }

    public static boolean checkType(EClass parent, Object tested) {
        return null != tested && tested instanceof EObject && checkType(parent, ((EObject) tested).eClass());
    }

    public static boolean checkType(EClass parent, EClass tested) {
        return parent.isSuperTypeOf(tested);
    }

    private static boolean hasCircles(ContainingNode parent, Node child) {
        Set<Node> checked = new HashSet<Node>();
        List<Node> descendants = new ArrayList<Node>();
        descendants.add(child);
        for (int i = 0; i < descendants.size(); i++) {
            Node n = descendants.get(i);
            if (n == parent) {
                return true;
            }
            if (!checked.contains(n) && n instanceof ContainingNode) {
                descendants.addAll(((ContainingNode) n).getChildren());
            }
            checked.add(n);
        }
        return false;
    }

    public static List<IPropertyDescriptor> getPropertyDescriptors(EClass c) {
        List<IPropertyDescriptor> result = new ArrayList<IPropertyDescriptor>();
        if (taggedNodeMap.containsKey(c)) {
            for (YangTag tag : taggedNodeMap.get(c)) {
                result.add(tag.getPropertyDescriptor());
            }
        }
        return result;
    }

    public static Tag getTag(YangTag id, TaggedNode node) {
        for (Tag tag : node.getTags()) {
            if (id.getName().equals(tag.getName())) {
                return tag;
            }
        }
        return null;
    }

    public static Object getValue(YangTag id, TaggedNode node) {
        Tag tag = getTag(id, node);
        if (null != tag) {
            return tag.getValue();
        }
        return null;
    }

    public static Object getValue(YangTag id, ASTNode node) {
        if (YangTag.DESCRIPTION == id) {
            return node.getDescription();
        }
        if (YangTag.REFERENCE == id) {
            return node.getReference();
        }
        if (YangTag.STATUS == id) {
            return node.getStatus();
        }
        if (node instanceof ASTCompositeNode) {
            for (ASTNode n : ((ASTCompositeNode) node).getChildren()) {
                if (n instanceof SimpleNode<?>) {
                    if (id.getName().equals(((SimpleNode<?>) n).getNodeName())) {
                        return ((SimpleNode<?>) n).getValue();
                    }
                }
            }
        }
        return null;
    }

    public static void setValue(YangTag id, TaggedNode node, Object value) {
        Tag tag = getTag(id, node);
        if (null != tag) {
            tag.setValue(value);
        } else {
            Tag t = ModelFactory.eINSTANCE.createTag();
            t.setName(id.getName());
            t.setValue(value);
            node.getTags().add(t);
        }
    }

    private static List<EClass> getPossibleChildren(EClass e) {
        return (null != compositeNodeMap.get(e) ? compositeNodeMap.get(e) : Collections.<EClass> emptyList());
    }

    public static Module exportModel(com.cisco.yangide.core.dom.Module module, Map<Node, ASTNode> relations) {
        return (Module) createEObject(module, null, relations);
    }

    private static EObject createEObject(ASTNode n, EObject parent, Map<Node, ASTNode> relations) {
        EClass cl = getEClass(n);
        if (null != cl) {
            EObject o = ModelFactory.eINSTANCE.create(cl);
            if (null != o) {
                setName(o, n);
                setTags(o, n);
                setAdditionalInfo(o, n);
                if (null != parent) {
                    setRelation(parent, o);
                }
                if (checkType(MODEL_PACKAGE.getNode(), o)) {
                    relations.put((Node) o, n);
                    if (checkType(MODEL_PACKAGE.getContainingNode(), o) || checkType(MODEL_PACKAGE.getTypedNode(), o)) {
                        if (n instanceof ASTCompositeNode) {
                            for (ASTNode c : ((ASTCompositeNode) n).getChildren()) {
                                createEObject(c, o, relations);
                            }
                        }
                    }
                }
                return o;
            }
        }
        return null;
    }

    private static void setAdditionalInfo(EObject o, ASTNode n) {
        if (checkType(MODEL_PACKAGE.getRpcIO(), o)) {
            ((RpcIO) o).setInput(n instanceof RpcInputNode);
        }
        if (checkType(MODEL_PACKAGE.getUses(), o)) {
            if (n instanceof UsesNode) {
                ((Uses) o).setQName(((UsesNode) n).getName());
            }
        }
        if (checkType(MODEL_PACKAGE.getImport(), o)) {
            if (n instanceof ModuleImport) {
                ((Import) o).setModule(((ModuleImport) n).getName());
                ((Import) o).setPrefix(((ModuleImport) n).getPrefix());
                ((Import) o).setRevisionDate(((ModuleImport) n).getRevision());
            }
        }
        if (checkType(MODEL_PACKAGE.getIdentity(), o)) {
            if (n instanceof IdentitySchemaNode && null != ((IdentitySchemaNode) n).getBase()) {
                ((Identity) o).setReference(((IdentitySchemaNode) n).getBase().getName());
            }
        }
    }

    private static void setTags(EObject o, ASTNode n) {
        List<YangTag> tags = taggedNodeMap.get(o.eClass());
        if (null != tags && checkType(YangModelUtil.MODEL_PACKAGE.getTaggedNode(), o)) {
            for (YangTag t : tags) {
                setValue(t, (TaggedNode) o, getValue(t, n));
            }
        }
    }

    private static void setName(EObject o, ASTNode n) {
        if (checkType(MODEL_PACKAGE.getNamedNode(), o)) {
            if (n instanceof ASTNamedNode) {
                ((NamedNode) o).setName(((ASTNamedNode) n).getName());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void setRelation(EObject parent, EObject o) {
        if (canContain(parent, o) && checkType(MODEL_PACKAGE.getContainingNode(), parent)) {
            add(parent, o, ((ContainingNode) parent).getChildren().size());
        }
        EStructuralFeature esf = getReference(parent.eClass(), o.eClass());
        if (null != esf) {
            if (1 != esf.getUpperBound()) {
                ((Collection<EObject>) parent.eGet(esf)).add(o);
            } else {
                parent.eSet(esf, o);
            }
        }
    }

    public static String getQName(EObject obj) {
        if (checkType(MODEL_PACKAGE.getUses(), obj)) {
            if (null != obj && null != ((Uses) obj).getGrouping() && null != ((Uses) obj).getGrouping().getName()) {
                return ((Uses) obj).getGrouping().getName();
            }
            if (null != obj && null != ((Uses) obj).getQName()) {
                return ((Uses) obj).getQName();
            }
        }
        return null;
    }

    public static String getQNamePresentation(EObject obj) {
        String result = getQName(obj);
        if (null != result) {
            return obj.eClass().getName().toLowerCase() + " " + result;
        }
        return null;
    }

    public static EClass getEClass(ASTNode obj) {
        Class<? extends ASTNode> c = obj.getClass();
        EClass ec = astNodes.get(c);
        if (null == ec) {
            for (Map.Entry<Class<? extends ASTNode>, EClass> entry : astNodes.entrySet()) {
                if (c.isAssignableFrom(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        if (null == ec && obj instanceof SimpleNamedNode) {
            if ("key".equals(((SimpleNamedNode) obj).getNodeName())) {
                return MODEL_PACKAGE.getListKey();
            }
        }
        return ec;
    }

    public static <T extends EObject> List<T> filter(List<T> list, EClass ec) {
        List<T> result = new ArrayList<T>();
        for (T n : list) {
            if (checkType(ec, n)) {
                result.add(n);
            }
        }
        return result;
    }
}
