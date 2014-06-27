/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.m2e.yang;

import java.io.File;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.AbstractSourcesGenerationProjectConfigurator;

/**
 * @author Konstantin Zaitsev
 */
public class YangProjectConfigurator extends AbstractSourcesGenerationProjectConfigurator {

    @Override
    public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
    }

    @Override
    public AbstractBuildParticipant getBuildParticipant(IMavenProjectFacade projectFacade, MojoExecution execution,
            IPluginExecutionMetadata executionMetadata) {
        return new YangBuildParticipant(execution, true);
    }

    @Override
    protected File[] getSourceFolders(ProjectConfigurationRequest request, MojoExecution mojoExecution,
            IProgressMonitor monitor) throws CoreException {
        YangGeneratorConfiguration[] confs = getParameterValue(request.getMavenProject(), "codeGenerators",
                YangGeneratorConfiguration[].class, mojoExecution, monitor);
        if (confs == null) {
            return new File[0];
        }
        File[] sources = new File[confs.length + 1];
        sources[0] = getParameterValue(request.getMavenProject(), "yangFilesRootDir", File.class, mojoExecution,
                monitor);
        for (int i = 0; i < confs.length; i++) {
            sources[i + 1] = confs[i].getOutputBaseDir();
        }
        return sources;
    }
}
