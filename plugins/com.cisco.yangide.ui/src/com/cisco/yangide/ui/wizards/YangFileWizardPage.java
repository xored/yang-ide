/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.ui.wizards;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

/**
 * @author Konstantin Zaitsev
 * @date Jul 15, 2014
 */
public class YangFileWizardPage extends WizardPage {
    private DateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
    private Text moduleTxt;
    private Text namespaceTxt;
    private Text prefixTxt;
    private Text revisionTxt;
    private Text revisionDescTxt;
    private WizardNewFileCreationPage filePage;

    protected YangFileWizardPage(WizardNewFileCreationPage filePage) {
        super("yangFilePage");
        this.filePage = filePage;
        setTitle("YANG File");
        setDescription("Create a new YANG file");
    }

    @Override
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);
        Composite container = new Composite(parent, SWT.NULL);
        container.setLayout(new GridLayout(1, false));

        Composite group1 = new Composite(container, SWT.NONE);
        group1.setLayout(new GridLayout(2, false));
        group1.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        new Label(group1, SWT.NONE).setText("Module Name:");
        moduleTxt = new Text(group1, SWT.BORDER);
        moduleTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        moduleTxt.setText("module1");

        new Label(group1, SWT.NONE).setText("Namespace:");
        namespaceTxt = new Text(group1, SWT.BORDER);
        namespaceTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        namespaceTxt.setText("module1");

        new Label(group1, SWT.NONE).setText("Prefix:");
        prefixTxt = new Text(group1, SWT.BORDER);
        prefixTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        prefixTxt.setText("module1");

        new Label(group1, SWT.NONE).setText("Revision:");
        revisionTxt = new Text(group1, SWT.BORDER);
        revisionTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        revisionTxt.setText(DF.format(new Date()));

        Label revisionDescLabel = new Label(group1, SWT.NONE);
        revisionDescLabel.setText("Revision Description:");
        revisionDescLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));

        revisionDescTxt = new Text(group1, SWT.MULTI | SWT.BORDER | SWT.WRAP);
        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1);
        gridData.heightHint = 50;
        revisionDescTxt.setLayoutData(gridData);
        revisionDescTxt.setText("Initial revision");

        setControl(container);
    }

    @Override
    public void setVisible(boolean visible) {
        String name = filePage.getFileName();
        if (name.indexOf('.') > 0) {
            name = name.substring(0, name.indexOf('.'));
        }
        moduleTxt.setText(name);
        namespaceTxt.setText("urn:opendaylight:" + name);
        prefixTxt.setText(name);
        super.setVisible(visible);
    }
}
