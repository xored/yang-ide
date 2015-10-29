/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.model.editor.diagram;

import org.eclipse.graphiti.platform.ga.IGraphicsAlgorithmRenderer;
import org.eclipse.graphiti.platform.ga.IGraphicsAlgorithmRendererFactory;
import org.eclipse.graphiti.platform.ga.IRendererContext;

import com.cisco.yangide.ext.model.editor.figures.FeedbackFigure;

/**
 * @author Konstantin Zaitsev
 * @date Aug 22, 2014
 */
public class YangDiagramGraphicsAlgorithmRendererFactory implements IGraphicsAlgorithmRendererFactory {

    public YangDiagramGraphicsAlgorithmRendererFactory(EditorDiagramTypeProvider editorDiagramTypeProvider) {
    }

    @Override
    public IGraphicsAlgorithmRenderer createGraphicsAlgorithmRenderer(IRendererContext rendererContext) {
        return new FeedbackFigure(rendererContext.getGraphicsAlgorithm());
    }

}
