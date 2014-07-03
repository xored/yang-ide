package com.cisco.yangide.editor.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;

import com.cisco.yangide.editor.YangEditorPlugin;
import com.cisco.yangide.ui.YangUIPlugin;
import com.cisco.yangide.ui.preferences.OverlayPreferenceStore;

public class YangEditorColoringPreferencePage extends AbstractConfigurationBlockPreferencePage implements IWorkbenchPreferencePage {

    //TODO extarct to class ContextsIds @see IJavaHelpContextIds
    public static final String PREFIX = YangEditorPlugin.PLUGIN_ID + '.';
    public static final String YANG_EDITOR_PREFERENCE_PAGE= PREFIX + "yang_editor_preference_page_context"; //$NON-NLS-1$

    /* (non-Javadoc)
     * @see com.cisco.yangide.editor.preferences.AbstractConfigurationBlockPreferencePage#setPreferenceStore()
     */
    @Override
    protected void setPreferenceStore() {
        setPreferenceStore(YangUIPlugin.getDefault().getPreferenceStore());
        
    }
    
    /* (non-Javadoc)
     * @see com.cisco.yangide.editor.preferences.JavaEditorColoringPreferencePage#createConfigurationBlock(com.cisco.yangide.editor.preferences.OverlayPreferenceStore)
     */
    @Override
    protected IPreferenceConfigurationBlock createConfigurationBlock(OverlayPreferenceStore overlayPreferenceStore) {
        return new YangEditorColoringConfigurationBlock(overlayPreferenceStore);
    }
    
    /* (non-Javadoc)
     * @see com.cisco.yangide.editor.preferences.AbstractConfigurationBlockPreferencePage#performOk()
     */
    @Override
    public boolean performOk() {
        //JavaPlugin.flushInstanceScope();
        //XXX to change coloring after @apply
        //doesnot work
        try {
            InstanceScope.INSTANCE.getNode(YangEditorPlugin.PLUGIN_ID).flush();
        } catch (BackingStoreException e) {
            YangEditorPlugin.log(e);
        }
        return super.performOk();
    }

    /* (non-Javadoc)
     * @see com.cisco.yangide.editor.preferences.AbstractConfigurationBlockPreferencePage#getHelpId()
     */
    @Override
    protected String getHelpId() {
        // TODO 
        return YangEditorColoringPreferencePage.YANG_EDITOR_PREFERENCE_PAGE;
    }

    /* (non-Javadoc)
     * @see com.cisco.yangide.editor.preferences.AbstractConfigurationBlockPreferencePage#setDescription()
     */
    @Override
    protected void setDescription() {
        // TODO 
        String description= YangPreferencesMessages.YANGEditorPreferencePage_colors;
        setDescription(description);        
    }
}