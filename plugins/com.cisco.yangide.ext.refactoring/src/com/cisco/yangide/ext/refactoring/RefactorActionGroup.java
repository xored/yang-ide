/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.refactoring;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionGroup;

import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.editor.editors.IActionGroup;
import com.cisco.yangide.editor.editors.YangEditor;
import com.cisco.yangide.ext.refactoring.actions.ChangeRevisionAction;
import com.cisco.yangide.ext.refactoring.actions.ExtractGroupingAction;
import com.cisco.yangide.ext.refactoring.actions.InlineGroupingAction;
import com.cisco.yangide.ext.refactoring.actions.RenameAction;
import com.cisco.yangide.ext.refactoring.actions.SelectionDispatchAction;
import com.cisco.yangide.ext.refactoring.nls.Messages;

/**
 * @author Konstantin Zaitsev
 * @date Jul 29, 2014
 */
public class RefactorActionGroup extends ActionGroup implements IActionGroup {

    private static final String RENAME_ACTION_ID = "com.cisco.yangide.ui.actions.Rename"; //$NON-NLS-1$
    private static final String RENAME_DEF_ID = "com.cisco.yangide.ext.rename.element"; //$NON-NLS-1$

    private static final String INLINE_GROUP_ACTION_ID = "com.cisco.yangide.ui.actions.Inline"; //$NON-NLS-1$
    private static final String INLINE_GROUP_DEF_ID = "com.cisco.yangide.ext.group.inline.element"; //$NON-NLS-1$

    private static final String REVISION_ACTION_ID = "com.cisco.yangide.ui.actions.Revision"; //$NON-NLS-1$
    private static final String REVISION_DEF_ID = "com.cisco.yangide.ext.change.revision"; //$NON-NLS-1$

    private static final String EXTRACT_ACTION_ID = "com.cisco.yangide.ui.actions.ExtractGrouping"; //$NON-NLS-1$
    private static final String EXTRACT_DEF_ID = "com.cisco.yangide.ext.extract.grouping"; //$NON-NLS-1$

    private static final String REFACTORING_MENU_ID = "com.cisco.yangide.ext.refactoring.menu"; //$NON-NLS-1$

    /** Context menu group id. */
    private static final String REORG_GROUP_ID = "reorgGroup"; //$NON-NLS-1$

    private YangEditor editor;
    private String groupName;
    private ISelectionProvider selectionProvider;
    private SelectionDispatchAction renameAction;
    private SelectionDispatchAction inlineAction;
    private SelectionDispatchAction revisionAction;
    private SelectionDispatchAction extractAction;

    private final List<SelectionDispatchAction> actions = new ArrayList<SelectionDispatchAction>();

    private static class NoActionAvailable extends Action {
        public NoActionAvailable() {
            setEnabled(true);
            setText(Messages.RefactorActionGroup_noRefactorAvailable);
        }
    }

    private Action noActionAvailable = new NoActionAvailable();
    private StyledText control;

    @Override
    public void init(YangEditor editor, String groupName) {
        this.selectionProvider = editor.getSelectionProvider();
        this.editor = editor;
        this.groupName = groupName;
        this.control = (StyledText) editor.getAdapter(Control.class);

        ISelection selection = selectionProvider.getSelection();

        renameAction = new RenameAction(editor);
        initAction(renameAction, selection, RENAME_DEF_ID);
        editor.setAction("RenameElement", renameAction); //$NON-NLS-1$

        inlineAction = new InlineGroupingAction(editor);
        initAction(inlineAction, selection, INLINE_GROUP_DEF_ID);
        editor.setAction("InlineElement", inlineAction); //$NON-NLS-1$

        revisionAction = new ChangeRevisionAction(editor);
        initAction(revisionAction, selection, REVISION_DEF_ID);
        editor.setAction("ChangeRevision", revisionAction); //$NON-NLS-1$

        extractAction = new ExtractGroupingAction(editor);
        initAction(extractAction, selection, EXTRACT_DEF_ID);
        editor.setAction("ExtractGrouping", extractAction); //$NON-NLS-1$
    }

    @Override
    public void fillActionBars(IActionBars actionBars) {
        actionBars.setGlobalActionHandler(RENAME_ACTION_ID, renameAction);
        actionBars.setGlobalActionHandler(INLINE_GROUP_ACTION_ID, inlineAction);
        actionBars.setGlobalActionHandler(REVISION_ACTION_ID, revisionAction);
        actionBars.setGlobalActionHandler(EXTRACT_ACTION_ID, extractAction);
    }

    /**
     * Retargets the File actions with the corresponding refactoring actions.
     *
     * @param actionBars the action bar to register the move and rename action with
     */
    public void retargetFileMenuActions(IActionBars actionBars) {
        actionBars.setGlobalActionHandler(ActionFactory.RENAME.getId(), renameAction);
    }

    @Override
    public void fillContextMenu(IMenuManager menu) {
        addRefactorSubmenu(menu);
    }

    @Override
    public void dispose() {
        disposeAction(renameAction);
        super.dispose();
    }

    private void initAction(SelectionDispatchAction action, ISelection selection, String actionDefinitionId) {
        initUpdatingAction(action, selection, actionDefinitionId);
    }

    private void initUpdatingAction(SelectionDispatchAction action, ISelection selection, String actionDefinitionId) {
        action.setActionDefinitionId(actionDefinitionId);
        action.update(selection);
        if (selectionProvider != null) {
            selectionProvider.addSelectionChangedListener(action);
        }
        if (control != null) {
            control.addCaretListener(action);
        }
        actions.add(action);
    }

    private void disposeAction(SelectionDispatchAction action) {
        if (action != null) {
            selectionProvider.removeSelectionChangedListener(action);
            if (!control.isDisposed()) {
                control.removeCaretListener(action);
            }
        }
    }

    private void addRefactorSubmenu(IMenuManager menu) {
        MenuManager refactorSubmenu = new MenuManager(Messages.RefactorActionGroup_Refactor, REFACTORING_MENU_ID);
        if (editor != null) {
            if (editor.isEditable() && getModule() != null) {
                refactorSubmenu.addMenuListener(new IMenuListener() {
                    @Override
                    public void menuAboutToShow(IMenuManager manager) {
                        refactorMenuShown(manager);
                    }
                });
            }
            menu.appendToGroup(groupName, refactorSubmenu);
            refactorSubmenu.add(noActionAvailable);
        } else {
            ISelection selection = selectionProvider.getSelection();
            for (Iterator<SelectionDispatchAction> iter = actions.iterator(); iter.hasNext();) {
                iter.next().update(selection);
            }
            if (fillRefactorMenu(refactorSubmenu) > 0) {
                menu.appendToGroup(groupName, refactorSubmenu);
            }
        }
    }

    private int fillRefactorMenu(IMenuManager refactorSubmenu) {
        int added = 0;
        refactorSubmenu.add(new Separator(REORG_GROUP_ID));
        added += addAction(refactorSubmenu, renameAction);
        added += addAction(refactorSubmenu, inlineAction);
        added += addAction(refactorSubmenu, revisionAction);
        added += addAction(refactorSubmenu, extractAction);
        return added;
    }

    private int addAction(IMenuManager menu, IAction action) {
        if (action != null && action.isEnabled()) {
            menu.add(action);
            return 1;
        }
        return 0;
    }

    private void refactorMenuShown(IMenuManager refactorSubmenu) {
        Menu menu = ((MenuManager) refactorSubmenu).getMenu();
        menu.addMenuListener(new MenuAdapter() {
            @Override
            public void menuHidden(MenuEvent e) {
                refactorMenuHidden();
            }
        });
        ITextSelection textSelection = (ITextSelection) editor.getSelectionProvider().getSelection();
        for (Iterator<SelectionDispatchAction> iter = actions.iterator(); iter.hasNext();) {
            SelectionDispatchAction action = iter.next();
            action.update(textSelection);
        }
        refactorSubmenu.removeAll();
        if (fillRefactorMenu(refactorSubmenu) == 0) {
            refactorSubmenu.add(noActionAvailable);
        }
    }

    private void refactorMenuHidden() {
        ITextSelection textSelection = (ITextSelection) editor.getSelectionProvider().getSelection();
        for (Iterator<SelectionDispatchAction> iter = actions.iterator(); iter.hasNext();) {
            SelectionDispatchAction action = iter.next();
            action.update(textSelection);
        }
    }

    private Module getModule() {
        try {
            return editor.getModule();
        } catch (YangModelException e) {
            // ignore exception
            return null;
        }
    }
}
