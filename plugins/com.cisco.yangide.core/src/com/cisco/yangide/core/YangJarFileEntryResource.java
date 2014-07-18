/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJarEntryResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;

/**
 * Custom implementation of {@link IJarEntryResource} to open correct jar file from JDT perspective.
 *
 * @author Konstantin Zaitsev
 * @date Jul 7, 2014
 */
public class YangJarFileEntryResource extends PlatformObject implements IJarEntryResource, IStorage {

    private IPath path;
    private String entry;
    private IJavaProject project;

    public YangJarFileEntryResource(IJavaProject project, IPath path, String entry) {
        this.project = project;
        this.path = path;
        this.entry = entry;
    }

    @Override
    public InputStream getContents() throws CoreException {
        try (JarFile file = new JarFile(path.toFile())) {
            InputStream in = file.getInputStream(file.getEntry(entry));
            byte[] buff = new byte[1024];
            int len = 0;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while ((len = in.read(buff)) > 0) {
                out.write(buff, 0, len);
            }

            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new CoreException(new Status(IStatus.ERROR, YangCorePlugin.PLUGIN_ID, e.getMessage(), e));
        }
    }

    @Override
    public IPath getFullPath() {
        return new Path(entry);
    }

    @Override
    public String getName() {
        return new Path(entry).lastSegment();
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public IJarEntryResource[] getChildren() {
        return new IJarEntryResource[0];
    }

    @Override
    public Object getParent() {
        IPackageFragmentRoot root = getPackageFragmentRoot();
        Path p = new Path(entry);
        return resolveParent(root, p);
    }

    private Object resolveParent(IPackageFragmentRoot parent, IPath path) {
        if (path.segmentCount() > 0) {
            try {
                for (Object element : parent.getNonJavaResources()) {
                    if (element instanceof IJarEntryResource) {
                        IJarEntryResource res = (IJarEntryResource) element;
                        if (path.segment(0).equals(res.getName())) {
                            return resolveParent(res, path.removeFirstSegments(1));
                        }
                    }
                }
            } catch (Exception e) {
                YangCorePlugin.log(e);
            }
        }
        return parent;
    }

    private Object resolveParent(IJarEntryResource parent, IPath path) {
        if (path.segmentCount() > 0) {
            for (IJarEntryResource element : parent.getChildren()) {
                if (path.segment(0).equals(element.getName())) {
                    return resolveParent(element, path.removeFirstSegments(1));
                }
            }
        }
        return parent.getParent();
    }

    @Override
    public IPackageFragmentRoot getPackageFragmentRoot() {
        return project.getPackageFragmentRoot(path.toString());
    }

    @Override
    public boolean isFile() {
        return true; 
    }

    /**
     * @return the entry
     */
    public String getEntry() {
        return entry;
    }

    /**
     * @return the path
     */
    public IPath getPath() {
        return path;
    }
}
