/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.core.parser;

import org.antlr.v4.runtime.Token;

/**
 * @author Konstantin Zaitsev
 * @date Jul 22, 2014
 */
public interface ITokenFormatter {
    void process(Token token);

    String getFormattedContent();
}
