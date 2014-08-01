/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.compare;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.IViewerCreator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Konstantin Zaitsev
 * @date Jul 31, 2014
 */
public class YangFileMergeViewerCreator implements IViewerCreator {

    @Override
    public Viewer createViewer(Composite parent, CompareConfiguration config) {
        return new YangFileMergeViewer(parent, config);
    }
}
