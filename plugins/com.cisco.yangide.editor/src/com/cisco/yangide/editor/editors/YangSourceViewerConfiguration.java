/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors;

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ITextEditor;

import com.cisco.yangide.editor.editors.text.CompositeReconcilingStrategy;
import com.cisco.yangide.editor.editors.text.YangFormattingStrategy;
import com.cisco.yangide.editor.editors.text.YangReconcilingStrategy;
import com.cisco.yangide.editor.preferences.YangDocumentSetupParticipant;

/**
 * @author Alexey Kholupko
 */
public class YangSourceViewerConfiguration extends TextSourceViewerConfiguration {
    private YangDoubleClickStrategy doubleClickStrategy;
    private YangStringScanner stringScanner;
    private YangCommentScanner commentScanner;
    private YangScanner scanner;
    private IColorManager colorManager;
    private IPreferenceStore preferencesStore;
    private ITextEditor editor;

    public YangSourceViewerConfiguration(IPreferenceStore preferencesStore, IColorManager colorManager,
            ITextEditor editor) {
        super(preferencesStore);
        this.preferencesStore = preferencesStore;
        this.colorManager = colorManager;
        this.editor = editor;
        this.scanner = new YangScanner(colorManager, preferencesStore);
        this.stringScanner = new YangStringScanner(colorManager, preferencesStore);
        this.commentScanner = new YangCommentScanner(colorManager, preferencesStore);
    }

    @Override
    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
        return new String[] { IDocument.DEFAULT_CONTENT_TYPE, YangPartitionScanner.YANG_COMMENT,
                YangPartitionScanner.YANG_STRING, YangPartitionScanner.YANG_STRING_SQ };
    }

    @Override
    public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
        if (doubleClickStrategy == null) {
            doubleClickStrategy = new YangDoubleClickStrategy();
        }
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
        }
        return stringScanner;
    }

    /**
     * @return the commentScanner
     */
    public YangCommentScanner getYangCommentScanner() {
        if (commentScanner == null) {
            commentScanner = new YangCommentScanner(colorManager, preferencesStore);
        }
        return commentScanner;
    }

    @Override
    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {

        /**
         * for semantic higlighting @see org.eclipse.jdt.internal.ui.text.JavaPresentationReconciler
         */
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

        dr = new DefaultDamagerRepairer(getYangStringScanner());
        reconciler.setDamager(dr, YangPartitionScanner.YANG_STRING_SQ);
        reconciler.setRepairer(dr, YangPartitionScanner.YANG_STRING_SQ);

        return reconciler;
    }

    @Override
    public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
        return YangDocumentSetupParticipant.YANG_PARTITIONING;
    }

    /**
     * Determines whether the preference change encoded by the given event changes the behavior of
     * one of its contained components.
     */
    public boolean affectsTextPresentation(PropertyChangeEvent event) {
        return scanner.affectsBehavior(event) || stringScanner.affectsBehavior(event)
                || commentScanner.affectsBehavior(event);

    }

    /**
     * Adapts the behavior of the contained components to the change encoded in the given event.
     */
    public void handlePropertyChangeEvent(PropertyChangeEvent event) {

        if (scanner.affectsBehavior(event)) {
            scanner.adaptToPreferenceChange(event);
        }
        if (stringScanner.affectsBehavior(event)) {
            stringScanner.adaptToPreferenceChange(event);
        }
        if (commentScanner.affectsBehavior(event)) {
            commentScanner.adaptToPreferenceChange(event);

        }
    }

    @Override
    public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
        String partitioning = getConfiguredDocumentPartitioning(sourceViewer);
        return new IAutoEditStrategy[] { new YangAutoIndentStrategy(partitioning, sourceViewer) }; // usefull
    }

    @Override
    public int getTabWidth(ISourceViewer sourceViewer) {
        if (preferencesStore == null) {
            return super.getTabWidth(sourceViewer);
        }
        return preferencesStore.getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
    }

    @Override
    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {

        ContentAssistant assistant = new ContentAssistant();
        IContentAssistProcessor processor = new YangSimpleCompletionProcessor();
        assistant.setContentAssistProcessor(processor, IDocument.DEFAULT_CONTENT_TYPE);

        assistant.setDocumentPartitioning(YangDocumentSetupParticipant.YANG_PARTITIONING);

        assistant.enableAutoActivation(false);
        assistant.setAutoActivationDelay(200);
        assistant.setProposalPopupOrientation(ContentAssistant.PROPOSAL_REMOVE);
        assistant.setContextInformationPopupOrientation(ContentAssistant.CONTEXT_INFO_ABOVE);

        Color background = JFaceResources.getColorRegistry().get(JFacePreferences.CONTENT_ASSIST_BACKGROUND_COLOR);
        assistant.setContextInformationPopupBackground(background);
        assistant.setContextSelectorBackground(background);

        Color foreground = JFaceResources.getColorRegistry().get(JFacePreferences.CONTENT_ASSIST_FOREGROUND_COLOR);
        assistant.setContextInformationPopupForeground(foreground);
        assistant.setContextSelectorForeground(foreground);

        assistant.setStatusLineVisible(true);

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

    @Override
    public String[] getDefaultPrefixes(ISourceViewer sourceViewer, String contentType) {
        return new String[] { "//", "" }; //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    public IReconciler getReconciler(ISourceViewer sourceViewer) {
        final ITextEditor editor = getEditor();
        if (editor != null && editor.isEditable()) {
            CompositeReconcilingStrategy strategy = new CompositeReconcilingStrategy();
            strategy.setReconcilingStrategies(new IReconcilingStrategy[] {
            // yang syntax reconcile
            new YangReconcilingStrategy(sourceViewer, getEditor()) });
            MonoReconciler reconciler = new MonoReconciler(strategy, false);
            reconciler.setIsAllowedToModifyDocument(false);
            reconciler.setDelay(500);

            return reconciler;
        }
        return null;
    }

    @Override
    public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
        final MultiPassContentFormatter formatter = new MultiPassContentFormatter(
                getConfiguredDocumentPartitioning(sourceViewer), IDocument.DEFAULT_CONTENT_TYPE);
        formatter.setMasterStrategy(new YangFormattingStrategy());
        return formatter;
    }

    /**
     * @return the editor
     */
    protected ITextEditor getEditor() {
        return editor;
    }
}
