package com.cisco.yangide.ext.model.editor.property;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

import com.cisco.yangide.ext.model.TaggedNode;
import com.cisco.yangide.ext.model.editor.util.Strings;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;
import com.cisco.yangide.ext.model.editor.util.YangTag;

public class AttributesTabContentSource implements IPropertySource {

    private TaggedNode node;
    private IPropertyDescriptor[] descriptors;
    
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
        if (null == descriptors) {
            descriptors = YangModelUtil.getPropertyDescriptors(node.eClass()).toArray(new IPropertyDescriptor[0]);
        }
        return descriptors;
    }

    @Override
    public Object getPropertyValue(Object id) {
        YangTag tag = (YangTag) id;
        Object result = YangModelUtil.getValue((YangTag) id, node);
        if (tag.getPropertyDescriptor() instanceof ComboBoxPropertyDescriptor) {
            if (null != tag.getPossibleValues() && tag.getPossibleValues().contains(result)) {
                return tag.getPossibleValues().indexOf(result);
            } else {
                return 0;
            }
        }
        
        if (!Strings.isEmpty(result)) {
            return result.toString();
        }
        return Strings.EMPTY_STRING;
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
        YangTag tag = (YangTag) id;
        if (tag.getPropertyDescriptor() instanceof ComboBoxPropertyDescriptor && null != tag.getPossibleValues() && value instanceof Integer) {
            Integer pos = (Integer) value;
            if (pos < tag.getPossibleValues().size()) {
                YangModelUtil.setValue((YangTag) id, node, tag.getPossibleValues().get(pos));
            }
        } else {
            YangModelUtil.setValue((YangTag) id, node, value);        
        }
    }

}
