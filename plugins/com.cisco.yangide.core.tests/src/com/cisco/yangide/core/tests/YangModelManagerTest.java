/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.core.tests;

import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;

import com.cisco.yangide.core.IOpenable;
import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.model.YangFile;
import com.cisco.yangide.core.model.YangFileInfo;
import com.cisco.yangide.core.model.YangFolder;
import com.cisco.yangide.core.model.YangModel;
import com.cisco.yangide.core.model.YangProject;

/**
 * @author Konstantin Zaitsev
 * @date Jun 30, 2014
 */
@SuppressWarnings("restriction")
public class YangModelManagerTest extends AbstractMavenProjectTestCase {

    public void testFileInfoLoading() throws Exception {
        YangModel model = YangCorePlugin.getYangModel();
        assertNotNull(model);
        YangProject[] projects = model.getYangProjects();
        assertEquals(0, projects.length);

        ResolverConfiguration configuration = new ResolverConfiguration();
        importProject("projects/yang/yang-p001/pom.xml", configuration);
        waitForJobsToComplete();

        projects = model.getYangProjects();
        assertEquals(1, projects.length);
        YangProject prj = projects[0];

        assertNotNull(prj.getChildren());
        assertEquals(1, prj.getChildren().length);
        YangFolder folder = (YangFolder) prj.getChildren()[0];
        assertEquals("src/main/yang", folder.getName());
        IOpenable[] files = folder.getChildren();
        assertEquals(1, files.length);
        YangFile file = (YangFile) files[0];
        assertEquals("simple-string-demo.yang", file.getName());
        YangFileInfo elementInfo = (YangFileInfo) file.getElementInfo(null);
        assertNotNull(elementInfo);
        // check that AST parsed
        assertNotNull(elementInfo.getModule());
        YangFile newFile = YangCorePlugin.createYangFile(file.getResource());
        // check new handler with the same info
        assertTrue(newFile != file);
        assertTrue(newFile.getElementInfo(null) == elementInfo);
    }
}
