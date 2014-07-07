/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.dom;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Konstantin Zaitsev
 * @date Jul 4, 2014
 */
public class GroupingDefinition extends ASTNamedNode {
    private List<ASTNode> children;

    public GroupingDefinition(ASTNode parent) {
        super(parent);
        this.children = new ArrayList<>();
    }

    @Override
    public String getNodeName() {
        return "grouping";
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.preVisit(this);
        boolean visitChildren = visitor.visit(this);
        if (visitChildren) {
            acceptChildren(visitor, children);
        }
    }

    /**
     * @return the children
     */
    public List<ASTNode> getChildren() {
        return children;
    }
}
