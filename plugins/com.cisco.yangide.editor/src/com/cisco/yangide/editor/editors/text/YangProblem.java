/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.editor.editors.text;

import org.eclipse.jface.text.Position;

/**
 * @author Konstantin Zaitsev
 * @date Jul 16, 2014
 */
public class YangProblem {
    private YangSyntaxAnnotation annotation;
    private Position position;

    /**
     * @param annotation
     * @param position
     */
    public YangProblem(YangSyntaxAnnotation annotation, Position position) {
        this.annotation = annotation;
        this.position = position;
    }

    /**
     * @return the annotation
     */
    public YangSyntaxAnnotation getAnnotation() {
        return annotation;
    }

    /**
     * @return the position
     */
    public Position getPosition() {
        return position;
    }
}
