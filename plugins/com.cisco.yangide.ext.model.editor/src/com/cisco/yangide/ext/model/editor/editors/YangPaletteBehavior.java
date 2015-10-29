/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.model.editor.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.Tool;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.internal.ui.palette.editparts.DetailedLabelFigure;
import org.eclipse.gef.tools.ConnectionCreationTool;
import org.eclipse.gef.tools.CreationTool;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DefaultPaletteBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.cisco.yangide.ext.model.ModelPackage;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

/**
 * @author Konstantin Zaitsev
 * @date Aug 29, 2014
 */
@SuppressWarnings("restriction")
public class YangPaletteBehavior extends DefaultPaletteBehavior {
    private Map<String, EClass> toolEClassMap;
    private IFigure rootFigure;

    public YangPaletteBehavior(DiagramBehavior diagramBehavior) {
        super(diagramBehavior);
        toolEClassMap = new HashMap<>();
        toolEClassMap.put("grouping", ModelPackage.Literals.GROUPING);
        toolEClassMap.put("container", ModelPackage.Literals.CONTAINER);
        toolEClassMap.put("rpc", ModelPackage.Literals.RPC);
        toolEClassMap.put("input", ModelPackage.Literals.RPC_IO);
        toolEClassMap.put("output", ModelPackage.Literals.RPC_IO);
        toolEClassMap.put("notification", ModelPackage.Literals.NOTIFICATION);
        toolEClassMap.put("augment", ModelPackage.Literals.AUGMENT);
        toolEClassMap.put("list", ModelPackage.Literals.LIST);
        toolEClassMap.put("choice", ModelPackage.Literals.CHOICE);
        toolEClassMap.put("case", ModelPackage.Literals.CHOICE_CASE);
        toolEClassMap.put("leaf", ModelPackage.Literals.LEAF);
        toolEClassMap.put("anyxml", ModelPackage.Literals.ANYXML);
        toolEClassMap.put("uses", ModelPackage.Literals.USES);
        toolEClassMap.put("deviation", ModelPackage.Literals.DEVIATION);
        toolEClassMap.put("extension", ModelPackage.Literals.EXTENSION);
        toolEClassMap.put("feature", ModelPackage.Literals.FEATURE);
        toolEClassMap.put("identity", ModelPackage.Literals.IDENTITY);
        toolEClassMap.put("leaf list", ModelPackage.Literals.LEAF_LIST);
        toolEClassMap.put("typedef", ModelPackage.Literals.TYPEDEF);
    }

    @Override
    protected PaletteViewerProvider createPaletteViewerProvider() {
        return new PaletteViewerProvider(diagramBehavior.getEditDomain()) {
            private KeyHandler paletteKeyHandler = null;

            @Override
            public PaletteViewer createPaletteViewer(Composite parent) {
                PaletteViewer pViewer = new PaletteViewer() {
                    @Override
                    protected void setRootFigure(IFigure figure) {
                        super.setRootFigure(figure);
                        rootFigure = figure;
                    }
                };
                pViewer.createControl(parent);
                configurePaletteViewer(pViewer);
                hookPaletteViewer(pViewer);
                return pViewer;
            }

            @Override
            protected void configurePaletteViewer(PaletteViewer viewer) {
                super.configurePaletteViewer(viewer);
                viewer.getKeyHandler().setParent(getPaletteKeyHandler());
                viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
            }

            /**
             * @return Palette Key Handler for the palette
             */
            private KeyHandler getPaletteKeyHandler() {
                if (paletteKeyHandler == null) {
                    paletteKeyHandler = new KeyHandler() {
                        /**
                         * Processes a <i>key released </i> event. This method is called by the Tool
                         * whenever a key is released, and the Tool is in the proper state.
                         * Overridden to support pressing the enter key to create a shape or
                         * connection (between two selected shapes)
                         *
                         * @param event the KeyEvent
                         * @return <code>true</code> if KeyEvent was handled in some way
                         */
                        @Override
                        public boolean keyReleased(KeyEvent event) {
                            if (event.keyCode == SWT.Selection) {
                                Tool tool = getEditDomain().getPaletteViewer().getActiveTool().createTool();
                                if (tool instanceof CreationTool || tool instanceof ConnectionCreationTool) {
                                    tool.keyUp(event, diagramBehavior.getDiagramContainer().getGraphicalViewer());
                                    // Deactivate current selection
                                    getEditDomain().getPaletteViewer().setActiveTool(null);
                                    return true;
                                }
                            }
                            return super.keyReleased(event);
                        }
                    };
                }
                return paletteKeyHandler;
            }
        };
    }

    public void updateSelection(ISelection selection) {
        EClass container = null;
        if (selection != null && selection instanceof StructuredSelection
                && ((StructuredSelection) selection).size() == 1) {
            EditPart editPart = (EditPart) ((StructuredSelection) selection).getFirstElement();
            if (editPart.getModel() != null && editPart.getModel() instanceof PictogramElement) {
                PictogramElement model = (PictogramElement) editPart.getModel();
                EObject eObject = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(model);
                if (eObject != null) {
                    container = eObject.eClass();
                }
            }
        }

        ArrayList<DetailedLabelFigure> list = new ArrayList<>();
        aggregateToolFigures(rootFigure, list);
        for (DetailedLabelFigure figure : list) {
            if (container == null || getToolEClass(figure) == null) {
                figure.setForegroundColor(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
            } else {
                if (YangModelUtil.compositeNodeMap.containsKey(container)
                        && YangModelUtil.compositeNodeMap.get(container).contains(getToolEClass(figure))) {
                    figure.setForegroundColor(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
                } else {
                    figure.setForegroundColor(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
                }
            }
            figure.repaint();
        }
    }

    private void aggregateToolFigures(IFigure parent, List<DetailedLabelFigure> list) {
        if (parent instanceof DetailedLabelFigure) {
            DetailedLabelFigure fig = (DetailedLabelFigure) parent;
            if (fig.getChildren().size() == 2 && fig.getChildren().get(1) instanceof FlowPage) {
                list.add(fig);
            }
            return;
        }
        if (parent.getChildren().size() > 0) {
            for (Object obj : parent.getChildren()) {
                aggregateToolFigures((IFigure) obj, list);
            }
        }
    }

    private EClass getToolEClass(DetailedLabelFigure figure) {
        TextFlow textFlow = ((TextFlow) ((FlowPage) figure.getChildren().get(1)).getChildren().get(0));
        String text = textFlow.getText();
        return toolEClassMap.get(text);
    }
}
