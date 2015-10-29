/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.model.editor.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;

import com.cisco.yangide.ext.model.Module;

/**
 * @author Konstantin Zaitsev
 * @date Aug 7, 2014
 */
public class YangDiagramEditorInput extends DiagramEditorInput {

    private Module module;
    private IFile file;

    /**
     * @param diagramUri
     * @param providerId
     */
    public YangDiagramEditorInput(URI diagramUri, IFile file, String providerId, Module m) {
        super(diagramUri, providerId);
        this.file = file;
        this.module = m;
    }

    /**
     * @return the module
     */
    public Module getModule() {
        return module;
    }

    public IFile getFile() {
        return file;
    }
    
    
}
