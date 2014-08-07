/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.refactoring.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.cisco.yangide.core.dom.ASTNamedNode;
import com.cisco.yangide.core.dom.UsesNode;
import com.cisco.yangide.ext.refactoring.code.InlineGroupingRefactoring;

/**
 * @author Konstantin Zaitsev
 * @date Jul 30, 2014
 */
public class InlineGroupingInputWizardPage extends UserInputWizardPage {
    private ASTNamedNode node;
    private Button allInlineSel;
    private Button deleteGroupCheck;
    private Button singleInlineSel;

    // private Button updateReferences;

    public InlineGroupingInputWizardPage(ASTNamedNode node) {
        super("InlineInputPage");
        this.node = node;
        setDescription("Inline Grouping");
    }

    @Override
    public void createControl(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.verticalSpacing = 5;
        content.setLayout(layout);

        String name = node instanceof UsesNode ? ((UsesNode) node).getGrouping().getName() : node.getName();
        new Label(content, SWT.NONE).setText(NLS.bind("Inline grouping {0}:", name));

        allInlineSel = new Button(content, SWT.RADIO);
        allInlineSel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        allInlineSel.setText("&All references");
        allInlineSel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setState();
            }
        });

        deleteGroupCheck = new Button(content, SWT.CHECK);
        GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
        gd.horizontalIndent = 15;
        deleteGroupCheck.setLayoutData(gd);
        deleteGroupCheck.setText("&Delete grouping declaration");
        deleteGroupCheck.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setState();
            }

        });

        singleInlineSel = new Button(content, SWT.RADIO);
        singleInlineSel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        singleInlineSel.setText("&Only the selected reference");
        singleInlineSel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setState();
            }
        });

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
        deleteGroupCheck.setEnabled(allInlineSel.getSelection());

        InlineGroupingRefactoring refactoring = (InlineGroupingRefactoring) getRefactoring();
        refactoring.setInlineAll(allInlineSel.getSelection());
        refactoring.setDeleteGrouping(deleteGroupCheck.getSelection());
    }

    private void loadState() {
        InlineGroupingRefactoring refactoring = (InlineGroupingRefactoring) getRefactoring();

        singleInlineSel.setEnabled(node instanceof UsesNode);

        allInlineSel.setSelection(refactoring.isInlineAll());
        singleInlineSel.setSelection(!refactoring.isInlineAll());

        deleteGroupCheck.setEnabled(allInlineSel.getSelection());
        deleteGroupCheck.setSelection(refactoring.isDeleteGrouping());
    }
}
