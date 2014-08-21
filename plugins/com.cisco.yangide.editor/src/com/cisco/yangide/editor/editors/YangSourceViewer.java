/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors;

import java.util.ArrayList;

import org.eclipse.jface.text.ITextPresentationListener;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Konstantin Zaitsev
 * @date Jul 24, 2014
 */
public class YangSourceViewer extends ProjectionViewer {
    private boolean notifyTextListeners = true;

    public YangSourceViewer(Composite parent, IVerticalRuler verticalRuler, IOverviewRuler overviewRuler,
            boolean showAnnotationsOverview, int styles) {
        super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles);
    }

    /**
     * Prepends the text presentation listener at the beginning of the viewer's list of text
     * presentation listeners. If the listener is already registered with the viewer this call moves
     * the listener to the beginning of the list.
     *
     * @param listener the text presentation listener
     */
    @SuppressWarnings("unchecked")
    public void prependTextPresentationListener(ITextPresentationListener listener) {
        if (fTextPresentationListeners == null) {
            fTextPresentationListeners = new ArrayList<ITextPresentationListener>();
        }

        fTextPresentationListeners.remove(listener);
        fTextPresentationListeners.add(0, listener);
    }

    /**
     * @return the reconciler
     */
    public IReconciler getReconciler() {
        return fReconciler;
    }

    public void updateDocument() {
        setVisibleDocument(getDocument());
    }

    public void enableTextListeners() {
        this.notifyTextListeners = true;
    }

    public void disableTextListeners() {
        this.notifyTextListeners = false;
    }

    @Override
    protected void updateTextListeners(WidgetCommand cmd) {
        if (notifyTextListeners) {
            super.updateTextListeners(cmd);
        }
    }
}
