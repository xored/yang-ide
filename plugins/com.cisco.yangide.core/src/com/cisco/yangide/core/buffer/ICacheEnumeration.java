/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */ 
package com.cisco.yangide.core.buffer;

import java.util.Enumeration;

/**
 * @author Konstantin Zaitsev
 * @date   Jun 24, 2014
 */
@SuppressWarnings({ "rawtypes" })
public interface ICacheEnumeration extends Enumeration {
    /**
     * Returns the value of the previously accessed key in the enumeration.
     * Must be called after a call to nextElement().
     *
     * @return Value of current cache entry
     */
    public Object getValue();
}
