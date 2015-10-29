/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.model.editor.dialog;

import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.cisco.yangide.core.indexing.ElementIndexInfo;
import com.cisco.yangide.core.indexing.ElementIndexType;
import com.cisco.yangide.ext.model.Module;
import com.cisco.yangide.ext.model.editor.dialog.YangElementListSelectionDialog.Transformer;
import com.cisco.yangide.ext.model.editor.util.Strings;
import com.cisco.yangide.ext.model.editor.util.YangDiagramImageProvider;

public class ElementLabelProvider extends LabelProvider {

    protected Transformer transformer;
    protected Module module;

    public ElementLabelProvider(Transformer transformer) {
        this.transformer = transformer;
    }

    public ElementLabelProvider(Module module) {
        this.module = module;
        this.transformer = new Transformer() {

            @Override
            public String transform(ElementIndexInfo info) {
                if (getModule().getName().equals(info.getModule())) {
                    return info.getName();
                } else {
                    return info.getModule() + " : " + info.getName();
                }
            }

        };
    }

    @Override
    public Image getImage(Object element) {
        if (element instanceof ElementIndexInfo) {
            ElementIndexInfo info = (ElementIndexInfo) element;
            if (ElementIndexType.GROUPING.equals(info.getType())) {
                return GraphitiUi.getImageService().getImageForId(YangDiagramImageProvider.DIAGRAM_TYPE_PROVIDER_ID,
                        YangDiagramImageProvider.IMG_GROUPING_PROPOSAL);
            }
            if (ElementIndexType.TYPE.equals(info.getType())) {
                return GraphitiUi.getImageService().getImageForId(YangDiagramImageProvider.DIAGRAM_TYPE_PROVIDER_ID,
                        YangDiagramImageProvider.IMG_CUSTOM_TYPE_PROPOSAL);
            }
            if (ElementIndexType.IDENTITY.equals(info.getType())) {
                return GraphitiUi.getImageService().getImageForId(YangDiagramImageProvider.DIAGRAM_TYPE_PROVIDER_ID,
                        YangDiagramImageProvider.IMG_IDENTITY_PROPOSAL);
            }
        } else {
            return GraphitiUi.getImageService().getImageForId(YangDiagramImageProvider.DIAGRAM_TYPE_PROVIDER_ID,
                    YangDiagramImageProvider.IMG_TYPE_PROPOSAL);
        }
        return super.getImage(element);
    }

    @Override
    public String getText(Object element) {
        if (element instanceof ElementIndexInfo) {
            return transformer.transform((ElementIndexInfo) element);
        }
        return Strings.getAsString(element);
    }

    private Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }
}
