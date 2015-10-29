package com.cisco.yangide.ext.model.editor.patterns.objects;

import org.eclipse.emf.ecore.EClass;

import com.cisco.yangide.ext.model.editor.util.YangDiagramImageProvider;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class FeaturePattern extends DomainObjectPattern {

    @Override
    public EClass getObjectEClass() {
        return YangModelUtil.MODEL_PACKAGE.getFeature();
    }

    @Override
    public String getCreateImageId() {
        return YangDiagramImageProvider.IMG_FEATURE_PROPOSAL;
    }

    @Override
    public String getCreateName() {
        return "feature";
    }

    
}
