/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.core;

import java.io.File;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

import com.cisco.yangide.core.model.YangElement;
import com.cisco.yangide.core.model.YangFile;
import com.cisco.yangide.core.model.YangFolder;
import com.cisco.yangide.core.model.YangJarEntry;
import com.cisco.yangide.core.model.YangJarFile;
import com.cisco.yangide.core.model.YangModel;
import com.cisco.yangide.core.model.YangModelManager;
import com.cisco.yangide.core.model.YangProject;

/**
 * The activator class controls the plug-in life cycle
 */
public class YangCorePlugin extends Plugin {

    /** The plug-in ID */
    public static final String PLUGIN_ID = "com.cisco.yangide.core"; //$NON-NLS-1$

    /** Problem marker ID. */
    public static final String YANGIDE_PROBLEM_MARKER = "com.cisco.yangide.core.problem";

    /** The shared instance */
    private static YangCorePlugin plugin;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        YangModelManager.getYangModelManager().startup();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        try {
            YangModelManager.getYangModelManager().shutdown();
        } finally {
            super.stop(context);
        }
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static YangCorePlugin getDefault() {
        return plugin;
    }

    /**
     * @return global YangModel instance
     */
    public static YangModel getYangModel() {
        return YangModelManager.getYangModelManager().getYangModel();
    }

    /**
     * Reports log to Error Log view.
     *
     * @param statusErrorID plugin related error ID
     * @param message error message
     */
    public static void log(Throwable e, String message) {
        Throwable nestedException;
        if (e instanceof YangModelException && (nestedException = ((YangModelException) e).getException()) != null) {
            e = nestedException;
        }
        log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, message, e));
    }

    /**
     * Reports log to Error Log view.
     *
     * @param statusErrorID plugin related error ID
     * @param message error message
     */
    public static void log(int statusErrorID, String message) {
        log(new Status(statusErrorID, PLUGIN_ID, message));
    }

    /**
     * Reports log to Error Log view.
     *
     * @param status status
     */
    public static void log(IStatus status) {
        getDefault().getLog().log(status);
    }

    /**
     * Reports exception to Error Log view.
     *
     * @param e exception
     */
    public static void log(Throwable e) {
        log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
    }

    /**
     * @param project project to check
     * @return <code>true</code> if project is Yang project
     */
    public static boolean isYangProject(IProject project) {
        try {
            return project.isAccessible()
                    && (project.hasNature("org.eclipse.jdt.core.javanature") || project
                            .hasNature("org.eclipse.m2e.core.maven2Nature"));
        } catch (CoreException e) {
            log(e);
            return false;
        }
    }

    public static YangJarEntry createJarEntry(IPath path, String entry) {
        return new YangJarEntry(new Path(entry), createJarFile(path));
    }

    public static YangJarFile createJarFile(IPath path) {
        return new YangJarFile(path, getYangModel());
    }

    /**
     * @param deltaRes
     * @return
     */
    public static YangElement create(IResource resource) {
        switch (resource.getType()) {
        case IResource.PROJECT:
            return new YangProject((IProject) resource, getYangModel());
        case IResource.FOLDER:
            return new YangFolder(resource, create(resource.getProject()));
        }
        return new YangFile((IFile) resource, create(resource.getParent()));
    }

    /**
     * Returns a parsed model of the yang file that resides at the given {@code path}.
     * @return a {@link YangFile} object
     */
    public static YangFile createYangFile(String path) {
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path));
        return createYangFile(file);
    }
    
    /**
     * @param resource
     */
    public static YangFile createYangFile(IResource resource) {
        return new YangFile((IFile) resource, create(resource.getParent()));
    }

    public static IFile getIFileFromFile(File file) {
        IFile[] files = ResourcesPlugin.getWorkspace().getRoot()
                .findFilesForLocationURI(URIUtil.toURI(file.getAbsolutePath()));
        if (files != null && files.length > 0) {
            return files[0];
        }
        return null;
    }

    /**
     * Creates YANG problem marker for resource.
     *
     * @param path workspace relative path
     * @param message text message
     * @param lineNumber optional line number or <code>-1</code> if no line number
     */
    public static void createProblemMarker(String path, String message, int lineNumber) {
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path));
        if (file.exists()) {
            createProblemMarker(file, message, lineNumber);
        }
    }

    /**
     * Creates YANG problem marker for resource.
     *
     * @param resource resource
     * @param message text message
     * @param lineNumber optional line number or <code>-1</code> if no line number
     */
    public static void createProblemMarker(IResource resource, String message, int lineNumber) {
        try {
            IMarker marker = resource.createMarker(YANGIDE_PROBLEM_MARKER);
            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
            marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
            marker.setAttribute(IMarker.MESSAGE, message);
            if (lineNumber >= 0) {
                marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
            }
        } catch (CoreException e) {
            log(e);
        }
    }

    /**
     * Creates YANG problem marker for resource.
     *
     * @param resource resource
     * @param message text message
     * @param lineNumber optional line number or <code>-1</code> if no line number
     */
    public static void createProblemMarker(IResource resource, String message, int lineNumber, int charStart,
            int charEnd) {
        try {
            IMarker marker = resource.createMarker(YANGIDE_PROBLEM_MARKER);// "com.cisco.yangide.core.syntaxproblem");
            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
            marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
            marker.setAttribute(IMarker.MESSAGE, message.trim());
            marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
            marker.setAttribute(IMarker.CHAR_START, charStart);
            marker.setAttribute(IMarker.CHAR_END, charEnd);
        } catch (CoreException e) {
            log(e);
        }
    }
}
