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
public class YangStringScanner extends AbstractYangScanner {

    private static String[] tokenProperties = { IYangColorConstants.YANG_STRING };

    /**
     * @param manager
     * @param store
     */
    public YangStringScanner(IColorManager manager, IPreferenceStore store) {
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

        IToken string = getToken(IYangColorConstants.YANG_STRING);
        setDefaultReturnToken(string);

        // Do not create rules here, they are defined in PartitionScanner
        // here we just define default token for string partition

        return rules;
    }
}
