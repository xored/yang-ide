package com.cisco.yangide.ext.model.editor.diagram;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;

public class EditorDiagramTypeProvider extends AbstractDiagramTypeProvider {

	public EditorDiagramTypeProvider() {
		super();
		setFeatureProvider(new EditorFeatureProvider(this));
	}
	
}
