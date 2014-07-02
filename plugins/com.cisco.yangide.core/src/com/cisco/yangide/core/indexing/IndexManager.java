/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.indexing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NavigableSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun;
import org.mapdb.Fun.Tuple5;

import com.cisco.yangide.core.YangCorePlugin;

/**
 * @author Konstantin Zaitsev
 * @date Jun 25, 2014
 */
public class IndexManager extends JobManager {

    private static final ElementIndexInfo[] NO_ELEMENTS = new ElementIndexInfo[0];

    private final DB db;

    /**
     * Keywords index contains the following values:
     * <ul>
     * <li>namespace</li>
     * <li>name</li>
     * <li>type</li>
     * <li>file path</li>
     * <li>ast info</li>
     * </ul>
     */
    private final NavigableSet<Fun.Tuple5<String, String, ElementIndexType, String, ElementIndexInfo>> idxKeywords;

    public IndexManager() {
        this.db = DBMaker.newFileDB(YangCorePlugin.getDefault().getStateLocation().append("index.db").toFile())
                .closeOnJvmShutdown().make();
        this.idxKeywords = db.getTreeSet("keywords");
    }

    @Override
    public String processName() {
        return "Yang indexer";
    }

    /**
     * Indexes project only on project open event.
     * 
     * @param project project to index
     */
    public void indexAll(IProject project) {
        request(new IndexAllProject(project, this));
    }

    public void addSource(IFile file) {
        request(new IndexFileRequest(file, this));
    }

    @Override
    public void shutdown() {
        super.shutdown();
        db.compact();
        db.close();
    }

    public synchronized void removeIndexFamily(IPath path) {
        removeIndex(path);
    }

    public synchronized void remove(IFile file) {
        removeIndex(file.getFullPath());
    }

    public synchronized void jobWasCancelled(IPath containerPath) {
    }

    public synchronized void removeIndex(IPath containerPath) {
        Iterator<Tuple5<String, String, ElementIndexType, String, ElementIndexInfo>> iterator = idxKeywords.iterator();
        while (iterator.hasNext()) {
            Tuple5<String, String, ElementIndexType, String, ElementIndexInfo> entry = iterator.next();
            if (containerPath.isPrefixOf(new Path(entry.d))) {
                iterator.remove();
            }
        }
    }

    public synchronized void addElementIndexInfo(ElementIndexInfo info) {
        System.err.println("Add element to index - " + info.getName() + " - " + info.getType());
        idxKeywords.add(Fun.t5(info.getNamespace(), info.getName(), info.getType(), info.getPath(), info));
    }

    public synchronized ElementIndexInfo[] search(String namespace, String name, ElementIndexType type, IPath scope) {
        ArrayList<ElementIndexInfo> infos = null;
        for (Tuple5<String, String, ElementIndexType, String, ElementIndexInfo> entry : idxKeywords) {
            boolean add = true;
            if (namespace != null && namespace.length() > 0 && !namespace.equals(entry.a)) {
                add = false;
            }

            if (type != null && type != entry.c) {
                add = false;
            }

            if (name != null && name.length() > 0 && !entry.b.startsWith(name)) {
                add = false;
            }

            if (scope != null && !scope.isPrefixOf(new Path(entry.d))) {
                add = false;
            }

            if (add) {
                if (infos == null) {
                    infos = new ArrayList<ElementIndexInfo>();
                }
                infos.add(entry.e);
            }
        }

        if (infos != null) {
            return (ElementIndexInfo[]) infos.toArray(new ElementIndexInfo[infos.size()]);
        }
        return NO_ELEMENTS;
    }
}
