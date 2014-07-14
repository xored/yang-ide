/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors.text;

/**
 * @author Alexey Kholupko
 *
 */

import java.text.CharacterIterator;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

/**
 * An <code>IDocument</code> based implementation of <code>CharacterIterator</code> and
 * <code>CharSequence</code>. Supplied document is not copied; if the document is modified during
 * the lifetime of a <code>DocumentCharacterIterator</code>, the methods returning document content
 * may not always return the same values.
 */
public class DocumentCharacterIterator implements CharacterIterator, CharSequence {

    private int fIndex = -1;
    private final IDocument fDocument;
    private final int fFirst;
    private final int fLast;

    private void invariant() {
        Assert.isTrue(fIndex >= fFirst);
        Assert.isTrue(fIndex <= fLast);
    }

    /**
     * Creates an iterator for the entire document.
     */
    public DocumentCharacterIterator(IDocument document) throws BadLocationException {
        this(document, 0);
    }

    /**
     * Creates an iterator, starting at offset <code>first</code>.
     */
    public DocumentCharacterIterator(IDocument document, int first) throws BadLocationException {
        this(document, first, document.getLength());
    }

    /**
     * Creates an iterator for the document contents from <code>first</code> (inclusive) to
     * <code>last</code> (exclusive).
     */
    public DocumentCharacterIterator(IDocument document, int first, int last) throws BadLocationException {
        if (document == null)
            throw new NullPointerException();
        if (first < 0 || first > last)
            throw new BadLocationException();
        if (last > document.getLength()) {
            throw new BadLocationException();
        }
        fDocument = document;
        fFirst = first;
        fLast = last;
        fIndex = first;
        invariant();
    }

    /*
     * @see java.text.CharacterIterator#first()
     */
    public char first() {
        return setIndex(getBeginIndex());
    }

    /*
     * @see java.text.CharacterIterator#last()
     */
    public char last() {
        if (fFirst == fLast)
            return setIndex(getEndIndex());
        else
            return setIndex(getEndIndex() - 1);
    }

    /*
     * @see java.text.CharacterIterator#current()
     */
    public char current() {
        if (fIndex >= fFirst && fIndex < fLast)
            try {
                return fDocument.getChar(fIndex);
            } catch (BadLocationException e) {
                // ignore
            }
        return DONE;
    }

    /*
     * @see java.text.CharacterIterator#next()
     */
    public char next() {
        return setIndex(Math.min(fIndex + 1, getEndIndex()));
    }

    /*
     * @see java.text.CharacterIterator#previous()
     */
    public char previous() {
        if (fIndex > getBeginIndex()) {
            return setIndex(fIndex - 1);
        } else {
            return DONE;
        }
    }

    /*
     * @see java.text.CharacterIterator#setIndex(int)
     */
    public char setIndex(int position) {
        if (position >= getBeginIndex() && position <= getEndIndex())
            fIndex = position;
        else
            throw new IllegalArgumentException();

        invariant();
        return current();
    }

    /*
     * @see java.text.CharacterIterator#getBeginIndex()
     */
    public int getBeginIndex() {
        return fFirst;
    }

    /*
     * @see java.text.CharacterIterator#getEndIndex()
     */
    public int getEndIndex() {
        return fLast;
    }

    /*
     * @see java.text.CharacterIterator#getIndex()
     */
    public int getIndex() {
        return fIndex;
    }

    /*
     * @see java.text.CharacterIterator#clone()
     */
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    /*
     * @see java.lang.CharSequence#length()
     */
    public int length() {
        return getEndIndex() - getBeginIndex();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.CharSequence#charAt(int)
     */
    @Override
    public char charAt(int index) {
        if (index >= 0 && index < length())
            try {
                return fDocument.getChar(getBeginIndex() + index);
            } catch (BadLocationException e) {
                // ignore and return DONE
                return DONE;
            }
        else
            throw new IndexOutOfBoundsException();
    }

    /*
     * @see java.lang.CharSequence#subSequence(int, int)
     */
    public CharSequence subSequence(int start, int end) {
        if (start < 0)
            throw new IndexOutOfBoundsException();
        if (end < start)
            throw new IndexOutOfBoundsException();
        if (end > length())
            throw new IndexOutOfBoundsException();
        try {
            return new DocumentCharacterIterator(fDocument, getBeginIndex() + start, getBeginIndex() + end);
        } catch (BadLocationException ex) {
            throw new IndexOutOfBoundsException();
        }
    }
}
