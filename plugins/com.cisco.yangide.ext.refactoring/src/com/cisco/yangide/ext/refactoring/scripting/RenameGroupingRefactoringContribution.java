/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
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
