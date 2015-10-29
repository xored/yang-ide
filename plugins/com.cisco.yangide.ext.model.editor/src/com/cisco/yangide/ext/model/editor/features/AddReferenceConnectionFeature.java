/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.model.editor.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import com.cisco.yangide.ext.model.editor.util.YangModelUIUtil;

public class AddReferenceConnectionFeature extends AbstractAddFeature {

    public AddReferenceConnectionFeature(IFeatureProvider fp) {
        super(fp);
    }

    @Override
    public boolean canAdd(IAddContext context) {
        if (context instanceof IAddConnectionContext && null != context.getNewObject()) {
            IAddConnectionContext acc = (IAddConnectionContext) context;
            return null != acc.getSourceAnchor() && null != acc.getTargetAnchor();
        }
        return false;
    }

    @Override
    public PictogramElement add(IAddContext context) {
        return YangModelUIUtil.drawPictogramConnectionElement((IAddConnectionContext) context, getFeatureProvider());
    }

}
