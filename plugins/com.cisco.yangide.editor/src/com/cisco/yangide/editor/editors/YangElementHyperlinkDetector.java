package com.cisco.yangide.editor.editors;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.ModuleImport;
import com.cisco.yangide.core.indexing.ElementIndexInfo;
import com.cisco.yangide.core.indexing.ElementIndexType;
import com.cisco.yangide.core.internal.YangASTParser;
import com.cisco.yangide.core.model.YangModelManager;

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
            Module module = new YangASTParser().parseYangFile(document.get().toCharArray());
            ASTNode node = module.getNodeAtPosition(offset);

            if (node == null) {
                return null;
            }

            if (node instanceof ModuleImport) {
                ModuleImport importNode = (ModuleImport) node;
                IRegion elementRegion = new Region(importNode.getNameStartPosition(), importNode.getNameLength());
                ElementIndexInfo[] searchResult = YangModelManager.search(null, importNode.getName(),
                        ElementIndexType.MODULE, null);
                if (searchResult.length > 0) {
                    return new IHyperlink[] { new YangElementHyperlink(elementRegion, searchResult[0]) };
                }
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }

}
