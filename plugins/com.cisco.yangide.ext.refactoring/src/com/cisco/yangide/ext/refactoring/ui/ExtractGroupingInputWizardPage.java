/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.refactoring.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.cisco.yangide.ext.refactoring.code.ExtractGroupingRefactoring;
import com.cisco.yangide.ext.refactoring.nls.Messages;

/**
 * @author Konstantin Zaitsev
 * @date Aug 19, 2014
 */
public class ExtractGroupingInputWizardPage extends UserInputWizardPage {
    private Text groupNameTxt;

    public ExtractGroupingInputWizardPage() {
        super("ExtractGroupingInputPage"); //$NON-NLS-1$
        setDescription(Messages.ExtractGroupingInputWizardPage_description);
    }

    @Override
    public void createControl(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        GridLayoutFactory.swtDefaults().numColumns(2).spacing(0, 5).applyTo(content);

        new Label(content, SWT.NONE).setText(Messages.ExtractGroupingInputWizardPage_groupingNameLabel);
        groupNameTxt = new Text(content, SWT.BORDER);
        groupNameTxt.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setState();
            }
        });
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(groupNameTxt);

        Dialog.applyDialogFont(content);
        setControl(content);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            loadState();
        }
        super.setVisible(visible);
    }

    private void setState() {
        ExtractGroupingRefactoring refactoring = (ExtractGroupingRefactoring) getRefactoring();
        refactoring.setGroupName(groupNameTxt.getText());
    }

    private void loadState() {
        ExtractGroupingRefactoring refactoring = (ExtractGroupingRefactoring) getRefactoring();
        if (refactoring.getGroupName() == null) {
            refactoring.setGroupName("extracted"); //$NON-NLS-1$
        }
        groupNameTxt.setText(refactoring.getGroupName());
    }
}
