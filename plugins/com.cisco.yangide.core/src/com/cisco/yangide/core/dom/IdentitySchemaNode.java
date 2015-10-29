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
 * @date Jul 23, 2014
 */
public class IdentitySchemaNode extends ASTNamedNode {
    private BaseReference base;

    public IdentitySchemaNode(ASTNode parent) {
        super(parent);
    }

    @Override
    public String getNodeName() {
        return "identity";
    }

    /**
     * @return the base
     */
    public BaseReference getBase() {
        return base;
    }

    /**
     * @param base the base to set
     */
    public void setBase(BaseReference base) {
        this.base = base;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.preVisit(this);
        visitor.visit(this);
        if (base != null) {
            base.accept(visitor);
        }
    }
}
