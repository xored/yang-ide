/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.editor.editors;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * @author Alexey Kholupko
 */
public class YangWordDetector implements IWordDetector {

    public boolean isWordPart(char character) {
        return Character.isJavaIdentifierPart(character) || character == '-';
    }

    public boolean isWordStart(char character) {
        return Character.isJavaIdentifierPart(character);
    }

}
