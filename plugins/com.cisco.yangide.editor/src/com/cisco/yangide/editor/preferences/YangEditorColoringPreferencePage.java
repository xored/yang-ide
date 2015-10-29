/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.editor.preferences;

import org.eclipse.ui.IWorkbenchPreferencePage;

import com.cisco.yangide.editor.YangEditorPlugin;
import com.cisco.yangide.ui.YangUIPlugin;
import com.cisco.yangide.ui.preferences.OverlayPreferenceStore;

/**
 * @author Alexey Kholupko
 */
public class YangEditorColoringPreferencePage extends AbstractConfigurationBlockPreferencePage implements
        IWorkbenchPreferencePage {

    // TODO extarct to class ContextsIds @see IJavaHelpContextIds
    public static final String PREFIX = YangEditorPlugin.PLUGIN_ID + '.';
    public static final String YANG_EDITOR_PREFERENCE_PAGE = PREFIX + "yang_editor_preference_page_context"; //$NON-NLS-1$

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.cisco.yangide.editor.preferences.AbstractConfigurationBlockPreferencePage#setPreferenceStore
     * ()
     */
    @Override
    protected void setPreferenceStore() {
        setPreferenceStore(YangUIPlugin.getDefault().getPreferenceStore());

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.cisco.yangide.editor.preferences.YangEditorColoringPreferencePage#createConfigurationBlock
     * (com.cisco.yangide.editor.preferences.OverlayPreferenceStore)
     */
    @Override
    protected IPreferenceConfigurationBlock createConfigurationBlock(OverlayPreferenceStore overlayPreferenceStore) {
        return new YangEditorColoringConfigurationBlock(overlayPreferenceStore);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.cisco.yangide.editor.preferences.AbstractConfigurationBlockPreferencePage#getHelpId()
     */
    @Override
    protected String getHelpId() {
        // TODO
        return YangEditorColoringPreferencePage.YANG_EDITOR_PREFERENCE_PAGE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.cisco.yangide.editor.preferences.AbstractConfigurationBlockPreferencePage#setDescription
     * ()
     */
    @Override
    protected void setDescription() {
        String description = YangPreferencesMessages.YANGEditorPreferencePage_colors;
        setDescription(description);
    }
}
