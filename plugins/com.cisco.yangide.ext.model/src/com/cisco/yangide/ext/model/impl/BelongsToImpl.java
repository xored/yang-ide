/**
 */
package com.cisco.yangide.ext.model.impl;

import com.cisco.yangide.ext.model.BelongsTo;
import com.cisco.yangide.ext.model.ModelPackage;
import com.cisco.yangide.ext.model.Module;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Belongs To</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.cisco.yangide.ext.model.impl.BelongsToImpl#getOwnerModule <em>Owner Module</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class BelongsToImpl extends MinimalEObjectImpl.Container implements BelongsTo {
    /**
     * The cached value of the '{@link #getOwnerModule() <em>Owner Module</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOwnerModule()
     * @generated
     * @ordered
     */
    protected Module ownerModule;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected BelongsToImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ModelPackage.Literals.BELONGS_TO;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Module getOwnerModule() {
        if (ownerModule != null && ownerModule.eIsProxy()) {
            InternalEObject oldOwnerModule = (InternalEObject)ownerModule;
            ownerModule = (Module)eResolveProxy(oldOwnerModule);
            if (ownerModule != oldOwnerModule) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, ModelPackage.BELONGS_TO__OWNER_MODULE, oldOwnerModule, ownerModule));
            }
        }
        return ownerModule;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Module basicGetOwnerModule() {
        return ownerModule;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setOwnerModule(Module newOwnerModule) {
        Module oldOwnerModule = ownerModule;
        ownerModule = newOwnerModule;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.BELONGS_TO__OWNER_MODULE, oldOwnerModule, ownerModule));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case ModelPackage.BELONGS_TO__OWNER_MODULE:
                if (resolve) return getOwnerModule();
                return basicGetOwnerModule();
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
            case ModelPackage.BELONGS_TO__OWNER_MODULE:
                setOwnerModule((Module)newValue);
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
            case ModelPackage.BELONGS_TO__OWNER_MODULE:
                setOwnerModule((Module)null);
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
            case ModelPackage.BELONGS_TO__OWNER_MODULE:
                return ownerModule != null;
        }
        return super.eIsSet(featureID);
    }

} //BelongsToImpl
