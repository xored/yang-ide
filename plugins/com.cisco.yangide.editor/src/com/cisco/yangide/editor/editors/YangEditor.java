package com.cisco.yangide.editor.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
//import org.eclipse.jdt.internal.ui.javaeditor.JavaSourceViewer;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.source.DefaultCharacterPairMatcher;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension2;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.ITextEditorHelpContextIds;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

import com.cisco.yangide.editor.YangEditorPlugin;

public class YangEditor extends TextEditor {

    // TODO extract to another class
    public final static String EDITOR_MATCHING_BRACKETS = "matchingBrackets";

    private IColorManager colorManager;

    public YangEditor() {
        super();
	
        // colorManager = new YangColorManager();
        colorManager = new YangColorManager(false);
        setSourceViewerConfiguration(new YangSourceViewerConfiguration(colorManager));
        setDocumentProvider(new YangDocumentProvider());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.editors.text.TextEditor#initializeEditor() Called from TextEditor.<init>
     */
    protected void initializeEditor() {
        setCompatibilityMode(false);

        setEditorContextMenuId("#TextEditorContext"); //$NON-NLS-1$
        setRulerContextMenuId("#TextRulerContext"); //$NON-NLS-1$
        setHelpContextId(ITextEditorHelpContextIds.TEXT_EDITOR);
        // XXX legal everywhere else is false, causes to instantiate new smart mode
        configureInsertMode(SMART_INSERT, true);
        setInsertMode(INSERT);

        setPreferenceStore(YangEditorPlugin.getDefault().getCombinedPreferenceStore());
    }

    public void dispose() {
        colorManager.dispose();
        super.dispose();
    }

    /*
     * @see AbstractTextEditor#doSetInput
     */
    @Override
    protected void doSetInput(IEditorInput input) throws CoreException {
        ISourceViewer sourceViewer = getSourceViewer();
        if (!(sourceViewer instanceof ISourceViewerExtension2)) {
            setPreferenceStore(createCombinedPreferenceStore(input));
            internalDoSetInput(input);
            return;
        }

        // uninstall & unregister preference store listener
        getSourceViewerDecorationSupport(sourceViewer).uninstall();
        ((ISourceViewerExtension2) sourceViewer).unconfigure();

        setPreferenceStore(createCombinedPreferenceStore(input));

        // install & register preference store listener
        sourceViewer.configure(getSourceViewerConfiguration());
        getSourceViewerDecorationSupport(sourceViewer).install(getPreferenceStore());

        internalDoSetInput(input);
    }

    /*
     * //TODO
     * 
     * @see JavaEditor#internalDoSetInput
     */
    private void internalDoSetInput(IEditorInput input) throws CoreException {

        super.doSetInput(input);

        // ISourceViewer sourceViewer= getSourceViewer();
        // JavaSourceViewer javaSourceViewer= null;
        // if (sourceViewer instanceof JavaSourceViewer)
        // javaSourceViewer= (JavaSourceViewer)sourceViewer;
        //
        // IPreferenceStore store= getPreferenceStore();

        // if (javaSourceViewer != null && javaSourceViewer.getReconciler() == null) {
        // IReconciler reconciler= getSourceViewerConfiguration().getReconciler(javaSourceViewer);
        // if (reconciler != null) {
        // reconciler.install(javaSourceViewer);
        // javaSourceViewer.setReconciler(reconciler);
        // }
        // }

        if (fEncodingSupport != null)
            fEncodingSupport.reset();

        // setOutlinePageInput(fOutlinePage, input);
        //
        // if (isShowingOverrideIndicators())
        // installOverrideIndicator(false);
    }

    /**
     * Creates and returns the preference store for this Java editor with the given input.
     * 
     * @param input The editor input for which to create the preference store
     * @return the preference store for this editor
     * @since 3.0
     */
    private IPreferenceStore createCombinedPreferenceStore(IEditorInput input) {
        List<IPreferenceStore> stores = new ArrayList<IPreferenceStore>(3);

        stores.add(YangEditorPlugin.getDefault().getPreferenceStore());
        stores.add(EditorsUI.getPreferenceStore());
        stores.add(PlatformUI.getPreferenceStore());

        return new ChainedPreferenceStore(stores.toArray(new IPreferenceStore[stores.size()]));
    }

    @Override
    protected void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support) {
        // Enable bracket highlighting in the preference store

        IPreferenceStore store = YangEditorPlugin.getDefault().getPreferenceStore();
        store.setDefault(EDITOR_MATCHING_BRACKETS, true);

        char[] matchChars = { '{', '}', '(', ')', '[', ']' }; // which brackets to match
        ICharacterPairMatcher matcher = new DefaultCharacterPairMatcher(matchChars,
                IDocumentExtension3.DEFAULT_PARTITIONING);
        support.setCharacterPairMatcher(matcher);
        support.setMatchingCharacterPainterPreferenceKeys(EDITOR_MATCHING_BRACKETS,
                IYangColorConstants.EDITOR_MATCHING_BRACKETS_COLOR);

        super.configureSourceViewerDecorationSupport(support);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.editors.text.TextEditor#handlePreferenceStoreChanged(org.eclipse.jface.util
     * .PropertyChangeEvent)
     */
    @Override
    protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {

        ((YangSourceViewerConfiguration) getSourceViewerConfiguration()).handlePropertyChangeEvent(event);
        getSourceViewer().invalidateTextPresentation();

        super.handlePreferenceStoreChanged(event);

    }
}
