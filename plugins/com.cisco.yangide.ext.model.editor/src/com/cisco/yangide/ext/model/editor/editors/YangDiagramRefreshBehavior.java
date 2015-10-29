/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.model.editor.editors;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.graphiti.ui.editor.DefaultRefreshBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;

import com.cisco.yangide.ext.model.editor.figures.FeedbackFigure;

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
        if (obj instanceof AbstractGraphicalEditPart) {
            IFigure figure = ((AbstractGraphicalEditPart) obj).getFigure();
            for (Object child : figure.getChildren()) {
                if (child instanceof FeedbackFigure) {
                    FeedbackFigure f = (FeedbackFigure) child;
                    if (f.getEditPart() == null) {
                        f.setEditPart((AbstractGraphicalEditPart) obj);
                    }
                    break;
                }
            }
        }
        return super.shouldRefresh(obj);
    }
}
