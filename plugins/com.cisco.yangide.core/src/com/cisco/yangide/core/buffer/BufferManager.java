/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.buffer;

import java.text.NumberFormat;
import java.util.Enumeration;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

import com.cisco.yangide.core.IOpenable;
import com.cisco.yangide.core.Openable;

/**
 * @author Konstantin Zaitsev
 * @date Jun 24, 2014
 */
public class BufferManager {

    protected static BufferManager DEFAULT_BUFFER_MANAGER;
    protected static boolean VERBOSE;

    /**
     * LRU cache of buffers. The key and value for an entry in the table is the identical buffer.
     */
    private BufferCache openBuffers = new BufferCache(60);

    /**
     * Adds a buffer to the table of open buffers.
     */
    public void addBuffer(IBuffer buffer) {
        if (VERBOSE) {
            String owner = ((Openable) buffer.getOwner()).toStringWithAncestors();
            System.out.println("Adding buffer for " + owner); //$NON-NLS-1$
        }
        synchronized (this.openBuffers) {
            this.openBuffers.put(buffer.getOwner(), buffer);
        }
        // close buffers that were removed from the cache if space was needed
        this.openBuffers.closeBuffers();
        if (VERBOSE) {
            System.out
                    .println("-> Buffer cache filling ratio = " + NumberFormat.getInstance().format(this.openBuffers.fillingRatio()) + "%"); //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    public static IBuffer createBuffer(IOpenable owner) {
        IResource resource = owner.getResource();
        return new Buffer(resource instanceof IFile ? (IFile) resource : null, owner, owner.isReadOnly());
    }

    public static IBuffer createNullBuffer(IOpenable owner) {
        IResource resource = owner.getResource();
        return new NullBuffer(resource instanceof IFile ? (IFile) resource : null, owner, owner.isReadOnly());
    }

    /**
     * Returns the open buffer associated with the given owner, or <code>null</code> if the owner
     * does not have an open buffer associated with it.
     */
    public IBuffer getBuffer(IOpenable owner) {
        synchronized (this.openBuffers) {
            return (IBuffer) this.openBuffers.get(owner);
        }
    }

    /**
     * Returns the default buffer manager.
     */
    public synchronized static BufferManager getDefaultBufferManager() {
        if (DEFAULT_BUFFER_MANAGER == null) {
            DEFAULT_BUFFER_MANAGER = new BufferManager();
        }
        return DEFAULT_BUFFER_MANAGER;
    }

    /**
     * Returns an enumeration of all open buffers.
     * <p>
     * The <code>Enumeration</code> answered is thread safe.
     *
     * @see OverflowingLRUCache
     * @return Enumeration of IBuffer
     */
    public Enumeration<?> getOpenBuffers() {
        Enumeration<?> result;
        synchronized (this.openBuffers) {
            this.openBuffers.shrink();
            result = this.openBuffers.elements();
        }
        // close buffers that were removed from the cache if space was needed
        this.openBuffers.closeBuffers();
        return result;
    }

    /**
     * Removes a buffer from the table of open buffers.
     */
    public void removeBuffer(IBuffer buffer) {
        synchronized (this.openBuffers) {
            this.openBuffers.remove(buffer.getOwner());
        }
        // close buffers that were removed from the cache (should be only one)
        this.openBuffers.closeBuffers();
    }
}
