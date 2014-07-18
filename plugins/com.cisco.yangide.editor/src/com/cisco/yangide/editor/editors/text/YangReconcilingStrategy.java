/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors.text;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.model.YangFile;
import com.cisco.yangide.core.model.YangFileInfo;
import com.cisco.yangide.core.model.YangModelManager;
import com.cisco.yangide.core.parser.IYangValidationListener;
import com.cisco.yangide.core.parser.YangParserUtil;
import com.cisco.yangide.editor.editors.YangEditor;

/**
 * @author Konstantin Zaitsev
 * @date Jul 10, 2014
 */
public class YangReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {

    private IProgressMonitor monitor;
    private IDocument document;
    private ISourceViewer viewer;
    private ITextEditor editor;

    /**
     * next character position - used locally and only valid while {@link #calculatePositions()} is
     * in progress.
     */
    protected int cNextPos = 0;

    /** number of newLines found by {@link #classifyTag()} */
    protected int cNewLines = 0;

    public YangReconcilingStrategy(ISourceViewer viewer, ITextEditor editor) {
        this.viewer = viewer;
        this.editor = editor;
    }

    @Override
    public void setDocument(IDocument document) {
        this.document = document;
    }

    @Override
    public void setProgressMonitor(IProgressMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
        reconcile(dirtyRegion);
    }

    @Override
    public void reconcile(IRegion partition) {
        if (editor == null || editor.getEditorInput() == null || !(editor.getEditorInput() instanceof IFileEditorInput)) {
            return;
        }
        IFile file = ((IFileEditorInput) editor.getEditorInput()).getFile();
        final YangAnnotationModel annotationModel = (YangAnnotationModel) getAnnotationModel();

        YangFile yangFile = YangCorePlugin.createYangFile(file);
        try {
            final AtomicBoolean errors = new AtomicBoolean(false);
            annotationModel.init();
            Module module = YangParserUtil.parseYangFile(document.get().toCharArray(), file.getProject(),
                    new IYangValidationListener() {

                        @Override
                        public void validationError(String msg, int lineNumber, int charStart, int charEnd) {
                            errors.set(true);
                            annotationModel.addProblem(new YangProblem(new YangSyntaxAnnotation(null), new Position(
                                    charStart, charEnd - charStart)));
                        }

                        @Override
                        public void syntaxError(String msg, int lineNumber, int charStart, int charEnd) {
                            errors.set(true);
                            annotationModel.addProblem(new YangProblem(new YangSyntaxAnnotation(null), new Position(
                                    charStart, charEnd - charStart)));
                        }
                    });
            annotationModel.reportProblem();
            // reindex if no errors found
            if (!errors.get()) {
                YangFileInfo fileInfo = (YangFileInfo) yangFile.getElementInfo(monitor);
                fileInfo.setModule(module);
                fileInfo.setIsStructureKnown(true);
                // re index content
                YangModelManager.getIndexManager().addWorkingCopy(file);
            }

            if (editor instanceof YangEditor && module != null)
                ((YangEditor) editor).updateFoldingRegions(module);

        } catch (Exception e) {
            // ignore any exception on reconcile
        }
    }

    private IAnnotationModel getAnnotationModel() {
        return viewer.getAnnotationModel();
    }

    @Override
    public void initialReconcile() {
        reconcile(new Region(0, document.getLength()));
    }

}
