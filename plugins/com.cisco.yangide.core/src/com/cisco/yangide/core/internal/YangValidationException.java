/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.internal;

import org.antlr.v4.runtime.tree.ParseTree;

/**
 * @author Konstantin Zaitsev
 * @date Jul 9, 2014
 */
public class YangValidationException extends RuntimeException {

    private static final long serialVersionUID = 8844776097829361020L;

    private ParseTree context;

    public YangValidationException(ParseTree ctx, String message, Throwable cause) {
        super(message, cause);
        this.context = ctx;
    }

    public YangValidationException(ParseTree ctx, String message) {
        super(message);
        this.context = ctx;
    }

    /**
     * @return the context
     */
    public ParseTree getContext() {
        return context;
    }
}
