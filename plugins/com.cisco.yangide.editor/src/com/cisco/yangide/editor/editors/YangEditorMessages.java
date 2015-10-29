/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.editor.editors;

import org.eclipse.osgi.util.NLS;

/**
 * @author Alexey Kholupko
 */
public class YangEditorMessages extends NLS {
    private static final String BUNDLE_NAME = YangEditorMessages.class.getName();

    /**
     * @return the bundleName
     */
    public static String getBundleName() {
        return BUNDLE_NAME;
    }

    static {
        NLS.initializeMessages(BUNDLE_NAME, YangEditorMessages.class);
    }

    public static String ToggleComment_error_title;
    public static String ToggleComment_error_message;
    public static String ContentFormat_label;
    public static String ContentFormat_tooltip;
    public static String ContentFormat_image;
    public static String ContentFormat_description;

    public static String ToggleComment_label;
    public static String ToggleComment_tooltip;
    public static String ToggleComment_description;

    public static String AddBlockComment_label;
    public static String AddBlockComment_tooltip;
    public static String AddBlockComment_description;

    public static String RemoveBlockComment_label;
    public static String RemoveBlockComment_tooltip;
    public static String RemoveBlockComment_description;

    public static String OpenDeclaration_label;
    public static String OpenDeclaration_tooltip;
    public static String OpenDeclaration_image;
    public static String OpenDeclaration_description;

}
