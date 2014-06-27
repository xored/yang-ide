/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.ui.wizards;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Konstantin Zaitsev
 * @date Jun 27, 2014
 */
public class YangCodeGeneratorDialog extends TitleAreaDialog {

    private Text groupIdTxt;
    private Text artifactIdTxt;
    private Text versionTxt;
    private Text genClassNameTxt;
    private Text genOutputDirectoryTxt;

    private CodeGeneratorConfig config;

    public YangCodeGeneratorDialog(Shell shell) {
        this(shell, new CodeGeneratorConfig());
    }

    public YangCodeGeneratorDialog(Shell shell, CodeGeneratorConfig config) {
        super(shell);
        this.config = config;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Code Generator Configuration");
    }
    
    @Override
    public void create() {
        super.create();
        setTitle("Code Generator Configuration");
        setMessage("Specify Maven parameters for code generator configuration", IMessageProvider.INFORMATION);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));

        GridLayout layout = new GridLayout(2, false);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        container.setLayout(layout);

        groupIdTxt = createTextInput(container, "Maven Group ID:", config.getGroupId());
        artifactIdTxt = createTextInput(container, "Maven Artifact ID:", config.getArtifactId());
        versionTxt = createTextInput(container, "Maven Artifact Version:", config.getVersion());
        genClassNameTxt = createTextInput(container, "Generator Class Name:", config.getGenClassName());
        genOutputDirectoryTxt = createTextInput(container, "Generator Output Directory:", config.getGenOutputDirectory());

        return area;
    }

    @Override
    protected void okPressed() {
        config.setGroupId(groupIdTxt.getText());
        config.setArtifactId(artifactIdTxt.getText());
        config.setVersion(versionTxt.getText());
        config.setGenClassName(genClassNameTxt.getText());
        config.setGenOutputDirectory(genOutputDirectoryTxt.getText());
        super.okPressed();
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    private Text createTextInput(Composite parent, String label, String defValue) {
        new Label(parent, SWT.NONE).setText(label);

        Text text = new Text(parent, SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        text.setText(defValue != null ? defValue : "");
        return text;
    }

    /**
     * @return the config
     */
    public CodeGeneratorConfig getConfig() {
        return config;
    }
}
