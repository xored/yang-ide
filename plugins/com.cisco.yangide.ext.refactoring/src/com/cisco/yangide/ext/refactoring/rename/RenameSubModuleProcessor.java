/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.ext.refactoring.rename;

import com.cisco.yangide.core.dom.QName;
import com.cisco.yangide.core.dom.SubModule;
import com.cisco.yangide.core.indexing.ElementIndexReferenceInfo;
import com.cisco.yangide.core.indexing.ElementIndexReferenceType;
import com.cisco.yangide.core.model.YangModelManager;

/**
 * @author Konstantin Zaitsev
 * @date Aug 6, 2014
 */
public class RenameSubModuleProcessor extends YangRenameProcessor<SubModule> {

    public RenameSubModuleProcessor(SubModule subModule) {
        super(subModule);
    }

    @Override
    public String getIdentifier() {
        return "com.cisco.yangide.ext.refactoring.rename.RenameSubModuleProcessor";
    }

    @Override
    protected ElementIndexReferenceType getReferenceType() {
        return ElementIndexReferenceType.INCLUDE;
    }

    @Override
    protected ElementIndexReferenceInfo[] getReferences() {
        SubModule subModule = getNode();
        QName qname = new QName(subModule.getName(), null, subModule.getName(), subModule.getRevision());
        return YangModelManager.getIndexManager().searchReference(qname, getReferenceType(), getFile().getProject());
    }
}
