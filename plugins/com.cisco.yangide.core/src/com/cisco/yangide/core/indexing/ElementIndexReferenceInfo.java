/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.indexing;

import java.io.Serializable;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

import com.cisco.yangide.core.dom.ASTNamedNode;
import com.cisco.yangide.core.dom.QName;

/**
 * @author Konstantin Zaitsev
 * @date Jul 31, 2014
 */
public class ElementIndexReferenceInfo implements Serializable, Comparable<ElementIndexReferenceInfo> {

    /** Serial version UID. */
    private static final long serialVersionUID = 8090385352032844152L;

    private ElementIndexReferenceType type;
    private int startPosition = -1;
    private int length = 0;
    private String project;
    private String path;
    private QName reference;

    public ElementIndexReferenceInfo(ASTNamedNode node, QName reference, ElementIndexReferenceType type,
            IProject project, IPath path) {
        this.reference = reference;
        this.type = type;
        this.startPosition = node.getNameStartPosition();
        this.length = node.getNameLength();
        this.project = project.getName();
        this.path = path.toString();
    }

    /**
     * @return the type
     */
    public ElementIndexReferenceType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(ElementIndexReferenceType type) {
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
     * @return the project
     */
    public String getProject() {
        return project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(String project) {
        this.project = project;
    }

    /**
     * @return the reference
     */
    public QName getReference() {
        return reference;
    }

    /**
     * @param reference the reference to set
     */
    public void setReference(QName reference) {
        this.reference = reference;
    }

    @Override
    public int compareTo(ElementIndexReferenceInfo o) {
        return o.toString().compareTo(this.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ElementIndexReferenceInfo)) {
            return false;
        }
        ElementIndexReferenceInfo i = (ElementIndexReferenceInfo) obj;
        return this.project.equals(i.project) && this.path.equals(i.path) && this.startPosition == i.startPosition
                && this.length == i.length && this.type == i.type;
    }

    @Override
    public int hashCode() {
        return 31 * this.toString().hashCode();
    }

    @Override
    public String toString() {
        return project + " - " + path + " - " + startPosition + " - " + length + " - " + type;
    }
}
