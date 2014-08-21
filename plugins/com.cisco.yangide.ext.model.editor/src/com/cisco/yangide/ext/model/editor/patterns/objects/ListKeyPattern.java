package com.cisco.yangide.ext.model.editor.patterns.objects;

import org.eclipse.emf.ecore.EClass;

import com.cisco.yangide.ext.model.editor.util.YangDiagramImageProvider;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class ListKeyPattern extends DomainObjectPattern {
    

    @Override
    public String getCreateImageId() {
        return YangDiagramImageProvider.IMG_LIST_KEY_PROPOSAL;
    }

    @Override
    public String getCreateName() {
        return "list key";
    }

    @Override
    protected EClass getObjectEClass() {
        return YangModelUtil.MODEL_PACKAGE.getListKey();
    }

}
