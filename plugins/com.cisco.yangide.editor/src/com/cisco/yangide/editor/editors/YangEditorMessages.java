/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */ 
package com.cisco.yangide.editor.editors;

import org.eclipse.osgi.util.NLS;

/**
 * @author Alexey Kholupko
 *
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
    
    

}
