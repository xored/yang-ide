/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */ 
package com.cisco.yangide.editor.editors;

/**
 * @author Alexey Kholupko
 *
 */

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistantExtension2;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import com.cisco.yangide.editor.YangEditorPlugin;

/**
 * Example Java completion processor.
 */
public class YangSimpleCompletionProcessor implements IContentAssistProcessor {

    
    private final static ICompletionProposal[] NO_PROPOSALS= new ICompletionProposal[0];
    
    /**
     * Simple content assist tip closer. The tip is valid in a range
     * of 5 characters around its popup location.
     */
    protected static class Validator implements IContextInformationValidator {

        protected int fInstallOffset;

        /*
         * @see IContextInformationValidator#isContextInformationValid(int)
         */
        public boolean isContextInformationValid(int offset) {
            return Math.abs(fInstallOffset - offset) < 5;
        }

        /*
         * @see IContextInformationValidator#install(IContextInformation, ITextViewer, int)
         */
        public void install(IContextInformation info, ITextViewer viewer, int offset) {
            fInstallOffset= offset;
        }
    };

    private Comparator proposalComparator = new Comparator() {
        public int compare(Object o1, Object o2) {

            String string1 = ((ICompletionProposal) o1).getDisplayString();
            String string2 = ((ICompletionProposal) o2).getDisplayString();
            return string1.compareToIgnoreCase(string2);
        }
    };

    protected final static String[] fgProposals = YangScanner.keywords;
        //{ "abstract", "boolean", "break", "byte", "case", "catch", "char", "class", "continue", "default", "do", "double", "else", "extends", "false", "final", "finally", "float", "for", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "null", "package", "private", "protected", "public", "return", "short", "static", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "true", "try", "void", "volatile", "while" }; //$NON-NLS-48$ //$NON-NLS-47$ //$NON-NLS-46$ //$NON-NLS-45$ //$NON-NLS-44$ //$NON-NLS-43$ //$NON-NLS-42$ //$NON-NLS-41$ //$NON-NLS-40$ //$NON-NLS-39$ //$NON-NLS-38$ //$NON-NLS-37$ //$NON-NLS-36$ //$NON-NLS-35$ //$NON-NLS-34$ //$NON-NLS-33$ //$NON-NLS-32$ //$NON-NLS-31$ //$NON-NLS-30$ //$NON-NLS-29$ //$NON-NLS-28$ //$NON-NLS-27$ //$NON-NLS-26$ //$NON-NLS-25$ //$NON-NLS-24$ //$NON-NLS-23$ //$NON-NLS-22$ //$NON-NLS-21$ //$NON-NLS-20$ //$NON-NLS-19$ //$NON-NLS-18$ //$NON-NLS-17$ //$NON-NLS-16$ //$NON-NLS-15$ //$NON-NLS-14$ //$NON-NLS-13$ //$NON-NLS-12$ //$NON-NLS-11$ //$NON-NLS-10$ //$NON-NLS-9$ //$NON-NLS-8$ //$NON-NLS-7$ //$NON-NLS-6$ //$NON-NLS-5$ //$NON-NLS-4$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$

    protected IContextInformationValidator fValidator= new Validator();

    private IContentAssistantExtension2 fContentAssistant;

    private ITextViewer viewer;

    private int cursorPosition;

    private int lineNumber;

    private int columnNumber;

    private String currentPrefix = null;

    /* (non-Javadoc)
     * Method declared on IContentAssistProcessor
     */
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentOffset) {
        
        this.viewer = viewer;
        
        currentPrefix = null;
        
        ICompletionProposal[] proposals = determineProposals();
        Arrays.sort(proposals, proposalComparator);
        
        return proposals; 
        
        /*        
        ICompletionProposal[] result= new ICompletionProposal[fgProposals.length];
        for (int i= 0; i < fgProposals.length; i++) {
            IContextInformation info= new ContextInformation(fgProposals[i], MessageFormat.format("{0}", new Object[] { fgProposals[i] })); //$NON-NLS-1$
            result[i]= new CompletionProposal(fgProposals[i], documentOffset, 0, fgProposals[i].length(), info); //$NON-NLS-1$
        }
        */
    }
    
    /* (non-Javadoc)
     * Method declared on IContentAssistProcessor
     */
    public IContextInformation[] computeContextInformation(ITextViewer viewer, int documentOffset) {
        
        IContextInformation[] result= new IContextInformation[5];
        for (int i= 0; i < result.length; i++)
            result[i]= new ContextInformation(
                MessageFormat.format("{0} {1}", new Object[] { new Integer(i), new Integer(documentOffset) }),  //$NON-NLS-1$
                MessageFormat.format("{0} {1}", new Object[] { new Integer(i), new Integer(documentOffset - 5), new Integer(documentOffset + 5)})); //$NON-NLS-1$
        return result;
    }
    
    /* (non-Javadoc)
     * Method declared on IContentAssistProcessor
     */
    public char[] getCompletionProposalAutoActivationCharacters() {
        return new char[] { ':' };
    }
    
    /* (non-Javadoc)
     * Method declared on IContentAssistProcessor
     */
    public char[] getContextInformationAutoActivationCharacters() {
        return new char[] { '#' };
    }
    
    /* (non-Javadoc)
     * Method declared on IContentAssistProcessor
     */
    public IContextInformationValidator getContextInformationValidator() {
        return fValidator;
    }
    
    /* (non-Javadoc)
     * Method declared on IContentAssistProcessor
     */
    public String getErrorMessage() {
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.text.contentassist.ICompletionListener#assistSessionStarted(org.eclipse.jface.text.contentassist.ContentAssistEvent)
     */
    public void assistSessionStarted(ContentAssistEvent event) {
        IContentAssistant assistant = event.assistant;
        if (assistant instanceof IContentAssistantExtension2) {
            fContentAssistant = (IContentAssistantExtension2) assistant;
        }
    }    
    
    /**
     * Returns the new determined proposals.
     */ 
    private ICompletionProposal[] determineProposals() {
        ITextSelection selection= (ITextSelection)viewer.getSelectionProvider().getSelection();
        cursorPosition = selection.getOffset() + selection.getLength();
        
        IDocument doc = viewer.getDocument();
        try {
            lineNumber = doc.getLineOfOffset(cursorPosition);
            columnNumber = cursorPosition - doc.getLineOffset(lineNumber);
        } catch (BadLocationException e) {
            YangEditorPlugin.log(e);
        }
        
        String prefix = getCurrentPrefix();
        if (prefix == null || cursorPosition == -1) {
            return NO_PROPOSALS;
        }
    
        //ICompletionProposal[] proposals = getProposalsFromDocument(doc, prefix);
        List<ICompletionProposal> proposalsList = new ArrayList<ICompletionProposal>();
        for(String proposal : fgProposals){
            if(proposal.startsWith(prefix))
                proposalsList.add(new CompletionProposal(proposal, cursorPosition - prefix.length(), prefix.length(), proposal.length()));


        }

        return (ICompletionProposal[]) proposalsList.toArray(new ICompletionProposal[proposalsList.size()]);
    }    

    /**
     * Determines the current prefix that should be used for completion.
     */
    private String getCurrentPrefix() {
        if (currentPrefix != null) {
            return currentPrefix;
        }
        ITextSelection selection = (ITextSelection) viewer.getSelectionProvider().getSelection();
        IDocument doc = viewer.getDocument();
        return getPrefixFromDocument(doc.get(), selection.getOffset() + selection.getLength()).toLowerCase();
    }

    /**
     * Returns the prefix in the specified document text with respect to the specified offset.
     * 
     * @param aDocumentText the whole content of the edited file as String
     * @param anOffset the cursor position
     */
    protected String getPrefixFromDocument(String aDocumentText, int anOffset) {
        if (currentPrefix != null) {
            return currentPrefix;
        }
        int startOfWordToken = anOffset;

        char token = 'a';
        if (startOfWordToken > 0) {
            token = aDocumentText.charAt(startOfWordToken - 1);
        }

        while (startOfWordToken > 0
                && (Character.isJavaIdentifierPart(token) || '.' == token || '-' == token || ';' == token)
                && !('$' == token)) {
            startOfWordToken--;
            if (startOfWordToken == 0) {
                break; // word goes right to the beginning of the doc
            }
            token = aDocumentText.charAt(startOfWordToken - 1);
        }

        if (startOfWordToken != anOffset) {
            currentPrefix = aDocumentText.substring(startOfWordToken, anOffset).toLowerCase();
        } else {
            currentPrefix = "";
        }
        return currentPrefix;
    }
     
    
}
