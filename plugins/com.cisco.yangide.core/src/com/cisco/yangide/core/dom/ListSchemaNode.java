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
 * @date Aug 20, 2014
 */
public class ListSchemaNode extends ASTCompositeNode {
    private SimpleNamedNode key;

    public ListSchemaNode(ASTNode parent) {
        super(parent);
    }

    @Override
    public String getNodeName() {
        return "list";
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.preVisit(this);
        boolean visitChildren = visitor.visit(this);
        if (visitChildren) {
            acceptChildren(visitor, getChildren());
        }
    }

    /**
     * @return the key
     */
    public SimpleNamedNode getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(SimpleNamedNode key) {
        this.key = key;
    }
}
