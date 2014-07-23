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
import com.cisco.yangide.core.dom.Module;

/**
 * @author Konstantin Zaitsev
 * @date Jul 8, 2014
 */
public class ElementIndexInfo implements Serializable, Comparable<ElementIndexInfo> {

    /** Serial version UID. */
    private static final long serialVersionUID = -7971951214450877471L;

    private String module;
    private String revision;
    private String name;
    private ElementIndexType type;
    private int startPosition = -1;
    private int length = 0;
    private String project;
    private String path;
    private String description;
    private String reference;
    private String status;
    // module fields
    private String namespace;
    private String organization;
    private String contact;

    /** Optional entry in case of Jar Entry. */
    private String entry;

    public ElementIndexInfo(ASTNamedNode node, String module, String revision, ElementIndexType type, IProject project,
            IPath path, String entry) {
        this.module = module;
        this.revision = revision;
        this.name = node.getName();
        this.type = type;
        this.startPosition = node.getNameStartPosition();
        this.length = node.getNameLength();
        this.description = node.getDescription();
        this.reference = node.getReference();
        this.status = node.getStatus();
        if (node instanceof Module) {
            Module m = (Module) node;
            this.namespace = m.getNamespace() != null ? m.getNamespace().toASCIIString() : null;
            this.organization = m.getOrganization() != null ? m.getOrganization().getValue() : null;
            this.contact = m.getContact() != null ? m.getContact().getValue() : null;
        }
        this.project = project.getName();
        this.path = path.toString();
        this.entry = entry;
    }

    /**
     * @return the module
     */
    public String getModule() {
        return module;
    }

    /**
     * @param module the module to set
     */
    public void setModule(String module) {
        this.module = module;
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
     * @return the revision
     */
    public String getRevision() {
        return revision;
    }

    /**
     * @param revision the revision to set
     */
    public void setRevision(String revision) {
        this.revision = revision;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the organization
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * @param organization the organization to set
     */
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    /**
     * @return the contact
     */
    public String getContact() {
        return contact;
    }

    /**
     * @param contact the contact to set
     */
    public void setContact(String contact) {
        this.contact = contact;
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

    @Override
    public int compareTo(ElementIndexInfo o) {
        return o.toString().compareTo(this.toString());
    }

    @Override
    public String toString() {
        return "ElementIndexInfo [module=" + module + ", revision=" + revision + ", name=" + name + ", type=" + type
                + ", startPosition=" + startPosition + ", length=" + length + ", project=" + project + ", path=" + path
                + ", description=" + description + ", reference=" + reference + ", status=" + status + ", namespace="
                + namespace + ", organization=" + organization + ", contact=" + contact + ", entry=" + entry + "]";
    }
}
