/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.presentation.PresentationReconciler;

/**
 * Presentation reconciler, adding functionality for operation without a viewer.
 * 
 * @author Alexey Kholupko
 */
public class YangPresentationReconciler extends PresentationReconciler {

    /** Last used document */
    private IDocument fLastDocument;

    /**
     * Constructs a "repair description" for the given damage and returns this description as a text
     * presentation.
     * <p>
     * NOTE: Should not be used if this reconciler is installed on a viewer.
     * </p>
     */
    public TextPresentation createRepairDescription(IRegion damage, IDocument document) {
        if (document != fLastDocument) {
            setDocumentToDamagers(document);
            setDocumentToRepairers(document);
            fLastDocument = document;
        }
        return createPresentation(damage, document);
    }
}
