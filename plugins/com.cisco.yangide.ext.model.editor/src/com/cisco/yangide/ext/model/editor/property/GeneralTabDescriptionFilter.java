/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.model.editor.property;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;

import com.cisco.yangide.ext.model.TaggedNode;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;
import com.cisco.yangide.ext.model.editor.util.YangTag;

/**
 * @author Konstantin Zaitsev
 * @date Aug 29, 2014
 */
public class GeneralTabDescriptionFilter extends AbstractPropertySectionFilter {

    @Override
    protected boolean accept(PictogramElement pictogramElement) {
        EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pictogramElement);
        return YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getTaggedNode(), bo)
                && YangModelUtil.getTag(YangTag.DESCRIPTION, (TaggedNode) bo) != null;
    }
}
