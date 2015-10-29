/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.refactoring.nls;

import org.eclipse.osgi.util.NLS;

/**
 * @author Konstantin Zaitsev
 * @date Aug 18, 2014
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = "com.cisco.yangide.ext.refactoring.nls.messages"; //$NON-NLS-1$
    public static String ChangeRevisionAction_description;
    public static String ChangeRevisionAction_name;
    public static String ChangeRevisionAction_text;
    public static String ChangeRevisionInputWizardPage_description;
    public static String ChangeRevisionInputWizardPage_descriptionLabel;
    public static String ChangeRevisionInputWizardPage_newFileCheckLabel;
    public static String ChangeRevisionInputWizardPage_refGroupLabel;
    public static String ChangeRevisionInputWizardPage_refTableDeselectAllBtn;
    public static String ChangeRevisionInputWizardPage_refTableName;
    public static String ChangeRevisionInputWizardPage_refTablePath;
    public static String ChangeRevisionInputWizardPage_refTableProject;
    public static String ChangeRevisionInputWizardPage_refTableSellectAllBtn;
    public static String ChangeRevisionInputWizardPage_revisionLabel;
    public static String ChangeRevisionRefactoring_changeName;
    public static String ChangeRevisionRefactoring_name;
    public static String ChangeRevisionRefactoring_updateReferenceChangeName;
    public static String CreateYangFileChange_fileAlreadyExists;
    public static String CreateYangFileChange_name;
    public static String CreateYangFileChange_taskName;
    public static String ExtractGroupingAction_description;
    public static String ExtractGroupingAction_name;
    public static String ExtractGroupingAction_text;
    public static String ExtractGroupingInputWizardPage_description;
    public static String ExtractGroupingInputWizardPage_groupingNameLabel;
    public static String ExtractGroupingRefactoring_name;
    public static String ExtractGroupingRefactoring_updateReferenceEditName;
    public static String RefactorActionGroup_Refactor;
    public static String RefactorActionGroup_noRefactorAvailable;
    public static String RevisionDialog_title;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
