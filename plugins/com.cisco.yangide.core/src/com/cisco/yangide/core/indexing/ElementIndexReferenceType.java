/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.core.indexing;

import com.cisco.yangide.core.model.YangModelManager;

/**
 * Enumeration of available indexed types of Yang reference elements.
 *
 * @see YangModelManager#search(String, String, String, ElementIndexReferenceType,
 * org.eclipse.core.resources.IProject, org.eclipse.core.runtime.IPath)
 * @author Konstantin Zaitsev
 * @date Jul 31, 2014
 */
public enum ElementIndexReferenceType {
    /** Uses declaration with grouping references. */
    USES,

    /** Import statement with reference to module. */
    IMPORT,

    /** Identity reference statement. */
    IDENTITY_REF,

    /** Type references statement. */
    TYPE_REF,

    /** Include statement with references to submodule. */
    INCLUDE;
}
