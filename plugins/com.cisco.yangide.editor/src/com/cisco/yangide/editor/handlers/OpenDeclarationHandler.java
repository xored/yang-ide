/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.ModuleImport;
import com.cisco.yangide.core.indexing.ElementIndexInfo;
import com.cisco.yangide.core.indexing.ElementIndexType;
import com.cisco.yangide.core.internal.YangASTParser;
import com.cisco.yangide.core.model.YangModelManager;
import com.cisco.yangide.editor.EditorUtility;
import com.cisco.yangide.editor.editors.YangEditor;
import com.cisco.yangide.ui.YangUIPlugin;

/**
 * Open type declaration.
 * 
 * @author Konstantin Zaitsev
 * @date Jul 4, 2014
 */
public class OpenDeclarationHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
        if (editorPart != null && editorPart instanceof YangEditor) {
            YangEditor editor = (YangEditor) editorPart;
            YangASTParser parser = new YangASTParser();
            try {
                ISelection selection = editor.getSelectionProvider().getSelection();
                Module module = parser.parseYangFile(editor.getDocument().get().toCharArray());
                ASTNode node = module.getNodeAtPosition(((ITextSelection) selection).getOffset());
                if (node instanceof ModuleImport) {
                    ModuleImport moduleImport = (ModuleImport) node;
                    ElementIndexInfo[] result = YangModelManager.search(null, moduleImport.getName(),
                            ElementIndexType.MODULE, null);
                    if (result.length > 0) {
                        EditorUtility.openInEditor(result[0]);
                    }
                }

            } catch (Exception e) {
                YangUIPlugin.log(e);
            }
        }
        return null;
    }
}
