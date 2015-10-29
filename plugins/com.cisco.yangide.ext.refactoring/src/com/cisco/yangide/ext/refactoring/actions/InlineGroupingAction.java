/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.refactoring.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchSite;

import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.core.dom.ASTNamedNode;
import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.GroupingDefinition;
import com.cisco.yangide.core.dom.UsesNode;
import com.cisco.yangide.editor.editors.YangEditor;
import com.cisco.yangide.ext.refactoring.code.InlineGroupingRefactoring;
import com.cisco.yangide.ext.refactoring.ui.InlineGroupingRefactoringWizard;

/**
 * @author Konstantin Zaitsev
 * @date Aug 06, 2014
 */
public class InlineGroupingAction extends SelectionDispatchAction {

    private YangEditor editor;
    private ASTNode node;

    public InlineGroupingAction(IWorkbenchSite site) {
        super(site);
        this.node = null;
        setText("Inline Grouping...");
        setToolTipText("Inline grouping element in place of reference");
        setDescription("Inline grouping element in place of reference");
    }

    public InlineGroupingAction(YangEditor editor) {
        this(editor.getSite());
        this.editor = editor;
    }

    @Override
    public void selectionChanged(ITextSelection selection) {
        try {
            if (editor.getModule() != null) {
                this.node = editor.getModule().getNodeAtPosition(selection.getOffset());
            } else {
                this.node = null;
            }
        } catch (YangModelException e) {
            this.node = null;
        }
        setEnabled(node != null && (node instanceof GroupingDefinition || node instanceof UsesNode));
    }

    @Override
    public void run(ITextSelection selection) {
        if (node != null && (node instanceof GroupingDefinition || node instanceof UsesNode)) {
            IFile file = ((IFileEditorInput) editor.getEditorInput()).getFile();
            InlineGroupingRefactoring refactoring = new InlineGroupingRefactoring(file, (ASTNamedNode) node);
            InlineGroupingRefactoringWizard wizard = new InlineGroupingRefactoringWizard(refactoring);

            RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard);
            try {
                op.run(getShell(), "Inline Grouping");
            } catch (InterruptedException e) {
                // do nothing
            }
        } else {
            MessageDialog.openInformation(getShell(), "Inline",
                    "Operation unavailable on the current selection.\nSelect a grouping or uses element.");
        }
    }
}
