/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.refactoring.scripting;

import java.util.Map;

import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

/**
 * @author Konstantin Zaitsev
 * @date Jul 29, 2014
 */
public class RenameGroupingRefactoringContribution extends RefactoringContribution {

    @Override
    public RefactoringDescriptor createDescriptor(String id, String project, String description, String comment,
            @SuppressWarnings("rawtypes") Map arguments, int flags) throws IllegalArgumentException {
        return null;
    }
}
