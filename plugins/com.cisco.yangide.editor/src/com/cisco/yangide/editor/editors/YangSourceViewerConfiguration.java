package com.cisco.yangide.editor.editors;

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
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
    private YangStringScanner tagScanner;
    private YangScanner scanner;
    private IColorManager colorManager;

    public YangSourceViewerConfiguration() {
        super();
    }

    public YangSourceViewerConfiguration(IColorManager colorManager) {
        this.colorManager = colorManager;
        this.scanner = new YangScanner(colorManager, YangEditorPlugin.getDefault().getPreferenceStore());
    }

    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
        return new String[] { IDocument.DEFAULT_CONTENT_TYPE, YangPartitionScanner.YANG_COMMENT,
                YangPartitionScanner.YANG_STRING };
    }

    public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
        if (doubleClickStrategy == null)
            doubleClickStrategy = new YangDoubleClickStrategy();
        return doubleClickStrategy;
    }

    protected YangScanner getYangScanner() {
        if (scanner == null) {
            scanner = new YangScanner(colorManager, YangEditorPlugin.getDefault().getPreferenceStore());
            scanner.setDefaultReturnToken(new Token(new TextAttribute(colorManager
                    .getColor(IYangColorConstants.YANG_IDENTIFIER))));
        }
        return scanner;
    }

    protected YangStringScanner getYangStringScanner() {
        if (tagScanner == null) {
            tagScanner = new YangStringScanner(colorManager);
            tagScanner.setDefaultReturnToken(new Token(new TextAttribute(colorManager
                    .getColor(IYangColorConstants.YANG_STRING))));
        }
        return tagScanner;
    }

    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
        PresentationReconciler reconciler = new PresentationReconciler();
        reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

        DefaultDamagerRepairer dr = new DefaultDamagerRepairer(scanner);
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

        dr = new DefaultDamagerRepairer(getYangScanner());
        reconciler.setDamager(dr, YangPartitionScanner.YANG_COMMENT);
        reconciler.setRepairer(dr, YangPartitionScanner.YANG_COMMENT);

        // dr = new DefaultDamagerRepairer(new SingleTokenJavaScanner(colorManager,
        // YangEditorPlugin.getDefault().getPreferenceStore(), IYangColorConstants.YANG_STRING));
        dr = new DefaultDamagerRepairer(getYangStringScanner());
        reconciler.setDamager(dr, YangPartitionScanner.YANG_STRING);
        reconciler.setRepairer(dr, YangPartitionScanner.YANG_STRING);

        // NonRuleBasedDamagerRepairer ndr =
        // new NonRuleBasedDamagerRepairer(
        // new TextAttribute(
        // colorManager.getColor(IYangColorConstants.YANG_COMMENT)));
        // reconciler.setDamager(ndr, YangPartitionScanner.YANG_COMMENT);
        // reconciler.setRepairer(ndr, YangPartitionScanner.YANG_COMMENT);

        return reconciler;
    }

    /**
     * Determines whether the preference change encoded by the given event changes the behavior of
     * one of its contained components.
     *
     * @param event the event to be investigated
     * @return <code>true</code> if event causes a behavioral change
     * @since 3.0
     */
    public boolean affectsTextPresentation(PropertyChangeEvent event) {
        return scanner.affectsBehavior(event);
    }

    /**
     * Adapts the behavior of the contained components to the change encoded in the given event.
     * <p>
     * Clients are not allowed to call this method if the old setup with text tools is in use.
     * </p>
     *
     * @param event the event to which to adapt
     * @see JavaSourceViewerConfiguration#JavaSourceViewerConfiguration(IColorManager,
     * IPreferenceStore, ITextEditor, String)
     * @since 3.0
     */
    public void handlePropertyChangeEvent(PropertyChangeEvent event) {
        // Assert.isTrue(isNewSetup());
        if (scanner.affectsBehavior(event))
            scanner.adaptToPreferenceChange(event);
        // if (fJavaDoubleClickSelector != null &&
        // JavaCore.COMPILER_SOURCE.equals(event.getProperty()))
        // if (event.getNewValue() instanceof String)
        // fJavaDoubleClickSelector.setSourceVersion((String) event.getNewValue());
    }

}