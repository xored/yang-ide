/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.refactoring;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 *
 * @author Konstantin Zaitsev
 * @date Jul 29, 2014
 */
public class YangRefactoringPlugin extends AbstractUIPlugin {

    /** The plug-in ID */
    public static final String PLUGIN_ID = "com.cisco.yangide.ext.refactoring"; //$NON-NLS-1$

    /** The shared instance */
    private static YangRefactoringPlugin plugin;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static YangRefactoringPlugin getDefault() {
        return plugin;
    }

    public IDialogSettings getDialogSettingsSection(String name) {
        IDialogSettings dialogSettings = getDialogSettings();
        IDialogSettings section = dialogSettings.getSection(name);
        if (section == null) {
            section = dialogSettings.addNewSection(name);
        }
        return section;
    }

    /**
     * Reports log to Error Log view.
     *
     * @param statusErrorID plugin related error ID
     * @param message error message
     */
    public static void log(Throwable e, String message) {
        log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, message, e));
    }

    /**
     * Reports log to Error Log view.
     *
     * @param statusErrorID plugin related error ID
     * @param message error message
     */
    public static void log(int statusErrorID, String message) {
        log(new Status(statusErrorID, PLUGIN_ID, message));
    }

    /**
     * Reports log to Error Log view.
     *
     * @param status status
     */
    public static void log(IStatus status) {
        getDefault().getLog().log(status);
    }

    /**
     * Reports exception to Error Log view.
     *
     * @param e exception
     */
    public static void log(Throwable e) {
        log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
    }
}
