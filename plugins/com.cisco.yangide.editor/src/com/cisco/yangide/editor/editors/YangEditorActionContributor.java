/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

/**
 * @author Alexey Kholupko
 */
public class YangEditorActionContributor extends BasicTextEditorActionContributor {

    public YangEditorActionContributor() {
        super();
    }

    /*
     * @see IEditorActionBarContributor#setActiveEditor(IEditorPart)
     */
    @Override
    public void setActiveEditor(IEditorPart part) {
        super.setActiveEditor(part);

        ITextEditor textEditor = null;
        if (part instanceof ITextEditor)
            textEditor = (ITextEditor) part;

        // Source menu.
        IActionBars bars = getActionBars();
        bars.setGlobalActionHandler(
                "com.cisco.yangide.editor.actions.ToggleComment", getAction(textEditor, "ToggleComment")); //$NON-NLS-1$
        bars.setGlobalActionHandler(
                "com.cisco.yangide.editor.actions.AddBlockComment", getAction(textEditor, "AddBlockComment")); //$NON-NLS-1$
        bars.setGlobalActionHandler(
                "com.cisco.yangide.editor.actions.RemoveBlockComment", getAction(textEditor, "RemoveBlockComment")); //$NON-NLS-1$

        IAction action = getAction(textEditor, ITextEditorActionConstants.REFRESH);
        bars.setGlobalActionHandler(ITextEditorActionConstants.REFRESH, action);
    }
}
