/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.core.buffer;

import java.util.Enumeration;

/**
 * @author Konstantin Zaitsev
 * @date Jun 24, 2014
 */
@SuppressWarnings({ "rawtypes" })
public class LRUCacheEnumerator implements Enumeration {
    /**
     * Current element;
     */
    protected LRUEnumeratorElement elementQueue;

    public static class LRUEnumeratorElement {
        /**
         * Value returned by <code>nextElement()</code>;
         */
        public Object value;

        /**
         * Next element
         */
        public LRUEnumeratorElement next;

        /**
         * Constructor
         */
        public LRUEnumeratorElement(Object value) {
            this.value = value;
        }
    }

    /**
     * Creates a CacheEnumerator on the list of <code>LRUEnumeratorElements</code>.
     */
    public LRUCacheEnumerator(LRUEnumeratorElement firstElement) {
        this.elementQueue = firstElement;
    }

    /**
     * Returns true if more elements exist.
     */
    public boolean hasMoreElements() {
        return this.elementQueue != null;
    }

    /**
     * Returns the next element.
     */
    public Object nextElement() {
        Object temp = this.elementQueue.value;
        this.elementQueue = this.elementQueue.next;
        return temp;
    }
}
