/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.refactoring.ui;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import com.cisco.yangide.ext.refactoring.rename.YangRenameProcessor;

/**
 * @author Konstantin Zaitsev
 * @date Jul 30, 2014
 */
public class RenameRefactoringWizard extends RefactoringWizard {

    public RenameRefactoringWizard(Refactoring refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE);
    }

    @Override
    protected void addUserInputPages() {
        YangRenameProcessor<?> processor = (YangRenameProcessor<?>) ((RenameRefactoring) getRefactoring())
                .getProcessor();
        String initialSetting = processor.getNewName();
        RenameInputWizardPage inputPage = new RenameInputWizardPage("Rename Element", initialSetting);
        // inputPage.setImageDescriptor(fInputPageImageDescriptor);
        addPage(inputPage);
    }
}
