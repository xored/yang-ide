/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.actions;

/**
 * @author Alexey Kholupko
 */
public interface IYangEditorActionDefinitionIds {
    /**
     * Action definition ID of the source -> toggle comment action (value
     * <code>"com.cisco.yangide.actions.toggle.comment"</code>).
     * 
     */
    public static final String TOGGLE_COMMENT = "com.cisco.yangide.actions.toggle.comment"; //$NON-NLS-1$

    /**
     * Action definition ID of the source -> add block comment action (value
     * <code>"com.cisco.yangide.actions.add.block.comment"</code>).
     * 
     */
    public static final String ADD_BLOCK_COMMENT = "com.cisco.yangide.actions.add.block.comment"; //$NON-NLS-1$

    /**
     * Action definition ID of the source -> remove block comment action (value
     * <code>"com.cisco.yangide.actions.remove.block.comment"</code>).
     * 
     */
    public static final String REMOVE_BLOCK_COMMENT = "com.cisco.yangide.actions.remove.block.comment"; //$NON-NLS-1$

}
