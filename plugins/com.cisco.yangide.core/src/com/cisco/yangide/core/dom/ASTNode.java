/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.dom;

import java.util.Collection;

/**
 * @author Konstantin Zaitsev
 * @date Jun 26, 2014
 */
public abstract class ASTNode {

    /** Common field "description" for Yang statement. */
    private SimpleNode<String> descriptionNode;

    /** Common field "reference" for Yang statement. */
    private SimpleNode<String> referenceNode;

    /** Common field "status" for Yang statement. */
    private SimpleNode<String> statusNode;

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

    /** Line number. */
    private int lineNumber = -1;

    /** Start position of AST node body '{' or '"'. */
    private int bodyStartPosition = -1;

    /** Parent AST node. */
    private ASTNode parent = null;

    /**
     * Flag constant (bit mask, value 1) indicating that there is something not quite right with
     * this AST node.
     * <p>
     * The standard parser (<code>ASTParser</code>) SHOULD set this flag on a node to indicate a
     * syntax error detected in the vicinity.
     * </p>
     */
    public static final int MALFORMED = 1;

    public static final int VALID = 0;

    /**
     * int containing the node type in the top 16 bits and flags in the bottom 16 bits; none set by
     * default.
     * <p>
     * N.B. This is a private field, but declared as package-visible for more efficient access from
     * inner classes.
     * </p>
     *
     * @see #MALFORMED
     */
    int typeAndFlags = 0;

    /**
     * Returns the flags associated with this node.
     * <p>
     * No flags are associated with newly created nodes.
     * </p>
     * <p>
     * The flags are the bitwise-or of individual flags. The following flags are currently defined:
     * <ul>
     * <li>{@link #MALFORMED} - indicates node is syntactically malformed</li>
     * </ul>
     * Other bit positions are reserved for future use.
     * </p>
     *
     * @return the bitwise-or of individual flags
     * @see #setFlags(int)
     */
    public final int getFlags() {
        return this.typeAndFlags & 0xFFFF;
    }

    /**
     * Sets the flags associated with this node to the given value.
     * <p>
     * The flags are the bitwise-or of individual flags. The following flags are currently defined:
     * <ul>
     * <li>{@link #MALFORMED} - indicates node is syntactically malformed</li>
     * </ul>
     * Other bit positions are reserved for future use.
     * </p>
     * <p>
     * Note that the flags are <em>not</em> considered a structural property of the node, and can be
     * changed even if the node is marked as protected.
     * </p>
     *
     * @param flags the bitwise-or of individual flags
     * @see #getFlags()
     */
    public final void setFlags(int flags) {
        int old = this.typeAndFlags & 0xFFFF0000;
        this.typeAndFlags = old | (flags & 0xFFFF);
    }

    public ASTNode(ASTNode parent) {
        this.parent = parent;
        if (parent instanceof ASTCompositeNode) {
            ((ASTCompositeNode) parent).getChildren().add(this);
        }
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
     * @return the parent module of this node
     */
    public ASTNode getModule() {
        ASTNode module = this;
        while (module.getParent() != null) {
            module = module.getParent();
        }
        if (module instanceof Module) {
            return module;
        }

        return null;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return descriptionNode != null ? descriptionNode.getValue() : null;
    }

    /**
     * @return the reference
     */
    public String getReference() {
        return referenceNode != null ? referenceNode.getValue() : null;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return statusNode != null ? statusNode.getValue() : null;
    }

    /**
     * @return the lineNumber
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * @param lineNumber the lineNumber to set
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * @return the bodyStartPosition
     */
    public int getBodyStartPosition() {
        return bodyStartPosition;
    }

    /**
     * @param bodyStartPosition the bodyStartPosition to set
     */
    public void setBodyStartPosition(int bodyStartPosition) {
        this.bodyStartPosition = bodyStartPosition;
    }

    /**
     * @return the bodyLenght
     */
    public int getBodyLength() {
        return (bodyStartPosition >= 0 && startPosition >= 0) ? (length - (bodyStartPosition - startPosition) + 1) : 0;
    }

    /**
     * @return the bodyEndPosition
     */
    public int getBodyEndPosition() {
        return bodyStartPosition + getBodyLength();
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
        child.accept(visitor);
    }

    final void acceptChildren(ASTVisitor visitor, Collection<? extends ASTNode> children) {
        for (ASTNode child : children) {
            child.accept(visitor);
        }
    }

    public boolean isShowedInOutline() {
        return true;
    }

    /**
     * @return the descriptionNode
     */
    public SimpleNode<String> getDescriptionNode() {
        return descriptionNode;
    }

    /**
     * @param descriptionNode the descriptionNode to set
     */
    public void setDescriptionNode(SimpleNode<String> descriptionNode) {
        this.descriptionNode = descriptionNode;
    }

    /**
     * @return the referenceNode
     */
    public SimpleNode<String> getReferenceNode() {
        return referenceNode;
    }

    /**
     * @param referenceNode the referenceNode to set
     */
    public void setReferenceNode(SimpleNode<String> referenceNode) {
        this.referenceNode = referenceNode;
    }

    /**
     * @return the statusNode
     */
    public SimpleNode<String> getStatusNode() {
        return statusNode;
    }

    /**
     * @param statusNode the statusNode to set
     */
    public void setStatusNode(SimpleNode<String> statusNode) {
        this.statusNode = statusNode;
    }
}
