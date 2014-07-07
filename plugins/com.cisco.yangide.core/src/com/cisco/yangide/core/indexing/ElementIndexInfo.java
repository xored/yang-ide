/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.indexing;

import java.io.Serializable;

import org.eclipse.core.runtime.IPath;

import com.cisco.yangide.core.dom.ASTNamedNode;

/**
 * @author Konstantin Zaitsev
 * @date Jul 1, 2014
 */
public class ElementIndexInfo implements Serializable, Comparable<ElementIndexInfo> {

    /** Serial version UID. */
    private static final long serialVersionUID = -7971951214450877471L;

    private String namespace;
    private String name;
    private ElementIndexType type;
    private int startPosition = -1;
    private int length = 0;
    private String path;
    private String description;
    private String reference;
    /** Optional entry in case of Jar Entry. */
    private String entry;

    /**
     * @param module
     * @param module2
     */
    public ElementIndexInfo(ASTNamedNode node, String namespace, ElementIndexType type, IPath path, String entry) {
        this.namespace = namespace;
        this.name = node.getName();
        this.type = type;
        this.startPosition = node.getNameStartPosition();
        this.length = node.getNameLength();
        this.description = node.getDescription();
        this.reference = node.getReference();
        this.path = path.toString();
        this.entry = entry;
    }

    /**
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @param namespace the namespace to set
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public ElementIndexType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(ElementIndexType type) {
        this.type = type;
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

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
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
     * @return the entry
     */
    public String getEntry() {
        return entry;
    }

    /**
     * @param entry the entry to set
     */
    public void setEntry(String entry) {
        this.entry = entry;
    }

    @Override
    public String toString() {
        return "ElementIndexInfo [namespace=" + namespace + ", name=" + name + ", type=" + type + ", startPosition="
                + startPosition + ", length=" + length + ", path=" + path + ", description=" + description
                + ", reference=" + reference + ", entry=" + entry + "]";
    }

    @Override
    public int compareTo(ElementIndexInfo o) {
        return o.toString().compareTo(this.toString());
    }
}
