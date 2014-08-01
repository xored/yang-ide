/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.ext.refactoring;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.ModuleImport;
import com.cisco.yangide.core.dom.QName;
import com.cisco.yangide.core.dom.TypeReference;
import com.cisco.yangide.core.dom.UsesNode;
import com.cisco.yangide.core.indexing.ElementIndexInfo;
import com.cisco.yangide.core.indexing.ElementIndexType;
import com.cisco.yangide.core.model.YangModelManager;

/**
 * @author Konstantin Zaitsev
 * @date Aug 1, 2014
 */
public final class RefactorUtil {

    /**
     * Protect from initialization.
     */
    private RefactorUtil() {
        // empty block
    }

    /**
     * @param project project
     * @param node reference node
     * @return index information about original node that used as reference or <code>null</code> if
     * node not found.
     * @see RefactorUtil#resolveIndexInfo(ElementIndexInfo)
     */
    public static ElementIndexInfo getByReference(IProject project, ASTNode node) {
        QName qname = null;
        ElementIndexType type = null;
        if (node instanceof UsesNode) {
            qname = ((UsesNode) node).getGrouping();
            type = ElementIndexType.GROUPING;
        } else if (node instanceof TypeReference) {
            qname = ((TypeReference) node).getType();
            type = ElementIndexType.TYPE;
        } else if (node instanceof ModuleImport) {
            ModuleImport moduleImport = (ModuleImport) node;
            qname = new QName(moduleImport.getName(), moduleImport.getPrefix(), moduleImport.getName(),
                    moduleImport.getRevision());
            type = ElementIndexType.MODULE;
        }

        if (qname != null) {
            ElementIndexInfo[] infos = YangModelManager.search(qname.getModule(), qname.getRevision(), qname.getName(),
                    type, project, null);
            if (infos.length > 0) {
                return infos[0];
            }
        }
        return null;
    }

    /**
     * @param info index info
     * @return resolved AST node or <code>null</code> if node not found
     */
    public static ASTNode resolveIndexInfo(ElementIndexInfo info) {
        try {
            Module module = null;
            if (info.getEntry() != null && !info.getEntry().isEmpty()) {
                module = YangCorePlugin.createJarEntry(new Path(info.getPath()), info.getEntry()).getModule();
            } else {
                module = YangCorePlugin.createYangFile(
                        ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(info.getPath()))).getModule();
            }
            return module.getNodeAtPosition(info.getStartPosition());
        } catch (YangModelException e) {
            return null;
        }
    }
}
