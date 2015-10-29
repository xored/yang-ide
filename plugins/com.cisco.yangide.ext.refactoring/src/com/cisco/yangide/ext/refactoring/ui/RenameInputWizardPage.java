/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.refactoring.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.cisco.yangide.ext.refactoring.rename.YangRenameProcessor;

/**
 * @author Konstantin Zaitsev
 * @date Jul 30, 2014
 */
public class RenameInputWizardPage extends UserInputWizardPage {
    // private Button updateReferences;
    private Text newNameTxt;
    private String initialValue;

    public RenameInputWizardPage(String description, String initialValue) {
        super("RenameInputPage");
        this.initialValue = initialValue == null ? "" : initialValue;
        setDescription(description);
    }

    @Override
    public void createControl(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(2, false));

        new Label(content, SWT.NONE).setText("New name:");

        newNameTxt = new Text(content, SWT.BORDER);
        newNameTxt.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        newNameTxt.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                textModified();
            }
        });
        newNameTxt.setText(initialValue);
        newNameTxt.selectAll();

        new Label(content, SWT.NONE).setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 2, 1));

        // updateReferences = new Button(content, SWT.CHECK);
        // updateReferences.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 2, 1));
        // updateReferences.setText("Update references");
        // updateReferences.setSelection(true);

        // final YangRenameProcessor processor = (YangRenameProcessor) ((RenameRefactoring)
        // getRefactoring())
        // .getProcessor();

        // processor.setUpdateReferences(updateReferences.getSelection());
        // updateReferences.addSelectionListener(new SelectionAdapter() {
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        // processor.setUpdateReferences(updateReferences.getSelection());
        // }
        // });

        Dialog.applyDialogFont(content);
        setControl(content);
    }

    /**
     * Checks the page's state and issues a corresponding error message. The page validation is
     * computed by calling <code>validatePage</code>.
     */
    protected void textModified() {
        String txt = newNameTxt.getText();
        if ("".equals(txt)) { //$NON-NLS-1$
            setPageComplete(false);
            setErrorMessage(null);
            setMessage(null);
            return;
        }
        if (initialValue.equals(txt)) {
            setPageComplete(false);
            setErrorMessage(null);
            setMessage(null);
            return;
        }

        RefactoringStatus status = validateTextField(txt);
        if (status == null) {
            status = new RefactoringStatus();
        }
        setPageComplete(status);
    }

    private RefactoringStatus validateTextField(String txt) {
        YangRenameProcessor<?> processor = (YangRenameProcessor<?>) ((RenameRefactoring) getRefactoring())
                .getProcessor();
        processor.setNewName(txt);
        return null;
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            YangRenameProcessor<?> processor = (YangRenameProcessor<?>) ((RenameRefactoring) getRefactoring())
                    .getProcessor();
            if (processor != null) {
                String newName = processor.getNewName();
                if (newName != null && newName.length() > 0 && !newName.equals(initialValue)) {
                    newNameTxt.setText(newName);
                    newNameTxt.setSelection(0, newName.length());
                }
            }
        }
        super.setVisible(visible);
    }

    protected boolean getBooleanSetting(String key, boolean defaultValue) {
        String update = getRefactoringSettings().get(key);
        if (update != null) {
            return Boolean.valueOf(update).booleanValue();
        } else {
            return defaultValue;
        }
    }
}
