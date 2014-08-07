/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.ext.refactoring.code;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEditGroup;

import com.cisco.yangide.core.dom.ASTNamedNode;
import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.GroupingDefinition;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.QName;
import com.cisco.yangide.core.indexing.ElementIndexInfo;
import com.cisco.yangide.core.indexing.ElementIndexReferenceInfo;
import com.cisco.yangide.core.indexing.ElementIndexReferenceType;
import com.cisco.yangide.core.model.YangModelManager;
import com.cisco.yangide.core.parser.YangFormattingPreferences;
import com.cisco.yangide.core.parser.YangParserUtil;
import com.cisco.yangide.ext.refactoring.RefactorUtil;

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

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        CompositeChange composite = new CompositeChange("Inline");
        composite.markAsSynthetic();

        HashMap<String, TextChange> map = new HashMap<>();
        if (!isInlineAll()) {
            ElementIndexInfo ref = RefactorUtil.getByReference(file.getProject(), node);
            String content = RefactorUtil.loadIndexInfoContent(ref);
            if (content != null) {
                content = content.substring(0, content.lastIndexOf('}'));
                content = content.substring(content.indexOf('{') + 1, content.length());

                int indentLevel = -1;
                ASTNode parent = node;
                while (parent != null) {
                    parent = parent.getParent();
                    indentLevel++;
                }

                content = YangParserUtil.formatYangSource(new YangFormattingPreferences(), content.toCharArray(),
                        indentLevel, System.getProperty("line.separator"));
                addEdit(composite, map, file.getFullPath().toString(), node.getStartPosition(), node.getLength() + 1,
                        content);
            }
        } else {
            String content = null;
            IFile groupFile = null;
            ASTNamedNode group = null;
            if (node instanceof GroupingDefinition) {
                groupFile = file;
                group = node;
                try (InputStreamReader reader = new InputStreamReader(file.getContents(), "UTF-8")) {
                    char[] cbuf = new char[node.getBodyLength()];
                    reader.skip(node.getBodyStartPosition());
                    reader.read(cbuf, 0, node.getBodyLength());
                    content = new String(cbuf);
                } catch (IOException | CoreException e) {
                }
                if (isDeleteGrouping()) {
                    addEdit(composite, map, file.getFullPath().toString(), node.getStartPosition(),
                            node.getLength() + 1, "");
                }
            } else {
                ElementIndexInfo ref = RefactorUtil.getByReference(file.getProject(), node);
                group = (ASTNamedNode) RefactorUtil.resolveIndexInfo(ref);
                groupFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(ref.getPath()));

                content = RefactorUtil.loadIndexInfoContent(ref);
                if (isDeleteGrouping()) {
                    addEdit(composite, map, ref.getPath(), group.getStartPosition(), group.getLength() + 1, "");
                }
            }
            if (content != null) {
                content = content.substring(0, content.lastIndexOf('}'));
                content = content.substring(content.indexOf('{') + 1, content.length());

                int indentLevel = -1;
                ASTNode parent = node;
                while (parent != null) {
                    parent = parent.getParent();
                    indentLevel++;
                }

                Module module = (Module) group.getModule();
                QName qname = new QName(module.getName(), null, group.getName(), module.getRevision());
                ElementIndexReferenceInfo[] infos = YangModelManager.getIndexManager().searchReference(qname,
                        ElementIndexReferenceType.USES, groupFile.getProject());
                String c = YangParserUtil.formatYangSource(new YangFormattingPreferences(), content.toCharArray(),
                        indentLevel, System.getProperty("line.separator"));
                addEdit(composite, map, file.getFullPath().toString(), node.getStartPosition(), node.getLength() + 1, c);
            }
        }
        return composite;
    }

    protected void addEdit(CompositeChange composite, HashMap<String, TextChange> map, String path, int pos, int len,
            String newName) {
        TextChange change = getChangeOrCreate(composite, map, path);
        ReplaceEdit child = new ReplaceEdit(pos, len, newName);
        change.getEdit().addChild(child);
        change.addTextEditGroup(new TextEditGroup("Inline grouping", child));
    }

    protected TextChange getChangeOrCreate(CompositeChange composite, HashMap<String, TextChange> map, String path) {
        if (!map.containsKey(path)) {
            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path));
            TextChange change = new TextFileChange("Inline grouping", file);
            change.setTextType("yang");
            MultiTextEdit edit = new MultiTextEdit();
            change.setEdit(edit);
            change.setKeepPreviewEdits(true);
            composite.add(change);
            map.put(path, change);
        }
        return map.get(path);
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
}
