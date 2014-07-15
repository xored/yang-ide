/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.parser;

import static com.cisco.yangide.core.model.YangModelManager.search;

import org.eclipse.core.resources.IProject;

import com.cisco.yangide.core.YangTypeUtil;
import com.cisco.yangide.core.dom.ASTNamedNode;
import com.cisco.yangide.core.dom.ASTVisitor;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.ModuleImport;
import com.cisco.yangide.core.dom.QName;
import com.cisco.yangide.core.dom.SubModule;
import com.cisco.yangide.core.dom.SubModuleInclude;
import com.cisco.yangide.core.dom.TypeReference;
import com.cisco.yangide.core.dom.UsesNode;
import com.cisco.yangide.core.indexing.ElementIndexInfo;
import com.cisco.yangide.core.indexing.ElementIndexType;

/**
 * @author Konstantin Zaitsev
 * @date Jul 14, 2014
 */
public class SemanticValidations {

    private IYangValidationListener listener;
    private Module module;
    private IProject project;

    public SemanticValidations(IYangValidationListener validationListener, IProject project, Module module) {
        this.listener = validationListener;
        this.project = project;
        this.module = module;
    }

    /**
     * Validates model for semantics rules.
     */
    public void validate() {
        validateImports();
        validateSubModule();
        validatesGrouping();
        validateTypes();
    }

    /**
     * Validates for correct submodule relationship.
     */
    private void validateSubModule() {
        if (module instanceof SubModule) {
            SubModule subModule = (SubModule) module;
            ElementIndexInfo[] infos = search(null, null, subModule.getParentModule(), ElementIndexType.MODULE,
                    project, null);
            if (infos.length == 0) {
                String msg = String.format("Parent module '%s' not found", subModule.getParentModule());
                reportError(msg, subModule);
            }
        }
        for (SubModuleInclude include : module.getIncludes().values()) {
            ElementIndexInfo[] infos = search(null, include.getRevision(), include.getName(),
                    ElementIndexType.SUBMODULE, project, null);
            if (infos.length == 0) {
                String msg = String.format("Not existing submodule included: %s:%s by: %s:%s", include.getName(),
                        include.getRevision(), module.getName(), module.getRevision());
                reportError(msg, include);
            }
        }
    }

    /**
     * Validates for import existence.
     */
    private void validateImports() {
        for (ModuleImport moduleImport : module.getImports().values()) {
            ElementIndexInfo[] infos = search(null, moduleImport.getRevision(), moduleImport.getName(),
                    ElementIndexType.MODULE, project, null);
            if (infos.length == 0) {
                String msg = String.format("Not existing module imported: %s:%s by: %s:%s", moduleImport.getName(),
                        moduleImport.getRevision(), module.getName(), module.getRevision());
                reportError(msg, moduleImport);
            }
        }
    }

    /**
     * Validates grouping existence in <code>uses</code> statement.
     */
    private void validatesGrouping() {
        module.accept(new ASTVisitor() {
            @Override
            public boolean visit(UsesNode uses) {
                String name = uses.getName();
                QName grouping = uses.getGrouping();

                if (search(grouping.getModule(), grouping.getRevision(), grouping.getName(), ElementIndexType.GROUPING,
                        project, null).length == 0) {
                    reportError(String.format("Grouping '%s' not found", name), uses);
                }
                return true;
            }
        });
    }

    /**
     * Validates types existence in <code>type</code> statement.
     */
    private void validateTypes() {
        module.accept(new ASTVisitor() {
            @Override
            public boolean visit(TypeReference typeRef) {
                String name = typeRef.getName();
                QName type = typeRef.getType();
                if (YangTypeUtil.isBuiltInType(name)) {
                    return true;
                }
                if (search(type.getModule(), type.getRevision(), type.getName(), ElementIndexType.TYPE, project, null).length == 0) {
                    reportError(String.format("Type '%s' not found", name), typeRef);
                }
                return true;
            }
        });
    }

    /**
     * Reports error message to listener.
     *
     * @param msg message
     * @param node related AST node
     */
    private void reportError(String msg, ASTNamedNode node) {
        listener.validationError(msg, -1, node.getNameStartPosition(),
                node.getNameStartPosition() + node.getNameLength());
    }
}
