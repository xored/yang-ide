/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
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
