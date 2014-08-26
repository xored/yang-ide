package com.cisco.yangide.ext.model.editor.patterns.objects;

import org.eclipse.emf.ecore.EClass;

import com.cisco.yangide.ext.model.editor.util.YangDiagramImageProvider;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class ChoiceCasePattern extends DomainObjectPattern {

    @Override
    public String getCreateImageId() {
        return YangDiagramImageProvider.IMG_CHOICE_CASE_PROPOSAL;
    }

    @Override
    public String getCreateName() {
        return "case";
    }

    @Override
    public EClass getObjectEClass() {
        return YangModelUtil.MODEL_PACKAGE.getChoiceCase();
    }

}
