/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.editor.compare;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

import com.cisco.yangide.editor.YangEditorPlugin;
import com.cisco.yangide.editor.editors.YangColorManager;
import com.cisco.yangide.editor.editors.YangSourceViewerConfiguration;
import com.cisco.yangide.editor.preferences.YangDocumentSetupParticipant;

/**
 * @author Konstantin Zaitsev
 * @date Jul 31, 2014
 */
public class YangFileMergeViewer extends TextMergeViewer {

    public YangFileMergeViewer(Composite parent, CompareConfiguration config) {
        super(parent, config);
    }

    @Override
    protected void configureTextViewer(TextViewer textViewer) {
        SourceViewer yangSourceViewer = (SourceViewer) textViewer;
        YangColorManager colorManager = new YangColorManager(false);

        IPreferenceStore store = new ChainedPreferenceStore(new IPreferenceStore[] {
                YangEditorPlugin.getDefault().getCombinedPreferenceStore(), EditorsUI.getPreferenceStore() });

        YangSourceViewerConfiguration configuration = new YangSourceViewerConfiguration(store, colorManager, null);

        yangSourceViewer.configure(configuration);
        yangSourceViewer.setEditable(false);
        Font font = JFaceResources.getFont(JFaceResources.TEXT_FONT);
        yangSourceViewer.getTextWidget().setFont(font);
    }

    @Override
    protected IDocumentPartitioner getDocumentPartitioner() {
        return YangDocumentSetupParticipant.createDocumentPartitioner();
    }

    @Override
    protected String getDocumentPartitioning() {
        return YangDocumentSetupParticipant.YANG_PARTITIONING;
    }

    @Override
    public String getTitle() {
        return "YANG File Compare Viewer";
    }
}
