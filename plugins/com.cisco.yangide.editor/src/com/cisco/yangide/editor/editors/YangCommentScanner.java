package com.cisco.yangide.editor.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;

import com.cisco.yangide.ui.preferences.IYangColorConstants;

/**
 * @author Alexey Kholupko
 */
public class YangCommentScanner extends AbstractYangScanner {

    private static String[] tokenProperties = { IYangColorConstants.YANG_COMMENT };

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
        setDefaultReturnToken(comment);

        /*
         * @see com.cisco.yangide.editor.editors.YangStringScanner#createRules()
         */

        return rules;
    }
}
