/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.m2e.yang;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.internal.lifecyclemapping.LifecycleMappingFactory;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.lifecyclemapping.model.PluginExecutionAction;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.MojoExecutionKey;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.AbstractSourcesGenerationProjectConfigurator;

/**
 * @author Konstantin Zaitsev
 * @date Jul 2, 2014
 */
@SuppressWarnings("restriction")
public class YangProjectConfigurator extends AbstractSourcesGenerationProjectConfigurator {

    @Override
    public AbstractBuildParticipant getBuildParticipant(IMavenProjectFacade projectFacade, MojoExecution execution,
            IPluginExecutionMetadata executionMetadata) {
        return new YangBuildParticipant(execution, true);
    }

    /**
     * Returns list of MojoExecutions this configurator is enabled for.
     */
    @Override
    protected List<MojoExecution> getMojoExecutions(ProjectConfigurationRequest request, IProgressMonitor monitor)
            throws CoreException {
        IMavenProjectFacade projectFacade = request.getMavenProjectFacade();

        Map<String, Set<MojoExecutionKey>> configuratorExecutions = getPatchedConfiguratorExecutions(projectFacade);

        ArrayList<MojoExecution> executions = new ArrayList<MojoExecution>();

        Set<MojoExecutionKey> executionKeys = configuratorExecutions.get(getId());
        if (executionKeys != null) {
            for (MojoExecutionKey key : executionKeys) {
                executions.add(projectFacade.getMojoExecution(key, monitor));
            }
        }

        return executions;
    }

    @Override
    protected File[] getSourceFolders(ProjectConfigurationRequest request, MojoExecution mojoExecution,
            IProgressMonitor monitor) throws CoreException {
        YangGeneratorConfiguration[] confs = getParameterValue(request.getMavenProject(),
                YangM2EPlugin.YANG_CODE_GENERATORS, YangGeneratorConfiguration[].class, mojoExecution, monitor);
        if (confs == null) {
            return new File[0];
        }
        File[] sources = new File[confs.length + 1];
        sources[0] = getParameterValue(request.getMavenProject(), YangM2EPlugin.YANG_FILES_ROOT_DIR, File.class,
                mojoExecution, monitor);
        // set default value
        if (sources[0] == null) {
            sources[0] = new File(request.getMavenProject().getBasedir(), YangM2EPlugin.YANG_FILES_ROOT_DIR_DEFAULT);
        }
        for (int i = 0; i < confs.length; i++) {
            sources[i + 1] = confs[i].getOutputBaseDir();
        }
        return sources;
    }

    private Map<String, Set<MojoExecutionKey>> getPatchedConfiguratorExecutions(IMavenProjectFacade projectFacade) {
        Map<String, Set<MojoExecutionKey>> configuratorExecutions = new HashMap<String, Set<MojoExecutionKey>>();
        Map<MojoExecutionKey, List<IPluginExecutionMetadata>> executionMapping = projectFacade
                .getMojoExecutionMapping();
        for (Map.Entry<MojoExecutionKey, List<IPluginExecutionMetadata>> entry : executionMapping.entrySet()) {
            List<IPluginExecutionMetadata> metadatas = entry.getValue();
            if (metadatas != null) {
                if (entry.getKey().getArtifactId().equals(YangM2EPlugin.YANG_MAVEN_PLUGIN)) {
                    String configuratorId = this.getClass().getName();
                    Set<MojoExecutionKey> executions = configuratorExecutions.get(configuratorId);
                    if (executions == null) {
                        executions = new LinkedHashSet<MojoExecutionKey>();
                        configuratorExecutions.put(configuratorId, executions);
                    }
                    executions.add(entry.getKey());
                } else {
                    for (IPluginExecutionMetadata metadata : metadatas) {
                        if (metadata.getAction() == PluginExecutionAction.configurator) {
                            String configuratorId = LifecycleMappingFactory.getProjectConfiguratorId(metadata);
                            if (configuratorId != null) {
                                Set<MojoExecutionKey> executions = configuratorExecutions.get(configuratorId);
                                if (executions == null) {
                                    executions = new LinkedHashSet<MojoExecutionKey>();
                                    configuratorExecutions.put(configuratorId, executions);
                                }
                                executions.add(entry.getKey());
                            }
                        }
                    }
                }
            }
        }
        return configuratorExecutions;
    }
}
