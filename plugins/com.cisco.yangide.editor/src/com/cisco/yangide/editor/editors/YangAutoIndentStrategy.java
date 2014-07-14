/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.DocumentRewriteSessionType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.texteditor.ITextEditorExtension3;

import com.cisco.yangide.editor.YangEditorPlugin;
import com.cisco.yangide.editor.editors.text.Symbols;
import com.cisco.yangide.editor.editors.text.YangHeuristicScanner;
import com.cisco.yangide.editor.editors.text.YangIndenter;
import com.cisco.yangide.editor.preferences.YangDocumentSetupParticipant;
import com.cisco.yangide.ui.YangUIPlugin;
import com.cisco.yangide.ui.preferences.YangPreferenceConstants;

/**
 * Auto indent strategy sensitive to brackets.
 * 
 * @author Alexey Kholupko
 */
public class YangAutoIndentStrategy extends DefaultIndentLineAutoEditStrategy {

    /** The line comment introducer. Value is "{@value} " */
    private static final String LINE_COMMENT = "//"; //$NON-NLS-1$

    private static final int DEFAULT_TAB_WIDTH = 2;

    private boolean fCloseBrace;
    private boolean fIsSmartMode;
    private boolean fIsSmartTab;
    private boolean fIsSmartIndentAfterNewline;

    private String fPartitioning;
    /**
     * The viewer.
     */
    private final ISourceViewer fViewer;

    /**
     * Creates a new YANG auto indent strategy for the given document partitioning.
     * 
     * @param partitioning the document partitioning
     * @param viewer the source viewer that this strategy is attached to
     */
    public YangAutoIndentStrategy(String partitioning, ISourceViewer viewer) {
        fPartitioning = partitioning;
        fViewer = viewer;
    }

    private int getBracketCount(IDocument d, int startOffset, int endOffset, boolean ignoreCloseBrackets)
            throws BadLocationException {

        int bracketCount = 0;
        while (startOffset < endOffset) {
            char curr = d.getChar(startOffset);
            startOffset++;
            switch (curr) {
            case '/':
                if (startOffset < endOffset) {
                    char next = d.getChar(startOffset);
                    if (next == '*') {
                        // a comment starts, advance to the comment end
                        startOffset = getCommentEnd(d, startOffset + 1, endOffset);
                    } else if (next == '/') {
                        // '//'-comment: nothing to do anymore on this line
                        startOffset = endOffset;
                    }
                }
                break;
            case '*':
                if (startOffset < endOffset) {
                    char next = d.getChar(startOffset);
                    if (next == '/') {
                        // we have been in a comment: forget what we read before
                        bracketCount = 0;
                        startOffset++;
                    }
                }
                break;
            case '{':
                bracketCount++;
                ignoreCloseBrackets = false;
                break;
            case '}':
                if (!ignoreCloseBrackets) {
                    bracketCount--;
                }
                break;
            case '"':
            case '\'':
                startOffset = getStringEnd(d, startOffset, endOffset, curr);
                break;
            default:
            }
        }
        return bracketCount;
    }

    // ----------- bracket counting ------------------------------------------------------

    private int getCommentEnd(IDocument d, int offset, int endOffset) throws BadLocationException {
        while (offset < endOffset) {
            char curr = d.getChar(offset);
            offset++;
            if (curr == '*') {
                if (offset < endOffset && d.getChar(offset) == '/') {
                    return offset + 1;
                }
            }
        }
        return endOffset;
    }

    private String getIndentOfLine(IDocument d, int line) throws BadLocationException {
        if (line > -1) {
            int start = d.getLineOffset(line);
            int end = start + d.getLineLength(line) - 1;
            int whiteEnd = findEndOfWhiteSpace(d, start, end);
            return d.get(start, whiteEnd - start);
        } else {
            return ""; //$NON-NLS-1$
        }
    }

    private int getStringEnd(IDocument d, int offset, int endOffset, char ch) throws BadLocationException {
        while (offset < endOffset) {
            char curr = d.getChar(offset);
            offset++;
            if (curr == '\\') {
                // ignore escaped characters
                offset++;
            } else if (curr == ch) {
                return offset;
            }
        }
        return endOffset;
    }

    private void smartIndentAfterClosingBracket(IDocument d, DocumentCommand c) {
        if (c.offset == -1 || d.getLength() == 0)
            return;

        try {

            int p = (c.offset == d.getLength() ? c.offset - 1 : c.offset);
            int line = d.getLineOfOffset(p);
            int start = d.getLineOffset(line);
            int whiteend = findEndOfWhiteSpace(d, start, c.offset);

            YangHeuristicScanner scanner = new YangHeuristicScanner(d);
            YangIndenter indenter = new YangIndenter(d, scanner);

            // shift only when line does not contain any text up to the closing bracket
            if (whiteend == c.offset) { // evaluate the line with the opening bracket that matches
                                        // out closing bracket

                int reference = indenter.findReferencePosition(c.offset, false, true, false, false);
                int indLine = d.getLineOfOffset(reference);
                if (indLine != -1 && indLine != line) { // take the indent of the found line
                    StringBuffer replaceText = new StringBuffer(getIndentOfLine(d, indLine));
                    // add the rest of the current line including the just added close bracket
                    replaceText.append(d.get(whiteend, c.offset - whiteend));
                    replaceText.append(c.text); // modify document command
                    c.length += c.offset - start;
                    c.offset = start;
                    c.text = replaceText.toString();
                }
            }
        } catch (BadLocationException e) {
            YangEditorPlugin.log(e);
        }
    }

    private void smartIndentAfterOpeningBracket(IDocument d, DocumentCommand c) {
        if (c.offset < 1 || d.getLength() == 0)
            return;

        YangHeuristicScanner scanner = new YangHeuristicScanner(d);

        int p = (c.offset == d.getLength() ? c.offset - 1 : c.offset);

        try {
            // current line
            int line = d.getLineOfOffset(p);
            int lineOffset = d.getLineOffset(line);

            // make sure we don't have any leading comments etc.
            if (d.get(lineOffset, p - lineOffset).trim().length() != 0)
                return;

            // line of last Java code
            int pos = scanner.findNonWhitespaceBackward(p, YangHeuristicScanner.UNBOUND);
            if (pos == -1)
                return;
            int lastLine = d.getLineOfOffset(pos);

            // only shift if the last java line is further up and is a braceless block candidate
            if (lastLine < line) {

                YangIndenter indenter = new YangIndenter(d, scanner);
                StringBuffer indent = indenter.computeIndentation(p, true);
                String toDelete = d.get(lineOffset, c.offset - lineOffset);
                if (indent != null && !indent.toString().equals(toDelete)) {
                    c.text = indent.append(c.text).toString();
                    c.length += c.offset - lineOffset;
                    c.offset = lineOffset;
                }
            }

        } catch (BadLocationException e) {
            YangEditorPlugin.log(e);
        }

    }

    private void smartIndentAfterNewLine(IDocument d, DocumentCommand c) {
        YangHeuristicScanner scanner = new YangHeuristicScanner(d);
        YangIndenter indenter = new YangIndenter(d, scanner);
        StringBuffer indent = indenter.computeIndentation(c.offset);
        if (indent == null)
            indent = new StringBuffer();

        // indent.append("    ");

        int docLength = d.getLength();
        if (c.offset == -1 || docLength == 0)
            return;

        try {
            int p = (c.offset == docLength ? c.offset - 1 : c.offset);
            int line = d.getLineOfOffset(p);

            StringBuffer buf = new StringBuffer(c.text + indent);

            IRegion reg = d.getLineInformation(line);
            int lineEnd = reg.getOffset() + reg.getLength();

            int contentStart = findEndOfWhiteSpace(d, c.offset, lineEnd);
            c.length = Math.max(contentStart - c.offset, 0);

            int start = reg.getOffset();

            // insert closing brace on new line after an unclosed opening brace
            if (getBracketCount(d, start, c.offset, true) > 0 && closeBrace() && !isClosed(d, c.offset, c.length)) {
                c.caretOffset = c.offset + buf.length();
                c.shiftsCaret = false;

                // copy old content of line behind insertion point to new line

                if (c.offset == 0) {
                    if (lineEnd - contentStart > 0) {
                        c.length = lineEnd - c.offset;
                        buf.append(d.get(contentStart, lineEnd - contentStart).toCharArray());
                    }
                }

                buf.append(TextUtilities.getDefaultLineDelimiter(d));
                StringBuffer reference = null;
                int nonWS = findEndOfWhiteSpace(d, start, lineEnd);
                if (nonWS < c.offset && d.getChar(nonWS) == '{')
                    reference = new StringBuffer(d.get(start, nonWS - start));
                else
                    reference = indenter.getReferenceIndentation(c.offset);
                if (reference != null)
                    buf.append(reference);
                buf.append('}');
            }
            // insert extra line upon new line between two braces
            else if (c.offset > start && contentStart < lineEnd && d.getChar(contentStart) == '}') {
                int firstCharPos = scanner.findNonWhitespaceBackward(c.offset - 1, start);
                if (firstCharPos != YangHeuristicScanner.NOT_FOUND && d.getChar(firstCharPos) == '{') {
                    c.caretOffset = c.offset + buf.length();
                    c.shiftsCaret = false;

                    StringBuffer reference = null;
                    int nonWS = findEndOfWhiteSpace(d, start, lineEnd);
                    if (nonWS < c.offset && d.getChar(nonWS) == '{')
                        reference = new StringBuffer(d.get(start, nonWS - start));
                    else
                        reference = indenter.getReferenceIndentation(c.offset);

                    buf.append(TextUtilities.getDefaultLineDelimiter(d));

                    if (reference != null)
                        buf.append(reference);
                }
            }
            c.text = buf.toString();

        } catch (BadLocationException e) {
            YangEditorPlugin.log(e);
        }
    }

    /*
     * @see org.eclipse.jdt.internal.ui.text.java.JavaAutoIndentStrategy#isClosed
     */
    private boolean isClosed(IDocument document, int offset, int length) {

        return getBlockBalance(document, offset, fPartitioning) <= 0;

    }

    private void smartPaste(IDocument document, DocumentCommand command) {
        int newOffset = command.offset;
        int newLength = command.length;
        String newText = command.text;

        try {
            YangHeuristicScanner scanner = new YangHeuristicScanner(document);
            YangIndenter indenter = new YangIndenter(document, scanner);
            int offset = newOffset;

            // reference position to get the indent from
            int refOffset = indenter.findReferencePosition(offset);
            if (refOffset == YangHeuristicScanner.NOT_FOUND)
                return;
            int peerOffset = getPeerPosition(document, command);
            peerOffset = indenter.findReferencePosition(peerOffset);
            if (peerOffset != YangHeuristicScanner.NOT_FOUND)
                refOffset = Math.min(refOffset, peerOffset);

            // eat any WS before the insertion to the beginning of the line
            int firstLine = 1; // don't format the first line per default, as it has other content
                               // before it
            IRegion line = document.getLineInformationOfOffset(offset);
            String notSelected = document.get(line.getOffset(), offset - line.getOffset());
            if (notSelected.trim().length() == 0) {
                newLength += notSelected.length();
                newOffset = line.getOffset();
                firstLine = 0;
            }

            // prefix: the part we need for formatting but won't paste
            IRegion refLine = document.getLineInformationOfOffset(refOffset);
            String prefix = document.get(refLine.getOffset(), newOffset - refLine.getOffset());

            // handle the indentation computation inside a temporary document
            Document temp = new Document(prefix + newText);
            DocumentRewriteSession session = temp.startRewriteSession(DocumentRewriteSessionType.STRICTLY_SEQUENTIAL);
            scanner = new YangHeuristicScanner(temp);
            indenter = new YangIndenter(temp, scanner);
            installYangStuff(temp);

            // indent the first and second line
            // compute the relative indentation difference from the second line
            // (as the first might be partially selected) and use the value to
            // indent all other lines.
            boolean isIndentDetected = false;
            StringBuffer addition = new StringBuffer();
            int insertLength = 0;
            int firstLineInsertLength = 0;
            int firstLineIndent = 0;
            int first = document.computeNumberOfLines(prefix) + firstLine; // don't format first
                                                                           // line
            int lines = temp.getNumberOfLines();
            int tabLength = getVisualTabLengthPreference();
            boolean changed = false;
            for (int l = first; l < lines; l++) { // we don't change the number of lines while
                                                  // adding indents

                IRegion r = temp.getLineInformation(l);
                int lineOffset = r.getOffset();
                int lineLength = r.getLength();

                if (lineLength == 0) // don't modify empty lines
                    continue;

                if (!isIndentDetected) {

                    // indent the first pasted line
                    String current = getCurrentIndent(temp, l);
                    StringBuffer correct = indenter.computeIndentation(lineOffset);
                    if (correct == null)
                        return; // bail out

                    insertLength = subtractIndent(correct, current, addition, tabLength);
                    if (l == first) {
                        firstLineInsertLength = insertLength;
                        firstLineIndent = current.length();
                    }
                    if (l != first && temp.get(lineOffset, lineLength).trim().length() != 0) {
                        isIndentDetected = true;
                        if (firstLineIndent >= current.length())
                            insertLength = firstLineInsertLength;
                        if (insertLength == 0) {
                            // no adjustment needed, bail out
                            if (firstLine == 0) {
                                // but we still need to adjust the first line
                                command.offset = newOffset;
                                command.length = newLength;
                                if (changed)
                                    break; // still need to get the leading indent of the first line
                            }
                            return;
                        }
                    } else {
                        changed = insertLength != 0;
                    }
                }

                // relatively indent all pasted lines
                if (insertLength > 0)
                    addIndent(temp, l, addition, tabLength);
                else if (insertLength < 0)
                    cutIndent(temp, l, -insertLength, tabLength);

            }

            removeYangStuff(temp);
            temp.stopRewriteSession(session);
            newText = temp.get(prefix.length(), temp.getLength() - prefix.length());

            command.offset = newOffset;
            command.length = newLength;
            command.text = newText;

        } catch (BadLocationException e) {
            YangEditorPlugin.log(e);
        }

    }

    private void installYangStuff(Document temp) {
        YangDocumentSetupParticipant setupParticipant = new YangDocumentSetupParticipant();
        setupParticipant.setup(temp);

    }

    private static void removeYangStuff(Document document) {
        document.setDocumentPartitioner(YangDocumentSetupParticipant.YANG_PARTITIONING, null);
    }

    /**
     * Returns the indentation of the line <code>line</code> in <code>document</code>. The returned
     * string may contain pairs of leading slashes that are considered part of the indentation.
     * 
     */
    private static String getCurrentIndent(Document document, int line) throws BadLocationException {
        IRegion region = document.getLineInformation(line);
        int from = region.getOffset();
        int endOffset = region.getOffset() + region.getLength();

        // go behind line comments
        int to = from;
        while (to < endOffset - 2 && document.get(to, 2).equals(LINE_COMMENT))
            to += 2;

        while (to < endOffset) {
            char ch = document.getChar(to);
            if (!Character.isWhitespace(ch))
                break;
            to++;
        }

        // don't count the space before javadoc like, asterisk-style comment lines
        if (to > from && to < endOffset - 1 && document.get(to - 1, 2).equals(" *")) { //$NON-NLS-1$
            String type = TextUtilities.getContentType(document, YangDocumentSetupParticipant.YANG_PARTITIONING, to,
                    true);
            if (type.equals(YangPartitionScanner.YANG_COMMENT))
                to--;
        }

        return document.get(from, to - from);
    }

    /**
     * Computes the difference of two indentations and returns the difference in length of current
     * and correct. If the return value is positive, <code>addition</code> is initialized with a
     * substring of that length of <code>correct</code>.
     * 
     */
    private int subtractIndent(CharSequence correctIndentation, CharSequence currentIndentation, StringBuffer difference, int tabLength) {
        int c1 = computeVisualLength(correctIndentation, tabLength);
        int c2 = computeVisualLength(currentIndentation, tabLength);
        int diff = c1 - c2;
        if (diff <= 0)
            return diff;

        difference.setLength(0);
        int len = 0, i = 0;
        while (len < diff) {
            char c = correctIndentation.charAt(i++);
            difference.append(c);
            len += computeVisualLength(c, tabLength);
        }

        return diff;
    }

    /**
     * Indents line <code>line</code> in <code>document</code> with <code>indent</code>. Leaves
     * leading comment signs alone.
     * 
     */
    private void addIndent(Document document, int line, CharSequence indent, int tabLength) throws BadLocationException {
        IRegion region = document.getLineInformation(line);
        int insert = region.getOffset();
        int endOffset = region.getOffset() + region.getLength();

        // Compute insert after all leading line comment markers
        int newInsert = insert;
        while (newInsert < endOffset - 2 && document.get(newInsert, 2).equals(LINE_COMMENT))
            newInsert += 2;

        // Heuristic to check whether it is commented code or just a comment
        if (newInsert > insert) {
            int whitespaceCount = 0;
            int i = newInsert;
            while (i < endOffset - 1) {
                char ch = document.get(i, 1).charAt(0);
                if (!Character.isWhitespace(ch))
                    break;
                whitespaceCount = whitespaceCount + computeVisualLength(ch, tabLength);
                i++;
            }

            // TODO
            if (whitespaceCount != 0)// && whitespaceCount >=
                                     // CodeFormatterUtil.getIndentWidth(fProject))
                insert = newInsert;
        }

        // Insert indent
        document.replace(insert, 0, indent.toString());
    }

    /**
     * Cuts the visual equivalent of <code>toDelete</code> characters out of the indentation of line
     * <code>line</code> in <code>document</code>. Leaves leading comment signs alone.
     * 
     */
    private void cutIndent(Document document, int line, int toDelete, int tabLength) throws BadLocationException {
        IRegion region = document.getLineInformation(line);
        int from = region.getOffset();
        int endOffset = region.getOffset() + region.getLength();

        // go behind line comments
        while (from < endOffset - 2 && document.get(from, 2).equals(LINE_COMMENT))
            from += 2;

        int to = from;
        while (toDelete > 0 && to < endOffset) {
            char ch = document.getChar(to);
            if (!Character.isWhitespace(ch))
                break;
            toDelete -= computeVisualLength(ch, tabLength);
            if (toDelete >= 0)
                to++;
            else
                break;
        }

        document.replace(from, to - from, ""); //$NON-NLS-1$
    }

    /**
     * Returns the visual length of a given <code>CharSequence</code> taking into account the visual
     * tabulator length.
     * 
     */
    private int computeVisualLength(CharSequence seq, int tabLength) {
        int size = 0;

        for (int i = 0; i < seq.length(); i++) {
            char ch = seq.charAt(i);
            if (ch == '\t') {
                if (tabLength != 0)
                    size += tabLength - size % tabLength;
                // else: size stays the same
            } else {
                size++;
            }
        }
        return size;
    }

    /**
     * Returns the visual length of a given character taking into account the visual tabulator
     * length.
     * 
     */
    private int computeVisualLength(char ch, int tabLength) {
        if (ch == '\t')
            return tabLength;
        else
            return 1;
    }

    /**
     * The preference setting for the visual tabulator display.
     * 
     */
    private int getVisualTabLengthPreference() {
        // return CodeFormatterUtil.getTabWidth(fProject);
        return DEFAULT_TAB_WIDTH;
    }

    private boolean isLineDelimiter(IDocument document, String text) {
        String[] delimiters = document.getLegalLineDelimiters();
        if (delimiters != null)
            return TextUtilities.equals(delimiters, text) > -1;
        return false;
    }

    private void smartIndentOnKeypress(IDocument document, DocumentCommand command) {
        switch (command.text.charAt(0)) {
        case '}':
            smartIndentAfterClosingBracket(document, command);
            break;
        case '{':
            smartIndentAfterOpeningBracket(document, command);
            break;
        }
    }

    /*
     * @see
     * org.eclipse.jface.text.IAutoIndentStrategy#customizeDocumentCommand(org.eclipse.jface.text
     * .IDocument, org.eclipse.jface.text.DocumentCommand)
     */
    @Override
    public void customizeDocumentCommand(IDocument d, DocumentCommand c) {
        if (c.doit == false)
            return;

        clearCachedValues();

        if (c.length == 0 && c.text != null && isLineDelimiter(d, c.text)) {
            if (fIsSmartIndentAfterNewline)
                smartIndentAfterNewLine(d, c);
        } else if (c.text.length() == 1)
            smartIndentOnKeypress(d, c);
        else if (c.text.length() > 1 && getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_SMART_PASTE))
            if (fViewer == null || fViewer.getTextWidget() == null || !fViewer.getTextWidget().getBlockSelection())
                smartPaste(d, c); // no smart backspace for paste
    }

    private static IPreferenceStore getPreferenceStore() {
        return YangUIPlugin.getDefault().getPreferenceStore();
    }

    private boolean closeBrace() {
        return fCloseBrace;
    }

    private void clearCachedValues() {
        IPreferenceStore preferenceStore = getPreferenceStore();
        fCloseBrace = preferenceStore.getBoolean(YangPreferenceConstants.EDITOR_CLOSE_BRACES);
        fIsSmartTab = preferenceStore.getBoolean(YangPreferenceConstants.EDITOR_SMART_TAB);
        fIsSmartIndentAfterNewline = preferenceStore
                .getBoolean(YangPreferenceConstants.EDITOR_SMART_INDENT_AFTER_NEWLINE);
        fIsSmartMode = computeSmartMode();
    }

    private boolean computeSmartMode() {
        IWorkbenchPage page = YangEditorPlugin.getActivePage();
        if (page != null) {
            IEditorPart part = page.getActiveEditor();
            if (part instanceof ITextEditorExtension3) {
                ITextEditorExtension3 extension = (ITextEditorExtension3) part;
                return extension.getInsertMode() == ITextEditorExtension3.SMART_INSERT;
            }
        }
        return false;
    }

    /**
     * Returns the block balance, i.e. zero if the blocks are balanced at <code>offset</code>, a
     * negative number if there are more closing than opening braces, and a positive number if there
     * are more opening than closing braces.
     * 
     */
    private static int getBlockBalance(IDocument document, int offset, String partitioning) {
        if (offset < 1)
            return -1;
        if (offset >= document.getLength())
            return 1;

        int begin = offset;
        int end = offset - 1;

        YangHeuristicScanner scanner = new YangHeuristicScanner(document);

        while (true) {
            begin = scanner.findOpeningPeer(begin - 1, '{', '}');
            end = scanner.findClosingPeer(end + 1, '{', '}');
            if (begin == -1 && end == -1)
                return 0;
            if (begin == -1)
                return -1;
            if (end == -1)
                return 1;
        }
    }

    private int getPeerPosition(IDocument document, DocumentCommand command) {
        if (document.getLength() == 0)
            return 0;
        /*
         * Search for scope closers in the pasted text and find their opening peers in the document.
         */
        Document pasted = new Document(command.text);
        installYangStuff(pasted);
        int firstPeer = command.offset;

        YangHeuristicScanner pScanner = new YangHeuristicScanner(pasted);
        YangHeuristicScanner dScanner = new YangHeuristicScanner(document);

        // add scope relevant after context to peer search
        int afterToken = dScanner.nextToken(command.offset + command.length, YangHeuristicScanner.UNBOUND);
        try {
            switch (afterToken) {
            case Symbols.TokenRBRACE:
                pasted.replace(pasted.getLength(), 0, "}"); //$NON-NLS-1$
                break;
            case Symbols.TokenRPAREN:
                pasted.replace(pasted.getLength(), 0, ")"); //$NON-NLS-1$
                break;
            case Symbols.TokenRBRACKET:
                pasted.replace(pasted.getLength(), 0, "]"); //$NON-NLS-1$
                break;
            }
        } catch (BadLocationException e) {
            // cannot happen
            Assert.isTrue(false);
        }

        int pPos = 0; // paste text position (increasing from 0)
        int dPos = Math.max(0, command.offset - 1); // document position (decreasing from paste
                                                    // offset)
        while (true) {
            int token = pScanner.nextToken(pPos, YangHeuristicScanner.UNBOUND);
            pPos = pScanner.getPosition();
            switch (token) {
            case Symbols.TokenLBRACE:
            case Symbols.TokenLBRACKET:
            case Symbols.TokenLPAREN:
                pPos = skipScope(pScanner, pPos, token);
                if (pPos == YangHeuristicScanner.NOT_FOUND)
                    return firstPeer;
                break; // closed scope -> keep searching
            case Symbols.TokenRBRACE:
                int peer = dScanner.findOpeningPeer(dPos, '{', '}');
                dPos = peer - 1;
                if (peer == YangHeuristicScanner.NOT_FOUND)
                    return firstPeer;
                firstPeer = peer;
                break; // keep searching
            case Symbols.TokenRBRACKET:
                peer = dScanner.findOpeningPeer(dPos, '[', ']');
                dPos = peer - 1;
                if (peer == YangHeuristicScanner.NOT_FOUND)
                    return firstPeer;
                firstPeer = peer;
                break; // keep searching
            case Symbols.TokenRPAREN:
                peer = dScanner.findOpeningPeer(dPos, '(', ')');
                dPos = peer - 1;
                if (peer == YangHeuristicScanner.NOT_FOUND)
                    return firstPeer;
                firstPeer = peer;
                break; // keep searching
            case Symbols.TokenEOF:
                return firstPeer;
            default:
                // keep searching
            }
        }
    }

    /**
     * Skips the scope opened by <code>token</code>.
     *
     */
    private static int skipScope(YangHeuristicScanner scanner, int startPosition, int token) {
        int openToken = token;
        int closeToken;
        switch (token) {
        case Symbols.TokenLPAREN:
            closeToken = Symbols.TokenRPAREN;
            break;
        case Symbols.TokenLBRACKET:
            closeToken = Symbols.TokenRBRACKET;
            break;
        case Symbols.TokenLBRACE:
            closeToken = Symbols.TokenRBRACE;
            break;
        default:
            Assert.isTrue(false);
            return -1; // dummy
        }

        int depth = 1;
        int p = startPosition;

        while (true) {
            int tok = scanner.nextToken(p, YangHeuristicScanner.UNBOUND);
            p = scanner.getPosition();

            if (tok == openToken) {
                depth++;
            } else if (tok == closeToken) {
                depth--;
                if (depth == 0)
                    return p + 1;
            } else if (tok == Symbols.TokenEOF) {
                return YangHeuristicScanner.NOT_FOUND;
            }
        }
    }

}
