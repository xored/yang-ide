/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.model;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.cisco.yangide.core.IOpenable;
import com.cisco.yangide.core.OpenableElementInfo;
import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.core.buffer.BufferManager;
import com.cisco.yangide.core.buffer.IBuffer;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.internal.YangParserUtil;

/**
 * @author Konstantin Zaitsev
 * @date Jun 24, 2014
 */
public class YangFile extends YangElement {
    private IFile resource;

    /**
     * @param resource
     * @param parent
     */
    public YangFile(IFile resource, IOpenable parent) {
        super(parent);
        this.resource = resource;
    }

    public char[] getContents() throws YangModelException {
        IBuffer buffer = getBuffer();
        return buffer.getCharacters();
    }

    @Override
    protected boolean buildStructure(OpenableElementInfo info, IProgressMonitor pm,
            Map<IOpenable, OpenableElementInfo> newElements, IResource underlyingResource) throws YangModelException {
        // ensure buffer is opened
        IBuffer buffer = getBufferManager().getBuffer(this);
        if (buffer == null) {
            buffer = openBuffer(pm, info);
        }

        Module module = YangParserUtil.parseYangFile(buffer.getCharacters(), null);
        ((YangFileInfo) info).setModule(module);
        info.setIsStructureKnown(true);
        return true;
    }

    @Override
    protected IStatus validateExistence(IResource underlyingResource) {
        return underlyingResource.exists() && underlyingResource.isAccessible() ? Status.OK_STATUS : new Status(
                Status.ERROR, YangCorePlugin.PLUGIN_ID, "Does not exist");
    }

    @Override
    protected boolean hasBuffer() {
        return true;
    }

    @Override
    protected OpenableElementInfo createElementInfo() {
        return new YangFileInfo();
    }

    @Override
    protected IBuffer openBuffer(IProgressMonitor pm, Object info) throws YangModelException {
        BufferManager bufManager = getBufferManager();
        IBuffer buffer = BufferManager.createBuffer(this);
        if (buffer == null) {
            return null;
        }

        synchronized (bufManager) {
            IBuffer existingBuffer = bufManager.getBuffer(this);
            if (existingBuffer != null) {
                return existingBuffer;
            }

            // set the buffer source
            if (buffer.getCharacters() == null) {
                IFile file = (IFile) getResource();
                if (file == null || !file.exists()) {
                    throw new YangModelException("File not found");
                }
                InputStreamReader contents = null;
                try {
                    contents = new InputStreamReader(file.getContents(true), "utf-8");
                    char[] buf = new char[8096];
                    StringBuilder builder = new StringBuilder();
                    while (true) {
                        int r = contents.read(buf);
                        if (r == -1) {
                            break;
                        }
                        builder.append(buf, 0, r);
                    }
                    buffer.setContents(builder.toString());
                } catch (Exception e) {
                    throw new YangModelException(e, 0);
                } finally {
                    if (contents != null) {
                        try {
                            contents.close();
                        } catch (IOException e) {
                            // Ingore
                        }
                    }
                }
            }

            // add buffer to buffer cache
            // note this may cause existing buffers to be removed from the buffer cache, but only
            // primary compilation unit's buffer
            // can be closed, thus no call to a client's IBuffer#close() can be done in this
            // synchronized block.
            bufManager.addBuffer(buffer);

            // listen to buffer changes
            buffer.addBufferChangedListener(this);
        }
        return buffer;
    }

    @Override
    public IResource getResource() {
        return resource;
    }

    @Override
    public IPath getPath() {
        return resource.getFullPath().makeRelativeTo(getParent().getResource().getFullPath());
    }

    public Module getModule() throws YangModelException {
        return ((YangFileInfo) getElementInfo(null)).getModule();
    }

    @Override
    public YangElementType getElementType() {
        return YangElementType.YANG_FILE;
    }
}
