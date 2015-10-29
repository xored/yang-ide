/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.core.parser;

import java.util.BitSet;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.eclipse.core.resources.IProject;
import org.opendaylight.yangtools.antlrv4.code.gen.YangLexer;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.YangContext;

import com.cisco.yangide.core.dom.Module;

/**
 * @author Konstantin Zaitsev
 * @date Jul 9, 2014
 */
public class YangParserUtil {

    // protect from initializaiton
    private YangParserUtil() {
        // empty block
    }

    /**
     * Parses YANG file contents and returns AST tree as {@link Module}
     *
     * @param chars file contents
     * @return AST Tree
     */
    public static Module parseYangFile(char[] chars) {
        YangContext yangContext = parseYangSource(chars, null);
        YangParserModelListener modelListener = new YangParserModelListener();
        ParseTreeWalker.DEFAULT.walk(modelListener, yangContext);
        return modelListener.getModule();
    }

    /**
     * @param chars
     * @param project
     * @param validationListener
     * @return
     */
    public static Module parseYangFile(char[] chars, IProject project, IYangValidationListener validationListener) {
        YangContext yangContext = parseYangSource(chars, validationListener);
        if (validationListener != null) {
            validateYangContext(yangContext, validationListener);
        }
        YangParserModelListener modelListener = new YangParserModelListener();
        ParseTreeWalker.DEFAULT.walk(modelListener, yangContext);
        Module module = modelListener.getModule();
        if (validationListener != null) {
            new SemanticValidations(validationListener, project, module).validate();
        }
        return module;
    }

    public static void validateYangContext(YangContext context, IYangValidationListener validationListener) {
        final ParseTreeWalker walker = new ParseTreeWalker();
        final YangModelBasicValidationListener yangModelParser = new YangModelBasicValidationListener();
        try {
            walker.walk(yangModelParser, context);
        } catch (YangValidationException e) {
            if (validationListener != null) {
                int lineNumber = -1;
                int charStart = 0;
                int charEnd = 0;
                if (e.getContext() instanceof ParserRuleContext) {
                    Token token = ((ParserRuleContext) e.getContext()).getStart();
                    lineNumber = token.getLine();
                    charStart = token.getStartIndex();
                    charEnd = token.getStopIndex() + 1;
                }
                validationListener.validationError(e.getMessage(), lineNumber, charStart, charEnd);
            }
        }
    }

    public static void validateYangFile(char[] content, IProject project, IYangValidationListener validationListener) {
        YangContext parseTree = parseYangSource(content, validationListener);
        validateYangContext(parseTree, validationListener);

        YangParserModelListener modelListener = new YangParserModelListener();
        ParseTreeWalker.DEFAULT.walk(modelListener, parseTree);
        new SemanticValidations(validationListener, project, modelListener.getModule()).validate();
    }

    public static YangContext parseYangSource(char[] content, final IYangValidationListener validationListener) {
        final ANTLRInputStream input = new ANTLRInputStream(content, content.length);
        final YangLexer lexer = new YangLexer(input);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final YangParser parser = new YangParser(tokens);
        parser.removeErrorListeners();
        if (validationListener != null) {
            parser.addErrorListener(new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                        int charPositionInLine, String msg, RecognitionException e) {

                    int charStart = 0;
                    int charEnd = 0;
                    if (offendingSymbol != null && offendingSymbol instanceof Token) {
                        charStart = ((Token) offendingSymbol).getStartIndex();
                        charEnd = ((Token) offendingSymbol).getStopIndex() + 1;
                    }
                    validationListener.syntaxError(msg, line, charStart, charEnd);
                }
            });
        }
        return parser.yang();
    }

    public static String formatYangSource(YangFormattingPreferences preferences, char[] content, int indentationLevel,
            String lineSeparator) {
        ANTLRInputStream input = new ANTLRInputStream(content, content.length);
        final YangLexer lexer = new YangLexer(input) {
            @Override
            public void skip() {
                // disable skipping of comment tokens
            }
        };
        LexerErrorListener errorListener = new LexerErrorListener();
        lexer.addErrorListener(errorListener);
        final BufferedTokenStream tokens = new BufferedTokenStream(lexer);
        final ITokenFormatter formatter = new YangTokenFormatter(preferences, indentationLevel, lineSeparator);
        while (tokens.LT(1).getType() != IntStream.EOF) {
            formatter.process(tokens.LT(1));
            tokens.consume();
        }
        if (errorListener.isErrorDetected()) {
            // Source that contains parsing errors should never be formatted
            return String.valueOf(content);
        }
        return formatter.getFormattedContent();
    }

    private static final class LexerErrorListener extends BaseErrorListener {
        private boolean anErrorDetected = false;

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
                String msg, RecognitionException e) {
            anErrorDetected = true;
        }

        @Override
        public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet ambigAlts,
                ATNConfigSet configs) {
            anErrorDetected = true;
        }
        
        public boolean isErrorDetected() {
            return anErrorDetected;
        }
    }
}
