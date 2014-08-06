/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.ext.refactoring.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IEditingSupport;
import org.eclipse.jface.text.IEditingSupportRegistry;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewerExtension6;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.IUndoManagerExtension;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.jface.text.link.LinkedModeUI.IExitPolicy;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.core.dom.ASTNamedNode;
import com.cisco.yangide.editor.editors.YangEditor;
import com.cisco.yangide.ext.refactoring.YangRefactoringPlugin;
import com.cisco.yangide.ext.refactoring.actions.RenameSupport;

/**
 * @author Konstantin Zaitsev
 * @date Aug 4, 2014
 */
public class RenameLinkedMode {

    private class FocusEditingSupport implements IEditingSupport {
        @Override
        public boolean ownsFocusShell() {
            if (fInfoPopup == null) {
                return false;
            }
            if (fInfoPopup.ownsFocusShell()) {
                return true;
            }

            Shell editorShell = editor.getSite().getShell();
            Shell activeShell = editorShell.getDisplay().getActiveShell();
            if (editorShell == activeShell) {
                return true;
            }
            return false;
        }

        @Override
        public boolean isOriginator(DocumentEvent event, IRegion subjectRegion) {
            return false; // leave on external modification outside positions
        }
    }

    private class EditorSynchronizer implements ILinkedModeListener {
        @Override
        public void left(LinkedModeModel model, int flags) {
            linkedModeLeft();
            if ((flags & ILinkedModeListener.UPDATE_CARET) != 0) {
                doRename(fShowPreview);
            }
        }

        @Override
        public void resume(LinkedModeModel model, int flags) {
        }

        @Override
        public void suspend(LinkedModeModel model) {
        }
    }

    private class ExitPolicy implements IExitPolicy {
        private IDocument document;

        public ExitPolicy(IDocument document) {
            this.document = document;
        }

        @Override
        public ExitFlags doExit(LinkedModeModel model, VerifyEvent event, int offset, int length) {
            fShowPreview = (event.stateMask & SWT.CTRL) != 0
                    && (event.character == SWT.CR || event.character == SWT.LF);
            if (length == 0 && (event.character == SWT.BS || event.character == SWT.DEL)) {
                LinkedPosition position = model.findPosition(new LinkedPosition(document, offset, 0,
                        LinkedPositionGroup.NO_STOP));
                if (position != null) {
                    if (event.character == SWT.BS) {
                        if (offset - 1 < position.getOffset()) {
                            // skip backspace at beginning of linked position
                            event.doit = false;
                        }
                    } else /* event.character == SWT.DEL */{
                        if (offset + 1 > position.getOffset() + position.getLength()) {
                            // skip delete at end of linked position
                            event.doit = false;
                        }
                    }
                }
            }

            return null; // don't change behavior
        }
    }

    private static RenameLinkedMode fgActiveLinkedMode;

    private final YangEditor editor;
    /** Original definition node */
    private final ASTNamedNode originalNode;

    private RenameInformationPopup fInfoPopup;

    private Point fOriginalSelection;
    private String fOriginalName;
    /** Selected in editor node. */
    private ASTNamedNode selectedNode;

    private LinkedPosition fNamePosition;
    private LinkedModeModel fLinkedModeModel;
    private LinkedPositionGroup fLinkedPositionGroup;
    private final FocusEditingSupport fFocusEditingSupport;
    private boolean fShowPreview;
    private IUndoableOperation fStartingUndoOperation;

    private IFile originalFile;

    public RenameLinkedMode(ASTNamedNode originalNode, IFile originalFile, ASTNamedNode selectedNode, YangEditor editor) {
        this.editor = editor;
        this.originalFile = originalFile;
        this.originalNode = originalNode;
        this.selectedNode = selectedNode;
        fFocusEditingSupport = new FocusEditingSupport();
    }

    public static RenameLinkedMode getActiveLinkedMode() {
        if (fgActiveLinkedMode != null) {
            ISourceViewer viewer = fgActiveLinkedMode.editor.getViewer();
            if (viewer != null) {
                StyledText textWidget = viewer.getTextWidget();
                if (textWidget != null && !textWidget.isDisposed()) {
                    return fgActiveLinkedMode;
                }
            }
            // make sure we don't hold onto the active linked mode if anything went wrong with
            // canceling:
            fgActiveLinkedMode = null;
        }
        return null;
    }

    public void start() {
        if (getActiveLinkedMode() != null) {
            // for safety; should already be handled in RenameJavaElementAction
            fgActiveLinkedMode.startFullDialog();
            return;
        }

        ISourceViewer viewer = editor.getViewer();
        IDocument document = viewer.getDocument();
        fOriginalSelection = viewer.getSelectedRange();
        int offset = fOriginalSelection.x;

        try {
            if (viewer instanceof ITextViewerExtension6) {
                IUndoManager undoManager = ((ITextViewerExtension6) viewer).getUndoManager();
                if (undoManager instanceof IUndoManagerExtension) {
                    IUndoManagerExtension undoManagerExtension = (IUndoManagerExtension) undoManager;
                    IUndoContext undoContext = undoManagerExtension.getUndoContext();
                    IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
                    fStartingUndoOperation = operationHistory.getUndoOperation(undoContext);
                }
            }

            fOriginalName = selectedNode.getName();
            if (fOriginalName.indexOf(':') > 0) {
                fOriginalName = fOriginalName.substring(fOriginalName.indexOf(':'), fOriginalName.length());
            }
            final int pos = selectedNode.getNameStartPosition();
            fLinkedPositionGroup = new LinkedPositionGroup();

            ASTNamedNode[] sameNodes = RenameSupport.findLocalReferences(editor.getModule(), originalNode);

            Arrays.sort(sameNodes, new Comparator<ASTNamedNode>() {
                @Override
                public int compare(ASTNamedNode o1, ASTNamedNode o2) {
                    return rank(o1) - rank(o2);
                }

                /**
                 * Returns the absolute rank of an <code>ASTNode</code>. Nodes preceding
                 * <code>pos</code> are ranked last.
                 *
                 * @param node the node to compute the rank for
                 * @return the rank of the node with respect to the invocation offset
                 */
                private int rank(ASTNamedNode node) {
                    int relativeRank = node.getNameStartPosition() + node.getNameLength() - pos;
                    if (relativeRank < 0) {
                        return Integer.MAX_VALUE + relativeRank;
                    } else {
                        return relativeRank;
                    }
                }
            });
            for (int i = 0; i < sameNodes.length; i++) {
                ASTNamedNode elem = sameNodes[i];
                String name = elem.getName();
                int elPrefixOffset = name.indexOf(':');
                int elPos = elem.getNameStartPosition();
                int elLength = elem.getNameLength();

                // normalize quote
                if (name.length() + 2 == elLength) {
                    elPos++;
                    elLength -= 2;
                }
                // normalize prefixes
                if (elPrefixOffset > 0) {
                    elPos += elPrefixOffset + 1;
                    elLength = elLength - elPrefixOffset - 1;
                }
                LinkedPosition linkedPosition = new LinkedPosition(document, elPos, elLength, i);
                if (i == 0) {
                    fNamePosition = linkedPosition;
                }
                fLinkedPositionGroup.addPosition(linkedPosition);
            }

            fLinkedModeModel = new LinkedModeModel();
            fLinkedModeModel.addGroup(fLinkedPositionGroup);
            fLinkedModeModel.forceInstall();
            fLinkedModeModel.addLinkingListener(new EditorHighlightingSynchronizer(editor));
            fLinkedModeModel.addLinkingListener(new EditorSynchronizer());

            LinkedModeUI ui = new EditorLinkedModeUI(fLinkedModeModel, viewer);
            ui.setExitPosition(viewer, offset, 0, Integer.MAX_VALUE);
            ui.setExitPolicy(new ExitPolicy(document));
            ui.enter();

            // by default, full word is selected;
            // restore original selection
            viewer.setSelectedRange(fOriginalSelection.x, fOriginalSelection.y);

            if (viewer instanceof IEditingSupportRegistry) {
                IEditingSupportRegistry registry = (IEditingSupportRegistry) viewer;
                registry.register(fFocusEditingSupport);
            }

            openSecondaryPopup();
            // startAnimation();
            fgActiveLinkedMode = this;

        } catch (BadLocationException | YangModelException e) {
            YangRefactoringPlugin.log(e);
        }
    }

    void doRename(boolean showPreview) {
        cancel();

        Image image = null;
        Label label = null;

        fShowPreview |= showPreview;
        try {
            ISourceViewer viewer = editor.getViewer();
            if (viewer instanceof SourceViewer) {
                SourceViewer sourceViewer = (SourceViewer) viewer;
                Control viewerControl = sourceViewer.getControl();
                if (viewerControl instanceof Composite) {
                    Composite composite = (Composite) viewerControl;
                    Display display = composite.getDisplay();

                    // Flush pending redraw requests:
                    while (!display.isDisposed() && display.readAndDispatch()) {
                    }

                    // Copy editor area:
                    GC gc = new GC(composite);
                    Point size;
                    try {
                        size = composite.getSize();
                        image = new Image(gc.getDevice(), size.x, size.y);
                        gc.copyArea(image, 0, 0);
                    } finally {
                        gc.dispose();
                        gc = null;
                    }

                    // Persist editor area while executing refactoring:
                    label = new Label(composite, SWT.NONE);
                    label.setImage(image);
                    label.setBounds(0, 0, size.x, size.y);
                    label.moveAbove(null);
                }
            }

            String newName = fNamePosition.getContent();
            if (fOriginalName.equals(newName)) {
                return;
            }
            RenameSupport renameSupport = undoAndCreateRenameSupport(newName);
            if (renameSupport == null) {
                return;
            }

            Shell shell = editor.getSite().getShell();
            boolean executed;
            if (fShowPreview) {
                executed = renameSupport.openDialog(shell, true);
            } else {
                renameSupport.perform(shell, editor.getSite().getWorkbenchWindow());
                executed = true;
            }
            if (executed) {
                restoreFullSelection();
            }
            editor.reconcileModel();
        } catch (BadLocationException | CoreException e) {
            YangRefactoringPlugin.log(e);
        } finally {
            if (label != null) {
                label.dispose();
            }
            if (image != null) {
                image.dispose();
            }
        }
    }

    public void cancel() {
        if (fLinkedModeModel != null) {
            fLinkedModeModel.exit(ILinkedModeListener.NONE);
        }
        linkedModeLeft();
    }

    private void restoreFullSelection() {
        if (fOriginalSelection.y != 0) {
            int originalOffset = fOriginalSelection.x;
            LinkedPosition[] positions = fLinkedPositionGroup.getPositions();
            for (int i = 0; i < positions.length; i++) {
                LinkedPosition position = positions[i];
                if (!position.isDeleted() && position.includes(originalOffset)) {
                    editor.getViewer().setSelectedRange(position.offset, position.length);
                    return;
                }
            }
        }
    }

    private RenameSupport undoAndCreateRenameSupport(String newName) throws CoreException {
        // Assumption: the linked mode model should be shut down by now.

        final ISourceViewer viewer = editor.getViewer();

        try {
            if (!fOriginalName.equals(newName)) {
                editor.getSite().getWorkbenchWindow().run(false, true, new IRunnableWithProgress() {
                    @Override
                    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                        if (viewer instanceof ITextViewerExtension6) {
                            IUndoManager undoManager = ((ITextViewerExtension6) viewer).getUndoManager();
                            if (undoManager instanceof IUndoManagerExtension) {
                                IUndoManagerExtension undoManagerExtension = (IUndoManagerExtension) undoManager;
                                IUndoContext undoContext = undoManagerExtension.getUndoContext();
                                IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
                                while (undoManager.undoable()) {
                                    if (fStartingUndoOperation != null
                                            && fStartingUndoOperation.equals(operationHistory
                                                    .getUndoOperation(undoContext))) {
                                        return;
                                    }
                                    undoManager.undo();
                                }
                            }
                        }
                    }
                });
            }
        } catch (InvocationTargetException e) {
            throw new CoreException(
                    new Status(IStatus.ERROR, YangRefactoringPlugin.PLUGIN_ID, "Error saving editor", e));
        } catch (InterruptedException e) {
            // canceling is OK
            return null;
        } finally {
            editor.reconcileModel();
        }

        viewer.setSelectedRange(fOriginalSelection.x, fOriginalSelection.y);

        if (newName.length() == 0) {
            return null;
        }
        return new RenameSupport(originalFile, originalNode, newName);
    }

    public void startFullDialog() {
        cancel();

        try {
            String newName = fNamePosition.getContent();
            RenameSupport renameSupport = undoAndCreateRenameSupport(newName);
            if (renameSupport != null) {
                renameSupport.openDialog(editor.getSite().getShell());
            }
        } catch (BadLocationException | CoreException e) {
            YangRefactoringPlugin.log(e);
        }
    }

    private void linkedModeLeft() {
        fgActiveLinkedMode = null;
        if (fInfoPopup != null) {
            fInfoPopup.close();
        }

        ISourceViewer viewer = editor.getViewer();
        if (viewer instanceof IEditingSupportRegistry) {
            IEditingSupportRegistry registry = (IEditingSupportRegistry) viewer;
            registry.unregister(fFocusEditingSupport);
        }
    }

    private void openSecondaryPopup() {
        fInfoPopup = new RenameInformationPopup(editor, this);
        fInfoPopup.open();
    }

    public boolean isCaretInLinkedPosition() {
        return getCurrentLinkedPosition() != null;
    }

    public LinkedPosition getCurrentLinkedPosition() {
        Point selection = editor.getViewer().getSelectedRange();
        int start = selection.x;
        int end = start + selection.y;
        LinkedPosition[] positions = fLinkedPositionGroup.getPositions();
        for (int i = 0; i < positions.length; i++) {
            LinkedPosition position = positions[i];
            if (position.includes(start) && position.includes(end)) {
                return position;
            }
        }
        return null;
    }

    public boolean isOriginalName() {
        try {
            String newName = fNamePosition.getContent();
            return fOriginalName.equals(newName);
        } catch (BadLocationException e) {
            return false;
        }
    }

}
