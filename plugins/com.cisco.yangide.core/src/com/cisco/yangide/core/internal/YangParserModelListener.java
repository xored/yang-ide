/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.internal;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Contact_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Container_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Description_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Grouping_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Import_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Leaf_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Module_header_stmtsContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Module_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Namespace_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Organization_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Prefix_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Reference_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Revision_date_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Revision_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Revision_stmtsContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.StringContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Submodule_header_stmtsContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Submodule_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Type_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Typedef_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Uses_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Yang_version_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParserBaseListener;

import com.cisco.yangide.core.dom.ASTNamedNode;
import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.ContrainerSchemaNode;
import com.cisco.yangide.core.dom.GroupingDefinition;
import com.cisco.yangide.core.dom.LeafSchemaNode;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.ModuleImport;
import com.cisco.yangide.core.dom.QName;
import com.cisco.yangide.core.dom.SimpleNode;
import com.cisco.yangide.core.dom.SubModule;
import com.cisco.yangide.core.dom.TypeDefinition;
import com.cisco.yangide.core.dom.TypeReference;
import com.cisco.yangide.core.dom.UsesNode;

/**
 * @author Konstantin Zaitsev
 * @date Jun 26, 2014
 */
public class YangParserModelListener extends YangParserBaseListener {
    public final static DateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private Module module;
    private URI namespace = URI.create("none");
    private String yangModelPrefix;
    private String revision = SIMPLE_DATE_FORMAT.format(new Date(0L));
    private Stack<ASTNode> stack = new Stack<>();

    @Override
    public void enterModule_stmt(Module_stmtContext ctx) {
        module = new Module();
        module.setNamespace(URI.create(""));
        module.setRevision(revision);
        stack.push(module);
        updateNamedNode(module, ctx);
        setNodeDescription(module, ctx);
    }

    @Override
    public void enterSubmodule_stmt(Submodule_stmtContext ctx) {
        module = new SubModule();
        module.setNamespace(URI.create(""));
        module.setRevision(revision);
        stack.push(module);
        updateNamedNode(module, ctx);
        setNodeDescription(module, ctx);
    }

    @Override
    public void exitModule_stmt(Module_stmtContext ctx) {
        module.setLength(ctx.stop.getStopIndex() - module.getStartPosition());
    }

    @Override
    public void enterModule_header_stmts(Module_header_stmtsContext ctx) {
        for (int i = 0; i < ctx.getChildCount(); ++i) {
            final ParseTree treeNode = ctx.getChild(i);
            if (treeNode instanceof Namespace_stmtContext) {
                final String namespaceStr = stringFromNode(treeNode);
                try {
                    namespace = URI.create(namespaceStr);
                } catch (Exception e) {
                    // ignore exception
                }
                SimpleNode<URI> astNode = new SimpleNode<URI>(module, ((Namespace_stmtContext) treeNode)
                        .NAMESPACE_KEYWORD().getText(), namespace);
                updateNodePosition(astNode, treeNode);
                module.setNamespaceNode(astNode);
            } else if (treeNode instanceof Prefix_stmtContext) {
                yangModelPrefix = stringFromNode(treeNode);
                SimpleNode<String> astNode = new SimpleNode<String>(module, ((Prefix_stmtContext) treeNode)
                        .PREFIX_KEYWORD().getText(), yangModelPrefix);
                updateNodePosition(astNode, treeNode);
                module.setPrefix(astNode);
            } else if (treeNode instanceof Yang_version_stmtContext) {
                SimpleNode<String> astNode = new SimpleNode<String>(module, ((Yang_version_stmtContext) treeNode)
                        .YANG_VERSION_KEYWORD().getText(), stringFromNode(treeNode));
                updateNodePosition(astNode, treeNode);
                module.setYangVersion(astNode);
            }
        }
    }

    @Override
    public void enterSubmodule_header_stmts(Submodule_header_stmtsContext ctx) {
        for (int i = 0; i < ctx.getChildCount(); ++i) {
            final ParseTree treeNode = ctx.getChild(i);
            if (treeNode instanceof Namespace_stmtContext) {
                final String namespaceStr = stringFromNode(treeNode);
                namespace = URI.create(namespaceStr);
                SimpleNode<URI> astNode = new SimpleNode<URI>(module, ((Namespace_stmtContext) treeNode)
                        .NAMESPACE_KEYWORD().getText(), namespace);
                updateNodePosition(astNode, treeNode);
                module.setNamespaceNode(astNode);
            } else if (treeNode instanceof Prefix_stmtContext) {
                yangModelPrefix = stringFromNode(treeNode);
                SimpleNode<String> astNode = new SimpleNode<String>(module, ((Prefix_stmtContext) treeNode)
                        .PREFIX_KEYWORD().getText(), yangModelPrefix);
                updateNodePosition(astNode, treeNode);
                module.setPrefix(astNode);
            } else if (treeNode instanceof Yang_version_stmtContext) {
                SimpleNode<String> astNode = new SimpleNode<String>(module, ((Yang_version_stmtContext) treeNode)
                        .YANG_VERSION_KEYWORD().getText(), stringFromNode(treeNode));
                updateNodePosition(astNode, treeNode);
                module.setYangVersion(astNode);
            }
        }
    }

    @Override
    public void enterMeta_stmts(YangParser.Meta_stmtsContext ctx) {
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree treeNode = ctx.getChild(i);
            if (treeNode instanceof Organization_stmtContext) {
                SimpleNode<String> astNode = new SimpleNode<String>(module, ((Organization_stmtContext) treeNode)
                        .ORGANIZATION_KEYWORD().getText(), stringFromNode(treeNode));
                updateNodePosition(astNode, treeNode);
                module.setOrganization(astNode);
            } else if (treeNode instanceof Contact_stmtContext) {
                SimpleNode<String> astNode = new SimpleNode<String>(module, ((Contact_stmtContext) treeNode)
                        .CONTACT_KEYWORD().getText(), stringFromNode(treeNode));
                updateNodePosition(astNode, treeNode);
                module.setContact(astNode);
            } else if (treeNode instanceof Description_stmtContext) {
                final String description = stringFromNode(treeNode);
                module.setDescription(description);
            } else if (treeNode instanceof Reference_stmtContext) {
                final String reference = stringFromNode(treeNode);
                module.setReference(reference);
            }
        }
    }

    @Override
    public void enterRevision_stmts(Revision_stmtsContext ctx) {
        for (int i = 0; i < ctx.getChildCount(); ++i) {
            final ParseTree treeNode = ctx.getChild(i);
            if (treeNode instanceof Revision_stmtContext) {
                updateRevisionForRevisionStatement(treeNode);
            }
        }
    }

    @Override
    public void enterImport_stmt(Import_stmtContext ctx) {
        String importPrefix = null;
        String importRevision = null;

        for (int i = 0; i < ctx.getChildCount(); ++i) {
            final ParseTree treeNode = ctx.getChild(i);
            if (treeNode instanceof Prefix_stmtContext) {
                importPrefix = stringFromNode(treeNode);
            }
            if (treeNode instanceof Revision_date_stmtContext) {
                importRevision = stringFromNode(treeNode);
            }
        }
        ModuleImport moduleImport = new ModuleImport(module, importRevision, importPrefix);
        updateNamedNode(moduleImport, ctx);
        module.getImports().add(moduleImport);
    }

    @Override
    public void enterTypedef_stmt(Typedef_stmtContext ctx) {
        TypeDefinition typeDefinition = new TypeDefinition(module);
        updateNamedNode(typeDefinition, ctx);
        module.getTypeDefinitions().add(typeDefinition);
        stack.push(typeDefinition);
    }

    @Override
    public void exitTypedef_stmt(Typedef_stmtContext ctx) {
        stack.pop();
    }

    @Override
    public void enterGrouping_stmt(Grouping_stmtContext ctx) {
        GroupingDefinition grouping = new GroupingDefinition(stack.peek());
        stack.push(grouping);
        module.getGroupings().add(grouping);
        updateNamedNode(grouping, ctx);
    }

    @Override
    public void exitGrouping_stmt(Grouping_stmtContext ctx) {
        stack.pop();
    }

    @Override
    public void enterContainer_stmt(Container_stmtContext ctx) {
        ContrainerSchemaNode container = new ContrainerSchemaNode(stack.peek());
        stack.push(container);
        updateNamedNode(container, ctx);
    }

    @Override
    public void exitContainer_stmt(Container_stmtContext ctx) {
        stack.pop();
    }

    @Override
    public void enterLeaf_stmt(Leaf_stmtContext ctx) {
        LeafSchemaNode leaf = new LeafSchemaNode(stack.peek());
        stack.push(leaf);
        updateNamedNode(leaf, ctx);
    }

    @Override
    public void exitLeaf_stmt(Leaf_stmtContext ctx) {
        stack.pop();
    }

    @Override
    public void enterType_stmt(Type_stmtContext ctx) {
        final String typeName = stringFromNode(ctx);
        final QName typeQName = parseQName(typeName);
        TypeReference typeRef = new TypeReference(stack.peek());
        updateNamedNode(typeRef, ctx);
        typeRef.setType(typeQName);
    }

    @Override
    public void enterUses_stmt(Uses_stmtContext ctx) {
        final String groupingPath = stringFromNode(ctx);
        UsesNode usesNode = new UsesNode(stack.peek());
        updateNamedNode(usesNode, ctx);
        usesNode.setGrouping(parseQName(groupingPath));
    }

    /**
     * @return
     */
    public Module getModule() {
        return module;
    }

    /**
     * @param typeName
     * @return
     */
    private QName parseQName(String typeName) {
        String[] parts = typeName.split(":");
        if (parts.length == 2) {
            ModuleImport moduleImport = module.getImport(parts[0]);
            if (moduleImport != null) {
                return new QName(moduleImport.getName(), moduleImport.getPrefix(), parts[1], moduleImport.getRevision());
            }
        }
        return new QName(module.getName(), module.getPrefix().getValue(), typeName, revision);
    }

    private void updateRevisionForRevisionStatement(final ParseTree treeNode) {
        final String revisionDate = stringFromNode(treeNode);
        if ((revisionDate != null) && (this.revision.compareTo(revisionDate) < 0)) {
            this.revision = revisionDate;
            SimpleNode<String> revisionNode = new SimpleNode<String>(module, "revision", revisionDate);
            module.setRevisionNode(revisionNode);
            for (int i = 0; i < treeNode.getChildCount(); ++i) {
                ParseTree child = treeNode.getChild(i);
                if (child instanceof Reference_stmtContext) {
                    module.setReference(stringFromNode(child));
                }
            }
        }
    }

    private void updateNodePosition(ASTNode astNode, ParseTree treeNode) {
        if (astNode != null && treeNode instanceof ParserRuleContext) {
            astNode.setStartPosition(((ParserRuleContext) treeNode).start.getStartIndex());
            astNode.setLength(((ParserRuleContext) treeNode).stop.getStopIndex() - astNode.getStartPosition());
        }
    }

    private void setNodeDescription(ASTNode astNode, ParseTree treeNode) {
        String description = null;
        String reference = null;
        for (int i = 0; i < treeNode.getChildCount(); i++) {
            ParseTree child = treeNode.getChild(i);
            if (child instanceof Description_stmtContext) {
                description = stringFromNode(child);
            } else if (child instanceof Reference_stmtContext) {
                reference = stringFromNode(child);
            } else {
                if (description != null && reference != null) {
                    break;
                }
            }
        }
        astNode.setDescription(description);
        astNode.setReference(reference);
    }

    private void updateNamedNode(ASTNamedNode astNode, ParseTree treeNode) {
        updateNodePosition(astNode, treeNode);
        for (int i = 0; i < treeNode.getChildCount(); ++i) {
            if (treeNode.getChild(i) instanceof StringContext) {
                final StringContext context = (StringContext) treeNode.getChild(i);
                if (context != null) {
                    astNode.setNameStartPosition(context.getStart().getStartIndex());
                    astNode.setName(stringFromStringContext(context));
                }
            }
        }
    }

    /**
     * Parse given tree and get first string value.
     *
     * @param treeNode tree to parse
     * @return first string value from given tree
     */
    private static String stringFromNode(final ParseTree treeNode) {
        String result = "";
        for (int i = 0; i < treeNode.getChildCount(); ++i) {
            if (treeNode.getChild(i) instanceof StringContext) {
                final StringContext context = (StringContext) treeNode.getChild(i);
                if (context != null) {
                    return stringFromStringContext(context);

                }
            }
        }
        return result;
    }

    private static String stringFromStringContext(final StringContext context) {
        StringBuilder str = new StringBuilder();
        for (TerminalNode stringNode : context.STRING()) {
            String result = stringNode.getText();
            if (!result.contains("\"")) {
                str.append(result);
            } else {
                str.append(result.replace("\"", ""));
            }
        }
        return str.toString();
    }
}
