/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.indexing;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NavigableSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun;
import org.mapdb.Fun.Tuple6;

import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.dom.ASTVisitor;
import com.cisco.yangide.core.dom.GroupingDefinition;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.SubModule;
import com.cisco.yangide.core.dom.TypeDefinition;

/**
 * Provides functionality to index AST nodes and search item in index.
 *
 * @author Konstantin Zaitsev
 * @date Jun 25, 2014
 */
public class IndexManager extends JobManager {

    /**
     * Stores index version, it is required increment version on each major changes of indexing
     * algorithm or indexed data.
     */
    private static final int INDEX_VERSION = 3;

    /**
     * Index DB file path.
     */
    private static final String INDEX_PATH = "index_" + INDEX_VERSION + ".db";

    /**
     * Empty result array.
     */
    private static final ElementIndexInfo[] NO_ELEMENTS = new ElementIndexInfo[0];

    /**
     * Index database.
     */
    private DB db;

    /**
     * Keywords index contains the following values:
     * <ul>
     * <li>module</li>
     * <li>revision</li>
     * <li>name</li>
     * <li>type</li>
     * <li>file path (scope, for JAR entries path to project)</li>
     * <li>ast info</li>
     * </ul>
     */
    private NavigableSet<Fun.Tuple6<String, String, String, ElementIndexType, String, ElementIndexInfo>> idxKeywords;

    /**
     * Resources index that contains relation of indexed resource and modification stamp of resource
     * when indexed was performed.
     */
    private BTreeMap<String, Long> idxResources;

    public IndexManager() {
        File indexFile = YangCorePlugin.getDefault().getStateLocation().append(INDEX_PATH).toFile();
        try {
            initDB(indexFile, false);

            if (!idxKeywords.isEmpty() && !(idxKeywords.first() instanceof Fun.Tuple6)) {
                initDB(indexFile, true);
            }
        } catch (Throwable e) {
            initDB(indexFile, true);
        }
    }

    /**
     * Inits database by cleans old version of DB and recreate current index file if necessary.
     *
     * @param indexFile index file
     * @param cleanAll if <code>true</code> remove old version and current index also otherwise
     * remove only old version of index DB.
     */
    private void initDB(File indexFile, final boolean cleanAll) {
        // delete index db in case if index is broken and reopen with clean state
        if (this.db != null) {
            this.db.close();
        }
        File[] files = indexFile.getParentFile().listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("index") && (cleanAll || !name.startsWith("index_" + INDEX_VERSION));
            }
        });
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        this.db = DBMaker.newFileDB(indexFile).closeOnJvmShutdown().make();
        this.idxKeywords = db.getTreeSet("keywords");
        this.idxResources = db.getTreeMap("resources");
        indexAllProjects();
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
        // this workaround need in case of old project that has target copied yang file but this
        // files not ignored by JDT yet.
        if ("target".equals(file.getProjectRelativePath().segment(0))) {
            return;
        }
        // in case of file not change, skip indexing
        String path = file.getFullPath().toString();
        if (idxResources.containsKey(path) && idxResources.get(path) == file.getModificationStamp()) {
            System.err.println("[x] " + file);
            return;
        }
        request(new IndexFileRequest(file, this));
    }

    public void addWorkingCopy(IFile file) {
        request(new IndexFileRequest(file, this));
    }

    public void addJarFile(IProject project, IPath file) {
        // in case of file not change, skip indexing
        if (idxResources.containsKey(file.toString())
                && idxResources.get(file.toString()) == file.toFile().lastModified()) {
            System.err.println("[x] " + file);
            return;
        }
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
            if (project.getName().equals(entry.f.getProject())) {
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
        idxResources.remove(containerPath.toString());
    }

    public synchronized void addElementIndexInfo(ElementIndexInfo info) {
        System.err.println("[I] " + info.getModule() + "@" + info.getRevision() + " - " + info.getName() + " - "
                + info.getType());
        idxKeywords.add(Fun.t6(info.getModule(), info.getRevision(), info.getName(), info.getType(), info.getPath(),
                info));
    }

    public void addModule(Module module, final IProject project, final IPath path, final String entry) {
        if (module != null && module.getRevision() != null && module.getRevision() != null) {
            final String revision = module.getRevision();
            final String moduleName = module.getName();
            module.accept(new ASTVisitor() {
                @Override
                public boolean visit(Module module) {
                    addElementIndexInfo(new ElementIndexInfo(module, moduleName, revision, ElementIndexType.MODULE,
                            project, path, entry));
                    return true;
                }

                @Override
                public boolean visit(SubModule module) {
                    addElementIndexInfo(new ElementIndexInfo(module, moduleName, revision, ElementIndexType.SUBMODULE,
                            project, path, entry));
                    return true;
                }

                @Override
                public boolean visit(TypeDefinition typeDefinition) {
                    addElementIndexInfo(new ElementIndexInfo(typeDefinition, moduleName, revision,
                            ElementIndexType.TYPE, project, path, entry));
                    return true;
                }

                @Override
                public boolean visit(GroupingDefinition groupingDefinition) {
                    addElementIndexInfo(new ElementIndexInfo(groupingDefinition, moduleName, revision,
                            ElementIndexType.GROUPING, project, path, entry));
                    return true;
                }
            });
            db.commit();
        }
    }

    public synchronized ElementIndexInfo[] search(String module, String revision, String name, ElementIndexType type,
            IProject project, IPath scope) {
        ArrayList<ElementIndexInfo> infos = null;
        for (Tuple6<String, String, String, ElementIndexType, String, ElementIndexInfo> entry : idxKeywords) {
            if (module != null && module.length() > 0 && !module.equals(entry.a)) {
                continue;
            }

            if (revision != null && revision.length() > 0 && !revision.equals(entry.b)) {
                continue;
            }

            if (type != null && type != entry.d) {
                continue;
            }

            if (name != null && name.length() > 0 && !entry.c.equals(name)) {
                continue;
            }

            if (project != null && !entry.f.getProject().equals(project.getName())) {
                continue;
            }

            if (scope != null && !scope.isPrefixOf(new Path(entry.e))) {
                continue;
            }

            if (infos == null) {
                infos = new ArrayList<ElementIndexInfo>();
            }
            infos.add(entry.f);
        }

        if (infos != null) {
            return infos.toArray(new ElementIndexInfo[infos.size()]);
        }
        return NO_ELEMENTS;
    }

    private void indexAllProjects() {
        // reindex all projects
        for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
            if (YangCorePlugin.isYangProject(project)) {
                indexAll(project);
            }
        }
    }

    protected void fileAddedToIndex(IPath path, long modificationStamp) {
        idxResources.put(path.toString(), modificationStamp);
    }
}
