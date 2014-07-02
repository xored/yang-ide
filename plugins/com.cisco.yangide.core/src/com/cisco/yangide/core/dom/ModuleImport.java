/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.dom;

import java.util.Date;

/**
 * @author Konstantin Zaitsev
 * @date Jun 26, 2014
 */
public class ModuleImport extends ASTNamedNode {
    
    /** Module revision in date format. */
    private Date revision;
    
    /** Module namespace prefix.*/
    private String prefix;

    public ModuleImport(ASTNode parent, String name, Date revision, String prefix) {
        super(parent, name);
        this.revision = revision;
        this.prefix = prefix;
    }

    @Override
    public String getNodeName() {
        return "import";
    }

    /**
     * @return the revision
     */
    public Date getRevision() {
        return revision;
    }

    /**
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
