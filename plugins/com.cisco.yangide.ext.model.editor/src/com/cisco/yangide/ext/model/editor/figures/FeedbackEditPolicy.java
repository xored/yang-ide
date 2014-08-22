/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.model.editor.figures;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalEditPolicy;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.internal.command.ICommand;
import org.eclipse.graphiti.internal.command.MoveShapeFeatureCommandWithContext;
import org.eclipse.graphiti.ui.internal.command.CreateModelObjectCommand;
import org.eclipse.graphiti.ui.internal.command.GefCommandWrapper;

/**
 * @author Konstantin Zaitsev
 * @date Aug 22, 2014
 */
@SuppressWarnings("restriction")
public class FeedbackEditPolicy extends GraphicalEditPolicy {

    private FeedbackFigure feedbackFigure;
    private EditPolicy editPolicy;

    /**
     * @param feedbackFigure
     * @param editPolicy
     */
    public FeedbackEditPolicy(FeedbackFigure feedbackFigure, EditPolicy editPolicy) {
        this.feedbackFigure = feedbackFigure;
        this.editPolicy = editPolicy;
    }

    @Override
    public EditPart getTargetEditPart(Request request) {
        return editPolicy.getTargetEditPart(request);
    }

    @Override
    public void eraseTargetFeedback(Request request) {
        editPolicy.eraseTargetFeedback(request);
        feedbackFigure.setVisible(false);
    }

    @Override
    public void showTargetFeedback(Request request) {
        if (request.getType().equals(RequestConstants.REQ_CREATE) || request.getType().equals(RequestConstants.REQ_ADD)) {
            Command command = getHost().getCommand(request);
            if (command.canExecute()) {
                int y = 0;
                if (command instanceof GefCommandWrapper) {
                    ICommand cmd = ((GefCommandWrapper) command).getCommand();
                    if (cmd instanceof MoveShapeFeatureCommandWithContext) {
                        y = ((MoveShapeContext) ((MoveShapeFeatureCommandWithContext) cmd).getContext()).getY();
                    }
                } else if (command instanceof CreateModelObjectCommand) {
                    y = ((CreateContext) ((CreateModelObjectCommand) command).getContext()).getY();
                }
                feedbackFigure.setPosition(y);
                feedbackFigure.setVisible(true);
                feedbackFigure.repaint();
            } else {
                feedbackFigure.setVisible(false);
                feedbackFigure.repaint();
            }
        }
        editPolicy.showTargetFeedback(request);
    }
}