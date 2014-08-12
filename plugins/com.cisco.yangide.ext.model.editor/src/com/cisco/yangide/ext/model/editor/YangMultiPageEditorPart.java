/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.model.editor;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.merge.BatchMerger;
import org.eclipse.emf.compare.merge.IBatchMerger;
import org.eclipse.emf.compare.merge.IMerger;
import org.eclipse.emf.compare.merge.ReferenceChangeMerger;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.TextEditProcessor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.core.dom.ASTCompositeNode;
import com.cisco.yangide.core.dom.ASTNamedNode;
import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.parser.YangFormattingPreferences;
import com.cisco.yangide.core.parser.YangParserUtil;
import com.cisco.yangide.editor.YangEditorPlugin;
import com.cisco.yangide.editor.editors.YangEditor;
import com.cisco.yangide.ext.model.ModelPackage;
import com.cisco.yangide.ext.model.Module;
import com.cisco.yangide.ext.model.Node;
import com.cisco.yangide.ext.model.editor.editors.IModelChangeHandler;
import com.cisco.yangide.ext.model.editor.editors.YangDiagramEditor;
import com.cisco.yangide.ext.model.editor.editors.YangDiagramEditorInput;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

/**
 * @author Konstantin Zaitsev
 * @date Aug 7, 2014
 */
public class YangMultiPageEditorPart extends MultiPageEditorPart {

    private YangEditor yangSourceEditor;
    private YangDiagramEditor yangDiagramEditor;
    private Module diagModule;
    private com.cisco.yangide.core.dom.Module astModule;

    /** Diag node to AST node mapping. */
    private Map<Node, ASTNode> mapping = new WeakHashMap<>();

    private boolean notification;
    private Map<Node, String> removedBlock = new WeakHashMap<>();
    private IModelChangeHandler modelChangeHandler;

    private EContentAdapter diagModelMergeAdapter = new EContentAdapter() {
        @Override
        public void notifyChanged(Notification notification) {
            if (notification.getEventType() != Notification.REMOVING_ADAPTER
                    && notification.getFeature() != ModelPackage.Literals.NODE__PARENT) {
                switch (notification.getEventType()) {
                case Notification.ADD:
                    if (notification.getFeature() == ModelPackage.Literals.CONTAINING_NODE__CHILDREN) {
                        Node node = (Node) notification.getNewValue();
                        Node parent = (Node) notification.getNotifier();
                        modelChangeHandler.nodeAdded(parent, node, notification.getPosition());
                    }
                    break;
                case Notification.REMOVE:
                    if (notification.getOldValue() != null) {
                        Node node = (Node) notification.getOldValue();
                        modelChangeHandler.nodeRemoved(node);
                    }
                    break;
                case Notification.SET:
                    if (notification.getNotifier() instanceof Node) {
                        Node node = (Node) notification.getNotifier();
                        modelChangeHandler.nodeChanged(node, (EAttribute) notification.getFeature(),
                                notification.getNewValue());
                    }
                    break;
                default:
                    break;
                }
            }
            super.notifyChanged(notification);
        };
    };

    @Override
    protected void createPages() {
        createSourcePage();
        createDiagramPage();
        enableNotification();
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        yangSourceEditor.doSave(monitor);
    }

    @Override
    public void doSaveAs() {
        yangSourceEditor.doSaveAs();
    }

    @Override
    public boolean isSaveAsAllowed() {
        return yangSourceEditor.isSaveAsAllowed();
    }

    @Override
    protected void setInput(IEditorInput input) {
        super.setInput(input);
    }

    @Override
    public boolean isDirty() {
        return yangSourceEditor.isDirty();
    }

    private void createDiagramPage() {
        yangDiagramEditor = new YangDiagramEditor();
        modelChangeHandler = yangDiagramEditor.getModelChangeHandler();
        try {
            diagModule = YangModelUtil.exportModel(astModule, mapping);
            YangDiagramEditorInput input = new YangDiagramEditorInput(URI.createURI("tmp:/local"),
                    "com.cisco.yangide.ext.model.editor.editorDiagramTypeProvider", diagModule);
            addPage(1, yangDiagramEditor, input);
            setPageText(1, "Diagram");
            diagModule.eAdapters().add(new EContentAdapter() {
                @Override
                public void notifyChanged(Notification notification) {
                    super.notifyChanged(notification);
                    if (notification.getEventType() != Notification.REMOVING_ADAPTER) {
                        if (isNotificationEnabled()) {
                            try {
                                disableNotification();
                                switch (notification.getEventType()) {
                                case Notification.ADD:
                                    ASTNode node = mapping.get(notification.getNotifier());
                                    addFromDiagram(node, (Node) notification.getNewValue(), notification.getPosition());
                                    break;
                                case Notification.SET:
                                    if (notification.getFeature() != ModelPackage.Literals.NODE__PARENT
                                    && notification.getNotifier() instanceof Node) {
                                        updateFromDiagram((Node) notification.getNotifier(),
                                                (EAttribute) notification.getFeature(), notification.getNewValue());
                                    }
                                    break;
                                case Notification.REMOVE:
                                    if (notification.getOldValue() != null
                                    && notification.getOldValue() instanceof Node
                                    && mapping.get(notification.getOldValue()) != null) {
                                        deleteFromDiagram((Node) notification.getOldValue());
                                    }
                                    break;
                                default:
                                    break;
                                }
                            } finally {
                                enableNotification();
                            }
                        }
                    }
                }
            });
        } catch (PartInitException e) {
            YangEditorPlugin.log(e);
        }
    }

    private void createSourcePage() {
        yangSourceEditor = new YangEditor();
        try {
            addPage(0, yangSourceEditor, getEditorInput());
            setPageText(0, "Source");
            astModule = YangParserUtil.parseYangFile(yangSourceEditor.getDocument().get().toCharArray());
            yangSourceEditor.getDocument().addDocumentListener(new IDocumentListener() {

                @Override
                public void documentChanged(DocumentEvent event) {
                    checkSourceForUpdate();
                }

                @Override
                public void documentAboutToBeChanged(DocumentEvent event) {
                }
            });
        } catch (PartInitException e) {
            YangEditorPlugin.log(e);
        }
        setPartName(yangSourceEditor.getPartName());
    }

    @Override
    protected void pageChange(int newPageIndex) {
        if (newPageIndex == 1) {
            checkSourceForUpdate();
        }
        super.pageChange(newPageIndex);
    }

    private void checkSourceForUpdate() {
        if (isNotificationEnabled()) {
            com.cisco.yangide.core.dom.Module module = null;
            try {
                module = yangSourceEditor.getModule();
            } catch (YangModelException e) {
                // ignore exception
            }

            if (module == null || module.getFlags() == com.cisco.yangide.core.dom.Module.MALFORMED
                    || module == astModule) {
                return;
            }
            try {
                disableNotification();
                updateFromSource(module);
            } finally {
                enableNotification();
            }
        }
    }

    private void updateFromSource(com.cisco.yangide.core.dom.Module module) {
        final Map<Node, ASTNode> newMapping = new WeakHashMap<>();

        astModule = module;
        Module moduleNew = YangModelUtil.exportModel(module, newMapping);
        IComparisonScope scope = new DefaultComparisonScope(diagModule, moduleNew, diagModule);
        Comparison comparison = EMFCompare.builder().build().compare(scope);

        updateMappings(newMapping, comparison.getMatches());
        List<Diff> differences = comparison.getDifferences();
        IMerger.Registry mergerRegistry = IMerger.RegistryImpl.createStandaloneInstance();
        ReferenceChangeMerger refMerger = new ReferenceChangeMerger() {
            @Override
            protected EObject createCopy(EObject referenceObject) {
                EObject copy = super.createCopy(referenceObject);
                if (newMapping.containsKey(referenceObject) && copy instanceof Node) {
                    mapping.put((Node) copy, newMapping.remove(referenceObject));
                }
                return copy;
            }
        };
        refMerger.setRanking(20); // highest rank then default
        mergerRegistry.add(refMerger);
        IBatchMerger merger = new BatchMerger(mergerRegistry);
        diagModule.eAdapters().add(diagModelMergeAdapter);
        merger.copyAllRightToLeft(differences, null);
        diagModule.eAdapters().remove(diagModelMergeAdapter);
    }

    private void updateMappings(Map<Node, ASTNode> newMapping, Iterable<Match> matches) {
        for (Match match : matches) {
            if (newMapping.containsKey(match.getRight())) {
                mapping.put((Node) match.getLeft(), newMapping.remove(match.getRight()));
            }
            updateMappings(newMapping, match.getAllSubmatches());
        }
    }

    private void updateFromDiagram(Node node, EAttribute feature, Object newValue) {
        ASTNode astNode = mapping.get(node);
        if (astNode == null) {
            throw new RuntimeException("Cannot find references source block from diagram editor");
        }

        try {
            if (feature == ModelPackage.Literals.NAMED_NODE__NAME) {
                if (!(astNode instanceof ASTNamedNode)) {
                    throw new RuntimeException("Source block is not named element");
                }
                ASTNamedNode nnode = (ASTNamedNode) astNode;
                MultiTextEdit edit = new MultiTextEdit();
                edit.addChild(new ReplaceEdit(nnode.getNameStartPosition(), nnode.getNameLength(), (String) newValue));
                TextEditProcessor processor = new TextEditProcessor(yangSourceEditor.getDocument(), edit,
                        TextEdit.UPDATE_REGIONS);
                processor.performEdits();
            }
        } catch (MalformedTreeException | BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void addFromDiagram(ASTNode node, Node newValue, int position) {

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
            String content = null;
            if (removedBlock.containsKey(newValue)) { // block moved from another location
                content = removedBlock.remove(newValue);
            } else {
                content = newValue.toYangString();
            }
            String formattedContent = YangParserUtil.formatYangSource(new YangFormattingPreferences(),
                    content.toCharArray(), getIndentLevel(node), System.getProperty("line.separator"));
            MultiTextEdit edit = new MultiTextEdit();
            edit.addChild(new InsertEdit(insertPosition, formattedContent));
            TextEditProcessor processor = new TextEditProcessor(yangSourceEditor.getDocument(), edit,
                    TextEdit.UPDATE_REGIONS);
            processor.performEdits();
        } catch (MalformedTreeException | BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void deleteFromDiagram(Node node) {
        try {
            ASTNode astNode = mapping.get(node);
            removedBlock.put(node,
                    yangSourceEditor.getDocument().get(astNode.getStartPosition(), astNode.getLength() + 1));

            MultiTextEdit edit = new MultiTextEdit();
            edit.addChild(new DeleteEdit(astNode.getStartPosition(), astNode.getLength() + 1));

            TextEditProcessor processor = new TextEditProcessor(yangSourceEditor.getDocument(), edit,
                    TextEdit.UPDATE_REGIONS);
            processor.performEdits();
        } catch (MalformedTreeException | BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void disableNotification() {
        notification = false;
    }

    private void enableNotification() {
        notification = true;
    }

    private boolean isNotificationEnabled() {
        return notification;
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
