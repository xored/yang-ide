/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.core.model;

import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.cisco.yangide.core.IOpenable;
import com.cisco.yangide.core.OpenableElementInfo;
import com.cisco.yangide.core.YangModelException;

/**
 * @author Konstantin Zaitsev
 * @date Jul 07, 2014
 */
public class YangJarFile extends YangElement {
    private IPath path;

    /**
     * @param resource
     * @param parent
     */
    public YangJarFile(IPath path, IOpenable parent) {
        super(parent);
        this.path = path;
    }

    @Override
    protected boolean buildStructure(OpenableElementInfo info, IProgressMonitor pm,
            Map<IOpenable, OpenableElementInfo> newElements, IResource underlyingResource) throws YangModelException {
        return true;
    }

    @Override
    protected IStatus validateExistence(IResource underlyingResource) {
        return Status.OK_STATUS;
    }

    @Override
    public IPath getPath() {
        return path;
    }

    @Override
    public YangElementType getElementType() {
        return YangElementType.YANG_JAR_FILE;
    }
}
