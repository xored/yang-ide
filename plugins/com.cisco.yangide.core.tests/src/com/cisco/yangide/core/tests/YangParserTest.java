/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.tests;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.SubModule;
import com.cisco.yangide.core.parser.YangParserUtil;

/**
 * @author Konstantin Zaitsev
 * @date Jul 14, 2014
 */
public class YangParserTest extends TestCase {
    public void testSimpleParser() throws Exception {
        try (InputStream in = FileLocator.openStream(Platform.getBundle("com.cisco.yangide.core.tests"), new Path(
                "yang/simple_import.yang"), false)) {

            Module module = YangParserUtil.parseYangFile(getContent(in));
            assertEquals("my-crypto", module.getName());
            assertEquals(7, module.getNameStartPosition());
            assertEquals(0, module.getStartPosition());
            // assertEquals(328, module.getLength());
        }
    }

    public void testNodeAtPostion() throws Exception {
        try (InputStream in = FileLocator.openStream(Platform.getBundle("com.cisco.yangide.core.tests"), new Path(
                "yang/simple_import.yang"), false)) {

            Module module = YangParserUtil.parseYangFile(getContent(in));
            assertEquals(module, module.getNodeAtPosition(1));
            assertEquals(module.getImports().get("crypto-base"), module.getNodeAtPosition(100));
        }
    }

    public void testIncompleteParse() throws Exception {
        try (InputStream in = FileLocator.openStream(Platform.getBundle("com.cisco.yangide.core.tests"), new Path(
                "yang/simple_import_incomplete.yang"), false)) {

            Module module = YangParserUtil.parseYangFile(getContent(in));
            assertNotNull(module);
            assertEquals(1, module.getImports().size());
        }
    }

    public void testSubmoduleParse() throws Exception {
        try (InputStream in = FileLocator.openStream(Platform.getBundle("com.cisco.yangide.core.tests"), new Path(
                "yang/acme-types.yang"), false)) {

            SubModule module = (SubModule) YangParserUtil.parseYangFile(getContent(in));
            assertNotNull(module);
            assertEquals("acme-system", module.getParentModule().getValue());
            assertEquals("acme", module.getParentPrefix());
        }
    }

    public void testParseComments() throws Exception {
        try (InputStream in = FileLocator.openStream(Platform.getBundle("com.cisco.yangide.core.tests"), new Path(
                "yang/yang-ext.yang"), false)) {

            Module module = YangParserUtil.parseYangFile(getContent(in));
            assertNotNull(module);
        }
    }

    private static char[] getContent(InputStream in) throws IOException {
        char[] buff = new char[1024];
        int len = 0;
        InputStreamReader reader = new InputStreamReader(in, "UTF-8");
        CharArrayWriter out = new CharArrayWriter();
        while ((len = reader.read(buff)) > 0) {
            out.write(buff, 0, len);
        }
        return out.toCharArray();
    }
}
