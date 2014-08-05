/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.ui.editors.text.StorageDocumentProvider;

import com.cisco.yangide.editor.preferences.YangDocumentSetupParticipant;

/**
 * @author Alexey Kholupko
 */
public class YangStorageDocumentProvider extends StorageDocumentProvider {

    @Override
    protected void setupDocument(Object element, IDocument document) {
        if (document != null) {
            IDocumentPartitioner partitioner = createDocumentPartitioner();
            if (document instanceof IDocumentExtension3) {
                IDocumentExtension3 extension3 = (IDocumentExtension3) document;
                extension3.setDocumentPartitioner(YangDocumentSetupParticipant.YANG_PARTITIONING, partitioner);
            } else {
                document.setDocumentPartitioner(partitioner);
            }
            partitioner.connect(document);
        }
    }

    private IDocumentPartitioner createDocumentPartitioner() {
        return YangDocumentSetupParticipant.createDocumentPartitioner();
    }
}