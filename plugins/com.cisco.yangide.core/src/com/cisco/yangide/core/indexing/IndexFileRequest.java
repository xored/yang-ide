/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.indexing;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.core.dom.ASTVisitor;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.TypeDefinition;
import com.cisco.yangide.core.internal.YangParserModelListener;
import com.cisco.yangide.core.model.YangFileInfo;

/**
 * @author Konstantin Zaitsev
 * @date Jul 1, 2014
 */
public class IndexFileRequest extends IndexRequest {

    private IFile file;

    public IndexFileRequest(IFile file, IndexManager manager) {
        super(file.getFullPath(), manager);
        this.file = file;
    }

    public boolean execute(IProgressMonitor progressMonitor) {
        if (this.isCancelled || progressMonitor != null && progressMonitor.isCanceled()) {
            return true;
        }
        try {
            // remove previously indexed file
            manager.remove(file);

            YangFileInfo info = (YangFileInfo) YangCorePlugin.createYangFile(file).getElementInfo(progressMonitor);
            Module module = info.getModule();
            final String namespace = module.getNamespace().getValue().toASCIIString();

            module.accept(new ASTVisitor() {
                @Override
                public boolean visit(Module module) {
                    manager.addElementIndexInfo(new ElementIndexInfo(module, namespace, ElementIndexType.MODULE, file));
                    return true;
                }
                @Override
                public boolean visit(TypeDefinition typeDefinition) {
                    manager.addElementIndexInfo(new ElementIndexInfo(typeDefinition, namespace, ElementIndexType.TYPE, file));
                    return true;
                }
            });
            System.err.println(toString());
        } catch (YangModelException e) {
            YangCorePlugin.log(e);
        }
        return true;
    }

    public String toString() {
        return "indexing " + file.getFullPath();
    }
}
