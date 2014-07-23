/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.IProjectionPosition;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;

import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.ASTVisitor;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.editor.YangEditorPlugin;
import com.cisco.yangide.editor.editors.YangEditor;
import com.cisco.yangide.editor.editors.YangPartitionScanner;

/**
 * @author Alexey Kholupko
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class YangFoldingStructureProvider {

    private YangEditor fEditor;
    private IDocument fDocument;

    /**
     * A mapping of the foldable position to the
     * <code>AntElementNode<code> that represent that region
     */

    private Map fPositionToElement = new HashMap();

    public YangFoldingStructureProvider(YangEditor editor) {
        fEditor = editor;
    }

    
    private void updateFoldingRegions(ProjectionAnnotationModel model, List currentRegions) {
        Annotation[] deletions = computeDifferences(model, currentRegions);

        Map additionsMap = new HashMap();

        Position headerCommentCandidate = (Position) currentRegions.get(0);
        ProjectionAnnotation headerCommentCandidateAnnotation = null;
        if(headerCommentCandidate != null){
            headerCommentCandidateAnnotation = new ProjectionAnnotation(false);
            additionsMap.put(headerCommentCandidateAnnotation, headerCommentCandidate);
        }

        for (int i = 1; i < currentRegions.size(); i++) {
            Position position = (Position) currentRegions.get(i);
            additionsMap.put(new ProjectionAnnotation(false), position);
        }

        if ((deletions.length != 0 || additionsMap.size() != 0)) {
            model.modifyAnnotations(deletions, additionsMap, new Annotation[] {});
        }

        if (isHeaderComment(headerCommentCandidate))
            model.collapse(headerCommentCandidateAnnotation);

    }

    /**
     * @param headerCommentCandidate
     * @return
     */
    private boolean isHeaderComment(Position headerCommentCandidate) {
        if(headerCommentCandidate == null)
            return false;
        
        YangPartitionScanner scanner = new YangPartitionScanner();
        scanner.setRange(fDocument, 0, fDocument.getLength());

        try {
            // first token must be YANG_COMMENT

            String contentType = null;
            IToken token;
            do {
                token = scanner.nextToken();
                contentType = getTokenContentType(token);
            } while (contentType == null && !token.isEOF());

            if (contentType != null && contentType.equals(YangPartitionScanner.YANG_COMMENT)) {

                int tokenStartLine = fDocument.getLineOfOffset(scanner.getTokenOffset());
                int tokenEndLine = fDocument.getLineOfOffset(scanner.getTokenOffset() + scanner.getTokenLength());

                int start = fDocument.getLineOffset(tokenStartLine);
                int end = fDocument.getLineOffset(tokenEndLine) + fDocument.getLineLength(tokenEndLine);
                Position tokenPosition = new Position(start, end - start);

                if (headerCommentCandidate.equals(tokenPosition))
                    return true;

            }

        } catch (BadLocationException e) {
            YangEditorPlugin.log(e);
        }

        return false;
    }

    private Annotation[] computeDifferences(ProjectionAnnotationModel model, List currentRegions) {
        List deletions = new ArrayList();
        for (Iterator iter = model.getAnnotationIterator(); iter.hasNext();) {
            Object annotation = iter.next();
            if (annotation instanceof ProjectionAnnotation) {
                Position position = model.getPosition((Annotation) annotation);
                if (currentRegions.contains(position)) {
                    currentRegions.remove(position);
                } else {
                    deletions.add(annotation);
                }
            }
        }
        return (Annotation[]) deletions.toArray(new Annotation[deletions.size()]);
    }

    public void updateFoldingRegions(Module yangModule) {
        if (yangModule != null) {
            fPositionToElement = new HashMap();
            try {
                ProjectionAnnotationModel model = (ProjectionAnnotationModel) fEditor
                        .getAdapter(ProjectionAnnotationModel.class);
                if (model == null) {
                    return;
                }

                final List root = new ArrayList();

                yangModule.accept(new ASTVisitor() {

                    @Override
                    public void preVisit(ASTNode node) {
                        root.add(node);
                    }
                });

                List currentRegions = new ArrayList<>();

                // order is important, to know about header comment
                addFoldingNonASTregions(currentRegions);

                addFoldingRegions(currentRegions, root);

                updateFoldingRegions(model, currentRegions);
            } catch (BadLocationException be) {
                // ignore as document has changed
            }
        }
    }

    private void addFoldingNonASTregions(List currentRegions) {

        // litle hack here, because of FastPartitioner odd privacy
        String[] categories = fDocument.getPositionCategories();
        for (String category : categories) {
            if (category.startsWith("__content_types_category"))
                try {
                    Position[] positions = fDocument.getPositions(category);
                    for (Position position : positions) {

                        int positionOffset = position.getOffset();
                        int positionLength = position.getLength();
                        // for single line comment - EndOfLineRule
                        if (fDocument.getChar(positionOffset + position.getLength() - 1) == '\n')
                            positionLength--;

                        int startLine = fDocument.getLineOfOffset(positionOffset);
                        int endLine = fDocument.getLineOfOffset(positionOffset + positionLength);
                        if (startLine < endLine) {
                            int start = fDocument.getLineOffset(startLine);
                            int end = fDocument.getLineOffset(endLine) + fDocument.getLineLength(endLine);
                            Position foldingPosition = // new Position(start, end - start);
                            new CommentPosition(start, end - start);
                            currentRegions.add(foldingPosition);
                            // fPositionToElement.put(foldingPosition, element);
                        }

                    }

                } catch (BadPositionCategoryException | BadLocationException e) {
                    YangEditorPlugin.log(e);
                }
        }

    }

    protected String getTokenContentType(IToken token) {
        Object data = token.getData();
        if (data instanceof String)
            return (String) data;
        return null;
    }

    private void addFoldingRegions(List currentRegions, List children) throws BadLocationException {
        // add a Position to 'regions' for each foldable region
        Iterator iter = children.iterator();
        while (iter.hasNext()) {
            ASTNode element = (ASTNode) iter.next();
            // if (element.getImportNode() != null || element.isExternal()) {
            // continue; //elements are not really in this document and therefore are not foldable
            // }
            int startLine = fDocument.getLineOfOffset(element.getStartPosition());
            int endLine = fDocument.getLineOfOffset(element.getStartPosition() + element.getLength());
            if (startLine < endLine) {
                int start = fDocument.getLineOffset(startLine);
                int end = fDocument.getLineOffset(endLine) + fDocument.getLineLength(endLine);
                Position position = new Position(start, end - start);
                currentRegions.add(position);
                fPositionToElement.put(position, element);
            }

            // List childNodes= element.getChildNodes();
            // if (childNodes != null) {
            // addFoldingRegions(regions, childNodes);
            // }
        }
    }

    public void setDocument(IDocument document) {
        fDocument = document;
    }

    /**
     * Projection position that will return two foldable regions: one folding away the region from
     * after the '/**' to the beginning of the content, the other from after the first content line
     * until after the comment.
     */
    private static final class CommentPosition extends Position implements IProjectionPosition {
        CommentPosition(int offset, int length) {
            super(offset, length);
        }

        /*
         * @see
         * org.eclipse.jface.text.source.projection.IProjectionPosition#computeFoldingRegions(org
         * .eclipse.jface.text.IDocument)
         */
        public IRegion[] computeProjectionRegions(IDocument document) throws BadLocationException {
            DocumentCharacterIterator sequence = new DocumentCharacterIterator(document, offset, offset + length);
            int prefixEnd = 0;
            int contentStart = findFirstContent(sequence, prefixEnd);

            int firstLine = document.getLineOfOffset(offset + prefixEnd);
            int captionLine = document.getLineOfOffset(offset + contentStart);
            int lastLine = document.getLineOfOffset(offset + length);

            Assert.isTrue(firstLine <= captionLine, "first folded line is greater than the caption line"); //$NON-NLS-1$
            Assert.isTrue(captionLine <= lastLine, "caption line is greater than the last folded line"); //$NON-NLS-1$

            IRegion preRegion;
            if (firstLine < captionLine) {
                // preRegion= new Region(offset + prefixEnd, contentStart - prefixEnd);
                int preOffset = document.getLineOffset(firstLine);
                IRegion preEndLineInfo = document.getLineInformation(captionLine);
                int preEnd = preEndLineInfo.getOffset();
                preRegion = new Region(preOffset, preEnd - preOffset);
            } else {
                preRegion = null;
            }

            if (captionLine < lastLine) {
                int postOffset = document.getLineOffset(captionLine + 1);
                int postLength = offset + length - postOffset;
                if (postLength > 0) {
                    IRegion postRegion = new Region(postOffset, postLength);
                    if (preRegion == null)
                        return new IRegion[] { postRegion };
                    return new IRegion[] { preRegion, postRegion };
                }
            }

            if (preRegion != null)
                return new IRegion[] { preRegion };

            return null;
        }

        /**
         * Finds the offset of the first identifier part within <code>content</code>. Returns 0 if
         * none is found.
         * 
         * @param content the content to search
         * @param prefixEnd the end of the prefix
         * @return the first index of a unicode identifier part, or zero if none can be found
         */
        private int findFirstContent(final CharSequence content, int prefixEnd) {
            int lenght = content.length();
            for (int i = prefixEnd; i < lenght; i++) {
                if (Character.isUnicodeIdentifierPart(content.charAt(i)))
                    return i;
            }
            return 0;
        }

        /*
         * @see
         * org.eclipse.jface.text.source.projection.IProjectionPosition#computeCaptionOffset(org
         * .eclipse.jface.text.IDocument)
         */
        public int computeCaptionOffset(IDocument document) throws BadLocationException {
            DocumentCharacterIterator sequence = new DocumentCharacterIterator(document, offset, offset + length);
            return findFirstContent(sequence, 0);
        }
    }
}
