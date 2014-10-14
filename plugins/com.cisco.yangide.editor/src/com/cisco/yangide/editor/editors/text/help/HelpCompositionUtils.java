/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */ 
package com.cisco.yangide.editor.editors.text.help;

import java.util.Objects;

import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.internal.text.html.HTMLPrinter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.FontData;

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

/**
 * Utilities aimed to be used in different context-oriented help providers for YANG source
 * code text editor. These providers include hover, quick-fix, content-assist and the like.
 * 
 * @author Kirill Karmakulov
 * @date   Oct 13, 2014
 */
@SuppressWarnings("restriction")
public class HelpCompositionUtils {

    private static String styleSheet = null;
    
    /**
     * Gets quick help related to the provided {@code node}.
     * 
     * @return a {@link String} containing the help  
     */
    public static String getNodeHelp(ASTNode node) {
        if (node.getDescription() != null && node.getDescription().length() > 0) {
            return getLocalInfo(node);
        }
        return getIndexedInfo(node);
    }

    /**
     * Gets quick help related to the provided {@code node}. The information
     * returned as the result is taken from the index and matches the given {@code node}.
     */
    public static String getIndexedInfo(ASTNode node) {
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
        return name != null ? getIndexedInfo(name.getName(), name.getModule(), name.getRevision(), indexType) : null;
    }

    /**
     * Gets quick help related to the provided {@code nodeName}, {@code module}, {@code revision}. The information
     * returned as the result is taken from the index and matches the given fields.
     */
    public static String getIndexedInfo(String nodeName, String module, String revision, ElementIndexType indexType) {
        ElementIndexInfo[] search = YangModelManager.search(module, revision, nodeName, indexType, null, null);
        if (indexType == ElementIndexType.TYPE && search.length == 0) {
            search = YangModelManager.search(module, revision, nodeName, ElementIndexType.IDENTITY, null, null);
        }
        if (search.length == 0)
            return null;

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
        return finish(buffer);
    }

    /**
     * Gets quick help related to the provided {@code node}. The information
     * returned as the result is retrieved from the node itself.
     */
    public static String getLocalInfo(ASTNode node) {
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
            addValue(buffer, "Namespace", module.getNamespace() != null ? module.getNamespace() : null);
            addValue(buffer, "Organization", module.getOrganization() != null ? module.getOrganization().getValue()
                    : null);
            addValue(buffer, "Contact", module.getContact() != null ? module.getContact().getValue() : null);
        }
        addValue(buffer, "Reference", node.getReference());
        addValue(buffer, "Status", node.getStatus());
        buffer.append("</dl>");
        return finish(buffer);
    }

    /**
     * Adds standard HTML header and footer to a partial HTML given in {@code text}.
     * Adds the given {@code title} if one is specified. 
     * 
     * @param text  the wrapped HTML text
     * @param title  an optional title
     * @return  a String that contains fully equipped HTML text 
     */
    public static String wrapHtmlText(String text, String title)
    {
        Objects.requireNonNull(text, "The wrapped HTML content must not be null");
        StringBuffer buffer = new StringBuffer();
        if (title != null && !title.isEmpty()) {
            HTMLPrinter.addSmallHeader(buffer, title);
        }
        if (text.contains("<")) // has formatting
            buffer.append(text);
        else
            HTMLPrinter.addParagraph(buffer, text);
        return finish(buffer);
    }

    private static String formatValue(String source) {
        return source.replaceAll("\\n", "<br>");
    }

    private static void addValue(StringBuffer buffer, String key, String value) {
        if (value != null && value.length() > 0) {
            HTMLPrinter.addParagraph(buffer, "<dt>" + key + ":</dt><dd>" + formatValue(value) + "</dd>");
        }
    }

    private static String finish(StringBuffer buffer) {
        HTMLPrinter.insertPageProlog(buffer, 0, getStyleSheet());
        HTMLPrinter.addPageEpilog(buffer);
        return buffer.toString();
    }

    /**
     * Returns a stylesheet used among quick-help infopopups. 
     */
    private static String getStyleSheet() {
        if (styleSheet == null) {
            styleSheet = YangEditorPlugin.getDefault().getBundleFileContent(
                    "/resources/HoverStyleSheet.css");
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
