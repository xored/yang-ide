/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */ 
package com.cisco.yangide.core.model;

import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.SimpleNode;
import com.cisco.yangide.core.indexing.ElementIndexInfo;

/**
 * General utilities for working with different parts of the core plug-in.
 * <br>
 * One may think of this class as a library of business-layer utilities.
 *   
 * @author Kirill Karmakulov
 * @date   Oct 13, 2014
 */
public class YangModelUtil {

    /**
     * Retrieves value of "prefix" node of the YANG module, which is identified 
     * by the given {@code info}.
     * 
     * @param info  {@link ElementIndexInfo}, which identifies the quested module
     * @return  value of "prefix" node, if it existed; an empty string otherwise. 
     */
    public static String retrieveModulePrefix(ElementIndexInfo info) {
        String defaultPrefix = "";
        try {
            Module importedModule = YangCorePlugin.createYangFile(info.getPath())
                    .getModule();
            SimpleNode<String> prefixNode = importedModule.getPrefix();
            if (prefixNode != null)
                defaultPrefix = prefixNode.getValue();
        } catch (YangModelException ex) {
            YangCorePlugin.log(ex, "Yang source file could not be loaded.");
        }
        return defaultPrefix;
    }
}
