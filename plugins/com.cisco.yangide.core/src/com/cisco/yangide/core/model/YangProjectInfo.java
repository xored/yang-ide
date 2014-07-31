/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.model;

import java.util.Set;

import com.cisco.yangide.core.OpenableElementInfo;

/**
 * @author Konstantin Zaitsev
 * @date Jul 30, 2014
 */
public class YangProjectInfo extends OpenableElementInfo {
    /** Contains list of project names that uses by this project. */
    private Set<String> projectScope;

    /**
     * @return the projectScope
     */
    public Set<String> getProjectScope() {
        return projectScope;
    }

    /**
     * @param projectScope the projectScope to set
     */
    public void setProjectScope(Set<String> projectScope) {
        System.err.println("set " + projectScope);
        this.projectScope = projectScope;
    }
}
