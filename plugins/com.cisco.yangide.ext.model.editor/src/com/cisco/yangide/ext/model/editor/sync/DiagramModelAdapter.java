/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.model.editor.sync;

import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.PerformChangeOperation;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.dom.ASTCompositeNode;
import com.cisco.yangide.core.dom.ASTNamedNode;
import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.ModuleImport;
import com.cisco.yangide.core.dom.SimpleNode;
import com.cisco.yangide.core.dom.SubModule;
import com.cisco.yangide.core.parser.YangFormattingPreferences;
import com.cisco.yangide.core.parser.YangParserUtil;
import com.cisco.yangide.editor.YangEditorPlugin;
import com.cisco.yangide.editor.editors.YangEditor;
import com.cisco.yangide.ext.model.Import;
import com.cisco.yangide.ext.model.ModelPackage;
import com.cisco.yangide.ext.model.Node;
import com.cisco.yangide.ext.model.Tag;
import com.cisco.yangide.ext.model.editor.Activator;
import com.cisco.yangide.ext.refactoring.RefactorUtil;

/**
 * @author Konstantin Zaitsev
 * @date Aug 13, 2014
 */
final class DiagramModelAdapter extends EContentAdapter {
    private final ModelSynchronizer modelSynchronizer;
    private Map<Node, String> removedBlock = new WeakHashMap<>();
    private YangEditor yangSourceEditor;
    private Map<Node, ASTNode> mapping;

    /**
     * @param modelSynchronizer
     */
    DiagramModelAdapter(ModelSynchronizer modelSynchronizer, YangEditor yangSourceEditor, Map<Node, ASTNode> mapping) {
        this.modelSynchronizer = modelSynchronizer;
        this.yangSourceEditor = yangSourceEditor;
        this.mapping = mapping;
    }

    @Override
    public synchronized void notifyChanged(Notification notification) {
        super.notifyChanged(notification);
        if (notification.getEventType() != Notification.REMOVING_ADAPTER) {
            if (this.modelSynchronizer.isNotificationEnabled()) {
                try {
                    this.modelSynchronizer.disableNotification();
                    System.out.println("from diag: " + notification);
                    switch (notification.getEventType()) {
                    case Notification.ADD:
                        ASTNode node = mapping.get(notification.getNotifier());
                        if (notification.getNewValue() instanceof Node) {
                            Node newValue = (Node) notification.getNewValue();
                            String content = null;
                            if (removedBlock.containsKey(newValue)) { // block moved from another
                                // location
                                System.out.println("block moved");
                                content = removedBlock.remove(newValue);
                                add(node, content, notification.getPosition());
                            }
                            if (newValue.eClass() == ModelPackage.Literals.IMPORT) {
                                addImport((Module) node, newValue);
                            }
                            break;
                        } else if (notification.getNewValue() instanceof Tag) {
                            if (((Tag) notification.getNewValue()).getValue() != null) {
                                Activator.logError("tag added with value: " + notification);
                            }
                        } else {
                            Activator.logError("unknown notification : " + notification);
                        }
                    case Notification.SET:
                        if (notification.getFeature() != ModelPackage.Literals.NODE__PARENT) {
                            // skip notification if value not changed
                            if (notification.getNewValue() != null && notification.getOldValue() != null
                                    && notification.getOldValue().equals(notification.getNewValue())) {
                                break;
                            }

                            if (notification.getFeature() == ModelPackage.Literals.NAMED_NODE__NAME) {
                                updateName((Node) notification.getNotifier(), (EAttribute) notification.getFeature(),
                                        notification.getNewValue());
                            }

                            if (notification.getNotifier() instanceof Tag) {
                                Tag tag = (Tag) notification.getNotifier();
                                Node parent = (Node) tag.eContainer();
                                ASTNode astNode = mapping.get(parent);
                                if (astNode == null) {
                                    throw new RuntimeException(
                                            "Cannot find references source block from diagram editor");
                                }
                                if (parent.eClass() == ModelPackage.Literals.MODULE) {
                                    updateModuleProperty(astNode, tag.getName(), notification.getNewValue());
                                } else if (parent.eClass() == ModelPackage.Literals.REVISION) {
                                    updateRevisionProperty(astNode, tag.getName(), notification.getNewValue());
                                } else {
                                    updateProperty(astNode, tag.getName(), notification.getNewValue(),
                                            astNode.getBodyStartPosition() + 1);
                                }
                            }
                        }

                        break;
                    case Notification.REMOVE:
                        if (notification.getOldValue() != null && notification.getOldValue() instanceof Node
                        && mapping.containsKey(notification.getOldValue())) {
                            delete((Node) notification.getOldValue());
                        }
                        break;
                    case Notification.MOVE:
                        if (notification.getFeature() == ModelPackage.Literals.CONTAINING_NODE__CHILDREN) {
                            move((Node) notification.getNotifier(), (Node) notification.getNewValue(),
                                    (Integer) notification.getOldValue(), notification.getPosition());
                        }
                    default:
                        break;
                    }
                } finally {
                    this.modelSynchronizer.enableNotification();
                }
            }
        }
    }

    private void addImport(Module module, Node newValue) {
        int position = 0;
        if (module.getImports().isEmpty()) {
            if (module.getPrefix() != null) {
                position = module.getPrefix().getEndPosition() + 1;
            } else {
                position = module.getBodyStartPosition() + 1;
            }
        } else {
            for (ASTNode astNode : module.getChildren()) {
                if (astNode instanceof ModuleImport) {
                    position = astNode.getEndPosition() + 1;
                }
            }
        }

        performEdit(new InsertEdit(position, System.lineSeparator() + formatImport((Import) newValue)));
    }

    /**
     * @param notifier
     * @param newValue
     * @param oldIntValue
     * @param position
     */
    private void move(Node notifier, Node newValue, int oldPosition, int position) {
        ASTNode node = mapping.get(notifier);
        ASTNode child = mapping.get(newValue);
        if (!(node instanceof ASTCompositeNode)) {
            throw new RuntimeException("Parent node should be composite");
        }
        ASTCompositeNode parent = (ASTCompositeNode) node;

        int insertPosition = parent.getBodyStartPosition() + 2;
        if (parent.getChildren().size() > 0) {
            int size = parent.getChildren().size();
            insertPosition = position < 0 || position >= size ? parent.getChildren().get(size - 1).getEndPosition() + 2
                    : parent.getChildren().get(position).getEndPosition() + 2;
        }

        try {
            String content = yangSourceEditor.getDocument().get(child.getStartPosition(), child.getLength() + 1);

            TextEdit composite = new MultiTextEdit();
            composite.addChild(new DeleteEdit(child.getStartPosition(), child.getLength() + 1));
            composite.addChild(new InsertEdit(insertPosition, content));
            performEdit(composite);
        } catch (BadLocationException e) {
            YangCorePlugin.log(e);
        }

    }

    public void add(ASTNode node, String content, int position) {

        if (!(node instanceof ASTCompositeNode)) {
            throw new RuntimeException("Parent node should be composite");
        }
        ASTCompositeNode parent = (ASTCompositeNode) node;

        int insertPosition = parent.getBodyStartPosition() + 2;
        if (parent.getChildren().size() > 0) {
            int size = parent.getChildren().size();
            insertPosition = position < 0 || position >= size ? parent.getChildren().get(size - 1).getEndPosition() + 2
                    : parent.getChildren().get(position).getEndPosition() + 2;
        }

        if (node instanceof Module || node instanceof SubModule) {
            insertPosition = parent.getBodyEndPosition() - 1;
        }
        String formattedContent = YangParserUtil.formatYangSource(new YangFormattingPreferences(),
                content.toCharArray(), getIndentLevel(node), System.getProperty("line.separator"));
        performEdit(new InsertEdit(insertPosition, formattedContent));
    }

    void delete(Node node) {
        ASTNode astNode = mapping.get(node);
        try {
            removedBlock.put(node,
                    yangSourceEditor.getDocument().get(astNode.getStartPosition(), astNode.getLength() + 1));
        } catch (BadLocationException e) {
            // ignore exception
        }
        performEdit(new DeleteEdit(astNode.getStartPosition(), astNode.getLength() + 1));
    }

    void updateName(Node node, EAttribute feature, Object newValue) {
        ASTNode astNode = mapping.get(node);
        if (astNode == null) {
            throw new RuntimeException("Cannot find references source block from diagram editor");
        }

        if (feature == ModelPackage.Literals.NAMED_NODE__NAME) {
            if (!(astNode instanceof ASTNamedNode)) {
                throw new RuntimeException("Source block is not named element");
            }
            ASTNamedNode nnode = (ASTNamedNode) astNode;

            performEdit(new ReplaceEdit(nnode.getNameStartPosition(), nnode.getNameLength(), (String) newValue));
        }
    }

    private void updateModuleProperty(ASTNode node, String name, Object newValue) {
        Module module = (Module) node;
        SimpleNode<String> prop = null;
        boolean handle = false;

        switch (name) {
        case "namespace":
            prop = module.getNamespaceNode();
            handle = true;
            break;
        case "prefix":
            prop = module.getPrefix();
            handle = true;
            break;
        case "yang-version":
            prop = module.getYangVersion();
            handle = true;
            break;
        }

        if (handle) {
            if (prop == null) { // insert new property
                int pos = node.getBodyStartPosition() + 1;
                performEdit(new InsertEdit(pos, System.lineSeparator() + formatTag(node, name, (String) newValue)));
            } else { // update property
                performEdit(new ReplaceEdit(prop.getStartPosition(), prop.getLength() + 1, formatTag(node, name,
                        (String) newValue).trim()));
            }
        }
        ASTNode beforeRevisionNode = getAboveChildNode(module, module.getRevisionNode());
        int beforeRevision = beforeRevisionNode != null ? beforeRevisionNode.getEndPosition() + 1 : node
                .getBodyStartPosition() + 1;

        updateProperty(node, name, newValue, beforeRevision);
    }

    private void updateRevisionProperty(ASTNode node, String name, Object newValue) {
        updateProperty(node, name, newValue, node.getBodyStartPosition() + 1);
    }

    private void updateProperty(ASTNode node, String name, Object newValue, int startPosition) {
        SimpleNode<String> prop = null;

        switch (name) {
        case "description":
            prop = node.getDescriptionNode();
            break;
        case "reference":
            prop = node.getReferenceNode();
            break;
        case "status":
            prop = node.getStatusNode();
            break;
        case "organization":
            prop = ((Module) node).getOrganization();
            break;
        case "contact":
            prop = ((Module) node).getContact();
            break;
        default:
            Activator.logError("unknoun tag: " + name);
            return;
        }

        if (prop == null) { // insert new property
            int pos = startPosition;
            if (name.equals("status")) {
                if (node.getReferenceNode() != null) {
                    pos = node.getReferenceNode().getEndPosition() + 1;
                } else if (node.getDescriptionNode() != null) {
                    pos = node.getDescriptionNode().getEndPosition() + 1;
                }
            } else if (name.equals("reference") && node.getDescriptionNode() != null) {
                pos = node.getDescriptionNode().getEndPosition() + 1;
            }
            performEdit(new InsertEdit(pos, System.lineSeparator() + formatTag(node, name, (String) newValue)));
        } else if (newValue == null || ((String) newValue).isEmpty()) { // delete property
            if (prop != null) {
                performEdit(new DeleteEdit(prop.getStartPosition(), prop.getLength() + 1));
            }
        } else { // update property
            if (prop != null) {
                performEdit(new ReplaceEdit(prop.getStartPosition(), prop.getLength() + 1, formatTag(node, name,
                        (String) newValue).trim()));
            }
        }
    }

    private void performEdit(TextEdit edit) {
        DocumentChange change = new DocumentChange("edit", yangSourceEditor.getDocument());
        change.setEdit(edit);
        change.initializeValidationData(new NullProgressMonitor());
        PerformChangeOperation op = new PerformChangeOperation(change);
        try {
            ResourcesPlugin.getWorkspace().run(op, null);
        } catch (CoreException e) {
            YangEditorPlugin.log(e);
        }

        char[] content = yangSourceEditor.getDocument().get().toCharArray();
        com.cisco.yangide.core.dom.Module module = YangParserUtil.parseYangFile(content);
        modelSynchronizer.updateFromSource(module, true);
    }

    private int getIndentLevel(ASTNode node) {
        int level = 0;
        ASTNode parent = node;
        while (parent != null) {
            parent = parent.getParent();
            level++;
        }
        return level;
    }

    private String formatTag(ASTNode node, String name, String value) {
        return trimTrailingSpaces(RefactorUtil.formatCodeSnipped(name + " \"" + value + "\";", getIndentLevel(node)));
    }

    private String formatImport(Import newValue) {
        StringBuilder sb = new StringBuilder();
        sb.append("import ").append(newValue.getModule()).append(" {\n");
        sb.append("prefix ").append(newValue.getPrefix()).append(";\n");
        sb.append("revision-date \"").append(newValue.getRevisionDate()).append("\";\n");
        sb.append("}");
        return trimTrailingSpaces(RefactorUtil.formatCodeSnipped(sb.toString(), 1));
    }

    private ASTNode getAboveChildNode(ASTCompositeNode parent, ASTNode node) {
        ASTNode result = null;
        for (ASTNode child : parent.getChildren()) {
            if (child == node) {
                return result;
            }
            result = child;
        }
        return null;
    }

    private String trimTrailingSpaces(String str) {
        int len = str.length();
        char[] val = str.toCharArray();

        while ((len > 0) && (val[len - 1] <= ' ')) {
            len--;
        }
        return (len < str.length()) ? str.substring(0, len) : str;
    }
}