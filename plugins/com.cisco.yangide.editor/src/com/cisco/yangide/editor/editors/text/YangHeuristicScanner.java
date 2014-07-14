/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors.text;

import java.util.Arrays;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.TypedRegion;

import com.cisco.yangide.editor.editors.YangScanner;
import com.cisco.yangide.editor.preferences.YangDocumentSetupParticipant;

/**
 * Utility methods for heuristic based YANG manipulations in an incomplete YANG source file.
 *
 * @author Alexey Kholupko
 */
public final class YangHeuristicScanner implements Symbols {
    /**
     * Returned by all methods when the requested position could not be found
     */
    public static final int NOT_FOUND = -1;

    /**
     * Special bound parameter that means either -1 (backward scanning) or <code>fDocument.getLength()</code> (forward scanning).
     */
    public static final int UNBOUND = -2;

    /* character constants */
    private static final char LBRACE = '{';
    private static final char RBRACE = '}';
    private static final char LPAREN = '(';
    private static final char RPAREN = ')';
    private static final char SEMICOLON = ';';
    private static final char COLON = ':';
    private static final char COMMA = ',';
    private static final char LBRACKET = '[';
    private static final char RBRACKET = ']';
    private static final char QUESTIONMARK = '?';
    private static final char EQUAL = '=';
    private static final char LANGLE = '<';
    private static final char RANGLE = '>';
    private static final char PLUS = '+';
    private static final char AT = '@';

    /**
     * Specifies the stop condition, upon which the <code>scanXXX</code> methods will decide whether
     * to keep scanning or not. This interface may implemented by clients.
     */
    private static abstract class StopCondition {
        /**
         * Instructs the scanner to return the current position.
         *
         * @param ch the char at the current position
         * @param position the current position
         * @param forward the iteration direction
         * @return <code>true</code> if the stop condition is met.
         */
        public abstract boolean stop(char ch, int position, boolean forward);

        /**
         * Asks the condition to return the next position to query. The default is to return the
         * next/previous position.
         * 
         * @param position the position
         * @param forward <code>true</code> if next position should be returned
         * @return the next position to scan
         */
        public int nextPosition(int position, boolean forward) {
            return forward ? position + 1 : position - 1;
        }
    }

    /**
     * Stops upon a non-whitespace (as defined by {@link Character#isWhitespace(char)}) character.
     */
    private static class NonWhitespace extends StopCondition {
        /*
         * @see com.cisco.yangide.editor.editors.text.YangHeuristicScanner.StopCondition#stop(char)
         */
        @Override
        public boolean stop(char ch, int position, boolean forward) {
            return !Character.isWhitespace(ch);
        }
    }

    /**
     * Stops upon a non-whitespace character in the default partition.
     *
     * @see YangHeuristicScanner.NonWhitespace
     */
    private final class NonWhitespaceDefaultPartition extends NonWhitespace {
        /*
         * @see com.cisco.yangide.editor.editors.text.YangHeuristicScanner.StopCondition#stop(char)
         */
        @Override
        public boolean stop(char ch, int position, boolean forward) {
            return super.stop(ch, position, true) && isDefaultPartition(position);
        }

        /*
         * @see
         * com.cisco.yangide.editor.editors.text.YangHeuristicScanner.StopCondition#nextPosition
         * (int, boolean)
         */
        @Override
        public int nextPosition(int position, boolean forward) {
            ITypedRegion partition = getPartition(position);
            if (fPartition.equals(partition.getType()))
                return super.nextPosition(position, forward);

            if (forward) {
                int end = partition.getOffset() + partition.getLength();
                if (position < end)
                    return end;
            } else {
                int offset = partition.getOffset();
                if (position > offset)
                    return offset - 1;
            }
            return super.nextPosition(position, forward);
        }
    }

    /**
     * Stops upon a non-YANG identifier (as defined by {@link #isYAngIdentifierPart(char)})
     * character.
     */
    private static class NonYangIdentifierPart extends StopCondition {
        /*
         * @see com.cisco.yangide.editor.editors.text.YangHeuristicScanner.StopCondition#stop(char)
         */
        @Override
        public boolean stop(char ch, int position, boolean forward) {
            return !isYangIdentifierPart(ch);
        }
    }

    /**
     * Stops upon a non-YANG identifier character in the default partition.
     *
     * @see YangHeuristicScanner.NonYangIdentifierPart
     */
    private final class NonYangIdentifierPartDefaultPartition extends NonYangIdentifierPart {
        /*
         * @see com.cisco.yangide.editor.editors.text.YangHeuristicScanner.StopCondition#stop(char)
         */
        @Override
        public boolean stop(char ch, int position, boolean forward) {
            return super.stop(ch, position, true) || !isDefaultPartition(position);
        }

        /*
         * @see
         * com.cisco.yangide.editor.editors.text.YangHeuristicScanner.StopCondition#nextPosition
         * (int, boolean)
         */
        @Override
        public int nextPosition(int position, boolean forward) {
            ITypedRegion partition = getPartition(position);
            if (fPartition.equals(partition.getType()))
                return super.nextPosition(position, forward);

            if (forward) {
                int end = partition.getOffset() + partition.getLength();
                if (position < end)
                    return end;
            } else {
                int offset = partition.getOffset();
                if (position > offset)
                    return offset - 1;
            }
            return super.nextPosition(position, forward);
        }
    }

    /**
     * Stops upon a character in the default partition that matches the given character list.
     */
    private final class CharacterMatch extends StopCondition {
        private final char[] fChars;

        /**
         * Creates a new instance.
         * 
         * @param ch the single character to match
         */
        public CharacterMatch(char ch) {
            this(new char[] { ch });
        }

        /**
         * Creates a new instance.
         * 
         * @param chars the chars to match.
         */
        public CharacterMatch(char[] chars) {
            Assert.isNotNull(chars);
            Assert.isTrue(chars.length > 0);
            fChars = chars;
            Arrays.sort(chars);
        }

        /*
         * @see com.cisco.yangide.editor.editors.text.YangHeuristicScanner.StopCondition#stop(char,
         * int)
         */
        @Override
        public boolean stop(char ch, int position, boolean forward) {
            return Arrays.binarySearch(fChars, ch) >= 0 && isDefaultPartition(position);
        }

        /*
         * @see
         * com.cisco.yangide.editor.editors.text.YangHeuristicScanner.StopCondition#nextPosition
         * (int, boolean)
         */
        @Override
        public int nextPosition(int position, boolean forward) {
            ITypedRegion partition = getPartition(position);
            if (fPartition.equals(partition.getType()))
                return super.nextPosition(position, forward);

            if (forward) {
                int end = partition.getOffset() + partition.getLength();
                if (position < end)
                    return end;
            } else {
                int offset = partition.getOffset();
                if (position > offset)
                    return offset - 1;
            }
            return super.nextPosition(position, forward);
        }
    }

    /** The document being scanned. */
    private final IDocument fDocument;
    /** The partitioning being used for scanning. */
    private final String fPartitioning;
    /** The partition to scan in. */
    private final String fPartition;

    /* internal scan state */

    /** the most recently read character. */
    private char fChar;
    /** the most recently read position. */
    private int fPos;
    /**
     * The most recently used partition.
     */
    private ITypedRegion fCachedPartition = new TypedRegion(-1, 0, "__no_partition_at_all"); //$NON-NLS-1$

    /* preset stop conditions */
    private final StopCondition fNonWSDefaultPart = new NonWhitespaceDefaultPartition();
    private final static StopCondition fNonWS = new NonWhitespace();
    private final StopCondition fNonIdent = new NonYangIdentifierPartDefaultPartition();

    /**
     * Creates a new instance.
     *
     * @param document the document to scan
     * @param partitioning the partitioning to use for scanning
     * @param partition the partition to scan in
     */
    public YangHeuristicScanner(IDocument document, String partitioning, String partition) {
        Assert.isLegal(document != null);
        Assert.isLegal(partitioning != null);
        Assert.isLegal(partition != null);
        fDocument = document;
        fPartitioning = partitioning;
        fPartition = partition;
    }

    /**
     * Calls <code>this(document, YangDocumentSetupParticipant.YANG_PARTITIONING, IDocument.DEFAULT_CONTENT_TYPE)</code>
     *
     * @param document the document to scan.
     */
    public YangHeuristicScanner(IDocument document) {
        this(document, YangDocumentSetupParticipant.YANG_PARTITIONING, IDocument.DEFAULT_CONTENT_TYPE);
    }

    /**
     * Returns the most recent internal scan position.
     *
     */
    public int getPosition() {
        return fPos;
    }

    /**
     * Returns the next token in forward direction, starting at <code>start</code>, and not
     * extending further than <code>bound</code>. The return value is one of the constants defined
     * in {@link Symbols}. After a call, {@link #getPosition()} will return the position just after
     * the scanned token (i.e. the next position that will be scanned).
     *
     */
    public int nextToken(int start, int bound) {
        int pos = scanForward(start, bound, fNonWSDefaultPart);
        if (pos == NOT_FOUND)
            return TokenEOF;

        fPos++;

        switch (fChar) {
        case LBRACE:
            return TokenLBRACE;
        case RBRACE:
            return TokenRBRACE;
        case LBRACKET:
            return TokenLBRACKET;
        case RBRACKET:
            return TokenRBRACKET;
        case LPAREN:
            return TokenLPAREN;
        case RPAREN:
            return TokenRPAREN;
        case SEMICOLON:
            return TokenSEMICOLON;
        case COMMA:
            return TokenCOMMA;
        case QUESTIONMARK:
            return TokenQUESTIONMARK;
        case EQUAL:
            return TokenEQUAL;
        case LANGLE:
            return TokenLESSTHAN;
        case RANGLE:
            return TokenGREATERTHAN;
        case PLUS:
            return TokenPLUS;
        case AT:
            return TokenAT;
        }

        // else
        if (isYangIdentifierPart(fChar)) {
            // assume an identifier or keyword
            int from = pos, to;
            pos = scanForward(pos + 1, bound, fNonIdent);
            if (pos == NOT_FOUND)
                to = bound == UNBOUND ? fDocument.getLength() : bound;
            else
                to = pos;

            String identOrKeyword;
            try {
                identOrKeyword = fDocument.get(from, to - from);
            } catch (BadLocationException e) {
                return TokenEOF;
            }

            return getToken(identOrKeyword);

        } else {
            // operators, number literals etc
            return TokenOTHER;
        }
    }

    /**
     * Returns the next token in backward direction, starting at <code>start</code>, and not
     * extending further than <code>bound</code>. The return value is one of the constants defined
     * in {@link Symbols}. After a call, {@link #getPosition()} will return the position just before
     * the scanned token starts (i.e. the next position that will be scanned).
     *
     */
    public int previousToken(int start, int bound) {
        int pos = scanBackward(start, bound, fNonWSDefaultPart);
        if (pos == NOT_FOUND)
            return TokenEOF;

        fPos--;

        switch (fChar) {
        case LBRACE:
            return TokenLBRACE;
        case RBRACE:
            return TokenRBRACE;
        case LBRACKET:
            return TokenLBRACKET;
        case RBRACKET:
            return TokenRBRACKET;
        case LPAREN:
            return TokenLPAREN;
        case RPAREN:
            return TokenRPAREN;
        case SEMICOLON:
            return TokenSEMICOLON;
        case COLON:
            return TokenCOLON;
        case COMMA:
            return TokenCOMMA;
        case QUESTIONMARK:
            return TokenQUESTIONMARK;
        case EQUAL:
            return TokenEQUAL;
        case LANGLE:
            return TokenLESSTHAN;
        case RANGLE:
            return TokenGREATERTHAN;
        case PLUS:
            return TokenPLUS;
        case AT:
            return TokenAT;
        }

        // else
        if (isYangIdentifierPart(fChar)) {
            // assume an ident or keyword
            int from, to = pos + 1;
            pos = scanBackward(pos - 1, bound, fNonIdent);
            if (pos == NOT_FOUND)
                from = bound == UNBOUND ? 0 : bound + 1;
            else
                from = pos + 1;

            String identOrKeyword;
            try {
                identOrKeyword = fDocument.get(from, to - from);
            } catch (BadLocationException e) {
                return TokenEOF;
            }

            return getToken(identOrKeyword);

        } else {
            // operators, number literals etc
            return TokenOTHER;
        }

    }

    /**
     * @param fChar2
     * @return
     */
    public static boolean isYangIdentifierPart(char fChar2) {

        return Character.isJavaIdentifierPart(fChar2) || fChar2 == '-';
    }

    /**
     * Returns one of the keyword constants or <code>TokenIDENT</code> for a scanned identifier.
     *
     * @param s a scanned identifier
     * @return one of the constants defined in {@link Symbols}
     */
    private int getToken(String s) {
        Assert.isNotNull(s);

        //TODO types are separate tokens
        if(Arrays.asList(YangScanner.getKeywords()).contains(s) || Arrays.asList(YangScanner.getTypes()).contains(s))
            return TokenKEYWORD;

        return TokenIDENT;
    }

    /**
     * Returns the position of the closing peer character (forward search). Any scopes introduced by
     * opening peers are skipped. All peers accounted for must reside in the default partition.
     * <p>
     * <code>start</code> must not point to the opening peer, but to the first character being searched.
     * </p>
     *
     */
    public int findClosingPeer(int start, final char openingPeer, final char closingPeer) {
        return findClosingPeer(start, UNBOUND, openingPeer, closingPeer);
    }

    public int findClosingPeer(int start, int bound, final char openingPeer, final char closingPeer) {
        Assert.isLegal(start >= 0);

        try {
            CharacterMatch match = new CharacterMatch(new char[] { openingPeer, closingPeer });
            int depth = 1;
            start -= 1;
            while (true) {
                start = scanForward(start + 1, bound, match);
                if (start == NOT_FOUND)
                    return NOT_FOUND;

                if (fDocument.getChar(start) == openingPeer)
                    depth++;
                else
                    depth--;

                if (depth == 0)
                    return start;
            }

        } catch (BadLocationException e) {
            return NOT_FOUND;
        }
    }

    /**
     * Returns the position of the opening peer character (backward search). Any scopes introduced
     * by closing peers are skipped. All peers accounted for must reside in the default partition.
     * <p>
     * <code>start</code> must not point to the closing peer, but to the first character being searched.
     * </p>
     *
     */
    public int findOpeningPeer(int start, char openingPeer, char closingPeer) {
        return findOpeningPeer(start, UNBOUND, openingPeer, closingPeer);
    }

    public int findOpeningPeer(int start, int bound, char openingPeer, char closingPeer) {
        Assert.isLegal(start < fDocument.getLength());

        try {
            final CharacterMatch match = new CharacterMatch(new char[] { openingPeer, closingPeer });
            int depth = 1;
            start += 1;
            while (true) {
                start = scanBackward(start - 1, bound, match);
                if (start == NOT_FOUND)
                    return NOT_FOUND;

                if (fDocument.getChar(start) == closingPeer)
                    depth++;
                else
                    depth--;

                if (depth == 0)
                    return start;
            }

        } catch (BadLocationException e) {
            return NOT_FOUND;
        }
    }

    /**
     * Computes the surrounding block around <code>offset</code>. The search is started at the
     * beginning of <code>offset</code>, i.e. an opening brace at <code>offset</code> will not be
     * part of the surrounding block, but a closing brace will.
     *
     */
    public IRegion findSurroundingBlock(int offset) {
        if (offset < 1 || offset >= fDocument.getLength())
            return null;

        int begin = findOpeningPeer(offset - 1, LBRACE, RBRACE);
        int end = findClosingPeer(offset, LBRACE, RBRACE);
        if (begin == NOT_FOUND || end == NOT_FOUND)
            return null;
        return new Region(begin, end + 1 - begin);
    }

    /**
     * Finds the smallest position in <code>fDocument</code> such that the position is &gt;=
     * <code>position</code> and &lt; <code>bound</code> and
     * <code>Character.isWhitespace(fDocument.getChar(pos))</code> evaluates to <code>false</code>
     * and the position is in the default partition.
     * 
     */
    public int findNonWhitespaceForward(int position, int bound) {
        return scanForward(position, bound, fNonWSDefaultPart);
    }

    public int findNonWhitespaceForwardInAnyPartition(int position, int bound) {
        return scanForward(position, bound, fNonWS);
    }

    /**
     * Finds the highest position in <code>fDocument</code> such that the position is &lt;=
     * <code>position</code> and &gt; <code>bound</code> and
     * <code>Character.isWhitespace(fDocument.getChar(pos))</code> evaluates to <code>false</code>
     * and the position is in the default partition.
     *
     */
    public int findNonWhitespaceBackward(int position, int bound) {
        return scanBackward(position, bound, fNonWSDefaultPart);
    }

    public int findNonWhitespaceBackwardInAnyPartition(int position, int bound) {
        return scanBackward(position, bound, fNonWS);
    }

    /**
     * Finds the lowest position <code>p</code> in <code>fDocument</code> such that
     * <code>start</code> &lt;= p &lt; <code>bound</code> and
     * <code>condition.stop(fDocument.getChar(p), p)</code> evaluates to <code>true</code>.
     * 
     */
    public int scanForward(int start, int bound, StopCondition condition) {
        Assert.isLegal(start >= 0);

        if (bound == UNBOUND)
            bound = fDocument.getLength();

        Assert.isLegal(bound <= fDocument.getLength());

        try {
            fPos = start;
            while (fPos < bound) {

                fChar = fDocument.getChar(fPos);
                if (condition.stop(fChar, fPos, true))
                    return fPos;

                fPos = condition.nextPosition(fPos, true);
            }
        } catch (BadLocationException e) {
        }
        return NOT_FOUND;
    }

    public int scanForward(int position, int bound, char ch) {
        return scanForward(position, bound, new CharacterMatch(ch));
    }

    public int scanForward(int position, int bound, char[] chars) {
        return scanForward(position, bound, new CharacterMatch(chars));
    }

    public int scanBackward(int start, int bound, StopCondition condition) {
        if (bound == UNBOUND)
            bound = -1;

        Assert.isLegal(bound >= -1);
        Assert.isLegal(start < fDocument.getLength());

        try {
            fPos = start;
            while (fPos > bound) {

                fChar = fDocument.getChar(fPos);
                if (condition.stop(fChar, fPos, false))
                    return fPos;

                fPos = condition.nextPosition(fPos, false);
            }
        } catch (BadLocationException e) {
        }
        return NOT_FOUND;
    }

    public int scanBackward(int position, int bound, char ch) {
        return scanBackward(position, bound, new CharacterMatch(ch));
    }

    public int scanBackward(int position, int bound, char[] chars) {
        return scanBackward(position, bound, new CharacterMatch(chars));
    }

    /**
     * Checks whether <code>position</code> resides in a default (YANG) partition of <code>fDocument</code>.
     *
     */
    public boolean isDefaultPartition(int position) {
        return fPartition.equals(getPartition(position).getType());
    }

    /**
     * Returns the partition at <code>position</code>.
     *
     */
    private ITypedRegion getPartition(int position) {
        if (!contains(fCachedPartition, position)) {
            Assert.isTrue(position >= 0);
            Assert.isTrue(position <= fDocument.getLength());

            try {
                fCachedPartition = TextUtilities.getPartition(fDocument, fPartitioning, position, false);
            } catch (BadLocationException e) {
                fCachedPartition = new TypedRegion(position, 0, "__no_partition_at_all"); //$NON-NLS-1$
            }
        }

        return fCachedPartition;
    }

    /**
     * Returns <code>true</code> if <code>region</code> contains <code>position</code>.
     *
     */
    private boolean contains(IRegion region, int position) {
        int offset = region.getOffset();
        return offset <= position && position < offset + region.getLength();
    }

    /**
     * Checks if the line seems to be an open condition not followed by a block (i.e. statement with just one following statement).
     *
     */
    public boolean isBracelessBlockStart(int position, int bound) {
        if (position < 1)
            return false;

        switch (previousToken(position, bound)) {
        // for example:
        // description
        //     "This is..."
        case TokenKEYWORD:
            return true;
        }

        return false;
    }

}
