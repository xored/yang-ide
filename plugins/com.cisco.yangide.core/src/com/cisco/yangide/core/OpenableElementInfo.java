/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core;

/**
 * @author Konstantin Zaitsev
 * @date Jun 25, 2014
 */
public class OpenableElementInfo implements Cloneable {

    /**
     * Collection of handles of immediate children of this object. This is an empty array if this
     * element has no children.
     */
    protected IOpenable[] children = Openable.NO_ELEMENTS;

    /**
     * Is the structure of this element known
     * 
     * @see IOpenable#isStructureKnown()
     */
    protected boolean isStructureKnown = false;

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error();
        }
    }

    public void addChild(IOpenable child) {
        int length = this.children.length;
        if (length == 0) {
            this.children = new IOpenable[] { child };
        } else {
            for (int i = 0; i < length; i++) {
                if (this.children[i].equals(child))
                    return; // already included
            }
            System.arraycopy(this.children, 0, this.children = new IOpenable[length + 1], 0, length);
            this.children[length] = child;
        }
    }

    public IOpenable[] getChildren() {
        return this.children;
    }

    /**
     * @see IOpenable#isStructureKnown()
     */
    public boolean isStructureKnown() {
        return this.isStructureKnown;
    }

    public void removeChild(IOpenable child) {
        for (int i = 0, length = this.children.length; i < length; i++) {
            IOpenable element = this.children[i];
            if (element.equals(child)) {
                if (length == 1) {
                    this.children = Openable.NO_ELEMENTS;
                } else {
                    IOpenable[] newChildren = new IOpenable[length - 1];
                    System.arraycopy(this.children, 0, newChildren, 0, i);
                    if (i < length - 1)
                        System.arraycopy(this.children, i + 1, newChildren, i, length - 1 - i);
                    this.children = newChildren;
                }
                break;
            }
        }
    }

    public void setChildren(IOpenable[] children) {
        this.children = children;
    }

    /**
     * Sets whether the structure of this element known
     * 
     * @see IOpenable#isStructureKnown()
     */
    public void setIsStructureKnown(boolean newIsStructureKnown) {
        this.isStructureKnown = newIsStructureKnown;
    }
}
