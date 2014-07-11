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
import com.cisco.yangide.core.internal.YangParserUtil;

public class YangParserTest extends TestCase {
    public void testSimpleParser() throws Exception {
        try (InputStream in = FileLocator.openStream(Platform.getBundle("com.cisco.yangide.core.tests"), new Path(
                "yang/simple_import.yang"), false)) {

            Module module = YangParserUtil.parseYangFile(getContent(in), null);
            assertEquals("my-crypto", module.getName());
            assertEquals(7, module.getNameStartPosition());
            assertEquals(0, module.getStartPosition());
            assertEquals(328, module.getLength());
        }
    }

    public void testNodeAtPostion() throws Exception {
        try (InputStream in = FileLocator.openStream(Platform.getBundle("com.cisco.yangide.core.tests"), new Path(
                "yang/simple_import.yang"), false)) {

            Module module = YangParserUtil.parseYangFile(getContent(in), null);
            assertEquals(module, module.getNodeAtPosition(1));
            assertEquals(module.getImports().get("crypto-base"), module.getNodeAtPosition(100));
        }
    }

    public void testIncompleteParse() throws Exception {
        try (InputStream in = FileLocator.openStream(Platform.getBundle("com.cisco.yangide.core.tests"), new Path(
                "yang/simple_import_incomplete.yang"), false)) {

            Module module = YangParserUtil.parseYangFile(getContent(in), null);
            assertNotNull(module);
            assertEquals(1, module.getImports().size());
        }
    }

    public void testSubmoduleParse() throws Exception {
        try (InputStream in = FileLocator.openStream(Platform.getBundle("com.cisco.yangide.core.tests"), new Path(
                "yang/acme-types.yang"), false)) {

            SubModule module = (SubModule) YangParserUtil.parseYangFile(getContent(in), null);
            assertNotNull(module);
            assertEquals("acme-system", module.getParentModule());
            assertEquals("acme", module.getParentPrefix());
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
