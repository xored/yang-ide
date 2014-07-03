/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.indexing;

import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaProject;

import com.cisco.yangide.core.CoreUtil;

/**
 * @author Konstantin Zaitsev
 * @date Jul 1, 2014
 */
@SuppressWarnings("restriction")
public class IndexAllProject extends IndexRequest {

    private IProject project;

    public IndexAllProject(IProject project, IndexManager manager) {
        super(project.getFullPath(), manager);
        this.project = project;
    }

    public boolean equals(Object o) {
        if (o instanceof IndexAllProject) {
            return this.project.equals(((IndexAllProject) o).project);
        }
        return false;
    }

    public boolean execute(IProgressMonitor progressMonitor) {

        if (this.isCancelled || progressMonitor != null && progressMonitor.isCanceled()) {
            return true;
        }

        if (!this.project.isAccessible()) {
            return true;
        }
        final HashSet<IPath> ignoredPath = new HashSet<IPath>();
        try {
            JavaProject proj = (JavaProject) JavaCore.create(project);
            if (proj != null) {
                IClasspathEntry[] classpath = proj.getResolvedClasspath();
                for (int i = 0, length = classpath.length; i < length; i++) {
                    IClasspathEntry entry = classpath[i];
                    IPath entryPath = entry.getPath();
                    IPath output = entry.getOutputLocation();
                    if (output != null && !entryPath.equals(output)) {
                        ignoredPath.add(output);
                    }
                }
            }
        } catch (JavaModelException e) {
            // java project doesn't exist: ignore
        }

        try {
            final HashSet<IFile> indexedFiles = new HashSet<IFile>();
            project.accept(new IResourceProxyVisitor() {
                public boolean visit(IResourceProxy proxy) {
                    if (IndexAllProject.this.isCancelled) {
                        return false;
                    }
                    if (!ignoredPath.isEmpty() && ignoredPath.contains(proxy.requestFullPath())) {
                        return false;
                    }
                    if (proxy.getType() == IResource.FILE) {
                        if (CoreUtil.isYangLikeFileName(proxy.getName())) {
                            IFile file = (IFile) proxy.requestResource();
                            indexedFiles.add(file);
                        }
                        return false;
                    }
                    return true;
                }
            }, IResource.NONE);

            for (IFile iFile : indexedFiles) {
                this.manager.addSource(iFile);
            }
        } catch (CoreException e) {
            this.manager.removeIndex(this.containerPath);
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.project.hashCode();
    }

    public String toString() {
        return "indexing project " + this.project.getFullPath(); //$NON-NLS-1$
    }
}
