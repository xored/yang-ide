/**
 */
package com.cisco.yangide.ext.model.impl;

import com.cisco.yangide.ext.model.Identity;
import com.cisco.yangide.ext.model.ModelPackage;
import com.cisco.yangide.ext.model.Node;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Identity</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.cisco.yangide.ext.model.impl.IdentityImpl#getParent <em>Parent</em>}</li>
 *   <li>{@link com.cisco.yangide.ext.model.impl.IdentityImpl#getRefId <em>Ref Id</em>}</li>
 *   <li>{@link com.cisco.yangide.ext.model.impl.IdentityImpl#getName <em>Name</em>}</li>
 *   <li>{@link com.cisco.yangide.ext.model.impl.IdentityImpl#getBase <em>Base</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class IdentityImpl extends MinimalEObjectImpl.Container implements Identity {
    /**
     * The cached value of the '{@link #getParent() <em>Parent</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getParent()
     * @generated
     * @ordered
     */
    protected Node parent;

    /**
     * The default value of the '{@link #getRefId() <em>Ref Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRefId()
     * @generated
     * @ordered
     */
    protected static final String REF_ID_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getRefId() <em>Ref Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRefId()
     * @generated
     * @ordered
     */
    protected String refId = REF_ID_EDEFAULT;

    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected String name = NAME_EDEFAULT;

    /**
     * The cached value of the '{@link #getBase() <em>Base</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getBase()
     * @generated
     * @ordered
     */
    protected Identity base;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected IdentityImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ModelPackage.Literals.IDENTITY;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Node getParent() {
        if (parent != null && parent.eIsProxy()) {
            InternalEObject oldParent = (InternalEObject)parent;
            parent = (Node)eResolveProxy(oldParent);
            if (parent != oldParent) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, ModelPackage.IDENTITY__PARENT, oldParent, parent));
            }
        }
        return parent;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Node basicGetParent() {
        return parent;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setParent(Node newParent) {
        Node oldParent = parent;
        parent = newParent;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.IDENTITY__PARENT, oldParent, parent));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getRefId() {
        return refId;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setRefId(String newRefId) {
        String oldRefId = refId;
        refId = newRefId;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.IDENTITY__REF_ID, oldRefId, refId));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setName(String newName) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.IDENTITY__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Identity getBase() {
        if (base != null && base.eIsProxy()) {
            InternalEObject oldBase = (InternalEObject)base;
            base = (Identity)eResolveProxy(oldBase);
            if (base != oldBase) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, ModelPackage.IDENTITY__BASE, oldBase, base));
            }
        }
        return base;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Identity basicGetBase() {
        return base;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setBase(Identity newBase) {
        Identity oldBase = base;
        base = newBase;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.IDENTITY__BASE, oldBase, base));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case ModelPackage.IDENTITY__PARENT:
                if (resolve) return getParent();
                return basicGetParent();
            case ModelPackage.IDENTITY__REF_ID:
                return getRefId();
            case ModelPackage.IDENTITY__NAME:
                return getName();
            case ModelPackage.IDENTITY__BASE:
                if (resolve) return getBase();
                return basicGetBase();
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
            case ModelPackage.IDENTITY__PARENT:
                setParent((Node)newValue);
                return;
            case ModelPackage.IDENTITY__REF_ID:
                setRefId((String)newValue);
                return;
            case ModelPackage.IDENTITY__NAME:
                setName((String)newValue);
                return;
            case ModelPackage.IDENTITY__BASE:
                setBase((Identity)newValue);
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
            case ModelPackage.IDENTITY__PARENT:
                setParent((Node)null);
                return;
            case ModelPackage.IDENTITY__REF_ID:
                setRefId(REF_ID_EDEFAULT);
                return;
            case ModelPackage.IDENTITY__NAME:
                setName(NAME_EDEFAULT);
                return;
            case ModelPackage.IDENTITY__BASE:
                setBase((Identity)null);
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
            case ModelPackage.IDENTITY__PARENT:
                return parent != null;
            case ModelPackage.IDENTITY__REF_ID:
                return REF_ID_EDEFAULT == null ? refId != null : !REF_ID_EDEFAULT.equals(refId);
            case ModelPackage.IDENTITY__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case ModelPackage.IDENTITY__BASE:
                return base != null;
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (refId: ");
        result.append(refId);
        result.append(", name: ");
        result.append(name);
        result.append(')');
        return result.toString();
    }

} //IdentityImpl
