/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.osgi.framework.BundleContext;

import com.cisco.yangide.ui.YangUIPlugin;

/**
 * The activator class controls the plug-in life cycle
 * @author Alexey Kholupko
 */
public class YangEditorPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "com.cisco.yangide.editor"; //$NON-NLS-1$

    // The shared instance
    private static YangEditorPlugin plugin;

    /**
     * The combined preference store.
     */
    private IPreferenceStore fCombinedPreferenceStore;

    /**
     * The constructor
     */
    public YangEditorPlugin() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static YangEditorPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    /**
     * Logs the specified throwable with this plug-in's log.
     * 
     * @param t throwable to log
     */
    public static void log(Throwable t) {
        IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, 500, "Error logged from YANG Editor: ", t); //$NON-NLS-1$
        getDefault().getLog().log(status);
    }

    private IWorkbenchPage internalGetActivePage() {
        IWorkbenchWindow window = getWorkbench().getActiveWorkbenchWindow();
        if (window == null)
            return null;
        return window.getActivePage();
    }

    public static IWorkbenchPage getActivePage() {
        return getDefault().internalGetActivePage();
    }

    /**
     * Returns a combined preference store, this store is read-only.
     * 
     * @return the combined preference store
     */
    public IPreferenceStore getCombinedPreferenceStore() {
        if (fCombinedPreferenceStore == null) {
            IPreferenceStore generalTextStore = EditorsUI.getPreferenceStore();
            fCombinedPreferenceStore = new ChainedPreferenceStore(new IPreferenceStore[] {
                    YangUIPlugin.getDefault().getPreferenceStore(), generalTextStore });
        }
        return fCombinedPreferenceStore;
    }

}
