/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.buffer;


/**
 * @author Konstantin Zaitsev
 * @date Jun 24, 2014
 */
public interface IBufferChangedListener {
    /**
     * Notifies that the given event has occurred.
     *
     * @param event the change event
     */
    public void bufferChanged(BufferChangedEvent event);
}
