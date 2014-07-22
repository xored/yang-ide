/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.preferences;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.model.WorkbenchViewerComparator;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

import com.cisco.yangide.editor.YangEditorPlugin;
import com.cisco.yangide.editor.editors.YangColorManager;
import com.cisco.yangide.editor.editors.YangSourceViewerConfiguration;
import com.cisco.yangide.ui.preferences.IYangColorConstants;
import com.cisco.yangide.ui.preferences.OverlayPreferenceStore;
import com.cisco.yangide.ui.preferences.OverlayPreferenceStore.OverlayKey;

/**
 * Configures YANG Editor hover preferences.
 *
 * @author Alexey Kholupko
 */
class YangEditorColoringConfigurationBlock extends AbstractConfigurationBlock {

    /**
     * Item in the highlighting color list.
     */
    private static class HighlightingColorListItem {
        /** Display name */
        private String fDisplayName;
        /** Color preference key */
        private String fColorKey;
        /** Bold preference key */
        private String fBoldKey;
        /** Italic preference key */
        private String fItalicKey;
        /**
         * Strikethrough preference key.
         */
        private String fStrikethroughKey;
        /**
         * Underline preference key.
         */
        private String fUnderlineKey;

        /**
         * Initialize the item with the given values.
         */
        public HighlightingColorListItem(String displayName, String colorKey, String boldKey, String italicKey,
                String strikethroughKey, String underlineKey) {
            fDisplayName = displayName;
            fColorKey = colorKey;
            fBoldKey = boldKey;
            fItalicKey = italicKey;
            fStrikethroughKey = strikethroughKey;
            fUnderlineKey = underlineKey;
        }

        /**
         * @return the bold preference key
         */
        public String getBoldKey() {
            return fBoldKey;
        }

        public String getItalicKey() {
            return fItalicKey;
        }

        public String getStrikethroughKey() {
            return fStrikethroughKey;
        }

        public String getUnderlineKey() {
            return fUnderlineKey;
        }

        public String getColorKey() {
            return fColorKey;
        }

        public String getDisplayName() {
            return fDisplayName;
        }
    }

    /**
     * Color list label provider.
     */
    private class ColorListLabelProvider extends LabelProvider {
        /*
         * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
         */
        @Override
        public String getText(Object element) {
            if (element instanceof String) {
                return (String) element;
            }
            return ((HighlightingColorListItem) element).getDisplayName();
        }
    }

    /**
     * Color list content provider.
     */
    private class ColorListContentProvider implements IStructuredContentProvider {

        /*
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        @Override
        @SuppressWarnings("rawtypes")
        public Object[] getElements(Object inputElement) {
            return ((java.util.List) inputElement).toArray();
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

    /**
     * Preference key suffix for bold preferences.
     */
    private static final String BOLD = PreferenceConstants.EDITOR_BOLD_SUFFIX;

    private static final String ITALIC = PreferenceConstants.EDITOR_ITALIC_SUFFIX;

    private static final String STRIKETHROUGH = PreferenceConstants.EDITOR_STRIKETHROUGH_SUFFIX;

    private static final String UNDERLINE = PreferenceConstants.EDITOR_UNDERLINE_SUFFIX;

    /**
     * The keys of the overlay store.
     */
    private final String[][] fSyntaxColorListModel = new String[][] {

            { YangPreferencesMessages.YANGEditorPreferencePage_strings, IYangColorConstants.YANG_STRING },
            { YangPreferencesMessages.YANGEditorPreferencePage_keywords, IYangColorConstants.YANG_KEYWORD },
            { YangPreferencesMessages.YANGEditorPreferencePage_comments, IYangColorConstants.YANG_COMMENT },
            { YangPreferencesMessages.YANGEditorPreferencePage_identifiers, IYangColorConstants.YANG_IDENTIFIER },
            { YangPreferencesMessages.YANGEditorPreferencePage_types, IYangColorConstants.YANG_TYPE },
            { YangPreferencesMessages.YANGEditorPreferencePage_numbers, IYangColorConstants.YANG_NUMBER } };

    private ColorSelector fSyntaxForegroundColorEditor;
    private Label fColorEditorLabel;

    /**
     * Check box for bold preference.
     */
    private Button fBoldCheckBox;

    private Button fItalicCheckBox;

    private Button fStrikethroughCheckBox;

    private Button fUnderlineCheckBox;
    /**
     * Highlighting color list
     */
    private final java.util.List<HighlightingColorListItem> fListModel = new ArrayList<HighlightingColorListItem>();

    private TableViewer fHighlightingColorListViewer;

    private SourceViewer fPreviewViewer;

    private IColorManager fColorManager;

    private FontMetrics fFontMetrics;

    public YangEditorColoringConfigurationBlock(OverlayPreferenceStore store) {
        super(store);

        fColorManager = new YangColorManager(false);

        for (int i = 0, n = fSyntaxColorListModel.length; i < n; i++) {
            fListModel.add(new HighlightingColorListItem(fSyntaxColorListModel[i][0], fSyntaxColorListModel[i][1],
                    fSyntaxColorListModel[i][1] + BOLD, fSyntaxColorListModel[i][1] + ITALIC,
                    fSyntaxColorListModel[i][1] + STRIKETHROUGH, fSyntaxColorListModel[i][1] + UNDERLINE));
        }

        store.addKeys(createOverlayStoreKeys());
    }

    private OverlayPreferenceStore.OverlayKey[] createOverlayStoreKeys() {

        ArrayList<OverlayKey> overlayKeys = new ArrayList<OverlayKey>();

        for (int i = 0, n = fListModel.size(); i < n; i++) {
            HighlightingColorListItem item = fListModel.get(i);
            overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.STRING, item.getColorKey()));
            overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, item.getBoldKey()));
            overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, item.getItalicKey()));
            overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, item
                    .getStrikethroughKey()));
            overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, item
                    .getUnderlineKey()));

        }

        OverlayPreferenceStore.OverlayKey[] keys = new OverlayPreferenceStore.OverlayKey[overlayKeys.size()];
        overlayKeys.toArray(keys);
        return keys;
    }

    @Override
    public Control createControl(Composite parent) {
        initializeDialogUnits(parent);

        ScrolledPageContent scrolled = new ScrolledPageContent(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        scrolled.setExpandHorizontal(true);
        scrolled.setExpandVertical(true);

        Control control = createSyntaxPage(scrolled);

        scrolled.setContent(control);
        final Point size = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        scrolled.setMinSize(size.x, size.y);

        return scrolled;
    }

    /**
     * Returns the number of pixels corresponding to the width of the given number of characters.
     * This method may only be called after <code>initializeDialogUnits</code> has been called.
     */
    private int convertWidthInCharsToPixels(int chars) {
        // test for failure to initialize for backward compatibility
        if (fFontMetrics == null) {
            return 0;
        }
        return Dialog.convertWidthInCharsToPixels(fFontMetrics, chars);
    }

    private int convertHeightInCharsToPixels(int chars) {
        // test for failure to initialize for backward compatibility
        if (fFontMetrics == null) {
            return 0;
        }
        return Dialog.convertHeightInCharsToPixels(fFontMetrics, chars);
    }

    @Override
    public void initialize() {
        super.initialize();

        fHighlightingColorListViewer.setInput(fListModel);
        fHighlightingColorListViewer
        .setSelection(new StructuredSelection(fHighlightingColorListViewer.getElementAt(0)));
    }

    @Override
    public void performDefaults() {
        super.performDefaults();

        handleSyntaxColorListSelection();

        fPreviewViewer.invalidateTextPresentation();
    }

    /*
     * @see com.cisco.yangide.editor.preferences.IPreferenceConfigurationBlock#dispose()
     */
    @Override
    public void dispose() {

        fColorManager.dispose();

        super.dispose();
    }

    private void handleSyntaxColorListSelection() {
        HighlightingColorListItem item = getHighlightingColorListItem();
        if (item == null) {
            fSyntaxForegroundColorEditor.getButton().setEnabled(false);
            fColorEditorLabel.setEnabled(false);
            fBoldCheckBox.setEnabled(false);
            fItalicCheckBox.setEnabled(false);
            fStrikethroughCheckBox.setEnabled(false);
            fUnderlineCheckBox.setEnabled(false);
            return;
        }
        RGB rgb = PreferenceConverter.getColor(getPreferenceStore(), item.getColorKey());
        fSyntaxForegroundColorEditor.setColorValue(rgb);
        fBoldCheckBox.setSelection(getPreferenceStore().getBoolean(item.getBoldKey()));
        fItalicCheckBox.setSelection(getPreferenceStore().getBoolean(item.getItalicKey()));
        fStrikethroughCheckBox.setSelection(getPreferenceStore().getBoolean(item.getStrikethroughKey()));
        fUnderlineCheckBox.setSelection(getPreferenceStore().getBoolean(item.getUnderlineKey()));

        fSyntaxForegroundColorEditor.getButton().setEnabled(true);
        fColorEditorLabel.setEnabled(true);
        fBoldCheckBox.setEnabled(true);
        fItalicCheckBox.setEnabled(true);
        fStrikethroughCheckBox.setEnabled(true);
        fUnderlineCheckBox.setEnabled(true);

    }

    private Control createSyntaxPage(final Composite parent) {

        Composite colorComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        colorComposite.setLayout(layout);

        Link link = new Link(colorComposite, SWT.NONE);
        link.setText(YangPreferencesMessages.YANGEditorColoringConfigurationBlock_link);
        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if ("org.eclipse.ui.preferencePages.GeneralTextEditor".equals(e.text)) {
                    PreferencesUtil.createPreferenceDialogOn(parent.getShell(), e.text, null, null);
                } else if ("org.eclipse.ui.preferencePages.ColorsAndFonts".equals(e.text)) {
                    PreferencesUtil.createPreferenceDialogOn(parent.getShell(), e.text, null,
                            "selectFont:org.eclipse.jdt.ui.editors.textfont"); //$NON-NLS-1$
                }
            }
        });

        GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        gridData.widthHint = 150; // only expand further if anyone else requires it
        gridData.horizontalSpan = 2;
        link.setLayoutData(gridData);

        addFiller(colorComposite, 1);

        Label label;
        label = new Label(colorComposite, SWT.LEFT);
        label.setText(YangPreferencesMessages.YANGEditorPreferencePage_coloring_element);
        label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Composite editorComposite = new Composite(colorComposite, SWT.NONE);
        layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        editorComposite.setLayout(layout);
        GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        editorComposite.setLayoutData(gd);

        fHighlightingColorListViewer = new TableViewer(editorComposite, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER
                | SWT.FULL_SELECTION);
        fHighlightingColorListViewer.setLabelProvider(new ColorListLabelProvider());
        fHighlightingColorListViewer.setContentProvider(new ColorListContentProvider());
        fHighlightingColorListViewer.setComparator(new WorkbenchViewerComparator());
        gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, true);
        gd.heightHint = convertHeightInCharsToPixels(9);
        int maxWidth = 0;
        for (Iterator<HighlightingColorListItem> it = fListModel.iterator(); it.hasNext();) {
            HighlightingColorListItem item = it.next();
            maxWidth = Math.max(maxWidth, convertWidthInCharsToPixels(item.getDisplayName().length()));
        }
        ScrollBar vBar = ((Scrollable) fHighlightingColorListViewer.getControl()).getVerticalBar();
        if (vBar != null) {
            maxWidth += vBar.getSize().x * 3; // scrollbars and tree indentation guess
        }
        gd.widthHint = maxWidth;

        fHighlightingColorListViewer.getControl().setLayoutData(gd);

        Composite stylesComposite = new Composite(editorComposite, SWT.NONE);
        layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.numColumns = 2;
        stylesComposite.setLayout(layout);
        stylesComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        fColorEditorLabel = new Label(stylesComposite, SWT.LEFT);
        fColorEditorLabel.setText(YangPreferencesMessages.YANGEditorPreferencePage_color);
        gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);

        gd.horizontalIndent = 20;
        fColorEditorLabel.setLayoutData(gd);

        fSyntaxForegroundColorEditor = new ColorSelector(stylesComposite);
        Button foregroundColorButton = fSyntaxForegroundColorEditor.getButton();
        gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        foregroundColorButton.setLayoutData(gd);

        fBoldCheckBox = new Button(stylesComposite, SWT.CHECK);
        fBoldCheckBox.setText(YangPreferencesMessages.YANGEditorPreferencePage_bold);
        gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gd.horizontalIndent = 20;
        gd.horizontalSpan = 2;
        fBoldCheckBox.setLayoutData(gd);

        fItalicCheckBox = new Button(stylesComposite, SWT.CHECK);
        fItalicCheckBox.setText(YangPreferencesMessages.YANGEditorPreferencePage_italic);
        gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gd.horizontalIndent = 20;
        gd.horizontalSpan = 2;
        fItalicCheckBox.setLayoutData(gd);

        fStrikethroughCheckBox = new Button(stylesComposite, SWT.CHECK);
        fStrikethroughCheckBox.setText(YangPreferencesMessages.YANGEditorPreferencePage_strikethrough);
        gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gd.horizontalIndent = 20;
        gd.horizontalSpan = 2;
        fStrikethroughCheckBox.setLayoutData(gd);

        fUnderlineCheckBox = new Button(stylesComposite, SWT.CHECK);
        fUnderlineCheckBox.setText(YangPreferencesMessages.YANGEditorPreferencePage_underline);
        gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gd.horizontalIndent = 20;
        gd.horizontalSpan = 2;
        fUnderlineCheckBox.setLayoutData(gd);

        label = new Label(colorComposite, SWT.LEFT);
        label.setText(YangPreferencesMessages.YANGEditorPreferencePage_preview);
        label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Control previewer = createPreviewer(colorComposite);
        gd = new GridData(GridData.FILL_BOTH);
        gd.widthHint = convertWidthInCharsToPixels(20);
        gd.heightHint = convertHeightInCharsToPixels(5);
        previewer.setLayoutData(gd);

        fHighlightingColorListViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                handleSyntaxColorListSelection();
            }
        });

        foregroundColorButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // do nothing
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                HighlightingColorListItem item = getHighlightingColorListItem();
                PreferenceConverter.setValue(getPreferenceStore(), item.getColorKey(),
                        fSyntaxForegroundColorEditor.getColorValue());
            }
        });

        fBoldCheckBox.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // do nothing
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                HighlightingColorListItem item = getHighlightingColorListItem();
                getPreferenceStore().setValue(item.getBoldKey(), fBoldCheckBox.getSelection());
            }
        });

        fItalicCheckBox.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // do nothing
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                HighlightingColorListItem item = getHighlightingColorListItem();
                getPreferenceStore().setValue(item.getItalicKey(), fItalicCheckBox.getSelection());
            }
        });
        fStrikethroughCheckBox.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // do nothing
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                HighlightingColorListItem item = getHighlightingColorListItem();
                getPreferenceStore().setValue(item.getStrikethroughKey(), fStrikethroughCheckBox.getSelection());
            }
        });

        fUnderlineCheckBox.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // do nothing
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                HighlightingColorListItem item = getHighlightingColorListItem();
                getPreferenceStore().setValue(item.getUnderlineKey(), fUnderlineCheckBox.getSelection());
            }
        });

        colorComposite.layout(false);

        return colorComposite;
    }

    private void addFiller(Composite composite, int horizontalSpan) {
        PixelConverter pixelConverter = new PixelConverter(composite);
        Label filler = new Label(composite, SWT.LEFT);
        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd.horizontalSpan = horizontalSpan;
        gd.heightHint = pixelConverter.convertHeightInCharsToPixels(1) / 2;
        filler.setLayoutData(gd);
    }

    private Control createPreviewer(Composite parent) {

        fPreviewViewer = new SourceViewer(parent, null, null, false, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

        YangSourceViewerConfiguration configuration = new YangSourceViewerConfiguration(YangEditorPlugin.getDefault()
                .getCombinedPreferenceStore(), fColorManager, null);

        fPreviewViewer.configure(configuration);
        fPreviewViewer.setEditable(false);
        Font font = JFaceResources.getFont(JFaceResources.TEXT_FONT);
        fPreviewViewer.getTextWidget().setFont(font);

        IPreferenceStore store = new ChainedPreferenceStore(new IPreferenceStore[] { getPreferenceStore(),
                EditorsUI.getPreferenceStore() });

        new YangPreviewerUpdater(fPreviewViewer, configuration, store);

        String content = YangEditorPlugin.getDefault().getBundleFileContent("/resources/ColorSettingPreviewCode.txt"); //$NON-NLS-1$
        IDocument document = new Document(content);
        new YangDocumentSetupParticipant().setup(document);
        fPreviewViewer.setDocument(document);

        return fPreviewViewer.getControl();
    }

    /**
     * Returns the current highlighting color list item.
     *
     * @return the current highlighting color list item
     */
    private HighlightingColorListItem getHighlightingColorListItem() {
        IStructuredSelection selection = (IStructuredSelection) fHighlightingColorListViewer.getSelection();
        Object element = selection.getFirstElement();
        if (element instanceof String) {
            return null;
        }
        return (HighlightingColorListItem) element;
    }

    /**
     * Initializes the computation of horizontal and vertical dialog units based on the size of
     * current font.
     * <p>
     * This method must be called before any of the dialog unit based conversion methods are called.
     * </p>
     *
     * @param testControl a control from which to obtain the current font
     */
    private void initializeDialogUnits(Control testControl) {
        // Compute and store a font metric
        GC gc = new GC(testControl);
        gc.setFont(JFaceResources.getDialogFont());
        fFontMetrics = gc.getFontMetrics();
        gc.dispose();
    }

}
