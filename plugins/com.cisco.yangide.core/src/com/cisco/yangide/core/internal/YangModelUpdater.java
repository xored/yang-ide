/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.internal;

import com.cisco.yangide.core.IYangElementDelta;
import com.cisco.yangide.core.OpenableElementInfo;
import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.core.model.YangElement;
import com.cisco.yangide.core.model.YangElementType;
import com.cisco.yangide.core.model.YangModelManager;

/**
 * @author Konstantin Zaitsev
 * @date Jun 30, 2014
 */
public class YangModelUpdater {

    /**
     * Adds the given child handle to its parent's cache of children.
     */
    protected void addToParentInfo(YangElement child) {

        YangElement parent = (YangElement) child.getParent();
        if (parent != null && parent.isOpen()) {
            try {
                OpenableElementInfo info = (OpenableElementInfo) parent.getElementInfo(null);
                info.addChild(child);
            } catch (YangModelException e) {
                // do nothing - we already checked if open
            }
        }
    }

    /**
     * Closes the given element, which removes it from the cache of open elements.
     */
    protected static void close(YangElement element) {
        try {
            element.close();
        } catch (YangModelException e) {
            // do nothing
        }
    }

    /**
     * Processing for an element that has been added:
     * <ul>
     * <li>If the element is a project, do nothing, and do not process children, as when a project
     * is created it does not yet have any natures - specifically a java nature.
     * <li>If the elemet is not a project, process it as added (see <code>basicElementAdded</code>.
     * </ul>
     */
    protected void elementAdded(YangElement element) {
        YangElementType elementType = element.getElementType();
        if (elementType == YangElementType.YANG_PROJECT) {
            addToParentInfo(element);
        } else {
            addToParentInfo(element);
            close(element);
        }
    }

    /**
     * Generic processing for elements with changed contents:
     * <ul>
     * <li>The element is closed such that any subsequent accesses will re-open the element
     * reflecting its new structure.
     * </ul>
     */
    protected void elementChanged(YangElement element) {
        close(element);
    }

    /**
     * Generic processing for a removed element:
     * <ul>
     * <li>Close the element, removing its structure from the cache
     * <li>Remove the element from its parent's cache of children
     * <li>Add a REMOVED entry in the delta
     * </ul>
     */
    @SuppressWarnings("restriction")
    protected void elementRemoved(YangElement element) {

        if (element.isOpen()) {
            close(element);
        }
        removeFromParentInfo(element);

        if (element.getElementType() == YangElementType.YANG_MODEL) {
            YangModelManager.getIndexManager().reset();
        }
    }

    /**
     * Converts a <code>IResourceDelta</code> rooted in a <code>Workspace</code> into the
     * corresponding set of <code>IYangElementDelta</code>, rooted in the relevant
     * <code>YangModel</code>s.
     */
    public void processYangDelta(IYangElementDelta delta) {
        traverseDelta(delta); // traverse delta
    }

    /**
     * Removes the given element from its parents cache of children. If the element does not have a
     * parent, or the parent is not currently open, this has no effect.
     */
    protected void removeFromParentInfo(YangElement child) {

        YangElement parent = (YangElement) child.getParent();
        if (parent != null && parent.isOpen()) {
            try {
                OpenableElementInfo info = (OpenableElementInfo) parent.getElementInfo(null);
                info.removeChild(child);
            } catch (YangModelException e) {
                // do nothing - we already checked if open
            }
        }
    }

    /**
     * Converts an <code>IResourceDelta</code> and its children into the corresponding
     * <code>IYangElementDelta</code>s. Return whether the delta corresponds to a resource on the
     * classpath. If it is not a resource on the classpath, it will be added as a non-java resource
     * by the sender of this method.
     */
    protected void traverseDelta(IYangElementDelta delta) {

        YangElement element = (YangElement) delta.getElement();

        switch (delta.getKind()) {
        case IYangElementDelta.ADDED:
            elementAdded(element);
            break;
        case IYangElementDelta.REMOVED:
            elementRemoved(element);
            break;
        case IYangElementDelta.CHANGED:
            if ((delta.getFlags() & IYangElementDelta.F_CONTENT) != 0) {
                elementChanged(element);
            }
            break;
        }

        IYangElementDelta[] children = delta.getAffectedChildren();
        for (int i = 0; i < children.length; i++) {
            IYangElementDelta childDelta = children[i];
            traverseDelta(childDelta);
        }
    }
}
