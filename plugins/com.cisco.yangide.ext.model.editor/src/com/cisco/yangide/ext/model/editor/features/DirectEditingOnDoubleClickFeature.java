/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.model.editor.features;

import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.Shape;

public class DirectEditingOnDoubleClickFeature extends AbstractCustomFeature {

    public DirectEditingOnDoubleClickFeature(IFeatureProvider fp) {
        super(fp);
    }

    @Override
    public boolean canExecute(ICustomContext context) {
        return true;
    }

    @Override
    public void execute(ICustomContext context) {
        final IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();
        directEditingInfo.setActive(true);
        directEditingInfo.setMainPictogramElement(((Shape) context.getInnerPictogramElement()).getContainer());
        directEditingInfo.setPictogramElement(context.getInnerPictogramElement());
        directEditingInfo.setGraphicsAlgorithm(context.getInnerGraphicsAlgorithm());
        getDiagramBehavior().refresh();

    }

}
