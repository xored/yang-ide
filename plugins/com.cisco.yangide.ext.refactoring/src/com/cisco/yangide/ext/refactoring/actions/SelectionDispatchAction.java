/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.ext.refactoring.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchSite;

/**
 * @author Konstantin Zaitsev
 * @date Jul 29, 2014
 */
public class SelectionDispatchAction extends Action implements ISelectionChangedListener, CaretListener {

    private IWorkbenchSite fSite;

    protected SelectionDispatchAction(IWorkbenchSite site) {
        fSite = site;
    }

    public IWorkbenchSite getSite() {
        return fSite;
    }

    public ISelection getSelection() {
        ISelectionProvider selectionProvider = getSelectionProvider();
        if (selectionProvider != null) {
            return selectionProvider.getSelection();
        } else {
            return null;
        }
    }

    public Shell getShell() {
        return fSite.getShell();
    }

    public ISelectionProvider getSelectionProvider() {
        return fSite.getSelectionProvider();
    }

    public void update(ISelection selection) {
        dispatchSelectionChanged(selection);
    }

    public void selectionChanged(IStructuredSelection selection) {
        selectionChanged((ISelection) selection);
    }

    public void run(IStructuredSelection selection) {
        run((ISelection) selection);
    }

    public void selectionChanged(ITextSelection selection) {
        selectionChanged((ISelection) selection);
    }

    public void run(ITextSelection selection) {
        run((ISelection) selection);
    }

    public void selectionChanged(ISelection selection) {
        setEnabled(false);
    }

    public void run(ISelection selection) {
    }

    @Override
    public void run() {
        dispatchRun(getSelection());
    }

    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        dispatchSelectionChanged(event.getSelection());
    }

    private void dispatchSelectionChanged(ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            selectionChanged((IStructuredSelection) selection);
        } else if (selection instanceof ITextSelection) {
            selectionChanged((ITextSelection) selection);
        } else {
            selectionChanged(selection);
        }
    }

    private void dispatchRun(ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            run((IStructuredSelection) selection);
        } else if (selection instanceof ITextSelection) {
            run((ITextSelection) selection);
        } else {
            run(selection);
        }
    }

    @Override
    public void caretMoved(CaretEvent event) {
        dispatchSelectionChanged(new TextSelection(event.caretOffset, 0));
    }
}
