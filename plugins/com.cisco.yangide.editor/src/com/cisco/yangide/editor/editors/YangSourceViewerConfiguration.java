package com.cisco.yangide.editor.editors;


import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ITextEditor;

import com.cisco.yangide.editor.YangEditorPlugin;
import com.cisco.yangide.editor.preferences.YangDocumentSetupParticipant;

public class YangSourceViewerConfiguration extends SourceViewerConfiguration {
	private YangDoubleClickStrategy doubleClickStrategy;
	private YangStringScanner stringScanner;
	private YangCommentScanner commentScanner;
	private YangScanner scanner;
	private IColorManager colorManager;
	private IPreferenceStore preferencesStore = YangEditorPlugin.getDefault().getCombinedPreferenceStore();

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
		if (stringScanner == null) 
			stringScanner = new YangStringScanner(colorManager, preferencesStore);
		return stringScanner;
	}
	

    /**
     * @return the commentScanner
     */
    public YangCommentScanner getYangCommentScanner() {
        if (commentScanner == null)
            commentScanner = new YangCommentScanner(colorManager, preferencesStore);
        return commentScanner;
    }

	

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
	    
	    /**
	     * for semantic higlighting @see org.eclipse.jdt.internal.ui.text.JavaPresentationReconciler
	     */
		PresentationReconciler reconciler = new PresentationReconciler();
		reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		
        DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getYangScanner());
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
        
        /**
         *  may need to be used @see org.eclipse.ant.internal.ui.editor.text.MultilineDamagerRepairer
         */
		dr = new DefaultDamagerRepairer(getYangCommentScanner());
		reconciler.setDamager(dr, YangPartitionScanner.YANG_COMMENT);
		reconciler.setRepairer(dr, YangPartitionScanner.YANG_COMMENT);
		
		dr = new DefaultDamagerRepairer(getYangStringScanner());
        reconciler.setDamager(dr, YangPartitionScanner.YANG_STRING);
        reconciler.setRepairer(dr, YangPartitionScanner.YANG_STRING);

		return reconciler;
	}

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getConfiguredDocumentPartitioning(org.eclipse.jface.text.source.ISourceViewer)
     */
    public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
        return YangDocumentSetupParticipant.YANG_PARTITIONING;
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

    /*
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getTabWidth(org.eclipse.jface.text.source.ISourceViewer)
     */
    public int getTabWidth(ISourceViewer sourceViewer) {
        if (preferencesStore == null)
            return super.getTabWidth(sourceViewer);
        return preferencesStore.getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
    }
 
    /* (non-Javadoc)
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getContentAssistant(org.eclipse.jface.text.source.ISourceViewer)
     */
    @Override
    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {

        ContentAssistant assistant = new ContentAssistant();
        IContentAssistProcessor processor = new YangSimpleCompletionProcessor();
        assistant.setContentAssistProcessor(processor, IDocument.DEFAULT_CONTENT_TYPE);
        
        assistant.setDocumentPartitioning(YangDocumentSetupParticipant.YANG_PARTITIONING);

        

        assistant.enableAutoActivation(false);
        assistant.setAutoActivationDelay(500);
        assistant.setProposalPopupOrientation(ContentAssistant.PROPOSAL_REMOVE);
        assistant.setContextInformationPopupOrientation(ContentAssistant.CONTEXT_INFO_ABOVE);

        Color background = JFaceResources.getColorRegistry().get(JFacePreferences.CONTENT_ASSIST_BACKGROUND_COLOR);
        assistant.setContextInformationPopupBackground(background);
        assistant.setContextSelectorBackground(background);

        Color foreground= JFaceResources.getColorRegistry().get(JFacePreferences.CONTENT_ASSIST_FOREGROUND_COLOR);
        assistant.setContextInformationPopupForeground(foreground);
        assistant.setContextSelectorForeground(foreground);

        //assistant.setRepeatedInvocationMode(true);
        assistant.setStatusLineVisible(true);
        //assistant.setShowEmptyList(true);
        assistant.enableAutoInsert(true);

        return assistant;
    }

    @Override
    public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
        IHyperlinkDetector[] detectors = super.getHyperlinkDetectors(sourceViewer);
        IHyperlinkDetector[] result = new IHyperlinkDetector[detectors.length + 1];
        System.arraycopy(detectors, 0, result, 0, detectors.length);
        result[detectors.length] = new YangElementHyperlinkDetector();
        return result;
    }
}