/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.refactoring.rename;

import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.indexing.ElementIndexReferenceType;

/**
 * @author Konstantin Zaitsev
 * @date Aug 6, 2014
 */
public class RenameModuleProcessor extends YangRenameProcessor<Module> {

    public RenameModuleProcessor(Module module) {
        super(module);
    }

    @Override
    public String getIdentifier() {
        return "com.cisco.yangide.ext.refactoring.rename.RenameModuleProcessor";
    }

    @Override
    protected ElementIndexReferenceType getReferenceType() {
        return ElementIndexReferenceType.IMPORT;
    }
}
