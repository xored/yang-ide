/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.refactoring.ui;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import com.cisco.yangide.ext.refactoring.code.InlineGroupingRefactoring;

/**
 * @author Konstantin Zaitsev
 * @date Aug 6, 2014
 */
public class InlineGroupingRefactoringWizard extends RefactoringWizard {

    public InlineGroupingRefactoringWizard(Refactoring refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE);
    }

    @Override
    protected void addUserInputPages() {
        addPage(new InlineGroupingInputWizardPage(((InlineGroupingRefactoring) getRefactoring()).getNode()));
    }
}
