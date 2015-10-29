/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.editor.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.cisco.yangide.editor.editors.text.YangAnnotationModel;
import com.cisco.yangide.editor.preferences.YangDocumentSetupParticipant;

/**
 * @author Konstantin Zaitsev
 * @date Aug 5, 2014
 */
public class YangDocumentProvider extends TextFileDocumentProvider {

    private YangDocumentSetupParticipant documentSetupParticipant;

    public YangDocumentProvider() {
        IDocumentProvider provider = new TextFileDocumentProvider(new YangStorageDocumentProvider());
        setParentDocumentProvider(provider);
        documentSetupParticipant = new YangDocumentSetupParticipant();
    }

    @Override
    protected IAnnotationModel createAnnotationModel(IFile file) {
        return new YangAnnotationModel(file);
    }

    @Override
    protected FileInfo createFileInfo(Object element) throws CoreException {
        FileInfo info = super.createFileInfo(element);
        if (info != null) {
            IDocument document = info.fTextFileBuffer.getDocument();
            if (document instanceof IDocumentExtension3) {
                IDocumentExtension3 extension = (IDocumentExtension3) document;
                if (extension.getDocumentPartitioner(YangDocumentSetupParticipant.YANG_PARTITIONING) == null) {
                    documentSetupParticipant.setup(document);
                }
            }
        }
        return info;
    }

}