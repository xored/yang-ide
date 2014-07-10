/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.internal;

/**
 * @author Konstantin Zaitsev
 * @date Jul 10, 2014
 */
public interface IYangValidationListener {
    void syntaxError(String msg, int lineNumber, int charStart, int charEnd);

    void validationError(String msg, int lineNumber, int charStart, int charEnd);
}
