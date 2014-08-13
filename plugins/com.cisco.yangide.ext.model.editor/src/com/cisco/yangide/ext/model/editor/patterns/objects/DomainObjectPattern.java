package com.cisco.yangide.ext.model.editor.patterns.objects;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.pattern.AbstractPattern;
import org.eclipse.graphiti.pattern.IPattern;

import com.cisco.yangide.ext.model.editor.util.LayoutUtil;
import com.cisco.yangide.ext.model.editor.util.YangModelUIUtil;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;
import com.cisco.yangide.ext.model.impl.ModelFactoryImpl;

public abstract class DomainObjectPattern extends AbstractPattern implements IPattern {

    public DomainObjectPattern() {
        super(null);
    }
    
    @Override
    public boolean canLayout(ILayoutContext context) {
        return super.canLayout(context) && context.getPictogramElement() instanceof ContainerShape;
    }

    @Override
    public boolean layout(ILayoutContext context) {
        if (context.getPictogramElement() instanceof ContainerShape && (!(context.getPictogramElement() instanceof Diagram))) {
            LayoutUtil.layoutContainerShape((ContainerShape) context.getPictogramElement(), getFeatureProvider());
            return true;
        }
        return false;
    }

    protected abstract EClass getObjectEClass();
    
    protected String getHeaderText(Object obj) {
        return getCreateName();
    }

    @Override
    public boolean canCreate(ICreateContext context) {
        return canContain(context.getTargetContainer());
    }

    @Override
    protected boolean isPatternControlled(PictogramElement pictogramElement) {
        Object domainObject = getBusinessObjectForPictogramElement(pictogramElement);
        return isMainBusinessObjectApplicable(domainObject);
    }

    @Override
    protected boolean isPatternRoot(PictogramElement pictogramElement) {
        Object domainObject = getBusinessObjectForPictogramElement(pictogramElement);
        return isMainBusinessObjectApplicable(domainObject);
    }

    @Override
    public boolean canAdd(IAddContext context) {
        return canContain(context.getTargetContainer(), context.getNewObject()) && checkEClass(context.getNewObject());
    }

    protected EObject createEObject() {
        return ModelFactoryImpl.eINSTANCE.create(getObjectEClass());
    }

    @Override
    public Object[] create(ICreateContext context) {
        EObject newDomainObject = createEObject();
        ContainerShape con = context.getTargetContainer();
        addGraphicalRepresentation(context, newDomainObject);
        getDiagram().eResource().getContents().add(newDomainObject);
        YangModelUtil.add(getBusinessObjectForPictogramElement(con), newDomainObject, YangModelUIUtil.getPositionInParent(context.getTargetContainer(), context.getY(), getFeatureProvider()));
        return new Object[] { newDomainObject };
    }

    protected boolean canContain(ContainerShape shape, Object n) {
        Object parent = getBusinessObjectForPictogramElement(shape);
        if (null != parent && YangModelUtil.canContain(parent)) {
            return YangModelUtil.canContain(parent, n);
        }
        return shape instanceof Diagram;
    }

    protected boolean canContain(ContainerShape shape) {
        Object parent = getBusinessObjectForPictogramElement(shape);
        if (null != parent && YangModelUtil.canContain(parent) && parent instanceof EObject) {
            return YangModelUtil.canContain(((EObject) parent).eClass(), getObjectEClass());
        }
        return shape instanceof Diagram;
    }
    
    @Override
    public boolean canMoveShape(IMoveShapeContext context) {
        return context.getTargetContainer() == context.getSourceContainer() || canContain(context.getTargetContainer(),
                getBusinessObjectForPictogramElement(context.getPictogramElement()));
    }

    @Override
    public void moveShape(IMoveShapeContext context) {
        super.moveShape(context);
        if (!(context.getTargetContainer() instanceof Diagram)) {
            layoutPictogramElement(context.getTargetContainer());
        }
        LayoutUtil.layoutDiagramConnections(getFeatureProvider());
        
        if (!(context.getTargetContainer() instanceof Diagram && context.getSourceContainer() instanceof Diagram)) {
            int pos = YangModelUtil.getPositionInParent(getBusinessObjectForPictogramElement(context.getSourceContainer()), getBusinessObjectForPictogramElement(context.getPictogramElement()));
            int newPos = YangModelUIUtil.getPositionInParent(context.getTargetContainer(), (Shape) context.getPictogramElement(), getFeatureProvider());
            if (context.getTargetContainer() != context.getSourceContainer() || pos != newPos){ 
                YangModelUtil.move(getBusinessObjectForPictogramElement(context.getSourceContainer()),
                        getBusinessObjectForPictogramElement(context.getTargetContainer()),
                        getBusinessObjectForPictogramElement(context.getPictogramElement()), newPos);
            }
        }
    }
    @Override
    public void resizeShape(IResizeShapeContext context) {
        super.resizeShape(context);
        layoutPictogramElement(context.getPictogramElement());
        if (!(context.getShape().getContainer() instanceof Diagram)) {
            layoutPictogramElement(context.getShape().getContainer());
        }
    }

    @Override
    public boolean isMainBusinessObjectApplicable(Object mainBusinessObject) {
        return checkEClass(mainBusinessObject);
    }

    @Override
    public PictogramElement add(IAddContext context) {        
        return YangModelUIUtil.drawPictogramElement(context, getFeatureProvider(), getCreateImageId(), getHeaderText(context.getNewObject()));
    }

    protected boolean checkEClass(Object obj) {
        return obj instanceof EObject && getObjectEClass().isSuperTypeOf(((EObject) obj).eClass());
    }
    
    @Override
    public String getCreateDescription() {
        return "Creates new " + getCreateName() + " object";
    }
    
}
