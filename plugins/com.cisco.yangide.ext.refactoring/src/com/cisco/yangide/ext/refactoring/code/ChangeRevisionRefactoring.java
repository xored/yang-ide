/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.refactoring.code;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.dom.ASTNamedNode;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.ModuleImport;
import com.cisco.yangide.core.dom.QName;
import com.cisco.yangide.core.indexing.ElementIndexReferenceInfo;
import com.cisco.yangide.core.indexing.ElementIndexReferenceType;
import com.cisco.yangide.core.model.YangFile;
import com.cisco.yangide.core.model.YangModelManager;
import com.cisco.yangide.ext.refactoring.CreateYangFileChange;
import com.cisco.yangide.ext.refactoring.RefactorUtil;
import com.cisco.yangide.ext.refactoring.YangCompositeChange;
import com.cisco.yangide.ext.refactoring.nls.Messages;

/**
 * @author Konstantin Zaitsev
 * @date Aug 18, 2014
 */
public class ChangeRevisionRefactoring extends Refactoring {

    private IFile file;
    private Module module;
    private String revision;
    private String description;
    private boolean createNewFile;
    private List<IPath> references;

    public ChangeRevisionRefactoring(IFile file, Module module) {
        this.file = file;
        this.module = module;
        this.references = new ArrayList<>();
        this.createNewFile = true;
    }

    @Override
    public String getName() {
        return Messages.ChangeRevisionRefactoring_name;
    }

    /**
     * @return the module
     */
    public Module getModule() {
        return module;
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
        String editName = Messages.ChangeRevisionRefactoring_changeName;

        YangCompositeChange composite = new YangCompositeChange(editName);
        composite.markAsSynthetic();
        if (createNewFile) {
            String source = FileBuffers.getTextFileBufferManager()
                    .getTextFileBuffer(file.getFullPath(), LocationKind.IFILE).getDocument().get();
            ASTNamedNode revisionNode = module.getRevisionNode();
            source = source.substring(0, revisionNode.getStartPosition()) + getFormattedRevision()
                    + source.substring(revisionNode.getEndPosition() + 1, source.length());
            IPath path = file.getFullPath();
            path = path.removeFileExtension();
            path = path.removeLastSegments(1).append(module.getName() + "@" + revision + ".yang"); //$NON-NLS-1$ //$NON-NLS-2$
            composite.add(new CreateYangFileChange(path, source));
        } else {
            ASTNamedNode revisionNode = module.getRevisionNode();
            composite.addTextEdit(file.getFullPath().toString(), editName, editName,
                    revisionNode.getNameStartPosition(), revisionNode.getNameLength(), "\"" + revision + "\""); //$NON-NLS-1$//$NON-NLS-2$
            composite
                    .addTextEdit(file.getFullPath().toString(), editName, editName,
                            revisionNode.getDescriptionStartPosition() + 1, revisionNode.getDescription().length(),
                            description);
        }

        YangCompositeChange refComposite = new YangCompositeChange(Messages.ChangeRevisionRefactoring_updateReferenceChangeName);
        composite.add(refComposite);

        QName qname = new QName(module.getName(), null, module.getName(), module.getRevision());
        ElementIndexReferenceInfo[] infos = YangModelManager.getIndexManager().searchReference(qname,
                ElementIndexReferenceType.IMPORT, file.getProject());
        for (ElementIndexReferenceInfo info : infos) {
            if (references.contains(new Path(info.getPath()))) {
                YangFile yangFile = YangCorePlugin.createYangFile(ResourcesPlugin.getWorkspace().getRoot()
                        .getFile(new Path(info.getPath())));
                Module refModule = yangFile.getModule();
                ModuleImport refImport = refModule.getImportByName(module.getName());

                refComposite.addTextEdit(info.getPath(), editName, editName, refImport.getStartPosition(),
                        refImport.getLength() + 1, getFormattedImport(refImport));
            }
        }
        return composite;
    }

    /**
     * @return the revision
     */
    public String getRevision() {
        return revision;
    }

    /**
     * @param revision the revision to set
     */
    public void setRevision(String revision) {
        this.revision = revision;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the createNewFile
     */
    public boolean isCreateNewFile() {
        return createNewFile;
    }

    /**
     * @param createNewFile the createNewFile to set
     */
    public void setCreateNewFile(boolean createNewFile) {
        this.createNewFile = createNewFile;
    }

    /**
     * @return the file
     */
    public IFile getFile() {
        return file;
    }

    /**
     * @return the references
     */
    public List<IPath> getReferences() {
        return references;
    }

    /**
     * @param references the references to set
     */
    public void setReferences(List<IPath> references) {
        this.references = references;
    }

    private String getFormattedRevision() {
        StringBuilder sb = new StringBuilder();
        sb.append("revision \"").append(revision).append("\" {\n"); //$NON-NLS-1$ //$NON-NLS-2$
        if (description != null && !description.isEmpty()) {
            sb.append("description \"").append(description).append("\";\n"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        String reference = module.getRevisionNode().getReference();
        if (reference != null && !reference.isEmpty()) {
            sb.append("reference \"").append(reference).append("\";\n"); //$NON-NLS-1$//$NON-NLS-2$
        }
        sb.append("}\n"); //$NON-NLS-1$
        return RefactorUtil.formatCodeSnipped(sb.toString(), 1).trim();
    }

    private String getFormattedImport(ModuleImport moduleImport) {
        StringBuilder sb = new StringBuilder();
        sb.append("import ").append(module.getName()).append("\" {\n"); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("prefix ").append(moduleImport.getPrefix()).append(";\n"); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("revision-date \"").append(revision).append("\";\n"); //$NON-NLS-1$//$NON-NLS-2$
        sb.append("}\n"); //$NON-NLS-1$
        return RefactorUtil.formatCodeSnipped(sb.toString(), 1).trim();
    }
}
