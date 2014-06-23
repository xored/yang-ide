package com.cisco.yangide.m2e.yang;

import java.io.File;
import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.codehaus.plexus.util.Scanner;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;
import org.sonatype.plexus.build.incremental.BuildContext;

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
		IMaven maven = MavenPlugin.getMaven();
		BuildContext buildContext = getBuildContext();

		File[] sources = maven.getMojoParameterValue(getSession()
				.getCurrentProject(), getMojoExecution(), "yangFilesRootDir",
				File[].class, monitor);

		boolean changed = false;
		
		for (File source : sources) {
			Scanner ds = buildContext.newScanner(source); // delta or full scanner
			ds.scan();
			String[] includedFiles = ds.getIncludedFiles();
			if (includedFiles != null && includedFiles.length > 0) {
				changed = true;
			}
		}

		if (!changed) {
			return null;
		}

		Set<IProject> result = super.build(kind, monitor);

		YangGeneratorConfiguration[] confs = maven.getMojoParameterValue(
				getSession().getCurrentProject(), getMojoExecution(), "codeGenerators",
				YangGeneratorConfiguration[].class, monitor);
		if (confs != null) {
			for (YangGeneratorConfiguration conf : confs) {
				if (conf.getOutputBaseDir() != null) {
					buildContext.refresh(conf.getOutputBaseDir());
				}
			}
		}

		return result;
	}
}
