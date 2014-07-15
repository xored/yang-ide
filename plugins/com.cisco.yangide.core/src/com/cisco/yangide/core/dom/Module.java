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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Konstantin Zaitsev
 * @date Jun 26, 2014
 */
public class Module extends ASTCompositeNode {
    private URI namespace;
    private String revision;

    private SimpleNode<URI> namespaceNode;
    private SimpleNode<String> revisionNode;
    private SimpleNode<String> sourcePath;
    private SimpleNode<String> prefix;
    private SimpleNode<String> yangVersion;
    private SimpleNode<String> organization;
    private SimpleNode<String> contact;

    /** Name to Import map. */
    private Map<String, ModuleImport> imports = new HashMap<>();
    /** Prefix to Import map. */
    private Map<String, ModuleImport> importPrefixes = new HashMap<>();
    /** Duplicate import declaration, used for validation purpose. */
    private Set<ModuleImport> duplicateImports = new HashSet<>();

    /** Contains name and submodule. */
    private Map<String, SubModuleInclude> includes = new HashMap<>();

    // private final Set<FeatureDefinition> features = new TreeSet<>(Comparators.SCHEMA_NODE_COMP);
    private ArrayList<TypeDefinition> typeDefinitions = new ArrayList<TypeDefinition>();

    // private final Set<NotificationDefinition> notifications = new
    // TreeSet<>(Comparators.SCHEMA_NODE_COMP);
    // private final Set<AugmentationSchema> augmentations = new HashSet<>();
    // private final Set<RpcDefinition> rpcs = new TreeSet<>(Comparators.SCHEMA_NODE_COMP);
    // private final Set<Deviation> deviations = new HashSet<>();
    // private final Set<DataSchemaNode> childNodes = new TreeSet<>(Comparators.SCHEMA_NODE_COMP);

    private ArrayList<GroupingDefinition> groupings = new ArrayList<>();

    // private final Set<UsesNode> uses = new HashSet<>();
    // private final List<ExtensionDefinition> extensionNodes = new ArrayList<>();
    // private final Set<IdentitySchemaNode> identities = new
    // TreeSet<>(Comparators.SCHEMA_NODE_COMP);
    // private final List<UnknownSchemaNode> unknownNodes = new ArrayList<>();
    // private String source;
    //

    public Module() {
        super(null);
    }

    @Override
    public String getNodeName() {
        return "module";
    }

    /**
     * @return the namespace
     */
    public URI getNamespace() {
        return namespace;
    }

    /**
     * @param namespace the namespace to set
     */
    public void setNamespace(URI namespace) {
        this.namespace = namespace;
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
     * @return the namespaceNode
     */
    public SimpleNode<URI> getNamespaceNode() {
        return namespaceNode;
    }

    /**
     * @param namespace the namespace to set
     */
    public void setNamespaceNode(SimpleNode<URI> namespaceNode) {
        this.namespaceNode = namespaceNode;
        this.namespace = namespaceNode.getValue();
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
    public SimpleNode<String> getRevisionNode() {
        return revisionNode;
    }

    /**
     * @param revision the revision to set
     */
    public void setRevisionNode(SimpleNode<String> revisionNode) {
        this.revisionNode = revisionNode;
        this.revision = revisionNode.getValue();
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
    public Map<String, ModuleImport> getImports() {
        return imports;
    }

    /**
     * @return the includes
     */
    public Map<String, SubModuleInclude> getIncludes() {
        return includes;
    }

    /**
     * @return the typeDefinitions
     */
    public ArrayList<TypeDefinition> getTypeDefinitions() {
        return typeDefinitions;
    }

    /**
     * @return the groupings
     */
    public ArrayList<GroupingDefinition> getGroupings() {
        return groupings;
    }

    /**
     * Add module import statement.
     *
     * @param moduleImport statement
     */
    public void addImport(ModuleImport moduleImport) {
        if (this.imports.containsKey(moduleImport.getName())) {
            duplicateImports.add(moduleImport);
        } else {
            this.imports.put(moduleImport.getName(), moduleImport);
            if (moduleImport.getPrefix() != null) {
                this.importPrefixes.put(moduleImport.getPrefix(), moduleImport);
            }
        }
    }

    /**
     * @return the duplicateImports
     */
    public Set<ModuleImport> getDuplicateImports() {
        return duplicateImports;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.preVisit(this);
        boolean visitChildren = visitor.visit(this);
        if (visitChildren) {
            visitChildren(visitor);
        }
    }

    /**
     * @param position absolute offset position in the file content
     * @return ASTNode at specified position or <code>null</code> if position out of any of nodes
     */
    public ASTNode getNodeAtPosition(final int position) {
        final AtomicReference<ASTNode> result = new AtomicReference<ASTNode>(null);
        this.accept(new ASTVisitor() {
            @Override
            public void preVisit(ASTNode node) {
                if (node.getStartPosition() <= position && node.getEndPosition() >= position) {
                    result.set(node);
                }
            }
        });
        return result.get();
    }

    /**
     * @param prefix namespace prefix
     * @return module import by prefix or <code>null</code> if no import found
     */
    public ModuleImport getImportByPrefix(String prefix) {
        if (importPrefixes.containsKey(prefix)) {
            return importPrefixes.get(prefix);
        }
        return null;
    }

    /**
     * @param name module name
     * @return import statement by name or <code>null</code> if import not found
     */
    public ModuleImport getImportByName(String name) {
        if (imports.containsKey(name)) {
            return imports.get(name);
        }
        return null;
    }

    /**
     * @param name submodule name
     * @return submodule or <code>null</code> if this submodule not included
     */
    public SubModuleInclude getIncludeByName(String name) {
        if (includes.containsKey(name)) {
            return includes.get(name);
        }
        return null;
    }

    protected void visitChildren(ASTVisitor visitor) {
        acceptChild(visitor, namespaceNode);
        acceptChild(visitor, sourcePath);
        acceptChild(visitor, revisionNode);
        acceptChild(visitor, prefix);
        acceptChild(visitor, yangVersion);
        acceptChild(visitor, organization);
        acceptChild(visitor, contact);

        acceptChildren(visitor, getChildren());
    }
}
