/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.model.editor.preferences;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import com.cisco.yangide.ext.model.editor.Activator;

/**
 * @author Konstantin Zaitsev
 * @date Aug 29, 2014
 */
public class ModelEditorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private FontData[] fontData;
    private Label preview;
    private Font oldFont;

    @Override
    public void init(IWorkbench workbench) {
        this.setPreferenceStore(Activator.getDefault().getPreferenceStore());
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite pageArea = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(3).spacing(LayoutConstants.getSpacing().x, 3).applyTo(pageArea);

        Label label = new Label(pageArea, SWT.NONE);
        label.setText("Diagram Editor Font: ");
        GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.TOP).span(1, 3).applyTo(label);

        preview = new Label(pageArea, SWT.BORDER | SWT.SHADOW_NONE);
        GridDataFactory.fillDefaults().grab(true, false).span(1, 2).applyTo(preview);

        Button editBtn = new Button(pageArea, SWT.PUSH);
        GridDataFactory.fillDefaults().hint(100, SWT.DEFAULT).applyTo(editBtn);
        editBtn.setText("Edit...");
        editBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FontDialog dialog = new FontDialog(getShell());
                dialog.setFontList(fontData);
                FontData fd = dialog.open();
                if (fd != null) {
                    fontData = new FontData[] { fd };
                    update();
                }
            }
        });
        Button systemBtn = new Button(pageArea, SWT.PUSH);
        GridDataFactory.fillDefaults().hint(100, SWT.DEFAULT).applyTo(systemBtn);
        systemBtn.setText("Use System Font");
        systemBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                fontData = JFaceResources.getDefaultFont().getFontData();
                update();
            }
        });

        Composite note = createNoteComposite(JFaceResources.getDialogFont(), pageArea, "Note: ",
                "Changing the font does not update open editors.");
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(note);

        Dialog.applyDialogFont(pageArea);
        load();
        return pageArea;
    }

    private void load() {
        fontData = toFontData(getPreferenceStore().getString(ModelEditorPreferences.DIAGRAM_EDITOR_FONT));
        if (fontData == null) {
            fontData = JFaceResources.getDefaultFont().getFontData();
        }
        update();
    }

    @Override
    public boolean performOk() {
        getPreferenceStore().setValue(ModelEditorPreferences.DIAGRAM_EDITOR_FONT, StringConverter.asString(fontData));
        return true;
    }

    @Override
    protected void performDefaults() {
        fontData = toFontData(getPreferenceStore().getDefaultString(ModelEditorPreferences.DIAGRAM_EDITOR_FONT));
        if (fontData == null) {
            fontData = JFaceResources.getDefaultFont().getFontData();
        }
        update();
        super.performDefaults();
    }

    private FontData[] toFontData(String str) {
        return JFaceResources.getFontRegistry().filterData(StringConverter.asFontDataArray(str),
                PlatformUI.getWorkbench().getDisplay());
    }

    private void update() {
        if (fontData != null && fontData.length > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(" ").append(fontData[0].getName()).append(" ").append(fontData[0].getHeight()).append("\n");
            sb.append(" The quick brown fox jumps over the lazy dog.");
            Font newFont = new Font(Display.getCurrent(), fontData);
            preview.setFont(newFont);
            preview.setText(sb.toString());

            if (oldFont != null) {
                oldFont.dispose();
            }
            oldFont = newFont;
        }
    }

    @Override
    public void dispose() {
        if (oldFont != null) {
            oldFont.dispose();
        }
        super.dispose();
    }
}
