/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.model.editor.editors;

import java.util.List;

import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.ext.model.Node;

/**
 * @author Konstantin Zaitsev
 * @date Aug 13, 2014
 */
public interface ISourceModelManager {
    /**
     * Creates a new elements in source model
     *
     * @param parent parent node to insert created element
     * @param position position in the parent node
     * @param content string content of the element
     */
    void createSourceElement(Node parent, int position, String content);

    /**
     * Invokes Extract Grouping refactoring for selected nodes.
     *
     * @param nodes nodes to be extracted
     */
    void extractGrouping(List<Node> nodes);

    ASTNode getModuleNode(Node node);
}
