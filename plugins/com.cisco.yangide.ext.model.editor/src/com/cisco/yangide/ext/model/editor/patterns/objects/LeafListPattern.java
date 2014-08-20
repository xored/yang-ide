package com.cisco.yangide.ext.model.editor.patterns.objects;

import org.eclipse.emf.ecore.EClass;

import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class LeafListPattern extends DomainObjectPattern {

    @Override
    public String getCreateImageId() {
        return null;
    }

    @Override
    public String getCreateName() {
        return "leaf list";
    }

    @Override
    protected EClass getObjectEClass() {
        return YangModelUtil.MODEL_PACKAGE.getLeafList();
    }

}
