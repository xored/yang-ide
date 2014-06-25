/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */ 
package com.cisco.yangide.editor.preferences;

/**
 * @author Alexey Kholupko
 *
 */

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;

import com.cisco.yangide.editor.editors.YANGPartitionScanner;

/**
 * The document setup participant from Ant. 
 */
public class YANGDocumentSetupParticipant  implements IDocumentSetupParticipant {

    /**
     * The name of the Ant partitioning. Again, clarify whats the purpose of partition scanner 
     * and why can it be used in preview instead of real scanne
     * @since 3.0
     */
    public final static String YANG_PARTITIONING= "com.cisco.yangide.editor.YANGPartitioning";  //$NON-NLS-1$
    
    public YANGDocumentSetupParticipant() {
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.core.filebuffers.IDocumentSetupParticipant#setup(org.eclipse.jface.text.IDocument)
     */
    public void setup(IDocument document) {
        if (document instanceof IDocumentExtension3) {
            IDocumentExtension3 extension3= (IDocumentExtension3) document;
            IDocumentPartitioner partitioner = createDocumentPartitioner();
            extension3.setDocumentPartitioner(YANG_PARTITIONING, partitioner);
            partitioner.connect(document);
        } 
    }
    
    private IDocumentPartitioner createDocumentPartitioner() {
        return new FastPartitioner(
                new YANGPartitionScanner(), new String[]{
                    YANGPartitionScanner.YANG_COMMENT,
                    YANGPartitionScanner.YANG_IDENTIFIER, 
                    YANGPartitionScanner.YANG_KEYWORD,
                    YANGPartitionScanner.YANG_STRING});
    }
}
