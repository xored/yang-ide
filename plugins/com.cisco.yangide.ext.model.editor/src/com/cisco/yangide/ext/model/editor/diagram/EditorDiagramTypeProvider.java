package com.cisco.yangide.ext.model.editor.diagram;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.platform.ga.IGraphicsAlgorithmRendererFactory;

public class EditorDiagramTypeProvider extends AbstractDiagramTypeProvider {

    public EditorDiagramTypeProvider() {
        super();
        setFeatureProvider(new EditorFeatureProvider(this));
    }

    @Override
    public IGraphicsAlgorithmRendererFactory getGraphicsAlgorithmRendererFactory() {
        return new YangDiagramGraphicsAlgorithmRendererFactory(this);
    }
}
