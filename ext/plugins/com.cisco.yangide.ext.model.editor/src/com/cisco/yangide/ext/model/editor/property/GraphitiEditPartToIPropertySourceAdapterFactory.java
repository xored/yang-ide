package com.cisco.yangide.ext.model.editor.property;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.platform.GraphitiShapeEditPart;
import org.eclipse.ui.views.properties.IPropertySource;

import com.cisco.yangide.ext.model.TaggedNode;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

@SuppressWarnings("rawtypes")
public class GraphitiEditPartToIPropertySourceAdapterFactory implements IAdapterFactory {

    public GraphitiEditPartToIPropertySourceAdapterFactory() {
        super();
    }

    public Object getAdapter(Object adaptableObject,  Class adapterType) {
        if (IPropertySource.class.equals(adapterType)) {
            if (adaptableObject instanceof GraphitiShapeEditPart) {
                GraphitiShapeEditPart editPart = (GraphitiShapeEditPart) adaptableObject;
                PictogramElement pictogramElement = editPart.getPictogramElement();
                Object object = editPart.getFeatureProvider().getBusinessObjectForPictogramElement(pictogramElement);
                if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getTaggedNode(), object)) {
                    return new AttributesTabContentSource((TaggedNode) object);
                }
            }
        }
        return null;
    }

    public Class[] getAdapterList() {
        return new Class[] {IPropertySource.class};
    }

}
