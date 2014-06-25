/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core;

import com.cisco.yangide.core.buffer.LRUCache;
import com.cisco.yangide.core.buffer.OverflowingLRUCache;

/**
 * @author Konstantin Zaitsev
 * @date Jun 25, 2014
 */
public class OpenableElementCache extends OverflowingLRUCache {

    IOpenable spaceLimitParent = null;

    /**
     * Constructs a new element cache of the given size.
     */
    public OpenableElementCache(int size) {
        super(size);
    }

    /**
     * Constructs a new element cache of the given size.
     */
    public OpenableElementCache(int size, int overflow) {
        super(size, overflow);
    }

    /**
     * Returns true if the element is successfully closed and removed from the cache, otherwise
     * false.
     * <p>
     * NOTE: this triggers an external removal of this element by closing the element.
     */
    protected boolean close(LRUCacheEntry entry) {
        Openable element = (Openable) entry.key;
        try {
            if (!element.canBeRemovedFromCache()) {
                return false;
            } else {
                element.close();
                return true;
            }
        } catch (YangModelException npe) {
            return false;
        }
    }

    /*
     * Ensures that there is enough room for adding the children of the given info. If the space
     * limit must be increased, record the parent that needed this space limit.
     */
    protected void ensureSpaceLimit(Object info, IOpenable parent) {
        // ensure the children can be put without closing other elements
        int childrenSize = ((OpenableElementInfo) info).getChildren().length;
        int spaceNeeded = 1 + (int) ((1 + this.loadFactor) * (childrenSize + this.overflow));
        if (this.spaceLimit < spaceNeeded) {
            // parent is being opened with more children than the space limit
            shrink(); // remove overflow
            setSpaceLimit(spaceNeeded);
            this.spaceLimitParent = parent;
        }
    }

    /*
     * Returns a new instance of the receiver.
     */
    protected LRUCache newInstance(int size, int newOverflow) {
        return new OpenableElementCache(size, newOverflow);
    }

    /*
     * If the given parent was the one that increased the space limit, reset the space limit to the
     * given default value.
     */
    protected void resetSpaceLimit(int defaultLimit, IOpenable parent) {
        if (parent.equals(this.spaceLimitParent)) {
            setSpaceLimit(defaultLimit);
            this.spaceLimitParent = null;
        }
    }

}
