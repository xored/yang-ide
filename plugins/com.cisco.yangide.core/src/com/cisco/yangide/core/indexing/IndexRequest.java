/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.core.indexing;

import org.eclipse.core.runtime.IPath;

/**
 * @author Konstantin Zaitsev
 * @date Jul 1, 2014
 */
public abstract class IndexRequest implements IJob {
    protected boolean isCancelled = false;
    protected IPath containerPath;
    protected IndexManager manager;

    public IndexRequest(IPath containerPath, IndexManager manager) {
        this.containerPath = containerPath;
        this.manager = manager;
    }

    public boolean belongsTo(String projectNameOrJarPath) {
        // used to remove pending jobs because the project was deleted... not to delete index files
        // can be found either by project name or JAR path name
        return projectNameOrJarPath.equals(this.containerPath.segment(0))
                || projectNameOrJarPath.equals(this.containerPath.toString());
    }

    public void cancel() {
        this.manager.jobWasCancelled(this.containerPath);
        this.isCancelled = true;
    }

    public void ensureReadyToRun() {
    }

    public String getJobFamily() {
        return this.containerPath.toString();
    }
}
