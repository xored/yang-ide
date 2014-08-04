/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.m2e.yang;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.ui.YangUIPlugin;
import com.cisco.yangide.ui.preferences.YangPreferenceConstants;

/**
 * @author Konstantin Zaitsev
 * @date Jul 2, 2014
 */
public class YangM2EPlugin extends Plugin implements BundleActivator {
    // The plug-in ID
    public static final String PLUGIN_ID = "com.cisco.yangide.m2e.yang"; //$NON-NLS-1$

    public static final String YANG_FILES_ROOT_DIR = "yangFilesRootDir";
    public static final String YANG_FILES_ROOT_DIR_DEFAULT = "src/main/yang";
    public static final String YANG_MAVEN_PLUGIN = "yang-maven-plugin";
    public static final String YANG_CODE_GENERATORS = "codeGenerators";

    // The shared instance
    private static YangM2EPlugin plugin;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        YangCorePlugin.getDefault();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static YangM2EPlugin getDefault() {
        return plugin;
    }

    public static void traceTime(String category, String message, long start, long end) {
        if (YangUIPlugin.getDefault().getPreferenceStore().getBoolean(YangPreferenceConstants.ENABLE_TRACING)) {
            Status status = new Status(IStatus.INFO, YangM2EPlugin.PLUGIN_ID, "[" + category + "] " + message + ": "
                    + (end - start) + "ms");
            YangM2EPlugin.getDefault().getLog().log(status);
        }
    }
}
