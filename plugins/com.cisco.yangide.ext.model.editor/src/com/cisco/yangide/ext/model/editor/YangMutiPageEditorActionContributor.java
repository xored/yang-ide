/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.model.editor;

import org.eclipse.ui.IEditorPart;

import com.cisco.yangide.editor.editors.YangEditorActionContributor;

/**
 * @author Konstantin Zaitsev
 * @date Aug 27, 2014
 */
public class YangMutiPageEditorActionContributor extends YangEditorActionContributor {
    @Override
    public void setActiveEditor(IEditorPart part) {
        if (part instanceof YangMultiPageEditorPart) {
            super.setActiveEditor((IEditorPart) ((YangMultiPageEditorPart) part).getSelectedPage());
        }
    }
}
