/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.source.DefaultCharacterPairMatcher;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension2;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.IProjectionListener;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.ITextEditorHelpContextIds;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.model.YangModelManager;
import com.cisco.yangide.core.parser.YangParserUtil;
import com.cisco.yangide.editor.YangEditorPlugin;
import com.cisco.yangide.editor.actions.AddBlockCommentAction;
import com.cisco.yangide.editor.actions.IYangEditorActionDefinitionIds;
import com.cisco.yangide.editor.actions.RemoveBlockCommentAction;
import com.cisco.yangide.editor.actions.ToggleCommentAction;
import com.cisco.yangide.editor.editors.text.YangFoldingStructureProvider;
import com.cisco.yangide.ui.YangUIPlugin;
import com.cisco.yangide.ui.preferences.IYangColorConstants;

/**
 * Editor class
 *
 * @author Alexey Kholupko
 */
public class YangEditor extends TextEditor implements IProjectionListener {

    // TODO extract logic to separate classes
    public final static String EDITOR_MATCHING_BRACKETS = "matchingBrackets";

    private IColorManager colorManager;

    private ProjectionSupport projectionSupport;

    private YangFoldingStructureProvider fFoldingStructureProvider;

    public YangEditor() {
        super();

        colorManager = new YangColorManager(false);
        setSourceViewerConfiguration(new YangSourceViewerConfiguration(YangEditorPlugin.getDefault()
                .getCombinedPreferenceStore(), colorManager, this));
        setDocumentProvider(new YangDocumentProvider());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.editors.text.TextEditor#initializeEditor() Called from TextEditor.<init>
     */
    @Override
    protected void initializeEditor() {
        setCompatibilityMode(false);

        setEditorContextMenuId("#TextEditorContext"); //$NON-NLS-1$
        setRulerContextMenuId("#TextRulerContext"); //$NON-NLS-1$
        setHelpContextId(ITextEditorHelpContextIds.TEXT_EDITOR);
        // XXX everywhere else is false, causes to instantiate new smart mode
        configureInsertMode(SMART_INSERT, true);
        setInsertMode(INSERT);

        setPreferenceStore(YangEditorPlugin.getDefault().getCombinedPreferenceStore());
    }

    @Override
    public void dispose() {
        colorManager.dispose();
        super.dispose();
        IEditorInput input = getEditorInput();
        // revert index to file content instead of editor
        if (input != null && input instanceof IFileEditorInput) {
            IFile file = ((IFileEditorInput) input).getFile();
            if (file != null) {
                try {
                    YangModelManager.getYangModelManager().removeInfoAndChildren(YangCorePlugin.createYangFile(file));
                    YangModelManager.getIndexManager().addWorkingCopy(file);
                } catch (YangModelException e) {
                    // ignore exception
                }
            }
        }
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

    private void internalDoSetInput(IEditorInput input) throws CoreException {

        super.doSetInput(input);

        if (fEncodingSupport != null) {
            fEncodingSupport.reset();
        }
    }

    /**
     * Creates and returns the preference store for this YANG editor with the given input.
     */
    private IPreferenceStore createCombinedPreferenceStore(IEditorInput input) {
        List<IPreferenceStore> stores = new ArrayList<IPreferenceStore>(3);

        stores.add(YangUIPlugin.getDefault().getPreferenceStore());
        stores.add(EditorsUI.getPreferenceStore());
        stores.add(PlatformUI.getPreferenceStore());

        return new ChainedPreferenceStore(stores.toArray(new IPreferenceStore[stores.size()]));
    }

    @Override
    protected void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support) {
        // Enable bracket highlighting in the preference store

        IPreferenceStore store = YangUIPlugin.getDefault().getPreferenceStore();
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

    @Override
    protected void initializeKeyBindingScopes() {
        setKeyBindingScopes(new String[] { "com.cisco.yangide.ui.Context" }); //$NON-NLS-1$
    }

    @Override
    public ISelectionProvider getSelectionProvider() {
        return getSourceViewer().getSelectionProvider();
    }

    public IDocument getDocument() {
        return getSourceViewer().getDocument();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.editors.text.TextEditor#createActions()
     */
    @Override
    protected void createActions() {

        super.createActions();

        IAction action = getAction(ITextEditorActionConstants.CONTENT_ASSIST_CONTEXT_INFORMATION);

        action = new ToggleCommentAction(ResourceBundle.getBundle(YangEditorMessages.getBundleName()),
                "ToggleComment.", this); //$NON-NLS-1$

        action.setActionDefinitionId(IYangEditorActionDefinitionIds.TOGGLE_COMMENT);
        setAction("ToggleComment", action); //$NON-NLS-1$
        markAsStateDependentAction("ToggleComment", true); //$NON-NLS-1$
        configureToggleCommentAction();

        action = new AddBlockCommentAction(ResourceBundle.getBundle(YangEditorMessages.getBundleName()),
                "AddBlockComment.", this); //$NON-NLS-1$
        action.setActionDefinitionId(IYangEditorActionDefinitionIds.ADD_BLOCK_COMMENT);
        setAction("AddBlockComment", action); //$NON-NLS-1$
        markAsStateDependentAction("AddBlockComment", true); //$NON-NLS-1$
        markAsSelectionDependentAction("AddBlockComment", true); //$NON-NLS-1$

        action = new RemoveBlockCommentAction(ResourceBundle.getBundle(YangEditorMessages.getBundleName()),
                "RemoveBlockComment.", this); //$NON-NLS-1$
        action.setActionDefinitionId(IYangEditorActionDefinitionIds.REMOVE_BLOCK_COMMENT);
        setAction("RemoveBlockComment", action); //$NON-NLS-1$
        markAsStateDependentAction("RemoveBlockComment", true); //$NON-NLS-1$
        markAsSelectionDependentAction("RemoveBlockComment", true); //$NON-NLS-1$
    }

    private void configureToggleCommentAction() {
        IAction action = getAction("ToggleComment"); //$NON-NLS-1$
        if (action instanceof ToggleCommentAction) {
            ISourceViewer sourceViewer = getSourceViewer();
            SourceViewerConfiguration configuration = getSourceViewerConfiguration();
            ((ToggleCommentAction) action).configure(sourceViewer, configuration);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createPartControl(org.eclipse.swt.widgets
     * .Composite)
     */
    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        ProjectionViewer projectionviewer = (ProjectionViewer) getSourceViewer();
        projectionviewer.addProjectionListener(this);

        projectionSupport = new ProjectionSupport(projectionviewer, getAnnotationAccess(), getSharedColors());
        projectionSupport.install();

        // turn projection mode on
        projectionviewer.doOperation(ProjectionViewer.TOGGLE);

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createSourceViewer(org.eclipse.swt.
     * widgets.Composite, org.eclipse.jface.text.source.IVerticalRuler, int)
     */
    @Override
    protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
        fAnnotationAccess = getAnnotationAccess();
        fOverviewRuler = createOverviewRuler(getSharedColors());

        ISourceViewer viewer = new ProjectionViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles);

        // ensure decoration support has been created and configured.
        getSourceViewerDecorationSupport(viewer);

        if (fFoldingStructureProvider != null) {
            fFoldingStructureProvider.setDocument(getDocumentProvider().getDocument(getEditorInput()));
        }

        return viewer;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.text.source.projection.IProjectionListener#projectionEnabled()
     */
    @Override
    public void projectionEnabled() {
        fFoldingStructureProvider = new YangFoldingStructureProvider(this);
        fFoldingStructureProvider.setDocument(getDocumentProvider().getDocument(getEditorInput()));

        Module module = YangParserUtil.parseYangFile(getDocumentProvider().getDocument(getEditorInput()).get()
                .toCharArray());

        if(module != null)
            fFoldingStructureProvider.updateFoldingRegions(module);

        // IPreferenceStore preferenceStore = AntUIPlugin.getDefault().getPreferenceStore();
        // preferenceStore.setValue(AntEditorPreferenceConstants.EDITOR_FOLDING_ENABLED, true);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.text.source.projection.IProjectionListener#projectionDisabled()
     */
    @Override
    public void projectionDisabled() {
        fFoldingStructureProvider = null;
        // IPreferenceStore preferenceStore = AntUIPlugin.getDefault().getPreferenceStore();
        // preferenceStore.setValue(AntEditorPreferenceConstants.EDITOR_FOLDING_ENABLED, false);

    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @Override
    public Object getAdapter(Class key) {

        if (projectionSupport != null) {
            Object adapter = projectionSupport.getAdapter(getSourceViewer(), key);
            if (adapter != null) {
                return adapter;
            }
        }

        return super.getAdapter(key);
    }

    public void updateFoldingRegions(Module module) {
        if (fFoldingStructureProvider != null && module != null) {
            fFoldingStructureProvider.updateFoldingRegions(module);
        }
    }
}
