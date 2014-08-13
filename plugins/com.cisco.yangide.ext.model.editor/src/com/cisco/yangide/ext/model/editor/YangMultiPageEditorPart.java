/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.model.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

import com.cisco.yangide.editor.YangEditorPlugin;
import com.cisco.yangide.editor.editors.YangEditor;
import com.cisco.yangide.ext.model.Module;
import com.cisco.yangide.ext.model.editor.editors.YangDiagramEditor;
import com.cisco.yangide.ext.model.editor.editors.YangDiagramEditorInput;
import com.cisco.yangide.ext.model.editor.sync.ModelSynchronizer;

/**
 * @author Konstantin Zaitsev
 * @date Aug 7, 2014
 */
public class YangMultiPageEditorPart extends MultiPageEditorPart {

    private YangEditor yangSourceEditor;
    private YangDiagramEditor yangDiagramEditor;
    private ModelSynchronizer modelSynchronizer;

    @Override
    protected void createPages() {
        yangSourceEditor = new YangEditor();
        yangDiagramEditor = new YangDiagramEditor();
        modelSynchronizer = new ModelSynchronizer(yangSourceEditor, yangDiagramEditor);
        initSourcePage();
        initDiagramPage();
        modelSynchronizer.enableNotification();
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        yangSourceEditor.doSave(monitor);
    }

    @Override
    public void doSaveAs() {
        yangSourceEditor.doSaveAs();
    }

    @Override
    public boolean isSaveAsAllowed() {
        return yangSourceEditor.isSaveAsAllowed();
    }

    @Override
    public boolean isDirty() {
        return yangSourceEditor.isDirty();
    }

    private void initDiagramPage() {
        try {
            Module diagModule = modelSynchronizer.getDiagramModule();
            yangDiagramEditor.setSourceElementCreator(modelSynchronizer.getSourceElementCreator());
            YangDiagramEditorInput input = new YangDiagramEditorInput(URI.createURI("tmp:/local"),
                    "com.cisco.yangide.ext.model.editor.editorDiagramTypeProvider", diagModule);
            addPage(1, yangDiagramEditor, input);
            setPageText(1, "Diagram");
        } catch (PartInitException e) {
            YangEditorPlugin.log(e);
        }
    }

    private void initSourcePage() {
        try {
            addPage(0, yangSourceEditor, getEditorInput());
            setPageText(0, "Source");
        } catch (PartInitException e) {
            YangEditorPlugin.log(e);
        }
        setPartName(yangSourceEditor.getPartName());
    }

    @Override
    protected void pageChange(int newPageIndex) {
        if (newPageIndex == 1) {
            modelSynchronizer.syncWithSource();
        }
        super.pageChange(newPageIndex);
    }
}
