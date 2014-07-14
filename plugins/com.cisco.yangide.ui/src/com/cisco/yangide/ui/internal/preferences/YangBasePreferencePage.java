/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.ui.internal.preferences;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.cisco.yangide.ui.YangUIPlugin;
import com.cisco.yangide.ui.nls.Messages;
import com.cisco.yangide.ui.preferences.YangPreferenceConstants;

/**
 * @author Konstantin Zaitsev
 * @date Jul 14, 2014
 */
public class YangBasePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private Button cleanBtn;

    @Override
    public void init(IWorkbench workbench) {
        this.setPreferenceStore(YangUIPlugin.getDefault().getPreferenceStore());
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite pageArea = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, true);
        pageArea.setLayout(layout);

        cleanBtn = new Button(pageArea, SWT.CHECK);
        cleanBtn.setText(Messages.pref_Base_cleanCodeGen);
        cleanBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        cleanBtn.setSelection(getPreferenceStore().getBoolean(YangPreferenceConstants.M2E_PLUGIN_CLEAN_TARGET));
        Dialog.applyDialogFont(pageArea);

        return pageArea;
    }

    @Override
    public boolean performOk() {
        getPreferenceStore().setValue(YangPreferenceConstants.M2E_PLUGIN_CLEAN_TARGET, cleanBtn.getSelection());
        return true;
    }

    @Override
    protected void performDefaults() {
        cleanBtn.setSelection(getPreferenceStore().getDefaultBoolean(YangPreferenceConstants.M2E_PLUGIN_CLEAN_TARGET));
        super.performDefaults();
    }
}
