package com.cisco.yangide.editor.editors;

import org.eclipse.jdt.internal.ui.text.JavaColorManager;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

public class YANGEditor extends TextEditor {

	private IColorManager colorManager;

	public YANGEditor() {
		super();
		//colorManager = new ColorManager();
		colorManager = new JavaColorManager(false);
		setSourceViewerConfiguration(new YANGConfiguration(colorManager));
		setDocumentProvider(new YANGDocumentProvider());
	}
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}
	
	
	@Override
	protected void configureSourceViewerDecorationSupport(
			SourceViewerDecorationSupport support) {
		// TODO brackets higlihting
		super.configureSourceViewerDecorationSupport(support);
	}

}
