/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.editor.dialogs;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;

import com.cisco.yangide.editor.YangEditorPlugin;

/**
 * A settable IStatus. Can be an error, warning, info or ok. For error, info and warning states, a
 * message describes the problem.
 * 
 * @author Alexey Kholupko
 */
public class StatusInfo implements IStatus {

    public static final IStatus OK_STATUS = new StatusInfo();

    private String fStatusMessage;
    private int fSeverity;

    /**
     * Creates a status set to OK (no message)
     */
    public StatusInfo() {
        this(OK, null);
    }

    /**
     * Creates a status .
     * 
     * @param severity: ERROR, WARNING, INFO and OK.
     * @param message The message of the status. Applies only for ERROR, WARNING and INFO.
     */
    public StatusInfo(int severity, String message) {
        fStatusMessage = message;
        fSeverity = severity;
    }

    public boolean isOK() {
        return fSeverity == IStatus.OK;
    }

    public boolean isWarning() {
        return fSeverity == IStatus.WARNING;
    }

    public boolean isInfo() {
        return fSeverity == IStatus.INFO;
    }

    public boolean isError() {
        return fSeverity == IStatus.ERROR;
    }

    /**
     * @see IStatus#getMessage
     */
    public String getMessage() {
        return fStatusMessage;
    }

    public void setError(String errorMessage) {
        Assert.isNotNull(errorMessage);
        fStatusMessage = errorMessage;
        fSeverity = IStatus.ERROR;
    }

    public void setWarning(String warningMessage) {
        Assert.isNotNull(warningMessage);
        fStatusMessage = warningMessage;
        fSeverity = IStatus.WARNING;
    }

    public void setInfo(String infoMessage) {
        Assert.isNotNull(infoMessage);
        fStatusMessage = infoMessage;
        fSeverity = IStatus.INFO;
    }

    public void setOK() {
        fStatusMessage = null;
        fSeverity = IStatus.OK;
    }

    /*
     * @see IStatus#matches(int)
     */
    public boolean matches(int severityMask) {
        return (fSeverity & severityMask) != 0;
    }

    /**
     * Returns always <code>false</code>.
     * 
     * @see IStatus#isMultiStatus()
     */
    public boolean isMultiStatus() {
        return false;
    }

    /*
     * @see IStatus#getSeverity()
     */
    public int getSeverity() {
        return fSeverity;
    }

    /*
     * @see IStatus#getPlugin()
     */
    public String getPlugin() {
        return YangEditorPlugin.PLUGIN_ID;
    }

    /**
     * Returns always <code>null</code>.
     * 
     * @see IStatus#getException()
     */
    public Throwable getException() {
        return null;
    }

    /**
     * Returns always the error severity.
     * 
     * @see IStatus#getCode()
     */
    public int getCode() {
        return fSeverity;
    }

    /**
     * Returns always an empty array.
     * 
     * @see IStatus#getChildren()
     */
    public IStatus[] getChildren() {
        return new IStatus[0];
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("StatusInfo ");
        if (fSeverity == OK) {
            buf.append("OK");
        } else if (fSeverity == ERROR) {
            buf.append("ERROR");
        } else if (fSeverity == WARNING) {
            buf.append("WARNING");
        } else if (fSeverity == INFO) {
            buf.append("INFO");
        } else {
            buf.append("severity=");
            buf.append(fSeverity);
        }
        buf.append(": ");
        buf.append(fStatusMessage);
        return buf.toString();
    }
}
