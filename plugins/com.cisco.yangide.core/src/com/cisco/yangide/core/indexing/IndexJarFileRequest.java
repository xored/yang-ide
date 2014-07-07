/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.indexing;

import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import com.cisco.yangide.core.CoreUtil;
import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.core.model.YangFileInfo;
import com.cisco.yangide.core.model.YangJarEntry;

/**
 * @author Konstantin Zaitsev
 * @date Jul 1, 2014
 */
public class IndexJarFileRequest extends IndexRequest {

    private IPath path;

    public IndexJarFileRequest(IPath path, IndexManager manager) {
        super(path, manager);
        this.path = path;
    }

    @Override
    public boolean execute(IProgressMonitor progressMonitor) {
        if (this.isCancelled || progressMonitor != null && progressMonitor.isCanceled()) {
            return true;
        }

        try (JarFile jarFile = new JarFile(path.toFile())) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().startsWith("META-INF/yang/") && CoreUtil.isYangLikeFileName(entry.getName())) {
                    IPath jarEntryPath = path.append(entry.getName());
                    manager.removeIndex(jarEntryPath);
                    YangJarEntry element = YangCorePlugin.createJarEntry(path, entry.getName());
                    YangFileInfo info = (YangFileInfo) element.getElementInfo(progressMonitor);
                    manager.addModule(info.getModule(), path, entry.getName());
                }
            }
        } catch (IOException e) {
            YangCorePlugin.log(e);
        } catch (YangModelException e) {
            YangCorePlugin.log(e);
        }
        return true;
    }

    @Override
    public String toString() {
        return "indexing " + path;
    }
}
