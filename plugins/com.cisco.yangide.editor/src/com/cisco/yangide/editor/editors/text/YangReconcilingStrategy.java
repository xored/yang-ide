/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors.text;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.internal.IYangValidationListener;
import com.cisco.yangide.core.internal.YangParserUtil;
import com.cisco.yangide.core.model.YangFile;
import com.cisco.yangide.core.model.YangFileInfo;
import com.cisco.yangide.core.model.YangModelManager;

/**
 * @author Konstantin Zaitsev
 * @date Jul 10, 2014
 */
public class YangReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {

    private IProgressMonitor monitor;
    private IDocument document;
    private ISourceViewer viewer;
    private ITextEditor editor;

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
        System.out.println();

    }

    @Override
    public void reconcile(IRegion partition) {
        if (editor == null || editor.getEditorInput() == null || !(editor.getEditorInput() instanceof IFileEditorInput)) {
            return;
        }
        IFile file = ((IFileEditorInput) editor.getEditorInput()).getFile();
        final IAnnotationModel annotationModel = getAnnotationModel();
        ArrayList<Annotation> toRemove = new ArrayList<Annotation>();

        Iterator<?> it = annotationModel.getAnnotationIterator();
        while (it.hasNext()) {
            Annotation annotation = (Annotation) it.next();
            if (annotation.getType().equals(YangSyntaxAnnotation.TYPE)) {
                toRemove.add(annotation);
            }
        }
        for (Annotation annotation : toRemove) {
            annotationModel.removeAnnotation(annotation);
        }
        YangFile yangFile = YangCorePlugin.createYangFile(file);
        Module module = YangParserUtil.parseYangFile(document.get().toCharArray(), new IYangValidationListener() {

            @Override
            public void validationError(String msg, int lineNumber, int charStart, int charEnd) {
                annotationModel.addAnnotation(new YangSyntaxAnnotation(msg), new Position(charStart, charEnd
                        - charStart));
            }

            @Override
            public void syntaxError(String msg, int lineNumber, int charStart, int charEnd) {
                annotationModel.addAnnotation(new YangSyntaxAnnotation(msg), new Position(charStart, charEnd
                        - charStart));
            }
        });
        try {
            YangFileInfo fileInfo = (YangFileInfo) yangFile.getElementInfo(monitor);
            fileInfo.setModule(module);
            fileInfo.setIsStructureKnown(true);
            // reindex content
            YangModelManager.getIndexManager().addSource(file);
        } catch (YangModelException e) {
            // ignore this exception on reconcile
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