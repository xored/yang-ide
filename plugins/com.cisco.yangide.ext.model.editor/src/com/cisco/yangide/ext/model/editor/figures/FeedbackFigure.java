/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.model.editor.figures;

import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.platform.ga.IGraphicsAlgorithmRenderer;

public class FeedbackFigure extends Polyline implements IGraphicsAlgorithmRenderer {

    private EditPart editPart;

    public FeedbackFigure(GraphicsAlgorithm algorithm) {
        setVisible(false);
    }

    /**
     * @param editPart
     */
    public void setEditPart(EditPart editPart) {
        this.editPart = editPart;
        editPart.installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,
                new FeedbackEditPolicy(this, editPart.getEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE)));
    }

    /**
     * @return the editPart
     */
    public EditPart getEditPart() {
        return editPart;
    }

    public void setPosition(int y) {
        removeAllPoints();
        addPoint(new Point(getParent().getBounds().x + 10, y));
        addPoint(new Point(getParent().getBounds().x + getParent().getBounds().width - 10, y));
    }
}
