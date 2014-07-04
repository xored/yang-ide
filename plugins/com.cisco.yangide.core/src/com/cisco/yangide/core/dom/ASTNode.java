/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.dom;

import java.util.List;

/**
 * @author Konstantin Zaitsev
 * @date Jun 26, 2014
 */
public abstract class ASTNode {

    /** Common field "description" for Yang statement. */
    private String description;

    /** Common field "reference" for Yang statement. */
    private String reference;

    /**
     * A character index into the original source string, or <code>-1</code> if no source position
     * information is available for this node; <code>-1</code> by default.
     */
    private int startPosition = -1;

    /**
     * A character length, or <code>0</code> if no source position information is recorded for this
     * node; <code>0</code> by default.
     */
    private int length = 0;

    private ASTNode parent = null;

    public ASTNode(ASTNode parent) {
        this.parent = parent;
    }

    /**
     * @return the startPosition
     */
    public int getStartPosition() {
        return startPosition;
    }

    /**
     * @param startPosition the startPosition to set
     */
    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(int length) {
        this.length = length;
    }

    public int getEndPosition() {
        return this.startPosition + this.length;
    }

    /**
     * @return the parent
     */
    public ASTNode getParent() {
        return parent;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the reference
     */
    public String getReference() {
        return reference;
    }

    /**
     * @param reference the reference to set
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * @return the name
     */
    public abstract String getNodeName();

    public abstract void accept(ASTVisitor visitor);

    final void acceptChild(ASTVisitor visitor, ASTNode child) {
        if (child == null) {
            return;
        }
        visitor.preVisit(child);
        child.accept(visitor);
    }

    final void acceptChildren(ASTVisitor visitor, List<? extends ASTNode> children) {
        // TODO KOS: rewrite to concurrent invocation
        for (ASTNode child : children) {
            visitor.preVisit(child);
            child.accept(visitor);
        }
    }
}
