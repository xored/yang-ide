/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
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
        IProject project1 = importProject("projects/yang/yang-p001/pom.xml", configuration);
        waitForJobsToComplete();

        project1.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
        project1.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);
        waitForJobsToComplete();

        // assertNoErrors(project1);

        IJavaProject javaProject1 = JavaCore.create(project1);
        IClasspathEntry[] cp1 = javaProject1.getRawClasspath();

        assertNotNull(ClasspathHelpers.getClasspathEntry(cp1, "/yang-p001/target/generated-sources/sal"));
        assertNotNull(ClasspathHelpers.getClasspathEntry(cp1, "/yang-p001/src/main/yang"));

        IFile file = project1
                .getFile("target/generated-sources/sal/org/opendaylight/yang/gen/v1/urn/simple/string/demo/rev130618/TypedefString.java");
        assertTrue(file.isSynchronized(IResource.DEPTH_ZERO));
        assertTrue(file.isDerived(IResource.CHECK_ANCESTORS));
    }
}
