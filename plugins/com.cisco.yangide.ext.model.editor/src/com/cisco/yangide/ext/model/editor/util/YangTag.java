package com.cisco.yangide.ext.model.editor.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.ui.views.properties.PropertyDescriptor;

public enum YangTag {
    DESCRIPTION, YANG_VERSION("yang-version"), NAMESPACE, PREFIX, ORGANIZATION(true), CONTACT, REFERENCE, CONFIG(Arrays.asList(Boolean.TRUE, Boolean.FALSE)), 
    MANDATORY(Arrays.asList(Boolean.TRUE, Boolean.FALSE)), STATUS(Arrays.asList("current", "deprecated", "obsolete")), PRESENCE(Arrays.asList(Boolean.TRUE, Boolean.FALSE)), 
    ORDERED_BY("ordered-by", Arrays.asList("user", "system", "obsolete"));
    private String name;
    private boolean required;
    private List<?> possibleValues;
    private PropertyDescriptor pd;
    private YangTag() {
        required = false;
    }
    private YangTag(boolean required) {
        this();
        this.required = required;
    }
    private YangTag(String name) {
        this();
        this.name = name;
    }
    private YangTag(List<?> possibleValues) {
        this();
        this.possibleValues = possibleValues;
    }
    private YangTag(String name, List<?> possibleValues) {
        this();
        this.name = name;
        this.possibleValues = possibleValues;
    }
    public String getDescriptor() {
        return toString();
    }
    public String getName() {
        if (null == name) {
            return toString().toLowerCase();
        }
        return name;
    }
    public boolean isRequired() {
        return required;
    }
    public Collection<?> getPossibleValues() {
        return possibleValues;
    }
    public PropertyDescriptor getPropertyDescriptor() {
        if (null == pd) {
            pd = new PropertyDescriptor(toString(), getName());
        }
        return pd;
    }
}
