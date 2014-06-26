package com.cisco.yangide.core.tests;

import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.internal.YangASTParser;

public class YangParserTest extends TestCase {
    public void testSimpleParser() throws Exception {
        try (InputStream in = FileLocator.openStream(Platform.getBundle("com.cisco.yangide.core.tests"), new Path(
                "yang/simple_import.yang"), false)) {
            Module module = new YangASTParser().parseYangFile(in);
            assertEquals("my-crypto", module.getName());
            assertEquals(7, module.getNameStartPosition());
            assertEquals(0, module.getStartPosition());
            assertEquals(328, module.getLength());
        }
    }
}
