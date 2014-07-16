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
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.DefaultCharacterPairMatcher;
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
    
    /** holds the calculated positions */
    protected final ArrayList fPositions = new ArrayList();
    /** The end offset of the range to be scanned */
    protected int fRangeEnd;    
    
    /**
     * next character position - used locally and only valid while
     * {@link #calculatePositions()} is in progress.
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
        try {
            final AtomicBoolean errors = new AtomicBoolean(false);
            Module module = YangParserUtil.parseYangFile(document.get().toCharArray(), file.getProject(),
                    new IYangValidationListener() {

                @Override
                public void validationError(String msg, int lineNumber, int charStart, int charEnd) {
                    errors.set(true);
                    annotationModel.addAnnotation(new YangSyntaxAnnotation(msg), new Position(charStart,
                            charEnd - charStart));
                }

                @Override
                public void syntaxError(String msg, int lineNumber, int charStart, int charEnd) {
                    errors.set(true);
                    annotationModel.addAnnotation(new YangSyntaxAnnotation(msg), new Position(charStart,
                            charEnd - charStart));
                }
            });
            // reindex if no errors found
            if (!errors.get()) {
                YangFileInfo fileInfo = (YangFileInfo) yangFile.getElementInfo(monitor);
                fileInfo.setModule(module);
                fileInfo.setIsStructureKnown(true);
                // reindex content
                YangModelManager.getIndexManager().addSource(file);
            }
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
        fRangeEnd = document.getLength();
        calculatePositions();        
    }
    
    /**
     * uses {@link #fDocument}, {@link #fOffset} and {@link #fRangeEnd} to
     * calculate {@link #fPositions}. About syntax errors: this method is not a
     * validator, it is useful.
     */
    protected void calculatePositions() {
            fPositions.clear();
            cNextPos = 0;

            try {
                    recursiveTokens(0);
            } catch (BadLocationException e) {
                    e.printStackTrace();
            }
            // Collections.sort(fPositions, new RangeTokenComparator());

            Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                            ((YangEditor) editor).updateFoldingStructure(fPositions);
                    }

            });
    }
    
    protected void emitPosition(int startOffset, int length) {
        fPositions.add(new Position(startOffset, length));
    }
    
    /**
     * emits tokens to {@link #fPositions}.
     *
     * @return number of newLines
     * @throws BadLocationException
     */
    protected int recursiveTokens(int depth) throws BadLocationException {
        
        char[] matchChars = { '{', '}' }; // which brackets to match
        ICharacterPairMatcher matcher = new DefaultCharacterPairMatcher(matchChars,
                IDocumentExtension3.DEFAULT_PARTITIONING);
        
        
            int newLines = 0;
            while (cNextPos < fRangeEnd) {

                            char ch = document.getChar(cNextPos++);
                            switch (ch) {
                            case '{':
                                IRegion region = matcher.match(document, cNextPos);
                                
                                String foldedString = document.get(cNextPos - 1, region.getLength()); // this is to see where we are in the debugger
                                
                                int startPosition = document.getLineOffset(document.getLineOfOffset(cNextPos - 1));
                                
                                emitPosition(startPosition, region.getLength() + cNextPos - startPosition);
                                break;
                            }
                    }

            
            return newLines;
    }
   
}
