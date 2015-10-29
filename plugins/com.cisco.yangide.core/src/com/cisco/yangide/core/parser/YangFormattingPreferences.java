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
 * @author Konstantin Zaitsev
 * @date Jul 22, 2014
 */
public class YangFormattingPreferences {

    /** Indent size. */
    private int indentSize = 4;

    /** Use space characters instead tabs. */
    private boolean spaceForTabs = true;

    /** Max line length. Used in case of {@link #formatComment} or {@link #formatStrings} enabled. */
    private int maxLineLength = 80;

    /** Reformat block comments. */
    private boolean formatComment = false;

    /** Reformat strings. */
    private boolean formatStrings = true;

    /** Make import one line statement. */
    private boolean compactImport = true;

    /**
     * @return the indentSize
     */
    public int getIndentSize() {
        return indentSize;
    }

    /**
     * @param indentSize the indentSize to set
     */
    public void setIndentSize(int indentSize) {
        this.indentSize = indentSize;
    }

    /**
     * @return the spaceForTabs
     */
    public boolean isSpaceForTabs() {
        return spaceForTabs;
    }

    /**
     * @param spaceForTabs the spaceForTabs to set
     */
    public void setSpaceForTabs(boolean spaceForTabs) {
        this.spaceForTabs = spaceForTabs;
    }

    /**
     * @return the maxLineLength
     */
    public int getMaxLineLength() {
        return maxLineLength;
    }

    /**
     * @param maxLineLength the maxLineLength to set
     */
    public void setMaxLineLength(int maxLineLength) {
        this.maxLineLength = maxLineLength;
    }

    /**
     * @return the formatComment
     */
    public boolean isFormatComment() {
        return formatComment;
    }

    /**
     * @param formatComment the formatComment to set
     */
    public void setFormatComment(boolean formatComment) {
        this.formatComment = formatComment;
    }

    /**
     * @return the formatStrings
     */
    public boolean isFormatStrings() {
        return formatStrings;
    }

    /**
     * @param formatStrings the formatStrings to set
     */
    public void setFormatStrings(boolean formatStrings) {
        this.formatStrings = formatStrings;
    }

    /**
     * @return the compactImport
     */
    public boolean isCompactImport() {
        return compactImport;
    }

    /**
     * @param compactImport the compactImport to set
     */
    public void setCompactImport(boolean compactImport) {
        this.compactImport = compactImport;
    }

}
