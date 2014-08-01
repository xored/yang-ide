/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.ext.refactoring.rename;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.participants.RenameProcessor;

/**
 * @author Konstantin Zaitsev
 * @date Jul 30, 2014
 */
public abstract class YangRenameProcessor extends RenameProcessor {
    private String newName;
    private boolean updateReferences;
    private IFile file;
    private IDocument document;

    /**
     * @return the newName
     */
    public String getNewName() {
        return newName;
    }

    /**
     * @param newName the newName to set
     */
    public void setNewName(String newName) {
        this.newName = newName;
    }

    /**
     * @param selection
     */
    public void setUpdateReferences(boolean updateReferences) {
        this.updateReferences = updateReferences;
    }

    /**
     * @return the updateReferences
     */
    public boolean isUpdateReferences() {
        return updateReferences;
    }

    /**
     * @return the document
     */
    public IDocument getDocument() {
        return document;
    }

    /**
     * @param document the document to set
     */
    public void setDocument(IDocument document) {
        this.document = document;
    }

    /**
     * @return the file
     */
    public IFile getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(IFile file) {
        this.file = file;
    }
}
