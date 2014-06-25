/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Konstantin Zaitsev
 * @date Jun 24, 2014
 */
public class YangModelException extends CoreException {

    private static final long serialVersionUID = -1110448694670326045L;

    private CoreException nestedCoreException;

    public YangModelException(String message) {
        super(new Status(IStatus.ERROR, YangCore.PLUGIN_ID, message));
    }
    
    public YangModelException(Throwable e, int code) {
        super(new Status(code, YangCore.PLUGIN_ID, e.getMessage(), e));
    }

    public YangModelException(CoreException exception) {
        super(exception.getStatus());
        this.nestedCoreException = exception;
    }

    public Throwable getException() {
        if (this.nestedCoreException == null) {
            return getStatus().getException();
        } else {
            return this.nestedCoreException;
        }
    }
}
