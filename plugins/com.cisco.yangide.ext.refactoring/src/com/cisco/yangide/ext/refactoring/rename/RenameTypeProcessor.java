/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.refactoring.rename;

import com.cisco.yangide.core.dom.TypeDefinition;
import com.cisco.yangide.core.indexing.ElementIndexReferenceType;

/**
 * @author Konstantin Zaitsev
 * @date Aug 2, 2014
 */
public class RenameTypeProcessor extends YangRenameProcessor<TypeDefinition> {

    public RenameTypeProcessor(TypeDefinition type) {
        super(type);
    }

    @Override
    public String getIdentifier() {
        return "com.cisco.yangide.ext.refactoring.rename.RenameTypeProcessor";
    }

    @Override
    protected ElementIndexReferenceType getReferenceType() {
        return ElementIndexReferenceType.TYPE_REF;
    }
}
