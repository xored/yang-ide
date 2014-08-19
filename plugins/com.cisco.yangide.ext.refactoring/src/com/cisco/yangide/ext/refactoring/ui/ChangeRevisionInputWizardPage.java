/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.refactoring.ui;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.QName;
import com.cisco.yangide.core.indexing.ElementIndexReferenceInfo;
import com.cisco.yangide.core.indexing.ElementIndexReferenceType;
import com.cisco.yangide.core.model.YangModelManager;
import com.cisco.yangide.ext.refactoring.code.ChangeRevisionRefactoring;
import com.cisco.yangide.ext.refactoring.nls.Messages;
import com.cisco.yangide.ui.internal.IYangUIConstants;
import com.cisco.yangide.ui.internal.YangUIImages;

/**
 * @author Konstantin Zaitsev
 * @date Aug 18, 2014
 */
public class ChangeRevisionInputWizardPage extends UserInputWizardPage {
    private Text revisionTxt;
    private Text descriptionTxt;
    private Button newFileCheck;
    private TableViewer table;
    private IFile[] files;

    public ChangeRevisionInputWizardPage() {
        super("RevisionInputPage"); //$NON-NLS-1$
        setDescription(Messages.ChangeRevisionInputWizardPage_description);
    }

    @Override
    public void createControl(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        GridLayoutFactory.swtDefaults().numColumns(3).spacing(0, 5).applyTo(content);

        new Label(content, SWT.NONE).setText(Messages.ChangeRevisionInputWizardPage_revisionLabel);
        revisionTxt = new Text(content, SWT.BORDER);
        revisionTxt.setEditable(false);
        Button revisionBtn = new Button(content, SWT.FLAT | SWT.PUSH);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.TOP).hint(24, 24).applyTo(revisionBtn);
        revisionBtn.setImage(RefactoringImages.getImage(RefactoringImages.IMG_CALENDAR));
        revisionBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                RevisionDialog dialog = new RevisionDialog(getShell());
                try {
                    dialog.setRevision(revisionTxt.getText());
                } catch (ParseException ex) {
                    setErrorMessage(ex.getMessage());
                    setPageComplete(false);
                }
                if (dialog.open() == Window.OK) {
                    revisionTxt.setText(dialog.getRevision());
                    setState();
                }
            }
        });

        new Label(content, SWT.NONE).setText(Messages.ChangeRevisionInputWizardPage_descriptionLabel);
        descriptionTxt = new Text(content, SWT.BORDER | SWT.MULTI);
        GridDataFactory.fillDefaults().span(4, 1).align(SWT.FILL, SWT.TOP).grab(true, false).hint(SWT.DEFAULT, 50)
        .applyTo(descriptionTxt);

        descriptionTxt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                setState();
            }
        });

        newFileCheck = new Button(content, SWT.CHECK);
        GridDataFactory.fillDefaults().span(3, 1).align(SWT.FILL, SWT.TOP).applyTo(newFileCheck);
        newFileCheck.setText(Messages.ChangeRevisionInputWizardPage_newFileCheckLabel);
        newFileCheck.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setState();
            }
        });

        Label tableLabel = new Label(content, SWT.NONE);
        tableLabel.setText(Messages.ChangeRevisionInputWizardPage_refGroupLabel);
        GridDataFactory.fillDefaults().span(3, 1).align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(tableLabel);

        Composite group = new Composite(content, SWT.NONE);
        GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(2).applyTo(group);
        GridDataFactory.fillDefaults().span(3, 1).grab(true, true).align(SWT.FILL, SWT.TOP).applyTo(group);

        table = new TableViewer(group, SWT.CHECK | SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, true).hint(SWT.DEFAULT, 200)
        .applyTo(table.getControl());
        table.setContentProvider(ArrayContentProvider.getInstance());
        table.getTable().setHeaderVisible(true);
        table.getTable().setLinesVisible(true);

        TableViewerColumn viewerColumn = new TableViewerColumn(table, SWT.NONE);
        TableColumn column = viewerColumn.getColumn();
        column.setText(Messages.ChangeRevisionInputWizardPage_refTableName);
        column.setWidth(250);
        column.setResizable(true);
        viewerColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public Image getImage(Object element) {
                return YangUIImages.getImage(IYangUIConstants.IMG_YANG_FILE);
            }

            @Override
            public String getText(Object element) {
                IFile file = (IFile) element;
                return file.getName();
            }
        });

        viewerColumn = new TableViewerColumn(table, SWT.NONE);
        column = viewerColumn.getColumn();
        column.setText(Messages.ChangeRevisionInputWizardPage_refTablePath);
        column.setWidth(300);
        column.setResizable(true);
        viewerColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                IFile file = (IFile) element;
                return file.getProjectRelativePath().toString();
            }
        });

        viewerColumn = new TableViewerColumn(table, SWT.NONE);
        column = viewerColumn.getColumn();
        column.setText(Messages.ChangeRevisionInputWizardPage_refTableProject);
        column.setWidth(200);
        column.setResizable(true);
        viewerColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                IFile file = (IFile) element;
                return file.getProject().getName();
            }
        });

        Composite controls = new Composite(group, SWT.NONE);
        GridLayoutFactory.swtDefaults().margins(0, 0).applyTo(controls);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.TOP).applyTo(controls);

        Button tableSelectAllBtn = new Button(controls, SWT.PUSH);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(tableSelectAllBtn);
        tableSelectAllBtn.setText(Messages.ChangeRevisionInputWizardPage_refTableSellectAllBtn);
        tableSelectAllBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                for (int i = 0; i < table.getTable().getItemCount(); i++) {
                    table.getTable().getItem(i).setChecked(true);
                }
                setState();
            }
        });

        Button tableDeselectAllBtn = new Button(controls, SWT.PUSH);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(tableDeselectAllBtn);
        tableDeselectAllBtn.setText(Messages.ChangeRevisionInputWizardPage_refTableDeselectAllBtn);
        tableDeselectAllBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                for (int i = 0; i < table.getTable().getItemCount(); i++) {
                    table.getTable().getItem(i).setChecked(false);
                }
                setState();
            }
        });
        table.getTable().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setState();
            }
        });

        Dialog.applyDialogFont(content);
        setControl(content);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            loadState();
        }
        super.setVisible(visible);
    }

    private void setState() {
        ChangeRevisionRefactoring refactoring = (ChangeRevisionRefactoring) getRefactoring();
        refactoring.setCreateNewFile(newFileCheck.getSelection());
        refactoring.setRevision(revisionTxt.getText());
        refactoring.setDescription(descriptionTxt.getText());

        List<IPath> refs = new ArrayList<>();
        for (int i = 0; i < table.getTable().getItemCount(); i++) {
            if (table.getTable().getItem(i).getChecked()) {
                refs.add(files[i].getFullPath());
            }
        }
        refactoring.setReferences(refs);
    }

    private void loadState() {
        ChangeRevisionRefactoring refactoring = (ChangeRevisionRefactoring) getRefactoring();
        Module module = refactoring.getModule();

        if (refactoring.getRevision() == null) {
            refactoring.setRevision(RevisionDialog.DF.format(new Date()));
        }

        revisionTxt.setText(refactoring.getRevision());
        descriptionTxt.setText(refactoring.getDescription() != null ? refactoring.getDescription() : ""); //$NON-NLS-1$
        newFileCheck.setSelection(refactoring.isCreateNewFile());

        QName name = new QName(module.getName(), null, module.getName(), module.getRevision());
        ElementIndexReferenceInfo[] reference = YangModelManager.getIndexManager().searchReference(name,
                ElementIndexReferenceType.IMPORT, null);
        files = new IFile[reference.length];

        for (int i = 0; i < reference.length; i++) {
            files[i] = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(reference[i].getPath()));
        }
        table.setInput(files);

        for (int i = 0; i < files.length; i++) {
            table.getTable().getItem(i).setChecked(refactoring.getReferences().contains(files[i].getFullPath()));
        }

    }
}
