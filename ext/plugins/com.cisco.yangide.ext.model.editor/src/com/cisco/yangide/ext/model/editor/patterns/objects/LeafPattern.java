package com.cisco.yangide.ext.model.editor.patterns.objects;

import org.eclipse.emf.ecore.EClass;

import com.cisco.yangide.ext.model.ModelPackage;
import com.cisco.yangide.ext.model.editor.util.YangDiagramImageProvider;

public class LeafPattern extends DomainObjectPattern {

    @Override
    public String getCreateName() {
        return "leaf";
    }

    @Override
    public String getCreateImageId() {
        return YangDiagramImageProvider.IMG_LEAF_PROPOSAL;
    }

    @Override
    public EClass getObjectEClass() {
        return ModelPackage.eINSTANCE.getLeaf();
    }

}
