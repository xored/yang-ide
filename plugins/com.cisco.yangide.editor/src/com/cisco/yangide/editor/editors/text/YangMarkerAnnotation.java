/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors.text;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.texteditor.MarkerAnnotation;

/**
 * @author Konstantin Zaitsev
 * @date Jul 10, 2014
 */
public class YangMarkerAnnotation extends MarkerAnnotation {
    public static final String TYPE = "com.cisco.yangide.core.error";

    public YangMarkerAnnotation(IMarker marker) {
        super(marker);
    }

    /**
     * @param annotation
     */
    public void setOverlay(YangSyntaxAnnotation annotation) {
        if (annotation == null) {
            markDeleted(true);
        }
    }

    public Position getPosition() {
        IMarker marker = getMarker();
        int startPos = marker.getAttribute(IMarker.CHAR_START, -1);
        int endPos = marker.getAttribute(IMarker.CHAR_END, -1);
        if (startPos != -1 && endPos != -1) {
            return new Position(startPos, endPos - startPos);
        }
        return null;
    }
}
