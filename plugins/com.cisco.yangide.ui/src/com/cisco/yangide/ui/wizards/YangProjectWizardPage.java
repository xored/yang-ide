/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * @author Konstantin Zaitsev
 * @date Jun 27, 2014
 */
class YangProjectWizardPage extends WizardPage {

    private Text rootDirTxt;
    private Combo yangVersion;
    private Button exampleFileChk;
    private Table generatorsTable;
    private TableViewer generatorsViewer;
    private Button removeBtn;
    private Button editBtn;
    private Button addBtn;

    /**
     * @param pageName
     */
    protected YangProjectWizardPage() {
        super("yangProjectPage");
        setTitle("YANG Tools Configuration");
        setDescription("Specify YANG Code Generators Parameters");
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        container.setLayout(new GridLayout(1, false));

        Composite group1 = new Composite(container, SWT.NONE);
        group1.setLayout(new GridLayout(2, false));
        group1.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        new Label(group1, SWT.NONE).setText("YANG Tools Version:");
        yangVersion = new Combo(group1, SWT.BORDER | SWT.READ_ONLY);
        yangVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        new Label(group1, SWT.NONE).setText("YANG Files Root Directory:");
        rootDirTxt = new Text(group1, SWT.BORDER);
        rootDirTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        rootDirTxt.setText(YangProjectWizard.SRC_MAIN_YANG);

        Composite group2 = new Composite(container, SWT.NONE);
        group2.setLayout(new GridLayout(2, false));
        group2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

        privateCreateGeneratorControls(group2);

        exampleFileChk = new Button(group2, SWT.CHECK);
        exampleFileChk.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        exampleFileChk.setText("Create Example YANG File");
        exampleFileChk.setSelection(true);

        setControl(container);

        // add default values
        // TODO KOS: load version from repository
        yangVersion.add("0.6.2-SNAPSHOT");
        yangVersion.add("0.6.1");
        yangVersion.add("0.6.0");
        yangVersion.add("0.5.8");
        yangVersion.select(0);

        // default generator
        CodeGeneratorConfig config = new CodeGeneratorConfig();
        config.setGroupId("org.opendaylight.yangtools");
        config.setArtifactId("maven-sal-api-gen-plugin");
        config.setVersion(yangVersion.getText());
        config.setGenClassName("org.opendaylight.yangtools.maven.sal.api.gen.plugin.CodeGeneratorImpl");
        config.setGenOutputDirectory("target/generated-sources/sal");
        generatorsViewer.add(config);
    }

    /**
     * @param parent
     */
    private void privateCreateGeneratorControls(Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1));
        label.setText("Source Code Generators:");

        generatorsViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
        generatorsTable = generatorsViewer.getTable();
        generatorsTable.setLinesVisible(true);
        generatorsTable.setHeaderVisible(true);
        generatorsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        generatorsTable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                validate();
            }
        });
        createTableColum(generatorsTable, "Group ID", 120);
        createTableColum(generatorsTable, "Artifact ID", 120);
        createTableColum(generatorsTable, "Version", 100);
        createTableColum(generatorsTable, "Generator Class Name", 200);
        createTableColum(generatorsTable, "Output Directory", 200);

        generatorsViewer.setLabelProvider(new ITableLabelProvider() {

            @Override
            public void removeListener(ILabelProviderListener listener) {
            }

            @Override
            public boolean isLabelProperty(Object element, String property) {
                return false;
            }

            @Override
            public void dispose() {
            }

            @Override
            public void addListener(ILabelProviderListener listener) {
            }

            @Override
            public String getColumnText(Object element, int columnIndex) {
                if (element instanceof CodeGeneratorConfig) {
                    CodeGeneratorConfig conf = (CodeGeneratorConfig) element;
                    String txt = null;
                    switch (columnIndex) {
                    case 0:
                        txt = conf.getGroupId();
                        break;
                    case 1:
                        txt = conf.getArtifactId();
                        break;
                    case 2:
                        txt = conf.getVersion();
                        break;
                    case 3:
                        txt = conf.getGenClassName();
                        break;
                    case 4:
                        txt = conf.getGenOutputDirectory();
                        break;
                    }
                    return txt != null ? txt : "";
                }
                return "";
            }

            @Override
            public Image getColumnImage(Object element, int columnIndex) {
                return null;
            }
        });

        Composite group = new Composite(parent, SWT.NONE);
        group.setLayout(new GridLayout(1, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

        addBtn = new Button(group, SWT.NONE);
        addBtn.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
        addBtn.setText("Add...");
        addBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                YangCodeGeneratorDialog dialog = new YangCodeGeneratorDialog(getShell());
                if (dialog.open() == Window.OK) {
                    generatorsViewer.add(dialog.getConfig());
                }
            }
        });

        editBtn = new Button(group, SWT.NONE);
        editBtn.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
        editBtn.setText("Edit...");
        editBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                CodeGeneratorConfig config = (CodeGeneratorConfig) generatorsViewer.getElementAt(generatorsTable
                        .getSelectionIndex());
                YangCodeGeneratorDialog dialog = new YangCodeGeneratorDialog(getShell(), config);
                if (dialog.open() == Window.OK) {
                    generatorsViewer.update(dialog.getConfig(), null);
                }
            }
        });

        removeBtn = new Button(group, SWT.NONE);
        removeBtn.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        removeBtn.setText("Remove");
        removeBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if (generatorsTable.getSelectionCount() > 1) {
                    generatorsTable.remove(generatorsTable.getSelectionIndices());
                    validate();
                }
            }
        });
        validate();
    }

    private TableColumn createTableColum(Table table, String name, int width) {
        TableColumn tableColumn = new TableColumn(table, SWT.NONE);
        tableColumn.setWidth(width);
        tableColumn.setText(name);
        return tableColumn;
    }

    private void validate() {
        editBtn.setEnabled(generatorsTable.getSelectionCount() == 1);
        removeBtn.setEnabled(generatorsTable.getItemCount() > 1);
    }

    public boolean createExampleFile() {
        return exampleFileChk.getSelection();
    }

    public String getRootDir() {
        return rootDirTxt.getText();
    }

    public String getYangVersion() {
        return yangVersion.getText();
    }

    public List<CodeGeneratorConfig> getCodeGenerators() {
        List<CodeGeneratorConfig> list = new ArrayList<>();
        for (TableItem item : generatorsTable.getItems()) {
            list.add((CodeGeneratorConfig) item.getData());
        }
        return list;
    }
}
