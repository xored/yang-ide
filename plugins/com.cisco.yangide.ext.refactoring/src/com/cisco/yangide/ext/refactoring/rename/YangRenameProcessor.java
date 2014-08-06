/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.ext.refactoring.rename;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.RenameProcessor;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEditGroup;

import com.cisco.yangide.core.dom.ASTNamedNode;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.QName;
import com.cisco.yangide.core.indexing.ElementIndexReferenceInfo;
import com.cisco.yangide.core.indexing.ElementIndexReferenceType;
import com.cisco.yangide.core.model.YangModelManager;

/**
 * @author Konstantin Zaitsev
 * @param <T> original AST node to rename
 * @date Jul 30, 2014
 */
public abstract class YangRenameProcessor<T extends ASTNamedNode> extends RenameProcessor {
    private String newName;
    private boolean updateReferences;
    private IFile file;
    private T node;

    public YangRenameProcessor(T node) {
        this.node = node;
    }

    /**
     * @return the newName
     */
    public String getNewName() {
        return newName;
    }

    /**
     * @param newName the newName to set
     */
    public void setNewName(String newName) {
        this.newName = newName;
    }

    /**
     * @param selection
     */
    public void setUpdateReferences(boolean updateReferences) {
        this.updateReferences = updateReferences;
    }

    /**
     * @return the updateReferences
     */
    public boolean isUpdateReferences() {
        return updateReferences;
    }

    /**
     * @return the file
     */
    public IFile getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(IFile file) {
        this.file = file;
    }

    @Override
    public String getProcessorName() {
        return "Rename grouping element";
    }

    @Override
    public boolean isApplicable() throws CoreException {
        return node != null;
    }

    @Override
    public Object[] getElements() {
        return new Object[] { node };
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException,
            OperationCanceledException {
        return new RefactoringStatus();
    }

    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor pm, CheckConditionsContext context)
            throws CoreException, OperationCanceledException {
        return new RefactoringStatus();
    }

    @Override
    public RefactoringParticipant[] loadParticipants(RefactoringStatus status, SharableParticipants sharedParticipants)
            throws CoreException {
        return null;
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        ElementIndexReferenceInfo[] infos = getReferences();

        CompositeChange composite = new CompositeChange("Rename");
        composite.markAsSynthetic();

        HashMap<String, TextChange> map = new HashMap<>();
        addEdit(composite, map, file.getFullPath().toString(), node.getNameStartPosition(), node.getNameLength(),
                getNewName());
        for (ElementIndexReferenceInfo info : infos) {
            String name = getNewName();
            if (!info.getPath().equals(file.getFullPath().toString())
                    && info.getType() != ElementIndexReferenceType.IMPORT
                    && info.getType() != ElementIndexReferenceType.INCLUDE) {
                String newName = getNewName();
                if (newName.startsWith("\"")) {
                    name = '"' + info.getReference().getPrefix() + ":" + newName.substring(1, newName.length() - 2)
                            + '"';
                } else {
                    name = info.getReference().getPrefix() + ":" + newName;
                }
            }
            addEdit(composite, map, info.getPath(), info.getStartPosition(), info.getLength(), name);
        }
        return composite;
    }

    protected ElementIndexReferenceInfo[] getReferences() {
        Module module = (Module) node.getModule();
        QName qname = new QName(module.getName(), null, node.getName(), module.getRevision());
        return YangModelManager.getIndexManager().searchReference(qname, getReferenceType(), getFile().getProject());
    }

    protected void addEdit(CompositeChange composite, HashMap<String, TextChange> map, String path, int pos, int len,
            String newName) {
        TextChange change = getChangeOrCreate(composite, map, path);
        ReplaceEdit child = new ReplaceEdit(pos, len, newName);
        change.getEdit().addChild(child);
        change.addTextEditGroup(new TextEditGroup("Update reference", child));
    }

    protected TextChange getChangeOrCreate(CompositeChange composite, HashMap<String, TextChange> map, String path) {
        if (!map.containsKey(path)) {
            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path));
            TextChange change = new TextFileChange("Rename element in file", file);
            change.setTextType("yang");
            MultiTextEdit edit = new MultiTextEdit();
            change.setEdit(edit);
            change.setKeepPreviewEdits(true);
            composite.add(change);
            map.put(path, change);
        }
        return map.get(path);
    }

    protected abstract ElementIndexReferenceType getReferenceType();

    /**
     * @return the node
     */
    public T getNode() {
        return node;
    }
}
