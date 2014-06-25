package com.cisco.yangide.editor.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jdt.internal.ui.JavaUIPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.PlatformUI;

import com.cisco.yangide.editor.YANGEditorPlugin;
import com.cisco.yangide.editor.editors.IYANGColorConstants;

public class YANGUIPreferenceInitializer extends AbstractPreferenceInitializer {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences
     * ()
     */
    public void initializeDefaultPreferences() {
        IPreferenceStore store = YANGEditorPlugin.getDefault().getPreferenceStore();
        // store.setDefault(YANGPreferenceConstants.P_BOOLEAN, true);
        // store.setDefault(YANGPreferenceConstants.P_CHOICE, "choice2");
        // store.setDefault(YANGPreferenceConstants.P_STRING, "Default value");

        
        PreferenceConverter.setDefault(store, IYANGColorConstants.YANG_COMMENT, new RGB(128, 0, 0));
        PreferenceConverter.setDefault(store, IYANGColorConstants.YANG_IDENTIFIER, new RGB(128, 128, 128));
        PreferenceConverter.setDefault(store, IYANGColorConstants.YANG_KEYWORD, new RGB(0, 0, 128));
        PreferenceConverter.setDefault(store, IYANGColorConstants.YANG_STRING, new RGB(0, 128, 0));
        //
    }

}
