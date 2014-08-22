package com.cisco.yangide.ext.model.editor.util;

import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

public class PropertyUtil {
    private PropertyUtil() {
        super();
    }
    
    public static final String OBJECT_NUMBER_SHAPE_KEY = "object-number-shape-id";
    public static final String OBJECT_HEADER_TEXT_SHAPE_KEY = "object-header-shape-id";
    public static final String OBJECT_IMAGE_SHAPE_KEY = "object-image-shape-id";
    public static final String BUSINESS_OBJECT_SHAPE_KEY = "business-object-shape-id";
    public static final String BUSINESS_OBJECT_TYPE_SHAPE_KEY = "business-object-type-shape-id";
    
    public static final void setObjectShapeProp(PictogramElement pe, String prop, boolean set) {
        Graphiti.getPeService().setPropertyValue(pe, prop,
            set ? Boolean.TRUE.toString() : Boolean.FALSE.toString());
    }
 
    public static boolean isObjectShapeProp(PictogramElement pe, String prop) {
        return Boolean.TRUE.toString().equals(Graphiti.getPeService()
           .getPropertyValue(pe, prop));
    }
    
    public static boolean isBusinessObjectShape(PictogramElement pe) {
        return isObjectShapeProp(pe, BUSINESS_OBJECT_SHAPE_KEY);
    }
    
}
