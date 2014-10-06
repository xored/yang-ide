/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.parser;

import static org.opendaylight.yangtools.antlrv4.code.gen.YangLexer.LEFT_BRACE;
import static org.opendaylight.yangtools.antlrv4.code.gen.YangLexer.RIGHT_BRACE;
import static org.opendaylight.yangtools.antlrv4.code.gen.YangLexer.S;
import static org.opendaylight.yangtools.antlrv4.code.gen.YangLexer.SEMICOLON;
import static org.opendaylight.yangtools.antlrv4.code.gen.YangLexer.STRING;
import static org.opendaylight.yangtools.antlrv4.code.gen.YangLexer.WS;
import static org.opendaylight.yangtools.antlrv4.code.gen.YangLexer.LINE_COMMENT;
import static org.opendaylight.yangtools.antlrv4.code.gen.YangLexer.END_BLOCK_COMMENT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.antlr.v4.runtime.Token;
import org.opendaylight.yangtools.antlrv4.code.gen.YangLexer;

/**
 * @author Konstantin Zaitsev
 * @date Jul 22, 2014
 */
public class YangTokenFormatter implements ITokenFormatter {

    private StringBuilder sb;
    private List<Token> tokens;
    private Token prevToken;

    // states
    private int currIndent = 0;
    private boolean wasNL;
    private boolean ovrNL;
    private boolean wasWS;
    // was new line separator '{' '}' ';'
    private int nlCount = 0;
    private boolean importScope = false;
    private StringBuilder importStatement = null;

    // preferences
    private int indent;
    private String lineSeparator;
    private Boolean formatString;
    private Boolean formatComment;
    private int maxLineLength;
    private Boolean compactImport;
    private boolean spaceForTabs;

    public YangTokenFormatter(YangFormattingPreferences preferences, int indentationLevel, String lineSeparator) {
        this.sb = new StringBuilder();
        this.spaceForTabs = preferences.isSpaceForTabs();
        this.indent = preferences.getIndentSize();
        this.formatComment = preferences.isFormatComment();
        this.formatString = preferences.isFormatStrings();
        this.compactImport = preferences.isCompactImport();
        this.maxLineLength = preferences.getMaxLineLength();
        this.lineSeparator = lineSeparator;
        this.currIndent = indentationLevel * this.indent;
        this.tokens = new ArrayList<>();
    }

    @Override
    public void process(Token token) {
        // ignore '\r' token
        if (isCarriageReturn(token)) {
            return;
        }

        if (isWS(token) && (isWS(prevToken) || isNewLine(prevToken))) {
            return;
        }

        if (isNewLine(token) && (isNewLine(prevToken) && nlCount > 1)) {
            return;
        }

        nlCount = isNewLine(token) ? (nlCount + 1) : 0;
        prevToken = token;
        tokens.add(token);

        // System.out.println(ignore + " '" + token.getText() + "'" + " - " +
        // YangLexer.tokenNames[token.getType()]);
    }

    /**
     * @param token token to inspect
     * @return <code>true</code> if token should be ignored from processing
     */
    private boolean checkForIgnore(Token token) {

        // ignore any repeat whitespace
        if (isWS(token) && (wasWS || wasNL)) {
            return true;
        }

        // ignore multiple new lines
        if (isNewLine(token) && (ovrNL || nlCount > 1)) {
            ovrNL = false;
            return true;
        }
        return false;
    }

    /**
     * @param token
     * @return <code>true</code> if token processed by import case
     */
    private boolean processImportCase(Token token) {
        if (!compactImport) {
            return false;
        }

        if (token.getType() == YangLexer.IMPORT_KEYWORD) {
            importScope = true;
            importStatement = new StringBuilder();
        }

        if (importScope) {
            if (token.getType() == RIGHT_BRACE) {
                importScope = false;
                printIndent();
                sb.append(importStatement.toString()).append(' ');
                wasWS = true;
                nlCount = 0;
                // add indent for right brace processing
                currIndent += indent;
                return false;
            } else if (!isNewLine(token) && !isWS(token)) {
                if (importStatement.length() > 0 && token.getType() != SEMICOLON) {
                    importStatement.append(' ');
                }
                importStatement.append(token.getText());
            }
            return true;
        }

        return importScope;
    }

    /**
     * Prints formatted text of token into string buffer.
     *
     * @param token current token
     */
    private void processToken(Token token) {
        int type = token.getType();

        if (type == STRING) {
            String text = token.getText();
            if (!formatString || text.length() < maxLineLength) {
                if (!(wasWS || token.getType() == LEFT_BRACE || token.getType() == SEMICOLON)) {
                    if (wasNL) {
                        printIndent();
                    }
                    printIndent();
                }
                sb.append(text);
            } else {
                printFormattedString(text);
            }
        } else if (type == YangLexer.END_BLOCK_COMMENT) {
            if (formatComment) {
                sb.append(lineSeparator);
                printIndent();
                String text = token.getText().substring(0, token.getText().length() - 2);
                text = text.replaceAll("(?m)^\\s+\\*", "");
                printFormattedString(text);
                sb.append(lineSeparator);
                printIndent();
                sb.append("*/");
            } else {
                sb.append(token.getText());
            }
        } else {
            if (!(wasWS || isNewLine(token) || isWS(token) || token.getType() == SEMICOLON || token.getType() == LEFT_BRACE)) {
                printIndent();
            }
            if (!isNewLine(token)) {
                sb.append(token.getText());
            }
        }
        if (isNewLine(token)) {
            sb.append(lineSeparator);
        }
    }

    /**
     * Prints formatted string token.
     *
     * @param token string token
     */
    private void printFormattedString(String str) {
        String text = str;
        text = text.replaceAll("\\s+", " ");

        boolean firstLine = true;
        StringBuilder textSB = new StringBuilder();
        StringTokenizer st = new StringTokenizer(text);
        while (st.hasMoreTokens()) {
            if (textSB.length() > 0) {
                textSB.append(" ");
            }
            textSB.append(st.nextToken());
            if (textSB.length() > maxLineLength) {
                if (!firstLine || wasNL) {
                    printIndent();
                    printIndent();
                }
                sb.append(textSB.toString());
                if (st.hasMoreTokens()) {
                    sb.append(lineSeparator);
                }
                textSB = new StringBuilder();
                firstLine = false;
            }
        }
        if (textSB.length() > 0) {
            if (!firstLine || wasNL) {
                printIndent();
                printIndent();
            }
            sb.append(textSB.toString());
        }
    }

    /**
     * Updates the state before token processed.
     *
     * @param token current token
     */
    private void updatePreState(Token token) {
        int type = token.getType();

        // decrease indent if '}' token
        if (type == RIGHT_BRACE) {
            currIndent -= indent;
            if (currIndent < 0) {
                currIndent = 0;
            }
        }

        // add space in expression like 'st {'
        if (!wasWS && type == LEFT_BRACE) {
            sb.append(" ");
            wasWS = true;
        }
    }

    /**
     * Updates the state after token processed.
     *
     * @param token current token
     */
    private void updateState(Token token) {
        int type = token.getType();

        // increase indent if '{' token
        if (type == LEFT_BRACE) {
            currIndent += indent;
        }
        ovrNL = false;

        // added new line after '{', ';', '}' tokens
        if (type == LEFT_BRACE || type == SEMICOLON || type == RIGHT_BRACE) {
            sb.append(lineSeparator);
            ovrNL = true;
            // nlCount++;
        }

        wasNL = isNewLine(token) || ovrNL;
        wasWS = isWS(token);

        if (isNewLine(token)) {
            nlCount++;
        }

        if (!wasNL) {
            nlCount = 0;
        }
    }

    /**
     * Prints appropriate indent to string buffer.
     */
    private void printIndent() {
        char[] str = new char[currIndent];
        Arrays.fill(str, spaceForTabs ? ' ' : '\t');
        sb.append(str);
    }

    /**
     * @param token token to inspect
     * @return <code>true</code> if token is space or space separator
     */
    private boolean isWS(Token token) {
        if (token == null) {
            return false;
        }
        int type = token.getType();
        return (type == WS || type == S) && !token.getText().equals("\n") && !token.getText().equals("\r");
    }

    /**
     * @param token token to inspect
     * @return <code>true</code> if token is new line token.
     */
    private boolean isNewLine(Token token) {
        if (token == null) {
            return false;
        }
        int type = token.getType();
        return (type == WS || type == S) && token.getText().equals("\n");
    }

    /**
     * @param token token to inspect
     * @return <code>true</code> if token is '\r' token.
     */
    private boolean isCarriageReturn(Token token) {
        if (token == null) {
            return false;
        }
        int type = token.getType();
        return (type == WS || type == S) && token.getText().equals("\r");
    }

    /**
     * @param token token to inspect
     * @return <code>true</code> if token is '{' '}' ';' token.
     */
    private boolean isBlockSeparator(Token token) {
        if (token == null) {
            return false;
        }
        int type = token.getType();
        return type == LEFT_BRACE || type == SEMICOLON || type == RIGHT_BRACE;
    }

    @Override
    public String getFormattedContent() {
        cleanTokens();
        for (Token token : tokens) {
            boolean ignore = checkForIgnore(token);
            if (!ignore) {
                if (!processImportCase(token)) {
                    updatePreState(token);
                    processToken(token);
                    updateState(token);
                }
            }
        }
        return sb.toString();
    }

    /**
     * Cleans 'new line' tokens before block separators. We assume that we already clean all 'new
     * line' that occurred more 2 times.
     * <br>
     * New line token is not erased if it followed a comment. 
     */
    private void cleanTokens() {
        int idx = 0;
        while (idx < tokens.size() - 2) {
            if (isWS(tokens.get(idx)) && isNewLine(tokens.get(idx + 1))) {
                tokens.remove(tokens.get(idx));
                idx--;
            } 
            else if (isNewLine(tokens.get(idx)) && isBlockSeparator(tokens.get(idx + 1)) && !isComment(idx-1))
            {
                tokens.remove(tokens.get(idx));
                idx--;
            } else {
                idx++;
            }
        }
    }

    /**
     * Returns true if token at idx is a line comment or is a block comment end.
     */
    private boolean isComment(int idx) 
    {
        return idx > 0 && (tokens.get(idx).getType() == LINE_COMMENT || tokens.get(idx).getType() == END_BLOCK_COMMENT);
    }
}
