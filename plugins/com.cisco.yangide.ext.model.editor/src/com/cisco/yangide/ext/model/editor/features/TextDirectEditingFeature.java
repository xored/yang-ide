package com.cisco.yangide.ext.model.editor.features;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.pictograms.Shape;

public class TextDirectEditingFeature extends AbstractDirectEditingFeature {
    public TextDirectEditingFeature(IFeatureProvider fp) {
        super(fp);
    }

    @Override
    public int getEditingType() {
        return TYPE_TEXT;
    }

    @Override
    public String getInitialValue(IDirectEditingContext context) {
        Object[] objects = getAllBusinessObjectsForPictogramElement(context.getPictogramElement());
        if (null != objects && 2 == objects.length &&  objects[0] instanceof EObject && objects[1] instanceof EStructuralFeature) {
            return null == ((EObject) objects[0]).eGet((EStructuralFeature) objects[1]) ? null : ((EObject) objects[0]).eGet((EStructuralFeature) objects[1]).toString();
        }
        return null;
    }

    @Override
    public boolean canDirectEdit(IDirectEditingContext context) {
        Object[] objects = getAllBusinessObjectsForPictogramElement(context.getPictogramElement());
        return null != objects && 2 == objects.length ;
    }

    @Override
    public void setValue(String value, IDirectEditingContext context) {
        Object[] objects = getAllBusinessObjectsForPictogramElement(context.getPictogramElement());
        if (null != objects && 2 == objects.length &&  objects[0] instanceof EObject && objects[1] instanceof EStructuralFeature) {
            ((EObject) objects[0]).eSet(((EStructuralFeature) objects[1]), value);
        }
        updatePictogramElement(((Shape) context.getPictogramElement()));
    }    

}
