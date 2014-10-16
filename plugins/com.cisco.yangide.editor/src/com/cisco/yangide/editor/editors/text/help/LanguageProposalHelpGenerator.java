/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors.text.help;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Generator of additional information for a proposal backed
 * by YANG language types, keywords, etc.
 * 
 * @author Kirill Karmakulov
 * @date Oct 14, 2014
 */
public class LanguageProposalHelpGenerator extends YangLanguageHelpLoader implements IProposalHelpGenerator {

    public LanguageProposalHelpGenerator(String definition, DefinitionKind kind) {
        super(definition, kind);
    }

    @Override
    public String getAdditionalInfo(YangCompletionProposal proposal, IProgressMonitor monitor) {
        return get(monitor);
    }
}
