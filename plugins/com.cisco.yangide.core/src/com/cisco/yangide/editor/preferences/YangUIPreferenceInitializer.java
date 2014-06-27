package com.cisco.yangide.editor.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.PlatformUI;

import com.cisco.yangide.editor.YangEditorPlugin;
import com.cisco.yangide.editor.editors.IYangColorConstants;

public class YangUIPreferenceInitializer extends AbstractPreferenceInitializer {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences
     * ()
     */
    public void initializeDefaultPreferences() {
        IPreferenceStore store = YangEditorPlugin.getDefault().getPreferenceStore();

        // store.setDefault(YangPreferenceConstants.P_STRING, "Default value");
        store.setDefault(YangPreferenceConstants.EDITOR_CLOSE_BRACES, true);
        store.setDefault(YangPreferenceConstants.EDITOR_SMART_PASTE, true);
        store.setDefault(YangPreferenceConstants.EDITOR_SMART_TAB, true);
        store.setDefault(YangPreferenceConstants.EDITOR_SMART_INDENT_AFTER_NEWLINE, true);        
        
        PreferenceConverter.setDefault(store, IYangColorConstants.YANG_COMMENT, new RGB(128, 32, 32));
        PreferenceConverter.setDefault(store, IYangColorConstants.YANG_IDENTIFIER, new RGB(0, 0, 0));
        PreferenceConverter.setDefault(store, IYangColorConstants.YANG_KEYWORD, new RGB(0, 0, 128));
        PreferenceConverter.setDefault(store, IYangColorConstants.YANG_STRING, new RGB(0, 128, 0));
        PreferenceConverter.setDefault(store, IYangColorConstants.YANG_TYPE, new RGB(0, 128, 128));
        PreferenceConverter.setDefault(store, IYangColorConstants.YANG_NUMBER, new RGB(128, 0, 128));
        
        PreferenceConverter.setDefault(store, IYangColorConstants.EDITOR_MATCHING_BRACKETS_COLOR, new RGB(200, 200, 200));
        //
    }

}
