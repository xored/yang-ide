/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.refactoring;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.TextEditGroup;

/**
 * @author Konstantin Zaitsev
 * @date Aug 7, 2014
 */
public class YangCompositeChange extends CompositeChange {
    private HashMap<String, TextChange> map = new HashMap<>();

    public YangCompositeChange(String name) {
        super(name);
    }

    public TextEdit addTextEdit(String path, String changeName, String editName, int pos, int len, String newName) {
        TextChange change = getChangeOrCreate(path, changeName);
        ReplaceEdit child = new ReplaceEdit(pos, len, newName);
        change.getEdit().addChild(child);
        change.addTextEditGroup(new TextEditGroup(editName, child));
        return child;
    }

    private TextChange getChangeOrCreate(String path, String name) {
        if (!map.containsKey(path)) {
            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path));
            TextChange change = new TextFileChange(name, file);
            change.setTextType("yang"); //$NON-NLS-1$
            MultiTextEdit edit = new MultiTextEdit();
            change.setEdit(edit);
            change.setKeepPreviewEdits(true);
            add(change);
            map.put(path, change);
        }
        return map.get(path);
    }
}
