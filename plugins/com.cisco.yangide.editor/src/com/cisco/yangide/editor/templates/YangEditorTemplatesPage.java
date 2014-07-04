/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.templates;

import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;

import com.cisco.yangide.ui.YangUIPlugin;

/**
 * @author Alexey Kholupko
 */
public class YangEditorTemplatesPage extends TemplatePreferencePage {

    public YangEditorTemplatesPage() {
        setPreferenceStore(YangUIPlugin.getDefault().getPreferenceStore());
        setTemplateStore(YangTemplateAccess.getDefault().getTemplateStore());
        setContextTypeRegistry(YangTemplateAccess.getDefault().getContextTypeRegistry());
    }

    /*
     * @see org.eclipse.ui.texteditor.templates.TemplatePreferencePage#updateViewerInput()
     */
    protected void updateViewerInput() {
        IStructuredSelection selection = (IStructuredSelection) getTableViewer().getSelection();
        SourceViewer viewer = getViewer();

        if (selection.size() == 1 && selection.getFirstElement() instanceof TemplatePersistenceData) {
            TemplatePersistenceData data = (TemplatePersistenceData) selection.getFirstElement();
            Template template = data.getTemplate();
            viewer.getDocument().set(template.getPattern());

        } else {
            viewer.getDocument().set("");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.texteditor.templates.TemplatePreferencePage#isShowFormatterSetting()
     */
    protected boolean isShowFormatterSetting() {
        return false;
    }

}
