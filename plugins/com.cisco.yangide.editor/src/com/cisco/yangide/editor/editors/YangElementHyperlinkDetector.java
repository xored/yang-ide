/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import com.cisco.yangide.core.dom.ASTNamedNode;
import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.ModuleImport;
import com.cisco.yangide.core.dom.QName;
import com.cisco.yangide.core.dom.TypeReference;
import com.cisco.yangide.core.dom.UsesNode;
import com.cisco.yangide.core.indexing.ElementIndexInfo;
import com.cisco.yangide.core.indexing.ElementIndexType;
import com.cisco.yangide.core.model.YangModelManager;
import com.cisco.yangide.core.parser.YangParserUtil;

/**
 * @author Konstantin Zaitsev
 * @date Jul 4, 2014
 */
public class YangElementHyperlinkDetector extends AbstractHyperlinkDetector {

    @Override
    public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
        if (region == null || textViewer == null) {
            return null;
        }

        IDocument document = textViewer.getDocument();

        int offset = region.getOffset();

        if (document == null) {
            return null;
        }

        try {
            Module module = YangParserUtil.parseYangFile(document.get().toCharArray());
            ASTNode node = module.getNodeAtPosition(offset);

            if (node == null) {
                return null;
            }

            ElementIndexInfo[] searchResult = null;

            if (node instanceof ModuleImport) {
                ModuleImport importNode = (ModuleImport) node;
                searchResult = YangModelManager.search(null, importNode.getRevision(), importNode.getName(),
                        ElementIndexType.MODULE, null, null);
            } else if (node instanceof TypeReference) {
                TypeReference ref = (TypeReference) node;
                QName type = ref.getType();
                searchResult = YangModelManager.search(type.getModule(), type.getRevision(), type.getName(),
                        ElementIndexType.TYPE, null, null);
            } else if (node instanceof UsesNode) {
                UsesNode usesNode = (UsesNode) node;
                QName ref = usesNode.getGrouping();
                searchResult = YangModelManager.search(ref.getModule(), ref.getRevision(), ref.getName(),
                        ElementIndexType.GROUPING, null, null);
            }

            if (searchResult != null && searchResult.length > 0) {
                IRegion elementRegion = new Region(((ASTNamedNode) node).getNameStartPosition(),
                        ((ASTNamedNode) node).getNameLength());
                return new IHyperlink[] { new YangElementHyperlink(elementRegion, searchResult[0]) };
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }
}
