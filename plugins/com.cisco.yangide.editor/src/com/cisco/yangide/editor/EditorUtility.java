/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.javaeditor.JarEntryEditorInput;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import com.cisco.yangide.core.indexing.ElementIndexInfo;
import com.cisco.yangide.ui.YangUIPlugin;

/**
 * A number of routines for working with YangElements in editors. Use 'isOpenInEditor' to test if an
 * element is already open in a editor Use 'openInEditor' to force opening an element in a editor
 * With 'getWorkingCopy' you get the working copy (element in the editor) of an element
 *
 * @author Konstantin Zaitsev
 * @date Jul 4, 2014
 */
public class EditorUtility {

    /**
     * Tests if a CU is currently shown in an editor
     *
     * @param inputElement the input element
     * @return the IEditorPart if shown, null if element is not open in an editor
     */
    public static IEditorPart isOpenInEditor(Object inputElement) {
        IEditorInput input = getEditorInput(inputElement);

        if (input != null) {
            IWorkbenchPage p = YangUIPlugin.getActivePage();
            if (p != null) {
                return p.findEditor(input);
            }
        }

        return null;
    }

    /**
     * Opens a Yang editor for an element such as <code>YangElement</code>, <code>IFile</code>, or
     * <code>IStorage</code>. The editor is activated by default.
     *
     * @param inputElement the input element
     * @return an open editor or <code>null</code> if an external editor was opened
     * @throws PartInitException if the editor could not be opened or the input element is not
     * valid.
     */
    public static IEditorPart openInEditor(Object inputElement) throws PartInitException {
        return openInEditor(inputElement, true);
    }

    /**
     * Opens the editor currently associated with the given element (YangElement, IFile,
     * IStorage...)
     *
     * @param inputElement the input element
     * @param activate <code>true</code> if the editor should be activated
     * @return an open editor or <code>null</code> if an external editor was opened
     * @throws PartInitException if the editor could not be opened or the input element is not valid
     */
    public static IEditorPart openInEditor(Object inputElement, boolean activate) throws PartInitException {

        if (inputElement instanceof IFile) {
            return openInEditor((IFile) inputElement, activate);
        }

        IEditorInput input = getEditorInput(inputElement);
        if (input == null) {
            throwPartInitException("No editor input");
        }
        return openInEditor(input, getEditorID(input), activate);
    }

    /**
     * Selects and reveals the given region in the given editor part.
     *
     * @param part the editor part
     * @param region the region
     */
    public static void revealInEditor(IEditorPart part, IRegion region) {
        if (part != null && region != null) {
            revealInEditor(part, region.getOffset(), region.getLength());
        }
    }

    /**
     * Selects and reveals the given offset and length in the given editor part.
     *
     * @param editor the editor part
     * @param offset the offset
     * @param length the length
     */
    public static void revealInEditor(IEditorPart editor, final int offset, final int length) {
        if (editor instanceof ITextEditor) {
            ((ITextEditor) editor).selectAndReveal(offset, length);
            return;
        }

        if (editor != null && editor.getEditorSite().getSelectionProvider() != null) {
            IEditorSite site = editor.getEditorSite();
            if (site == null) {
                return;
            }

            ISelectionProvider provider = editor.getEditorSite().getSelectionProvider();
            if (provider == null) {
                return;
            }

            provider.setSelection(new TextSelection(offset, length));
        }
    }

    public static void openInEditor(ElementIndexInfo info) {
        IStorage storage = null;
        if (info.getEntry() != null && info.getEntry().length() > 0) {
            storage = new JarFileEntryStorage(new Path(info.getPath()), info.getEntry());
        } else {
            storage = YangUIPlugin.getWorkspace().getRoot().getFile(new Path(info.getPath()));
        }
        IEditorPart editor = isOpenInEditor(storage);
        if (editor == null) {
            try {
                editor = openInEditor(storage, true);
            } catch (PartInitException e) {
                YangUIPlugin.log(e);
            }
        }
        YangUIPlugin.getActivePage().activate(editor);
        revealInEditor(editor, info.getStartPosition(), info.getLength());
    }

    private static IEditorPart openInEditor(IFile file, boolean activate) throws PartInitException {
        if (file == null) {
            throwPartInitException("File must not be null");
        }

        IWorkbenchPage p = YangUIPlugin.getActivePage();
        if (p == null) {
            throwPartInitException("No active workbench page");
        }

        IEditorPart editorPart = IDE.openEditor(p, file, activate);
        return editorPart;
    }

    private static IEditorPart openInEditor(IEditorInput input, String editorID, boolean activate)
            throws PartInitException {
        Assert.isNotNull(input);
        Assert.isNotNull(editorID);

        IWorkbenchPage p = YangUIPlugin.getActivePage();
        if (p == null) {
            throwPartInitException("No active workbench");
        }

        return p.openEditor(input, editorID, activate);
    }

    private static void throwPartInitException(String message, int code) throws PartInitException {
        IStatus status = new Status(IStatus.ERROR, YangUIPlugin.PLUGIN_ID, code, message, null);
        throw new PartInitException(status);
    }

    private static void throwPartInitException(String message) throws PartInitException {
        throwPartInitException(message, IStatus.OK);
    }

    public static String getEditorID(IEditorInput input) throws PartInitException {
        Assert.isNotNull(input);
        IEditorDescriptor editorDescriptor;
        if (input instanceof IFileEditorInput) {
            editorDescriptor = IDE.getEditorDescriptor(((IFileEditorInput) input).getFile());
        } else {
            editorDescriptor = IDE.getEditorDescriptor(input.getName());
        }
        return editorDescriptor.getId();
    }

    public static IEditorInput getEditorInput(Object input) {

        if (input instanceof IFile) {
            return new FileEditorInput((IFile) input);
        }

        if (input instanceof IStorage) {
            return new JarEntryEditorInput((IStorage) input);
        }

        return null;
    }
}