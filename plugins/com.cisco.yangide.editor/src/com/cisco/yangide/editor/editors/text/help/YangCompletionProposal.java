/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */ 
package com.cisco.yangide.editor.editors.text.help;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension5;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.cisco.yangide.editor.YangEditorPlugin;

/**
 * Copy-pasted from {@link CompletionProposal} and augmented for needs of YANG.
 * 
 * @author Kirill Karmakulov
 * @date 10 Oct 2014
 */
public class YangCompletionProposal implements ICompletionProposal, ICompletionProposalExtension5 {

    /** The string to be displayed in the completion proposal popup. */
    private final String fDisplayString;
    /** The replacement string. */
    private final String fReplacementString;
    /** The replacement offset. */
    private final int fReplacementOffset;
    /** The replacement length. */
    private final int fReplacementLength;
    /** The cursor position after this proposal has been applied. */
    private final int fCursorPosition;
    /** The image to be displayed in the completion proposal popup. */
    private final Image fImage;
    /** Generator of the additional info of this proposal. */
    private final IProposalHelpGenerator fAdditionalInfoGenerator;
    private String cachedAdditionalInfo = null;

    /**
     * Constructor for {@link YangCompletionProposal} with a complex additional information, which
     * is generated on demand by {@code additionalInfoGenerator}. This additional information is 
     * generated on demand.
     */
    public YangCompletionProposal(String replacementString, int replacementOffset, int replacementLength,
            int cursorPosition, Image image, String displayString, IProposalHelpGenerator additionalInfoGenerator) 
    {
        Assert.isNotNull(replacementString);
        Assert.isTrue(replacementOffset >= 0);
        Assert.isTrue(replacementLength >= 0);
        Assert.isTrue(cursorPosition >= 0);

        fReplacementString = replacementString;
        fReplacementOffset = replacementOffset;
        fReplacementLength = replacementLength;
        fCursorPosition = cursorPosition;
        fImage = image;
        fDisplayString = displayString;
        fAdditionalInfoGenerator = additionalInfoGenerator;
    }

    /**
     * Constructor for {@link YangCompletionProposal} with a low-cost additional information.
     */
    public YangCompletionProposal(String replacementString, int replacementOffset, int replacementLength,
            int cursorPosition, Image image, String displayString, String additionalInfo) 
    {
        this(replacementString, replacementOffset, replacementLength, cursorPosition, image, displayString,
                (IProposalHelpGenerator) null);
        // This is the catch of the constructor
        this.cachedAdditionalInfo = additionalInfo;
    }

    /**
     * Constructor for {@link YangCompletionProposal} with no additional information.
     */
    public YangCompletionProposal(String replacementString, int replacementOffset,
            int replacementLength, int cursorPosition, Image image, String displayString) 
    {
        this(replacementString, replacementOffset, replacementLength, cursorPosition, image, displayString,
                (IProposalHelpGenerator) null);
    }

    @Override
    public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
        if (cachedAdditionalInfo == null && fAdditionalInfoGenerator != null) {
            try {
                cachedAdditionalInfo = fAdditionalInfoGenerator.getAdditionalInfo(this, monitor);
            } catch (Exception ex) {
                YangEditorPlugin.log(ex);
            }
        }
        return cachedAdditionalInfo;
    }

    @Override
    public void apply(IDocument document) {
        try {
            document.replace(fReplacementOffset, fReplacementLength, fReplacementString);
        } catch (BadLocationException x) {
            // ignore
        }
    }

    @Override
    public Point getSelection(IDocument document) {
        return new Point(fReplacementOffset + fCursorPosition, 0);
    }

    @Override
    public String getAdditionalProposalInfo() {
        return getAdditionalProposalInfo(new NullProgressMonitor()).toString();
    }

    @Override
    public String getDisplayString() {
        if (fDisplayString != null)
            return fDisplayString;
        return fReplacementString;
    }

    @Override
    public Image getImage() {
        return fImage;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }
}
