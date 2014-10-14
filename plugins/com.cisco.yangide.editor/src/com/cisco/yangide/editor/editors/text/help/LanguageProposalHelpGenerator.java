/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors.text.help;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osgi.util.NLS;

import com.cisco.yangide.editor.YangEditorPlugin;

/**
 * Contains factory methods that create generators of additional information for a proposal backed
 * by YANG language types, keywords, etc.
 * 
 * @author Kirill Karmakulov
 * @date Oct 14, 2014
 */
public class LanguageProposalHelpGenerator implements IProposalHelpGenerator {

    private enum DefinitionType {
        Keyword, Type
    }

    private final String definition;
    private final DefinitionType definitionType;

    private LanguageProposalHelpGenerator(String definition, DefinitionType definitionType) {
        super();
        this.definition = definition;
        this.definitionType = definitionType;
    }

    /**
     * @return an implementation of {@link IProposalHelpGenerator} that generates quick help for a
     * YANG built-in type
     */
    public static IProposalHelpGenerator type(String definition) {
        return new LanguageProposalHelpGenerator(definition, DefinitionType.Type);
    }

    /**
     * @return an implementation of {@link IProposalHelpGenerator} that generates quick help for a
     * YANG language keyword.
     */
    public static IProposalHelpGenerator keyword(String definition) {
        return new LanguageProposalHelpGenerator(definition, DefinitionType.Keyword);
    }

    /**
     * Reads an internal help file, packs it inside an HTML, and returns the result as a string.
     */
    @Override
    public String getAdditionalInfo(YangCompletionProposal proposal, IProgressMonitor monitor) {
        SubMonitor subMonitor = SubMonitor.convert(monitor, 2); // There's no way to discover size
                                                                // of the help file; so, the best
                                                                // guess is a 2-step process: read
                                                                // the file contents, construct an
                                                                // HTML that contains it.
        URL url = null;
        try {
            url = FileLocator.find(YangEditorPlugin.getDefault().getBundle(), getPath(), null);
            if (url == null) {
                YangEditorPlugin.logWarning(NLS.bind("There's no help topic about \"{0}\".", definition), null); //$NON-NLS-1$
                return null;
            }
            try (InputStream inputStream = url.openStream();
                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                for (String str = null; (str = r.readLine()) != null;) {
                    sb.append(str).append("\n"); // line breaks are important if the help file //$NON-NLS-1$
                                                 // contains <pre> HTML tags
                }
                subMonitor.worked(1); // reading completed
                String wrapHtmlText = HelpCompositionUtils.wrapHtmlText(sb.toString(), definition);
                subMonitor.worked(1); // HTML construction completed
                return wrapHtmlText;
            }
        } catch (IOException e) {
            YangEditorPlugin.logError("Failed to load help contents from " + url, e); //$NON-NLS-1$
        } finally {
            subMonitor.done();
        }
        return null;
    }

    /**
     * Computes path to a file that contains help related to the definition.
     */
    private IPath getPath() {
        String subdir = ""; //$NON-NLS-1$
        switch (definitionType) {
        case Keyword:
            subdir = "keywords"; //$NON-NLS-1$
            break;

        case Type:
            subdir = "types"; //$NON-NLS-1$
            break;

        default:
            break;
        }
        return new Path("help").append(subdir).append(definition).addFileExtension("txt"); //$NON-NLS-1$ //$NON-NLS-2$
    }

}
