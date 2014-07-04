/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core;

import com.cisco.yangide.core.model.YangElement;

/**
 * @author Konstantin Zaitsev
 * @date Jun 30, 2014
 */
public interface IYangElementDelta {

    /**
     * Status constant indicating that the element has been added. Note that an added yang element
     * delta has no children, as they are all implicitely added.
     */
    public int ADDED = 1;

    /**
     * Status constant indicating that the element has been removed. Note that a removed yang
     * element delta has no children, as they are all implicitely removed.
     */
    public int REMOVED = 2;

    /**
     * Status constant indicating that the element has been changed, as described by the change
     * flags.
     *
     * @see #getFlags()
     */
    public int CHANGED = 4;

    /**
     * Change flag indicating that the content of the element has changed. This flag is only valid
     * for elements which correspond to files.
     */
    public int F_CONTENT = 0x000001;

    /**
     * Change flag indicating that the modifiers of the element have changed.
     */
    public int F_MODIFIERS = 0x000002;

    /**
     * Change flag indicating that there are changes to the children of the element.
     */
    public int F_CHILDREN = 0x000008;

    /**
     * @return
     */
    YangElement getElement();

    /**
     * @return
     */
    int getKind();

    /**
     * @return
     */
    int getFlags();

    /**
     * @return
     */
    IYangElementDelta[] getAffectedChildren();
}
