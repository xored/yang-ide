/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.preferences;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;

import com.cisco.yangide.editor.editors.YangPartitionScanner;

/**
 * @author Alexey Kholupko
 */
public class YangDocumentSetupParticipant implements IDocumentSetupParticipant {

    public final static String YANG_PARTITIONING = "com.cisco.yangide.editor.ui.yangPartitioning"; //$NON-NLS-1$

    public YangDocumentSetupParticipant() {
    }

    @Override
    public void setup(IDocument document) {
        if (document instanceof IDocumentExtension3) {
            IDocumentExtension3 extension3 = (IDocumentExtension3) document;
            IDocumentPartitioner partitioner = createDocumentPartitioner();
            extension3.setDocumentPartitioner(YANG_PARTITIONING, partitioner);
            partitioner.connect(document);
        }
    }

    public static IDocumentPartitioner createDocumentPartitioner() {
        return new FastPartitioner(new YangPartitionScanner(), new String[] { YangPartitionScanner.YANG_COMMENT,
                YangPartitionScanner.YANG_STRING, YangPartitionScanner.YANG_STRING_SQ });
    }
}
