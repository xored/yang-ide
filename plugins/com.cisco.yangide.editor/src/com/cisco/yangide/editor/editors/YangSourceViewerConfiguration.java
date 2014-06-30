package com.cisco.yangide.editor.editors;

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.texteditor.ITextEditor;

import com.cisco.yangide.editor.YangEditorPlugin;

public class YangSourceViewerConfiguration extends SourceViewerConfiguration {
	private YangDoubleClickStrategy doubleClickStrategy;
	private YangStringScanner stringScanner;
	private YangCommentScanner commentScanner;
	private YangScanner scanner;
	private IColorManager colorManager;
	private IPreferenceStore preferencesStore = YangEditorPlugin.getDefault().getPreferenceStore();

    public YangSourceViewerConfiguration() {
        super();
    }	
	
	public YangSourceViewerConfiguration(IColorManager colorManager) {
		this.colorManager = colorManager;
		this.scanner = new YangScanner(colorManager, preferencesStore);
		this.stringScanner = new YangStringScanner(colorManager, preferencesStore);
		this.commentScanner = new YangCommentScanner(colorManager, preferencesStore);
	}
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			YangPartitionScanner.YANG_COMMENT,
			YangPartitionScanner.YANG_STRING };
	}
	public ITextDoubleClickStrategy getDoubleClickStrategy(
		ISourceViewer sourceViewer,
		String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new YangDoubleClickStrategy();
		return doubleClickStrategy;
	}

	protected YangScanner getYangScanner() {
		if (scanner == null) {
			scanner = new YangScanner(colorManager, preferencesStore);
		}
		return scanner;
	}
	protected YangStringScanner getYangStringScanner() {
		if (stringScanner == null) {
			stringScanner = new YangStringScanner(colorManager, preferencesStore);
			stringScanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(IYangColorConstants.YANG_STRING))));
		}
		return stringScanner;
	}
	

    /**
     * @return the commentScanner
     */
    public YangCommentScanner getYangCommentScanner() {
        if (commentScanner == null) {
            commentScanner = new YangCommentScanner(colorManager, preferencesStore);
            commentScanner.setDefaultReturnToken(
                new Token(
                    new TextAttribute(
                        colorManager.getColor(IYangColorConstants.YANG_COMMENT))));     
        }
        return commentScanner;
    }

	

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
	    
		PresentationReconciler reconciler = new PresentationReconciler();
		reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		
        DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getYangScanner());
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
        
		dr = new DefaultDamagerRepairer(getYangCommentScanner());
		reconciler.setDamager(dr, YangPartitionScanner.YANG_COMMENT);
		reconciler.setRepairer(dr, YangPartitionScanner.YANG_COMMENT);
		
		dr = new DefaultDamagerRepairer(getYangStringScanner());
        reconciler.setDamager(dr, YangPartitionScanner.YANG_STRING);
        reconciler.setRepairer(dr, YangPartitionScanner.YANG_STRING);

		return reconciler;
	}

    /**
     * Determines whether the preference change encoded by the given event
     * changes the behavior of one of its contained components.
     *
     * @param event the event to be investigated
     * @return <code>true</code> if event causes a behavioral change
     * @since 3.0
     */
    public boolean affectsTextPresentation(PropertyChangeEvent event) {
        return scanner.affectsBehavior(event) 
                || stringScanner.affectsBehavior(event)
                || commentScanner.affectsBehavior(event);

    }

    /**
     * Adapts the behavior of the contained components to the change
     * encoded in the given event.
     * <p>
     * Clients are not allowed to call this method if the old setup with
     * text tools is in use.
     * </p>
     *
     * @param event the event to which to adapt
     * @see JavaSourceViewerConfiguration#JavaSourceViewerConfiguration(IColorManager, IPreferenceStore, ITextEditor, String)
     * @since 3.0
     */
    public void handlePropertyChangeEvent(PropertyChangeEvent event) {
        //Assert.isTrue(isNewSetup());
        if (scanner.affectsBehavior(event))
            scanner.adaptToPreferenceChange(event);
        if (stringScanner.affectsBehavior(event))
            stringScanner.adaptToPreferenceChange(event);
        if (commentScanner.affectsBehavior(event))
            commentScanner.adaptToPreferenceChange(event);        
//        if (fJavaDoubleClickSelector != null && JavaCore.COMPILER_SOURCE.equals(event.getProperty()))
//            if (event.getNewValue() instanceof String)
//                fJavaDoubleClickSelector.setSourceVersion((String) event.getNewValue());
    }	
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
     */
    @Override
    public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
        String partitioning = getConfiguredDocumentPartitioning(sourceViewer);
        //TODO
        //if (YangPartitionScanner.YANG_COMMENT.equals(contentType))
        
        return new IAutoEditStrategy[] {new YangAutoIndentStrategy(partitioning, sourceViewer) }; //usefull  new SmartSemicolonAutoEditStrategy(partitioning), 


    }

	
}