package com.cisco.yangide.core;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.cisco.yangide.core.model.YangModel;

/**
 * The activator class controls the plug-in life cycle
 */
public class YangCore extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.cisco.yangide.core"; //$NON-NLS-1$

	// The shared instance
	private static YangCore plugin;
	
	/**
	 * The constructor
	 */
	public YangCore() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		YangModelManager.getYangModelManager().startup();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		try {
		    YangModelManager.getYangModelManager().shutdown();
		} finally {
		    super.stop(context);
		}
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static YangCore getDefault() {
		return plugin;
	}

    public static YangModel getYangModel() {
        return YangModelManager.getYangModelManager().getYangModel();
    }
}
