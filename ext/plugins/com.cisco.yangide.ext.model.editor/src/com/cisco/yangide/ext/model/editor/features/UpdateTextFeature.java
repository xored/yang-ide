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
import com.cisco.yangide.ext.model.TypedNode;
import com.cisco.yangide.ext.model.editor.util.LayoutUtil;
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
                || PropertyUtil.isObjectShapeProp(context.getPictogramElement(), PropertyUtil.BUSINESS_OBJECT_TYPE_SHAPE_KEY) 
                || (null != objects && 2 == objects.length && objects[0] instanceof EObject && objects [1] instanceof EStructuralFeature);

    }

    @Override
    public IReason updateNeeded(IUpdateContext context) {
        String pictogramValue = null;
        String objectValue = null;
        Object[] objects = getAllBusinessObjectsForPictogramElement(context.getPictogramElement());
        if (null == objects || 0 >= objects.length || !(context.getPictogramElement().getGraphicsAlgorithm() instanceof Text)) {
            return Reason.createFalseReason();
        }
        pictogramValue = ((Text) context.getPictogramElement().getGraphicsAlgorithm()).getValue();

        if (PropertyUtil.isObjectShapeProp(context.getPictogramElement(), PropertyUtil.OBJECT_HEADER_TEXT_SHAPE_KEY)) {            
            if (null != objects && 0 != objects.length) {
                if (null != objects && 1 < objects.length) {
                    if (null != objects && 2 == objects.length && objects[0] instanceof EObject
                            && objects[1] instanceof EStructuralFeature) {
                        objectValue = null == ((EObject) objects[0]).eGet((EStructuralFeature) objects[1]) ? null
                                : ((EObject) objects[0]).eGet((EStructuralFeature) objects[1]).toString();
                    }
                } else if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getNode(), objects[0])) {
                    objectValue = YangModelUtil.getQNamePresentation((Node) objects[0]);
                }
            }
        } else if (PropertyUtil.isObjectShapeProp(context.getPictogramElement(), PropertyUtil.BUSINESS_OBJECT_TYPE_SHAPE_KEY)) {
            if (null != objects[0] && YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getTypedNode(), objects[0])) {
                objectValue = YangModelUIUtil.getTypeText((TypedNode) objects[0]);
            }
        } else {
            return Reason.createFalseReason();
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
        Object[] objects = getAllBusinessObjectsForPictogramElement(context.getPictogramElement());
        String objectValue = null;
        if (null == objects || 0 >= objects.length || !(context.getPictogramElement().getGraphicsAlgorithm() instanceof Text)) {
            return false;
        }
        if (PropertyUtil.isObjectShapeProp(context.getPictogramElement(), PropertyUtil.OBJECT_HEADER_TEXT_SHAPE_KEY)) {           
            if (null != objects && 2 == objects.length && objects[0] instanceof EObject
                    && objects[1] instanceof EStructuralFeature) {
                objectValue = null == ((EObject) objects[0]).eGet((EStructuralFeature) objects[1]) ? null
                        : ((EObject) objects[0]).eGet((EStructuralFeature) objects[1]).toString();

            } else if (null != objects && 1 == objects.length && YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getNode(), objects[0])) {
                objectValue = YangModelUtil.getQNamePresentation((Node) objects[0]);
                YangModelUIUtil.updateConnections((Node) objects[0], getFeatureProvider());
            }
            if (context.getPictogramElement().getGraphicsAlgorithm() instanceof Text) {
                    ((Text) context.getPictogramElement().getGraphicsAlgorithm()).setValue(objectValue);
                    if (context.getPictogramElement() instanceof Shape) {
                        LayoutUtil.layoutContainerShapeHeader(((Shape) context.getPictogramElement()).getContainer(), getFeatureProvider());
                    }
                    return true;
            }
        } else if (PropertyUtil.isObjectShapeProp(context.getPictogramElement(), PropertyUtil.BUSINESS_OBJECT_TYPE_SHAPE_KEY)) {
            if (null != objects[0] && YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getTypedNode(), objects[0])) {
                objectValue = YangModelUIUtil.getTypeText((TypedNode) objects[0]);
                if (context.getPictogramElement().getGraphicsAlgorithm() instanceof Text) {
                    ((Text) context.getPictogramElement().getGraphicsAlgorithm()).setValue(objectValue);
                    if (context.getPictogramElement() instanceof Shape) {
                        LayoutUtil.layoutContainerShapeHeader(((Shape) context.getPictogramElement()).getContainer(), getFeatureProvider());
                    }
                    return true;
            }
            }
        }
        return false;
    }
}
