/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.parser;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Module_stmtContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.StringContext;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Submodule_stmtContext;

/**
 * Validation utilities.
 *
 * @author Konstantin Zaitsev
 * @date Jul 9, 2014
 */
public class ValidationUtil {

    /**
     * It isn't desirable to create instance of this class
     */
    private ValidationUtil() {
    }

    static void ex(ParseTree ctx, String message) {
        throw new YangValidationException(ctx, message);
    }

    static Set<String> getDuplicates(Collection<String> keyList) {
        Set<String> all = new HashSet<String>();
        Set<String> duplicates = new HashSet<String>();

        for (String key : keyList) {
            if (!all.add(key)) {
                duplicates.add(key);
            }
        }
        return duplicates;
    }

    static List<String> listKeysFromId(String keys) {
        return Arrays.asList(keys.split(" "));
    }

    static String getRootParentName(ParseTree ctx) {
        ParseTree root = getRootParent(ctx);
        return ValidationUtil.getName(root);
    }

    private static ParseTree getRootParent(ParseTree ctx) {
        ParseTree root = ctx;
        while (root.getParent() != null) {
            if (root.getClass().equals(Module_stmtContext.class) || root.getClass().equals(Submodule_stmtContext.class)) {
                break;
            }
            root = root.getParent();
        }
        return root;
    }

    static String getName(ParseTree child) {
        String result = "";
        for (int i = 0; i < child.getChildCount(); ++i) {
            if (child.getChild(i) instanceof StringContext) {
                final StringContext context = (StringContext) child.getChild(i);
                if (context != null) {
                    return stringFromStringContext(context);

                }
            }
        }
        return result;
    }

    static String f(String base, Object... args) {
        return String.format(base, args);
    }

    public static String stringFromStringContext(final StringContext context) {
        StringBuilder str = new StringBuilder();
        for (TerminalNode stringNode : context.STRING()) {
            String result = stringNode.getText();
            if (!result.contains("\"")) {
                str.append(result);
            } else {
                str.append(result.replace("\"", ""));
            }
        }
        return str.toString();
    }

    /**
     * Get simple name from statement class e.g. Module from Module_stmt_context
     */
    static String getSimpleStatementName(Class<? extends ParseTree> typeOfStatement) {

        String className = typeOfStatement.getSimpleName();
        int lastIndexOf = className.indexOf('$');
        className = lastIndexOf == -1 ? className : className.substring(lastIndexOf + 1);
        int indexOfStmt = className.indexOf("_stmt");
        int index = indexOfStmt == -1 ? className.indexOf("_arg") : indexOfStmt;
        return className.substring(0, index).replace('_', '-');
    }

    static int countPresentChildrenOfType(ParseTree parent, Set<Class<? extends ParseTree>> expectedChildTypes) {
        int foundChildrenOfType = 0;

        for (Class<? extends ParseTree> type : expectedChildTypes) {
            foundChildrenOfType += countPresentChildrenOfType(parent, type);
        }
        return foundChildrenOfType;
    }

    static int countPresentChildrenOfType(ParseTree parent, Class<? extends ParseTree> expectedChildType) {
        int foundChildrenOfType = 0;

        for (int i = 0; i < parent.getChildCount(); i++) {
            ParseTree child = parent.getChild(i);
            if (expectedChildType.isInstance(child)) {
                foundChildrenOfType++;
            }
        }
        return foundChildrenOfType;
    }
}