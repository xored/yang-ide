package com.cisco.yangide.editor.editors;

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.cisco.yangide.editor.YANGEditorPlugin;

public class YANGConfiguration extends SourceViewerConfiguration {
	private YANGDoubleClickStrategy doubleClickStrategy;
	private YANGTagScanner tagScanner;
	private YANGScanner scanner;
	private IColorManager colorManager;

    public YANGConfiguration() {
        super();
    }	
	
	public YANGConfiguration(IColorManager colorManager) {
		this.colorManager = colorManager;
	}
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			YANGPartitionScanner.YANG_COMMENT,
			YANGPartitionScanner.YANG_IDENTIFIER,
			YANGPartitionScanner.YANG_KEYWORD,
			YANGPartitionScanner.YANG_STRING };
	}
	public ITextDoubleClickStrategy getDoubleClickStrategy(
		ISourceViewer sourceViewer,
		String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new YANGDoubleClickStrategy();
		return doubleClickStrategy;
	}

	protected YANGScanner getYANGScanner() {
		if (scanner == null) {
			scanner = new YANGScanner(colorManager, YANGEditorPlugin.getDefault().getPreferenceStore());
			scanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(IYANGColorConstants.YANG_IDENTIFIER))));
		}
		return scanner;
	}
	protected YANGTagScanner getTagScanner() {
		if (tagScanner == null) {
			tagScanner = new YANGTagScanner(colorManager);
			tagScanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(IYANGColorConstants.YANG_KEYWORD))));
		}
		return tagScanner;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr =
			new DefaultDamagerRepairer(getTagScanner());
		reconciler.setDamager(dr, YANGPartitionScanner.YANG_KEYWORD);
		reconciler.setRepairer(dr, YANGPartitionScanner.YANG_KEYWORD);

		dr = new DefaultDamagerRepairer(getYANGScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		NonRuleBasedDamagerRepairer ndr =
			new NonRuleBasedDamagerRepairer(
				new TextAttribute(
					colorManager.getColor(IYANGColorConstants.YANG_COMMENT)));
		reconciler.setDamager(ndr, YANGPartitionScanner.YANG_COMMENT);
		reconciler.setRepairer(ndr, YANGPartitionScanner.YANG_COMMENT);

		return reconciler;
	}

}