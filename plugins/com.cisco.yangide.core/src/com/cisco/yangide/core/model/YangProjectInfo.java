/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.model;

import org.eclipse.jdt.core.IClasspathEntry;

import com.cisco.yangide.core.OpenableElementInfo;

/**
 * @author Konstantin Zaitsev
 * @date Jul 10, 2014
 */
public class YangProjectInfo extends OpenableElementInfo {
    private IClasspathEntry[] classpath;

    /**
     * @return the classpath
     */
    public IClasspathEntry[] getClasspath() {
        return classpath;
    }

    /**
     * @param classpath the classpath to set
     */
    public void setClasspath(IClasspathEntry[] classpath) {
        this.classpath = classpath;
    }

    public boolean isClasspathEquals(IClasspathEntry[] classpath) {
        if ((this.classpath == null && classpath != null) || (this.classpath != null && classpath == null)) {
            return false;
        }

        if (this.classpath == null && classpath == null) {
            return true;
        }

        if (this.classpath.length != this.classpath.length) {
            return false;
        }

        for (int i = 0; i < this.classpath.length; i++) {
            if (!this.classpath[i].toString().equals(classpath[i].toString())) {
                return false;
            }
        }
        return true;
    }
}
