/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ui.wizards;

/**
 * @author Konstantin Zaitsev
 * @date Jun 27, 2014
 */
public class CodeGeneratorConfig {
    private String groupId;
    private String artifactId;
    private String version = "1.0.0";
    private String genClassName;
    private String genOutputDirectory = "target/generated-sources/gen1";

    /**
     * @return the groupId
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * @param groupId the groupId to set
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * @return the artifactId
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * @param artifactId the artifactId to set
     */
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the genClassName
     */
    public String getGenClassName() {
        return genClassName;
    }

    /**
     * @param genClassName the genClassName to set
     */
    public void setGenClassName(String genClassName) {
        this.genClassName = genClassName;
    }

    /**
     * @return the genOutputDirectory
     */
    public String getGenOutputDirectory() {
        return genOutputDirectory;
    }

    /**
     * @param genOutputDirectory the genOutputDirectory to set
     */
    public void setGenOutputDirectory(String genOutputDirectory) {
        this.genOutputDirectory = genOutputDirectory;
    }
}
