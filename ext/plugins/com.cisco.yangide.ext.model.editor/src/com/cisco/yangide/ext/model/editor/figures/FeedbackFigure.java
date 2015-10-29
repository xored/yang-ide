/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.model.editor.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.platform.ga.IGraphicsAlgorithmRenderer;
import org.eclipse.graphiti.ui.internal.figures.GFRectangleFigure;
import org.eclipse.graphiti.ui.internal.figures.GFRoundedRectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

@SuppressWarnings("restriction")
public class FeedbackFigure extends Shape implements IGraphicsAlgorithmRenderer {

    private EditPart editPart;
    private int position;

    public FeedbackFigure(GraphicsAlgorithm algorithm) {
        position = 0;
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

    public void setPosition(int position) {
        if (position == 0) {
            this.position = getParent().getBounds().y + 20;
        } else {
            int pos = 0;
            IFigure figure = null;
            for (Object child : getParent().getChildren()) {
                if (child instanceof GFRectangleFigure || child instanceof GFRoundedRectangle) {
                    figure = (IFigure) child;
                    pos++;
                    if (pos == position) {
                        break;
                    }
                }
            }
            if (figure != null) {
                this.position = figure.getBounds().getBottom().y;
            }
        }
        setBounds(getParent().getBounds());
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
    }

    @Override
    public void repaint() {
        super.repaint();
    }

    @Override
    protected void fillShape(Graphics g) {
    }

    @Override
    protected void outlineShape(Graphics g) {
        int x = getParent().getBounds().x + 15;
        int x2 = x + getParent().getBounds().width - 30;
        g.setForegroundColor(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
        g.drawPolyline(new int[] { x - 2, position - 2, x, position, x - 2, position + 2 });
        g.drawPolyline(new int[] { x, position, x2, position });
        g.drawPolyline(new int[] { x2 + 3, position - 3, x2, position, x2 + 2, position + 2 });
    }
}
