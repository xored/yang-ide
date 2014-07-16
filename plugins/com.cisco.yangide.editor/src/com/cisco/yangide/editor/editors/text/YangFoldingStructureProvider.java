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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;

import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.ASTVisitor;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.editor.YangEditorPlugin;
import com.cisco.yangide.editor.editors.YangEditor;

/**
 * @author Alexey Kholupko
 */
@SuppressWarnings("rawtypes")
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

    private void updateFoldingRegions(ProjectionAnnotationModel model, Set currentRegions) {
        Annotation[] deletions = computeDifferences(model, currentRegions);

        Map additionsMap = new HashMap();
        for (Iterator iter = currentRegions.iterator(); iter.hasNext();) {
            Object position = iter.next();
            additionsMap.put(new ProjectionAnnotation(false), position);
        }

        if ((deletions.length != 0 || additionsMap.size() != 0)) {
            model.modifyAnnotations(deletions, additionsMap, new Annotation[] {});
        }
                
    }

    private Annotation[] computeDifferences(ProjectionAnnotationModel model, Set additions) {
        List deletions = new ArrayList();
        for (Iterator iter = model.getAnnotationIterator(); iter.hasNext();) {
            Object annotation = iter.next();
            if (annotation instanceof ProjectionAnnotation) {
                Position position = model.getPosition((Annotation) annotation);
                if (additions.contains(position)) {
                    additions.remove(position);
                } else {
                    deletions.add(annotation);
                }
            }
        }
        return (Annotation[]) deletions.toArray(new Annotation[deletions.size()]);
    }

    public void updateFoldingRegions(Module yangModule) {
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

            Set currentRegions = new HashSet();

            addFoldingRegions(currentRegions, root);

            addFoldingNonASTregions(currentRegions);

            updateFoldingRegions(model, currentRegions);
        } catch (BadLocationException be) {
            // ignore as document has changed
        }
    }

    private void addFoldingNonASTregions(Set regions) {

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
                            Position foldingPosition = new Position(start, end - start);
                            regions.add(foldingPosition);
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

    private void addFoldingRegions(Set regions, List children) throws BadLocationException {
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
                regions.add(position);
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
}
