/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.model.editor.property;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

import com.cisco.yangide.ext.model.Node;

/**
 * @author Konstantin Zaitsev
 * @date Aug 28, 2014
 */
public abstract class YangPropertySection extends GFPropertySection {
    private EObject obj;
    private DataBindingContext bindingContext = new DataBindingContext();
    private Binding binding;

    @Override
    public void setInput(IWorkbenchPart part, ISelection selection) {
        super.setInput(part, selection);

        PictogramElement pe = getSelectedPictogramElement();
        if (pe != null) {
            Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
            if (bo == null || !isApplied(bo)) {
                removeBinding();
                return;
            }
            if (obj != ((Node) bo)) {
                removeBinding();
                obj = ((Node) bo);

                binding = createBinding(bindingContext, obj);
                binding.updateModelToTarget();
            }
        } else {
            removeBinding();
        }
    }

    @Override
    public void aboutToBeHidden() {
        obj = null;
        removeBinding();
    }

    private void removeBinding() {
        if (binding != null && !binding.isDisposed()) {
            binding.updateTargetToModel();
            bindingContext.removeBinding(binding);
            binding.dispose();
        }
    }

    /**
     * @return the obj
     */
    protected EObject getEObject() {
        return obj;
    }

    protected abstract Binding createBinding(DataBindingContext bindingContext, EObject obj);

    protected abstract boolean isApplied(Object bo);
}
