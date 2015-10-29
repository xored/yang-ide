/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.core.dom;

/**
 * @author Konstantin Zaitsev
 * @date Jul 8, 2014
 */
public class TypeReference extends ASTNamedNode {
    private QName type;

    public TypeReference(ASTNode parent) {
        super(parent);
    }

    @Override
    public String getNodeName() {
        return "type";
    }

    /**
     * @param type the type to set
     */
    public void setType(QName type) {
        this.type = type;
    }

    /**
     * @return the type
     */
    public QName getType() {
        return type;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.preVisit(this);
        visitor.visit(this);
    }

    @Override
    public boolean isShowedInOutline() {
        return false;
    }  
    

}
