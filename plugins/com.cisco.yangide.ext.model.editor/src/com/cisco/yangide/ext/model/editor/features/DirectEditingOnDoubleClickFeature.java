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
