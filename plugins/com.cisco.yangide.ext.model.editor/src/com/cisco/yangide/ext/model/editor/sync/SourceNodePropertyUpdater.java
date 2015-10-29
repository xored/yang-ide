/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.model.editor.sync;

import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.cisco.yangide.core.dom.ASTCompositeNode;
import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.SimpleNode;
import com.cisco.yangide.ext.model.editor.Activator;
import com.cisco.yangide.ext.refactoring.RefactorUtil;

/**
 * @author Konstantin Zaitsev
 * @date Aug 28, 2014
 */
public class SourceNodePropertyUpdater<T extends ASTNode> {
    protected final DiagramModelAdapter adapter;

    public SourceNodePropertyUpdater(DiagramModelAdapter adapter) {
        this.adapter = adapter;
    }

    public void updateProperty(T node, String name, Object newValue, int startPosition) {
        if (isHandleProperty(name)) {
            SimpleNode<Object> prop = getProperty(node, name);
            if (prop == null) { // insert new property
                int pos = node.getBodyStartPosition() + 1;
                adapter.performEdit(new InsertEdit(pos, System.lineSeparator()
                        + formatTag(node, name, (String) newValue)));
            } else { // update property
                adapter.performEdit(new ReplaceEdit(prop.getStartPosition(), prop.getLength() + 1, formatTag(node,
                        name, (String) newValue).trim()));
            }
        } else {
            updateDefaultProperty(node, name, newValue, startPosition);
        }
    }

    protected boolean isHandleProperty(String name) {
        return false;
    }

    protected void updateDefaultProperty(ASTNode node, String name, Object newValue, int startPosition) {
        SimpleNode<String> prop = null;

        switch (name) {
        case "description":
            prop = node.getDescriptionNode();
            break;
        case "reference":
            prop = node.getReferenceNode();
            break;
        case "status":
            prop = node.getStatusNode();
            break;
        case "organization":
            prop = ((Module) node).getOrganization();
            break;
        case "contact":
            prop = ((Module) node).getContact();
            break;
        default:
            Activator.logError("unknoun tag: " + name);
            return;
        }

        if (prop == null) { // insert new property
            int pos = startPosition;
            if (name.equals("status")) {
                if (node.getReferenceNode() != null) {
                    pos = node.getReferenceNode().getEndPosition() + 1;
                } else if (node.getDescriptionNode() != null) {
                    pos = node.getDescriptionNode().getEndPosition() + 1;
                }
            } else if (name.equals("reference") && node.getDescriptionNode() != null) {
                pos = node.getDescriptionNode().getEndPosition() + 1;
            }
            adapter.performEdit(new InsertEdit(pos, System.lineSeparator() + formatTag(node, name, (String) newValue)));
        } else if (newValue == null || ((String) newValue).isEmpty()) { // delete property
            if (prop != null) {
                adapter.performEdit(new DeleteEdit(prop.getStartPosition(), prop.getLength() + 1));
            }
        } else { // update property
            if (prop != null) {
                adapter.performEdit(new ReplaceEdit(prop.getStartPosition(), prop.getLength() + 1, formatTag(node,
                        name, (String) newValue).trim()));
            }
        }
    }

    protected ASTNode getAboveChildNode(ASTCompositeNode parent, ASTNode node) {
        ASTNode result = null;
        for (ASTNode child : parent.getChildren()) {
            if (child == node) {
                return result;
            }
            result = child;
        }
        return null;
    }

    protected String formatTag(ASTNode node, String name, String value) {
        return trimTrailingSpaces(RefactorUtil.formatCodeSnipped(name + " \"" + value + "\";",
                adapter.getIndentLevel(node)));
    }

    protected String trimTrailingSpaces(String str) {
        int len = str.length();
        char[] val = str.toCharArray();

        while ((len > 0) && (val[len - 1] <= ' ')) {
            len--;
        }
        return (len < str.length()) ? str.substring(0, len) : str;
    }

    protected String empty2Quote(String str) {
        return str == null || str.trim().isEmpty() ? ("\"" + str + "\"") : str;
    }

    @SuppressWarnings("unchecked")
    protected SimpleNode<Object> getProperty(ASTNode node, String name) {
        if (node instanceof ASTCompositeNode) {
            for (ASTNode child : ((ASTCompositeNode) node).getChildren()) {
                if (child instanceof SimpleNode && child.getNodeName().equals(name)) {
                    return (SimpleNode<Object>) child;
                }
            }
        }
        return null;
    }
}
