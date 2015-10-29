/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.core.model;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.cisco.yangide.core.IOpenable;
import com.cisco.yangide.core.OpenableElementInfo;
import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.parser.YangParserUtil;

/**
 * @author Konstantin Zaitsev
 * @date Jul 07, 2014
 */
public class YangJarEntry extends YangElement {
    private IPath path;

    public YangJarEntry(IPath path, IOpenable parent) {
        super(parent);
        this.path = path;
    }

    @Override
    protected boolean buildStructure(OpenableElementInfo info, IProgressMonitor pm,
            Map<IOpenable, OpenableElementInfo> newElements, IResource underlyingResource) throws YangModelException {
        char[] content = getContent();
        Module module = YangParserUtil.parseYangFile(content);
        ((YangFileInfo) info).setModule(module);
        info.setIsStructureKnown(true);
        return true;
    }

    @Override
    protected IStatus validateExistence(IResource underlyingResource) {
        return Status.OK_STATUS;
    }

    @Override
    protected OpenableElementInfo createElementInfo() {
        return new YangFileInfo();
    }

    @Override
    public IPath getPath() {
        return path;
    }

    public Module getModule() throws YangModelException {
        return ((YangFileInfo) getElementInfo(null)).getModule();
    }

    @Override
    public YangElementType getElementType() {
        return YangElementType.YANG_JAR_ENTRY;
    }

    private char[] getContent() throws YangModelException {
        try (JarFile file = new JarFile(getParent().getPath().toFile())) {
            ZipEntry entry = file.getEntry(path.toString());
            InputStreamReader in = new InputStreamReader(file.getInputStream(entry), "UTF-8");
            CharArrayWriter out = new CharArrayWriter();
            char[] buff = new char[1024];
            int len = 0;
            while ((len = in.read(buff)) > 0) {
                out.write(buff, 0, len);
            }
            return out.toCharArray();
        } catch (IOException e) {
            throw new YangModelException(e, 0);
        }
    }
}
