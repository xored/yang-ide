/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.core.buffer;

import java.util.EventObject;

/**
 * @author Konstantin Zaitsev
 * @date Jun 24, 2014
 */
public class BufferChangedEvent extends EventObject {

    /**
     * The length of text that has been modified in the buffer.
     */
    private int length;

    /**
     * The offset into the buffer where the modification took place.
     */
    private int offset;

    /**
     * The text that was modified.
     */
    private String text;

    private static final long serialVersionUID = 655379473891745999L; // backward compatible

    /**
     * Creates a new buffer changed event indicating that the given buffer has changed.
     *
     * @param buffer the given buffer
     * @param offset the given offset
     * @param length the given length
     * @param text the given text
     */
    public BufferChangedEvent(IBuffer buffer, int offset, int length, String text) {
        super(buffer);
        this.offset = offset;
        this.length = length;
        this.text = text;
    }

    /**
     * Returns the buffer which has changed.
     *
     * @return the buffer affected by the change
     */
    public IBuffer getBuffer() {
        return (IBuffer) this.source;
    }

    /**
     * Returns the length of text removed or replaced in the buffer, or 0 if text has been inserted
     * into the buffer.
     *
     * @return the length of the original text fragment modified by the buffer change (
     * <code> 0 </code> in case of insertion).
     */
    public int getLength() {
        return this.length;
    }

    /**
     * Returns the index of the first character inserted, removed, or replaced in the buffer.
     *
     * @return the source offset of the textual manipulation in the buffer
     */
    public int getOffset() {
        return this.offset;
    }

    /**
     * Returns the text that was inserted, the replacement text, or <code>null</code> if text has
     * been removed.
     *
     * @return the text corresponding to the buffer change (<code> null </code> in case of
     * deletion).
     */
    public String getText() {
        return this.text;
    }
}
