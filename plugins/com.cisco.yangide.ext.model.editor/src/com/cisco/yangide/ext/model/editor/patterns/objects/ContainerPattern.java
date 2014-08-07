package com.cisco.yangide.ext.model.editor.patterns.objects;

import org.eclipse.emf.ecore.EClass;

import com.cisco.yangide.ext.model.editor.util.IYangImageConstants;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class ContainerPattern extends DomainObjectPattern {

    @Override
    public String getCreateName() {
        return "container";
    }

    @Override
    public String getCreateImageId() {
        return IYangImageConstants.IMG_CONTAINER_PROPOSAL;
    }

    @Override
    protected EClass getObjectEClass() {
        return YangModelUtil.MODEL_PACKAGE.getContainer();
    }
    
}
