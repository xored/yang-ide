package com.cisco.yangide.ext.model.editor.diagram;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.platform.ga.IGraphicsAlgorithmRendererFactory;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;

public class EditorDiagramTypeProvider extends AbstractDiagramTypeProvider {
    
    private IToolBehaviorProvider[] toolBehaviorProviders;

    public EditorDiagramTypeProvider() {
        super();
        setFeatureProvider(new EditorFeatureProvider(this));
    }

    @Override
    public IGraphicsAlgorithmRendererFactory getGraphicsAlgorithmRendererFactory() {
        return new YangDiagramGraphicsAlgorithmRendererFactory(this);
    }

    @Override
    public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
        if (null == toolBehaviorProviders) {
            toolBehaviorProviders = new IToolBehaviorProvider[] {new YangDiagramToolBehaviorProvider(this)};
        }
        return toolBehaviorProviders;
    }
    
    
}
