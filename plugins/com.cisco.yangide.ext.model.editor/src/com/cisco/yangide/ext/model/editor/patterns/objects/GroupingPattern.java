package com.cisco.yangide.ext.model.editor.patterns.objects;

import org.eclipse.emf.ecore.EClass;

import com.cisco.yangide.ext.model.editor.util.IYangImageConstants;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class GroupingPattern extends DomainObjectPattern {

    @Override
    public String getCreateName() {
        return "grouping";
    }
    
    @Override
    public String getCreateImageId() {
        return IYangImageConstants.IMG_GROUPING_PROPOSAL;
    }

    @Override
    protected EClass getObjectEClass() {
        return YangModelUtil.MODEL_PACKAGE.getGrouping();
    }  
    
}
