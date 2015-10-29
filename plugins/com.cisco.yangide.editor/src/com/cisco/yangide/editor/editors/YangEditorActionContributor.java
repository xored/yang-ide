/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.editor.editors;

import java.util.List;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @author Alexey Kholupko
 */
public class YangEditorActionContributor extends BasicTextEditorActionContributor {

    @Override
    public void setActiveEditor(IEditorPart part) {
        if (getActiveEditorPart() == part) {
            return;
        }
        super.setActiveEditor(part);

        ITextEditor textEditor = null;
        if (part instanceof ITextEditor) {
            textEditor = (ITextEditor) part;
        }
        IActionBars actionBars = getActionBars();
        actionBars.setGlobalActionHandler(
                "com.cisco.yangide.editor.actions.shiftRight", getAction(textEditor, "ShiftRight")); //$NON-NLS-1$
        actionBars.setGlobalActionHandler(
                "com.cisco.yangide.editor.actions.shiftLeft", getAction(textEditor, "ShiftLeft")); //$NON-NLS-1$
        actionBars.setGlobalActionHandler("com.cisco.yangide.editor.actions.ToggleComment",
                getAction(textEditor, "ToggleComment")); //$NON-NLS-1$
        actionBars.setGlobalActionHandler("com.cisco.yangide.editor.actions.AddBlockComment",
                getAction(textEditor, "AddBlockComment")); //$NON-NLS-1$
        actionBars.setGlobalActionHandler("com.cisco.yangide.editor.actions.RemoveBlockComment",
                getAction(textEditor, "RemoveBlockComment")); //$NON-NLS-1$
        actionBars.setGlobalActionHandler(
                "com.cisco.yangide.editor.actions.FormatDocument", getAction(textEditor, "FormatDocument")); //$NON-NLS-1$
        actionBars.setGlobalActionHandler(
                "com.cisco.yangide.editor.actions.OpenDeclaration", getAction(textEditor, "OpenDeclaration")); //$NON-NLS-1$

        if (part instanceof YangEditor) {
            List<ActionGroup> actionGroups = ((YangEditor) part).getActionGroups();
            for (ActionGroup actionGroup : actionGroups) {
                actionGroup.fillActionBars(actionBars);
            }
        }
    }
}
