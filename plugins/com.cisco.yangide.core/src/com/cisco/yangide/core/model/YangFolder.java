/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.core.model;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.cisco.yangide.core.IOpenable;
import com.cisco.yangide.core.OpenableElementInfo;
import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.YangModelException;

/**
 * @author Konstantin Zaitsev
 * @date Jun 24, 2014
 */
public class YangFolder extends YangElement {

    public static final String YANG_EXTENSION = "yang";
    private IResource resource;

    /**
     * @param resource
     * @param parent
     */
    public YangFolder(IResource resource, IOpenable parent) {
        super(parent);
        this.resource = resource;
    }

    @Override
    public IResource getResource() {
        return resource;
    }

    @Override
    public IPath getPath() {
        return resource.getFullPath().makeRelativeTo(getParent().getPath());
    }

    @Override
    protected boolean buildStructure(OpenableElementInfo info, IProgressMonitor pm,
            Map<IOpenable, OpenableElementInfo> newElements, IResource underlyingResource) throws YangModelException {
        final ArrayList<IOpenable> result = new ArrayList<IOpenable>();
        try {
            resource.accept(new IResourceVisitor() {
                public boolean visit(IResource res) throws CoreException {
                    if (res.getType() == IResource.FILE
                            && YANG_EXTENSION.equalsIgnoreCase(res.getFullPath().getFileExtension())) {
                        result.add(new YangFile((IFile) res, YangFolder.this));
                    }
                    return true;
                }
            }, IResource.DEPTH_ONE, false);
        } catch (CoreException e) {
            throw new YangModelException(e);
        }
        info.setChildren((IOpenable[]) result.toArray(new IOpenable[result.size()]));
        return true;
    }

    @Override
    protected IStatus validateExistence(IResource underlyingResource) {
        return underlyingResource.exists() && underlyingResource.isAccessible() ? Status.OK_STATUS : new Status(
                Status.ERROR, YangCorePlugin.PLUGIN_ID, "Does not exist");
    }

    @Override
    public YangElementType getElementType() {
        return YangElementType.YANG_FOLDER;
    }
}