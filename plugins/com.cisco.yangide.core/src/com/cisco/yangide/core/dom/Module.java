/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.dom;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Konstantin Zaitsev
 * @date Jun 26, 2014
 */
public class Module extends ASTNamedNode {
    private SimpleNode<URI> namespace;
    private SimpleNode<String> sourcePath;
    private SimpleNode<Date> revision;
    private SimpleNode<String> prefix;
    private SimpleNode<String> yangVersion;
    private SimpleNode<String> organization;
    private SimpleNode<String> contact;

    private ArrayList<ModuleImport> imports = new ArrayList<ModuleImport>();

    // private final Set<FeatureDefinition> features = new TreeSet<>(Comparators.SCHEMA_NODE_COMP);
    // private final Set<TypeDefinition<?>> typeDefinitions = new
    // TreeSet<>(Comparators.SCHEMA_NODE_COMP);
    // private final Set<NotificationDefinition> notifications = new
    // TreeSet<>(Comparators.SCHEMA_NODE_COMP);
    // private final Set<AugmentationSchema> augmentations = new HashSet<>();
    // private final Set<RpcDefinition> rpcs = new TreeSet<>(Comparators.SCHEMA_NODE_COMP);
    // private final Set<Deviation> deviations = new HashSet<>();
    // private final Set<DataSchemaNode> childNodes = new TreeSet<>(Comparators.SCHEMA_NODE_COMP);
    // private final Set<GroupingDefinition> groupings = new
    // TreeSet<>(Comparators.SCHEMA_NODE_COMP);
    // private final Set<UsesNode> uses = new HashSet<>();
    // private final List<ExtensionDefinition> extensionNodes = new ArrayList<>();
    // private final Set<IdentitySchemaNode> identities = new
    // TreeSet<>(Comparators.SCHEMA_NODE_COMP);
    // private final List<UnknownSchemaNode> unknownNodes = new ArrayList<>();
    // private String source;
    //

    public Module() {
        super(null, null);
    }

    @Override
    public String getNodeName() {
        return "module";
    }

    /**
     * @return the namespace
     */
    public SimpleNode<URI> getNamespace() {
        return namespace;
    }

    /**
     * @param namespace the namespace to set
     */
    public void setNamespace(SimpleNode<URI> namespace) {
        this.namespace = namespace;
    }

    /**
     * @return the sourcePath
     */
    public SimpleNode<String> getSourcePath() {
        return sourcePath;
    }

    /**
     * @param sourcePath the sourcePath to set
     */
    public void setSourcePath(SimpleNode<String> sourcePath) {
        this.sourcePath = sourcePath;
    }

    /**
     * @return the revision
     */
    public SimpleNode<Date> getRevision() {
        return revision;
    }

    /**
     * @param revision the revision to set
     */
    public void setRevision(SimpleNode<Date> revision) {
        this.revision = revision;
    }

    /**
     * @return the prefix
     */
    public SimpleNode<String> getPrefix() {
        return prefix;
    }

    /**
     * @param prefix the prefix to set
     */
    public void setPrefix(SimpleNode<String> prefix) {
        this.prefix = prefix;
    }

    /**
     * @return the yangVersion
     */
    public SimpleNode<String> getYangVersion() {
        return yangVersion;
    }

    /**
     * @param yangVersion the yangVersion to set
     */
    public void setYangVersion(SimpleNode<String> yangVersion) {
        this.yangVersion = yangVersion;
    }

    /**
     * @return the organization
     */
    public SimpleNode<String> getOrganization() {
        return organization;
    }

    /**
     * @param organization the organization to set
     */
    public void setOrganization(SimpleNode<String> organization) {
        this.organization = organization;
    }

    /**
     * @return the contact
     */
    public SimpleNode<String> getContact() {
        return contact;
    }

    /**
     * @param contact the contact to set
     */
    public void setContact(SimpleNode<String> contact) {
        this.contact = contact;
    }

    /**
     * @return the imports
     */
    public ArrayList<ModuleImport> getImports() {
        return imports;
    }
}
