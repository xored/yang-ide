/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.formatter;

//import org.eclipse.jdt.internal.formatter.Location;
//import org.eclipse.jdt.internal.formatter.Scribe;

/**
 * Alignment management
 * 
 * @author Alexey Kholupko
 * @since 2.1
 */
public class Alignment {

    // Kind of alignment
    public int kind;
    public static final int ALLOCATION = 1;
    public static final int ANNOTATION_MEMBERS_VALUE_PAIRS = 2;
    public static final int ARRAY_INITIALIZER = 3;
    public static final int ASSIGNMENT = 4;
    public static final int BINARY_EXPRESSION = 5;
    public static final int CASCADING_MESSAGE_SEND = 6;
    public static final int COMPACT_IF = 7;
    public static final int COMPOUND_ASSIGNMENT = 8;
    public static final int CONDITIONAL_EXPRESSION = 9;
    public static final int ENUM_CONSTANTS = 10;
    public static final int ENUM_CONSTANTS_ARGUMENTS = 11;
    public static final int EXPLICIT_CONSTRUCTOR_CALL = 12;
    public static final int FIELD_DECLARATION_ASSIGNMENT = 13;
    public static final int LOCAL_DECLARATION_ASSIGNMENT = 14;
    public static final int MESSAGE_ARGUMENTS = 15;
    public static final int MESSAGE_SEND = 16;
    public static final int METHOD_ARGUMENTS = 17;
    public static final int METHOD_DECLARATION = 18;
    public static final int MULTIPLE_FIELD = 19;
    public static final int SUPER_CLASS = 20;
    public static final int SUPER_INTERFACES = 21;
    public static final int THROWS = 22;
    public static final int TYPE_MEMBERS = 23;
    public static final int STRING_CONCATENATION = 24;
    public static final int TRY_RESOURCES = 25;
    public static final int MULTI_CATCH = 26;

    // name of alignment
    public String name;
    public static final String[] NAMES = { "", //$NON-NLS-1$
            "allocation", //$NON-NLS-1$
            "annotationMemberValuePairs", //$NON-NLS-1$
            "array_initializer", //$NON-NLS-1$
            "assignmentAlignment", //$NON-NLS-1$
            "binaryExpressionAlignment", //$NON-NLS-1$
            "cascadingMessageSendAlignment", //$NON-NLS-1$
            "compactIf", //$NON-NLS-1$
            "compoundAssignmentAlignment", //$NON-NLS-1$
            "conditionalExpression", //$NON-NLS-1$
            "enumConstants", //$NON-NLS-1$
            "enumConstantArguments", //$NON-NLS-1$
            "explicit_constructor_call", //$NON-NLS-1$
            "fieldDeclarationAssignmentAlignment", //$NON-NLS-1$
            "localDeclarationAssignmentAlignment", //$NON-NLS-1$
            "messageArguments", //$NON-NLS-1$
            "messageAlignment", //$NON-NLS-1$
            "methodArguments", //$NON-NLS-1$
            "methodDeclaration", //$NON-NLS-1$
            "multiple_field", //$NON-NLS-1$
            "superclass", //$NON-NLS-1$
            "superInterfaces", //$NON-NLS-1$
            "throws", //$NON-NLS-1$
            "typeMembers", //$NON-NLS-1$
            "stringConcatenation", //$NON-NLS-1$
            "tryResources", //$NON-NLS-1$
            "unionTypeInMulticatch", //$NON-NLS-1$
    };

    // link to enclosing alignment
    public Alignment enclosing;

    // start location of this alignment
    // public Location location;

    // indentation management
    public int fragmentIndex;
    public int fragmentCount;
    public int[] fragmentIndentations;
    public boolean needRedoColumnAlignment;

    // chunk management
    public int chunkStartIndex;
    public int chunkKind;

    // break management
    public int originalIndentationLevel;
    public int breakIndentationLevel;
    public int shiftBreakIndentationLevel;
    public int[] fragmentBreaks;
    public boolean wasSplit;
    public boolean blockAlign = false;
    public boolean tooLong = false;

    // public Scribe scribe;

    // reset
    private boolean reset = false;

    /*
     * Alignment modes
     */
    public static final int M_FORCE = 1; // if bit set, then alignment will be non-optional (default
                                         // is optional)
    public static final int M_INDENT_ON_COLUMN = 2; // if bit set, broken fragments will be aligned
                                                    // on current location column (default is to
                                                    // break at current indentation level)
    public static final int M_INDENT_BY_ONE = 4; // if bit set, broken fragments will be indented
                                                 // one level below current (not using continuation
                                                 // indentation)

    // split modes can be combined either with M_FORCE or M_INDENT_ON_COLUMN

    /**
     * foobar(#fragment1, #fragment2,
     * <ul>
     * <li>#fragment3, #fragment4</li>
     * </ul>
     */
    public static final int M_COMPACT_SPLIT = 16; // fill each line with all possible fragments

    /**
     * foobar(
     * <ul>
     * <li>#fragment1, #fragment2,</li>
     * <li>#fragment5, #fragment4,</li>
     * </ul>
     */
    public static final int M_COMPACT_FIRST_BREAK_SPLIT = 32; // compact mode, but will first try to
                                                              // break before first fragment

    /**
     * foobar(
     * <ul>
     * <li>#fragment1,</li>
     * <li>#fragment2,</li>
     * <li>#fragment3</li>
     * <li>#fragment4,</li>
     * </ul>
     */
    public static final int M_ONE_PER_LINE_SPLIT = 32 + 16; // one fragment per line

    /**
     * foobar(
     * <ul>
     * <li>#fragment1,</li>
     * <li>#fragment2,</li>
     * <li>#fragment3</li>
     * <li>#fragment4,</li>
     * </ul>
     */
    public static final int M_NEXT_SHIFTED_SPLIT = 64; // one fragment per line, subsequent are
                                                       // indented further

    /**
     * foobar(#fragment1,
     * <ul>
     * <li>#fragment2,</li>
     * <li>#fragment3</li>
     * <li>#fragment4,</li>
     * </ul>
     */
    public static final int M_NEXT_PER_LINE_SPLIT = 64 + 16; // one per line, except first fragment
                                                             // (if possible)

    // 64+32
    // 64+32+16

    // mode controlling column alignments
    /**
     * <table BORDER COLS=4 WIDTH="100%" >
     * <tr>
     * <td>#fragment1A</td>
     * <td>#fragment2A</td>
     * <td>#fragment3A</td>
     * <td>#very-long-fragment4A</td>
     * </tr>
     * <tr>
     * <td>#fragment1B</td>
     * <td>#long-fragment2B</td>
     * <td>#fragment3B</td>
     * <td>#fragment4B</td>
     * </tr>
     * <tr>
     * <td>#very-long-fragment1C</td>
     * <td>#fragment2C</td>
     * <td>#fragment3C</td>
     * <td>#fragment4C</td>
     * </tr>
     * </table>
     */
    public static final int M_MULTICOLUMN = 256; // fragments are on same line, but multiple line of
                                                 // fragments will be aligned vertically

    public static final int M_NO_ALIGNMENT = 0;

    public int mode;

    public static final int SPLIT_MASK = M_ONE_PER_LINE_SPLIT | M_NEXT_SHIFTED_SPLIT | M_COMPACT_SPLIT
            | M_COMPACT_FIRST_BREAK_SPLIT | M_NEXT_PER_LINE_SPLIT;

    // alignment tie-break rules - when split is needed, will decide whether innermost/outermost
    // alignment is to be chosen
    public static final int R_OUTERMOST = 1;
    public static final int R_INNERMOST = 2;
    public int tieBreakRule;
    public int startingColumn = -1;

    // alignment effects on a per fragment basis
    public static final int NONE = 0;
    public static final int BREAK = 1;

    // chunk kind
    public static final int CHUNK_FIELD = 1;
    public static final int CHUNK_METHOD = 2;
    public static final int CHUNK_TYPE = 3;
    public static final int CHUNK_ENUM = 4;
}
