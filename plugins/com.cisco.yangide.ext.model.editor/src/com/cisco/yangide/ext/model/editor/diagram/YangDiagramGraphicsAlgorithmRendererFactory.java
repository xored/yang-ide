/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
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
