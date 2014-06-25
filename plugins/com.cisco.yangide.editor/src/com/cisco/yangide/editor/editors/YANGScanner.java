package com.cisco.yangide.editor.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.ui.text.AbstractJavaScanner;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.IJavaColorConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser;

public class YANGScanner extends AbstractJavaScanner{

    private static String[] tokenProperties= {
        IYANGColorConstants.YANG_STRING,
    };    
    
    /**
     * @param manager
     * @param store
     */
    public YANGScanner(IColorManager manager, IPreferenceStore store) {
        super(manager, store);
        initialize();
    }

    private static final int DEFAULT_RULES_COUNT = 3;


    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.text.AbstractJavaScanner#getTokenProperties()
     */
    @Override
    protected String[] getTokenProperties() {
        return tokenProperties;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.text.AbstractJavaScanner#createRules()
     */
    @Override
    protected List<IRule> createRules() {

        List<IRule> rules= new ArrayList<IRule>();        
        // Add generic whitespace rule.
        rules.add(new WhitespaceRule(new YANGWhitespaceDetector()));

        IToken string = getToken(IYANGColorConstants.YANG_STRING);

        WordRule wordRule = new WordRule(new YangWordDetector(), string);

        //TODO hardcode kewords etc.
        // for (int i = 0; i < YangParser.tokenNames.length; i++) {
        //      String word = YangParser.tokenNames[i];
        //      wordRule.addWord(word.replace("'", ""), keyword);
        // }
        
        rules.add(wordRule);        

        return rules;
    }
}
