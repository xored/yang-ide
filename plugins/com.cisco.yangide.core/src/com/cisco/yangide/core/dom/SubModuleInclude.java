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
 * @date Jul 11, 2014
 */
public class SubModuleInclude extends ASTNamedNode {

    /** SubModule revision in date format. */
    private String revision;

    public SubModuleInclude(ASTNode parent, String revision) {
        super(parent);
        this.revision = revision;
    }

    @Override
    public String getNodeName() {
        return "include";
    }

    /**
     * @return the revision
     */
    public String getRevision() {
        return revision;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.preVisit(this);
        visitor.visit(this);
    }
}
