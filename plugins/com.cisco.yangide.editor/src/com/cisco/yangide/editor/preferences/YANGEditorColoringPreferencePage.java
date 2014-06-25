package com.cisco.yangide.editor.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.internal.ui.preferences.IPreferenceConfigurationBlock;
import org.eclipse.jdt.internal.ui.preferences.JavaEditorColoringPreferencePage;
import org.eclipse.jdt.internal.ui.preferences.OverlayPreferenceStore;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;

import com.cisco.yangide.editor.YANGEditorPlugin;

public class YANGEditorColoringPreferencePage extends JavaEditorColoringPreferencePage implements IWorkbenchPreferencePage {


    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.preferences.AbstractConfigurationBlockPreferencePage#setPreferenceStore()
     */
    @Override
    protected void setPreferenceStore() {
        setPreferenceStore(YANGEditorPlugin.getDefault().getPreferenceStore());
        
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.preferences.JavaEditorColoringPreferencePage#createConfigurationBlock(org.eclipse.jdt.internal.ui.preferences.OverlayPreferenceStore)
     */
    @Override
    protected IPreferenceConfigurationBlock createConfigurationBlock(OverlayPreferenceStore overlayPreferenceStore) {
        return new YANGEditorColoringConfigurationBlock(overlayPreferenceStore);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.preferences.AbstractConfigurationBlockPreferencePage#performOk()
     */
    @Override
    public boolean performOk() {
        //JavaPlugin.flushInstanceScope();
        //XXX to change coloring after @apply
        //doesnot work
        try {
            InstanceScope.INSTANCE.getNode(YANGEditorPlugin.PLUGIN_ID).flush();
        } catch (BackingStoreException e) {
            YANGEditorPlugin.log(e);
        }
        return super.performOk();
    }
}