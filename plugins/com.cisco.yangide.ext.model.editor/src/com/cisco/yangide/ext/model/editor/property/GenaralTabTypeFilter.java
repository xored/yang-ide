package com.cisco.yangide.ext.model.editor.property;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;

import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class GenaralTabTypeFilter extends AbstractPropertySectionFilter {

    @Override
    protected boolean accept(PictogramElement pictogramElement) {
        EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pictogramElement);
        return YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getNode(), bo);
    }

}
