/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.editor.templates;

import java.io.IOException;

import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;

import com.cisco.yangide.editor.YangEditorPlugin;
import com.cisco.yangide.ui.YangUIPlugin;

/**
 * @author Alexey Kholupko
 */
public class YangTemplateAccess {

    private static final String CUSTOM_TEMPLATES_KEY = "com.cisco.yangide.editor.customtemplates"; //$NON-NLS-1$

    private static YangTemplateAccess fgInstance;

    private TemplateStore fStore;

    private ContributionContextTypeRegistry fRegistry;

    private YangTemplateAccess() {
    }

    /**
     * Returns the shared instance.
     */
    public static YangTemplateAccess getDefault() {
        if (fgInstance == null) {
            fgInstance = new YangTemplateAccess();
        }
        return fgInstance;
    }

    public TemplateStore getTemplateStore() {
        if (fStore == null) {
            fStore = new ContributionTemplateStore(getContextTypeRegistry(), YangUIPlugin.getDefault()
                    .getPreferenceStore(), CUSTOM_TEMPLATES_KEY);
            try {
                fStore.load();
            } catch (IOException e) {
                YangEditorPlugin.log(e);
            }
        }
        return fStore;
    }

    /**
     * Returns this plug-in's context type registry.
     */
    public ContextTypeRegistry getContextTypeRegistry() {
        if (fRegistry == null) {
            // create and configure the contexts available in the template editor
            fRegistry = new ContributionContextTypeRegistry();
            fRegistry.addContextType(GeneralContextType.CONTEXT_TYPE);
        }
        return fRegistry;
    }

}
