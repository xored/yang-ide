/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.editor.editors;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;

/**
 * @author Alexey Kholupko
 */
public class YangDoubleClickStrategy implements ITextDoubleClickStrategy {

    @Override
    public void doubleClicked(ITextViewer part) {
        ISelectionProvider selectionProvider = part.getSelectionProvider();
        if (selectionProvider != null) {
            ISelection selection = selectionProvider.getSelection();
            if (selection != null && selection instanceof ITextSelection) {
                int pos = ((ITextSelection) selection).getOffset();
                if (pos > 0) {
                    if (!selectComment(part, pos)) {
                        selectWord(part, pos);
                    }
                }
            }
        }
    }

    private boolean selectComment(ITextViewer part, int caretPos) {
        IDocument doc = part.getDocument();
        int startPos, endPos;

        try {
            int pos = caretPos;
            char c = ' ';

            while (pos >= 0) {
                c = doc.getChar(pos);
                if (c == '\\') {
                    pos -= 2;
                    continue;
                }
                if (c == Character.LINE_SEPARATOR || c == '\"') {
                    break;
                }
                --pos;
            }

            if (c != '\"') {
                return false;
            }

            startPos = pos;

            pos = caretPos;
            int length = doc.getLength();
            c = ' ';

            while (pos < length) {
                c = doc.getChar(pos);
                if (c == Character.LINE_SEPARATOR || c == '\"') {
                    break;
                }
                ++pos;
            }
            if (c != '\"') {
                return false;
            }

            endPos = pos;

            int offset = startPos + 1;
            int len = endPos - offset;
            part.setSelectedRange(offset, len);
            return true;
        } catch (BadLocationException x) {
        }

        return false;
    }

    protected boolean selectWord(ITextViewer part, int caretPos) {

        IDocument doc = part.getDocument();
        int startPos;

        try {

            int pos = caretPos;
            char c;

            while (pos >= 0) {
                c = doc.getChar(pos);
                if (!isYangIdentifierPart(c)) {
                    break;
                }
                --pos;
            }

            startPos = pos;

            pos = caretPos;
            int length = doc.getLength();

            while (pos < length) {
                c = doc.getChar(pos);
                if (!isYangIdentifierPart(c)) {
                    break;
                }
                ++pos;
            }

            part.setSelectedRange(startPos + 1, pos - startPos - 1);
            return true;

        } catch (BadLocationException x) {
        }

        return false;
    }

    private boolean isYangIdentifierPart(char c) {
        return Character.isJavaIdentifierPart(c) || c == '-' || c == ':';
    }
}