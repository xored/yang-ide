package com.cisco.yangide.ext.model.editor.editors;

import org.eclipse.graphiti.ui.editor.DefaultPersistencyBehavior;
import org.eclipse.graphiti.ui.editor.DefaultRefreshBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.IDiagramContainerUI;
import org.eclipse.swt.graphics.Point;

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
}
