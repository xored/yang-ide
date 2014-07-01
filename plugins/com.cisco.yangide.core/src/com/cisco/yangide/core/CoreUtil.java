/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core;

/**
 * @author Konstantin Zaitsev
 * @date Jun 30, 2014
 */
public final class CoreUtil {

    /**
     * Protect from initialization.
     */
    private CoreUtil() {
        // empty block
    }

    /**
     * Combines two hash codes to make a new one.
     */
    public static int combineHashCodes(int hashCode1, int hashCode2) {
        return hashCode1 * 17 + hashCode2;
    }
}
