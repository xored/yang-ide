package com.cisco.yangide.m2e.yang;

import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;

/**
 * @author Konstantin Zaitsev
 */
public class YangBuildParticipant extends MojoExecutionBuildParticipant {

	public YangBuildParticipant(MojoExecution execution,
			boolean runOnIncremental) {
		super(execution, runOnIncremental);
	}

	@Override
	public Set<IProject> build(int kind, IProgressMonitor monitor)
			throws Exception {
		System.out.println("!!!!! build");
		return super.build(kind, monitor);
	}
}
