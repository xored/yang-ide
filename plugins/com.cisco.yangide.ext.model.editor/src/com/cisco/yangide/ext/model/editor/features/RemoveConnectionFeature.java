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
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.impl.DefaultRemoveFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;

public class RemoveConnectionFeature extends DefaultRemoveFeature {

    public RemoveConnectionFeature(IFeatureProvider fp) {
        super(fp);
    }

    @Override
    public boolean canRemove(IRemoveContext context) {
        return context.getPictogramElement() instanceof Connection;
    }

    @Override
    public void preRemove(IRemoveContext context) {
        if (context.getPictogramElement() instanceof Connection) {
            Connection con = (Connection) context.getPictogramElement();
            con.getConnectionDecorators().clear();
            if (con instanceof FreeFormConnection) {
                ((FreeFormConnection) con).getBendpoints().clear();
            }
        }
    }

}
