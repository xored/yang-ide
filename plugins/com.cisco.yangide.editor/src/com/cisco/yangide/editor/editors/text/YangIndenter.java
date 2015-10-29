/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.editor.editors.text;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
//import org.eclipse.jdt.internal.corext.util.CodeFormatterUtil;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;

import com.cisco.yangide.editor.YangEditorPlugin;
import com.cisco.yangide.ui.YangUIPlugin;

/**
 * Uses the {@link org.eclipse.jdt.internal.ui.text.YangHeuristicScanner} to get the indentation
 * level for a certain position in a document.
 *
 * @author Alexey Kholupko
 */
public final class YangIndenter {

    /**
     * Core preferences.
     */
    private final class CorePrefs {

        final boolean prefUseTabs;
        final int prefTabSize;
        final int prefIndentationSize;
        final boolean prefArrayDimensionsDeepIndent;
        final int prefArrayIndent;
        final boolean prefArrayDeepIndent;
        final boolean prefTernaryDeepAlign;
        final int prefTernaryIndent;
        final int prefCaseBlockIndent;
        final int prefSimpleIndent;
        final int prefBracketIndent;
        final boolean prefMethodDeclDeepIndent;
        final int prefMethodDeclIndent;
        final boolean prefMethodCallDeepIndent;
        final int prefMethodCallIndent;
        final boolean prefParenthesisDeepIndent;
        final int prefParenthesisIndent;
        final int prefBlockIndent;
        final int prefMethodBodyIndent;
        final int prefTypeIndent;
        final boolean prefIndentBracesForBlocks;
        final boolean prefIndentBracesForArrays;
        final boolean prefIndentBracesForMethods;
        final boolean prefIndentBracesForTypes;
        final int prefContinuationIndent;
        final boolean prefHasGenerics;
        final String prefTabChar;

        CorePrefs() {

            prefUseTabs = YangEditorPlugin.getDefault().getCombinedPreferenceStore()
                    .getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS);

            prefTabSize = prefTabSize();// 2;// 4;

            prefIndentationSize = prefTabSize();// 2;// 4;
            prefArrayDimensionsDeepIndent = true;

            prefContinuationIndent = prefTabSize();// 2;
            prefBlockIndent = 1;
            prefArrayIndent = prefContinuationIndent;
            prefArrayDeepIndent = true;
            prefTernaryDeepAlign = false;
            prefTernaryIndent = prefContinuationIndent;
            prefCaseBlockIndent = prefBlockIndent;
            prefIndentBracesForBlocks = false;
            prefSimpleIndent = (prefIndentBracesForBlocks && prefBlockIndent == 0) ? 1 : prefBlockIndent;
            prefBracketIndent = prefBlockIndent;
            prefMethodDeclDeepIndent = true;
            prefMethodDeclIndent = 1;
            prefMethodCallDeepIndent = false;
            prefMethodCallIndent = 1;
            prefParenthesisDeepIndent = false;
            prefParenthesisIndent = prefContinuationIndent;
            prefMethodBodyIndent = 1;
            prefTypeIndent = 1;
            prefIndentBracesForArrays = false;
            prefIndentBracesForMethods = false;
            prefIndentBracesForTypes = false;
            prefHasGenerics = false;
            prefTabChar = getPrefTabChar(); // YangCore.TAB;

        }

        private String getPrefTabChar() {
            if (YangEditorPlugin.getDefault().getCombinedPreferenceStore()
                    .getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS)) {
                return YangUIPlugin.SPACE;
            }
            return YangUIPlugin.TAB;

        }

        private int prefTabSize() {

            return YangEditorPlugin.getDefault().getCombinedPreferenceStore()
                    .getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
        }

    }

    /** The document being scanned. */
    private final IDocument fDocument;
    /** The indentation accumulated by <code>findReferencePosition</code>. */
    private int fIndent;
    /**
     * The absolute (character-counted) indentation offset for special cases (method defs, array
     * initializers)
     */
    private int fAlign;
    /** The stateful scanposition for the indentation methods. */
    private int fPosition;
    /** The previous position. */
    private int fPreviousPos;
    /** The most recent token. */
    private int fToken;
    /** The line of <code>fPosition</code>. */
    private int fLine;
    /**
     * The scanner we will use to scan the document. It has to be installed on the same document as
     * the one we get.
     */
    private final YangHeuristicScanner fScanner;
    /**
     * Core preferences.
     */
    private final CorePrefs fPrefs;

    /**
     * Creates a new instance.
     *
     * @param document the document to scan
     * @param scanner the {@link YangHeuristicScanner} to be used for scanning the document. Must be
     * installed on the same <code>IDocument</code>.
     */
    public YangIndenter(IDocument document, YangHeuristicScanner scanner) {
        Assert.isNotNull(document);
        Assert.isNotNull(scanner);
        fDocument = document;
        fScanner = scanner;
        fPrefs = new CorePrefs();
    }

    /**
     * Computes the indentation at the reference point of <code>position</code>.
     */
    public StringBuffer getReferenceIndentation(int offset) {
        return getReferenceIndentation(offset, false);
    }

    private StringBuffer getReferenceIndentation(int offset, boolean assumeOpeningBrace) {

        int unit;
        if (assumeOpeningBrace) {
            unit = findReferencePosition(offset, Symbols.TokenLBRACE);
        } else {
            unit = findReferencePosition(offset, peekChar(offset));
        }

        // if we were unable to find anything, return null
        if (unit == YangHeuristicScanner.NOT_FOUND) {
            return null;
        }

        return getLeadingWhitespace(unit);

    }

    /**
     * Computes the indentation at <code>offset</code>.
     */
    public StringBuffer computeIndentation(int offset) {
        return computeIndentation(offset, false);
    }

    public StringBuffer computeIndentation(int offset, boolean assumeOpeningBrace) {

        StringBuffer reference = getReferenceIndentation(offset, assumeOpeningBrace);

        // handle special alignment
        if (fAlign != YangHeuristicScanner.NOT_FOUND) {
            try {
                // a special case has been detected.
                IRegion line = fDocument.getLineInformationOfOffset(fAlign);
                int lineOffset = line.getOffset();
                return createIndent(lineOffset, fAlign, false);
            } catch (BadLocationException e) {
                return null;
            }
        }

        if (reference == null) {
            return null;
        }

        // add additional indent
        return createReusingIndent(reference, fIndent);
    }

    /**
     * Computes the length of a <code>CharacterSequence</code>, counting a tab character as the size
     * until the next tab stop and every other character as one.
     */
    private int computeVisualLength(CharSequence indent) {
        final int tabSize = fPrefs.prefTabSize;
        int length = 0;
        for (int i = 0; i < indent.length(); i++) {
            char ch = indent.charAt(i);
            switch (ch) {
            case '\t':
                if (tabSize > 0) {
                    int reminder = length % tabSize;
                    length += tabSize - reminder;
                }
                break;
            case ' ':
                length++;
                break;
            }
        }
        return length;
    }

    /**
     * Strips any characters off the end of <code>reference</code> that exceed
     * <code>indentLength</code>.
     */
    private StringBuffer stripExceedingChars(StringBuffer reference, int indentLength) {
        final int tabSize = fPrefs.prefTabSize;
        int measured = 0;
        int chars = reference.length();
        int i = 0;
        for (; measured < indentLength && i < chars; i++) {
            char ch = reference.charAt(i);
            switch (ch) {
            case '\t':
                if (tabSize > 0) {
                    int reminder = measured % tabSize;
                    measured += tabSize - reminder;
                }
                break;
            case ' ':
                measured++;
                break;
            }
        }
        int deleteFrom = measured > indentLength ? i - 1 : i;

        return reference.delete(deleteFrom, chars);
    }

    /**
     * Returns the indentation of the line at <code>offset</code> as a <code>StringBuffer</code>. If
     * the offset is not valid, the empty string is returned.
     */
    private StringBuffer getLeadingWhitespace(int offset) {
        StringBuffer indent = new StringBuffer();
        try {
            IRegion line = fDocument.getLineInformationOfOffset(offset);
            int lineOffset = line.getOffset();
            int nonWS = fScanner.findNonWhitespaceForwardInAnyPartition(lineOffset, lineOffset + line.getLength());
            indent.append(fDocument.get(lineOffset, nonWS - lineOffset));
            return indent;
        } catch (BadLocationException e) {
            return indent;
        }
    }

    /**
     * Creates an indentation string of the length indent - start, consisting of the content in
     * <code>fDocument</code> in the range [start, indent), with every character replaced by a space
     * except for tabs, which are kept as such.
     * <p>
     * If <code>convertSpaceRunsToTabs</code> is <code>true</code>, every run of the number of
     * spaces that make up a tab are replaced by a tab character. If it is not set, no conversion
     * takes place, but tabs in the original range are still copied verbatim.
     * </p>
     */
    private StringBuffer createIndent(int start, final int indent, final boolean convertSpaceRunsToTabs) {
        final boolean convertTabs = fPrefs.prefUseTabs && convertSpaceRunsToTabs;
        final int tabLen = fPrefs.prefTabSize;
        final StringBuffer ret = new StringBuffer();
        try {
            int spaces = 0;
            while (start < indent) {

                char ch = fDocument.getChar(start);
                if (ch == '\t') {
                    ret.append('\t');
                    spaces = 0;
                } else if (convertTabs) {
                    spaces++;
                    if (spaces == tabLen) {
                        ret.append('\t');
                        spaces = 0;
                    }
                } else {
                    ret.append(' ');
                }

                start++;
            }
            // remainder
            while (spaces-- > 0) {
                ret.append(' ');
            }

        } catch (BadLocationException e) {
        }

        return ret;
    }

    /**
     * Creates a string with a visual length of the given <code>additional</code> indentation units.
     *
     * @return the modified <code>buffer</code> reflecting the indentation adapted to
     * <code>additional</code>
     */
    private StringBuffer createReusingIndent(StringBuffer buffer, int additional) {
        int refLength = computeVisualLength(buffer);
        int addLength = fPrefs.prefIndentationSize * additional; // may be < 0
        int totalLength = Math.max(0, refLength + addLength);

        // copy the reference indentation for the indent up to the last tab
        // stop within the maxCopy area
        int minLength = Math.min(totalLength, refLength);
        int tabSize = fPrefs.prefTabSize;
        int maxCopyLength = tabSize > 0 ? minLength - minLength % tabSize : minLength; // maximum
        // indent to
        // copy
        stripExceedingChars(buffer, maxCopyLength);

        final String MIXED = "mixed"; //$NON-NLS-1$

        // add additional indent
        int missing = totalLength - maxCopyLength;
        final int tabs, spaces;
        if (YangUIPlugin.SPACE.equals(fPrefs.prefTabChar)) {
            tabs = 0;
            spaces = missing;
        } else if (YangUIPlugin.TAB.equals(fPrefs.prefTabChar)) {
            tabs = tabSize > 0 ? missing / tabSize : 0;
            spaces = tabSize > 0 ? missing % tabSize : missing;
        } else if (MIXED.equals(fPrefs.prefTabChar)) {
            tabs = tabSize > 0 ? missing / tabSize : 0;
            spaces = tabSize > 0 ? missing % tabSize : missing;
        } else {
            Assert.isTrue(false);
            return null;
        }
        for (int i = 0; i < tabs; i++) {
            buffer.append('\t');
        }
        for (int i = 0; i < spaces; i++) {
            buffer.append(' ');
        }
        return buffer;
    }

    /**
     * Returns the reference position regarding to indentation for <code>offset</code>, or
     * <code>NOT_FOUND</code>. This method calls {@link #findReferencePosition(int, int)
     * findReferencePosition(offset, nextChar)} where <code>nextChar</code> is the next character
     * after <code>offset</code>.
     */
    public int findReferencePosition(int offset) {
        return findReferencePosition(offset, peekChar(offset));
    }

    /**
     * Peeks the next char in the document that comes after <code>offset</code> on the same line as
     * <code>offset</code>.
     */
    private int peekChar(int offset) {
        if (offset < fDocument.getLength()) {
            try {
                IRegion line = fDocument.getLineInformationOfOffset(offset);
                int lineOffset = line.getOffset();
                int next = fScanner.nextToken(offset, lineOffset + line.getLength());
                return next;
            } catch (BadLocationException e) {
            }
        }
        return Symbols.TokenEOF;
    }

    /**
     * Returns the reference position regarding to indentation for <code>position</code>, or
     * <code>NOT_FOUND</code>.
     */

    public int findReferencePosition(int offset, int nextToken) {
        boolean danglingElse = false;
        boolean unindent = false;
        boolean indent = false;
        boolean matchBrace = false;
        boolean matchParen = false;
        boolean matchCase = false;
        boolean throwsClause = false;

        // account for un-indentation characters already typed in, but after position
        // if they are on a line by themselves, the indentation gets adjusted
        // accordingly
        //
        if (offset < fDocument.getLength()) {
            try {
                IRegion line = fDocument.getLineInformationOfOffset(offset);
                int lineOffset = line.getOffset();
                int prevPos = Math.max(offset - 1, 0);
                boolean isFirstTokenOnLine = fDocument.get(lineOffset, prevPos + 1 - lineOffset).trim().length() == 0;
                int prevToken = fScanner.previousToken(prevPos, YangHeuristicScanner.UNBOUND);
                boolean bracelessBlockStart = fScanner.isBracelessBlockStart(prevPos, YangHeuristicScanner.UNBOUND);

                switch (nextToken) {
                case Symbols.TokenLBRACE: // for opening-brace-on-new-line style
                    if (bracelessBlockStart && !fPrefs.prefIndentBracesForBlocks) {
                        unindent = true;
                    } else if ((prevToken == Symbols.TokenCOLON || prevToken == Symbols.TokenEQUAL)
                            && !fPrefs.prefIndentBracesForArrays) {
                        unindent = true;
                    } else if (!bracelessBlockStart && fPrefs.prefIndentBracesForMethods) {
                        indent = true;
                    }
                    break;
                case Symbols.TokenRBRACE: // closing braces get unindented
                    if (isFirstTokenOnLine) {
                        matchBrace = true;
                    }
                    break;
                case Symbols.TokenRPAREN:
                    if (isFirstTokenOnLine) {
                        matchParen = true;
                    }
                    break;
                case Symbols.TokenPLUS:
                    if (isStringContinuation(offset)) {
                        if (isSecondLineOfStringContinuation(offset)) {
                            fAlign = YangHeuristicScanner.NOT_FOUND;
                            fIndent = fPrefs.prefContinuationIndent;
                        } else {
                            int previousLineOffset = fDocument.getLineOffset(fDocument.getLineOfOffset(offset) - 1);
                            fAlign = fScanner.findNonWhitespaceForwardInAnyPartition(previousLineOffset,
                                    YangHeuristicScanner.UNBOUND);
                        }
                        return fPosition;
                    }
                    break;
                }
            } catch (BadLocationException e) {
            }
        } else {
            // don't assume an else could come if we are at the end of file
            danglingElse = false;
        }

        int ref = findReferencePosition(offset, danglingElse, matchBrace, matchParen, matchCase, throwsClause);
        if (unindent) {
            fIndent--;
        }
        if (indent) {
            fIndent++;
        }
        return ref;
    }

    /**
     * Tells whether the given string is a continuation expression.
     */
    private boolean isStringContinuation(int offset) {
        int nextNonWSCharPosition = fScanner.findNonWhitespaceBackwardInAnyPartition(offset - 1,
                YangHeuristicScanner.UNBOUND);
        try {
            if (fDocument.getChar(nextNonWSCharPosition) == '"') {
                return true;
            } else {
                return false;
            }
        } catch (BadLocationException e) {
            YangEditorPlugin.log(e);
            return false;
        }
    }

    /**
     * Checks if extra indentation for second line of string continuation is required.
     */
    private boolean isSecondLineOfStringContinuation(int offset) {
        try {
            int offsetLine = fDocument.getLineOfOffset(offset);
            fPosition = offset;
            while (true) {
                nextToken();
                switch (fToken) {
                // scopes: skip them
                case Symbols.TokenRPAREN:
                case Symbols.TokenRBRACKET:
                case Symbols.TokenRBRACE:
                case Symbols.TokenGREATERTHAN:
                    skipScope();
                    break;

                case Symbols.TokenPLUS:
                    if ((offsetLine - fLine) > 1) {
                        return false;
                    }
                    break;

                case Symbols.TokenCOMMA:
                case Symbols.TokenLPAREN:
                case Symbols.TokenLBRACE:
                case Symbols.TokenEQUAL:
                    int stringStartingOffset = fScanner.findNonWhitespaceForwardInAnyPartition(fPosition + 1,
                            YangHeuristicScanner.UNBOUND);
                    int stringStartingLine = fDocument.getLineOfOffset(stringStartingOffset);
                    if ((offsetLine - stringStartingLine) == 1) {
                        fPosition = stringStartingOffset;
                        return true;
                    } else {
                        return false;
                    }
                case Symbols.TokenLBRACKET:
                case Symbols.TokenEOF:
                    if ((offsetLine - fLine) == 1) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } catch (BadLocationException e) {
            YangEditorPlugin.log(e);
            return false;
        }
    }

    public int findReferencePosition(int offset, boolean danglingElse, boolean matchBrace, boolean matchParen,
            boolean matchCase) {
        return findReferencePosition(offset, danglingElse, matchBrace, matchParen, matchCase, false);
    }

    public int findReferencePosition(int offset, boolean danglingElse, boolean matchBrace, boolean matchParen,
            boolean matchCase, boolean throwsClause) {
        fIndent = 0; // the indentation modification
        fAlign = YangHeuristicScanner.NOT_FOUND;
        fPosition = offset;

        // forward cases an unindentation happens sometimes if the next token is special, namely on
        // braces, parens
        // and case labels align braces, but handle the case where we align with the method
        // declaration start
        // instead of the opening brace.
        if (matchBrace) {
            if (skipScope(Symbols.TokenLBRACE, Symbols.TokenRBRACE)) {
                try {
                    // align with the opening brace that is on a line by its own
                    int lineOffset = fDocument.getLineOffset(fLine);
                    if (lineOffset <= fPosition
                            && fDocument.get(lineOffset, fPosition - lineOffset).trim().length() == 0) {
                        return fPosition;
                    }
                } catch (BadLocationException e) {
                    // concurrent modification - walk default path
                }
                // if the opening brace is not on the start of the line, skip to the start
                int pos = skipToStatementStart(true, true);
                fIndent = 0; // indent is aligned with reference position
                return pos;
            } else {
                // if we can't find the matching brace, the heuristic is to unindent
                // by one against the normal position
                int pos = findReferencePosition(offset, danglingElse, false, matchParen, matchCase, throwsClause);
                fIndent--;
                return pos;
            }
        }

        // align parenthesis'
        if (matchParen) {
            if (skipScope(Symbols.TokenLPAREN, Symbols.TokenRPAREN)) {
                fIndent = fPrefs.prefContinuationIndent;
                return fPosition;
            } else {
                // if we can't find the matching paren, the heuristic is to unindent
                // by one against the normal position
                int pos = findReferencePosition(offset, danglingElse, matchBrace, false, matchCase, throwsClause);
                fIndent--;
                return pos;
            }
        }

        nextToken();
        switch (fToken) {
        case Symbols.TokenGREATERTHAN:
        case Symbols.TokenRBRACE:
            // skip the block and fall through
            // if we can't complete the scope, reset the scan position
            int pos = fPosition;
            if (!skipScope()) {
                fPosition = pos;
            }
            return skipToStatementStart(danglingElse, false);
        case Symbols.TokenSEMICOLON:
            // this is the 90% case: after a statement block
            // the end of the previous statement / block previous.end
            // search to the end of the statement / block before the previous; the token just after
            // that is previous.start
            pos = fPosition;
            return skipToStatementStart(danglingElse, false);
            // scope introduction: special treat who special is
        case Symbols.TokenLPAREN:
        case Symbols.TokenLBRACE:
        case Symbols.TokenLBRACKET:
            return handleScopeIntroduction(offset + 1);

        case Symbols.TokenEOF:
            // trap when hitting start of document
            return YangHeuristicScanner.NOT_FOUND;

        case Symbols.TokenEQUAL:
            // indent assignments
            return handleEqual();

        case Symbols.TokenCOLON:
            // TODO handle ternary deep indentation
            fIndent = fPrefs.prefCaseBlockIndent;
            return fPosition;

        case Symbols.TokenQUESTIONMARK:
            if (fPrefs.prefTernaryDeepAlign) {
                setFirstElementAlignment(fPosition, offset + 1);
                return fPosition;
            } else {
                fIndent = fPrefs.prefTernaryIndent;
                return fPosition;
            }

            // indentation for blockless introducers:
        case Symbols.TokenKEYWORD:
            fIndent = fPrefs.prefSimpleIndent;
            return fPosition;

        case Symbols.TokenRBRACKET:
            fIndent = fPrefs.prefContinuationIndent;
            return fPosition;

        case Symbols.TokenRPAREN:
            if (throwsClause) {
                fIndent = fPrefs.prefContinuationIndent;
                return fPosition;
            }
            int line = fLine;
            if (skipScope(Symbols.TokenLPAREN, Symbols.TokenRPAREN)) {
                int scope = fPosition;
                nextToken();
                fPosition = scope;
                if (looksLikeMethodDecl()) {
                    return skipToStatementStart(danglingElse, false);
                }
                fPosition = scope;
                if (looksLikeAnnotation()) {
                    return skipToStatementStart(danglingElse, false);
                }
            }
            // restore
            fPosition = offset;
            fLine = line;

            return skipToPreviousListItemOrListStart();
        case Symbols.TokenPLUS:
            if (isStringContinuation(fPosition)) {
                try {
                    if (isSecondLineOfStringContinuation(offset)) {
                        fAlign = YangHeuristicScanner.NOT_FOUND;
                        fIndent = fPrefs.prefContinuationIndent;
                    } else {
                        int previousLineOffset = fDocument.getLineOffset(fDocument.getLineOfOffset(offset) - 1);
                        fAlign = fScanner.findNonWhitespaceForwardInAnyPartition(previousLineOffset,
                                YangHeuristicScanner.UNBOUND);
                    }
                } catch (BadLocationException e) {
                    YangEditorPlugin.log(e);
                }
                return fPosition;
            }
            fPosition = offset;
            return skipToPreviousListItemOrListStart();
        case Symbols.TokenCOMMA:
            // inside a list of some type
            // easy if there is already a list item before with its own indentation - we just align
            // if not: take the start of the list ( LPAREN, LBRACE, LBRACKET ) and either align or
            // indent by list-indent
        default:
            // inside whatever we don't know about: similar to the list case:
            // if we are inside a continued expression, then either align with a previous line that
            // has indentation
            // or indent from the expression start line (either a scope introducer or the start of
            // the expr).
            return skipToPreviousListItemOrListStart();
        }
    }

    /**
     * Checks if the statement at position is itself a continuation of the previous, else sets the
     * indentation to Continuation Indent.
     *
     * @return the position of the token
     */
    private int handleEqual() {
        try {
            // If this line is itself continuation of the previous then do nothing
            IRegion line = fDocument.getLineInformationOfOffset(fPosition);
            int nonWS = fScanner.findNonWhitespaceBackward(line.getOffset(), YangHeuristicScanner.UNBOUND);
            if (nonWS != Symbols.TokenEOF) {
                int tokenAtPreviousLine = fScanner.nextToken(nonWS, nonWS + 1);
                if (tokenAtPreviousLine != Symbols.TokenSEMICOLON && tokenAtPreviousLine != Symbols.TokenRBRACE
                        && tokenAtPreviousLine != Symbols.TokenLBRACE && tokenAtPreviousLine != Symbols.TokenEOF) {
                    return fPosition;
                }
            }
        } catch (BadLocationException e) {
            return fPosition;
        }

        fIndent = fPrefs.prefContinuationIndent;
        return fPosition;
    }

    /**
     * Skips to the start of a statement that ends at the current position.
     *
     * @param danglingElse whether to indent aligned with the last <code>if</code>
     * @param isInBlock whether the current position is inside a block, which limits the search
     * scope to the next scope introducer
     * @return the reference offset of the start of the statement
     */
    private int skipToStatementStart(boolean danglingElse, boolean isInBlock) {
        final int NOTHING = 0;
        final int READ_PARENS = 1;
        final int READ_IDENT = 2;
        int mayBeMethodBody = NOTHING;
        boolean isTypeBody = false;
        while (true) {
            nextToken();

            if (isInBlock) {
                switch (fToken) {
                // exit on all block introducers
                case Symbols.TokenKEYWORD:
                    return fPosition;
                }
            }

            switch (fToken) {
            // scope introduction through: LPAREN, LBRACE, LBRACKET
            // search stop on SEMICOLON, RBRACE, COLON, EOF
            // -> the next token is the start of the statement (i.e. previousPos when backward
            // scanning)
            case Symbols.TokenLPAREN:
            case Symbols.TokenLBRACE:
            case Symbols.TokenLBRACKET:
            case Symbols.TokenSEMICOLON:
                // XXX custom case, when comments appear above new block
            case Symbols.TokenOTHER:
            case Symbols.TokenEOF:
                if (isInBlock) {
                    fIndent = getBlockIndent(mayBeMethodBody == READ_IDENT, isTypeBody);
                }
                // else: fIndent set by previous calls
                return fPreviousPos;

            case Symbols.TokenCOLON:
                int pos = fPreviousPos;
                if (!isConditional()) {
                    return pos;
                }
                break;

            case Symbols.TokenRBRACE:
                // RBRACE is a little tricky: it can be the end of an array definition, but
                // usually it is the end of a previous block
                pos = fPreviousPos; // store state
                if (skipScope() && looksLikeArrayInitializerIntro()) {
                    continue; // it's an array
                } else {
                    if (isInBlock) {
                        fIndent = getBlockIndent(mayBeMethodBody == READ_IDENT, isTypeBody);
                    }
                    return pos; // it's not - do as with all the above
                }

                // scopes: skip them
            case Symbols.TokenRPAREN:
                if (isInBlock) {
                    mayBeMethodBody = READ_PARENS;
                }
                //$FALL-THROUGH$
            case Symbols.TokenRBRACKET:
            case Symbols.TokenGREATERTHAN:
                pos = fPreviousPos;
                if (skipScope()) {
                    break;
                } else {
                    return pos;
                }

            case Symbols.TokenKEYWORD:
                // align the WHILE position with its do
                return fPosition;

            case Symbols.TokenIDENT:
                if (mayBeMethodBody == READ_PARENS) {
                    mayBeMethodBody = READ_IDENT;
                }
                break;

            default:
                // keep searching

            }

        }

    }

    private int getBlockIndent(boolean isMethodBody, boolean isTypeBody) {
        if (isTypeBody) {
            return fPrefs.prefTypeIndent + (fPrefs.prefIndentBracesForTypes ? 1 : 0);
        } else if (isMethodBody) {
            return fPrefs.prefMethodBodyIndent + (fPrefs.prefIndentBracesForMethods ? 1 : 0);
        } else {
            return fIndent;
        }
    }

    /**
     * Returns true if the colon at the current position is part of a conditional (ternary)
     * expression, false otherwise.
     *
     * @return true if the colon at the current position is part of a conditional
     */
    private boolean isConditional() {
        while (true) {
            nextToken();
            switch (fToken) {

            // search for case labels, which consist of (possibly qualified) identifiers or numbers
            case Symbols.TokenIDENT:
            case Symbols.TokenOTHER: // dots for qualified constants
                continue;
            default:
                return true;
            }
        }
    }

    /**
     * Returns the reference position for a list element: either a previous list item that has its
     * own indentation, or the list introduction start.
     */
    private int skipToPreviousListItemOrListStart() {
        int startLine = fLine;
        int startPosition = fPosition;
        while (true) {
            nextToken();

            // if any line item comes with its own indentation, adapt to it
            if (fLine < startLine) {
                // TODO
                // try {
                //
                // int lineOffset = fDocument.getLineOffset(startLine);
                // int bound = Math.min(fDocument.getLength(), startPosition + 1);
                // fAlign= fScanner.findNonWhitespaceForwardInAnyPartition(lineOffset, bound);
                // } catch (BadLocationException e) {
                // // ignore and return just the position
                // }
                return startPosition;
            }

            switch (fToken) {
            // scopes: skip them
            case Symbols.TokenRPAREN:
            case Symbols.TokenRBRACKET:
            case Symbols.TokenRBRACE:
            case Symbols.TokenGREATERTHAN:
                skipScope();
                break;

            // scope introduction: special treat who special is
            case Symbols.TokenLPAREN:
            case Symbols.TokenLBRACE:
            case Symbols.TokenLBRACKET:
                return handleScopeIntroduction(startPosition + 1);

            case Symbols.TokenSEMICOLON:
                return fPosition;
            case Symbols.TokenQUESTIONMARK:
                if (fPrefs.prefTernaryDeepAlign) {
                    setFirstElementAlignment(fPosition - 1, fPosition + 1);
                    return fPosition;
                } else {
                    fIndent = fPrefs.prefTernaryIndent;
                    return fPosition;
                }
            case Symbols.TokenEQUAL:
                return handleEqual();
            case Symbols.TokenEOF:
                return 0;

            }
        }
    }

    /**
     * Skips a scope and positions the cursor (<code>fPosition</code>) on the token that opens the
     * scope. Returns <code>true</code> if a matching peer could be found, <code>false</code>
     * otherwise. The current token when calling must be one out of <code>Symbols.TokenRPAREN</code>
     * , <code>Symbols.TokenRBRACE</code>, and <code>Symbols.TokenRBRACKET</code>.
     *
     * @return <code>true</code> if a matching peer was found, <code>false</code> otherwise
     */
    private boolean skipScope() {
        switch (fToken) {
        case Symbols.TokenRPAREN:
            return skipScope(Symbols.TokenLPAREN, Symbols.TokenRPAREN);
        case Symbols.TokenRBRACKET:
            return skipScope(Symbols.TokenLBRACKET, Symbols.TokenRBRACKET);
        case Symbols.TokenRBRACE:
            return skipScope(Symbols.TokenLBRACE, Symbols.TokenRBRACE);
        case Symbols.TokenGREATERTHAN:
            if (!fPrefs.prefHasGenerics) {
                return false;
            }
            int storedPosition = fPosition;
            int storedToken = fToken;
            nextToken();
            switch (fToken) {
            case Symbols.TokenIDENT:
                //$FALL-THROUGH$
            case Symbols.TokenQUESTIONMARK:
            case Symbols.TokenGREATERTHAN:
                if (skipScope(Symbols.TokenLESSTHAN, Symbols.TokenGREATERTHAN)) {
                    return true;
                }
            }
            // <> are harder to detect - restore the position if we fail
            fPosition = storedPosition;
            fToken = storedToken;
            return false;

        default:
            Assert.isTrue(false);
            return false;
        }
    }

    /**
     * Handles the introduction of a new scope. The current token must be one out of
     * <code>Symbols.TokenLPAREN</code>, <code>Symbols.TokenLBRACE</code>, and
     * <code>Symbols.TokenLBRACKET</code>. Returns as the reference position either the token
     * introducing the scope or - if available - the first YANG token after that.
     */
    private int handleScopeIntroduction(int bound) {
        switch (fToken) {
        // scope introduction: special treat who special is
        case Symbols.TokenLPAREN:
            int pos = fPosition; // store

            // special: method declaration deep indentation
            if (looksLikeMethodDecl()) {
                if (fPrefs.prefMethodDeclDeepIndent) {
                    return setFirstElementAlignment(pos, bound);
                } else {
                    fIndent = fPrefs.prefMethodDeclIndent;
                    return pos;
                }
            } else {
                fPosition = pos;
                if (looksLikeMethodCall()) {
                    if (fPrefs.prefMethodCallDeepIndent) {
                        return setFirstElementAlignment(pos, bound);
                    } else {
                        fIndent = fPrefs.prefMethodCallIndent;
                        return pos;
                    }
                } else if (fPrefs.prefParenthesisDeepIndent) {
                    return setFirstElementAlignment(pos, bound);
                }
            }

            // normal: return the parenthesis as reference
            fIndent = fPrefs.prefParenthesisIndent;
            return pos;

        case Symbols.TokenLBRACE:
            pos = fPosition; // store

            // special: array initializer
            if (looksLikeArrayInitializerIntro()) {
                if (fPrefs.prefArrayDeepIndent) {
                    return setFirstElementAlignment(pos, bound);
                } else {
                    fIndent = fPrefs.prefArrayIndent;
                }
            } else {
                fIndent = fPrefs.prefBlockIndent;
            }

            // normal: skip to the statement start before the scope introducer
            // opening braces are often on differently ending indents than e.g. a method definition
            if (looksLikeArrayInitializerIntro() && !fPrefs.prefIndentBracesForArrays
                    || !fPrefs.prefIndentBracesForBlocks) {
                fPosition = pos; // restore
                return skipToStatementStart(true, true); // set to true to match the first if
            } else {
                return pos;
            }

        case Symbols.TokenLBRACKET:
            pos = fPosition; // store

            // special: method declaration deep indentation
            if (fPrefs.prefArrayDimensionsDeepIndent) {
                return setFirstElementAlignment(pos, bound);
            }

            // normal: return the bracket as reference
            fIndent = fPrefs.prefBracketIndent;
            return pos; // restore

        default:
            Assert.isTrue(false);
            return -1; // dummy
        }
    }

    /**
     * Sets the deep indent offset (<code>fAlign</code>) to either the offset right after
     * <code>scopeIntroducerOffset</code> or - if available - the first YANG token after
     * <code>scopeIntroducerOffset</code>, but before <code>bound</code>.
     *
     * @param scopeIntroducerOffset the offset of the scope introducer
     * @param bound the bound for the search for another element
     * @return the reference position
     */
    private int setFirstElementAlignment(int scopeIntroducerOffset, int bound) {
        int firstPossible = scopeIntroducerOffset + 1; // align with the first position after the
        // scope intro
        fAlign = fScanner.findNonWhitespaceForwardInAnyPartition(firstPossible, bound);
        if (fAlign == YangHeuristicScanner.NOT_FOUND) {
            fAlign = firstPossible;
        }
        return fAlign;
    }

    /**
     * Returns <code>true</code> if the next token received after calling <code>nextToken</code> is
     * either an equal sign or an array designator ('[]').
     *
     * @return <code>true</code> if the next elements look like the start of an array definition
     */
    private boolean looksLikeArrayInitializerIntro() {
        nextToken();
        if (fToken == Symbols.TokenEQUAL || skipBrackets()) {
            return true;
        }
        return false;
    }

    /**
     * Skips brackets if the current token is a RBRACKET. There can be nothing but whitespace in
     * between, this is only to be used for <code>[]</code> elements.
     *
     * @return <code>true</code> if a <code>[]</code> could be scanned, the current token is left at
     * the LBRACKET.
     */
    private boolean skipBrackets() {
        if (fToken == Symbols.TokenRBRACKET) {
            nextToken();
            if (fToken == Symbols.TokenLBRACKET) {
                return true;
            }
        }
        return false;
    }

    /**
     * Reads the next token in backward direction from the heuristic scanner and sets the fields
     * <code>fToken, fPreviousPosition</code> and <code>fPosition</code> accordingly.
     */
    private void nextToken() {
        nextToken(fPosition);
    }

    /**
     * Reads the next token in backward direction of <code>start</code> from the heuristic scanner
     * and sets the fields <code>fToken, fPreviousPosition</code> and <code>fPosition</code>
     * accordingly.
     *
     * @param start the start offset from which to scan backwards
     */
    private void nextToken(int start) {
        fToken = fScanner.previousToken(start - 1, YangHeuristicScanner.UNBOUND);
        fPreviousPos = start;
        fPosition = fScanner.getPosition() + 1;
        try {
            fLine = fDocument.getLineOfOffset(fPosition);
        } catch (BadLocationException e) {
            fLine = -1;
        }
    }

    /**
     * Returns <code>true</code> if the current tokens look like a method declaration header (i.e.
     * only the return type and method name). The heuristic calls <code>nextToken</code> and expects
     * an identifier (method name) and a type declaration (an identifier with optional brackets)
     * which also covers the visibility modifier of constructors; it does not recognize package
     * visible constructors.
     *
     * @return <code>true</code> if the current position looks like a method declaration header.
     */
    private boolean looksLikeMethodDecl() {
        /*
         * TODO This heuristic does not recognize package private constructors since those do have
         * neither type nor visibility keywords. One option would be to go over the parameter list,
         * but that might be empty as well, or not typed in yet - hard to do without an AST...
         */

        nextToken();
        if (fToken == Symbols.TokenIDENT) { // method name
            do {
                nextToken();
            } while (skipBrackets()); // optional brackets for array valued return types

            return fToken == Symbols.TokenIDENT; // return type name

        }
        return false;
    }

    /**
     * Returns <code>true</code> if the current tokens look like an annotation (i.e. an annotation
     * name (potentially qualified) preceded by an at-sign).
     *
     * @return <code>true</code> if the current position looks like an annotation.
     */

    private boolean looksLikeAnnotation() {
        nextToken();
        if (fToken == Symbols.TokenIDENT) { // Annotation name
            nextToken();
            while (fToken == Symbols.TokenOTHER) { // dot of qualification
                nextToken();
                if (fToken != Symbols.TokenIDENT) {
                    return false;
                }
                nextToken();
            }
            return fToken == Symbols.TokenAT;
        }
        return false;
    }

    /**
     * Returns <code>true</code> if the current tokens look like a method call header (i.e. an
     * identifier as opposed to a keyword taking parenthesized parameters such as <code>if</code>).
     * <p>
     * The heuristic calls <code>nextToken</code> and expects an identifier (method name).
     *
     * @return <code>true</code> if the current position looks like a method call header.
     */
    private boolean looksLikeMethodCall() {
        nextToken();
        return fToken == Symbols.TokenIDENT; // method name
    }

    /**
     * Scans tokens for the matching opening peer. The internal cursor (<code>fPosition</code>) is
     * set to the offset of the opening peer if found.
     *
     * @param openToken the opening peer token
     * @param closeToken the closing peer token
     * @return <code>true</code> if a matching token was found, <code>false</code> otherwise
     */
    private boolean skipScope(int openToken, int closeToken) {

        int depth = 1;

        while (true) {
            nextToken();

            if (fToken == closeToken) {
                depth++;
            } else if (fToken == openToken) {
                depth--;
                if (depth == 0) {
                    return true;
                }
            } else if (fToken == Symbols.TokenEOF) {
                return false;
            }
        }
    }
}
