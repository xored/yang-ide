/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.model.editor.editors;

import org.eclipse.graphiti.ui.editor.DefaultRefreshBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;

/**
 * @author Konstantin Zaitsev
 * @date Aug 22, 2014
 */
public class YangDiagramRefreshBehavior extends DefaultRefreshBehavior {

    /**
     * @param diagramBehavior
     */
    public YangDiagramRefreshBehavior(DiagramBehavior diagramBehavior) {
        super(diagramBehavior);
    }

    @Override
    public boolean shouldRefresh(Object obj) {
        // if (obj instanceof ShapeEditPart) {
        // IFigure figure = ((ShapeEditPart) obj).getFigure();
        // for (Object child : figure.getChildren()) {
        // if (child instanceof FeedbackFigure) {
        // FeedbackFigure f = (FeedbackFigure) child;
        // if (f.getEditPart() == null) {
        // f.setEditPart((ShapeEditPart) obj);
        // }
        // break;
        // }
        // }
        // }
        return super.shouldRefresh(obj);
    }
}
