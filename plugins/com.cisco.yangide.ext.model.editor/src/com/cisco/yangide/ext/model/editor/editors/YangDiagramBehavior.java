package com.cisco.yangide.ext.model.editor.editors;

import org.eclipse.graphiti.ui.editor.DefaultPersistencyBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.IDiagramContainerUI;

public class YangDiagramBehavior extends DiagramBehavior {
    public YangDiagramBehavior(IDiagramContainerUI diagramContainer) {
        super(diagramContainer);
    }

    @Override
    protected DefaultPersistencyBehavior createPersistencyBehavior() {
        return new YangDiagramPersistencyBehavior(this);
    }  
    
}
