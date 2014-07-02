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
 * @date Jul 2, 2014
 */
public abstract class ASTVisitor {

    public boolean visit(Module module) {
        return true;
    }

    public boolean visit(ModuleImport module) {
        return true;
    }

    public boolean visit(TypeDefinition typeDefinition) {
        return true;
    }

    public boolean visit(SimpleNode<?> simpleNode) {
        return true;
    }
}
