/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import com.cisco.yangide.ui.internal.IYangUIConstants;
import com.cisco.yangide.ui.internal.YangUIImages;

/**
 * @author Konstantin Zaitsev
 * @date Jul 15, 2014
 */
public class YangFileWizard extends Wizard implements INewWizard {

    private YangFileWizardPage yangPage;
    private WizardNewFileCreationPage filePage;

    /**
     * Constructor.
     */
    public YangFileWizard() {
        setWindowTitle("New YANG File");
        setDefaultPageImageDescriptor(YangUIImages.getImageDescriptor(IYangUIConstants.IMG_NEW_FILE_WIZ));
        setNeedsProgressMonitor(true);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        filePage = new WizardNewFileCreationPage("filePage", selection);
        filePage.setAllowExistingResources(false);
        filePage.setFileExtension("yang");
        filePage.setTitle("YANG File");
        filePage.setDescription("Create a new YANG file");

        yangPage = new YangFileWizardPage(filePage);
        addPage(filePage);
        addPage(yangPage);
    }

    @Override
    public boolean performFinish() {
        return true;
    }
}
