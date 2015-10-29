/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.model.editor.util;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

import com.cisco.yangide.ext.model.ContainingNode;
import com.cisco.yangide.ext.model.ModelFactory;
import com.cisco.yangide.ext.model.Module;
import com.cisco.yangide.ext.model.Node;

public class DiagramImportSupport {
    public static void importDiagram(Diagram diagram, IFeatureProvider fp) {
        EObject obj = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(diagram);
        if (null == obj) {
            obj = ModelFactory.eINSTANCE.createModule();
            fp.link(diagram, obj);
        }
        if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getModule(), obj)) {
            drawShapes(((Module) obj).getChildren(), diagram, fp);
            drawLines(((Module) obj).getChildren(), fp);
            // YangModelUIUtil.layoutPictogramElement(diagram, fp);
        }
    }

    public static void drawShapes(List<Node> list, ContainerShape cs, IFeatureProvider fp) {
        for (Node n : list) {
            int pos = cs.getGraphicsAlgorithm().getHeight();
            if (cs instanceof Diagram) {
                pos = 0;
            }
            PictogramElement pe = YangModelUIUtil.drawObject(n, cs, fp, 0, pos);
            if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getContainingNode(), n) && null != pe
                    && pe instanceof ContainerShape) {
                drawShapes(((ContainingNode) n).getChildren(), (ContainerShape) pe, fp);
            }
        }
    }

    public static void drawLines(List<Node> list, IFeatureProvider fp) {
        for (Node n : list) {
            YangModelUIUtil.updateConnections(n, fp);
            if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getContainingNode(), n)) {
                drawLines(((ContainingNode) n).getChildren(), fp);
            }
        }
    }

}
