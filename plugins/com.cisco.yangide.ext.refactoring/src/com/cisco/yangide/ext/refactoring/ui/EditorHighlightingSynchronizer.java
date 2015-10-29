/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.refactoring.ui;

import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;

import com.cisco.yangide.editor.editors.YangEditor;
import com.cisco.yangide.editor.editors.YangSourceViewer;

/**
 * @author Konstantin Zaitsev
 * @date Aug 4, 2014
 */
public class EditorHighlightingSynchronizer implements ILinkedModeListener {

    private final YangEditor editor;
    private final boolean fWasOccurrencesOn;

    /**
     * Creates a new synchronizer.
     *
     * @param editor the java editor the occurrences markers of which will be synchronized with the
     * linked mode
     */
    public EditorHighlightingSynchronizer(YangEditor editor) {
        this.editor = editor;
        fWasOccurrencesOn = editor.isMarkingOccurrences();

        if (fWasOccurrencesOn && !isEditorDisposed()) {
            editor.uninstallOccurrencesFinder();
        }

        if (!isEditorDisposed()) {
            ((YangSourceViewer) editor.getViewer()).getReconciler().uninstall();
        }
    }

    @Override
    public void left(LinkedModeModel environment, int flags) {
        if (fWasOccurrencesOn && !isEditorDisposed()) {
            editor.installOccurrencesFinder(true);
        }
        if (!isEditorDisposed()) {
            ((YangSourceViewer) editor.getViewer()).getReconciler().install(editor.getViewer());
        }
    }

    private boolean isEditorDisposed() {
        return editor == null || editor.getSelectionProvider() == null;
    }

    @Override
    public void suspend(LinkedModeModel environment) {
    }

    @Override
    public void resume(LinkedModeModel environment, int flags) {
    }

}
