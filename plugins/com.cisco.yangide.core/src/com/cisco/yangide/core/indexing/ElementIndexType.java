/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.indexing;

import com.cisco.yangide.core.model.YangModelManager;

/**
 * Enumeration of available indexed types of Yang elements.
 *
 * @see YangModelManager#search(String, String, String, ElementIndexType,
 * org.eclipse.core.resources.IProject, org.eclipse.core.runtime.IPath)
 * @author Konstantin Zaitsev
 * @date Jul 1, 2014
 */
public enum ElementIndexType {

    /** Yang Module <code>module</code> */
    MODULE,

    /** Yang SubModule <code>submodule</code> */
    SUBMODULE,

    /** Custom defined type declaration <code>typedef</code> */
    TYPE,

    /** Yang Grouping <code>grouping</code> */
    GROUPING,

    /** Identity declaration <code>identity</code> */
    IDENTITY;
}
