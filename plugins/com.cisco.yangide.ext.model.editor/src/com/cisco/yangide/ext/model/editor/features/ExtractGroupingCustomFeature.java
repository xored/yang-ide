/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.model.editor.features;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import com.cisco.yangide.ext.model.Module;
import com.cisco.yangide.ext.model.Node;
import com.cisco.yangide.ext.model.editor.editors.ISourceModelManager;

/**
 * @author Konstantin Zaitsev
 * @date Aug 19, 2014
 */
public class ExtractGroupingCustomFeature extends AbstractCustomFeature {

    private ISourceModelManager sourceModelManager;

    public ExtractGroupingCustomFeature(IFeatureProvider fp, ISourceModelManager sourceModelManager) {
        super(fp);
        this.sourceModelManager = sourceModelManager;
    }

    @Override
    public void execute(ICustomContext context) {
        List<Node> nodes = new ArrayList<>();
        PictogramElement[] elements = context.getPictogramElements();
        for (PictogramElement element : elements) {
            nodes.add((Node) getBusinessObjectForPictogramElement(element));
        }
        sourceModelManager.extractGrouping(nodes);
    }

    @Override
    public boolean canExecute(ICustomContext context) {
        PictogramElement[] elements = context.getPictogramElements();
        if (elements.length == 0) {
            return false;
        }
        Node parent = null;
        for (PictogramElement element : elements) {
            Object obj = getBusinessObjectForPictogramElement(element);
            if (!(obj instanceof Node) || obj instanceof Module) {
                return false;
            }
            if (parent != null && ((Node) obj).getParent() != parent) {
                return false;
            }
            parent = ((Node) obj).getParent();
        }
        return true;
    }

    @Override
    public String getName() {
        return "Extract Grouping...";
    }
}
