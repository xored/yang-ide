/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core;

/**
 * An element changed listener receives notification of changes to Yang elements maintained by the
 * Java model.
 * 
 * @author Konstantin Zaitsev
 * @date Jun 30, 2014
 */
public interface IYangElementChangedListener {

    /**
     * Notifies that one or more attributes of one or more Yang elements have changed. The specific
     * details of the change are described by the given event.
     *
     * @param event the change event
     */
    public void elementChanged(ElementChangedEvent event);

}
