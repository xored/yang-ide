/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.refactoring.ui;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

/**
 * @author Konstantin Zaitsev
 * @date Aug 18, 2014
 */
public class ChangeRevisionRefactoringWizard extends RefactoringWizard {

    public ChangeRevisionRefactoringWizard(Refactoring refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE);
    }

    @Override
    protected void addUserInputPages() {
        addPage(new ChangeRevisionInputWizardPage());
    }
}
