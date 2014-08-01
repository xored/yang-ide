/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.ext.refactoring.rename;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEditGroup;

import com.cisco.yangide.core.dom.GroupingDefinition;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.QName;
import com.cisco.yangide.core.indexing.ElementIndexReferenceInfo;
import com.cisco.yangide.core.indexing.ElementIndexReferenceType;
import com.cisco.yangide.core.model.YangModelManager;
import com.cisco.yangide.ext.refactoring.YangFileChange;

/**
 * @author Konstantin Zaitsev
 * @date Jul 30, 2014
 */
public class RenameGroupingProcessor extends YangRenameProcessor {

    private GroupingDefinition grouping;

    public RenameGroupingProcessor(GroupingDefinition grouping) {
        this.grouping = grouping;
    }

    @Override
    public Object[] getElements() {
        return new Object[] { grouping };
    }

    @Override
    public String getIdentifier() {
        return "com.cisco.yangide.ext.refactoring.rename.RenameGroupingProcessor";
    }

    @Override
    public String getProcessorName() {
        return "Rename grouping element";
    }

    @Override
    public boolean isApplicable() throws CoreException {
        return grouping != null;
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
    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        CompositeChange composite = new CompositeChange("Rename");
        YangFileChange change = new YangFileChange("local file", getFile(), getDocument());
        MultiTextEdit edit = new MultiTextEdit();
        change.setEdit(edit);
        change.setKeepPreviewEdits(true);

        Module module = (Module) grouping.getModule();
        QName qname = new QName(module.getName(), null, grouping.getName(), module.getRevision());

        ElementIndexReferenceInfo[] infos = YangModelManager.getIndexManager().searchReference(qname,
                ElementIndexReferenceType.USES, getFile().getProject());

        HashMap<String, List<ElementIndexReferenceInfo>> map = new HashMap<>();
        for (ElementIndexReferenceInfo info : infos) {
            if (!map.containsKey(info.getPath())) {
                map.put(info.getPath(), new ArrayList<ElementIndexReferenceInfo>());
            }
            map.get(info.getPath()).add(info);
        }
        ReplaceEdit child = new ReplaceEdit(grouping.getNameStartPosition(), grouping.getNameLength(), getNewName());
        edit.addChild(child);
        change.addTextEditGroup(new TextEditGroup("Update grouping reference", child));
        composite.add(change);

        for (Entry<String, List<ElementIndexReferenceInfo>> elements : map.entrySet()) {
            if (elements.getKey().equals(getFile().getFullPath().toString())) {
                for (ElementIndexReferenceInfo info : elements.getValue()) {
                    ReplaceEdit replaceEdit = new ReplaceEdit(info.getStartPosition(), info.getLength(), getNewName());
                    edit.addChild(replaceEdit);
                    change.addTextEditGroup(new TextEditGroup("Update grouping reference", replaceEdit));
                }
            } else {
                YangFileChange fchange = new YangFileChange("file", ResourcesPlugin.getWorkspace().getRoot()
                        .getFile(new Path(elements.getKey())), null);
                MultiTextEdit fedit = new MultiTextEdit();
                fchange.setEdit(fedit);
                fchange.setKeepPreviewEdits(true);
                composite.add(fchange);
                for (ElementIndexReferenceInfo info : elements.getValue()) {
                    ReplaceEdit replaceEdit = new ReplaceEdit(info.getStartPosition(), info.getLength(), info
                            .getReference().getPrefix() + ":" + getNewName());
                    fedit.addChild(replaceEdit);
                    fchange.addTextEditGroup(new TextEditGroup("Update grouping reference", replaceEdit));
                }
            }
        }
        return composite;
    }

    @Override
    public RefactoringParticipant[] loadParticipants(RefactoringStatus status, SharableParticipants sharedParticipants)
            throws CoreException {
        return null;
    }
}
