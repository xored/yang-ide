/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core;

import java.util.EventObject;

/**
 * An element changed event describes a change to the structure or contents of a tree of Yang
 * elements. The changes to the elements are described by the associated delta object carried by
 * this event.
 * 
 * @author Konstantin Zaitsev
 * @date Jun 30, 2014
 */
public class ElementChangedEvent extends EventObject {

    /** Serial version UID. */
    private static final long serialVersionUID = 3695215681338659142L;

    /**
     * Event type constant (bit mask) indicating an after-the-fact report of creations, deletions,
     * and modifications to one or more Yang element(s) expressed as a hierarchical java element
     * delta as returned by <code>getDelta()</code>. Note: this notification occurs during the
     * corresponding POST_CHANGE resource change notification, and contains a full delta accounting
     * for any YangModel operation and/or resource change.
     *
     * @see org.eclipse.core.resources.IResourceChangeEvent
     * @see #getDelta()
     */
    public static final int POST_CHANGE = 1;

    /**
     * Event type constant (bit mask) indicating an after-the-fact report of creations, deletions,
     * and modifications to one or more Yang element(s) expressed as a hierarchical yang element
     * delta as returned by <code>getDelta</code>. Note: this notification occurs as a result of a
     * working copy reconcile operation.
     *
     * @see IJavaElementDelta
     * @see #getDelta()
     */
    public static final int POST_RECONCILE = 4;

    /**
     * Event type indicating the nature of this event. It can be a combination either: - POST_CHANGE
     * - POST_RECONCILE
     */
    private int type;

    /**
     * Creates an new element changed event (based on a <code>IJavaElementDelta</code>).
     *
     * @param delta the Java element delta.
     * @param type the type of delta (ADDED, REMOVED, CHANGED) this event contains
     */
    public ElementChangedEvent(IYangElementDelta delta, int type) {
        super(delta);
        this.type = type;
    }

    /**
     * Returns the delta describing the change.
     *
     * @return the delta describing the change
     */
    public IYangElementDelta getDelta() {
        return (IYangElementDelta) this.source;
    }

    /**
     * Returns the type of event being reported.
     *
     * @return one of the event type constants
     * @see #POST_CHANGE
     * @see #POST_RECONCILE
     */
    public int getType() {
        return this.type;
    }
}