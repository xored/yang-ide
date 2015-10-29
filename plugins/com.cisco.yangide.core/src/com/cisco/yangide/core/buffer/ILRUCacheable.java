/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.core.buffer;

/**
 * @author Konstantin Zaitsev
 * @date Jun 24, 2014
 */
public interface ILRUCacheable {
    /**
     * Returns the space the receiver consumes in an LRU Cache. The default space value is 1.
     *
     * @return int Amount of cache space taken by the receiver
     */
    public int getCacheFootprint();
}