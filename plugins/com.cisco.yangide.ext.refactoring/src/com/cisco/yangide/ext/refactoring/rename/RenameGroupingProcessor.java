/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.refactoring.rename;

import com.cisco.yangide.core.dom.GroupingDefinition;
import com.cisco.yangide.core.indexing.ElementIndexReferenceType;

/**
 * @author Konstantin Zaitsev
 * @date Jul 30, 2014
 */
public class RenameGroupingProcessor extends YangRenameProcessor<GroupingDefinition> {

    public RenameGroupingProcessor(GroupingDefinition grouping) {
        super(grouping);
    }

    @Override
    public String getIdentifier() {
        return "com.cisco.yangide.ext.refactoring.rename.RenameGroupingProcessor";
    }

    @Override
    protected ElementIndexReferenceType getReferenceType() {
        return ElementIndexReferenceType.USES;
    }
}
