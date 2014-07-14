/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.parser;

import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.ModuleImport;
import com.cisco.yangide.core.indexing.ElementIndexInfo;
import com.cisco.yangide.core.indexing.ElementIndexType;
import com.cisco.yangide.core.model.YangModelManager;

/**
 * @author Konstantin Zaitsev
 * @date Jul 14, 2014
 */
public class SemanticValidations {

    private IYangValidationListener listener;
    private Module module;

    /**
     * @param validationListener
     */
    public SemanticValidations(IYangValidationListener validationListener, Module module) {
        this.listener = validationListener;
        this.module = module;
    }

    /**
     * Validates model for semantics rules.
     */
    public void validate() {
        validateImports();
        validateSubModule();
    }

    /**
     * Validates for correct submodule relationship.
     */
    private void validateSubModule() {
    }

    /**
     * Validates for import existence and double declaration.
     */
    private void validateImports() {
        for (ModuleImport moduleImport : module.getImports().values()) {
            ElementIndexInfo[] infos = YangModelManager.search(null, moduleImport.getRevision(),
                    moduleImport.getName(), ElementIndexType.MODULE, null, null);
            if (infos.length == 0) {
                listener.validationError(String.format("Not existing module imported: %s:%s by: %s:%s",
                        moduleImport.getName(), moduleImport.getRevision(), module.getName(), module.getRevision()),
                        -1, moduleImport.getNameStartPosition(),
                        moduleImport.getNameStartPosition() + moduleImport.getNameLength());
            }
        }
    }
}
