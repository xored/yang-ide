/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.model.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.editor.YangEditorPlugin;
import com.cisco.yangide.editor.editors.YangEditor;
import com.cisco.yangide.ext.model.Module;
import com.cisco.yangide.ext.model.editor.editors.YangDiagramEditor;
import com.cisco.yangide.ext.model.editor.editors.YangDiagramEditorInput;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

/**
 * @author Konstantin Zaitsev
 * @date Aug 7, 2014
 */
public class YangMultiPageEditorPart extends MultiPageEditorPart {

    private YangEditor yangSourceEditor;
    private YangDiagramEditor yangDiagramEditor;

    @Override
    protected void createPages() {
        createSourcePage();
        createDiagramPage();
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public boolean isSaveAsAllowed() {
        return true;
    }

    @Override
    protected void setInput(IEditorInput input) {
        super.setInput(input);
    }

    private void createDiagramPage() {
        yangDiagramEditor = new YangDiagramEditor();
        try {
            Module m = YangModelUtil.exportModel(yangSourceEditor.getModule());
            YangDiagramEditorInput input = new YangDiagramEditorInput(URI.createURI("tmp:/local"),
                    "com.cisco.yangide.ext.model.editor.editorDiagramTypeProvider", m);
            addPage(1, yangDiagramEditor, input);
            setPageText(1, "Diagram");
        } catch (PartInitException | YangModelException e) {
            YangEditorPlugin.log(e);
        }
    }

    private void createSourcePage() {
        yangSourceEditor = new YangEditor();
        try {
            addPage(0, yangSourceEditor, getEditorInput());
            setPageText(0, "Source");
        } catch (PartInitException e) {
            YangEditorPlugin.log(e);
        }
        setPartName(yangSourceEditor.getPartName());
    }
}
