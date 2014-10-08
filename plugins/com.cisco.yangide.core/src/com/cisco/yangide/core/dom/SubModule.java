/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.dom;

/**
 * @author Konstantin Zaitsev
 * @date Jul 4, 2014
 */
public class SubModule extends Module {
    private String parentModule;
    private String parentPrefix;

    @Override
    public String getNodeName() {
        return "submodule";
    }

    /**
     * @return the parentModule
     */
    public String getParentModule() {
        return parentModule;
    }

    /**
     * @param parentModule the parentModule to set
     */
    public void setParentModule(String parentModule) {
        this.parentModule = parentModule;
    }

    /**
     * @return the parentPrefix
     */
    public String getParentPrefix() {
        if (parentPrefix == null) {
            parentPrefix = prefix != null ? prefix.getValue() : null;
        }
        return parentPrefix;
    }

    /**
     * @param parentPrefix the parentPrefix to set
     */
    public void setParentPrefix(String parentPrefix) {
        this.parentPrefix = parentPrefix;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.preVisit(this);
        boolean visitChildren = visitor.visit(this);
        if (visitChildren) {
            visitChildren(visitor);
        }
    }
}
