/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors.text.help;

import org.eclipse.core.runtime.IProgressMonitor;

import com.cisco.yangide.core.indexing.ElementIndexInfo;
import com.cisco.yangide.core.indexing.ElementIndexType;

/**
 * Contains factory methods that create generators of additional information for a proposal backed
 * by an indexed element.
 * 
 * @author Kirill Karmakulov
 * @date Oct 13, 2014
 */
public class IndexInfoProposalHelpGenerator implements IProposalHelpGenerator {

    protected final ElementIndexInfo info;
    protected final ElementIndexType type;

    protected IndexInfoProposalHelpGenerator(ElementIndexInfo info, ElementIndexType type) {
        this.info = info;
        this.type = type;
    }

    /**
     * @see HelpCompositionUtils#getIndexedInfo(String, String, String, ElementIndexType)
     */
    @Override
    public String getAdditionalInfo(YangCompletionProposal proposal, IProgressMonitor monitor) {
        return HelpCompositionUtils.getIndexedInfo(info.getName(), info.getModule(), info.getRevision(), type);
    }

    /**
     * @return an implementation of {@link IProposalHelpGenerator} that generates quick help for
     * YANG 'type' definition
     */
    public static IProposalHelpGenerator type(ElementIndexInfo info) {
        return new IndexInfoProposalHelpGenerator(info, ElementIndexType.TYPE);
    }

    /**
     * @return an implementation of {@link IProposalHelpGenerator} that generates quick help for
     * YANG 'grouping' definition
     */
    public static IProposalHelpGenerator grouping(ElementIndexInfo info) {
        return new IndexInfoProposalHelpGenerator(info, ElementIndexType.GROUPING);
    }

    /**
     * @return an implementation of {@link IProposalHelpGenerator} that generates quick help for
     * YANG 'identity' definition
     */
    public static IProposalHelpGenerator identity(ElementIndexInfo info) {
        return new IndexInfoProposalHelpGenerator(info, ElementIndexType.IDENTITY);
    }

    /**
     * @return an implementation of {@link IProposalHelpGenerator} that generates quick help for
     * YANG module definition
     */
    public static IProposalHelpGenerator module(ElementIndexInfo info) {
        return new IndexInfoProposalHelpGenerator(info, ElementIndexType.MODULE);
    }

    /**
     * @return an implementation of {@link IProposalHelpGenerator} that generates quick help for
     * YANG sub-module definition
     */
    public static IProposalHelpGenerator submodule(ElementIndexInfo info) {
        return new IndexInfoProposalHelpGenerator(info, ElementIndexType.SUBMODULE);
    }
}
