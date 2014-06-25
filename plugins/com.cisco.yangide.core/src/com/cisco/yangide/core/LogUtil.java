/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Konstantin Zaitsev
 * @date Jun 24, 2014
 */
public final class LogUtil {

    public static void log(Throwable e, String message) {
        Throwable nestedException;
        if (e instanceof YangModelException && (nestedException = ((YangModelException) e).getException()) != null) {
            e = nestedException;
        }
        log(new Status(IStatus.ERROR, YangCore.PLUGIN_ID, IStatus.ERROR, message, e));
    }

    public static void log(int statusErrorID, String message) {
        log(new Status(statusErrorID, YangCore.PLUGIN_ID, message));
    }

    /*
     * Add a log entry
     */
    public static void log(IStatus status) {
        YangCore.getDefault().getLog().log(status);
    }

    public static void log(Throwable e) {
        log(new Status(IStatus.ERROR, YangCore.PLUGIN_ID, e.getMessage(), e));
    }
}
