/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.refactoring.code;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.ext.refactoring.RefactorUtil;
import com.cisco.yangide.ext.refactoring.YangCompositeChange;
import com.cisco.yangide.ext.refactoring.YangRefactoringPlugin;
import com.cisco.yangide.ext.refactoring.nls.Messages;

/**
 * @author Konstantin Zaitsev
 * @date Aug 19, 2014
 */
public class ExtractGroupingRefactoring extends Refactoring {

    private final IFile file;
    private final Module module;
    private final int length;
    private final int offset;

    private String groupName;

    public ExtractGroupingRefactoring(IFile file, Module module, int offset, int length) {
        this.file = file;
        this.module = module;
        this.offset = offset;
        this.length = length;
    }

    @Override
    public String getName() {
        return Messages.ExtractGroupingRefactoring_name;
    }

    /**
     * @return the module
     */
    public Module getModule() {
        return module;
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        return new RefactoringStatus();
    }

    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
            throws CoreException, OperationCanceledException {
        return new RefactoringStatus();
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        String editName = Messages.ExtractGroupingRefactoring_name;

        YangCompositeChange composite = new YangCompositeChange(editName);
        composite.markAsSynthetic();

        String path = file.getFullPath().toString();
        try {
            String content = FileBuffers.getTextFileBufferManager()
                    .getTextFileBuffer(file.getFullPath(), LocationKind.IFILE).getDocument().get(offset, length);
            ASTNode node = module.getNodeAtPosition(offset);

            // find appropriate node to insert extracted grouping
            while (!(node.getParent() instanceof Module)) {
                node = node.getParent();
            }
            composite.addTextEdit(path, editName, editName, node.getEndPosition() + 2, 0, getGroupingContent(content));

            String uses = "uses " + groupName + ";\n"; //$NON-NLS-1$//$NON-NLS-2$
            composite.addTextEdit(path, editName, Messages.ExtractGroupingRefactoring_updateReferenceEditName, offset,
                    length, uses);

        } catch (BadLocationException e) {
            new CoreException(new Status(Status.ERROR, YangRefactoringPlugin.PLUGIN_ID, e.getMessage(), e));
        }
        return composite;
    }

    /**
     * @param content
     * @return
     */
    private String getGroupingContent(String content) {
        StringBuilder sb = new StringBuilder();
        sb.append("grouping ").append(groupName).append(" {\n"); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append(content);
        sb.append("}\n"); //$NON-NLS-1$
        return RefactorUtil.formatCodeSnipped(sb.toString(), 1);
    }

    /**
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName the groupName to set
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @return the offset
     */
    public int getOffset() {
        return offset;
    }
}
