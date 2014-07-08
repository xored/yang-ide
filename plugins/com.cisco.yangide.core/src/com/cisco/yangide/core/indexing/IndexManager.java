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
import org.mapdb.Fun.Tuple6;

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
     * <li>revision</li>
     * <li>name</li>
     * <li>type</li>
     * <li>file path (scope, for JAR entries path to project)</li>
     * <li>ast info</li>
     * </ul>
     */
    private NavigableSet<Fun.Tuple6<String, String, String, ElementIndexType, String, ElementIndexInfo>> idxKeywords;

    public IndexManager() {
        File indexFile = YangCorePlugin.getDefault().getStateLocation().append("index.db").toFile();
        this.db = DBMaker.newFileDB(indexFile).closeOnJvmShutdown().make();
        try {
            this.idxKeywords = db.getTreeSet("keywords");

            if (!idxKeywords.isEmpty() && !(idxKeywords.first() instanceof Fun.Tuple6)) {
                cleanDB(indexFile);
            }
        } catch (Throwable e) {
            cleanDB(indexFile);
        }
    }

    /**
     * Cleans DB by recreate index file.
     *
     * @param indexFile index file
     */
    private void cleanDB(File indexFile) {
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

    public void addJarFile(IProject project, IPath file) {
        request(new IndexJarFileRequest(project, file, this));
    }

    @Override
    public void shutdown() {
        super.shutdown();
        db.commit();
        db.compact();
        db.close();
    }

    public synchronized void removeIndexFamily(IProject project) {
        Iterator<Tuple6<String, String, String, ElementIndexType, String, ElementIndexInfo>> iterator = idxKeywords
                .iterator();
        while (iterator.hasNext()) {
            Tuple6<String, String, String, ElementIndexType, String, ElementIndexInfo> entry = iterator.next();
            if (project.getFullPath().toString().equals(entry.f.getProject())) {
                iterator.remove();
            }
        }
    }

    public synchronized void remove(IFile file) {
        removeIndex(file.getFullPath());
    }

    public synchronized void jobWasCancelled(IPath containerPath) {
    }

    public synchronized void removeIndex(IPath containerPath) {
        Iterator<Tuple6<String, String, String, ElementIndexType, String, ElementIndexInfo>> iterator = idxKeywords
                .iterator();
        while (iterator.hasNext()) {
            Tuple6<String, String, String, ElementIndexType, String, ElementIndexInfo> entry = iterator.next();
            if (containerPath.isPrefixOf(new Path(entry.e))) {
                iterator.remove();
            }
        }
    }

    public synchronized void addElementIndexInfo(ElementIndexInfo info) {
        System.err.println("[I] " + info.getNamespace() + "@" + info.getRevision() + " - " + info.getName() + " - "
                + info.getType());
        idxKeywords.add(Fun.t6(info.getNamespace(), info.getRevision(), info.getName(), info.getType(), info.getPath(),
                info));
        db.commit();
    }

    public void addModule(Module module, final IProject project, final IPath path, final String entry) {
        if (module != null && module.getNamespace() != null && module.getNamespace() != null
                && module.getRevision() != null && module.getRevision() != null) {
            final String namespace = module.getNamespace().toASCIIString();
            final String revision = module.getRevision();
            module.accept(new ASTVisitor() {
                @Override
                public boolean visit(Module module) {
                    addElementIndexInfo(new ElementIndexInfo(module, namespace, revision, ElementIndexType.MODULE,
                            project, path, entry));
                    return true;
                }

                @Override
                public boolean visit(TypeDefinition typeDefinition) {
                    addElementIndexInfo(new ElementIndexInfo(typeDefinition, namespace, revision,
                            ElementIndexType.TYPE, project, path, entry));
                    return true;
                }

                @Override
                public boolean visit(GroupingDefinition groupingDefinition) {
                    addElementIndexInfo(new ElementIndexInfo(groupingDefinition, namespace, revision,
                            ElementIndexType.GROUPING, project, path, entry));
                    return true;
                }
            });
        }
    }

    public synchronized ElementIndexInfo[] search(String namespace, String revision, String name,
            ElementIndexType type, IProject project, IPath scope) {
        ArrayList<ElementIndexInfo> infos = null;
        for (Tuple6<String, String, String, ElementIndexType, String, ElementIndexInfo> entry : idxKeywords) {
            boolean add = true;
            if (namespace != null && namespace.length() > 0 && !namespace.equals(entry.a)) {
                add = false;
            }

            if (revision != null && revision.length() > 0 && !revision.equals(entry.b)) {
                add = false;
            }

            if (type != null && type != entry.d) {
                add = false;
            }

            if (name != null && name.length() > 0 && !entry.c.startsWith(name)) {
                add = false;
            }

            if (project != null && !entry.f.getProject().equals(project.getFullPath().toString())) {
                add = false;
            }

            if (scope != null && !scope.isPrefixOf(new Path(entry.e))) {
                add = false;
            }

            if (add) {
                if (infos == null) {
                    infos = new ArrayList<ElementIndexInfo>();
                }
                infos.add(entry.f);
            }
        }

        if (infos != null) {
            return infos.toArray(new ElementIndexInfo[infos.size()]);
        }
        return NO_ELEMENTS;
    }
}
