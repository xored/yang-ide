package com.cisco.yangide.editor.editors;

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;

public class YangStringScanner extends BufferedRuleBasedScanner {

    public YangStringScanner(IColorManager manager) {
        IToken string = new Token(new TextAttribute(manager.getColor(IYangColorConstants.YANG_STRING)));

        IRule[] rules = new IRule[3];

        // Add rule for double quotes
        rules[0] = new MultiLineRule("\"", "\"", string, '\\');
        // Add a rule for single quotes
        rules[1] = new MultiLineRule("'", "'", string, '\\');
        // Add generic whitespace rule.
        rules[2] = new WhitespaceRule(new YangWhitespaceDetector());

        setRules(rules);
    }
}
