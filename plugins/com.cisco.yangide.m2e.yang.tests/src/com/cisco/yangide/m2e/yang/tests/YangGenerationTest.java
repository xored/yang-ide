package com.cisco.yangide.m2e.yang.tests;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;
import org.eclipse.m2e.tests.common.ClasspathHelpers;

@SuppressWarnings("restriction")
public class YangGenerationTest extends AbstractMavenProjectTestCase {
	public void test_p001_simple() throws Exception {
		ResolverConfiguration configuration = new ResolverConfiguration();
		IProject project1 = importProject("projects/yang/yang-p001/pom.xml",
				configuration);
		waitForJobsToComplete();

		project1.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
		project1.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);
		waitForJobsToComplete();

		assertNoErrors(project1);

		IJavaProject javaProject1 = JavaCore.create(project1);
		IClasspathEntry[] cp1 = javaProject1.getRawClasspath();

		ClasspathHelpers
				.assertClasspath(
						new String[] {
								"/yang-p001/src/main/java", //
								"/yang-p001/target/generated-sources/sal", //
								"/yang-p001/src/test/java", //
								"org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.7", //
								"org.eclipse.m2e.MAVEN2_CLASSPATH_CONTAINER" //
						}, cp1);

		IFile file = project1
				.getFile("target/generated-sources/sal/org/opendaylight/yang/gen/v1/urn/simple/string/demo/rev130618/TypedefString.java");
		assertTrue(file.isSynchronized(IResource.DEPTH_ZERO));
		assertTrue(file.isDerived(IResource.CHECK_ANCESTORS));
	}
}
