/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.model;

/**
 * @author Konstantin Zaitsev
 * @date Jun 30, 2014
 */
public enum YangElementType {

    /** YangModel type. */
    YANG_MODEL,

    /** YangProject type. */
    YANG_PROJECT,

    /** YangFolder type. */
    YANG_FOLDER,

    /** Yang File type. */
    YANG_FILE,

    /** Yang Jar File type. */
    YANG_JAR_FILE,

    /** Yang Jar Entry. */
    YANG_JAR_ENTRY;

}
