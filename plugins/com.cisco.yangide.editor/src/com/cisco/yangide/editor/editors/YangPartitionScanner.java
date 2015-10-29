/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.editor.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * @author Alexey Kholupko
 */
public class YangPartitionScanner extends RuleBasedPartitionScanner {

    /**
     * Double qoute string partition
     */    
    public final static String YANG_STRING = "__yang_string";
    /**
     * Single qoute string partition 
     */
    public final static String YANG_STRING_SQ = "__yang_string_sq"; 
    public final static String YANG_COMMENT = "__yang_comment";

    public YangPartitionScanner() {

        List<IPredicateRule> rules = new ArrayList<IPredicateRule>();

        IToken comment = new Token(YANG_COMMENT);
        rules.add(new MultiLineRule("/*", "*/", comment, (char) 0, true));
        rules.add(new EndOfLineRule("//", comment));

        IToken multiLineString = new Token(YANG_STRING);
        // TODO escaping " or ' in each other sequence
        rules.add(new MultiLineRule("\"", "\"", multiLineString, '\\', true));
        IToken multiLineString_sq = new Token(YANG_STRING_SQ);
        rules.add(new MultiLineRule("'", "'", multiLineString_sq, '\\', true));

        IPredicateRule[] result = new IPredicateRule[rules.size()];
        rules.toArray(result);
        setPredicateRules(result);
    }
}
