/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.model.editor.sync;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.jdt.internal.ui.actions.WorkbenchRunnableAdapter;
import org.eclipse.jdt.internal.ui.util.BusyIndicatorRunnableContext;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.PerformChangeOperation;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.PlatformUI;

import com.cisco.yangide.core.dom.ASTCompositeNode;
import com.cisco.yangide.core.dom.ASTNamedNode;
import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.parser.YangFormattingPreferences;
import com.cisco.yangide.core.parser.YangParserUtil;
import com.cisco.yangide.editor.editors.YangEditor;
import com.cisco.yangide.editor.editors.YangSourceViewer;
import com.cisco.yangide.ext.model.ModelPackage;
import com.cisco.yangide.ext.model.Node;

/**
 * @author Konstantin Zaitsev
 * @date Aug 13, 2014
 */
@SuppressWarnings("restriction")
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
    public void notifyChanged(Notification notification) {
        super.notifyChanged(notification);
        if (notification.getEventType() != Notification.REMOVING_ADAPTER) {
            if (this.modelSynchronizer.isNotificationEnabled()) {
                try {
                    this.modelSynchronizer.disableNotification();
                    System.out.println("from diag");
                    switch (notification.getEventType()) {
                    case Notification.ADD:
                        ASTNode node = mapping.get(notification.getNotifier());
                        Node newValue = (Node) notification.getNewValue();
                        String content = null;
                        if (removedBlock.containsKey(newValue)) { // block moved from another
                                                                  // location
                            content = removedBlock.remove(newValue);
                        } else {
                            content = newValue.toYangString();
                        }
                        addFromDiagram(node, content, notification.getPosition());
                        break;
                    case Notification.SET:
                        if (notification.getFeature() != ModelPackage.Literals.NODE__PARENT
                        && notification.getNotifier() instanceof Node) {
                            updateFromDiagram((Node) notification.getNotifier(),
                                    (EAttribute) notification.getFeature(), notification.getNewValue());
                        }
                        break;
                    case Notification.REMOVE:
                        if (notification.getOldValue() != null && notification.getOldValue() instanceof Node
                        && mapping.containsKey(notification.getOldValue())) {
                            deleteFromDiagram((Node) notification.getOldValue());
                        }
                        break;
                    default:
                        break;
                    }
                    this.modelSynchronizer.updateFromSource(
                            YangParserUtil.parseYangFile(yangSourceEditor.getDocument().get().toCharArray()), false);
                } finally {
                    this.modelSynchronizer.enableNotification();
                }
            }
        }
    }

    public void addFromDiagram(ASTNode node, String content, int position) {

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

        String formattedContent = YangParserUtil.formatYangSource(new YangFormattingPreferences(),
                content.toCharArray(), getIndentLevel(node), System.getProperty("line.separator"));
        performEdit(new InsertEdit(insertPosition, formattedContent));
    }

    void deleteFromDiagram(Node node) {
        ASTNode astNode = mapping.get(node);
        try {
            removedBlock.put(node,
                    yangSourceEditor.getDocument().get(astNode.getStartPosition(), astNode.getLength() + 1));
        } catch (BadLocationException e) {
            // ignore exception
        }
        performEdit(new DeleteEdit(astNode.getStartPosition(), astNode.getLength() + 1));
    }

    void updateFromDiagram(Node node, EAttribute feature, Object newValue) {
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

    private void performEdit(TextEdit edit) {
        DocumentChange change = new DocumentChange("edit", yangSourceEditor.getDocument());
        change.setEdit(edit);
        change.initializeValidationData(new NullProgressMonitor());
        PerformChangeOperation op = new PerformChangeOperation(change);
        WorkbenchRunnableAdapter adapter = new WorkbenchRunnableAdapter(op);
        try {
            PlatformUI.getWorkbench().getProgressService()
            .runInUI(new BusyIndicatorRunnableContext(), adapter, adapter.getSchedulingRule());
        } catch (InvocationTargetException | InterruptedException e) {
            e.printStackTrace();
        }
        ((YangSourceViewer) yangSourceEditor.getViewer()).resetVisibleRegion();
        ((YangSourceViewer) yangSourceEditor.getViewer()).invalidateTextPresentation();
        yangSourceEditor.reconcile();
        yangSourceEditor.reconcileModel();
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
}