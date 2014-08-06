/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.ext.refactoring.rename;

import com.cisco.yangide.core.dom.IdentitySchemaNode;
import com.cisco.yangide.core.indexing.ElementIndexReferenceType;

/**
 * @author Konstantin Zaitsev
 * @date Aug 6, 2014
 */
public class RenameIdentityProcessor extends YangRenameProcessor<IdentitySchemaNode> {

    public RenameIdentityProcessor(IdentitySchemaNode type) {
        super(type);
    }

    @Override
    public String getIdentifier() {
        return "com.cisco.yangide.ext.refactoring.rename.RenameIdentityProcessor";
    }

    @Override
    protected ElementIndexReferenceType getReferenceType() {
        return ElementIndexReferenceType.IDENTITY_REF;
    }
}
