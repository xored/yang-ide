package com.cisco.yangide.ext.model.editor.diagram;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.tb.IContextButtonPadData;

import com.cisco.yangide.ext.model.editor.features.DirectEditingOnDoubleClickFeature;
import com.cisco.yangide.ext.model.editor.util.PropertyUtil;

public class YangDiagramToolBehaviorProvider extends DefaultToolBehaviorProvider {

    @Override
    public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) {
        if (PropertyUtil.isObjectShapeProp(context.getInnerPictogramElement(), PropertyUtil.EDITABLE_SHAPE)) {
            return new DirectEditingOnDoubleClickFeature(getFeatureProvider());
        }
        return super.getDoubleClickFeature(context);
    }

    public YangDiagramToolBehaviorProvider(IDiagramTypeProvider diagramTypeProvider) {
        super(diagramTypeProvider);
    }

    @Override
    public IContextButtonPadData getContextButtonPad(IPictogramElementContext context) {
		// remove button pads over diagram elements
        return null;
    }
    
    

}
