/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.model.editor.editors;

import com.cisco.yangide.ext.model.Node;

/**
 * @author Konstantin Zaitsev
 * @date Aug 13, 2014
 */
public interface ISourceElementCreator {
    void createSourceElement(Node parent, int position, String content);
}
