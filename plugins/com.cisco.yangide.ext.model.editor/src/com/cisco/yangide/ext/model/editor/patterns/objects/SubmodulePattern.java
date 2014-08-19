package com.cisco.yangide.ext.model.editor.patterns.objects;

import org.eclipse.emf.ecore.EClass;

import com.cisco.yangide.ext.model.editor.util.YangDiagramImageProvider;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class SubmodulePattern extends DomainObjectPattern {

    @Override
    public String getCreateName() {
        return "submodule";
    }

    @Override
    public String getCreateImageId() {
        return YangDiagramImageProvider.IMG_SUBMODULE_PROPOSAL;
    }

    @Override
    protected EClass getObjectEClass() {
        return YangModelUtil.MODEL_PACKAGE.getSubmodule();
    }   
    
}
