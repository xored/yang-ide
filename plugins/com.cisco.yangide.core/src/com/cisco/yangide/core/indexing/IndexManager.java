/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.indexing;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NavigableSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun;
import org.mapdb.Fun.Tuple5;

import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.dom.ASTVisitor;
import com.cisco.yangide.core.dom.GroupingDefinition;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.TypeDefinition;

/**
 * @author Konstantin Zaitsev
 * @date Jun 25, 2014
 */
public class IndexManager extends JobManager {

    private static final ElementIndexInfo[] NO_ELEMENTS = new ElementIndexInfo[0];

    private DB db;

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
    private NavigableSet<Fun.Tuple5<String, String, ElementIndexType, String, ElementIndexInfo>> idxKeywords;

    public IndexManager() {
        File indexFile = YangCorePlugin.getDefault().getStateLocation().append("index.db").toFile();
        this.db = DBMaker.newFileDB(indexFile).closeOnJvmShutdown().make();
        try {
            this.idxKeywords = db.getTreeSet("keywords");
        } catch (Throwable e) {
            // delete index db incase if index is broken and reopen with clean state
            this.db.close();
            DBMaker.newFileDB(indexFile).deleteFilesAfterClose().make().close();
            this.db = DBMaker.newFileDB(indexFile).closeOnJvmShutdown().make();
            this.idxKeywords = db.getTreeSet("keywords");
            // reindex all projects
            for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
                if (YangCorePlugin.isYangProject(project)) {
                    indexAll(project);
                }
            }
        }
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

    public void addJarFile(IPath file) {
        request(new IndexJarFileRequest(file, this));
    }

    @Override
    public void shutdown() {
        super.shutdown();
        db.commit();
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
        System.err.println("Add element to index - " + info.getNamespace() + " - " + info.getName() + " - "
                + info.getType());
        String path = info.getPath();
        if (info.getEntry() != null && info.getEntry().length() > 0) {
            path = path + "/" + info.getEntry();
        }

        idxKeywords.add(Fun.t5(info.getNamespace(), info.getName(), info.getType(), path, info));
        db.commit();
    }

    public void addModule(Module module, final IPath path, final String entry) {
        if (module != null && module.getNamespace() != null && module.getNamespace().getValue() != null) {
            final String namespace = module.getNamespace().getValue().toASCIIString();

            module.accept(new ASTVisitor() {
                @Override
                public boolean visit(Module module) {
                    addElementIndexInfo(new ElementIndexInfo(module, namespace, ElementIndexType.MODULE, path, entry));
                    return true;
                }

                @Override
                public boolean visit(TypeDefinition typeDefinition) {
                    addElementIndexInfo(new ElementIndexInfo(typeDefinition, namespace, ElementIndexType.TYPE, path,
                            entry));
                    return true;
                }

                @Override
                public boolean visit(GroupingDefinition groupingDefinition) {
                    addElementIndexInfo(new ElementIndexInfo(groupingDefinition, namespace, ElementIndexType.GROUPING,
                            path, entry));
                    return true;
                }
            });
        }
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
            return infos.toArray(new ElementIndexInfo[infos.size()]);
        }
        return NO_ELEMENTS;
    }
}
