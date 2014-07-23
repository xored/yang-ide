/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors.text.hover;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.internal.text.html.BrowserInformationControl;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Konstantin Zaitsev
 * @date Jul 23, 2014
 */
@SuppressWarnings("restriction")
public final class PresenterControlCreator extends AbstractReusableInformationControlCreator {

    @Override
    public IInformationControl doCreateInformationControl(Shell parent) {
        if (BrowserInformationControl.isAvailable(parent)) {
            ToolBarManager tbm = new ToolBarManager(SWT.FLAT);
            String font = JFaceResources.DEFAULT_FONT;
            return new BrowserInformationControl(parent, font, tbm);
        } else {
            return new DefaultInformationControl(parent, true);
        }
    }
}