package com.cisco.yangide.editor.editors;

import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.IInformationProviderExtension;
import org.eclipse.jface.text.information.IInformationProviderExtension2;
import org.eclipse.ui.IEditorPart;

public class YangInformationProvider  implements IInformationProvider, IInformationProviderExtension, IInformationProviderExtension2{
	protected YangTextHover fImplementation;

	public YangInformationProvider(IEditorPart editor) {
		if (editor != null) {
			fImplementation= new YangTextHover();
			fImplementation.setEditor(editor);
		}
	}

	@Override
	public IInformationControlCreator getInformationPresenterControlCreator() {
	if (fImplementation == null)
			return null;
		return fImplementation.getInformationPresenterControlCreator();
	}

	@Override
	public Object getInformation2(ITextViewer textViewer, IRegion subject) {
		if (fImplementation == null)
			return null;
		return fImplementation.getHoverInfo2(textViewer, subject);
	}

	@Override
	public IRegion getSubject(ITextViewer textViewer, int offset) {
		return new Region(0, offset);
	}

	@Override
	public String getInformation(ITextViewer textViewer, IRegion subject) {
		if (fImplementation != null) {
			String s= fImplementation.getHoverInfo(textViewer, subject);
			if (s != null && s.trim().length() > 0) {
				return s;
			}
		}
		return null;
	}

}
