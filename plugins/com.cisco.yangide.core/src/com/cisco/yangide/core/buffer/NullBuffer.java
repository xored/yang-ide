/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.core.buffer;

import org.eclipse.core.resources.IFile;

import com.cisco.yangide.core.IOpenable;

/**
 * @author Konstantin Zaitsev
 * @date Jun 24, 2014
 */
public class NullBuffer extends Buffer {

    /**
     * Creates a new null buffer on an underlying resource.
     */
    public NullBuffer(IFile file, IOpenable owner, boolean readOnly) {
        super(file, owner, readOnly);
    }
}
