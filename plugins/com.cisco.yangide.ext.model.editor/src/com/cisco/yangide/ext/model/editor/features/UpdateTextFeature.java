package com.cisco.yangide.ext.model.editor.features;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.Shape;

public class UpdateTextFeature extends AbstractUpdateFeature {

    public UpdateTextFeature(IFeatureProvider fp) {
        super(fp);
    }

    @Override
    public boolean canUpdate(IUpdateContext context) {
        Object[] objects = getAllBusinessObjectsForPictogramElement(context.getPictogramElement());
        return null != objects && 2 == objects.length && objects[0] instanceof EObject && objects [1] instanceof EStructuralFeature;

    }

    @Override
    public IReason updateNeeded(IUpdateContext context) {
        Object[] objects = getAllBusinessObjectsForPictogramElement(context.getPictogramElement());
        if (null != objects && 1 < objects.length) {
            String pictogramValue = null;
            String objectValue = null;
            if (context.getPictogramElement() instanceof Shape) {
                if (((Shape) context.getPictogramElement()).getGraphicsAlgorithm() instanceof Text) {
                    pictogramValue = ((Text) ((Shape) context.getPictogramElement()).getGraphicsAlgorithm()).getValue();
                }
            }
            if (null != objects && 2 == objects.length && objects[0] instanceof EObject
                    && objects[1] instanceof EStructuralFeature) {
                objectValue = null == ((EObject) objects[0]).eGet((EStructuralFeature) objects[1]) ? null
                        : ((EObject) objects[0]).eGet((EStructuralFeature) objects[1]).toString();
            }
            boolean updateNeeded = (null == pictogramValue && null != objectValue)
                    || (null != pictogramValue && !pictogramValue.equals(objectValue));
            if (updateNeeded) {
                return Reason.createTrueReason("Name is out of date"); //$NON-NLS-1$
            } else {
                return Reason.createFalseReason();
            }
        }
        return Reason.createFalseReason();
    }

    @Override
    public boolean update(IUpdateContext context) {
        Object[] objects = getAllBusinessObjectsForPictogramElement(context.getPictogramElement());
        if (null != objects && 2 == objects.length && objects[0] instanceof EObject && objects[1] instanceof EStructuralFeature) {
            String objectValue = null == ((EObject) objects[0]).eGet((EStructuralFeature) objects[1]) ? null : ((EObject) objects[0]).eGet((EStructuralFeature) objects[1]).toString();
            if (context.getPictogramElement() instanceof Shape) {
                if (((Shape) context.getPictogramElement()).getGraphicsAlgorithm() instanceof Text) {
                    ((Text)((Shape) context.getPictogramElement()).getGraphicsAlgorithm()).setValue(objectValue);
                    return true;
                }
            }
        }
        return false;
    }

}
