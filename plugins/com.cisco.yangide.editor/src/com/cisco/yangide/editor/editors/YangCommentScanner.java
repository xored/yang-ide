package com.cisco.yangide.editor.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.WhitespaceRule;

public class YangCommentScanner extends AbstractYangScanner {

    private static String[] tokenProperties= {
        IYangColorConstants.YANG_COMMENT
    };        
    
    /**
     * @param manager
     * @param store
     */
    public YangCommentScanner(IColorManager manager, IPreferenceStore store) {
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
        
        IToken comment = getToken(IYangColorConstants.YANG_COMMENT);
        rules.add(new MultiLineRule("/*", "*/", comment, '\\'));
        
        rules.add(new EndOfLineRule("//", comment));
        
        // Add generic whitespace rule.
        rules.add(new WhitespaceRule(new YangWhitespaceDetector()));

        return rules;
    }
}
