/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.model.editor.sync;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.RewriteSessionEditProcessor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.dom.ASTCompositeNode;
import com.cisco.yangide.core.dom.ASTNamedNode;
import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.BaseReference;
import com.cisco.yangide.core.dom.IdentitySchemaNode;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.ModuleImport;
import com.cisco.yangide.core.dom.SimpleNode;
import com.cisco.yangide.core.dom.SubModule;
import com.cisco.yangide.core.parser.YangFormattingPreferences;
import com.cisco.yangide.core.parser.YangParserUtil;
import com.cisco.yangide.editor.editors.YangEditor;
import com.cisco.yangide.editor.editors.YangSourceViewer;
import com.cisco.yangide.ext.model.BelongsTo;
import com.cisco.yangide.ext.model.Import;
import com.cisco.yangide.ext.model.ModelPackage;
import com.cisco.yangide.ext.model.Node;
import com.cisco.yangide.ext.model.Tag;
import com.cisco.yangide.ext.model.editor.Activator;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;
import com.cisco.yangide.ext.model.editor.util.YangTag;
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
    private Map<EClass, SourceNodePropertyUpdater<? extends ASTNode>> propertyUpdaters;

    /**
     * @param modelSynchronizer
     */
    DiagramModelAdapter(ModelSynchronizer modelSynchronizer, YangEditor yangSourceEditor, Map<Node, ASTNode> mapping) {
        this.modelSynchronizer = modelSynchronizer;
        this.yangSourceEditor = yangSourceEditor;
        this.mapping = mapping;
        this.propertyUpdaters = new HashMap<>();
        // init property updaters
        this.propertyUpdaters.put(ModelPackage.Literals.NODE, new SourceNodePropertyUpdater<ASTNode>(this));
        this.propertyUpdaters.put(ModelPackage.Literals.MODULE, new ModulePropertyUpdater(this));
        this.propertyUpdaters.put(ModelPackage.Literals.SUBMODULE, new ModulePropertyUpdater(this));
        this.propertyUpdaters.put(ModelPackage.Literals.CONTAINER, new ContainerPropertyUpdater(this));
        this.propertyUpdaters.put(ModelPackage.Literals.LIST, new ListPropertyUpdater(this));
        this.propertyUpdaters.put(ModelPackage.Literals.LEAF_LIST, new ListPropertyUpdater(this));
        this.propertyUpdaters.put(ModelPackage.Literals.CHOICE, new ChoicePropertyUpdater(this));
        this.propertyUpdaters.put(ModelPackage.Literals.TYPEDEF, new TypedefPropertyUpdater(this));
        this.propertyUpdaters.put(ModelPackage.Literals.LEAF, new LeafPropertyUpdater(this));
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized void notifyChanged(Notification notification) {
        super.notifyChanged(notification);

        if (notification.getEventType() != Notification.REMOVING_ADAPTER) {
            if (this.modelSynchronizer.isNotificationEnabled()) {
                try {
                    this.modelSynchronizer.disableNotification();
                    if (Activator.getDefault().isDebugging()) {
                        System.out.println("from diag: " + notification);
                    }
                    switch (notification.getEventType()) {
                    case Notification.ADD:
                        ASTNode node = mapping.get(notification.getNotifier());
                        if (notification.getNewValue() instanceof Node) {
                            Node newValue = (Node) notification.getNewValue();
                            String content = null;
                            if (removedBlock.containsKey(newValue)) { // block moved from another
                                // location
                                if (Activator.getDefault().isDebugging()) {
                                    System.out.println("block moved");
                                }
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
                            if (notification.getFeature() == ModelPackage.Literals.NAMED_NODE__NAME
                                    || notification.getFeature() == ModelPackage.Literals.USES__QNAME) {
                                updateName((Node) notification.getNotifier(), (EAttribute) notification.getFeature(),
                                        notification.getNewValue());
                            } else if (notification.getFeature() == ModelPackage.Literals.REFERENCE_NODE__REFERENCE) {
                                updateIdentityReference((Node) notification.getNotifier(), notification.getNewValue());
                            } else if (notification.getFeature() == ModelPackage.Literals.SUBMODULE__BELONGS_TO) {
                                updateBelongsTo((Node) notification.getNotifier(), notification.getOldValue(),
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
                                if (propertyUpdaters.containsKey(parent.eClass())) {
                                    SourceNodePropertyUpdater<ASTNode> updater = (SourceNodePropertyUpdater<ASTNode>) propertyUpdaters
                                            .get(parent.eClass());
                                    updater.updateProperty(astNode, tag.getName(), notification.getNewValue(),
                                            astNode.getBodyStartPosition() + 1);
                                } else {
                                    SourceNodePropertyUpdater<ASTNode> updater = (SourceNodePropertyUpdater<ASTNode>) propertyUpdaters
                                            .get(ModelPackage.Literals.NODE);
                                    updater.updateProperty(astNode, tag.getName(), notification.getNewValue(),
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
        if (0 != position && parent.getChildren().size() > 0) {
            int size = parent.getChildren().size();
            insertPosition = position < 0 || position >= size ? parent.getChildren().get(size - 1).getEndPosition() + 2
                    : parent.getChildren().get(
                            parent.getChildren().contains(child) && position < oldPosition ? position - 1 : position)
                            .getEndPosition() + 2;
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
        if (0 != position && parent.getChildren().size() > 0) {
            int size = parent.getChildren().size();
            insertPosition = position < 0 || position >= size ? parent.getChildren().get(size - 1).getEndPosition() + 2
                    : parent.getChildren().get(position - 1).getEndPosition() + 2;
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

        if (!(astNode instanceof ASTNamedNode)) {
            throw new RuntimeException("Source block is not named element");
        }
        ASTNamedNode nnode = (ASTNamedNode) astNode;

        performEdit(new ReplaceEdit(nnode.getNameStartPosition(), nnode.getNameLength(), (String) newValue));
    }

    private void updateIdentityReference(Node node, Object newValue) {
        ASTNode astNode = mapping.get(node);
        if (astNode == null) {
            throw new RuntimeException("Cannot find references source block from diagram editor");
        }

        BaseReference base = ((IdentitySchemaNode) astNode).getBase();
        if (base == null && newValue != null) {
            performEdit(new InsertEdit(astNode.getBodyStartPosition() + 1, formatBase(astNode, (String) newValue)));
        } else if (base != null) {
            if (newValue != null && !((String) newValue).trim().isEmpty()) {
                performEdit(new ReplaceEdit(base.getNameStartPosition(), base.getNameLength(), (String) newValue));
            } else {
                performEdit(new DeleteEdit(base.getStartPosition(), base.getLength() + 1));
            }
        }
    }

    private void updateBelongsTo(Node node, Object oldValue, Object newValue) {
        ASTNode astNode = mapping.get(node);
        if (astNode == null) {
            throw new RuntimeException("Cannot find references source block from diagram editor");
        }

        com.cisco.yangide.core.dom.SubModule subModule = (com.cisco.yangide.core.dom.SubModule) astNode;
        SimpleNode<String> btNode = subModule.getParentModule();
        if (btNode != null) {
            performEdit(new ReplaceEdit(btNode.getStartPosition(), btNode.getLength() + 1,
                    formatBelongsTo((BelongsTo) newValue)));
        } else {
            performEdit(new InsertEdit(subModule.getBodyStartPosition() + 1,
                    System.lineSeparator() + formatBelongsTo((BelongsTo) newValue)));
        }
    }

    synchronized void performEdit(final TextEdit edit) {
        Display display = Display.getCurrent();
        if (display == null) {
            display = Display.getDefault();
        }
        display.syncExec(new Runnable() {
            @Override
            public void run() {
                try {
                    YangSourceViewer viewer = (YangSourceViewer) yangSourceEditor.getViewer();
                    MultiTextEdit mt = new MultiTextEdit();
                    mt.addChild(edit);
                    new RewriteSessionEditProcessor(viewer.getDocument(), mt, TextEdit.CREATE_UNDO).performEdits();
                    viewer.updateDocument();
                } catch (MalformedTreeException | BadLocationException e) {
                    Activator.log(e, e.getMessage());
                }
            }
        });
        char[] content = yangSourceEditor.getDocument().get().toCharArray();
        com.cisco.yangide.core.dom.Module module = YangParserUtil.parseYangFile(content);
        modelSynchronizer.updateFromSource(module, true);
        yangSourceEditor.reconcileModel();
    }

    public int getIndentLevel(ASTNode node) {
        int level = 0;
        ASTNode parent = node;
        while (parent != null) {
            parent = parent.getParent();
            level++;
        }
        return level;
    }

    private String formatBase(ASTNode node, String value) {
        return trimTrailingSpaces(
                RefactorUtil.formatCodeSnipped("\nbase " + empty2Quote(value) + ";", getIndentLevel(node)));
    }

    private String formatImport(Import newValue) {
        StringBuilder sb = new StringBuilder();
        sb.append("import ").append(newValue.getModule()).append(" {\n");
        sb.append("prefix ").append(newValue.getPrefix()).append(";\n");
        sb.append("revision-date \"").append(newValue.getRevisionDate()).append("\";\n");
        sb.append("}");
        return trimTrailingSpaces(RefactorUtil.formatCodeSnipped(sb.toString(), 1));
    }

    private String formatBelongsTo(BelongsTo belongsTo) {
        com.cisco.yangide.ext.model.Module parentModule = belongsTo.getOwnerModule();
        String prefix = (String) YangModelUtil.getValue(YangTag.PREFIX, parentModule);
        StringBuilder sb = new StringBuilder();
        sb.append("belongs-to ").append(parentModule.getName()).append(" {\n");
        sb.append("prefix ").append(prefix).append(";\n");
        sb.append("}");
        return trimTrailingSpaces(RefactorUtil.formatCodeSnipped(sb.toString(), 0));
    }

    private String trimTrailingSpaces(String str) {
        int len = str.length();
        char[] val = str.toCharArray();

        while ((len > 0) && (val[len - 1] <= ' ')) {
            len--;
        }
        return (len < str.length()) ? str.substring(0, len) : str;
    }

    private String empty2Quote(String str) {
        return str == null || str.trim().isEmpty() ? ("\"" + str + "\"") : str;
    }
}