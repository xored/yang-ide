/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.refactoring;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.jar.JarFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;

import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.BaseReference;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.ModuleImport;
import com.cisco.yangide.core.dom.QName;
import com.cisco.yangide.core.dom.SubModuleInclude;
import com.cisco.yangide.core.dom.TypeReference;
import com.cisco.yangide.core.dom.UsesNode;
import com.cisco.yangide.core.indexing.ElementIndexInfo;
import com.cisco.yangide.core.indexing.ElementIndexReferenceInfo;
import com.cisco.yangide.core.indexing.ElementIndexType;
import com.cisco.yangide.core.model.YangModelManager;
import com.cisco.yangide.core.parser.YangFormattingPreferences;
import com.cisco.yangide.core.parser.YangParserUtil;
import com.cisco.yangide.ui.YangUIPlugin;
import com.cisco.yangide.ui.preferences.YangPreferenceConstants;

/**
 * @author Konstantin Zaitsev
 * @date Aug 1, 2014
 */
public final class RefactorUtil {

    /**
     * Protect from initialization.
     */
    private RefactorUtil() {
        // empty block
    }

    /**
     * @param project project
     * @param node reference node
     * @return index information about original node that used as reference or <code>null</code> if
     * node not found.
     * @see RefactorUtil#resolveIndexInfo(ElementIndexInfo)
     */
    public static ElementIndexInfo getByReference(IProject project, ASTNode node) {
        QName qname = null;
        ElementIndexType type = null;
        if (node instanceof UsesNode) {
            qname = ((UsesNode) node).getGrouping();
            type = ElementIndexType.GROUPING;
        } else if (node instanceof TypeReference) {
            qname = ((TypeReference) node).getType();
            type = ElementIndexType.TYPE;
        } else if (node instanceof BaseReference) {
            qname = ((BaseReference) node).getType();
            type = ElementIndexType.IDENTITY;
        } else if (node instanceof ModuleImport) {
            ModuleImport moduleImport = (ModuleImport) node;
            qname = new QName(moduleImport.getName(), moduleImport.getPrefix(), moduleImport.getName(),
                    moduleImport.getRevision());
            type = ElementIndexType.MODULE;
        } else if (node instanceof SubModuleInclude) {
            SubModuleInclude include = (SubModuleInclude) node;
            qname = new QName(include.getName(), null, include.getName(), include.getRevision());
            type = ElementIndexType.SUBMODULE;
        }

        if (qname != null) {
            ElementIndexInfo[] infos = YangModelManager.search(qname.getModule(), qname.getRevision(), qname.getName(),
                    type, project, null);
            if (infos.length > 0) {
                return infos[0];
            }
        }
        return null;
    }

    /**
     * @param info index info
     * @return resolved AST node or <code>null</code> if node not found
     */
    public static ASTNode resolveIndexInfo(ElementIndexInfo info) {
        try {
            Module module = null;
            if (info.getEntry() != null && !info.getEntry().isEmpty()) {
                module = YangCorePlugin.createJarEntry(new Path(info.getPath()), info.getEntry()).getModule();
            } else {
                module = getModule(info.getPath());
            }
            return module.getNodeAtPosition(info.getStartPosition());
        } catch (YangModelException e) {
            return null;
        }
    }

    /**
     * @param info index info
     * @return resolved AST node or <code>null</code> if node not found
     */
    public static ASTNode resolveIndexInfo(ElementIndexReferenceInfo info) {
        try {
            return getModule(info.getPath()).getNodeAtPosition(info.getStartPosition());
        } catch (YangModelException e) {
            return null;
        }
    }

    /**
     * @param info index info
     * @return string content of AST node or <code>null</code> if node not found
     * @throws CoreException IO errors
     */
    public static String loadIndexInfoContent(ElementIndexInfo info) throws CoreException {
        ASTNode node = resolveIndexInfo(info);
        if (node != null) {
            if (info.getEntry() != null && !info.getEntry().isEmpty()) {
                try (JarFile jarFile = new JarFile(new Path(info.getPath()).toFile())) {
                    try (InputStreamReader reader = new InputStreamReader(jarFile.getInputStream(jarFile.getEntry(info
                            .getEntry())), "UTF-8")) { //$NON-NLS-1$
                        char[] cbuf = new char[node.getBodyLength()];
                        reader.skip(node.getBodyStartPosition());
                        reader.read(cbuf, 0, node.getBodyLength());
                        return new String(cbuf);
                    }
                } catch (IOException e) {
                    throw new CoreException(new Status(IStatus.ERROR, YangRefactoringPlugin.PLUGIN_ID, "Error", e)); //$NON-NLS-1$
                }
            } else {
                IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(info.getPath()));
                return loadNodeContent(node, file);
            }
        }
        return null;
    }

    /**
     * @param node AST node
     * @param file file where node located
     * @return string content of node body
     * @throws CoreException IO errors
     */
    public static String loadNodeContent(ASTNode node, IFile file) throws CoreException {
        try (InputStreamReader reader = new InputStreamReader(file.getContents(), "UTF-8")) { //$NON-NLS-1$
            char[] cbuf = new char[node.getBodyLength()];
            reader.skip(node.getBodyStartPosition());
            reader.read(cbuf, 0, node.getBodyLength());
            return new String(cbuf);
        } catch (IOException | CoreException e) {
            throw new CoreException(new Status(IStatus.ERROR, YangRefactoringPlugin.PLUGIN_ID, "Error", e)); //$NON-NLS-1$
        }
    }

    /**
     * Format code snippet according formating preferences.
     *
     * @param snipped code snippet
     * @param indentationLevel indentation level
     * @return formatted code
     */
    public static String formatCodeSnipped(String snipped, int indentationLevel) {
        YangFormattingPreferences pref = new YangFormattingPreferences();

        IPreferenceStore store = YangUIPlugin.getDefault().getPreferenceStore();
        pref.setSpaceForTabs(store.getBoolean(YangPreferenceConstants.FMT_INDENT_SPACE));
        pref.setIndentSize(store.getInt(YangPreferenceConstants.FMT_INDENT_WIDTH));
        pref.setCompactImport(store.getBoolean(YangPreferenceConstants.FMT_COMPACT_IMPORT));
        pref.setFormatComment(store.getBoolean(YangPreferenceConstants.FMT_COMMENT));
        pref.setFormatStrings(store.getBoolean(YangPreferenceConstants.FMT_STRING));
        pref.setMaxLineLength(store.getInt(YangPreferenceConstants.FMT_MAX_LINE_LENGTH));

        return YangParserUtil.formatYangSource(pref, snipped.toCharArray(), indentationLevel,
                System.getProperty("line.separator")); //$NON-NLS-1$
    }

    /**
     * @param node node to calculate
     * @return node level
     */
    public static int getNodeLevel(ASTNode node) {
        int nodeLevel = -1;
        ASTNode parent = node;
        while (parent != null) {
            parent = parent.getParent();
            nodeLevel++;
        }
        return nodeLevel >= 0 ? nodeLevel : 0;
    }

    private static Module getModule(String path) throws YangModelException {
        return YangCorePlugin.createYangFile(path).getModule();
    }

}
