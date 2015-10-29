/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.editor.editors;

/**
 * Interface to describe custom actions group for YANG editor.
 * 
 * @author Konstantin Zaitsev
 * @date Jul 29, 2014
 */
public interface IActionGroup {

    /**
     * Initializes action group.
     *
     * @param editor yang editor
     * @param groupName action group name
     */
    void init(YangEditor editor, String groupName);
}
