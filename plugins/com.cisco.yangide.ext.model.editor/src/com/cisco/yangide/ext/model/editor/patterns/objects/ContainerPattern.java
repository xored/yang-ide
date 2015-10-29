package com.cisco.yangide.ext.model.editor.patterns.objects;

import org.eclipse.emf.ecore.EClass;

import com.cisco.yangide.ext.model.editor.util.YangDiagramImageProvider;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class ContainerPattern extends DomainObjectPattern {

    @Override
    public String getCreateName() {
        return "container";
    }

    @Override
    public String getCreateImageId() {
        return YangDiagramImageProvider.IMG_CONTAINER_PROPOSAL;
    }

    @Override
    public EClass getObjectEClass() {
        return YangModelUtil.MODEL_PACKAGE.getContainer();
    }
    
}
