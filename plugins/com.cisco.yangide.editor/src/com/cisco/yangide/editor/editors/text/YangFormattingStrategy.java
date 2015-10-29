/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.editor.editors.text;

import java.util.LinkedList;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import com.cisco.yangide.core.parser.YangFormattingPreferences;
import com.cisco.yangide.editor.YangEditorPlugin;
import com.cisco.yangide.ui.YangUIPlugin;
import com.cisco.yangide.ui.preferences.YangPreferenceConstants;

/**
 * @author Konstantin Zaitsev
 * @date Jul 21, 2014
 */
public class YangFormattingStrategy extends ContextBasedFormattingStrategy {

    /** Documents to be formatted by this strategy */
    private final LinkedList<IDocument> documents = new LinkedList<IDocument>();
    /** Partitions to be formatted by this strategy */
    private final LinkedList<TypedPosition> partitions = new LinkedList<TypedPosition>();

    @SuppressWarnings("unchecked")
    @Override
    public void format() {
        super.format();
        final IDocument document = documents.removeFirst();
        final TypedPosition partition = partitions.removeFirst();

        if (document != null && partition != null) {
            Map<String, IDocumentPartitioner> partitioners = null;
            try {
                YangFormattingPreferences pref = new YangFormattingPreferences();

                IPreferenceStore store = YangUIPlugin.getDefault().getPreferenceStore();
                pref.setSpaceForTabs(store.getBoolean(YangPreferenceConstants.FMT_INDENT_SPACE));
                pref.setIndentSize(store.getInt(YangPreferenceConstants.FMT_INDENT_WIDTH));
                pref.setCompactImport(store.getBoolean(YangPreferenceConstants.FMT_COMPACT_IMPORT));
                pref.setFormatComment(store.getBoolean(YangPreferenceConstants.FMT_COMMENT));
                pref.setFormatStrings(store.getBoolean(YangPreferenceConstants.FMT_STRING));
                pref.setMaxLineLength(store.getInt(YangPreferenceConstants.FMT_MAX_LINE_LENGTH));

                int offset = partition.getOffset();
                final TextEdit edit = new YangCodeFormatter(pref).format(document.get(), offset, partition.getLength(),
                        getIndentationLevel(document, offset), TextUtilities.getDefaultLineDelimiter(document));
                if (edit != null) {
                    if (edit.getChildrenSize() > 20) {
                        partitioners = TextUtilities.removeDocumentPartitioners(document);
                    }

                    edit.apply(document);
                }

            } catch (MalformedTreeException | BadLocationException e) {
                YangEditorPlugin.log(e);
            } finally {
                if (partitioners != null) {
                    TextUtilities.addDocumentPartitioners(document, partitioners);
                }
            }
        }
    }

    @Override
    public void formatterStarts(final IFormattingContext context) {
        super.formatterStarts(context);

        partitions.addLast((TypedPosition) context.getProperty(FormattingContextProperties.CONTEXT_PARTITION));
        documents.addLast((IDocument) context.getProperty(FormattingContextProperties.CONTEXT_MEDIUM));
    }

    @Override
    public void formatterStops() {
        super.formatterStops();

        partitions.clear();
        documents.clear();
    }

    private int getIndentationLevel(IDocument doc, int offset) {
        int indentationLevel = 0;
        boolean skip = false;
        char[] buff = doc.get().toCharArray();
        for (int i = 1; i < offset; i++) {
            if (buff[i - 1] == '/' && buff[i] == '*') {
                skip = true;
            }

            if (buff[i - 1] == '*' && buff[i] == '/') {
                skip = false;
            }

            if (!skip) {
                if (buff[i] == '{') {
                    indentationLevel++;
                }
                if (buff[i] == '}') {
                    indentationLevel--;
                }
            }
        }
        return indentationLevel > 0 ? indentationLevel : 0;
    }
}
