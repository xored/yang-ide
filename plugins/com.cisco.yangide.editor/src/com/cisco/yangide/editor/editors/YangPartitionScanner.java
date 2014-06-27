package com.cisco.yangide.editor.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * @author Alexey Kholupko
 */
public class YangPartitionScanner extends RuleBasedPartitionScanner {

    public final static String YANG_STRING = "__yang_string";
    public final static String YANG_COMMENT = "__yang_comment";

    public YangPartitionScanner() {

        List<IPredicateRule> rules = new ArrayList<IPredicateRule>();

        IToken multiLinestring = new Token(YANG_STRING);
        // TODO escaping " or ' in each other sequence
        rules.add(new MultiLineRule("\"", "\"", multiLinestring, '\\'));
        rules.add(new MultiLineRule("'", "'", multiLinestring, '\\'));

        IToken comment = new Token(YANG_COMMENT);
        rules.add(new MultiLineRule("/*", "*/", comment));

        IPredicateRule[] result = new IPredicateRule[rules.size()];
        rules.toArray(result);
        setPredicateRules(result);
    }
}
