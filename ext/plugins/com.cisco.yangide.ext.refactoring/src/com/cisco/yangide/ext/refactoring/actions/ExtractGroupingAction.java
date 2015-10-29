/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.refactoring.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchSite;

import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.editor.editors.YangEditor;
import com.cisco.yangide.ext.refactoring.RefactorUtil;
import com.cisco.yangide.ext.refactoring.code.ExtractGroupingRefactoring;
import com.cisco.yangide.ext.refactoring.nls.Messages;
import com.cisco.yangide.ext.refactoring.ui.ExtractGroupingRefactoringWizard;

/**
 * @author Konstantin Zaitsev
 * @date Aug 19, 2014
 */
public class ExtractGroupingAction extends SelectionDispatchAction {

    /** Reference to the editor. */
    private YangEditor editor;

    public ExtractGroupingAction(IWorkbenchSite site) {
        super(site);
        setText(Messages.ExtractGroupingAction_text);
        setToolTipText(Messages.ExtractGroupingAction_description);
        setDescription(Messages.ExtractGroupingAction_description);
    }

    public ExtractGroupingAction(YangEditor editor) {
        this(editor.getSite());
        this.editor = editor;
    }

    @Override
    public void selectionChanged(ITextSelection selection) {
        setEnabled(getNormalizedSelection(selection) != null);
    }

    @Override
    public void run(ITextSelection selection) {
        if (editor.getEditorInput() != null && editor.getEditorInput() instanceof IFileEditorInput) {
            IFile file = ((IFileEditorInput) editor.getEditorInput()).getFile();
            ITextSelection sel = getNormalizedSelection(selection);
            try {
                ExtractGroupingRefactoring refactoring = new ExtractGroupingRefactoring(file, editor.getModule(),
                        sel.getOffset(), sel.getLength());
                ExtractGroupingRefactoringWizard wizard = new ExtractGroupingRefactoringWizard(refactoring);

                RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard);
                op.run(getShell(), Messages.ExtractGroupingAction_name);
                editor.reconcileModel();
            } catch (InterruptedException | YangModelException e) {
                // do nothing
            }
        }
    }

    private ITextSelection getNormalizedSelection(ITextSelection selection) {
        if (editor != null) {
            try {
                if (selection.getText() != null) {
                    Module module = editor.getModule();
                    if (module != null) {

                        String txt = selection.getText().trim();
                        int offset = selection.getOffset() + selection.getText().indexOf(txt);
                        ASTNode startNode = module.getNodeAtPosition(offset);
                        int startLevel = RefactorUtil.getNodeLevel(startNode);

                        ASTNode endNode = module.getNodeAtPosition(offset + txt.length() - 1);
                        int endLevel = RefactorUtil.getNodeLevel(endNode);
                        if (endLevel == startLevel && startLevel > 0
                                && startNode.getParent().equals(endNode.getParent())) {
                            return new TextSelection(startNode.getStartPosition(), endNode.getEndPosition()
                                    - startNode.getStartPosition() + 1);
                        }
                    }
                }
            } catch (YangModelException e) {
                // ignore exception
            }
        }
        return null;
    }
}
