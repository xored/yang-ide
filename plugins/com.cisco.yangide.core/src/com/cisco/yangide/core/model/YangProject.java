/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaCore;

import com.cisco.yangide.core.IOpenable;
import com.cisco.yangide.core.OpenableElementInfo;
import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.YangModelException;

/**
 * @author Konstantin Zaitsev
 * @date Jun 24, 2014
 */
public class YangProject extends YangElement {

    private IProject project;

    /**
     * @param parent
     */
    public YangProject(IProject project, IOpenable parent) {
        super(parent);
        this.project = project;
    }

    @Override
    protected boolean buildStructure(OpenableElementInfo info, IProgressMonitor pm,
            Map<IOpenable, OpenableElementInfo> newElements, IResource underlyingResource) throws YangModelException {
        final HashSet<IResource> resources = new HashSet<IResource>();
        try {
            project.accept(new IResourceVisitor() {
                public boolean visit(IResource resource) throws CoreException {
                    if (YangFolder.YANG_EXTENSION.equalsIgnoreCase(resource.getFullPath().getFileExtension())) {
                        resources.add(resource.getParent());
                    }
                    return true;
                }
            });
        } catch (CoreException e) {
            throw new YangModelException(e);
        }
        ArrayList<IOpenable> result = new ArrayList<IOpenable>();
        for (IResource resource : resources) {
            if (resource.getType() == IResource.FOLDER) {
                result.add(new YangFolder(resource, this));
            }
        }
        info.setChildren((IOpenable[]) result.toArray(new IOpenable[result.size()]));
        return true;
    }

    @Override
    public IResource getResource() {
        return project;
    }

    @Override
    public IPath getPath() {
        return project.getFullPath();
    }

    @Override
    public YangElementType getElementType() {
        return YangElementType.YANG_PROJECT;
    }

    @Override
    protected IStatus validateExistence(IResource underlyingResource) {
        // check whether the java project can be opened
        if (!YangCorePlugin.isYangProject((IProject) underlyingResource)) {
            return new Status(Status.ERROR, YangCorePlugin.PLUGIN_ID, "Does not exist");
        }
        return Status.OK_STATUS;
    }
}
