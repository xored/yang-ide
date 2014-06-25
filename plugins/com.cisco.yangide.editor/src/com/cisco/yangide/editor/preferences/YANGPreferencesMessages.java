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
 *
 */
public class YANGPreferencesMessages extends NLS {
    private static final String BUNDLE_NAME = "com.cisco.yangide.editor.preferences.YANGPreferencesMessages";//$NON-NLS-1$    
    public static String YANGEditorPreferencePage_strings;
    public static String YANGEditorPreferencePage_keywords;
    public static String YANGEditorPreferencePage_comments;
    public static String YANGEditorPreferencePage_others;

    static {
        // load message values from bundle file
        NLS.initializeMessages(BUNDLE_NAME, YANGPreferencesMessages.class);
    }
    
    
}
