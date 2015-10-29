/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.refactoring;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.resource.DeleteResourceChange;
import org.eclipse.ltk.core.refactoring.resource.ResourceChange;
import org.eclipse.osgi.util.NLS;

import com.cisco.yangide.ext.refactoring.nls.Messages;

/**
 * @author Konstantin Zaitsev
 * @date Aug 19, 2014
 */
public class CreateYangFileChange extends ResourceChange {

    private IPath path;
    private String source;

    public CreateYangFileChange(IPath path, String source) {
        this.path = path;
        this.source = source;
    }

    @Override
    protected IResource getModifiedResource() {
        return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
    }

    @Override
    public String getName() {
        return NLS.bind(Messages.CreateYangFileChange_name, path.lastSegment());
    }

    @Override
    public Change perform(IProgressMonitor pm) throws CoreException {
        InputStream is = null;
        try {
            pm.beginTask(Messages.CreateYangFileChange_taskName, 2);

            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
            try {
                is = new ByteArrayInputStream(source.getBytes("UTF-8")); //$NON-NLS-1$
                file.create(is, false, new SubProgressMonitor(pm, 1));
                pm.worked(1);

                return new DeleteResourceChange(file.getFullPath(), true);
            } catch (UnsupportedEncodingException e) {
                throw new CoreException(new Status(Status.ERROR, YangRefactoringPlugin.PLUGIN_ID, e.getMessage(), e));
            }
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                throw new CoreException(new Status(Status.ERROR, YangRefactoringPlugin.PLUGIN_ID, e.getMessage(), e));
            } finally {
                pm.done();
            }
        }
    }

    @Override
    public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException {
        RefactoringStatus result = new RefactoringStatus();
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

        IFileInfo jfile = EFS.getStore(file.getLocationURI()).fetchInfo();
        if (jfile.exists()) {
            result.addFatalError(
                    NLS.bind(Messages.CreateYangFileChange_fileAlreadyExists, file.getFullPath().toString()));
            return result;
        }
        return result;
    }
}
