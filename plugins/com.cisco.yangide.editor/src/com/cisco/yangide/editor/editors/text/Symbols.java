/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors.text;

/**
 * Symbols for the heuristic YANG scanner.
 * 
 * @author Alexey Kholupko
 */
public interface Symbols {
    int TokenEOF = -1;
    int TokenLBRACE = 1;
    int TokenRBRACE = 2;
    int TokenLBRACKET = 3;
    int TokenRBRACKET = 4;
    int TokenLPAREN = 5;
    int TokenRPAREN = 6;
    int TokenSEMICOLON = 7;
    int TokenOTHER = 8;
    int TokenCOLON = 9;
    int TokenQUESTIONMARK = 10;
    int TokenCOMMA = 11;
    int TokenEQUAL = 12;
    int TokenLESSTHAN = 13;
    int TokenGREATERTHAN = 14;
    int TokenPLUS = 15;
    int TokenAT = 16;
    int TokenKEYWORD = 1000;
    int TokenIDENT = 2000;
}
