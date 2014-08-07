package com.cisco.yangide.ext.model.editor.editors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DefaultPersistencyBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;

public class YangDiagramPersistencyBehavior extends DefaultPersistencyBehavior {
    
    private String DIAGRAM_TYPE_ID = "yang";

    public YangDiagramPersistencyBehavior(DiagramBehavior diagramBehavior) {
        super(diagramBehavior);
    }

    @Override
    public Diagram loadDiagram(URI uri) {
        Diagram diagram = super.loadDiagram(uri);
        if (null == diagram) {
            diagram = Graphiti.getPeCreateService().createDiagram(DIAGRAM_TYPE_ID, uri.lastSegment(), false);      
        }
        return diagram;
    }    
    
    
}
