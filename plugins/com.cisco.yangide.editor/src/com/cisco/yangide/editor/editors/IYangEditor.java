/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors;

/**
 * Basic interface to operate with YangEditor.
 *
 * @author Konstantin Zaitsev
 * @date Aug 27, 2014
 */
public interface IYangEditor {

    /**
     * Select text and scroll to selected text.
     *
     * @param offset
     * @param length
     */
    void selectAndReveal(int offset, int length);

}
