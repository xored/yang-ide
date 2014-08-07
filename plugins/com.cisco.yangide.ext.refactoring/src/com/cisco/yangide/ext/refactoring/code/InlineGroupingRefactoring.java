/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.refactoring.code;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cisco.yangide.core.dom.ASTNamedNode;
import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.GroupingDefinition;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.QName;
import com.cisco.yangide.core.dom.UsesNode;
import com.cisco.yangide.core.indexing.ElementIndexInfo;
import com.cisco.yangide.core.indexing.ElementIndexReferenceInfo;
import com.cisco.yangide.core.indexing.ElementIndexReferenceType;
import com.cisco.yangide.core.model.YangModelManager;
import com.cisco.yangide.core.parser.YangFormattingPreferences;
import com.cisco.yangide.core.parser.YangParserUtil;
import com.cisco.yangide.ext.refactoring.RefactorUtil;
import com.cisco.yangide.ext.refactoring.YangCompositeChange;
import com.cisco.yangide.ext.refactoring.YangRefactoringPlugin;

/**
 * @author Konstantin Zaitsev
 * @date Aug 6, 2014
 */
public class InlineGroupingRefactoring extends Refactoring {

    private ASTNamedNode node;
    private boolean deleteGrouping;
    private boolean inlineAll;
    private IFile file;

    public InlineGroupingRefactoring(IFile file, ASTNamedNode node) {
        this.file = file;
        this.node = node;
        this.inlineAll = node instanceof GroupingDefinition;
    }

    @Override
    public String getName() {
        return "Inline Grouping";
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException,
    OperationCanceledException {
        return new RefactoringStatus();
    }

    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        return new RefactoringStatus();
    }

    /**
     * @return the node
     */
    public ASTNamedNode getNode() {
        return node;
    }

    /**
     * @return the deleteGrouping
     */
    public boolean isDeleteGrouping() {
        return deleteGrouping;
    }

    /**
     * @param deleteGrouping the deleteGrouping to set
     */
    public void setDeleteGrouping(boolean deleteGrouping) {
        this.deleteGrouping = deleteGrouping;
    }

    /**
     * @return the inlineAll
     */
    public boolean isInlineAll() {
        return inlineAll;
    }

    /**
     * @param inlineAll the inlineAll to set
     */
    public void setInlineAll(boolean inlineAll) {
        this.inlineAll = inlineAll;
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        String editName = "Inline grouping";

        IFile groupFile = file;
        ASTNamedNode groupNode = node;
        String groupContent = null;

        // get content of grouping element
        if (node instanceof UsesNode) {
            ElementIndexInfo ref = RefactorUtil.getByReference(file.getProject(), node);
            groupContent = RefactorUtil.loadIndexInfoContent(ref);
            groupNode = (ASTNamedNode) RefactorUtil.resolveIndexInfo(ref);
            groupFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(ref.getPath()));
        } else {
            groupContent = RefactorUtil.loadNodeContent(groupNode, groupFile);
        }

        groupContent = trimGroupContent(groupContent);
        groupContent = formatGroupContent(groupContent, node);

        if (groupContent == null || groupNode == null) {
            throw new CoreException(new Status(IStatus.ERROR, YangRefactoringPlugin.PLUGIN_ID,
                    "Cannot get grouping content"));
        }

        YangCompositeChange composite = new YangCompositeChange("Inline");
        composite.markAsSynthetic();

        if (!isInlineAll()) { // in this case UsesNode is selected
            composite.addTextEdit(file.getFullPath().toString(), editName, editName, node.getStartPosition(),
                    node.getLength() + 1, groupContent);
        } else { // inline all occurrence
            if (isDeleteGrouping()) { // delete orignal group
                composite.addTextEdit(groupFile.getFullPath().toString(), editName, "Delete grouping declaration",
                        groupNode.getStartPosition(), groupNode.getLength() + 1, "");
            }

            Module module = (Module) groupNode.getModule();
            QName qname = new QName(module.getName(), null, groupNode.getName(), module.getRevision());
            ElementIndexReferenceInfo[] infos = YangModelManager.getIndexManager().searchReference(qname,
                    ElementIndexReferenceType.USES, groupFile.getProject());

            pm.beginTask("Inline references", infos.length);
            for (ElementIndexReferenceInfo info : infos) {
                ASTNode usesNode = RefactorUtil.resolveIndexInfo(info);
                String content = formatGroupContent(groupContent, usesNode);
                composite.addTextEdit(info.getPath(), editName, editName, usesNode.getStartPosition(),
                        usesNode.getLength() + 1, content);
                pm.worked(1);
            }
            pm.done();
        }
        return composite;
    }

    /**
     * Removed brackets from group body content.
     *
     * @param content
     * @return trimmed group body
     */
    private String trimGroupContent(String content) {
        if (content == null) {
            return null;
        }
        String str = content.trim();
        if (str.indexOf('{') >= 0) {
            str = str.substring(str.indexOf('{') + 1, str.length());
        }
        if (str.lastIndexOf('}') >= 0) {
            str = str.substring(0, str.lastIndexOf('}'));
        }

        return str;
    }

    /**
     * @param content content to forma
     * @param node ast node for replacement to calculate indentation level
     * @return formatted string
     */
    private String formatGroupContent(String content, ASTNode node) {
        if (content == null) {
            return null;
        }
        return YangParserUtil.formatYangSource(new YangFormattingPreferences(), content.toCharArray(),
                getIndentLevel(node), System.getProperty("line.separator"));
    }

    /**
     * @param node node to calculate
     * @return indentation level for given node
     */
    private int getIndentLevel(ASTNode node) {
        int indentLevel = -1;
        ASTNode parent = node;
        while (parent != null) {
            parent = parent.getParent();
            indentLevel++;
        }
        return indentLevel >= 0 ? indentLevel : 0;
    }
}
