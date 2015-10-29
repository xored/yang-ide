/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.core.indexing;

import java.util.ArrayList;

import com.cisco.yangide.core.IOpenable;
import com.cisco.yangide.core.IYangElementDelta;
import com.cisco.yangide.core.model.YangElement;

/**
 * @author Konstantin Zaitsev
 * @date Jun 30, 2014
 */
public class YangElementDelta implements IYangElementDelta {
    static IYangElementDelta[] EMPTY_DELTA = new IYangElementDelta[0];

    private YangElement changedElement;
    private int kind = 0;
    private int flags = 0;
    private IYangElementDelta[] affectedChildren = EMPTY_DELTA;

    private int changeFlags;

    public YangElementDelta(YangElement element) {
        this.changedElement = element;
    }

    @Override
    public YangElement getElement() {
        return changedElement;
    }

    @Override
    public int getKind() {
        return kind;
    }

    @Override
    public int getFlags() {
        return flags;
    }

    @Override
    public IYangElementDelta[] getAffectedChildren() {
        return affectedChildren;
    }

    public void added(YangElement element) {
        YangElementDelta addedDelta = new YangElementDelta(element);
        addedDelta.kind = IYangElementDelta.ADDED;
        insertDeltaTree(element, addedDelta);
    }

    public YangElementDelta changed(YangElement element, int changeFlag) {
        YangElementDelta changedDelta = new YangElementDelta(element);
        changedDelta.kind = IYangElementDelta.CHANGED;
        changedDelta.changeFlags |= flags;
        insertDeltaTree(element, changedDelta);
        return changedDelta;
    }

    public void removed(YangElement element) {
        YangElementDelta removedDelta = new YangElementDelta(element);
        insertDeltaTree(element, removedDelta);
        YangElementDelta actualDelta = getDeltaFor(element);
        if (actualDelta != null) {
            actualDelta.kind = IYangElementDelta.REMOVED;
            actualDelta.affectedChildren = EMPTY_DELTA;
        }
    }

    protected void insertDeltaTree(YangElement element, YangElementDelta delta) {
        YangElementDelta childDelta = createDeltaTree(element, delta);
        if (!equalsAndSameParent(element, getElement())) { // handle case of two jars that can be
                                                           // equals but not in the same project
            addAffectedChild(childDelta);
        }
    }

    /**
     * Creates the nested delta deltas based on the affected element its delta, and the root of this
     * delta tree. Returns the root of the created delta tree.
     */
    protected YangElementDelta createDeltaTree(YangElement element, YangElementDelta delta) {
        YangElementDelta childDelta = delta;
        ArrayList<IOpenable> ancestors = getAncestors(element);
        if (ancestors == null) {
            if (equalsAndSameParent(delta.getElement(), getElement())) {
                this.kind = delta.kind;
            }
        } else {
            for (int i = 0, size = ancestors.size(); i < size; i++) {
                YangElement ancestor = (YangElement) ancestors.get(i);
                YangElementDelta ancestorDelta = new YangElementDelta(ancestor);
                ancestorDelta.addAffectedChild(childDelta);
                childDelta = ancestorDelta;
            }
        }
        return childDelta;
    }

    /**
     * Adds the child delta to the collection of affected children. If the child is already in the
     * collection, walk down the hierarchy.
     */
    protected void addAffectedChild(YangElementDelta child) {
        switch (this.kind) {
        case ADDED:
        case REMOVED:
            // no need to add a child if this parent is added or removed
            return;
        case CHANGED:
            this.changeFlags |= F_CHILDREN;
            break;
        default:
            this.kind = CHANGED;
            this.changeFlags |= F_CHILDREN;
        }

        if (this.affectedChildren == null || this.affectedChildren.length == 0) {
            this.affectedChildren = new IYangElementDelta[] { child };
            return;
        }

        YangElementDelta existingChild = null;
        int existingChildIndex = -1;
        for (int i = 0; i < this.affectedChildren.length; i++) {
            if (equalsAndSameParent(this.affectedChildren[i].getElement(), child.getElement())) {
                existingChild = (YangElementDelta) this.affectedChildren[i];
                existingChildIndex = i;
                break;
            }
        }
        if (existingChild == null) { // new affected child
            this.affectedChildren = growAndAddToArray(this.affectedChildren, child);
        } else {
            switch (existingChild.getKind()) {
            case ADDED:
                switch (child.getKind()) {
                case ADDED: // child was added then added -> it is added
                case CHANGED: // child was added then changed -> it is added
                    return;
                case REMOVED: // child was added then removed -> noop
                    this.affectedChildren = removeAndShrinkArray(this.affectedChildren, existingChildIndex);
                    return;
                }
                break;
            case REMOVED:
                switch (child.getKind()) {
                case ADDED: // child was removed then added -> it is changed
                    child.kind = CHANGED;
                    this.affectedChildren[existingChildIndex] = child;
                    return;
                case CHANGED: // child was removed then changed -> it is removed
                case REMOVED: // child was removed then removed -> it is removed
                    return;
                }
                break;
            case CHANGED:
                switch (child.getKind()) {
                case ADDED: // child was changed then added -> it is added
                case REMOVED: // child was changed then removed -> it is removed
                    this.affectedChildren[existingChildIndex] = child;
                    return;
                case CHANGED: // child was changed then changed -> it is changed
                    IYangElementDelta[] children = child.getAffectedChildren();
                    for (int i = 0; i < children.length; i++) {
                        YangElementDelta childsChild = (YangElementDelta) children[i];
                        existingChild.addAffectedChild(childsChild);
                    }

                    // update flags
                    boolean childHadContentFlag = (child.changeFlags & F_CONTENT) != 0;
                    boolean existingChildHadChildrenFlag = (existingChild.changeFlags & F_CHILDREN) != 0;
                    existingChild.changeFlags |= child.changeFlags;

                    // remove F_CONTENT flag if existing child had F_CHILDREN flag set
                    // (case of fine grained delta (existing child) and delta coming from
                    // DeltaProcessor (child))
                    if (childHadContentFlag && existingChildHadChildrenFlag) {
                        existingChild.changeFlags &= ~F_CONTENT;
                    }

                    return;
                }
                break;
            default:
                // unknown -> existing child becomes the child with the existing child's flags
                int flags = existingChild.getFlags();
                this.affectedChildren[existingChildIndex] = child;
                child.changeFlags |= flags;
            }
        }
    }

    /**
     * Returns whether the two yang elements are equals and have the same parent.
     */
    protected boolean equalsAndSameParent(YangElement e1, YangElement e2) {
        IOpenable parent1;
        return e1.equals(e2) && ((parent1 = e1.getParent()) != null) && parent1.equals(e2.getParent());
    }

    private ArrayList<IOpenable> getAncestors(YangElement element) {
        IOpenable parent = element.getParent();
        if (parent == null) {
            return null;
        }
        ArrayList<IOpenable> parents = new ArrayList<IOpenable>();
        while (!parent.equals(this.changedElement)) {
            parents.add(parent);
            parent = parent.getParent();
            if (parent == null) {
                return null;
            }
        }
        parents.trimToSize();
        return parents;
    }

    /**
     * Adds the new element to a new array that contains all of the elements of the old array.
     * Returns the new array.
     */
    protected IYangElementDelta[] growAndAddToArray(IYangElementDelta[] array, IYangElementDelta addition) {
        IYangElementDelta[] old = array;
        array = new IYangElementDelta[old.length + 1];
        System.arraycopy(old, 0, array, 0, old.length);
        array[old.length] = addition;
        return array;
    }

    /**
     * Removes the child delta from the collection of affected children.
     */
    protected void removeAffectedChild(YangElementDelta child) {
        int index = -1;
        if (this.affectedChildren != null) {
            for (int i = 0; i < this.affectedChildren.length; i++) {
                if (equalsAndSameParent(this.affectedChildren[i].getElement(), child.getElement())) {
                    index = i;
                    break;
                }
            }
        }
        if (index >= 0) {
            this.affectedChildren = removeAndShrinkArray(this.affectedChildren, index);
        }
    }

    /**
     * Returns the delta for a given element. Only looks below this delta.
     */
    protected YangElementDelta getDeltaFor(YangElement element) {
        if (equalsAndSameParent(getElement(), element)) {
            return this;
        }

        if (this.affectedChildren.length == 0) {
            return null;
        }
        int childrenCount = this.affectedChildren.length;
        for (int i = 0; i < childrenCount; i++) {
            YangElementDelta delta = (YangElementDelta) this.affectedChildren[i];
            if (equalsAndSameParent(delta.getElement(), element)) {
                return delta;
            } else {
                delta = delta.getDeltaFor(element);
                if (delta != null)
                    return delta;
            }
        }
        return null;
    }

    /**
     * Removes the element from the array. Returns the a new array which has shrunk.
     */
    protected IYangElementDelta[] removeAndShrinkArray(IYangElementDelta[] old, int index) {
        IYangElementDelta[] array = new IYangElementDelta[old.length - 1];
        if (index > 0)
            System.arraycopy(old, 0, array, 0, index);
        int rest = old.length - index - 1;
        if (rest > 0)
            System.arraycopy(old, index + 1, array, index, rest);
        return array;
    }
}
