/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

import com.cisco.yangide.core.parser.YangFormattingPreferences;
import com.cisco.yangide.core.parser.YangParserUtil;
import com.cisco.yangide.editor.YangEditorPlugin;
import com.cisco.yangide.editor.editors.YangColorManager;
import com.cisco.yangide.editor.editors.YangSourceViewerConfiguration;
import com.cisco.yangide.ui.YangUIPlugin;
import com.cisco.yangide.ui.preferences.YangPreferenceConstants;

/**
 * @author Konstantin Zaitsev
 * @date Jul 22, 2014
 */
public class YangFormatterPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private SourceViewer previewViewer;
    private YangColorManager colorManager;
    private Button useTabs;
    private Text tabSizeTxt;
    private Text lineWidthTxt;
    private Button formatString;
    private Button formatComments;
    private Button collapseImport;
    private String content;

    public YangFormatterPreferencePage() {
        colorManager = new YangColorManager(false);
    }

    @Override
    public void init(IWorkbench workbench) {
        this.setPreferenceStore(YangUIPlugin.getDefault().getPreferenceStore());
    }

    @Override
    public void dispose() {
        super.dispose();
        colorManager.dispose();
    }

    @Override
    protected Control createContents(Composite parent) {
        initializeDialogUnits(parent);
        Composite content = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        content.setLayout(layout);

        Group indentationGroup = createGroup(2, content, "Indentation");

        new Label(indentationGroup, SWT.NONE).setText("Tab size:");
        tabSizeTxt = new Text(indentationGroup, SWT.BORDER);

        useTabs = new Button(indentationGroup, SWT.CHECK);
        useTabs.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
        useTabs.setText("Use tab character instead of space");

        Group wrappingGroup = createGroup(2, content, "Line Wrapping");

        new Label(wrappingGroup, SWT.NONE).setText("Maximum line width (characters):");
        lineWidthTxt = new Text(wrappingGroup, SWT.BORDER);

        formatString = new Button(wrappingGroup, SWT.CHECK);
        formatString.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
        formatString.setText("Wrap long strings (description, references, etc)");

        formatComments = new Button(wrappingGroup, SWT.CHECK);
        formatComments.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
        formatComments.setText("Wrap long block comments");

        collapseImport = new Button(wrappingGroup, SWT.CHECK);
        collapseImport.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
        collapseImport.setText("Collapse import statement in single line");

        Label previewLabel = new Label(content, SWT.LEFT);
        previewLabel.setText("Preview:");
        previewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Control previewer = createPreviewer(content);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.widthHint = convertWidthInCharsToPixels(20);
        gd.heightHint = convertHeightInCharsToPixels(5);
        previewer.setLayoutData(gd);

        initializeFields();

        applyDialogFont(content);
        updatePreview();
        return content;
    }

    private void initializeFields() {
        tabSizeTxt.setText(String.valueOf(getPreferenceStore().getInt(YangPreferenceConstants.FMT_INDENT_WIDTH)));
        useTabs.setSelection(!getPreferenceStore().getBoolean(YangPreferenceConstants.FMT_INDENT_SPACE));
        lineWidthTxt.setText(String.valueOf(getPreferenceStore().getInt(YangPreferenceConstants.FMT_MAX_LINE_LENGTH)));
        formatComments.setSelection(getPreferenceStore().getBoolean(YangPreferenceConstants.FMT_COMMENT));
        formatString.setSelection(getPreferenceStore().getBoolean(YangPreferenceConstants.FMT_STRING));
        collapseImport.setSelection(getPreferenceStore().getBoolean(YangPreferenceConstants.FMT_COMPACT_IMPORT));

        tabSizeTxt.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                updatePreview();
            }
        });
        useTabs.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updatePreview();
            }
        });
        lineWidthTxt.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                updatePreview();
            }
        });
        formatComments.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updatePreview();
            }
        });
        formatString.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updatePreview();
            }
        });
        collapseImport.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updatePreview();
            }
        });
    }

    @Override
    public boolean performOk() {
        IPreferenceStore store = getPreferenceStore();
        store.setValue(YangPreferenceConstants.FMT_INDENT_SPACE, !useTabs.getSelection());
        store.setValue(YangPreferenceConstants.FMT_INDENT_WIDTH, Integer.parseInt(tabSizeTxt.getText()));
        store.setValue(YangPreferenceConstants.FMT_MAX_LINE_LENGTH, Integer.parseInt(lineWidthTxt.getText()));
        store.setValue(YangPreferenceConstants.FMT_COMMENT, formatComments.getSelection());
        store.setValue(YangPreferenceConstants.FMT_STRING, formatString.getSelection());
        store.setValue(YangPreferenceConstants.FMT_COMPACT_IMPORT, collapseImport.getSelection());
        return true;
    }

    @Override
    protected void performDefaults() {
        IPreferenceStore store = getPreferenceStore();
        tabSizeTxt.setText(String.valueOf(store.getDefaultInt(YangPreferenceConstants.FMT_INDENT_WIDTH)));
        useTabs.setSelection(!store.getDefaultBoolean(YangPreferenceConstants.FMT_INDENT_SPACE));
        lineWidthTxt.setText(String.valueOf(store.getDefaultInt(YangPreferenceConstants.FMT_MAX_LINE_LENGTH)));
        formatComments.setSelection(store.getDefaultBoolean(YangPreferenceConstants.FMT_COMMENT));
        formatString.setSelection(store.getDefaultBoolean(YangPreferenceConstants.FMT_STRING));
        collapseImport.setSelection(store.getDefaultBoolean(YangPreferenceConstants.FMT_COMPACT_IMPORT));
        super.performDefaults();
    }

    private void updatePreview() {
        YangFormattingPreferences preferences = new YangFormattingPreferences();
        preferences.setSpaceForTabs(!useTabs.getSelection());
        preferences.setIndentSize(Integer.parseInt(tabSizeTxt.getText()));
        preferences.setMaxLineLength(Integer.parseInt(lineWidthTxt.getText()));
        preferences.setCompactImport(collapseImport.getSelection());
        preferences.setFormatComment(formatComments.getSelection());
        preferences.setFormatStrings(formatString.getSelection());

        String str = YangParserUtil.formatYangSource(preferences, content.toCharArray(), 0,
                TextUtilities.getDefaultLineDelimiter(previewViewer.getDocument()));
        previewViewer.getDocument().set(str);
    }

    private Group createGroup(int numColumns, Composite parent, String text) {
        final Group group = new Group(parent, SWT.NONE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = numColumns;
        gd.widthHint = 0;
        group.setLayoutData(gd);
        group.setFont(parent.getFont());

        final GridLayout layout = new GridLayout(numColumns, false);
        group.setLayout(layout);
        group.setText(text);
        return group;
    }

    private Control createPreviewer(Composite parent) {
        previewViewer = new SourceViewer(parent, null, null, false, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

        YangSourceViewerConfiguration configuration = new YangSourceViewerConfiguration(YangEditorPlugin.getDefault()
                .getCombinedPreferenceStore(), colorManager, null);

        previewViewer.configure(configuration);
        previewViewer.setEditable(false);
        Font font = JFaceResources.getFont(JFaceResources.TEXT_FONT);
        previewViewer.getTextWidget().setFont(font);

        IPreferenceStore store = new ChainedPreferenceStore(new IPreferenceStore[] { getPreferenceStore(),
                EditorsUI.getPreferenceStore() });

        new YangPreviewerUpdater(previewViewer, configuration, store);

        content = YangEditorPlugin.getDefault().getBundleFileContent("/resources/FormatterSettingPreviewCode.txt");
        IDocument document = new Document(content);
        new YangDocumentSetupParticipant().setup(document);
        previewViewer.setDocument(document);

        return previewViewer.getControl();
    }
}
