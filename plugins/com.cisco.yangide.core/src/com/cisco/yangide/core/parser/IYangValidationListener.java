/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.core.parser;

/**
 * Validation listener interface to detect all syntax and validation errors during parsing a YANG
 * file.
 *
 * @author Konstantin Zaitsev
 * @date Jul 10, 2014
 */
public interface IYangValidationListener {

    /**
     * Invokes on ANTLR syntax error.
     *
     * @param msg error message
     * @param lineNumber line number where errors occurred
     * @param charStart absolute char start position
     * @param charEnd absolute char end position
     */
    void syntaxError(String msg, int lineNumber, int charStart, int charEnd);

    /**
     * Invokes on post-parsing validation error (name conventions, import existence).
     *
     * @param msg error message
     * @param lineNumber line number where errors occurred
     * @param charStart absolute char start position
     * @param charEnd absolute char end position
     */
    void validationError(String msg, int lineNumber, int charStart, int charEnd);
}
