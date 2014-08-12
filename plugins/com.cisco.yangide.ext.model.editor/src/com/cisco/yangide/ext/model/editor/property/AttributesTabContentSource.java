package com.cisco.yangide.ext.model.editor.property;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.cisco.yangide.ext.model.TaggedNode;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;
import com.cisco.yangide.ext.model.editor.util.YangTag;

public class AttributesTabContentSource implements IPropertySource {

    private TaggedNode node;
    
    public AttributesTabContentSource(TaggedNode node) {
        super();
        this.node = node;
    }
    
    @Override
    public Object getEditableValue() {
        return this;
    }

    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return YangModelUtil.getPropertyDescriptors(node.eClass()).toArray(new PropertyDescriptor[0]);
    }

    @Override
    public Object getPropertyValue(Object id) {
        return YangModelUtil.getValue((YangTag) id, node);
    }

    @Override
    public boolean isPropertySet(Object id) {
        return false;
    }

    @Override
    public void resetPropertyValue(Object id) {
        
    }

    @Override
    public void setPropertyValue(Object id, Object value) {
        YangModelUtil.setValue((YangTag) id, node, value);        
    }

}
