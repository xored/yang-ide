package com.cisco.yangide.ext.model.editor.patterns.objects;

import org.eclipse.emf.ecore.EClass;

import com.cisco.yangide.ext.model.editor.util.YangDiagramImageProvider;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class IdentityPattern extends DomainObjectPattern {

    @Override
    protected EClass getObjectEClass() {
        return YangModelUtil.MODEL_PACKAGE.getIdentity();
    }

    @Override
    public String getCreateImageId() {
        return YangDiagramImageProvider.IMG_IDENTITY_PROPOSAL;
    }

    @Override
    public String getCreateName() {
        return "identity";
    }
    
    

}
