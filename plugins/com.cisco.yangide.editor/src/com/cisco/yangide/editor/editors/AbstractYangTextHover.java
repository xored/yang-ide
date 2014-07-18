package com.cisco.yangide.editor.editors;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.ui.IEditorPart;

public class AbstractYangTextHover implements ITextHover, ITextHoverExtension2{
	
	private IEditorPart fEditor;
	

	public void setEditor(IEditorPart editor) {
		fEditor= editor;
	}

	protected IEditorPart getEditor() {
		return fEditor;
	}

	@Override
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
		return null;
	}

	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		return null;
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return new Region(offset, 0); 
	}

}
