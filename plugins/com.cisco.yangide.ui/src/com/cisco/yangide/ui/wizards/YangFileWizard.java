/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import com.cisco.yangide.ui.YangUIPlugin;
import com.cisco.yangide.ui.internal.IYangUIConstants;
import com.cisco.yangide.ui.internal.YangUIImages;

/**
 * @author Konstantin Zaitsev
 * @date Jul 15, 2014
 */
public class YangFileWizard extends Wizard implements INewWizard {

    private YangFileWizardPage yangPage;
    private WizardNewFileCreationPage filePage;
    private IWorkbench workbench;

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
        this.workbench = workbench;
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
        if (yangPage.getModule().isEmpty()) {
            yangPage.init();
        }
        try {
            getContainer().run(false, false, new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    IFile file = filePage.createNewFile();
                    try {
                        file.setContents(getTemplateContent(), true, false, monitor);
                    } catch (CoreException | IOException e) {
                        YangUIPlugin.log(e);
                    }

                    BasicNewResourceWizard.selectAndReveal(file, workbench.getActiveWorkbenchWindow());

                    // Open editor on new file.
                    IWorkbenchWindow dw = workbench.getActiveWorkbenchWindow();
                    try {
                        if (dw != null) {
                            IWorkbenchPage page = dw.getActivePage();
                            if (page != null) {
                                IDE.openEditor(page, file, true);
                            }
                        }
                    } catch (PartInitException e) {
                        YangUIPlugin.log(e);
                    }
                }
            });
        } catch (InvocationTargetException | InterruptedException e) {
            YangUIPlugin.log(e);
        }
        return true;
    }

    /**
     * @return InputStream of template with replaced placeholders.
     * @throws IOException read errors
     */
    private InputStream getTemplateContent() throws IOException {
        StringBuilder sb = new StringBuilder();

        char[] buff = new char[1024];
        int len = 0;
        Path templatePath = new Path("resources/yang/new_yang_file.yang");
        try (InputStreamReader in = new InputStreamReader(FileLocator.openStream(YangUIPlugin.getDefault().getBundle(),
                templatePath, false), "UTF-8")) {
            while ((len = in.read(buff)) > 0) {
                sb.append(buff, 0, len);
            }
        }
        String str = sb.toString();
        str = str.replaceAll("%MODULE%", yangPage.getModule());
        str = str.replaceAll("%NAMESPACE%", yangPage.getNamespace());
        str = str.replaceAll("%PREFIX%", yangPage.getPrefix());
        str = str.replaceAll("%REVISION%", yangPage.getRevision());
        str = str.replaceAll("%REVISION_DESC%", yangPage.getRevisionDesc());

        return new ByteArrayInputStream(str.toString().getBytes("UTF-8"));
    }
}
