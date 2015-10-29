/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Different utility to work with YANG types system.
 *
 * @author Konstantin Zaitsev
 * @date Jul 15, 2014
 */
public final class YangTypeUtil {

    /** Set of built-in YANG types. */
    private static Set<String> BUILTIN_TYPES = new HashSet<>(Arrays.asList("binary", "bits", "boolean", "decimal64",
            "empty", "enumeration", "identityref", "instance-identifier", "int8", "int16", "int32", "int64", "leafref",
            "string", "uint8", "uint16", "uint32", "uint64", "union"));

    /**
     * Protect from initialization.
     */
    private YangTypeUtil() {
        // empty block
    }

    /**
     * @param type type name to check
     * @return <code>true</code> it type is YANG built-in type
     */
    public static boolean isBuiltInType(String type) {
        return BUILTIN_TYPES.contains(type);
    }
}
