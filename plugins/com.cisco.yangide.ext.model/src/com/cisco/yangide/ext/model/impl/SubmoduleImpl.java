/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.model.impl;

import com.cisco.yangide.ext.model.BelongsTo;
import com.cisco.yangide.ext.model.ModelPackage;
import com.cisco.yangide.ext.model.Submodule;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Submodule</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.cisco.yangide.ext.model.impl.SubmoduleImpl#getBelongsTo <em>Belongs To</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SubmoduleImpl extends ModuleImpl implements Submodule {
    /**
     * The cached value of the '{@link #getBelongsTo() <em>Belongs To</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getBelongsTo()
     * @generated
     * @ordered
     */
    protected BelongsTo belongsTo;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected SubmoduleImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ModelPackage.Literals.SUBMODULE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public BelongsTo getBelongsTo() {
        if (belongsTo != null && belongsTo.eIsProxy()) {
            InternalEObject oldBelongsTo = (InternalEObject)belongsTo;
            belongsTo = (BelongsTo)eResolveProxy(oldBelongsTo);
            if (belongsTo != oldBelongsTo) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, ModelPackage.SUBMODULE__BELONGS_TO, oldBelongsTo, belongsTo));
            }
        }
        return belongsTo;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public BelongsTo basicGetBelongsTo() {
        return belongsTo;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setBelongsTo(BelongsTo newBelongsTo) {
        BelongsTo oldBelongsTo = belongsTo;
        belongsTo = newBelongsTo;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.SUBMODULE__BELONGS_TO, oldBelongsTo, belongsTo));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case ModelPackage.SUBMODULE__BELONGS_TO:
                if (resolve) return getBelongsTo();
                return basicGetBelongsTo();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case ModelPackage.SUBMODULE__BELONGS_TO:
                setBelongsTo((BelongsTo)newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case ModelPackage.SUBMODULE__BELONGS_TO:
                setBelongsTo((BelongsTo)null);
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case ModelPackage.SUBMODULE__BELONGS_TO:
                return belongsTo != null;
        }
        return super.eIsSet(featureID);
    }

} //SubmoduleImpl
