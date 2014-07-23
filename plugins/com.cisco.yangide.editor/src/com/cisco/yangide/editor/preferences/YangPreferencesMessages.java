/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * @author Alexey Kholupko
 */
public class YangPreferencesMessages extends NLS {
    private static final String BUNDLE_NAME = "com.cisco.yangide.editor.preferences.YangPreferencesMessages";//$NON-NLS-1$    

    public static String YANGEditorPreferencePage_strings;
    public static String YANGEditorPreferencePage_keywords;
    public static String YANGEditorPreferencePage_comments;
    public static String YANGEditorPreferencePage_identifiers;
    public static String YANGEditorPreferencePage_types;
    public static String YANGEditorPreferencePage_numbers;

    public static String YANGEditorPreferencePage_color;
    public static String YANGEditorPreferencePage_bold;
    public static String YANGEditorPreferencePage_italic;
    public static String YANGEditorPreferencePage_strikethrough;
    public static String YANGEditorPreferencePage_underline;
    public static String YANGEditorPreferencePage_enable;
    public static String YANGEditorPreferencePage_preview;
    public static String YANGEditorPreferencePage_highlightMatchingBrackets;
    
    public static String SemanticHighlighting_type;
    public static String SemanticHighlighting_grouping;
    public static String SemanticHighlighting_prefix;

    public static String YANGEditorColoringConfigurationBlock_link;
    public static String YANGEditorPreferencePage_coloring_element;
    public static String YANGEditorPreferencePage_colors;

    public static String YANGEditorPreferencePage_invalid_input;
    public static String YANGEditorPreferencePage_empty_input;

    static {
        // load message values from bundle file
        NLS.initializeMessages(BUNDLE_NAME, YangPreferencesMessages.class);
    }

}
