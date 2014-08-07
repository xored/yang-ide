/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
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
