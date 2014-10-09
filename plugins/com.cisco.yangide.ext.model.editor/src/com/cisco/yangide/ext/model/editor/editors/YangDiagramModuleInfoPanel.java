package com.cisco.yangide.ext.model.editor.editors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.cisco.yangide.ext.model.Import;
import com.cisco.yangide.ext.model.ModelFactory;
import com.cisco.yangide.ext.model.Module;
import com.cisco.yangide.ext.model.Revision;
import com.cisco.yangide.ext.model.TaggedNode;
import com.cisco.yangide.ext.model.editor.dialog.AddImportDialog;
import com.cisco.yangide.ext.model.editor.dialog.MultilineTextDialog;
import com.cisco.yangide.ext.model.editor.util.BusinessObjectWrapper;
import com.cisco.yangide.ext.model.editor.util.Strings;
import com.cisco.yangide.ext.model.editor.util.YangDiagramImageProvider;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;
import com.cisco.yangide.ext.model.editor.util.YangTag;
import com.cisco.yangide.ext.model.editor.widget.DialogText;

public class YangDiagramModuleInfoPanel implements BusinessObjectWrapper<Module> {

    private Text nameText;
    private TableViewer importTable;

    private Module module;
    private FormToolkit toolkit;
    private IFile file;

    private Composite diagram;
    // private PropertyEdit editPropertyForm;

    private Text namespaceText;
    private DialogText organizationText;
    private DialogText contactText;
    private Text prefixText;
    private Text yangVersionText;
    private DialogText descriptionText;
    private DialogText referenceText;
    private TableViewer revisionTable;
    private SashForm infoPane;
    private Composite leftPane;

    private SashForm mainSashPanel;
    
    private DataBindingContext bindingContext = new DataBindingContext();

    protected class PropertyEdit extends SashForm {
        private RevisionEdit editRevision;
        private ImportEdit editImport;

        protected class RevisionEdit implements BusinessObjectWrapper<Revision> {
            private Text description;
            private Text reference;
            private Text name;
            private Revision revision;
            private Composite pane;
            private List<Binding> dataBindigs = new ArrayList<Binding>();

            public RevisionEdit() {
                // pane = toolkit.createComposite(editPropertyForm, SWT.NONE);
                createPane();
            }

            protected void createPane() {
                GridDataFactory.fillDefaults().grab(true, false).applyTo(pane);
                GridLayoutFactory.fillDefaults().numColumns(2).applyTo(pane);
                toolkit.createLabel(pane, "Name: ");
                name = toolkit.createText(pane, "");
                GridDataFactory.fillDefaults().grab(true, false).applyTo(name);

                toolkit.createLabel(pane, "Description: ");
                description = toolkit.createText(pane, "", SWT.MULTI | SWT.WRAP);
                GridDataFactory.fillDefaults().grab(true, true).applyTo(description);

                toolkit.createLabel(pane, "Reference: ");
                reference = toolkit.createText(pane, "", SWT.MULTI | SWT.WRAP);
                GridDataFactory.fillDefaults().grab(true, true).applyTo(reference);

            }

            protected void addListeneres() {
                removeBindings(dataBindigs);
                dataBindigs.clear();
                dataBindigs.add(addTextFieldListener(this, reference, YangTag.REFERENCE));
                dataBindigs.add(addTextFieldListener(this, description, YangTag.DESCRIPTION));
                dataBindigs.add(addTextFieldListener(this, name, YangModelUtil.MODEL_PACKAGE.getNamedNode_Name()));
            }

            public void updateData() {
                if (null != revision) {
                    name.setText(revision.getName());
                    description.setText(Strings.getAsString(YangModelUtil.getValue(YangTag.DESCRIPTION, revision)));
                    reference.setText(Strings.getAsString(YangModelUtil.getValue(YangTag.REFERENCE, revision)));
                    addListeneres();
                }
            }

            public Composite getPane(Revision revision) {
                if (revision != this.revision) {
                    this.revision = revision;
                    updateData();
                }
                return pane;
            }

            @Override
            public Revision getBusinessObject() {
                return revision;
            }

        }

        protected class ImportEdit implements BusinessObjectWrapper<Import> {
            private Text name;
            private Text prefix;
            private Text revision;
            private Import importObj;
            private Composite pane;
            private List<Binding> dataBindigs = new ArrayList<Binding>();

            public ImportEdit() {
                // pane = toolkit.createComposite(editPropertyForm, SWT.NONE);
                createPane();
            }

            protected void createPane() {
                GridLayoutFactory.fillDefaults().numColumns(2).applyTo(pane);
                GridDataFactory.fillDefaults().grab(true, false).applyTo(pane);
                toolkit.createLabel(pane, "Name: ");
                name = toolkit.createText(pane, "");
                name.setEditable(false);
                name.setEnabled(false);
                GridDataFactory.fillDefaults().grab(true, false).applyTo(name);

                toolkit.createLabel(pane, "Prefix: ");
                prefix = toolkit.createText(pane, "");
                prefix.setEditable(false);
                prefix.setEnabled(false);
                GridDataFactory.fillDefaults().grab(true, false).applyTo(prefix);

                toolkit.createLabel(pane, "Revision: ");
                revision = toolkit.createText(pane, "");
                revision.setEditable(false);
                revision.setEnabled(false);
                GridDataFactory.fillDefaults().grab(true, false).applyTo(revision);

            }

            protected void addListeneres() {
                removeBindings(dataBindigs);
                dataBindigs.clear();
                dataBindigs.add(addTextFieldListener(this, prefix, YangModelUtil.MODEL_PACKAGE.getImport_Prefix()));
                dataBindigs.add(addTextFieldListener(this, revision,
                        YangModelUtil.MODEL_PACKAGE.getImport_RevisionDate()));
            }

            public void updateData() {
                if (null != importObj) {
                    name.setText(importObj.getModule());
                    prefix.setText(Strings.getAsString(importObj.getPrefix()));
                    revision.setText(Strings.getAsString(importObj.getRevisionDate()));
                    addListeneres();
                }
            }

            public Composite getPane(Import importObj) {
                if (importObj != this.importObj) {
                    this.importObj = importObj;
                    updateData();
                }
                return pane;
            }

            @Override
            public Import getBusinessObject() {
                return importObj;
            }

        }

        public PropertyEdit(Composite parent) {
            super(parent, SWT.HORIZONTAL);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(this);
            setLayout(new GridLayout(1, false));
        }

        public void setRevision(Revision revision) {
            if (null == editRevision) {
                editRevision = new RevisionEdit();
            }
            setMaximizedControl(editRevision.getPane(revision));
        }

        public void setImport(Import importObj) {
            if (null == editImport) {
                editImport = new ImportEdit();
            }
            setMaximizedControl(editImport.getPane(importObj));
        }

    }

    public YangDiagramModuleInfoPanel(Composite parent, Module module, IFile file) {
        this.module = module;
        this.file = file;
        mainSashPanel = new SashForm(parent, SWT.HORIZONTAL);
        mainSashPanel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        mainSashPanel.setLayout(new GridLayout(1, false));

        leftPane = new Composite(mainSashPanel, SWT.NONE);
        diagram = new Composite(mainSashPanel, SWT.NONE);

        GridLayoutFactory.fillDefaults().applyTo(diagram);
        GridDataFactory.fillDefaults().grab(false, true).hint(200, -1).applyTo(leftPane);
        GridLayoutFactory.fillDefaults().applyTo(leftPane);

        createModuleInfoPanel(leftPane);

        GridDataFactory.fillDefaults().grab(true, true).applyTo(diagram);

        setPropertiesPaneVisible(false);
        parent.layout();
        final Point leftSize = leftPane.computeSize(-1, -1);

        leftPane.addControlListener(new ControlListener() {
            @Override
            public void controlResized(ControlEvent e) {
                Point size = leftPane.getSize();
                if (Math.abs(leftSize.x - size.x) > 5) {
                    leftSize.x = size.x;
                }
            }

            @Override
            public void controlMoved(ControlEvent e) {
                // TODO Auto-generated method stub

            }
        });

        mainSashPanel.addControlListener(new ControlListener() {
            @Override
            public void controlResized(ControlEvent e) {
                Point area = mainSashPanel.getSize();
                int x = Math.min(leftSize.x, leftPane.computeSize(leftSize.x, -1).x);
                mainSashPanel.setWeights(new int[] { x, area.x - x });
            }

            @Override
            public void controlMoved(ControlEvent e) {
                // TODO Auto-generated method stub

            }
        });
    }

    protected void createModuleInfoPanel(Composite parent) {
        toolkit = new FormToolkit(parent.getDisplay());
        toolkit.adapt(parent);
        ScrolledForm mainForm = toolkit.createScrolledForm(parent);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(mainForm);
        mainForm.setText("Module Info");
        toolkit.decorateFormHeading(mainForm.getForm());

        GridLayoutFactory.swtDefaults().applyTo(mainForm.getBody());
        infoPane = new SashForm(mainForm.getBody(), SWT.HORIZONTAL);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(infoPane);
        toolkit.adapt(infoPane);

        infoPane.setLayout(new FillLayout(SWT.VERTICAL));
        // pane = toolkit.createScrolledForm(infoPane);
        Composite pane = toolkit.createComposite(infoPane);
        GridLayoutFactory.fillDefaults().applyTo(pane);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(pane);

        // ScrolledForm editForm = toolkit.createScrolledForm(infoPane);
        // GridDataFactory.fillDefaults().grab(true, true).applyTo(editForm);
        //
        // GridLayoutFactory.fillDefaults().applyTo(editForm.getBody());
        // Section editSection = createSection(editForm, "Properties", Section.TITLE_BAR |
        // Section.EXPANDED);
        // GridDataFactory.fillDefaults().grab(true, false).applyTo(editSection);
        // editPropertyForm = new PropertyEdit(editSection);
        // toolkit.adapt(editPropertyForm);
        // editSection.setClient(editPropertyForm);

        createGeneralSection(pane);
        createRevisionSection(pane);
        createImportSection(pane);
        createMetaInfoSection(pane);
        // infoPane.setWeights(new int[] { 1, 0 });
        // infoPane.setMaximizedControl(pane);
        // createPropertiesButtonToolbar(editSection, infoPane, pane);
        // mainForm.pack();
        // mainForm.reflow(true);
    }

    protected void setPropertiesPaneVisible(boolean set) {
        // if (set) {
        // infoPane.setWeights(new int[] { 1, 1 });
        // infoPane.setMaximizedControl(null);
        // mainSashPanel.setWeights(new int[] { 4, 6 });
        //
        // } else {
        // infoPane.setWeights(new int[] { 1, 0 });
        // infoPane.setMaximizedControl(pane);
        // mainSashPanel.setWeights(new int[] { 2, 6 });
        // }
    }

    protected void createPropertiesButtonToolbar(final Section editSection, final SashForm infoPane,
            final ScrolledForm pane) {
        ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
        ToolBar toolbar = toolBarManager.createControl(editSection);
        toolbar.setCursor(Display.getDefault().getSystemCursor(SWT.CURSOR_HAND));

        Action closeButton = new Action("Close properties section", IAction.AS_CHECK_BOX) {

            @Override
            public void run() {
                super.run();
                setChecked(false);
                setPropertiesPaneVisible(false);
            }
        };

        closeButton.setImageDescriptor(GraphitiUi.getImageService().getImageDescriptorForId(
                YangDiagramImageProvider.DIAGRAM_TYPE_PROVIDER_ID,
                YangDiagramImageProvider.IMG_COLLAPSE_ALL_TOOL_PROPOSAL));
        closeButton.setEnabled(true);
        toolBarManager.add(closeButton);
        toolBarManager.update(true);
        editSection.setTextClient(toolbar);
    }

    protected void createMetaInfoSection(Composite parent) {
        Section section = createSection(parent, "Meta information");
        Composite meta = toolkit.createComposite(section);
        GridLayoutFactory.swtDefaults().numColumns(2).applyTo(meta);
        GridDataFactory.fillDefaults().hint(100, -1).grab(true, false).applyTo(section);

        toolkit.createLabel(meta, "Organization: ");
        organizationText = new DialogText(meta, toolkit) {

            @Override
            protected Object openDialogBox(Text text) {
                Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                MultilineTextDialog dialog = new MultilineTextDialog(shell, Strings.getAsString(text.getText()),
                        "Organization");
                if (IStatus.OK == dialog.open()) {
                    text.setText(dialog.getValue());
                }
                return null;
            }
        };

        toolkit.createLabel(meta, "Contact: ");
        contactText = new DialogText(meta, toolkit) {

            @Override
            protected Object openDialogBox(Text text) {
                Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                MultilineTextDialog dialog = new MultilineTextDialog(shell, Strings.getAsString(text.getText()),
                        "Contact");
                if (IStatus.OK == dialog.open()) {
                    text.setText(dialog.getValue());
                }
                return null;
            }
        };

        toolkit.createLabel(meta, "Description: ");
        descriptionText = new DialogText(meta, toolkit) {

            @Override
            protected Object openDialogBox(Text text) {
                Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                MultilineTextDialog dialog = new MultilineTextDialog(shell, Strings.getAsString(text.getText()),
                        "Description");
                if (IStatus.OK == dialog.open()) {
                    text.setText(dialog.getValue());
                }
                return null;
            }
        };

        toolkit.createLabel(meta, "Reference: ");
        referenceText = new DialogText(meta, toolkit) {

            @Override
            protected Object openDialogBox(Text text) {
                Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                MultilineTextDialog dialog = new MultilineTextDialog(shell, Strings.getAsString(text.getText()),
                        "Reference");
                if (IStatus.OK == dialog.open()) {
                    text.setText(dialog.getValue());
                }
                return null;
            }
        };

        updateMetaInfoSection();

        addMetaInfoSectionListeners();
        section.setClient(meta);
    }

    protected void createGeneralSection(Composite parent) {
        Section section = createSection(parent, "General");
        Composite header = toolkit.createComposite(section);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(header);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(section);

        toolkit.createLabel(header, "Name: ");
        nameText = toolkit.createText(header, "");
        nameText.setEditable(true);
        GridDataFactory.fillDefaults().hint(100, -1).grab(true, false).applyTo(nameText);

        toolkit.createLabel(header, "Yang-version: ");
        yangVersionText = toolkit.createText(header, "");
        yangVersionText.setEditable(true);
        GridDataFactory.fillDefaults().hint(100, -1).grab(true, false).applyTo(yangVersionText);

        toolkit.createLabel(header, "Namespace: ");
        namespaceText = toolkit.createText(header, "");
        namespaceText.setEditable(true);
        GridDataFactory.fillDefaults().hint(100, -1).grab(true, false).applyTo(namespaceText);

        toolkit.createLabel(header, "Prefix: ");
        prefixText = toolkit.createText(header, "");
        prefixText.setEditable(true);
        GridDataFactory.fillDefaults().hint(100, -1).grab(true, false).applyTo(prefixText);

        updateGeneralSection();
        addGeneralSectionListeners();
        section.setClient(header);
    }

    protected void updateMetaInfoSection() {
        if (null != module) {
            organizationText.setText(Strings.getAsString(YangModelUtil.getValue(YangTag.ORGANIZATION, module)));
            contactText.setText(Strings.getAsString(YangModelUtil.getValue(YangTag.CONTACT, module)));
            descriptionText.setText(Strings.getAsString(YangModelUtil.getValue(YangTag.DESCRIPTION, module)));
            referenceText.setText(Strings.getAsString(YangModelUtil.getValue(YangTag.REFERENCE, module)));
        }
    }

    protected void updateGeneralSection() {
        if (null != module) {
            nameText.setText(module.getName());
            yangVersionText.setText(Strings.getAsString(YangModelUtil.getValue(YangTag.YANG_VERSION, module)));
            namespaceText.setText(Strings.getAsString(YangModelUtil.getValue(YangTag.NAMESPACE, module)));
            prefixText.setText(Strings.getAsString(YangModelUtil.getValue(YangTag.PREFIX, module)));
        }
    }

    protected void addMetaInfoSectionListeners() {
        addTextFieldListener(this, organizationText.getTextControl(), YangTag.ORGANIZATION);
        addTextFieldListener(this, contactText.getTextControl(), YangTag.CONTACT);
        addTextFieldListener(this, descriptionText.getTextControl(), YangTag.DESCRIPTION);
        addTextFieldListener(this, referenceText.getTextControl(), YangTag.REFERENCE);
    }

    protected void addGeneralSectionListeners() {
        addTextFieldListener(this, nameText, YangModelUtil.MODEL_PACKAGE.getNamedNode_Name());
        addTextFieldListener(this, yangVersionText, YangTag.YANG_VERSION);
        addTextFieldListener(this, namespaceText, YangTag.NAMESPACE);
        addTextFieldListener(this, prefixText, YangTag.PREFIX);
    }

    protected void removeBindings(List<Binding> bindings) {
        for (Binding b : bindings) {
            b.updateTargetToModel();
            bindingContext.removeBinding(b);
        }
    }

    protected Binding addTextFieldListener(final BusinessObjectWrapper<? extends TaggedNode> node, Control text,
            final YangTag tag) {
        return bindingContext.bindValue(
                WidgetProperties.text(SWT.Modify).observeDelayed(200, text),
                EMFProperties.value(YangModelUtil.MODEL_PACKAGE.getTag_Value()).observe(
                        YangModelUtil.getTag(tag, node.getBusinessObject())));
    }

    protected Binding addTextFieldListener(final BusinessObjectWrapper<? extends EObject> node, final Text text,
            final EStructuralFeature esf) {
        return bindingContext.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(200, text), EMFProperties
                .value(esf).observe(node.getBusinessObject()));
    }

    protected void createRevisionSection(Composite parent) {
        Section section = createSection(parent, "Revision");
        Composite revisions = toolkit.createComposite(section);

        // Temporary code
        GridDataFactory.fillDefaults().grab(true, false).applyTo(revisions);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(revisions);
        toolkit.createLabel(revisions, "Name: ");
        Text name = toolkit.createText(revisions, "");
        GridDataFactory.fillDefaults().grab(true, false).applyTo(name);

        toolkit.createLabel(revisions, "Description: ");
        final DialogText description = new DialogText(revisions, toolkit) {

            @Override
            protected Object openDialogBox(Text text) {
                Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                MultilineTextDialog dialog = new MultilineTextDialog(shell, Strings.getAsString(text.getText()),
                        "Description");
                if (IStatus.OK == dialog.open()) {
                    text.setText(dialog.getValue());
                }
                return null;
            }
        };
        GridDataFactory.fillDefaults().grab(true, true).applyTo(description.getControl());

        toolkit.createLabel(revisions, "Reference: ");
        DialogText reference = new DialogText(revisions, toolkit) {

            @Override
            protected Object openDialogBox(Text text) {
                Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                MultilineTextDialog dialog = new MultilineTextDialog(shell, Strings.getAsString(text.getText()),
                        "Reference");
                if (IStatus.OK == dialog.open()) {
                    text.setText(dialog.getValue());
                }
                return null;
            }
        };
        GridDataFactory.fillDefaults().grab(true, true).applyTo(reference.getControl());
        if (null != module) {
            if (module.getRevisions().isEmpty()) {
                module.getRevisions().add(ModelFactory.eINSTANCE.createRevision());
            }
            Revision revision = module.getRevisions().get(0);
            Binding binding = bindingContext.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(200, name),
                    EMFProperties.value(YangModelUtil.MODEL_PACKAGE.getNamedNode_Name()).observe(revision));
            binding.updateModelToTarget();
            binding = bindingContext.bindValue(
                    WidgetProperties.text(SWT.Modify).observeDelayed(200, description.getTextControl()),
                    EMFProperties.value(YangModelUtil.MODEL_PACKAGE.getTag_Value()).observe(
                            YangModelUtil.getTag(YangTag.DESCRIPTION, revision)));
            binding.updateModelToTarget();
            binding = bindingContext.bindValue(
                    WidgetProperties.text(SWT.Modify).observeDelayed(200, reference.getTextControl()),
                    EMFProperties.value(YangModelUtil.MODEL_PACKAGE.getTag_Value()).observe(
                            YangModelUtil.getTag(YangTag.REFERENCE, revision)));
            binding.updateModelToTarget();
        }

        // end of Temporary code

        /*
         * GridLayoutFactory.swtDefaults().applyTo(revisions);
         * GridDataFactory.fillDefaults().grab(true, false).applyTo(section);
         * 
         * createRevisionTable(revisions); refreshRevisionTable();
         * revisionTable.addSelectionChangedListener(new ISelectionChangedListener() {
         * 
         * @Override public void selectionChanged(SelectionChangedEvent event) { if
         * (revisionTable.getSelection() instanceof IStructuredSelection) { Object selected =
         * ((IStructuredSelection) revisionTable.getSelection()).getFirstElement(); if (null !=
         * selected && selected instanceof Revision) { // editPropertyForm.setRevision((Revision)
         * selected); setPropertiesPaneVisible(true); } }
         * 
         * } });
         */
        section.setClient(revisions);
    }

    protected Composite createRevisionTable(Composite parent) {
        final Table t = toolkit.createTable(parent, SWT.FULL_SELECTION | SWT.V_SCROLL);

        GridDataFactory.fillDefaults().grab(true, false).applyTo(t);
        t.setLinesVisible(false);
        t.setHeaderVisible(false);
        revisionTable = new TableViewer(t);
        revisionTable.setContentProvider(new ArrayContentProvider());
        final TableViewerColumn col = new TableViewerColumn(revisionTable, SWT.NONE);

        col.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getRevision(), element)) {
                    return ((Revision) element).getName();
                }
                return super.getText(element);
            }
        });
        t.addControlListener(new ControlListener() {
            @Override
            public void controlResized(ControlEvent e) {
                col.getColumn().setWidth(t.getSize().x - 30);
            }

            @Override
            public void controlMoved(ControlEvent e) {
            }
        });
        col.getColumn().setWidth(t.getSize().x - 30);
        return t;
    }

    protected void createImportSection(Composite parent) {
        final Section section = createSection(parent, "Imports");
        Composite imports = toolkit.createComposite(section);
        GridLayoutFactory.swtDefaults().applyTo(imports);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(section);
        createImportTable(imports);
        createImportButtonToolbar(section);
        refreshImportTable();
        importTable.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                Object selected = ((IStructuredSelection) importTable.getSelection()).getFirstElement();
                if (null != selected && selected instanceof Import) {
                    // editPropertyForm.setImport((Import) selected);
                    setPropertiesPaneVisible(true);
                }

            }
        });
        section.setClient(imports);
    }

    protected Composite createImportTable(Composite parent) {
        final Table t = toolkit.createTable(parent, SWT.FULL_SELECTION | SWT.V_SCROLL);
        t.setLinesVisible(false);
        t.setHeaderVisible(false);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(t);
        importTable = new TableViewer(t);
        importTable.setContentProvider(new ArrayContentProvider());
        final TableViewerColumn col = new TableViewerColumn(importTable, SWT.NONE);
        col.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public Image getImage(Object element) {
                return GraphitiUi.getImageService().getImageForId(YangDiagramImageProvider.DIAGRAM_TYPE_PROVIDER_ID,
                        YangDiagramImageProvider.IMG_IMPORT_PROPOSAL);
            }

            @Override
            public String getText(Object element) {
                if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getImport(), element)) {
                    return ((Import) element).getPrefix() + " : " + ((Import) element).getModule();
                }
                return super.getText(element);
            }
        });
        t.addControlListener(new ControlListener() {
            @Override
            public void controlResized(ControlEvent e) {
                col.getColumn().setWidth(t.getSize().x - 30);
            }

            @Override
            public void controlMoved(ControlEvent e) {
            }
        });
        col.getColumn().setWidth(t.getSize().x - 30);
        return t;
    }

    protected void createImportButtonToolbar(Section section) {
        ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
        ToolBar toolbar = toolBarManager.createControl(section);
        toolbar.setCursor(Display.getDefault().getSystemCursor(SWT.CURSOR_HAND));

        Action addButton = new Action("Add new import", IAction.AS_CHECK_BOX) {
            @Override
            public void run() {
                super.run();
                Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                AddImportDialog dialog = new AddImportDialog(shell, module, file);
                if (0 <= dialog.open()) {
                    YangModelUtil.add(module, dialog.getResultImport(), module.getChildren().size());
                    refreshImportTable();
                }
                setChecked(false);
            }
        };

        addButton.setImageDescriptor(GraphitiUi.getImageService().getImageDescriptorForId(
                YangDiagramImageProvider.DIAGRAM_TYPE_PROVIDER_ID, YangDiagramImageProvider.IMG_ADD_TOOL_PROPOSAL));
        addButton.setEnabled(true);
        toolBarManager.add(addButton);

        Action deleteButton = new Action("Delete selected import", IAction.AS_CHECK_BOX) {

            @SuppressWarnings("unchecked")
            @Override
            public void run() {
                super.run();
                if (importTable.getSelection() instanceof IStructuredSelection) {
                    Iterator<Object> iter = ((IStructuredSelection) importTable.getSelection()).iterator();
                    while (iter.hasNext()) {
                        module.getChildren().remove(iter.next());
                    }
                    refreshImportTable();
                    setChecked(false);
                }
            }
        };

        deleteButton.setImageDescriptor(GraphitiUi.getImageService().getImageDescriptorForId(
                YangDiagramImageProvider.DIAGRAM_TYPE_PROVIDER_ID, YangDiagramImageProvider.IMG_DELETE_TOOL_PROPOSAL));
        deleteButton.setEnabled(true);
        toolBarManager.add(deleteButton);
        toolBarManager.update(true);
        section.setTextClient(toolbar);
    }

    public void refreshImportTable() {
        if (null != module) {
            importTable.setInput(YangModelUtil.filter(module.getChildren(), YangModelUtil.MODEL_PACKAGE.getImport()));
        }
    }

    protected void refreshRevisionTable() {
        if (null != module) {
            revisionTable.setInput(module.getRevisions());
        }
    }

    protected Section createSection(Composite parent, String title) {
        return createSection(parent, title, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
    }

    protected Section createSection(final Composite parent, String title, int styles) {
        Section section = toolkit.createSection(parent, styles);
        GridLayoutFactory.fillDefaults().applyTo(section);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(section);
        section.setText(title);
        section.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                parent.layout();
            }
        });
        return section;
    }

    public Composite getDiagram() {
        return diagram;
    }

    @Override
    public Module getBusinessObject() {
        return module;
    }

}
