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
     * <p>
     * Note: although {@link #close} is exposed in the API, clients are not expected to open and
     * close elements - the Java model does this automatically as elements are accessed.
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
     * Returns <code>true</code> if this element is open and:
     * <ul>
     * <li>its buffer has unsaved changes, or
     * <li>one of its descendants has unsaved changes, or
     * <li>a working copy has been created on one of this element's children and has not yet
     * destroyed
     * </ul>
     *
     * @exception YangModelException if this element does not exist or if an exception occurs while
     * accessing its corresponding resource.
     * @return <code>true</code> if this element is open and:
     * <ul>
     * <li>its buffer has unsaved changes, or
     * <li>one of its descendants has unsaved changes, or
     * <li>a working copy has been created on one of this element's children and has not yet
     * destroyed
     * </ul>
     */
    boolean hasUnsavedChanges() throws YangModelException;

    /**
     * Returns whether the element is consistent with its underlying resource or buffer. The element
     * is consistent when opened, and is consistent if the underlying resource or buffer has not
     * been modified since it was last consistent.
     * <p>
     * NOTE: Child consistency is not considered. For example, a package fragment responds
     * <code>true</code> when it knows about all of its compilation units present in its underlying
     * folder. However, one or more of the compilation units could be inconsistent.
     *
     * @exception JavaModelException if this element does not exist or if an exception occurs while
     * accessing its corresponding resource.
     * @return true if the element is consistent with its underlying resource or buffer, false
     * otherwise.
     * @see IOpenable#makeConsistent(IProgressMonitor)
     */
    boolean isConsistent() throws YangModelException;

    /**
     * Returns whether this openable is open. This is a handle-only method.
     * 
     * @return true if this openable is open, false otherwise
     */
    boolean isOpen();

    /**
     * Makes this element consistent with its underlying resource or buffer by updating the
     * element's structure and properties as necessary.
     * 
     * @param progress the given progress monitor
     * @exception YangModelException if the element is unable to access the contents of its
     * underlying resource. Reasons include:
     * <ul>
     * <li>This Java element does not exist (ELEMENT_DOES_NOT_EXIST)</li>
     * </ul>
     * @see IOpenable#isConsistent()
     */
    void makeConsistent(IProgressMonitor progress) throws YangModelException;

    /**
     * Opens this element and all parent elements that are not already open. For compilation units,
     * a buffer is opened on the contents of the underlying resource.
     * <p>
     * Note: although {@link #open} is exposed in the API, clients are not expected to open and
     * close elements - the Java model does this automatically as elements are accessed.
     *
     * @param progress the given progress monitor
     * @exception YangModelException if an error occurs accessing the contents of its underlying
     * resource. Reasons include:
     * <ul>
     * <li>This Java element does not exist (ELEMENT_DOES_NOT_EXIST)</li>
     * </ul>
     */
    public void open(IProgressMonitor progress) throws YangModelException;

    /**
     * Saves any changes in this element's buffer to its underlying resource via a workspace
     * resource operation. This has no effect if the element has no underlying buffer, or if there
     * are no unsaved changed in the buffer.
     * <p>
     * The <code>force</code> parameter controls how this method deals with cases where the
     * workbench is not completely in sync with the local file system. If <code>false</code> is
     * specified, this method will only attempt to overwrite a corresponding file in the local file
     * system provided it is in sync with the workbench. This option ensures there is no unintended
     * data loss; it is the recommended setting. However, if <code>true</code> is specified, an
     * attempt will be made to write a corresponding file in the local file system, overwriting any
     * existing one if need be. In either case, if this method succeeds, the resource will be marked
     * as being local (even if it wasn't before).
     * <p>
     * As a result of this operation, the element is consistent with its underlying resource or
     * buffer.
     *
     * @param progress the given progress monitor
     * @param force it controls how this method deals with cases where the workbench is not
     * completely in sync with the local file system
     * @exception YangModelException if an error occurs accessing the contents of its underlying
     * resource. Reasons include:
     * <ul>
     * <li>This Java element does not exist (ELEMENT_DOES_NOT_EXIST)</li> <li>This Java element is
     * read-only (READ_ONLY)</li>
     * </ul>
     */
    public void save(IProgressMonitor progress, boolean force) throws YangModelException;
    
    /**
     * Returns whether this Java element exists in the model.
     * <p>
     * Java elements are handle objects that may or may not be backed by an
     * actual element. Java elements that are backed by an actual element are
     * said to "exist", and this method returns <code>true</code>. For Java
     * elements that are not working copies, it is always the case that if the
     * element exists, then its parent also exists (provided it has one) and
     * includes the element as one of its children. It is therefore possible
     * to navigated to any existing Java element from the root of the Java model
     * along a chain of existing Java elements. On the other hand, working
     * copies are said to exist until they are destroyed (with
     * <code>IWorkingCopy.destroy</code>). Unlike regular Java elements, a
     * working copy never shows up among the children of its parent element
     * (which may or may not exist).
     * </p>
     *
     * @return <code>true</code> if this element exists in the Java model, and
     * <code>false</code> if this element does not exist
     */
    boolean exists();

    /**
     * Returns the first ancestor of this Java element that has the given type.
     * Returns <code>null</code> if no such an ancestor can be found.
     * This is a handle-only method.
     *
     * @param ancestorType the given type
     * @return the first ancestor of this Java element that has the given type, null if no such an ancestor can be found
     * @since 2.0
     */
    IOpenable getAncestor(int ancestorType);

    /**
     * Returns the element directly containing this element,
     * or <code>null</code> if this element has no parent.
     * This is a handle-only method.
     *
     * @return the parent element, or <code>null</code> if this element has no parent
     */
    IOpenable getParent();

    /**
     * Returns the path to the innermost resource enclosing this element.
     * If this element is not included in an external library,
     * the path returned is the full, absolute path to the underlying resource,
     * relative to the workbench.
     * If this element is included in an external library,
     * the path returned is the absolute path to the archive or to the
     * folder in the file system.
     * This is a handle-only method.
     *
     * @return the path to the innermost resource enclosing this element
     * @since 2.0
     */
    IPath getPath();

    /**
     * Returns the primary element (whose compilation unit is the primary compilation unit)
     * this working copy element was created from, or this element if it is a descendant of a
     * primary compilation unit or if it is not a descendant of a working copy (e.g. it is a
     * binary member).
     * The returned element may or may not exist.
     *
     * @return the primary element this working copy element was created from, or this
     *          element.
     * @since 3.0
     */
    IOpenable getPrimaryElement();

    /**
     * Returns the innermost resource enclosing this element.
     * If this element is included in an archive and this archive is not external,
     * this is the underlying resource corresponding to the archive.
     * If this element is included in an external library, <code>null</code>
     * is returned.
     * This is a handle-only method.
     *
     * @return the innermost resource enclosing this element, <code>null</code> if this
     * element is included in an external archive
     * @since 2.0
     */
    IResource getResource();

    /**
     * Returns whether this Java element is read-only. An element is read-only
     * if its structure cannot be modified by the java model.
     * <p>
     * Note this is different from IResource.isReadOnly(). For example, .jar
     * files are read-only as the java model doesn't know how to add/remove
     * elements in this file, but the underlying IFile can be writable.
     * <p>
     * This is a handle-only method.
     *
     * @return <code>true</code> if this element is read-only
     */
    boolean isReadOnly();

    /**
     * Returns whether the structure of this element is known. For example, for a
     * compilation unit that has syntax errors, <code>false</code> is returned.
     * If the structure of an element is unknown, navigations will return reasonable
     * defaults. For example, <code>getChildren</code> for a compilation unit with
     * syntax errors will return a collection of the children that could be parsed.
     * <p>
     * Note: This does not imply anything about consistency with the
     * underlying resource/buffer contents.
     * </p>
     *
     * @return <code>true</code> if the structure of this element is known
     * @exception JavaModelException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    boolean isStructureKnown() throws YangModelException;
}
