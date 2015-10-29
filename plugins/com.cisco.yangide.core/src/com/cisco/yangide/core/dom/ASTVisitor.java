/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.core.dom;

/**
 * @author Konstantin Zaitsev
 * @date Jul 2, 2014
 */
public abstract class ASTVisitor {

    public boolean visit(Module module) {
        return true;
    }

    public boolean visit(SubModule module) {
        return true;
    }

    public boolean visit(ModuleImport moduleImport) {
        return true;
    }

    public boolean visit(SubModuleInclude subModuleInclude) {
        return true;
    }

    public boolean visit(TypeDefinition typeDefinition) {
        return true;
    }

    public boolean visit(SimpleNode<?> simpleNode) {
        return true;
    }

    public boolean visit(ContrainerSchemaNode container) {
        return true;
    }

    public boolean visit(GroupingDefinition grouping) {
        return true;
    }

    public boolean visit(LeafSchemaNode leaf) {
        return true;
    }

    public boolean visit(UsesNode uses) {
        return true;
    }

    public boolean visit(TypeReference typeRef) {
        return true;
    }

    public boolean visit(NotificationDefinition notification) {
        return true;
    }

    public boolean visit(RpcDefinition rpc) {
        return true;
    }

    public boolean visit(FeatureDefinition feature) {
        return true;
    }

    public boolean visit(AugmentationSchema augmentation) {
        return true;
    }

    public boolean visit(Deviation deviation) {
        return true;
    }

    public boolean visit(ExtensionDefinition extension) {
        return true;
    }

    public boolean visit(IdentitySchemaNode identity) {
        return true;
    }

    public boolean visit(RpcInputNode input) {
        return true;
    }

    public boolean visit(RpcOutputNode output) {
        return true;
    }

    public boolean visit(BaseReference base) {
        return true;
    }

    public boolean visit(LeafListSchemaNode leafListSchemaNode) {
        return true;
    }

    public boolean visit(ListSchemaNode listSchemaNode) {
        return true;
    }

    public boolean visit(SimpleNamedNode simpleNamedNode) {
        return true;
    }

    public boolean visit(ChoiceNode choiceNode) {
        return true;
    }

    public boolean visit(ChoiceCaseNode choiceCaseNode) {
        return true;
    }

    public boolean visit(AnyXmlSchemaNode anyXmlSchemaNode) {
        return true;
    }

    public void preVisit(ASTNode node) {

    }
}
