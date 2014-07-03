/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.m2e.yang;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.internal.lifecyclemapping.LifecycleMappingFactory;
import org.eclipse.m2e.core.internal.project.registry.MavenProjectFacade;
import org.eclipse.m2e.core.internal.project.registry.ProjectRegistryManager;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.AbstractCustomizableLifecycleMapping;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.MojoExecutionKey;

/**
 * @author Konstantin Zaitsev
 * @date Jul 3, 2014
 */
@SuppressWarnings("restriction")
public class LifecycleMapping extends AbstractCustomizableLifecycleMapping {

    @Override
    public List<AbstractProjectConfigurator> getProjectConfigurators(IMavenProjectFacade projectFacade,
            IProgressMonitor monitor) {
        Map<String, AbstractProjectConfigurator> configurators = LifecycleMappingFactory
                .getProjectConfigurators(projectFacade);
        if (!configurators.containsKey(YangProjectConfigurator.class.getName())) {
            configurators.put(YangProjectConfigurator.class.getName(), new YangProjectConfigurator());
        }
        return new ArrayList<AbstractProjectConfigurator>(configurators.values());
    }

    @Override
    public Map<MojoExecutionKey, List<AbstractBuildParticipant>> getBuildParticipants(
            IMavenProjectFacade projectFacade, IProgressMonitor monitor) throws CoreException {
        Map<MojoExecutionKey, List<AbstractBuildParticipant>> result = new LinkedHashMap<MojoExecutionKey, List<AbstractBuildParticipant>>();

        Map<MojoExecutionKey, List<IPluginExecutionMetadata>> mapping = projectFacade.getMojoExecutionMapping();
        Map<String, AbstractProjectConfigurator> configurators = LifecycleMappingFactory
                .getProjectConfigurators(projectFacade);
        if (!configurators.containsKey(YangProjectConfigurator.class.getName())) {
            configurators.put(YangProjectConfigurator.class.getName(), new YangProjectConfigurator());
        }
        List<MojoExecution> mojoExecutions = ((MavenProjectFacade) projectFacade).getExecutionPlan(
                ProjectRegistryManager.LIFECYCLE_DEFAULT, monitor);

        if (mojoExecutions != null) { // null if execution plan could not be calculated
            for (MojoExecution mojoExecution : mojoExecutions) {
                MojoExecutionKey mojoExecutionKey = new MojoExecutionKey(mojoExecution);
                List<IPluginExecutionMetadata> executionMetadatas = mapping.get(mojoExecutionKey);
                List<AbstractBuildParticipant> executionMappings = new ArrayList<AbstractBuildParticipant>();
                if (executionMetadatas != null) {
                    for (IPluginExecutionMetadata executionMetadata : executionMetadatas) {
                        switch (executionMetadata.getAction()) {
                        case execute:
                            if (mojoExecutionKey.getArtifactId().equals(YangM2EPlugin.YANG_MAVEN_PLUGIN)) {
                                executionMappings.add(new YangBuildParticipant(projectFacade.getMojoExecution(
                                        mojoExecutionKey, monitor), true));
                            } else {
                                executionMappings.add(LifecycleMappingFactory.createMojoExecutionBuildParicipant(
                                        projectFacade, projectFacade.getMojoExecution(mojoExecutionKey, monitor),
                                        executionMetadata));
                            }
                            break;
                        case configurator:
                            String configuratorId = LifecycleMappingFactory.getProjectConfiguratorId(executionMetadata);
                            AbstractProjectConfigurator configurator = configurators.get(configuratorId);
                            if (configurator == null) {
                                break;
                            }
                            AbstractBuildParticipant buildParticipant = configurator.getBuildParticipant(projectFacade,
                                    projectFacade.getMojoExecution(mojoExecutionKey, monitor), executionMetadata);
                            if (buildParticipant != null) {
                                executionMappings.add(buildParticipant);
                            }
                            break;
                        case ignore:
                        case error:
                            break;
                        default:
                            throw new IllegalArgumentException("Missing handling for action="
                                    + executionMetadata.getAction());
                        }
                    }
                }

                result.put(mojoExecutionKey, executionMappings);
            }
        }

        return result;
    }
}
