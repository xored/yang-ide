/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;

import org.eclipse.core.resources.IEncodedStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;

import com.cisco.yangide.core.YangCorePlugin;

/**
 * @author Konstantin Zaitsev
 * @date Jul 7, 2014
 */
public class JarFileEntryStorage extends PlatformObject implements IEncodedStorage {

    private IPath path;
    private String entry;

    public JarFileEntryStorage(IPath path, String entry) {
        this.path = path;
        this.entry = entry;
    }

    @Override
    public InputStream getContents() throws CoreException {
        try {
            @SuppressWarnings("resource")
            JarFile file = new JarFile(path.toFile());
            return file.getInputStream(file.getEntry(entry));
        } catch (IOException e) {
            throw new CoreException(new Status(IStatus.ERROR, YangCorePlugin.PLUGIN_ID, e.getMessage(), e));
        }
    }

    @Override
    public IPath getFullPath() {
        return path;
    }

    @Override
    public String getName() {
        return entry;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public String getCharset() throws CoreException {
        return "UTF-8";
    }
}
