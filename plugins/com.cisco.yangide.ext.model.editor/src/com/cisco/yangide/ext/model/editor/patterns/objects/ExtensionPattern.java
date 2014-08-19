package com.cisco.yangide.ext.model.editor.patterns.objects;

import org.eclipse.emf.ecore.EClass;

import com.cisco.yangide.ext.model.editor.util.YangDiagramImageProvider;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class ExtensionPattern extends DomainObjectPattern {

    @Override
    protected EClass getObjectEClass() {
        return YangModelUtil.MODEL_PACKAGE.getExtension();
    }

    @Override
    public String getCreateImageId() {
        return YangDiagramImageProvider.IMG_EXTENSION_PROPOSAL;
    }

    @Override
    public String getCreateName() {
        return "extension";
    }

    
}
