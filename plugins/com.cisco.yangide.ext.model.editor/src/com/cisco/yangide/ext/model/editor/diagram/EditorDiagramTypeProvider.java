/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.model.editor.diagram;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.platform.ga.IGraphicsAlgorithmRendererFactory;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;

public class EditorDiagramTypeProvider extends AbstractDiagramTypeProvider {

    private IToolBehaviorProvider[] toolBehaviorProviders;

    public EditorDiagramTypeProvider() {
        super();
        setFeatureProvider(new EditorFeatureProvider(this));
    }

    @Override
    public IGraphicsAlgorithmRendererFactory getGraphicsAlgorithmRendererFactory() {
        return new YangDiagramGraphicsAlgorithmRendererFactory(this);
    }

    @Override
    public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
        if (null == toolBehaviorProviders) {
            toolBehaviorProviders = new IToolBehaviorProvider[] { new YangDiagramToolBehaviorProvider(this) };
        }
        return toolBehaviorProviders;
    }

}
