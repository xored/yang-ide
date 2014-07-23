/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors.text.hover;

import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.internal.text.html.HTMLPrinter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.ui.IEditorPart;

import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.core.dom.ASTNamedNode;
import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.BaseReference;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.ModuleImport;
import com.cisco.yangide.core.dom.QName;
import com.cisco.yangide.core.dom.SubModuleInclude;
import com.cisco.yangide.core.dom.TypeReference;
import com.cisco.yangide.core.dom.UsesNode;
import com.cisco.yangide.core.indexing.ElementIndexInfo;
import com.cisco.yangide.core.indexing.ElementIndexType;
import com.cisco.yangide.core.model.YangModelManager;
import com.cisco.yangide.editor.YangEditorPlugin;
import com.cisco.yangide.editor.editors.YangEditor;

/**
 * @author Konstantin Zaitsev
 * @date Jul 23, 2014
 */
@SuppressWarnings("restriction")
public class YangTextHover implements ITextHover, ITextHoverExtension {

    private static String styleSheet = null;

    private IInformationControlCreator hoverControlCreator;
    private IInformationControlCreator presenterCtrlCreator;
    private IEditorPart editor;

    /**
     * @param editor the editor to set
     */
    public void setEditor(IEditorPart editor) {
        this.editor = editor;
    }

    /**
     * @return the editor
     */
    public IEditorPart getEditor() {
        return editor;
    }

    @Override
    public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
        return new Region(offset, 0);
    }

    @Override
    public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
        String hoverInfo = null;
        YangEditor editor = (YangEditor) getEditor();
        try {
            Module module = editor.getModule();
            if (module != null) {
                ASTNode node = module.getNodeAtPosition(hoverRegion.getOffset());
                hoverInfo = getHoverInfo(node);
            }
        } catch (YangModelException e) {
            YangCorePlugin.log(e);
        }
        return hoverInfo;
    }

    private String getHoverInfo(ASTNode node) {
        if (node.getDescription() != null && node.getDescription().length() > 0) {
            return getLocalHoverInfo(node);
        }
        return getIndexedInfo(node);
    }

    /**
     * @param node
     * @return
     */
    private String getIndexedInfo(ASTNode node) {
        QName name = null;
        ElementIndexType indexType = null;
        if (node instanceof TypeReference) {
            name = ((TypeReference) node).getType();
            indexType = ElementIndexType.TYPE;
        } else if (node instanceof UsesNode) {
            name = ((UsesNode) node).getGrouping();
            indexType = ElementIndexType.GROUPING;
        } else if (node instanceof ModuleImport) {
            ModuleImport mImport = (ModuleImport) node;
            name = new QName(mImport.getName(), mImport.getPrefix(), mImport.getName(), mImport.getRevision());
            indexType = ElementIndexType.MODULE;
        } else if (node instanceof SubModuleInclude) {
            SubModuleInclude include = (SubModuleInclude) node;
            name = new QName(null, null, include.getName(), include.getRevision());
            indexType = ElementIndexType.SUBMODULE;
        } else if (node instanceof BaseReference) {
            name = ((BaseReference) node).getType();
            indexType = ElementIndexType.IDENTITY;
        }

        if (name != null) {
            ElementIndexInfo[] search = YangModelManager.search(name.getModule(), name.getRevision(), name.getName(),
                    indexType, null, null);
            if (indexType == ElementIndexType.TYPE && search.length == 0) {
                search = YangModelManager.search(name.getModule(), name.getRevision(), name.getName(),
                        ElementIndexType.IDENTITY, null, null);
            }
            if (search.length > 0) {
                ElementIndexInfo info = search[0];
                StringBuffer buffer = new StringBuffer();
                HTMLPrinter.addSmallHeader(buffer, info.getName());
                if (info.getDescription() != null) {
                    HTMLPrinter.addParagraph(buffer, formatValue(info.getDescription()));
                }
                buffer.append("<dl>");
                if (info.getType() != ElementIndexType.MODULE && info.getType() != ElementIndexType.SUBMODULE) {
                    addValue(buffer, "Module", info.getModule());
                }
                addValue(buffer, "Reference", info.getReference());
                addValue(buffer, "Status", info.getStatus());
                addValue(buffer, "Revision", info.getRevision());
                addValue(buffer, "Namespace", info.getNamespace());
                addValue(buffer, "Organization", info.getOrganization());
                addValue(buffer, "Contact", info.getContact());
                buffer.append("</dl>");
                HTMLPrinter.insertPageProlog(buffer, 0, getStyleSheet());
                HTMLPrinter.addPageEpilog(buffer);
                return buffer.toString();
            }
        }
        return null;
    }

    /**
     * @param node
     * @return
     */
    private String getLocalHoverInfo(ASTNode node) {
        StringBuffer buffer = new StringBuffer();
        String name = node.getNodeName();

        if (node instanceof ASTNamedNode) {
            name += ":" + ((ASTNamedNode) node).getName();
        }
        HTMLPrinter.addSmallHeader(buffer, name);
        HTMLPrinter.addParagraph(buffer, formatValue(node.getDescription()));
        buffer.append("<dl>");
        if (node instanceof Module) {
            Module module = (Module) node;
            addValue(buffer, "Namespace", module.getNamespace() != null ? module.getNamespace().toASCIIString() : null);
            addValue(buffer, "Organization", module.getOrganization() != null ? module.getOrganization().getValue()
                    : null);
            addValue(buffer, "Contact", module.getContact() != null ? module.getContact().getValue() : null);
        }
        addValue(buffer, "Reference", node.getReference());
        addValue(buffer, "Status", node.getStatus());
        buffer.append("</dl>");
        HTMLPrinter.insertPageProlog(buffer, 0, getStyleSheet());
        HTMLPrinter.addPageEpilog(buffer);
        return buffer.toString();
    }

    @Override
    public IInformationControlCreator getHoverControlCreator() {
        if (presenterCtrlCreator == null) {
            presenterCtrlCreator = new PresenterControlCreator();
        }
        if (hoverControlCreator == null) {
            hoverControlCreator = new HoverControlCreator(presenterCtrlCreator);
        }
        return hoverControlCreator;
    }

    private String formatValue(String source) {
        return source.replaceAll("\\n", "<br>");
    }

    private void addValue(StringBuffer buffer, String key, String value) {
        if (value != null && value.length() > 0) {
            HTMLPrinter.addParagraph(buffer, "<dt>" + key + ":</dt><dd>" + formatValue(value) + "</dd>");
        }
    }

    private static String getStyleSheet() {
        if (styleSheet == null) {
            styleSheet = YangEditorPlugin.getDefault().getBundleFileContent("/resources/HoverStyleSheet.css");
        }
        String css = styleSheet;
        if (css != null) {
            FontData fontData = JFaceResources.getFontRegistry().getFontData(
                    PreferenceConstants.APPEARANCE_JAVADOC_FONT)[0];
            css = HTMLPrinter.convertTopLevelFont(css, fontData);
        }

        return css;
    }
}
