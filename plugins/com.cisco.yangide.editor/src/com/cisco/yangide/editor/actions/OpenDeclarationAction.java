/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.actions;

import java.util.ResourceBundle;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.BaseReference;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.ModuleImport;
import com.cisco.yangide.core.dom.QName;
import com.cisco.yangide.core.dom.TypeReference;
import com.cisco.yangide.core.dom.UsesNode;
import com.cisco.yangide.core.indexing.ElementIndexInfo;
import com.cisco.yangide.core.indexing.ElementIndexType;
import com.cisco.yangide.core.model.YangModelManager;
import com.cisco.yangide.core.parser.YangParserUtil;
import com.cisco.yangide.editor.EditorUtility;
import com.cisco.yangide.editor.editors.YangEditor;
import com.cisco.yangide.ui.YangUIPlugin;

/**
 * Open type declaration.
 *
 * @author Konstantin Zaitsev
 * @date Jul 4, 2014
 */
public class OpenDeclarationAction extends TextEditorAction {

    public OpenDeclarationAction(ResourceBundle bundle, String prefix, ITextEditor editor) {
        super(bundle, prefix, editor);
    }

    @Override
    public void run() {
        YangEditor editor = (YangEditor) getTextEditor();

        try {
            ISelection selection = editor.getSelectionProvider().getSelection();
            Module module = YangParserUtil.parseYangFile(editor.getDocument().get().toCharArray());
            ASTNode node = module.getNodeAtPosition(((ITextSelection) selection).getOffset());

            ElementIndexInfo[] searchResult = null;

            if (node instanceof ModuleImport) {
                ModuleImport importNode = (ModuleImport) node;
                searchResult = YangModelManager.search(null, importNode.getRevision(), importNode.getName(),
                        ElementIndexType.MODULE, null, null);
            } else if (node instanceof TypeReference) {
                TypeReference ref = (TypeReference) node;
                QName type = ref.getType();
                searchResult = YangModelManager.search(type.getModule(), type.getRevision(), type.getName(),
                        ElementIndexType.TYPE, null, null);
                if (searchResult.length == 0) {
                    searchResult = YangModelManager.search(type.getModule(), type.getRevision(), type.getName(),
                            ElementIndexType.IDENTITY, null, null);
                }
            } else if (node instanceof UsesNode) {
                UsesNode usesNode = (UsesNode) node;
                QName ref = usesNode.getGrouping();
                searchResult = YangModelManager.search(ref.getModule(), ref.getRevision(), ref.getName(),
                        ElementIndexType.GROUPING, null, null);
            } else if (node instanceof BaseReference) {
                BaseReference base = (BaseReference) node;
                QName ref = base.getType();
                searchResult = YangModelManager.search(ref.getModule(), ref.getRevision(), ref.getName(),
                        ElementIndexType.IDENTITY, null, null);
            }

            if (searchResult != null && searchResult.length > 0) {
                EditorUtility.openInEditor(searchResult[0]);
            }
        } catch (Exception e) {
            YangUIPlugin.log(e);
        }
    }
}
