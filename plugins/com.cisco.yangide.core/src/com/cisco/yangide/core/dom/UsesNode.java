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
 * @date Jul 8, 2014
 */
public class UsesNode extends ASTNamedNode {
    private QName grouping;

    public UsesNode(ASTNode parent) {
        super(parent);
    }

    @Override
    public String getNodeName() {
        return "uses";
    }

    /**
     * @return the grouping
     */
    public QName getGrouping() {
        return grouping;
    }

    /**
     * @param grouping the grouping to set
     */
    public void setGrouping(QName grouping) {
        this.grouping = grouping;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.preVisit(this);
        visitor.visit(this);
    }
}
