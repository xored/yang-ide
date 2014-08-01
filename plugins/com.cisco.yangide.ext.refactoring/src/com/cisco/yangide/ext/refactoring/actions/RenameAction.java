/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.ext.refactoring.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchSite;

import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.core.YangTypeUtil;
import com.cisco.yangide.core.dom.ASTNamedNode;
import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.BaseReference;
import com.cisco.yangide.core.dom.GroupingDefinition;
import com.cisco.yangide.core.dom.IdentitySchemaNode;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.SubModule;
import com.cisco.yangide.core.dom.TypeDefinition;
import com.cisco.yangide.core.dom.TypeReference;
import com.cisco.yangide.core.dom.UsesNode;
import com.cisco.yangide.core.indexing.ElementIndexInfo;
import com.cisco.yangide.editor.editors.YangEditor;
import com.cisco.yangide.ext.refactoring.RefactorUtil;
import com.cisco.yangide.ext.refactoring.rename.RenameGroupingProcessor;
import com.cisco.yangide.ext.refactoring.rename.RenameTypeProcessor;
import com.cisco.yangide.ext.refactoring.rename.YangRenameProcessor;
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
                    enabled = isDirectRename(nnode) || isIndirectRename(nnode);
                }
            }
        }
        setEnabled(enabled);
    }

    @Override
    public void run(ITextSelection selection) {
        if (node != null && (isDirectRename(node) || isIndirectRename(node))) {
            YangRenameProcessor processor = null;
            IFile file = ((IFileEditorInput) editor.getEditorInput()).getFile();
            ASTNode originalNode = null;
            if (isIndirectRename(node)) {
                ElementIndexInfo info = RefactorUtil.getByReference(file.getProject(), node);
                if (info != null) {
                    if (info.getEntry() != null && !info.getEntry().isEmpty()) {
                        MessageDialog.openInformation(getShell(), "Rename",
                                "Operation unavailable on the current selection.\n"
                                        + "The original element is located in JAR file and cannot be renamed.");
                        return;
                    }
                    originalNode = RefactorUtil.resolveIndexInfo(info);
                }
                if (originalNode == null) {
                    MessageDialog.openInformation(getShell(), "Rename",
                            "Operation unavailable on the current selection.\n"
                                    + "Cannot find the original element for the reference.");
                    return;
                }
                file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(info.getPath()));
            } else {
                originalNode = node;
            }

            if (originalNode instanceof GroupingDefinition) {
                processor = new RenameGroupingProcessor((GroupingDefinition) originalNode);
            } else if (originalNode instanceof TypeDefinition) {
                processor = new RenameTypeProcessor((TypeDefinition) originalNode);
            }
            RenameRefactoring refactoring = new RenameRefactoring(processor);
            processor.setNewName(((ASTNamedNode) originalNode).getName());
            processor.setUpdateReferences(true);
            processor.setFile(file);
            processor.setDocument(null);
            RenameRefactoringWizard wizard = new RenameRefactoringWizard(refactoring);
            RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard);
            try {
                op.run(editor.getSite().getShell(), "Rename");
            } catch (InterruptedException e) {
                // do nothing
            }
        } else {
            MessageDialog.openInformation(getShell(), "Rename", "Operation unavailable on the current selection.\n"
                    + "Select a grouping name, module name, type name or identify name.");
        }
    }

    /**
     * @param node node to inspect
     * @return <code>true</code> if node available to rename
     */
    private boolean isDirectRename(ASTNode node) {
        return node instanceof GroupingDefinition || node instanceof TypeDefinition
                || node instanceof IdentitySchemaNode || node instanceof Module || node instanceof SubModule;
    }

    /**
     * @param node node to inspect
     * @return <code>true</code> if node is reference to perform indirect renaming
     */
    private boolean isIndirectRename(ASTNode node) {
        return node instanceof UsesNode
                || (node instanceof TypeReference && !YangTypeUtil.isBuiltInType(((TypeReference) node).getName()))
                || node instanceof BaseReference;
    }
}
