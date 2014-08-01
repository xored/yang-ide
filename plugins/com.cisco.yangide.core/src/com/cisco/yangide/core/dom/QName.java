/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.dom;

import java.io.Serializable;

/**
 * @author Konstantin Zaitsev
 * @date Jul 4, 2014
 */
public class QName implements Serializable, Comparable<QName> {

    /** Serial version UID. */
    private static final long serialVersionUID = 8322735104475690634L;

    private String module;
    private String prefix;
    private String name;
    private String revision;

    /**
     * @param namespace
     * @param prefix
     * @param name
     * @param revision
     */
    public QName(String module, String prefix, String name, String revision) {
        this.module = module;
        this.prefix = prefix;
        this.name = name;
        this.revision = revision;
    }

    /**
     * @return the module
     */
    public String getModule() {
        return module;
    }

    /**
     * @param module the module to set
     */
    public void setModule(String module) {
        this.module = module;
    }

    /**
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param prefix the prefix to set
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the revision
     */
    public String getRevision() {
        return revision;
    }

    /**
     * @param revision the revision to set
     */
    public void setRevision(String revision) {
        this.revision = revision;
    }

    @Override
    public String toString() {
        return "QName [module=" + module + ", prefix=" + prefix + ", name=" + name + ", revision=" + revision + "]";
    }

    @Override
    public int compareTo(QName o) {
        return toString().compareTo(o.toString());
    }
}
