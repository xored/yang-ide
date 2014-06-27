/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Konstantin Zaitsev
 * @date Jun 27, 2014
 */
class YangProjectWizardPage extends WizardPage {

    private Text rootDirTxt;
    private Combo yangVersion;
    private Button exampleFileChk;

    /**
     * @param pageName
     */
    protected YangProjectWizardPage() {
        super("yangProjectPage");
        setTitle("YANG Tools Configuration");
        setDescription("Specify YANG Code Generators Parameters");
    }

    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        container.setLayout(new GridLayout(1, false));

        Composite group1 = new Composite(container, SWT.NONE);
        group1.setLayout(new GridLayout(2, false));
        group1.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

        new Label(group1, SWT.NONE).setText("YANG Tools Version:");
        yangVersion = new Combo(group1, SWT.BORDER);
        yangVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        new Label(group1, SWT.NONE).setText("YANG Files Root Directory:");
        rootDirTxt = new Text(group1, SWT.BORDER);
        rootDirTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        rootDirTxt.setText("main/src/yang");

        Composite group2 = new Composite(container, SWT.NONE);
        group2.setLayout(new GridLayout(2, false));
        group2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

        exampleFileChk = new Button(group2, SWT.CHECK);
        exampleFileChk.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        exampleFileChk.setText("Create Example YANG File");

        setControl(container);
    }

}
