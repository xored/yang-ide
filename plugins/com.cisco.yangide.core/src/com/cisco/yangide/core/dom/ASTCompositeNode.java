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
 * @date Jul 8, 2014
 */
public abstract class ASTCompositeNode extends ASTNamedNode {
    private List<ASTNode> children;

    /**
     * @param parent
     */
    public ASTCompositeNode(ASTNode parent) {
        super(parent);
        children = new ArrayList<>();
    }

    /**
     * @return the children
     */
    public List<ASTNode> getChildren() {
        return children;
    }
}
