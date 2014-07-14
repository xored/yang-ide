/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import com.cisco.yangide.core.buffer.IBuffer;

/**
 * @author Konstantin Zaitsev
 * @date Jun 24, 2014
 */
public interface IOpenable {

    /**
     * Closes this element and its buffer (if any). Closing an element which is not open has no
     * effect.
     *
     * @exception YangModelException if an error occurs closing this element
     */
    public void close() throws YangModelException;

    /**
     * Returns the buffer opened for this element, or <code>null</code> if this element does not
     * have a buffer.
     *
     * @exception YangModelException if this element does not exist or if an exception occurs while
     * accessing its corresponding resource.
     * @return the buffer opened for this element, or <code>null</code> if this element does not
     * have a buffer
     */
    public IBuffer getBuffer() throws YangModelException;

    /**
     * Returns whether this openable is open. This is a handle-only method.
     *
     * @return true if this openable is open, false otherwise
     */
    boolean isOpen();

    /**
     * Opens this element and all parent elements that are not already open. For compilation units,
     * a buffer is opened on the contents of the underlying resource.
     *
     * @param progress the given progress monitor
     * @exception YangModelException if an error occurs accessing the contents of its underlying
     * resource.
     */
    public void open(IProgressMonitor progress) throws YangModelException;

    /**
     * Returns whether this Yang element exists in the model.
     *
     * @return <code>true</code> if this element exists in the model, and <code>false</code> if this
     * element does not exist
     */
    boolean exists();

    /**
     * Returns the element directly containing this element, or <code>null</code> if this element
     * has no parent. This is a handle-only method.
     *
     * @return the parent element, or <code>null</code> if this element has no parent
     */
    IOpenable getParent();

    /**
     * Returns the path to the resource enclosing this element.
     *
     * @return the path to the resource enclosing this element
     */
    IPath getPath();

    /**
     * Returns the resource enclosing this element.
     *
     * @return the resource enclosing this element, <code>null</code> if this element is included in
     * an external archive
     */
    IResource getResource();

    /**
     * Returns whether this element is read-only. An element is read-only if its structure cannot be
     * modified by the model.
     *
     * @return <code>true</code> if this element is read-only
     */
    boolean isReadOnly();

    /**
     * @return name of element (file path for example)
     */
    String getName();

    /**
     * @return string representation element with all parent hierarchy.
     */
    String toStringWithAncestors();
}
