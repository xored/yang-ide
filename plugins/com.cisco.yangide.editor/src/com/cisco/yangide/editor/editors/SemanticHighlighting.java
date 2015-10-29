/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.editor.editors;

import org.eclipse.swt.graphics.RGB;

import com.cisco.yangide.core.dom.ASTCompositeNode;
import com.cisco.yangide.core.dom.ASTNamedNode;
import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.SimpleNode;
import com.cisco.yangide.core.dom.SubModule;

/**
 * Semantic highlighting
 * 
 * @author Alexey Kholupko
 */
public abstract class SemanticHighlighting {

    /**
     * @return the preference key, will be augmented by a prefix and a suffix for each preference
     */
    public abstract String getPreferenceKey();

    /**
     * @return the default default text color
     */
    public abstract RGB getDefaultTextColor();

    /**
     * @return <code>true</code> if the text attribute bold is set by default
     */
    public abstract boolean isBoldByDefault();

    /**
     * @return <code>true</code> if the text attribute italic is set by default
     */
    public abstract boolean isItalicByDefault();

    /**
     * @return <code>true</code> if the text attribute strikethrough is set by default
     */
    public boolean isStrikethroughByDefault() {
        return false;
    }

    /**
     * @return <code>true</code> if the text attribute underline is set by default
     */
    public boolean isUnderlineByDefault() {
        return false;
    }

    /**
     * @return <code>true</code> if the text attribute italic is enabled by default
     */
    public abstract boolean isEnabledByDefault();

    /**
     * @return the display name
     */
    public abstract String getDisplayName();

    /**
     * Returns <code>true</code> iff the semantic highlighting consumes the semantic token.
     * <p>
     * NOTE: Implementors are not allowed to keep a reference on the token or on any object
     * retrieved from the token.
     * </p>
     *
     * @param node the semantic token for a {@link org.eclipse.jdt.core.dom.SimpleName}
     * @return <code>true</code> iff the semantic highlighting consumes the semantic token
     */
    public abstract boolean consumes(ASTNode node);

    /**
     * @param node
     * @return
     */
    protected int getHiglightingOffset(ASTNode node, String nodeModulePrefix) {
        int result = -1;
        if (node instanceof ASTNamedNode) {

            String thisModulePrefix = null;
            ASTNode thisModule = node.getModule();
            if (thisModule instanceof SubModule) {
                thisModulePrefix = ((SubModule) thisModule).getParentPrefix();
            } else {
                SimpleNode<String> prefixNode = ((Module) thisModule).getPrefix();
                if (prefixNode != null) {
                    thisModulePrefix = prefixNode.getValue();
                }
            }

            result = ((ASTNamedNode) node).getNameStartPosition();
            // skip prefix
            if (nodeModulePrefix != null && !nodeModulePrefix.equals(thisModulePrefix)) {
                result += nodeModulePrefix.length() + 1; // 1 is is for colon to be black
            }
        }
        if (node instanceof ASTCompositeNode) {
            return ((ASTCompositeNode) node).getNameStartPosition();
        }

        return result;
    }

    /**
     * @param node
     * @return
     */
    public abstract int getHiglightingOffset(ASTNode node);

    /**
     * @param node
     * @return
     */
    protected int getHiglightingLength(ASTNode node, String nodeModulePrefix) {
        int result = -1;
        if (node instanceof ASTNamedNode) {

            String thisModulePrefix = null;
            ASTNode thisModule = node.getModule();
            if (thisModule instanceof SubModule) {
                thisModulePrefix = ((SubModule) thisModule).getParentPrefix();
            } else {
                SimpleNode<String> prefixNode = ((Module) thisModule).getPrefix();
                if (prefixNode != null) {
                    thisModulePrefix = prefixNode.getValue();
                }
            }

            result = ((ASTNamedNode) node).getNameLength();
            // skip prefix
            if (nodeModulePrefix != null && !nodeModulePrefix.equals(thisModulePrefix)) {
                result -= nodeModulePrefix.length() + 1; // 1 is is for semicolon to be black
            }

            // in case of name is quoted
            if (((ASTNamedNode) node).getName().length() != ((ASTNamedNode) node).getNameLength()) {
                result -= 1;
            }
        }

        return result;
    }

    /**
     * @param node
     * @return
     */
    public abstract int getHiglightingLength(ASTNode node);

}
