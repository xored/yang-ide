package com.cisco.yangide.ext.model.editor.features;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;

import com.cisco.yangide.ext.model.editor.util.LayoutUtil;

public class DiagramLayoutFeature extends AbstractLayoutFeature {

    public DiagramLayoutFeature(IFeatureProvider fp) {
        super(fp);
    }

    @Override
    public boolean canLayout(ILayoutContext context) {
        return context.getPictogramElement() instanceof Diagram;
    }

    @Override
    public boolean layout(ILayoutContext context) {
        if (context.getPictogramElement() instanceof ContainerShape) {
            EList<Shape> elements = ((ContainerShape)context.getPictogramElement()).getChildren();
            for (Shape sh : elements) {
                layoutPictogramElement(sh);
            }
            LayoutUtil.layoutDiagram(getFeatureProvider());
            return true;
        }
        return false;
    }

}
