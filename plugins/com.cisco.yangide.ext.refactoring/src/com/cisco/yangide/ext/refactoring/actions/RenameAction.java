/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.ext.refactoring.actions;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchSite;

import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.core.dom.ASTNamedNode;
import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.GroupingDefinition;
import com.cisco.yangide.editor.editors.YangEditor;
import com.cisco.yangide.ext.refactoring.rename.RenameGroupingProcessor;
import com.cisco.yangide.ext.refactoring.ui.RenameRefactoringWizard;

/**
 * @author Konstantin Zaitsev
 * @date Jul 29, 2014
 */
public class RenameAction extends SelectionDispatchAction {

    private YangEditor editor;
    private ASTNode node;

    public RenameAction(IWorkbenchSite site) {
        super(site);
        this.node = null;
        setText("Rename...");
        setToolTipText("Rename the selected element");
        setDescription("Rename the selected element");
    }

    public RenameAction(YangEditor editor) {
        this(editor.getSite());
        this.editor = editor;
    }

    @Override
    public void selectionChanged(ITextSelection selection) {
        try {
            this.node = editor.getModule().getNodeAtPosition(selection.getOffset());
        } catch (YangModelException e) {
            this.node = null;
        }

        boolean enabled = false;
        if (this.node != null) {
            if (this.node instanceof ASTNamedNode) {
                ASTNamedNode nnode = (ASTNamedNode) node;
                if (nnode.getNameStartPosition() <= selection.getOffset()
                        && (nnode.getNameStartPosition() + nnode.getNameLength()) >= selection.getOffset()) {
                    if (nnode instanceof GroupingDefinition) {
                        enabled = true;
                    }
                }
            }
        }
        setEnabled(enabled);
    }

    @Override
    public void run(ITextSelection selection) {
        if (node != null && node instanceof GroupingDefinition) {
            RenameGroupingProcessor processor = new RenameGroupingProcessor((GroupingDefinition) node);
            RenameRefactoring refactoring = new RenameRefactoring(processor);
            processor.setNewName(((GroupingDefinition) node).getName());
            processor.setUpdateReferences(true);
            processor.setFile(((IFileEditorInput) editor.getEditorInput()).getFile());
            processor.setDocument(editor.getDocument());
            RenameRefactoringWizard wizard = new RenameRefactoringWizard(refactoring);
            RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard);
            try {
                op.run(editor.getSite().getShell(), "Rename");
            } catch (InterruptedException e) {
                // do nothing
            }
        }
    }

    // // ---- Structured selection ------------------------------------------------
    //
    // @Override
    // public void selectionChanged(IStructuredSelection selection) {
    // try {
    // if (selection.size() == 1) {
    // setEnabled(canEnable(selection));
    // return;
    // }
    // } catch (CoreException e) {
    // YangRefactoringPlugin.log(e);
    // }
    // setEnabled(false);
    // }
    //
    // private static boolean canEnable(IStructuredSelection selection) throws CoreException {
    // IJavaElement element = getJavaElement(selection);
    // if (element == null) {
    // return false;
    // }
    // return RefactoringAvailabilityTester.isRenameElementAvailable(element);
    // }
    //
    // private static IJavaElement getJavaElement(IStructuredSelection selection) {
    // if (selection.size() != 1) {
    // return null;
    // }
    // Object first = selection.getFirstElement();
    // if (!(first instanceof IJavaElement)) {
    // return null;
    // }
    // return (IJavaElement) first;
    // }
    //
    // @Override
    // public void run(IStructuredSelection selection) {
    // IJavaElement element = getJavaElement(selection);
    // if (element == null) {
    // return;
    // }
    // if (!ActionUtil.isEditable(getShell(), element)) {
    // return;
    // }
    // try {
    // run(element, false);
    // } catch (CoreException e) {
    // ExceptionHandler.handle(e, RefactoringMessages.RenameJavaElementAction_name,
    // RefactoringMessages.RenameJavaElementAction_exception);
    // }
    // }
    //
    // // ---- text selection ------------------------------------------------------------
    //
    // @Override
    // public void selectionChanged(ITextSelection selection) {
    // if (selection instanceof JavaTextSelection) {
    // try {
    // JavaTextSelection javaTextSelection = (JavaTextSelection) selection;
    // IJavaElement[] elements = javaTextSelection.resolveElementAtOffset();
    // if (elements.length == 1) {
    // setEnabled(RefactoringAvailabilityTester.isRenameElementAvailable(elements[0]));
    // } else {
    // ASTNode node = javaTextSelection.resolveCoveringNode();
    // setEnabled(node instanceof SimpleName);
    // }
    // } catch (CoreException e) {
    // setEnabled(false);
    // }
    // } else {
    // setEnabled(true);
    // }
    // }
    //
    // @Override
    // public void run(ITextSelection selection) {
    // if (!ActionUtil.isEditable(fEditor)) {
    // return;
    // }
    // if (canRunInEditor()) {
    // doRun();
    // } else {
    // MessageDialog.openInformation(getShell(), RefactoringMessages.RenameAction_rename,
    // RefactoringMessages.RenameAction_unavailable);
    // }
    // }
    //
    // public void doRun() {
    // RenameLinkedMode activeLinkedMode = RenameLinkedMode.getActiveLinkedMode();
    // if (activeLinkedMode != null) {
    // if (activeLinkedMode.isCaretInLinkedPosition()) {
    // activeLinkedMode.startFullDialog();
    // return;
    // } else {
    // activeLinkedMode.cancel();
    // }
    // }
    //
    // try {
    // IJavaElement element = getJavaElementFromEditor();
    // IPreferenceStore store = JavaPlugin.getDefault().getPreferenceStore();
    // boolean lightweight = store.getBoolean(PreferenceConstants.REFACTOR_LIGHTWEIGHT);
    // if (element != null && RefactoringAvailabilityTester.isRenameElementAvailable(element)) {
    // run(element, lightweight);
    // return;
    // } else if (lightweight) {
    // // fall back to local rename:
    // CorrectionCommandHandler handler = new CorrectionCommandHandler(fEditor,
    // LinkedNamesAssistProposal.ASSIST_ID, true);
    // if (handler.doExecute()) {
    // fEditor.setStatusLineErrorMessage(RefactoringMessages.RenameJavaElementAction_started_rename_in_file);
    // return;
    // }
    // }
    // } catch (CoreException e) {
    // ExceptionHandler.handle(e, RefactoringMessages.RenameJavaElementAction_name,
    // RefactoringMessages.RenameJavaElementAction_exception);
    // }
    // MessageDialog.openInformation(getShell(), RefactoringMessages.RenameJavaElementAction_name,
    // RefactoringMessages.RenameJavaElementAction_not_available);
    // }
    //
    // public boolean canRunInEditor() {
    // if (RenameLinkedMode.getActiveLinkedMode() != null) {
    // return true;
    // }
    //
    // try {
    // IJavaElement element = getJavaElementFromEditor();
    // if (element == null) {
    // return true;
    // }
    //
    // return RefactoringAvailabilityTester.isRenameElementAvailable(element);
    // } catch (JavaModelException e) {
    // if (JavaModelUtil.isExceptionToBeLogged(e)) {
    // JavaPlugin.log(e);
    // }
    // } catch (CoreException e) {
    // JavaPlugin.log(e);
    // }
    // return false;
    // }
    //
    // private IJavaElement getJavaElementFromEditor() throws JavaModelException {
    // IJavaElement[] elements = SelectionConverter.codeResolve(fEditor);
    // if (elements == null || elements.length != 1) {
    // return null;
    // }
    // return elements[0];
    // }
    //
    // // ---- helper methods -------------------------------------------------------------------
    //
    // private void run(IJavaElement element, boolean lightweight) throws CoreException {
    // // Work around for http://dev.eclipse.org/bugs/show_bug.cgi?id=19104
    // if (!ActionUtil.isEditable(fEditor, getShell(), element)) {
    // return;
    // }
    // // XXX workaround bug 31998
    // if (ActionUtil.mustDisableJavaModelAction(getShell(), element)) {
    // return;
    // }
    //
    // if (lightweight && fEditor instanceof CompilationUnitEditor && !(element instanceof
    // IPackageFragment)) {
    // new RenameLinkedMode(element, (CompilationUnitEditor) fEditor).start();
    // } else {
    // RefactoringExecutionStarter.startRenameRefactoring(element, getShell());
    // }
    // }
}
