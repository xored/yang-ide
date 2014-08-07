package com.cisco.yangide.ext.model.editor.property;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.cisco.yangide.ext.model.TaggedNode;

public class AttributesTabContentSource implements IPropertySource {

    private TaggedNode node;
    
    private static final String NAME_ID = "name";
    private static final PropertyDescriptor NAME_PROP_DESC = new PropertyDescriptor(NAME_ID, "Name");
    private static final IPropertyDescriptor[] DESCRIPTORS = { NAME_PROP_DESC };
    
    public AttributesTabContentSource(TaggedNode node) {
        super();
        this.node = node;
    }
    
    @Override
    public Object getEditableValue() {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        // TODO Auto-generated method stub
        return DESCRIPTORS;
    }

    @Override
    public Object getPropertyValue(Object id) {
        // TODO Auto-generated method stub
        return "test";
    }

    @Override
    public boolean isPropertySet(Object id) {
        return false;
    }

    @Override
    public void resetPropertyValue(Object id) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setPropertyValue(Object id, Object value) {
        // TODO Auto-generated method stub
        
    }

}
