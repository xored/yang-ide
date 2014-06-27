/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

import com.cisco.yangide.core.model.YangModel;

/**
 * The activator class controls the plug-in life cycle
 */
public class YangCorePlugin extends Plugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "com.cisco.yangide.core"; //$NON-NLS-1$

    // The shared instance
    private static YangCorePlugin plugin;

    /**
     * The constructor
     */
    public YangCorePlugin() {
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
    public static YangCorePlugin getDefault() {
        return plugin;
    }

    public static YangModel getYangModel() {
        return YangModelManager.getYangModelManager().getYangModel();
    }

    public static void log(Throwable e, String message) {
        Throwable nestedException;
        if (e instanceof YangModelException && (nestedException = ((YangModelException) e).getException()) != null) {
            e = nestedException;
        }
        log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, message, e));
    }

    public static void log(int statusErrorID, String message) {
        log(new Status(statusErrorID, PLUGIN_ID, message));
    }

    /*
     * Add a log entry
     */
    public static void log(IStatus status) {
        getDefault().getLog().log(status);
    }

    public static void log(Throwable e) {
        log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
    }
}
