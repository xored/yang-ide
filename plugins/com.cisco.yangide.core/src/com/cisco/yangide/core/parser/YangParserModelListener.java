/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.parser;

import java.net.URI;
import java.util.Stack;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.opendaylight.yangtools.antlrv4.code.gen.YangLexer;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Anyxml_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Augment_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Base_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Belongs_to_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Case_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Choice_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Contact_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Container_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Description_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Deviation_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Extension_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Feature_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Grouping_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Identity_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Import_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Include_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Input_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Key_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Leaf_list_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Leaf_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.List_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Module_header_stmtsContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Module_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Namespace_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Notification_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Organization_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Output_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Prefix_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Reference_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Revision_date_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Revision_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Revision_stmtsContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Rpc_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Status_stmtContext;
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
import com.cisco.yangide.core.dom.AnyXmlSchemaNode;
import com.cisco.yangide.core.dom.AugmentationSchema;
import com.cisco.yangide.core.dom.BaseReference;
import com.cisco.yangide.core.dom.ChoiceCaseNode;
import com.cisco.yangide.core.dom.ChoiceNode;
import com.cisco.yangide.core.dom.ContrainerSchemaNode;
import com.cisco.yangide.core.dom.Deviation;
import com.cisco.yangide.core.dom.ExtensionDefinition;
import com.cisco.yangide.core.dom.FeatureDefinition;
import com.cisco.yangide.core.dom.GroupingDefinition;
import com.cisco.yangide.core.dom.IdentitySchemaNode;
import com.cisco.yangide.core.dom.LeafListSchemaNode;
import com.cisco.yangide.core.dom.LeafSchemaNode;
import com.cisco.yangide.core.dom.ListSchemaNode;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.ModuleImport;
import com.cisco.yangide.core.dom.NotificationDefinition;
import com.cisco.yangide.core.dom.QName;
import com.cisco.yangide.core.dom.RevisionNode;
import com.cisco.yangide.core.dom.RpcDefinition;
import com.cisco.yangide.core.dom.RpcInputNode;
import com.cisco.yangide.core.dom.RpcOutputNode;
import com.cisco.yangide.core.dom.SimpleNamedNode;
import com.cisco.yangide.core.dom.SimpleNode;
import com.cisco.yangide.core.dom.SubModule;
import com.cisco.yangide.core.dom.SubModuleInclude;
import com.cisco.yangide.core.dom.TypeDefinition;
import com.cisco.yangide.core.dom.TypeReference;
import com.cisco.yangide.core.dom.UsesNode;

/**
 * @author Konstantin Zaitsev
 * @date Jun 26, 2014
 */
public class YangParserModelListener extends YangParserBaseListener {

    private Module module;
    private URI namespace = URI.create("none");
    private String yangModelPrefix;
    private String revision = "1970-01-01"; // default revision Date(0L)
    private Stack<ASTNode> stack = new Stack<>();

    @Override
    public void enterModule_stmt(Module_stmtContext ctx) {
        module = new Module();
        module.setNamespace(URI.create(""));
        module.setRevision(revision);
        stack.push(module);
        updateNamedNode(module, ctx);
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
        stack.peek().setFlags(ASTNode.MALFORMED);
        module.setFlags(ASTNode.MALFORMED);
        super.visitErrorNode(node);
    }

    @Override
    public void enterSubmodule_stmt(Submodule_stmtContext ctx) {
        module = new SubModule();
        module.setNamespace(URI.create(""));
        module.setRevision(revision);
        stack.push(module);
        updateNamedNode(module, ctx);
    }

    @Override
    public void enterBelongs_to_stmt(Belongs_to_stmtContext ctx) {
        if (module instanceof SubModule) {
            String moduleName = stringFromNode(ctx);
            String prefix = null;
            for (int i = 0; i < ctx.getChildCount(); ++i) {
                final ParseTree treeNode = ctx.getChild(i);
                if (treeNode instanceof Prefix_stmtContext) {
                    prefix = stringFromNode(treeNode);
                }
            }
            ((SubModule) module).setParentModule(moduleName);
            ((SubModule) module).setParentPrefix(prefix);
        }
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
        module.addImport(moduleImport);
    }

    @Override
    public void enterInclude_stmt(Include_stmtContext ctx) {
        String includeRevision = null;

        for (int i = 0; i < ctx.getChildCount(); ++i) {
            final ParseTree treeNode = ctx.getChild(i);
            if (treeNode instanceof Revision_date_stmtContext) {
                includeRevision = stringFromNode(treeNode);
            }
        }
        SubModuleInclude subModuleInclude = new SubModuleInclude(module, includeRevision);
        updateNamedNode(subModuleInclude, ctx);
        module.getIncludes().put(subModuleInclude.getName(), subModuleInclude);
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

    @Override
    public void enterAugment_stmt(Augment_stmtContext ctx) {
        AugmentationSchema augmentation = new AugmentationSchema(stack.peek());
        updateNamedNode(augmentation, ctx);
        stack.push(augmentation);
    }

    @Override
    public void exitAugment_stmt(Augment_stmtContext ctx) {
        stack.pop();
    }

    @Override
    public void enterDeviation_stmt(Deviation_stmtContext ctx) {
        Deviation deviation = new Deviation(stack.peek());
        updateNamedNode(deviation, ctx);
        stack.push(deviation);
    }

    @Override
    public void exitDeviation_stmt(Deviation_stmtContext ctx) {
        stack.pop();
    }

    @Override
    public void enterExtension_stmt(Extension_stmtContext ctx) {
        ExtensionDefinition extension = new ExtensionDefinition(stack.peek());
        updateNamedNode(extension, ctx);
        stack.push(extension);
    }

    @Override
    public void exitExtension_stmt(Extension_stmtContext ctx) {
        stack.pop();
    }

    @Override
    public void enterFeature_stmt(Feature_stmtContext ctx) {
        FeatureDefinition feature = new FeatureDefinition(stack.peek());
        updateNamedNode(feature, ctx);
        stack.push(feature);
    }

    @Override
    public void exitFeature_stmt(Feature_stmtContext ctx) {
        stack.pop();
    }

    @Override
    public void enterNotification_stmt(Notification_stmtContext ctx) {
        NotificationDefinition notification = new NotificationDefinition(stack.peek());
        updateNamedNode(notification, ctx);
        stack.push(notification);
    }

    @Override
    public void exitNotification_stmt(Notification_stmtContext ctx) {
        stack.pop();
    }

    @Override
    public void enterRpc_stmt(Rpc_stmtContext ctx) {
        RpcDefinition rpc = new RpcDefinition(stack.peek());
        updateNamedNode(rpc, ctx);
        stack.push(rpc);
    }

    @Override
    public void exitRpc_stmt(Rpc_stmtContext ctx) {
        stack.pop();
    }

    @Override
    public void enterInput_stmt(Input_stmtContext ctx) {
        RpcInputNode input = new RpcInputNode(stack.peek());
        updateNodePosition(input, ctx);
        input.setName("input");
        input.setNameStartPosition(input.getStartPosition());
        input.setNameLength(input.getName().length());
        stack.push(input);
    }

    @Override
    public void exitInput_stmt(Input_stmtContext ctx) {
        stack.pop();
    }

    @Override
    public void enterOutput_stmt(Output_stmtContext ctx) {
        RpcOutputNode output = new RpcOutputNode(stack.peek());
        updateNodePosition(output, ctx);
        output.setName("output");
        output.setNameStartPosition(output.getStartPosition());
        output.setNameLength(output.getName().length());
        stack.push(output);
    }

    @Override
    public void exitOutput_stmt(Output_stmtContext ctx) {
        stack.pop();
    }

    @Override
    public void enterIdentity_stmt(Identity_stmtContext ctx) {
        IdentitySchemaNode identity = new IdentitySchemaNode(stack.peek());
        updateNamedNode(identity, ctx);
        Base_stmtContext base = getChildNode(ctx, Base_stmtContext.class);
        if (base != null) {
            BaseReference baseRef = new BaseReference(identity);
            baseRef.setType(parseQName(stringFromNode(base)));
            updateNamedNode(baseRef, base);
            identity.setBase(baseRef);
        }
    }

    @Override
    public void enterLeaf_list_stmt(Leaf_list_stmtContext ctx) {
        LeafListSchemaNode leafList = new LeafListSchemaNode(stack.peek());
        stack.push(leafList);
        updateNamedNode(leafList, ctx);
    }

    @Override
    public void exitLeaf_list_stmt(Leaf_list_stmtContext ctx) {
        stack.pop();
    }

    @Override
    public void enterList_stmt(List_stmtContext ctx) {
        ListSchemaNode list = new ListSchemaNode(stack.peek());
        stack.push(list);
        updateNamedNode(list, ctx);
    }

    @Override
    public void enterKey_stmt(Key_stmtContext ctx) {
        ASTNode node = stack.peek();
        if (node instanceof ListSchemaNode) {
            SimpleNamedNode keyNode = new SimpleNamedNode(node, "key");
            updateNamedNode(keyNode, ctx);
            ((ListSchemaNode) node).setKey(keyNode);
        }
    }

    @Override
    public void exitList_stmt(List_stmtContext ctx) {
        stack.pop();
    }

    @Override
    public void enterAnyxml_stmt(Anyxml_stmtContext ctx) {
        AnyXmlSchemaNode xml = new AnyXmlSchemaNode(stack.peek());
        stack.push(xml);
        updateNamedNode(xml, ctx);
    }

    @Override
    public void exitAnyxml_stmt(Anyxml_stmtContext ctx) {
        stack.pop();
    }

    @Override
    public void enterChoice_stmt(Choice_stmtContext ctx) {
        ChoiceNode choice = new ChoiceNode(stack.peek());
        stack.push(choice);
        updateNamedNode(choice, ctx);
    }

    @Override
    public void exitChoice_stmt(Choice_stmtContext ctx) {
        stack.pop();
    }

    @Override
    public void enterCase_stmt(Case_stmtContext ctx) {
        ChoiceCaseNode choiceCase = new ChoiceCaseNode(stack.peek());
        stack.push(choiceCase);
        updateNamedNode(choiceCase, ctx);
    }

    @Override
    public void exitCase_stmt(Case_stmtContext ctx) {
        stack.pop();
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
            ModuleImport moduleImport = module.getImportByPrefix(parts[0]);
            if (moduleImport != null) {
                return new QName(moduleImport.getName(), moduleImport.getPrefix(), parts[1], moduleImport.getRevision());
            }

            if (module instanceof SubModule) {
                SubModule subModule = (SubModule) module;
                if (parts[0].equals(subModule.getParentPrefix())) {
                    return new QName(subModule.getParentModule(), parts[0], parts[1], null);
                }
            }
        }

        String prefix = module.getPrefix() != null ? module.getPrefix().getValue() : null;
        return new QName(module.getName(), prefix, typeName, revision);
    }

    private void updateRevisionForRevisionStatement(final ParseTree treeNode) {
        final String revisionDate = stringFromNode(treeNode);
        if ((revisionDate != null) && (this.revision.compareTo(revisionDate) < 0)) {
            this.revision = revisionDate;
            RevisionNode revisionNode = new RevisionNode(module);
            updateNodePosition(revisionNode, treeNode);
            updateNamedNode(revisionNode, treeNode);
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
            astNode.setLineNumber(((ParserRuleContext) treeNode).start.getLine());
            astNode.setLength(((ParserRuleContext) treeNode).stop.getStopIndex() - astNode.getStartPosition());

            Token bodyToken = null;
            for (int i = 0; i < treeNode.getChildCount(); ++i) {
                if (treeNode.getChild(i) instanceof TerminalNode) {
                    final TerminalNode term = (TerminalNode) treeNode.getChild(i);
                    if (term != null && term.getSymbol() != null && term.getSymbol().getType() == YangLexer.LEFT_BRACE) {
                        bodyToken = term.getSymbol();
                    }
                }
            }

            if (bodyToken != null) {
                astNode.setBodyStartPosition(bodyToken.getStartIndex());
            }

            setNodeDescription(astNode, treeNode);
        }
    }

    private void setNodeDescription(ASTNode astNode, ParseTree treeNode) {
        String description = null;
        int descriptionPosition = -1;

        String reference = null;
        int referencePosition = -1;

        String status = null;
        int statusPosition = -1;

        for (int i = 0; i < treeNode.getChildCount(); i++) {
            ParseTree child = treeNode.getChild(i);
            if (child instanceof Description_stmtContext) {
                description = stringFromNode(child);
                descriptionPosition = getStringStartPosition(child);
            } else if (child instanceof Reference_stmtContext) {
                reference = stringFromNode(child);
                referencePosition = getStringStartPosition(child);
            } else if (child instanceof Status_stmtContext) {
                status = stringFromNode(child);
                statusPosition = getStringStartPosition(child);
            } else {
                if (description != null && reference != null) {
                    break;
                }
            }
        }
        astNode.setStatus(status);
        astNode.setStatusStartPosition(statusPosition);
        astNode.setDescription(description);
        astNode.setDescriptionStartPosition(descriptionPosition);
        astNode.setReference(reference);
        astNode.setReferenceStartPosition(referencePosition);
    }

    private void updateNamedNode(ASTNamedNode astNode, ParseTree treeNode) {
        updateNodePosition(astNode, treeNode);
        for (int i = 0; i < treeNode.getChildCount(); ++i) {
            if (treeNode.getChild(i) instanceof StringContext) {
                final StringContext context = (StringContext) treeNode.getChild(i);
                if (context != null) {
                    Token token = context.getStart();
                    astNode.setNameStartPosition(token.getStartIndex());
                    astNode.setNameLength(token.getStopIndex() - token.getStartIndex() + 1);
                    astNode.setLineNumber(token.getLine());
                    astNode.setName(stringFromStringContext(context));
                }
            }
        }
    }

    private int getStringStartPosition(ParseTree treeNode) {
        for (int i = 0; i < treeNode.getChildCount(); ++i) {
            if (treeNode.getChild(i) instanceof StringContext) {
                final StringContext context = (StringContext) treeNode.getChild(i);
                if (context != null) {
                    Token token = context.getStart();
                    return token.getStartIndex();
                }
            }
        }
        return -1;
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

    @SuppressWarnings("unchecked")
    private <T> T getChildNode(ParseTree ctx, Class<T> clazz) {
        for (int i = 0; i < ctx.getChildCount(); ++i) {
            final ParseTree treeNode = ctx.getChild(i);
            if (treeNode.getClass().equals(clazz)) {
                return (T) treeNode;
            }
        }
        return null;
    }
}
