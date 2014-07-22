/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.ui.preferences;

/**
 * Constant definitions for plug-in preferences wizard generated, might be usefull later
 */
public class YangPreferenceConstants {

    /**
     * A named preference that controls whether the 'smart paste' feature is enabled.
     * <p>
     * Value is of type <code>Boolean</code>.
     * </p>
     *
     * @since 2.1
     */
    public final static String EDITOR_SMART_PASTE = "smartPaste"; //$NON-NLS-1$

    /**
     * A named preference that controls whether the 'close brackets' feature is enabled.
     * <p>
     * Value is of type <code>Boolean</code>.
     * </p>
     *
     * @since 2.1
     */
    public final static String EDITOR_CLOSE_BRACES = "closeBraces"; //$NON-NLS-1$
    /**
     * A named preference that controls the smart tab behavior.
     * <p>
     * Value is of type <code>Boolean</code>.
     *
     * @since 3.0
     */
    public static final String EDITOR_SMART_TAB = "smart_tab"; //$NON-NLS-1$
    /**
     * A named preference that controls whether on Enter key the indentation should be smart or the
     * same as previous line.
     * <p>
     * Value is of type <code>Boolean</code>.
     * </p>
     *
     * @since 3.7
     */
    public final static String EDITOR_SMART_INDENT_AFTER_NEWLINE = "smartIndentAfterNewline"; //$NON-NLS-1$

    public final static String M2E_PLUGIN_CLEAN_TARGET = "m2ePluginCleanTarget";

    // formatter preferences
    public final static String FMT_INDENT_SPACE = "fmtIndentSpace";
    public final static String FMT_INDENT_WIDTH = "fmtIndentWidth";
    public final static String FMT_COMMENT = "fmtComment";
    public final static String FMT_STRING = "fmtString";
    public final static String FMT_MAX_LINE_LENGTH = "fmtMaxLineLength";
    public final static String FMT_COMPACT_IMPORT = "fmtCompactImport";
}
