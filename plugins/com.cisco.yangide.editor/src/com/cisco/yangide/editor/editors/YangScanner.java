/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.NumberRule;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.cisco.yangide.ui.preferences.IYangColorConstants;

/**
 * @author Alexey Kholupko
 */
public class YangScanner extends AbstractYangScanner {

    private static String[] tokenProperties = { IYangColorConstants.YANG_IDENTIFIER, IYangColorConstants.YANG_KEYWORD,
            IYangColorConstants.YANG_TYPE, IYangColorConstants.YANG_NUMBER, };

    static String[] keywords = { "yin-element", "yang-version", "when", "value", "uses", "units", "unique", "typedef",
            "type", "submodule", "status", "rpc", "revision-date", "revision", "require-instance", "refine",
            "reference", "range", "presence", "prefix", "position", "pattern", "path", "output", "organization",
            "ordered-by", "notification", "namespace", "must", "module", "min-elements", "max-elements", "mandatory",
            "list", "length", "leaf-list", "leaf", "key", "input", "include", "import", "if-feature", "identity",
            "grouping", "fraction-digits", "feature", "deviate", "deviation", "extension", "error-message",
            "error-app-tag", "enum", "description", "default", "container", "contact", "config", "choice", "case",
            "bit", "belongs-to", "base", "augment", "argument", "anyxml" };

    static String[] types = { "binary", "bits", "boolean", "decimal64", "empty", "enumeration", "identityref",
            "instance-identifier", "int8", "int16", "int32", "int64", "leafref", "string", "uint8", "uint16", "uint32",
            "uint64", "union" };

    public static String[] getKeywords() {
        return keywords;
    }

    public static String[] getTypes() {
        return types;
    }

    /**
     * @param manager
     * @param store
     */
    public YangScanner(IColorManager manager, IPreferenceStore store) {
        super(manager, store);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.cisco.yangide.editor.editors.AbstractYangScanner#getTokenProperties()
     */
    @Override
    protected String[] getTokenProperties() {
        return tokenProperties;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.cisco.yangide.editor.editors.AbstractYangScanner#createRules()
     */
    @Override
    protected List<IRule> createRules() {

        List<IRule> rules = new ArrayList<IRule>();
        // Add generic whitespace rule.
        rules.add(new WhitespaceRule(new YangWhitespaceDetector()));

        IToken number = getToken(IYangColorConstants.YANG_NUMBER);
        NumberRule numberRule = new NumberRule(number);
        rules.add(numberRule);

        IToken identifier = getToken(IYangColorConstants.YANG_IDENTIFIER);
        IToken keyword = getToken(IYangColorConstants.YANG_KEYWORD);
        IToken type = getToken(IYangColorConstants.YANG_TYPE);

        setDefaultReturnToken(identifier);

        WordRule wordRule = new WordRule(new YangWordDetector(), identifier);

        for (int i = 0; i < keywords.length; i++) {
            String word = keywords[i];
            wordRule.addWord(word, keyword);
        }

        for (int i = 0; i < getTypes().length; i++) {
            String word = getTypes()[i];
            wordRule.addWord(word, type);
        }

        rules.add(wordRule);

        return rules;
    }
}
