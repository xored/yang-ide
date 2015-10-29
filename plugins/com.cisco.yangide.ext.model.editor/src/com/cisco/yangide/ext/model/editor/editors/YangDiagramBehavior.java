/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.model.editor.editors;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.graphiti.ui.editor.DefaultPaletteBehavior;
import org.eclipse.graphiti.ui.editor.DefaultPersistencyBehavior;
import org.eclipse.graphiti.ui.editor.DefaultRefreshBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditorContextMenuProvider;
import org.eclipse.graphiti.ui.editor.IDiagramContainerUI;
import org.eclipse.graphiti.ui.internal.action.RemoveAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.graphics.Point;

@SuppressWarnings("restriction")
public class YangDiagramBehavior extends DiagramBehavior {
    private Point createPosition;

    public YangDiagramBehavior(IDiagramContainerUI diagramContainer) {
        super(diagramContainer);
    }

    @Override
    protected DefaultPersistencyBehavior createPersistencyBehavior() {
        return new YangDiagramPersistencyBehavior(this);
    }

    /**
     * @return the createPosition
     */
    public Point getCreatePosition() {
        return createPosition;
    }

    /**
     * @param createPosition the createPosition to set
     */
    public void setCreatePosition(Point createPosition) {
        this.createPosition = createPosition;
    }

    @Override
    protected DefaultRefreshBehavior createRefreshBehavior() {
        return new YangDiagramRefreshBehavior(this);
    }

    @Override
    protected ContextMenuProvider createContextMenuProvider() {
        return new DiagramEditorContextMenuProvider(getDiagramContainer().getGraphicalViewer(),
                getDiagramContainer().getActionRegistry(), getConfigurationProvider()) {
            @Override
            protected void addActionToMenuIfAvailable(IMenuManager manager, String actionId, String menuGroup) {
                if (RemoveAction.ACTION_ID.equals(actionId)) {
                    return;
                }
                super.addActionToMenuIfAvailable(manager, actionId, menuGroup);
            }
        };
    }

    @Override
    protected DefaultPaletteBehavior createPaletteBehaviour() {
        return new YangPaletteBehavior(this);
    }

    public YangPaletteBehavior getYangPaletteBehavior() {
        return (YangPaletteBehavior) getPaletteBehavior();
    }
}
