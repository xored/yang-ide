package com.cisco.yangide.ext.model.editor.diagram;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.tb.IContextButtonPadData;

public class YangDiagramToolBehaviorProvider extends DefaultToolBehaviorProvider {

    public YangDiagramToolBehaviorProvider(IDiagramTypeProvider diagramTypeProvider) {
        super(diagramTypeProvider);
    }

    @Override
    public IContextButtonPadData getContextButtonPad(IPictogramElementContext context) {
        // remove button pads over diagram elements
        return null;
    }
    
    

}
