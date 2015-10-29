/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.refactoring.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchSite;

import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.editor.editors.YangEditor;
import com.cisco.yangide.ext.refactoring.code.ChangeRevisionRefactoring;
import com.cisco.yangide.ext.refactoring.nls.Messages;
import com.cisco.yangide.ext.refactoring.ui.ChangeRevisionRefactoringWizard;

/**
 * @author Konstantin Zaitsev
 * @date Aug 18, 2014
 */
public class ChangeRevisionAction extends SelectionDispatchAction {

    /** Reference to the editor. */
    private YangEditor editor;

    public ChangeRevisionAction(IWorkbenchSite site) {
        super(site);
        setText(Messages.ChangeRevisionAction_text);
        setToolTipText(Messages.ChangeRevisionAction_description);
        setDescription(Messages.ChangeRevisionAction_description);
    }

    public ChangeRevisionAction(YangEditor editor) {
        this(editor.getSite());
        this.editor = editor;
        setEnabled(editor != null);
    }

    @Override
    public void selectionChanged(ISelection selection) {
        setEnabled(editor != null);
    }

    @Override
    public void run(ITextSelection selection) {
        if (editor.getEditorInput() != null && editor.getEditorInput() instanceof IFileEditorInput) {
            IFile file = ((IFileEditorInput) editor.getEditorInput()).getFile();

            try {
                ChangeRevisionRefactoring refactoring = new ChangeRevisionRefactoring(file, editor.getModule());
                ChangeRevisionRefactoringWizard wizard = new ChangeRevisionRefactoringWizard(refactoring);

                RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard);
                op.run(getShell(), Messages.ChangeRevisionAction_name);

            } catch (InterruptedException | YangModelException e) {
                // do nothing
            }
        }
    }
}
