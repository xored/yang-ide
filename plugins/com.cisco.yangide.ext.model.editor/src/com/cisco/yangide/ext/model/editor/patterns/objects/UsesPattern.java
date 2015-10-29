/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.model.editor.patterns.objects;

import org.eclipse.emf.ecore.EClass;

import com.cisco.yangide.ext.model.Uses;
import com.cisco.yangide.ext.model.editor.util.YangDiagramImageProvider;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class UsesPattern extends DomainObjectPattern {

    @Override
    public String getCreateImageId() {
        return YangDiagramImageProvider.IMG_USES_PROPOSAL;
    }

    @Override
    public String getCreateName() {
        return "uses";
    }

    @Override
    public EClass getObjectEClass() {
        return YangModelUtil.MODEL_PACKAGE.getUses();
    }

    @Override
    protected String getHeaderText(Object obj) {
        if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getUses(), obj)) {
            return YangModelUtil.getQNamePresentation((Uses) obj);
        }
        return super.getHeaderText(obj);
    }

}
