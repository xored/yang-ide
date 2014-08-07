package com.cisco.yangide.ext.model.editor.features;

import org.eclipse.emf.ecore.EObject;
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
        return YangModelUIUtil.drawPictogramConnectionElement((IAddConnectionContext) context, getFeatureProvider(), ((EObject) context.getNewObject()).eClass().getName().toLowerCase());
    }

}
