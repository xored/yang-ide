/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.compare;

import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.IViewerCreator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

import com.cisco.yangide.editor.YangEditorPlugin;
import com.cisco.yangide.editor.editors.YangColorManager;
import com.cisco.yangide.editor.editors.YangSourceViewer;
import com.cisco.yangide.editor.editors.YangSourceViewerConfiguration;
import com.cisco.yangide.editor.preferences.YangDocumentSetupParticipant;

/**
 * @author Konstantin Zaitsev
 * @date Jul 31, 2014
 */
public class YangFileViewerCreator implements IViewerCreator {

    @Override
    public Viewer createViewer(Composite parent, CompareConfiguration config) {
        YangSourceViewer yangSourceViewer = new YangSourceViewer(parent, null, null, false, SWT.BORDER | SWT.V_SCROLL
                | SWT.H_SCROLL) {
            @Override
            public void setInput(Object input) {
                if (input instanceof IStreamContentAccessor) {
                    IDocument document = new Document(getContentFromStream((IStreamContentAccessor) input));
                    new YangDocumentSetupParticipant().setup(document);
                    setDocument(document);
                } else {
                    super.setInput(input);
                }
            }
        };
        YangColorManager colorManager = new YangColorManager(false);

        IPreferenceStore store = new ChainedPreferenceStore(new IPreferenceStore[] {
                YangEditorPlugin.getDefault().getCombinedPreferenceStore(), EditorsUI.getPreferenceStore() });

        YangSourceViewerConfiguration configuration = new YangSourceViewerConfiguration(store, colorManager, null);

        yangSourceViewer.configure(configuration);
        yangSourceViewer.setEditable(false);
        Font font = JFaceResources.getFont(JFaceResources.TEXT_FONT);
        yangSourceViewer.getTextWidget().setFont(font);

        return yangSourceViewer;
    }

    private String getContentFromStream(IStreamContentAccessor input) {
        char[] cbuf = new char[1024];
        int len = 0;
        StringBuilder sb = new StringBuilder();
        try (InputStreamReader in = new InputStreamReader(input.getContents(), "UTF-8")) {
            while ((len = in.read(cbuf)) > 0) {
                sb.append(cbuf, 0, len);
            }
        } catch (IOException | CoreException e) {
            YangEditorPlugin.log(e);
        }
        return sb.toString();
    }
}
