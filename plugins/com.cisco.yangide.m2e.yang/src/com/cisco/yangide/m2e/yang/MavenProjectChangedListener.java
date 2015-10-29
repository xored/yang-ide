/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.m2e.yang;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.internal.project.registry.MavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectChangedListener;
import org.eclipse.m2e.core.project.MavenProjectChangedEvent;

/**
 * @author Konstantin Zaitsev
 * @date Jul 2, 2014
 */
@SuppressWarnings("restriction")
public class MavenProjectChangedListener implements IMavenProjectChangedListener {

    @Override
    public void mavenProjectChanged(MavenProjectChangedEvent[] events, IProgressMonitor monitor) {
        for (MavenProjectChangedEvent event : events) {
            if (event.getMavenProject() != null) {
                event.getMavenProject().setSessionProperty(MavenProjectFacade.PROP_LIFECYCLE_MAPPING,
                        new LifecycleMapping());
            }
        }
    }
}
