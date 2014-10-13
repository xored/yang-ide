package com.cisco.yangide.editor.editors.text.help;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Interface for generators of additional information for {@link YangCompletionProposal}s.
 * Given an instance of a proposal an implementor of the interface must generate additional
 * information that should assist user in his choice. The additional information is
 * displayed in a separate window attached to list of proposals.
 *  
 * @author Kirill Karmakulov
 * @date   Oct 13, 2014
 */
public interface IProposalHelpGenerator {
    String getAdditionalInfo(YangCompletionProposal proposal, IProgressMonitor monitor);
}