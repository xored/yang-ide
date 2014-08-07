package com.cisco.yangide.ext.model.editor.patterns.objects;

import org.eclipse.emf.ecore.EClass;

import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class AnyxmlPattern extends DomainObjectPattern {

    @Override
    protected EClass getObjectEClass() {
        return YangModelUtil.MODEL_PACKAGE.getAnyxml();
    }

    @Override
    public String getCreateImageId() {
        return null;
    }

    @Override
    public String getCreateName() {
        return "anyxml";
    }
    
    

}
