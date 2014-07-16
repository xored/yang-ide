/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;

import com.cisco.yangide.editor.editors.text.YangAnnotationModel;
import com.cisco.yangide.editor.preferences.YangDocumentSetupParticipant;

/**
 * @author Alexey Kholupko
 */
public class YangDocumentProvider extends FileDocumentProvider {

    @Override
    protected IDocument createDocument(Object element) throws CoreException {
        IDocument document = super.createDocument(element);

        if (document != null) {
            IDocumentSetupParticipant partiticipant = new YangDocumentSetupParticipant();
            partiticipant.setup(document);
        }
        return document;
    }

    @Override
    protected IAnnotationModel createAnnotationModel(Object element) throws CoreException {
        if (element instanceof IFileEditorInput) {
            IFileEditorInput input = (IFileEditorInput) element;
            return new YangAnnotationModel(input.getFile());
        }
        return super.createAnnotationModel(element);
    }
}