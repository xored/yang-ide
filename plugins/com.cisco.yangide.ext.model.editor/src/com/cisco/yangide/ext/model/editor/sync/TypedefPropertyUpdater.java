/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
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
