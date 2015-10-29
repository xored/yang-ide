/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.model.editor.sync;

import com.cisco.yangide.core.dom.ContrainerSchemaNode;

/**
 * @author Konstantin Zaitsev
 * @date Aug 28, 2014
 */
public class TypedefPropertyUpdater extends SourceNodePropertyUpdater<ContrainerSchemaNode> {

    public TypedefPropertyUpdater(DiagramModelAdapter adapter) {
        super(adapter);
    }

    @Override
    protected boolean isHandleProperty(String name) {
        return "default".equals(name) || "units".equals(name);
    }
}
