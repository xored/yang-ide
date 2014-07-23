/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */ 
package com.cisco.yangide.editor.editors.text.hover;

import org.eclipse.jface.internal.text.html.BrowserInformationControl;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.EditorsUI;

public final class HoverControlCreator extends AbstractReusableInformationControlCreator {

    private final IInformationControlCreator fInformationPresenterControlCreator;

    public HoverControlCreator(IInformationControlCreator informationPresenterControlCreator) {
        this(informationPresenterControlCreator, false);
    }

    public HoverControlCreator(IInformationControlCreator informationPresenterControlCreator,
            boolean additionalInfoAffordance) {
        fInformationPresenterControlCreator = informationPresenterControlCreator;
    }

    @SuppressWarnings("restriction")
    @Override
    public IInformationControl doCreateInformationControl(Shell parent) {
        if (BrowserInformationControl.isAvailable(parent)) {
            String font = JFaceResources.DEFAULT_FONT;
            BrowserInformationControl iControl = new BrowserInformationControl(parent, font,
                    EditorsUI.getTooltipAffordanceString()) {
                @Override
                public IInformationControlCreator getInformationPresenterControlCreator() {
                    return fInformationPresenterControlCreator;
                }
            };
            return iControl;
        } else {
            return new DefaultInformationControl(parent, EditorsUI.getTooltipAffordanceString());
        }
    }
}