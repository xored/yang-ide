package com.cisco.yangide.ext.model.editor.patterns.objects;

import org.eclipse.emf.ecore.EClass;

import com.cisco.yangide.ext.model.editor.util.YangDiagramImageProvider;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class GroupingPattern extends DomainObjectPattern {

    @Override
    public String getCreateName() {
        return "grouping";
    }
    
    @Override
    public String getCreateImageId() {
        return YangDiagramImageProvider.IMG_GROUPING_PROPOSAL;
    }

    @Override
    public EClass getObjectEClass() {
        return YangModelUtil.MODEL_PACKAGE.getGrouping();
    }  
    
}
