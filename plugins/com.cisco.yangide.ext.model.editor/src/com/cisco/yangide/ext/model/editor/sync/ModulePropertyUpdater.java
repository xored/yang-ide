/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.model.editor.sync;

import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.SimpleNode;

/**
 * @author Konstantin Zaitsev
 * @date Aug 28, 2014
 */
public class ModulePropertyUpdater extends SourceNodePropertyUpdater<Module> {

    public ModulePropertyUpdater(DiagramModelAdapter adapter) {
        super(adapter);
    }

    @Override
    public void updateProperty(Module node, String name, Object newValue, int startPosition) {
        Module module = node;
        SimpleNode<String> prop = null;
        boolean handle = false;

        switch (name) {
        case "namespace":
            prop = module.getNamespaceNode();
            handle = true;
            break;
        case "prefix":
            prop = module.getPrefix();
            handle = true;
            break;
        case "yang-version":
            prop = module.getYangVersion();
            handle = true;
            break;
        }

        if (handle) {
            if (prop == null) { // insert new property
                int pos = node.getBodyStartPosition() + 1;
                adapter.performEdit(new InsertEdit(pos, System.lineSeparator()
                        + formatTag(node, name, (String) newValue)));
            } else { // update property
                adapter.performEdit(new ReplaceEdit(prop.getStartPosition(), prop.getLength() + 1, formatTag(node,
                        name, (String) newValue).trim()));
            }
        }
        ASTNode beforeRevisionNode = getAboveChildNode(module, module.getRevisionNode());
        int beforeRevision = beforeRevisionNode != null ? beforeRevisionNode.getEndPosition() + 1 : node
                .getBodyStartPosition() + 1;

        super.updateDefaultProperty(node, name, newValue, beforeRevision);
    }
}
