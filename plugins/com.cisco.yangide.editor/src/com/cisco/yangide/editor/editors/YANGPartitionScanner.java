package com.cisco.yangide.editor.editors;

import org.eclipse.jface.text.rules.*;
/**
 * Still dont understand purpose 
 */
public class YANGPartitionScanner extends RuleBasedPartitionScanner {

	public final static String YANG_STRING = "__yang_string";
	public final static String YANG_COMMENT = "__yang_comment";
	public final static String YANG_KEYWORD = "__yang_keyword";
	public final static String YANG_IDENTIFIER = "__yang_identifier";	

	public YANGPartitionScanner() {

		IToken string = new Token(YANG_STRING);
		

		IPredicateRule[] rules = new IPredicateRule[1];

		//rules[0] = new MultiLineRule("<!--", "-->", xmlComment);
		rules[0] = new MultiLineRule("\"", "\"", string);

		setPredicateRules(rules);
	}
}
