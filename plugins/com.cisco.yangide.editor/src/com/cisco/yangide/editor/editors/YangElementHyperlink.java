/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import com.cisco.yangide.core.indexing.ElementIndexInfo;
import com.cisco.yangide.editor.EditorUtility;

/**
 * @author Konstantin Zaitsev
 * @date Jul 4, 2014
 */
public class YangElementHyperlink implements IHyperlink {

    /** Hyperlink region. */
    private IRegion region;
    
    /** Info to reference. */
    private ElementIndexInfo info;

    public YangElementHyperlink(IRegion region, ElementIndexInfo info) {
        this.region = region;
        this.info = info;
    }

    @Override
    public IRegion getHyperlinkRegion() {
        return region;
    }

    @Override
    public String getTypeLabel() {
        return info.getType().toString();
    }

    @Override
    public String getHyperlinkText() {
        return info.getNamespace() + ": " + info.getPath();
    }

    @Override
    public void open() {
        EditorUtility.openInEditor(info);
    }
}
