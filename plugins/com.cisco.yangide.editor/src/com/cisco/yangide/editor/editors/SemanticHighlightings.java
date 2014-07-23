/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors;

import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.RGB;

import com.cisco.yangide.core.YangTypeUtil;
import com.cisco.yangide.core.dom.ASTNamedNode;
import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.GroupingDefinition;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.SubModule;
import com.cisco.yangide.core.dom.TypeDefinition;
import com.cisco.yangide.core.dom.TypeReference;
import com.cisco.yangide.core.dom.UsesNode;
import com.cisco.yangide.editor.preferences.YangPreferencesMessages;

/**
 * Semantic highlightings
 * 
 * @author Alexey Kholupko
 */
public class SemanticHighlightings {

    /**
     * A named preference part that controls the highlighting of fields.
     */
    public static final String TYPE = "type"; //$NON-NLS-1$
    public static final String GROUPING = "grouping"; //$NON-NLS-1$	
    public static final String PREFIX = "prefix"; //$NON-NLS-1$

    /**
     * Semantic highlightings
     */
    private static SemanticHighlighting[] fgSemanticHighlightings;

    /**
     * Semantic highlighting for client types.
     */
    private static final class TypeHighlighting extends SemanticHighlighting {

        /*
         * @see org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
         */
        @Override
        public String getPreferenceKey() {
            return TYPE;
        }

        /*
         * @see org.eclipse.jdt.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
         */
        @Override
        public RGB getDefaultTextColor() {
            return new RGB(128, 128, 0);
        }

        /*
         * @see
         * org.eclipse.jdt.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
         */
        @Override
        public boolean isBoldByDefault() {
            return false;
        }

        /*
         * @see org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
         */
        @Override
        public boolean isItalicByDefault() {
            return true;
        }

        /*
         * @see org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
         */
        @Override
        public boolean isEnabledByDefault() {
            return true;
        }

        /*
         * @see org.eclipse.jdt.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
         */
        @Override
        public String getDisplayName() {
            return YangPreferencesMessages.SemanticHighlighting_type;
        }

        /*
         * @see
         * org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlighting#consumes(org.eclipse.jdt.
         * internal.ui.javaeditor.SemanticToken)
         */
        @Override
        public boolean consumes(ASTNode node) {

            if (node instanceof TypeReference) {
                String typeWholeName = ((TypeReference) node).getName();
                return !YangTypeUtil.isBuiltInType(typeWholeName);
            }

            return node instanceof TypeDefinition;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.cisco.yangide.editor.editors.SemanticHighlighting#getHiglightingOffset(com.cisco.
         * yangide.core.dom.ASTNode)
         */
        @Override
        public int getHiglightingOffset(ASTNode node) {
            int result = -1;
            String nodeModulePrefix = null;
            if (node instanceof TypeReference)
                nodeModulePrefix = ((TypeReference) node).getType().getPrefix();

            result = getHiglightingOffset(node, nodeModulePrefix);
            return result;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.cisco.yangide.editor.editors.SemanticHighlighting#getHiglightingLength(com.cisco.
         * yangide.core.dom.ASTNode)
         */
        @Override
        public int getHiglightingLength(ASTNode node) {
            int result = -1;
            String nodeModulePrefix = null;
            if (node instanceof TypeReference)
                nodeModulePrefix = ((TypeReference) node).getType().getPrefix();

            result = getHiglightingLength(node, nodeModulePrefix);
            return result;
        }
    }

    /**
     * Semantic highlighting for client groupings.
     */
    private static final class GroupingHighlighting extends SemanticHighlighting {

        /*
         * @see org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
         */
        @Override
        public String getPreferenceKey() {
            return GROUPING;
        }

        /*
         * @see org.eclipse.jdt.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
         */
        @Override
        public RGB getDefaultTextColor() {
            return new RGB(192, 64, 0);
        }

        /*
         * @see
         * org.eclipse.jdt.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
         */
        @Override
        public boolean isBoldByDefault() {
            return false;
        }

        /*
         * @see org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
         */
        @Override
        public boolean isItalicByDefault() {
            return true;
        }

        /*
         * @see org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
         */
        @Override
        public boolean isEnabledByDefault() {
            return true;
        }

        /*
         * @see org.eclipse.jdt.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
         */
        @Override
        public String getDisplayName() {
            return YangPreferencesMessages.SemanticHighlighting_grouping;
        }

        /*
         * @see
         * org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlighting#consumes(org.eclipse.jdt.
         * internal.ui.javaeditor.SemanticToken)
         */
        @Override
        public boolean consumes(ASTNode node) {

            return node instanceof GroupingDefinition || node instanceof UsesNode;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.cisco.yangide.editor.editors.SemanticHighlighting#getHiglightingOffset(com.cisco.
         * yangide.core.dom.ASTNode)
         */
        @Override
        public int getHiglightingOffset(ASTNode node) {
            int result = -1;

            String nodeModulePrefix = null;
            if (node instanceof UsesNode)
                nodeModulePrefix = ((UsesNode) node).getGrouping().getPrefix();

            result = getHiglightingOffset(node, nodeModulePrefix);
            return result;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.cisco.yangide.editor.editors.SemanticHighlighting#getHiglightingLength(com.cisco.
         * yangide.core.dom.ASTNode)
         */
        @Override
        public int getHiglightingLength(ASTNode node) {
            int result = -1;

            String nodeModulePrefix = null;
            if (node instanceof UsesNode)
                nodeModulePrefix = ((UsesNode) node).getGrouping().getPrefix();

            result = getHiglightingLength(node, nodeModulePrefix);
            return result;
        }
    }

    /**
     * Semantic highlighting for fields.
     */
    private static final class PrefixHighlighting extends SemanticHighlighting {

        /*
         * @see org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlighting#getPreferenceKey()
         */
        @Override
        public String getPreferenceKey() {
            return PREFIX;
        }

        /*
         * @see org.eclipse.jdt.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextColor()
         */
        @Override
        public RGB getDefaultTextColor() {
            return new RGB(0, 192, 192);
        }

        /*
         * @see
         * org.eclipse.jdt.internal.ui.javaeditor.ISemanticHighlighting#getDefaultTextStyleBold()
         */
        @Override
        public boolean isBoldByDefault() {
            return false;
        }

        /*
         * @see org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlighting#isItalicByDefault()
         */
        @Override
        public boolean isItalicByDefault() {
            return false;
        }

        /*
         * @see org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlighting#isEnabledByDefault()
         */
        @Override
        public boolean isEnabledByDefault() {
            return true;
        }

        /*
         * @see org.eclipse.jdt.internal.ui.javaeditor.ISemanticHighlighting#getDisplayName()
         */
        @Override
        public String getDisplayName() {
            return YangPreferencesMessages.SemanticHighlighting_prefix;
        }

        /*
         * @see
         * org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlighting#consumes(org.eclipse.jdt.
         * internal.ui.javaeditor.SemanticToken)
         */
        @Override
        public boolean consumes(ASTNode node) {

            String thisModulePrefix = null;
            ASTNode thisModule = node.getModule();
            if (thisModule instanceof SubModule)
                thisModulePrefix = null;// ((SubModule) thisModule).getParentPrefix();
            else
                thisModulePrefix = ((Module) thisModule).getPrefix().getValue();

            if (node instanceof TypeReference) {
                String typeWholeName = ((TypeReference) node).getName();
                String typePrefix = ((TypeReference) node).getType().getPrefix();

                return !YangTypeUtil.isBuiltInType(typeWholeName) && !typePrefix.equals(thisModulePrefix);

            }

            if (node instanceof UsesNode) {
                String usesPrefix = ((UsesNode) node).getGrouping().getPrefix();
                return !usesPrefix.equals(thisModulePrefix);
            }

            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.cisco.yangide.editor.editors.SemanticHighlighting#getHiglightingOffset(com.cisco.
         * yangide.core.dom.ASTNode)
         */
        @Override
        public int getHiglightingOffset(ASTNode node) {
            if (node instanceof ASTNamedNode)
                return ((ASTNamedNode) node).getNameStartPosition();

            return -1;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.cisco.yangide.editor.editors.SemanticHighlighting#getHiglightingLength(com.cisco.
         * yangide.core.dom.ASTNode)
         */
        @Override
        public int getHiglightingLength(ASTNode node) {
            if (node instanceof TypeReference)
                return ((TypeReference) node).getType().getPrefix().length();

            if (node instanceof UsesNode)
                return ((UsesNode) node).getGrouping().getPrefix().length();

            return -1;
        }
    }

    /**
     * A named preference that controls the given semantic highlighting's color.
     */
    public static String getColorPreferenceKey(SemanticHighlighting semanticHighlighting) {
        return PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX + semanticHighlighting.getPreferenceKey()
                + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_COLOR_SUFFIX;
    }

    /**
     * A named preference that controls if the given semantic highlighting has the text attribute
     * bold.
     */
    public static String getBoldPreferenceKey(SemanticHighlighting semanticHighlighting) {
        return PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX + semanticHighlighting.getPreferenceKey()
                + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_BOLD_SUFFIX;
    }

    /**
     * A named preference that controls if the given semantic highlighting has the text attribute
     * italic.
     */
    public static String getItalicPreferenceKey(SemanticHighlighting semanticHighlighting) {
        return PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX + semanticHighlighting.getPreferenceKey()
                + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_ITALIC_SUFFIX;
    }

    /**
     * A named preference that controls if the given semantic highlighting has the text attribute
     * strikethrough.
     */
    public static String getStrikethroughPreferenceKey(SemanticHighlighting semanticHighlighting) {
        return PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX + semanticHighlighting.getPreferenceKey()
                + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_STRIKETHROUGH_SUFFIX;
    }

    /**
     * A named preference that controls if the given semantic highlighting has the text attribute
     * underline.
     */
    public static String getUnderlinePreferenceKey(SemanticHighlighting semanticHighlighting) {
        return PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX + semanticHighlighting.getPreferenceKey()
                + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_UNDERLINE_SUFFIX;
    }

    /**
     * A named preference that controls if the given semantic highlighting is enabled.
     */
    public static String getEnabledPreferenceKey(SemanticHighlighting semanticHighlighting) {
        return PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX + semanticHighlighting.getPreferenceKey()
                + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_ENABLED_SUFFIX;
    }

    /**
     * @return The semantic highlightings, the order defines the precedence of matches, the first
     * match wins.
     */
    public static SemanticHighlighting[] getSemanticHighlightings() {
        if (fgSemanticHighlightings == null)
            fgSemanticHighlightings = new SemanticHighlighting[] { new TypeHighlighting(), new GroupingHighlighting(),
                    new PrefixHighlighting() };
        return fgSemanticHighlightings;
    }

    /**
     * Initialize default preferences in the given preference store.
     * 
     * @param store The preference store
     * TODO refactor and call from PreferenceInitializer
     */
    public static void initDefaults(IPreferenceStore store) {
        SemanticHighlighting[] semanticHighlightings = getSemanticHighlightings();
        for (int i = 0, n = semanticHighlightings.length; i < n; i++) {
            SemanticHighlighting semanticHighlighting = semanticHighlightings[i];
            setDefaultAndFireEvent(store, SemanticHighlightings.getColorPreferenceKey(semanticHighlighting),
                    semanticHighlighting.getDefaultTextColor());
            store.setDefault(SemanticHighlightings.getBoldPreferenceKey(semanticHighlighting),
                    semanticHighlighting.isBoldByDefault());
            store.setDefault(SemanticHighlightings.getItalicPreferenceKey(semanticHighlighting),
                    semanticHighlighting.isItalicByDefault());
            store.setDefault(SemanticHighlightings.getStrikethroughPreferenceKey(semanticHighlighting),
                    semanticHighlighting.isStrikethroughByDefault());
            store.setDefault(SemanticHighlightings.getUnderlinePreferenceKey(semanticHighlighting),
                    semanticHighlighting.isUnderlineByDefault());
            store.setDefault(SemanticHighlightings.getEnabledPreferenceKey(semanticHighlighting),
                    semanticHighlighting.isEnabledByDefault());
        }

    }

    /**
     * Tests whether <code>event</code> in <code>store</code> affects the enablement of semantic
     * highlighting.
     */
    public static boolean affectsEnablement(IPreferenceStore store, PropertyChangeEvent event) {
        String relevantKey = null;
        SemanticHighlighting[] highlightings = getSemanticHighlightings();
        for (int i = 0; i < highlightings.length; i++) {
            if (event.getProperty().equals(getEnabledPreferenceKey(highlightings[i]))) {
                relevantKey = event.getProperty();
                break;
            }
        }
        if (relevantKey == null)
            return false;

        for (int i = 0; i < highlightings.length; i++) {
            String key = getEnabledPreferenceKey(highlightings[i]);
            if (key.equals(relevantKey))
                continue;
            if (store.getBoolean(key))
                return false; // another is still enabled or was enabled before
        }

        // all others are disabled, so toggling relevantKey affects the enablement
        return true;
    }

    /**
     * Tests whether semantic highlighting is currently enabled.
     */
    public static boolean isEnabled(IPreferenceStore store) {
        SemanticHighlighting[] highlightings = getSemanticHighlightings();
        boolean enable = false;
        for (int i = 0; i < highlightings.length; i++) {
            String enabledKey = getEnabledPreferenceKey(highlightings[i]);
            if (store.getBoolean(enabledKey)) {
                enable = true;
                break;
            }
        }

        return enable;
    }

    /**
     * Sets the default value and fires a property change event if necessary.
     */
    private static void setDefaultAndFireEvent(IPreferenceStore store, String key, RGB newValue) {
        RGB oldValue = null;
        if (store.isDefault(key))
            oldValue = PreferenceConverter.getDefaultColor(store, key);

        PreferenceConverter.setDefault(store, key, newValue);

        if (oldValue != null && !oldValue.equals(newValue))
            store.firePropertyChangeEvent(key, oldValue, newValue);
    }

    /**
     * Do not instantiate
     */
    private SemanticHighlightings() {
    }
}
