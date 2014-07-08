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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.MojoExecution;
import org.codehaus.plexus.util.Scanner;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;
import org.sonatype.plexus.build.incremental.BuildContext;

import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.indexing.ElementIndexInfo;
import com.cisco.yangide.core.indexing.ElementIndexType;
import com.cisco.yangide.core.model.YangModelManager;
import com.cisco.yangide.ui.YangUIPlugin;
import com.cisco.yangide.ui.preferences.YangPreferenceConstants;

/**
 * @author Konstantin Zaitsev
 */
public class YangBuildParticipant extends MojoExecutionBuildParticipant {
    private static Pattern YANG_ERROR_0_6_1 = Pattern.compile("Error in module '([^']+)' at line (\\d+): (.+)");

    public YangBuildParticipant(MojoExecution execution, boolean runOnIncremental) {
        super(execution, runOnIncremental);
    }

    @Override
    public Set<IProject> build(int kind, IProgressMonitor monitor) throws Exception {
        IMaven maven = MavenPlugin.getMaven();
        BuildContext buildContext = getBuildContext();

        // clear markers before build
        getMavenProjectFacade().getProject().deleteMarkers(YangCorePlugin.YANGIDE_PROBLEM_MARKER, true,
                IResource.DEPTH_INFINITE);

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
            createMarker(getMavenProjectFacade().getProject(), ex);
        }

        for (File outputDir : outputDirs) {
            buildContext.refresh(outputDir);
        }

        return result;
    }

    /**
     * @param ex
     */
    private void createMarker(IProject project, Throwable ex) {
        Throwable exception = ex.getCause();
        String className = exception.getClass().getName();
        if (className.equals("org.opendaylight.yangtools.yang.parser.util.YangParseException")) {
            Throwable cause = exception.getCause();
            if (cause.getClass().getName()
                    .equals("org.opendaylight.yangtools.yang.model.parser.api.YangSyntaxErrorException")) {
                YangCorePlugin.createProblemMarker(project, cause.getMessage(), -1);
            } else {
                // handle 0.6.1 version
                String[] parts = exception.toString().split("\n");
                if (parts.length > 1) {
                    for (int i = 1; i < parts.length; i++) {
                        System.out.println(parts[i]);
                        Matcher m = YANG_ERROR_0_6_1.matcher(parts[i]);
                        if (m.matches()) {
                            String module = m.group(1);
                            ElementIndexInfo[] result = YangModelManager.search(null, null, module,
                                    ElementIndexType.MODULE, getMavenProjectFacade().getProject(), null);
                            if (result.length > 0) {
                                YangCorePlugin.createProblemMarker(result[0].getPath(), m.group(3),
                                        Integer.parseInt(m.group(2)));
                            }
                        }
                    }
                }
            }
        } else if (className.equals("org.opendaylight.yangtools.yang.parser.util.YangValidationException")) {
            YangCorePlugin.createProblemMarker(project, exception.getMessage(), -1);

        }
    }
}
