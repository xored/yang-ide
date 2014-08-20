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

import com.cisco.yangide.ext.model.Node;
import com.cisco.yangide.ext.model.editor.util.PropertyUtil;
import com.cisco.yangide.ext.model.editor.util.YangModelUIUtil;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class UpdateTextFeature extends AbstractUpdateFeature {

    public UpdateTextFeature(IFeatureProvider fp) {
        super(fp);
    }

    @Override
    public boolean canUpdate(IUpdateContext context) {
        Object[] objects = getAllBusinessObjectsForPictogramElement(context.getPictogramElement());
        return PropertyUtil.isObjectShapeProp(context.getPictogramElement(), PropertyUtil.OBJECT_HEADER_TEXT_SHAPE_KEY) 
                || (null != objects && 2 == objects.length && objects[0] instanceof EObject && objects [1] instanceof EStructuralFeature);

    }

    @Override
    public IReason updateNeeded(IUpdateContext context) {
        if (!PropertyUtil.isObjectShapeProp(context.getPictogramElement(), PropertyUtil.OBJECT_HEADER_TEXT_SHAPE_KEY)) {
            return Reason.createFalseReason();
        }
        String pictogramValue = null;
        String objectValue = null;
        if (context.getPictogramElement() instanceof Shape) {
            if (((Shape) context.getPictogramElement()).getGraphicsAlgorithm() instanceof Text) {
                pictogramValue = ((Text) ((Shape) context.getPictogramElement()).getGraphicsAlgorithm()).getValue();
            }
        }
        Object[] objects = getAllBusinessObjectsForPictogramElement(context.getPictogramElement());
        if (null != objects && 1 < objects.length) {
            if (null != objects && 2 == objects.length && objects[0] instanceof EObject
                    && objects[1] instanceof EStructuralFeature) {
                objectValue = null == ((EObject) objects[0]).eGet((EStructuralFeature) objects[1]) ? null
                        : ((EObject) objects[0]).eGet((EStructuralFeature) objects[1]).toString();
            }
        } else if (null != objects && 0 < objects.length && YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getNode(), objects[0])) {
            objectValue = YangModelUtil.getQNamePresentation((Node) objects[0]);            
        }
        if ((null == pictogramValue && null != objectValue)
                || (null != pictogramValue && !pictogramValue.equals(objectValue))) {
            return Reason.createTrueReason("Attribute is out of date"); //$NON-NLS-1$
        } else {
            return Reason.createFalseReason();
        }
    }

    @Override
    public boolean update(IUpdateContext context) {
        if (PropertyUtil.isObjectShapeProp(context.getPictogramElement(), PropertyUtil.OBJECT_HEADER_TEXT_SHAPE_KEY)) {
            Object[] objects = getAllBusinessObjectsForPictogramElement(context.getPictogramElement());
            String objectValue = null;
            if (null != objects && 2 == objects.length && objects[0] instanceof EObject
                    && objects[1] instanceof EStructuralFeature) {
                objectValue = null == ((EObject) objects[0]).eGet((EStructuralFeature) objects[1]) ? null
                        : ((EObject) objects[0]).eGet((EStructuralFeature) objects[1]).toString();

            } else if (null != objects && 1 == objects.length && YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getNode(), objects[0])) {
                objectValue = YangModelUtil.getQNamePresentation((Node) objects[0]);
                YangModelUIUtil.updateConnections((Node) objects[0], getFeatureProvider());
            }
            if (context.getPictogramElement() instanceof Shape) {
                if (((Shape) context.getPictogramElement()).getGraphicsAlgorithm() instanceof Text) {
                    ((Text) ((Shape) context.getPictogramElement()).getGraphicsAlgorithm()).setValue(objectValue);
                    return true;
                }
            }
        }        
        return false;
    }
}
