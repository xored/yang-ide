/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.model;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaCore;

import com.cisco.yangide.core.IOpenable;
import com.cisco.yangide.core.Openable;
import com.cisco.yangide.core.OpenableElementInfo;
import com.cisco.yangide.core.YangModelException;

/**
 * @author Konstantin Zaitsev
 * @date Jun 25, 2014
 */
public class YangModel extends Openable {

    public YangModel() {
        super(null);
    }

    @Override
    protected boolean buildStructure(OpenableElementInfo info, IProgressMonitor pm,
            Map<IOpenable, OpenableElementInfo> newElements, IResource underlyingResource) throws YangModelException {

        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        int length = projects.length;
        IOpenable[] children = new IOpenable[length];
        int index = 0;
        for (int i = 0; i < length; i++) {
            IProject project = projects[i];
            try {
                if (project.hasNature(JavaCore.NATURE_ID)) {
                    children[index++] = new YangProject(project, this);
                }
            } catch (CoreException e) {
                throw new YangModelException(e);
            }
        }
        if (index < length) {
            System.arraycopy(children, 0, children = new IOpenable[index], 0, index);
        }

        info.setChildren(children);

        newElements.put(this, info);

        return true;
    }

    public YangProject[] getYangProjects() throws YangModelException {
        IOpenable[] children = getChildren();
        YangProject[] projects = new YangProject[children.length];
        for (int i = 0; i < projects.length; i++) {
            projects[i] = (YangProject) children[i];
        }
        return projects;
    }
    
    @Override
    protected IStatus validateExistence(IResource underlyingResource) {
        return Status.OK_STATUS;
    }

    @Override
    public IPath getPath() {
        return Path.ROOT;
    }

}
