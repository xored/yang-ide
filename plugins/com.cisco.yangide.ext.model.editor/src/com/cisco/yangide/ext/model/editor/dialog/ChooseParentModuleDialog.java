/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.model.editor.dialog;

import org.eclipse.core.resources.IFile;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.cisco.yangide.core.indexing.ElementIndexInfo;
import com.cisco.yangide.core.indexing.ElementIndexType;
import com.cisco.yangide.core.model.YangModelManager;
import com.cisco.yangide.ext.model.BelongsTo;
import com.cisco.yangide.ext.model.ModelFactory;
import com.cisco.yangide.ext.model.Module;
import com.cisco.yangide.ext.model.Submodule;
import com.cisco.yangide.ext.model.editor.util.YangDiagramImageProvider;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;
import com.cisco.yangide.ext.model.editor.util.YangTag;

/**
 * @author Victor Kachesov
 * @date Oct 08, 2014
 */
public class ChooseParentModuleDialog extends ElementListSelectionDialog {

    private Text prefix;
    private Submodule subModule;
    private ElementIndexInfo[] list;

    public ChooseParentModuleDialog(Shell parent, Submodule subModule, IFile file) {
        super(parent, new LabelProvider() {
            public String getText(Object element) {
                if (element instanceof ElementIndexInfo) {
                    return ((ElementIndexInfo) element).getName();
                }
                return null;
            }

            @Override
            public Image getImage(Object element) {
                return GraphitiUi.getImageService().getImageForId(YangDiagramImageProvider.DIAGRAM_TYPE_PROVIDER_ID,
                        YangDiagramImageProvider.IMG_MODULE_PROPOSAL);
            }

        });
        setAllowDuplicates(false);
        this.subModule = subModule;
        list = YangModelManager.search(null, null, null, ElementIndexType.MODULE,
                null == file ? null : file.getProject(), null);
        setElements(list);
        setTitle("Select parent module");
        setImage(GraphitiUi.getImageService().getImageForId(YangDiagramImageProvider.DIAGRAM_TYPE_PROVIDER_ID,
                YangDiagramImageProvider.IMG_IMPORT_PROPOSAL));
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(content);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(content);
        super.createDialogArea(content);

        Composite appendix = new Composite(content, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.numColumns = 2;
        appendix.setLayout(layout);
        appendix.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        CLabel label = new CLabel(appendix, SWT.NONE);
        label.setText("Prefix");
        prefix = new Text(appendix, SWT.BORDER);
        prefix.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        init();

        return content;
    }

    @Override
    protected void okPressed() {
        computeResult();
        if (null == getFirstResult()) {
            MessageDialog.openWarning(getShell(), "Warning", "No module was choosen");
        } else if (null == prefix.getText() || prefix.getText().isEmpty()) {
            MessageDialog.openWarning(getShell(), "Warning", "Prefix is not defined");
        } else {
            setResultObject();
            super.okPressed();
        }
    }

    private void init() {
        BelongsTo belongsTo = subModule.getBelongsTo();
        if (belongsTo != null) {
            Module parentModule = belongsTo.getOwnerModule();
            if (parentModule != null) {
                prefix.setText((String) YangModelUtil.getValue(YangTag.PREFIX, parentModule));
                ElementIndexInfo current = findByName(parentModule.getName());
                setSelection(new Object[] { current });
            }
        }
    }

    private ElementIndexInfo findByName(String moduleName) {
        for (ElementIndexInfo info : list) {
            if (info.getModule().equals(moduleName)) {
                return info;
            }
        }
        return null;
    }

    private void setResultObject() {
        ElementIndexInfo choosen = (ElementIndexInfo) getFirstResult();

        BelongsTo result = ModelFactory.eINSTANCE.createBelongsTo();
        Module parentModule = ModelFactory.eINSTANCE.createModule();
        parentModule.setName(choosen.getName());
        YangModelUtil.setValue(YangTag.PREFIX, parentModule, prefix.getText());
        result.setOwnerModule(parentModule);
        subModule.setBelongsTo(result);
    }
}
