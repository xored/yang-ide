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

    /** Contains list of project names that uses this project. */
    private Set<String> indirectScope;

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
        this.projectScope = projectScope;
    }

    /**
     * @return the indirectScope
     */
    public Set<String> getIndirectScope() {
        return indirectScope;
    }

    /**
     * @param indirectScope the indirectScope to set
     */
    public void setIndirectScope(Set<String> indirectScope) {
        this.indirectScope = indirectScope;
    }
}
