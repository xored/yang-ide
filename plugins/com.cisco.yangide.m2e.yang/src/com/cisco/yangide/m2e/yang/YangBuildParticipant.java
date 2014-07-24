/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.m2e.yang;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.codehaus.plexus.util.Scanner;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;
import org.sonatype.plexus.build.incremental.BuildContext;

import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.model.YangModelManager;
import com.cisco.yangide.core.parser.IYangValidationListener;
import com.cisco.yangide.core.parser.YangParserUtil;
import com.cisco.yangide.ui.YangUIPlugin;
import com.cisco.yangide.ui.preferences.YangPreferenceConstants;

/**
 * @author Konstantin Zaitsev
 * @date Jul 2, 2014
 */
public class YangBuildParticipant extends MojoExecutionBuildParticipant {

    public YangBuildParticipant(MojoExecution execution, boolean runOnIncremental) {
        super(execution, runOnIncremental);
    }

    @Override
    public Set<IProject> build(int kind, IProgressMonitor monitor) throws Exception {
        IMaven maven = MavenPlugin.getMaven();
        BuildContext buildContext = getBuildContext();

        File source = maven.getMojoParameterValue(getSession().getCurrentProject(), getMojoExecution(),
                YangM2EPlugin.YANG_FILES_ROOT_DIR, File.class, monitor);
        if (source == null) {
            source = new File(getSession().getCurrentProject().getBasedir(), YangM2EPlugin.YANG_FILES_ROOT_DIR_DEFAULT);
        }
        Scanner ds = buildContext.newScanner(source);
        ds.scan();
        String[] includedFiles = ds.getIncludedFiles();
        if (includedFiles == null || includedFiles.length <= 0) {
            return null;
        }
        // clear markers before build
        getMavenProjectFacade().getProject().deleteMarkers(YangCorePlugin.YANGIDE_PROBLEM_MARKER, true,
                IResource.DEPTH_INFINITE);

        // wait index job
        if (kind == FULL_BUILD) {
            YangModelManager.getIndexManager().indexAll(getMavenProjectFacade().getProject());
            try {
                while (YangModelManager.getIndexManager().awaitingJobsCount() > 0) {
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                // ignore
            }
        }

        for (String path : includedFiles) {
            final IFile ifile = YangCorePlugin.getIFileFromFile(new File(ds.getBasedir(), path));
            if (ifile != null) {
                YangParserUtil.validateYangFile(YangCorePlugin.createYangFile(ifile).getBuffer().getContents()
                        .toCharArray(), ifile.getProject(), new IYangValidationListener() {

                    @Override
                    public void validationError(String msg, int lineNumber, int charStart, int charEnd) {
                        YangCorePlugin.createProblemMarker(ifile, msg, lineNumber, charStart, charEnd);
                    }

                    @Override
                    public void syntaxError(String msg, int lineNumber, int charStart, int charEnd) {
                        YangCorePlugin.createProblemMarker(ifile, msg, lineNumber, charStart, charEnd);
                    }
                });
            }
        }
        Set<File> outputDirs = new HashSet<>();
        YangGeneratorConfiguration[] confs = maven.getMojoParameterValue(getSession().getCurrentProject(),
                getMojoExecution(), YangM2EPlugin.YANG_CODE_GENERATORS, YangGeneratorConfiguration[].class, monitor);
        if (confs != null) {
            for (YangGeneratorConfiguration conf : confs) {
                if (conf.getOutputBaseDir() != null) {
                    outputDirs.add(conf.getOutputBaseDir());
                }
            }
        }

        boolean isCleanRequired = YangUIPlugin.getDefault().getPreferenceStore()
                .getBoolean(YangPreferenceConstants.M2E_PLUGIN_CLEAN_TARGET);

        if (isCleanRequired) {
            for (File outputDir : outputDirs) {
                IContainer[] containers = ResourcesPlugin.getWorkspace().getRoot()
                        .findContainersForLocationURI(URIUtil.toURI(outputDir.getAbsolutePath()));
                if (containers != null && containers.length > 0) {
                    containers[0].delete(true, monitor);
                }
            }
        }

        Set<IProject> result = super.build(kind, monitor);

        for (Throwable ex : getSession().getResult().getExceptions()) {
            YangCorePlugin.log(ex);
        }

        for (File outputDir : outputDirs) {
            buildContext.refresh(outputDir);
        }

        return result;
    }
}
