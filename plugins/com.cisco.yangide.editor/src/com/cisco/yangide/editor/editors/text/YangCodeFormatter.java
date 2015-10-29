/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.editor.editors.text;

import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import com.cisco.yangide.core.parser.YangFormattingPreferences;
import com.cisco.yangide.core.parser.YangParserUtil;

/**
 * @author Konstantin Zaitsev
 * @date Jul 21, 2014
 */
public class YangCodeFormatter {

    /** Formatting preferences. */
    private YangFormattingPreferences preferences;

    /**
     * @param preferences
     */
    public YangCodeFormatter(YangFormattingPreferences preferences) {
        this.preferences = preferences;
    }

    public TextEdit format(String source, int offset, int length, int indentationLevel, String lineSeparator) {
        char[] content = source.substring(offset, offset + length).toCharArray();
        String str = YangParserUtil.formatYangSource(preferences, content, indentationLevel, lineSeparator);
        return new ReplaceEdit(offset, length, str);
    }
}
