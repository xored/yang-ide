/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.internal.core.JarEntryFile;
import org.eclipse.jdt.internal.ui.javaeditor.JarEntryEditorInput;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.DefaultCharacterPairMatcher;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension2;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.IProjectionListener;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.ITextEditorHelpContextIds;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.YangJarFileEntryResource;
import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.core.dom.ASTNamedNode;
import com.cisco.yangide.core.dom.ASTNode;
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
@SuppressWarnings("restriction")
public class YangEditor extends TextEditor implements IProjectionListener {

    // TODO extract logic to separate classes
    public final static String EDITOR_MATCHING_BRACKETS = "matchingBrackets";

    private IColorManager colorManager;

    private ProjectionSupport projectionSupport;

    private YangFoldingStructureProvider fFoldingStructureProvider;

    private SemanticHighlightingManager fSemanticManager;

    private YangEditorSelectionChangedListener editorSelectionChangedListener;

    private YangOutlineSelectionChangedListener outlineSelectionChangedListener = new YangOutlineSelectionChangedListener();

    private YangContentOutlinePage outlinePage;

    private List<ActionGroup> actionGroups = new ArrayList<ActionGroup>();

    private abstract class AbstractSelectionChangedListener implements ISelectionChangedListener {
        public void install(ISelectionProvider selectionProvider) {
            try {
                if (selectionProvider == null || getModule() == null) {
                    return;
                }
            } catch (YangModelException e) {
                return;
            }
            if (selectionProvider instanceof IPostSelectionProvider) {
                IPostSelectionProvider provider = (IPostSelectionProvider) selectionProvider;
                provider.addPostSelectionChangedListener(this);
            } else {
                selectionProvider.addSelectionChangedListener(this);
            }
        }

        public void uninstall(ISelectionProvider selectionProvider) {
            try {
                if (selectionProvider == null || getModule() == null) {
                    return;
                }
            } catch (YangModelException e) {
                return;
            }
            if (selectionProvider instanceof IPostSelectionProvider) {
                IPostSelectionProvider provider = (IPostSelectionProvider) selectionProvider;
                provider.removePostSelectionChangedListener(this);
            } else {
                selectionProvider.removeSelectionChangedListener(this);
            }
        }
    }

    private class YangEditorSelectionChangedListener extends AbstractSelectionChangedListener {

        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            if (event.getSelection() instanceof ITextSelection) {
                ITextSelection textSelection = (ITextSelection) event.getSelection();
                try {
                    if (null != outlinePage) {
                        outlinePage.selectNode(getModule().getNodeAtPosition(textSelection.getOffset()));
                    }
                } catch (YangModelException e) {
                }
            }

        }
    }

    private class YangOutlineSelectionChangedListener extends AbstractSelectionChangedListener {
        @Override
        @SuppressWarnings("unchecked")
        public void selectionChanged(SelectionChangedEvent event) {
            if (isYangOutlinePageActive()) {
                if (event.getSelection() instanceof IStructuredSelection) {
                    Iterator<Object> iter = ((IStructuredSelection) event.getSelection()).iterator();
                    if (iter.hasNext()) {
                        Object s = iter.next();
                        if (s instanceof ASTNamedNode) {
                            selectAndReveal(((ASTNamedNode) s).getNameStartPosition(),
                                    ((ASTNamedNode) s).getNameLength());
                        } else if (s instanceof ASTNode) {
                            selectAndReveal(((ASTNode) s).getStartPosition(), ((ASTNode) s).getLength());
                        }
                    }
                }
            }
        }
    }

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
        if (editorSelectionChangedListener != null) {
            editorSelectionChangedListener.uninstall(getSelectionProvider());
            editorSelectionChangedListener = null;
        }
        colorManager.dispose();
        for (ActionGroup actionGroup : actionGroups) {
            actionGroup.dispose();
        }
        actionGroups.clear();
        super.dispose();

        uninstallSemanticHighlighting();

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

    /**
     * @return the actionGroups
     */
    public List<ActionGroup> getActionGroups() {
        return actionGroups;
    }

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

    @Override
    protected void createActions() {

        super.createActions();
        IAction action = null;
        ResourceBundle bundle = ResourceBundle.getBundle(YangEditorMessages.getBundleName());

        action = new TextOperationAction(bundle, "ContentFormat.", this, ISourceViewer.FORMAT); //$NON-NLS-1$
        action.setActionDefinitionId(IYangEditorActionDefinitionIds.FORMAT);
        setAction("FormatDocument", action); //$NON-NLS-1$

        action = getAction(ITextEditorActionConstants.CONTENT_ASSIST_CONTEXT_INFORMATION);

        action = new ToggleCommentAction(bundle, "ToggleComment.", this); //$NON-NLS-1$

        action.setActionDefinitionId(IYangEditorActionDefinitionIds.TOGGLE_COMMENT);
        setAction("ToggleComment", action); //$NON-NLS-1$
        markAsStateDependentAction("ToggleComment", true); //$NON-NLS-1$
        configureToggleCommentAction();

        action = new AddBlockCommentAction(bundle, "AddBlockComment.", this); //$NON-NLS-1$
        action.setActionDefinitionId(IYangEditorActionDefinitionIds.ADD_BLOCK_COMMENT);
        setAction("AddBlockComment", action); //$NON-NLS-1$
        markAsStateDependentAction("AddBlockComment", true); //$NON-NLS-1$
        markAsSelectionDependentAction("AddBlockComment", true); //$NON-NLS-1$

        action = new RemoveBlockCommentAction(bundle, "RemoveBlockComment.", this); //$NON-NLS-1$
        action.setActionDefinitionId(IYangEditorActionDefinitionIds.REMOVE_BLOCK_COMMENT);
        setAction("RemoveBlockComment", action); //$NON-NLS-1$
        markAsStateDependentAction("RemoveBlockComment", true); //$NON-NLS-1$
        markAsSelectionDependentAction("RemoveBlockComment", true); //$NON-NLS-1$

        IConfigurationElement[] configs = Platform.getExtensionRegistry().getConfigurationElementsFor(
                "com.cisco.yangide.editor.actionGroup");
        for (IConfigurationElement config : configs) {
            try {
                IActionGroup actionGroup = (IActionGroup) config.createExecutableExtension("class");
                actionGroup.init(this, config.getAttribute("groupName"));
                actionGroups.add((ActionGroup) actionGroup);
            } catch (CoreException e) {
                YangEditorPlugin.log(e);
            }
        }
    }

    private void configureToggleCommentAction() {
        IAction action = getAction("ToggleComment"); //$NON-NLS-1$
        if (action instanceof ToggleCommentAction) {
            ISourceViewer sourceViewer = getSourceViewer();
            SourceViewerConfiguration configuration = getSourceViewerConfiguration();
            ((ToggleCommentAction) action).configure(sourceViewer, configuration);
        }
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        ProjectionViewer projectionviewer = (ProjectionViewer) getSourceViewer();
        projectionviewer.addProjectionListener(this);

        projectionSupport = new ProjectionSupport(projectionviewer, getAnnotationAccess(), getSharedColors());
        projectionSupport.install();

        editorSelectionChangedListener = new YangEditorSelectionChangedListener();
        editorSelectionChangedListener.install(getSelectionProvider());

        // turn projection mode on
        projectionviewer.doOperation(ProjectionViewer.TOGGLE);

        SemanticHighlightings.initDefaults(YangUIPlugin.getDefault().getPreferenceStore());
        installSemanticHighlighting();
    }

    @Override
    protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
        fAnnotationAccess = getAnnotationAccess();
        fOverviewRuler = createOverviewRuler(getSharedColors());

        ISourceViewer viewer = new YangSourceViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles);

        // ensure decoration support has been created and configured.
        getSourceViewerDecorationSupport(viewer);

        if (fFoldingStructureProvider != null) {
            fFoldingStructureProvider.setDocument(getDocumentProvider().getDocument(getEditorInput()));
        }

        return viewer;
    }

    @Override
    public void projectionEnabled() {
        fFoldingStructureProvider = new YangFoldingStructureProvider(this);
        fFoldingStructureProvider.setDocument(getDocumentProvider().getDocument(getEditorInput()));

        Module module = YangParserUtil.parseYangFile(getDocumentProvider().getDocument(getEditorInput()).get()
                .toCharArray());

        if (module != null) {
            fFoldingStructureProvider.updateFoldingRegions(module);
        }

        // IPreferenceStore preferenceStore = AntUIPlugin.getDefault().getPreferenceStore();
        // preferenceStore.setValue(AntEditorPreferenceConstants.EDITOR_FOLDING_ENABLED, true);
    }

    @Override
    public void projectionDisabled() {
        fFoldingStructureProvider = null;
        // IPreferenceStore preferenceStore = AntUIPlugin.getDefault().getPreferenceStore();
        // preferenceStore.setValue(AntEditorPreferenceConstants.EDITOR_FOLDING_ENABLED, false);

    }

    @Override
    protected void editorContextMenuAboutToShow(IMenuManager menu) {
        super.editorContextMenuAboutToShow(menu);
        ActionContext context = new ActionContext(getSelectionProvider().getSelection());
        for (ActionGroup actionGroup : actionGroups) {
            actionGroup.setContext(context);
            actionGroup.fillContextMenu(menu);
            actionGroup.setContext(null);
        }
    }

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class key) {
        if (IContentOutlinePage.class.equals(key)) {
            return getYangOulinePage();
        }
        if (projectionSupport != null) {
            Object adapter = projectionSupport.getAdapter(getSourceViewer(), key);
            if (adapter != null) {
                return adapter;
            }
        }

        return super.getAdapter(key);
    }

    private boolean isYangOutlinePageActive() {
        IWorkbenchPart part = getSite().getWorkbenchWindow().getPartService().getActivePart();
        return part instanceof ContentOutline && ((ContentOutline) part).getCurrentPage() == outlinePage;
    }

    private YangContentOutlinePage getYangOulinePage() {
        if (null == outlinePage) {
            outlinePage = new YangContentOutlinePage(this);
            outlineSelectionChangedListener.install(outlinePage);
        }
        return outlinePage;
    }

    public void reconcile() {
        updateOutline();
        updateFoldingRegions();
    }

    private void updateFoldingRegions() {
        try {
            if (fFoldingStructureProvider != null) {
                fFoldingStructureProvider.updateFoldingRegions(getModule());
            }
        } catch (YangModelException e) {
            YangUIPlugin.log(e);
        }
    }

    private void updateOutline() {
        if (null != outlinePage) {
            outlinePage.updateOutline();
        }
    }

    public void updateSemanticHigliting() {
        if (fSemanticManager != null) {
            fSemanticManager.getReconciler().refresh();
        }
    }

    /**
     * @return {@link Module} of the current editor input or <code>null</code> if editor input does
     * not contains appropriate {@link Module}
     * @throws YangModelException error during initialization of Module
     */
    public Module getModule() throws YangModelException {
        IEditorInput input = getEditorInput();
        if (input == null) {
            return null;
        }

        if (input instanceof IFileEditorInput) {
            IFile file = ((IFileEditorInput) input).getFile();
            return YangCorePlugin.createYangFile(file).getModule();
        } else if (input instanceof JarEntryEditorInput) {
            JarEntryEditorInput jarInput = (JarEntryEditorInput) input;
            IStorage storage = jarInput.getStorage();
            if (storage instanceof YangJarFileEntryResource) {
                YangJarFileEntryResource jarEntry = (YangJarFileEntryResource) storage;
                return YangCorePlugin.createJarEntry(jarEntry.getPath(), jarEntry.getEntry()).getModule();
            } else if (storage instanceof JarEntryFile) {
                JarEntryFile jarEntry = (JarEntryFile) storage;
                return YangCorePlugin.createJarEntry(jarEntry.getPackageFragmentRoot().getPath(),
                        jarEntry.getFullPath().makeRelative().toString()).getModule();
            }
        }
        return null;
    }

    public ISourceViewer getViewer() {
        return this.getSourceViewer();
    }

    protected void installSemanticHighlighting() {
        if (fSemanticManager == null) {
            fSemanticManager = new SemanticHighlightingManager();
            fSemanticManager.install(this, (SourceViewer) getSourceViewer(), colorManager, getPreferenceStore());
        }
    }

    private void uninstallSemanticHighlighting() {
        if (fSemanticManager != null) {
            fSemanticManager.uninstall();
            fSemanticManager = null;
        }
    }

    public void installOccurrencesFinder(boolean b) {
        // TODO implement mark occurrence functionality
    }

    public boolean isMarkingOccurrences() {
        return false;
    }

    public void uninstallOccurrencesFinder() {
    }
}
