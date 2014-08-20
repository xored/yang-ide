package com.cisco.yangide.ext.model.editor.editors;

import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Scrollable;
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
import com.cisco.yangide.ext.model.Module;
import com.cisco.yangide.ext.model.Revision;
import com.cisco.yangide.ext.model.TaggedNode;
import com.cisco.yangide.ext.model.editor.dialog.AddImportDialog;
import com.cisco.yangide.ext.model.editor.util.BusinessObjectWrapper;
import com.cisco.yangide.ext.model.editor.util.Strings;
import com.cisco.yangide.ext.model.editor.util.YangDiagramImageProvider;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;
import com.cisco.yangide.ext.model.editor.util.YangTag;



public class YangDiagramModuleInfoPanel implements BusinessObjectWrapper<Module> {
     
    private Text nameText;
    private TableViewer importTable;

    private Module module;
    private FormToolkit toolkit;
  
    private Composite diagram;
    private PropertyEdit editPropertyForm;
    
    private Text namespaceText;
    private Text organizationText;
    private Text contactText;
    private Text prefixText;
    private Text yangVersionText;
    private Text descriptionText;
    private Text referenceText;
    private TableViewer revisionTable;
    private SashForm infoPane;
    private ScrolledForm pane;
    private Composite leftPane;
    
    private boolean propertiesPaneIsVisible = false;
    
    private static final int H_OFFSET = 2;
    private static final int TEXT_AREA_HEIGHT = 80;
    
    protected class FillData {

        int defaultWidth = -1, defaultHeight = -1;
        int currentWhint, currentHhint, currentWidth = -1, currentHeight = -1;

        public Point computeSize(Control control, int wHint, int hHint, boolean flushCache) {
            if (flushCache)
                flushCache();
            if (wHint == SWT.DEFAULT && hHint == SWT.DEFAULT) {
                if (defaultWidth == -1 || defaultHeight == -1) {
                    Point size = control.computeSize(wHint, hHint, flushCache);
                    defaultWidth = size.x;
                    defaultHeight = size.y;
                }
                return new Point(defaultWidth, defaultHeight);
            }
            if (currentWidth == -1 || currentHeight == -1 || wHint != currentWhint || hHint != currentHhint) {
                Point size = control.computeSize(wHint, hHint, flushCache);
                currentWhint = wHint;
                currentHhint = hHint;
                currentWidth = size.x;
                currentHeight = size.y;
            }
            return new Point(currentWidth, currentHeight);
        }

        public void flushCache() {
            defaultWidth = defaultHeight = -1;
            currentWidth = currentHeight = -1;
        }
    }
    
    protected class YangPanelFillLayout extends Layout {

        public int type = SWT.VERTICAL;

        public int marginWidth = 3;

        public int marginHeight = 3;

        public int spacing = 2;

        public YangPanelFillLayout() {
        }

        protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
            int width = composite.getClientArea().width - 2 * marginWidth;
            int height = composite.getClientArea().height - 2 * marginHeight;
            return new Point(width, height);
        }

        protected Point computeChildSize(Control control, int wHint, int hHint, boolean flushCache) {
            FillData data = (FillData) control.getLayoutData();
            if (data == null) {
                data = new FillData();
                control.setLayoutData(data);
            }
            Point size = null;
            int trimX, trimY;
            if (control instanceof Scrollable) {
                Rectangle rect = ((Scrollable) control).computeTrim(0, 0, 0, 0);
                trimX = rect.width;
                trimY = rect.height;
            } else {
                trimX = trimY = control.getBorderWidth() * 2;
            }
            int w = wHint == SWT.DEFAULT ? wHint : Math.max(0, wHint - trimX);
            int h = hHint == SWT.DEFAULT ? hHint : Math.max(0, hHint - trimY);
            size = data.computeSize(control, w, h, flushCache);
            return size;
        }

        protected boolean flushCache(Control control) {
            Object data = control.getLayoutData();
            if (data != null)
                ((FillData) data).flushCache();
            return true;
        }

        protected void layout(Composite composite, boolean flushCache) {
            Rectangle rect = composite.getClientArea();
            Control[] children = composite.getChildren();
            int count = children.length;
            if (count == 0)
                return;
            int width = rect.width - marginWidth * 2;
            int height = rect.height - marginHeight * 2;
            height -= (count - 1) * spacing;

            int x = rect.x + marginWidth;
            int y = rect.y + marginHeight, extra = height % count;
            for (int i = 0; i < count; i++) {
                Control child = children[i];
                Point size = computeChildSize(child, -1, -1, true);
                int childHeight = size.y;
                if (i == 0) {
                    childHeight += extra / 2;
                } else {
                    if (i == count - 1)
                        childHeight += (extra + 1) / 2;
                }
                child.setBounds(x, y, width, childHeight);
                y += childHeight + spacing;
            }
        }
    }
        
    protected class PropertyEdit extends SashForm {
        private RevisionEdit editRevision;
        private ImportEdit editImport;
        
        protected class RevisionEdit implements BusinessObjectWrapper<Revision>{
            private Text description;
            private Text reference;
            private Text name;
            private Revision revision;
            private Composite pane;
            public RevisionEdit() {
                pane = toolkit.createComposite(editPropertyForm, SWT.NONE);
                createPane();
                updateData();     
                addListeneres();
            }
            protected void createPane() {
                pane.setLayout(new FormLayout());
                Label nameLabel = toolkit.createLabel(pane, "Name: ");  
                name  = toolkit.createText(pane, "");
                name.setEditable(true);                
                
                Label descriptionLabel = toolkit.createLabel(pane, "Description: ");  
                description  = toolkit.createText(pane, "", SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
                description.setEditable(true);                
                
                Label referenceLabel = toolkit.createLabel(pane, "Reference: ");  
                reference  = toolkit.createText(pane, "", SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
                reference.setEditable(true);
                
                FormData data = new FormData ();
                data.left = new FormAttachment (0, 0);
                nameLabel.setLayoutData (data);
                
                data = new FormData ();
                data.left = new FormAttachment (descriptionLabel, 0);
                data.right = new FormAttachment (100, 0);
                name.setLayoutData (data);
                
                data = new FormData ();
                data.left = new FormAttachment (0, 0);
                data.top = new FormAttachment(name, H_OFFSET);
                descriptionLabel.setLayoutData (data);
                
                data = new FormData ();
                data.left = new FormAttachment (descriptionLabel, 0);
                data.right = new FormAttachment (100, 0);
                data.top = new FormAttachment(name, H_OFFSET);
                data.bottom = new FormAttachment(description, TEXT_AREA_HEIGHT);
                description.setLayoutData (data);
                
                data = new FormData ();
                data.left = new FormAttachment (0, 0);
                data.top = new FormAttachment(description, H_OFFSET);
                referenceLabel.setLayoutData (data);
                
                data = new FormData ();
                data.left = new FormAttachment (descriptionLabel, 0);
                data.right = new FormAttachment (100, 0);
                data.top = new FormAttachment(description, H_OFFSET);
                data.bottom = new FormAttachment(reference, TEXT_AREA_HEIGHT);
                reference.setLayoutData (data);
                
                
            }
            protected void addListeneres() {
                addTextFieldListener(this, reference, YangTag.REFERENCE);
                addTextFieldListener(this, description, YangTag.DESCRIPTION);
                addTextFieldListener(this, name, YangModelUtil.MODEL_PACKAGE.getNamedNode_Name());
            }
            public void updateData() {
                if (null != revision) {
                    name.setText(revision.getName());
                    description.setText(Strings.getAsString(YangModelUtil.getValue(YangTag.DESCRIPTION, revision)));
                    reference.setText(Strings.getAsString(YangModelUtil.getValue(YangTag.REFERENCE, revision)));
                }
            }
            public Composite getPane(Revision revision) {
                this.revision = revision;
                updateData();
                return pane;
            }
            @Override
            public Revision getBusinessObject() {
                return revision;
            } 
            
        }
        
        protected class ImportEdit implements BusinessObjectWrapper<Import>{
            private Text name;
            private Text prefix;
            private Text revision;
            private Import importObj;
            private Composite pane;
            public ImportEdit() {
                pane = toolkit.createComposite(editPropertyForm, SWT.NONE);
                createPane();
                updateData();     
                addListeneres();
            }
            protected void createPane() {
                GridLayoutFactory.fillDefaults().numColumns(2).applyTo(pane);
                toolkit.createLabel(pane, "Name: ");  
                name  = toolkit.createText(pane, "");
                name.setEditable(false);
                name.setEnabled(false);
                GridDataFactory.fillDefaults().grab(true, false).applyTo(name);
                
                toolkit.createLabel(pane, "Prefix: ");  
                prefix  = toolkit.createText(pane, "");
                prefix.setEditable(true);
                GridDataFactory.fillDefaults().grab(true, false).applyTo(prefix);
                
                toolkit.createLabel(pane, "Revision: ");  
                revision  = toolkit.createText(pane, "");
                revision.setEditable(false);
                revision.setEnabled(false);
                GridDataFactory.fillDefaults().grab(true, false).applyTo(revision);
                
            }
            protected void addListeneres() {
                addTextFieldListener(this, prefix, YangModelUtil.MODEL_PACKAGE.getImport_Prefix());
                addTextFieldListener(this, revision, YangModelUtil.MODEL_PACKAGE.getImport_RevisionDate());
            }
            public void updateData() {
                if (null != importObj) {
                    name.setText(importObj.getModule().getName());
                    prefix.setText(Strings.getAsString(importObj.getPrefix()));
                    revision.setText(Strings.getAsString(importObj.getRevisionDate()));
                }
            }
            
            public Composite getPane(Import importObj) {
                this.importObj = importObj;
                updateData();
                return pane;
            }
            @Override
            public Import getBusinessObject() {
                return importObj;
            } 
            
        }

        public PropertyEdit(Composite parent) {
            super(parent, SWT.HORIZONTAL);
            setLayoutData(new GridData(GridData.FILL_BOTH));
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
    
    public YangDiagramModuleInfoPanel(Composite parent, Module module) {       
        this.module = module;
        SashForm form = new SashForm(parent, SWT.HORIZONTAL);
        form.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        form.setLayout(new GridLayout(1, false));
        
        leftPane = new Composite(form, SWT.NONE);

        leftPane.setLayout(new FillLayout(SWT.VERTICAL));
        diagram = new Composite(form, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        diagram.setLayout(layout);        
        createModuleInfoPanel(leftPane);     
        form.setWeights(new int[] {2, 6});
    }    
    
    protected void createModuleInfoPanel(Composite parent) {
        toolkit = new FormToolkit(parent.getDisplay());
        toolkit.adapt(parent);
        ScrolledForm mainForm = toolkit.createScrolledForm(parent);
        mainForm.setText("Module Info");
        toolkit.decorateFormHeading(mainForm.getForm());
       
        mainForm.getBody().setLayout(new FillLayout(SWT.VERTICAL));
        infoPane = new SashForm(mainForm.getBody(), SWT.HORIZONTAL);
        toolkit.adapt(infoPane);

        infoPane.setLayout(new FillLayout(SWT.VERTICAL));
        pane = toolkit.createScrolledForm(infoPane);
        
        pane.getBody().setLayout(new YangPanelFillLayout());
        
        ScrolledForm editForm = toolkit.createScrolledForm(infoPane);
        
        editForm.getBody().setLayout(new YangPanelFillLayout());
        Section editSection = createSection(editForm, "Properties", Section.TITLE_BAR | Section.EXPANDED);
        editPropertyForm = new PropertyEdit(editSection);
        toolkit.adapt(editPropertyForm);
        editSection.setClient(editPropertyForm);
        Dialog.applyDialogFont(mainForm.getBody());
        createGeneralSection(pane);
        createRevisionSection(pane);
        createImportSection(pane);
        infoPane.setWeights(new int[] {1, 0});
        infoPane.setMaximizedControl(pane);
        createPropertiesButtonToolbar(editSection, infoPane, pane);
        mainForm.reflow(true);
    }
    
    protected void setPropertiesPaneVisible(boolean set) {
        Point size = leftPane.getSize();
        if (set) {
            infoPane.setWeights(new int[] {1, 1});
            infoPane.setMaximizedControl(null);
            if (!propertiesPaneIsVisible) {
                size.x = 2 * size.x;
            }
            
        } else {
            infoPane.setWeights(new int[] {1, 0});
            infoPane.setMaximizedControl(pane);   
            if (propertiesPaneIsVisible) {
                size.x = size.x / 2;
            }
        }
        propertiesPaneIsVisible = set;
        leftPane.setSize(size);
    }
    
    protected void createPropertiesButtonToolbar(final Section editSection, final SashForm infoPane, final ScrolledForm pane) {
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

        closeButton.setImageDescriptor(GraphitiUi.getImageService().getImageDescriptorForId(YangDiagramImageProvider.DIAGRAM_TYPE_PROVIDER_ID, YangDiagramImageProvider.IMG_COLLAPSE_ALL_TOOL_PROPOSAL));
        closeButton.setEnabled(true);
        toolBarManager.add(closeButton);
        toolBarManager.update(true);
        editSection.setTextClient(toolbar);
    }
    
    protected void createGeneralSection(ScrolledForm form) {
        Section section = createSection(form, "General");
        Composite header = toolkit.createComposite(section);

        header.setLayout(new FormLayout());
        Label name = toolkit.createLabel(header, "Name: ");  
        
        nameText = toolkit.createText(header, "");
        nameText.setEditable(true);        
        
        Label yangVersion = toolkit.createLabel(header, "Yang-version: ");  
        
        yangVersionText  = toolkit.createText(header, "");
        yangVersionText.setEditable(true);
        
        Label namespace = toolkit.createLabel(header, "Namespace: ");  
        namespaceText = toolkit.createText(header, "");
        namespaceText.setEditable(true);
        
        Label prefix = toolkit.createLabel(header, "Prefix: ");  
        prefixText  = toolkit.createText(header, "");
        prefixText.setEditable(true);
        
        Label organization = toolkit.createLabel(header, "Organization: ");  
        organizationText = toolkit.createText(header, "", SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        organizationText.setEditable(true);
        
        Label contact = toolkit.createLabel(header, "Contact: ");  
        contactText  = toolkit.createText(header, "",  SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        contactText.setEditable(true);

        Label description = toolkit.createLabel(header, "Description: ");  
        descriptionText  = toolkit.createText(header, "",  SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        descriptionText.setEditable(true);
        
        Label reference = toolkit.createLabel(header, "Reference: ");  
        referenceText  = toolkit.createText(header, "",  SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        referenceText.setEditable(true);
        
        // layout elements
        FormData data = new FormData ();
        data.left = new FormAttachment (0, 0);
        name.setLayoutData (data);
        
        data = new FormData ();
        data.left = new FormAttachment (yangVersion, 0);
        data.right = new FormAttachment (100, 0);
        nameText.setLayoutData (data);
        
        data = new FormData ();
        data.left = new FormAttachment (0, 0);
        data.top = new FormAttachment(nameText, H_OFFSET);
        yangVersion.setLayoutData (data);
        
        data = new FormData ();
        data.left = new FormAttachment (yangVersion, 0);
        data.right = new FormAttachment (100, 0);
        data.top = new FormAttachment(nameText, H_OFFSET);
        yangVersionText.setLayoutData (data);
        
        data = new FormData ();
        data.left = new FormAttachment (0, 0);
        data.top = new FormAttachment(yangVersionText, H_OFFSET);
        namespace.setLayoutData (data);
        
        data = new FormData ();
        data.left = new FormAttachment (yangVersion, 0);
        data.right = new FormAttachment (100, 0);
        data.top = new FormAttachment(yangVersionText, H_OFFSET);
        namespaceText.setLayoutData (data);
        
        data = new FormData ();
        data.left = new FormAttachment (0, 0);
        data.top = new FormAttachment(namespaceText, H_OFFSET);
        prefix.setLayoutData (data);
        
        data = new FormData ();
        data.left = new FormAttachment (yangVersion, 0);
        data.right = new FormAttachment (100, 0);
        data.top = new FormAttachment(namespaceText, H_OFFSET);
        prefixText.setLayoutData (data);
        
        data = new FormData ();
        data.left = new FormAttachment (0, 0);
        data.top = new FormAttachment(prefixText, H_OFFSET);
        organization.setLayoutData (data);
        
        data = new FormData ();
        data.left = new FormAttachment (yangVersion, 0);
        data.right = new FormAttachment (100, 0);
        data.top = new FormAttachment(prefixText, H_OFFSET);
        data.bottom = new FormAttachment(organizationText, TEXT_AREA_HEIGHT);
        organizationText.setLayoutData (data);
        
        data = new FormData ();
        data.left = new FormAttachment (0, 0);
        data.top = new FormAttachment(organizationText, H_OFFSET);
        contact.setLayoutData (data);
        
        data = new FormData ();
        data.left = new FormAttachment (yangVersion, 0);
        data.right = new FormAttachment (100, 0);
        data.top = new FormAttachment(organizationText, H_OFFSET);
        data.bottom = new FormAttachment(contactText, TEXT_AREA_HEIGHT);
        contactText.setLayoutData (data);
        
        data = new FormData ();
        data.left = new FormAttachment (0, 0);
        data.top = new FormAttachment(contactText, H_OFFSET);
        description.setLayoutData (data);
        
        data = new FormData ();
        data.left = new FormAttachment (yangVersion, 0);
        data.right = new FormAttachment (100, 0);
        data.top = new FormAttachment(contactText, H_OFFSET);
        data.bottom = new FormAttachment(descriptionText, TEXT_AREA_HEIGHT);
        descriptionText.setLayoutData (data);
        
        data = new FormData ();
        data.left = new FormAttachment (0, 0);
        data.top = new FormAttachment(descriptionText, H_OFFSET);
        reference.setLayoutData (data);
        
        data = new FormData ();
        data.left = new FormAttachment (yangVersion, 0);
        data.right = new FormAttachment (100, 0);
        data.top = new FormAttachment(descriptionText, H_OFFSET);
        data.bottom = new FormAttachment(referenceText, TEXT_AREA_HEIGHT);
        referenceText.setLayoutData (data);
        
        updateGeneralSection();
        addGeneralSectionListeners();
        section.setClient(header);
    }    
    
    protected void updateGeneralSection() {
        if (null != module) {
            nameText.setText(module.getName());
            yangVersionText.setText(Strings.getAsString(YangModelUtil.getValue(YangTag.YANG_VERSION, module)));
            namespaceText.setText(Strings.getAsString(YangModelUtil.getValue(YangTag.NAMESPACE, module)));
            prefixText.setText(Strings.getAsString(YangModelUtil.getValue(YangTag.PREFIX, module)));
            organizationText.setText(Strings.getAsString(YangModelUtil.getValue(YangTag.ORGANIZATION, module)));
            contactText.setText(Strings.getAsString(YangModelUtil.getValue(YangTag.CONTACT, module)));
            descriptionText.setText(Strings.getAsString(YangModelUtil.getValue(YangTag.DESCRIPTION, module)));
            referenceText.setText(Strings.getAsString(YangModelUtil.getValue(YangTag.REFERENCE, module)));
        }
    }
    
    protected void addGeneralSectionListeners() {
        addTextFieldListener(this, nameText, YangModelUtil.MODEL_PACKAGE.getNamedNode_Name());
        addTextFieldListener(this, yangVersionText, YangTag.YANG_VERSION);
        addTextFieldListener(this, namespaceText, YangTag.NAMESPACE);
        addTextFieldListener(this, prefixText, YangTag.PREFIX);
        addTextFieldListener(this, organizationText, YangTag.ORGANIZATION);
        addTextFieldListener(this, contactText, YangTag.CONTACT);
        addTextFieldListener(this, descriptionText, YangTag.DESCRIPTION);
        addTextFieldListener(this, referenceText, YangTag.REFERENCE);
    }
    
    protected void addTextFieldListener(final BusinessObjectWrapper<? extends TaggedNode> node, Text text, final YangTag tag) {
        text.addModifyListener(new ModifyListener() {
            
            @Override
            public void modifyText(ModifyEvent e) {
                YangModelUtil.setValue(tag, node.getBusinessObject(), ((Text) e.widget).getText());
            }
        });
    }
    
    protected void addTextFieldListener(final BusinessObjectWrapper<? extends EObject> node, Text text, final EStructuralFeature esf) {
        text.addModifyListener(new ModifyListener() {
            
            @Override
            public void modifyText(ModifyEvent e) {
                node.getBusinessObject().eSet(esf, ((Text) e.widget).getText());
            }
        });
    }
    
    protected void createRevisionSection(final ScrolledForm form) {
        Section section = createSection(form, "Revision");      
        Composite revisions = toolkit.createComposite(section);
        revisions.setLayout(new FillLayout(SWT.VERTICAL));
        
        createRevisionTable(revisions);
        refreshRevisionTable();
        revisionTable.addSelectionChangedListener(new ISelectionChangedListener() {
            
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (revisionTable.getSelection() instanceof IStructuredSelection) {
                    Object selected = ((IStructuredSelection) revisionTable.getSelection()).getFirstElement();
                    if (null != selected && selected instanceof Revision) {
                        editPropertyForm.setRevision((Revision) selected);
                        setPropertiesPaneVisible(true);
                    }
                }
                
            }
        });
        section.setClient(revisions);
    }
    
    protected Composite createRevisionTable(Composite parent) {
        Table t = toolkit.createTable(parent, SWT.FULL_SELECTION | SWT.V_SCROLL);
        t.setLinesVisible(false);
        t.setHeaderVisible(false);
        TableLayout tableLayout = new TableLayout();
        tableLayout.addColumnData(new ColumnWeightData(1));
        t.setLayout(tableLayout);
        revisionTable = new TableViewer(t);
        revisionTable.setContentProvider(new ArrayContentProvider());
        TableViewerColumn col = new TableViewerColumn(revisionTable, SWT.NONE);
        col.setLabelProvider(new ColumnLabelProvider() {
            
            @Override
            public String getText(Object element) {
                if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getRevision(), element)) {
                    return ((Revision) element).getName();
                }
                return super.getText(element);
            }

        });
        return t;
    }
    
    protected void createImportSection(final ScrolledForm form) {
        final Section section = createSection(form, "Imports"); 
        Composite imports = toolkit.createComposite(section);
        imports.setLayout(new FillLayout(SWT.VERTICAL));
        createImportTable(imports);
        createImportButtonToolbar(section);
        refreshImportTable();
        importTable.addSelectionChangedListener(new ISelectionChangedListener() {
            
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                Object selected = ((IStructuredSelection) importTable.getSelection()).getFirstElement();
                if (null != selected && selected instanceof Import) {
                    editPropertyForm.setImport((Import) selected);
                    setPropertiesPaneVisible(true);               
                }
                
            }
        });
        section.setClient(imports);
    }
       

    protected Composite createImportTable(Composite parent) {
        Table t = toolkit.createTable(parent, SWT.FULL_SELECTION | SWT.V_SCROLL);
        t.setLinesVisible(false);
        t.setHeaderVisible(false);
        TableLayout tableLayout = new TableLayout();
        tableLayout.addColumnData(new ColumnWeightData(1));
        t.setLayout(tableLayout);
        importTable = new TableViewer(t);
        importTable.setContentProvider(new ArrayContentProvider());
        TableViewerColumn col = new TableViewerColumn(importTable, SWT.NONE);
        col.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public Image getImage(Object element) {
                return GraphitiUi.getImageService().getImageForId(YangDiagramImageProvider.DIAGRAM_TYPE_PROVIDER_ID, YangDiagramImageProvider.IMG_IMPORT_PROPOSAL);
            }

            @Override
            public String getText(Object element) {
                if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getImport(), element)) {
                    return ((Import) element).getPrefix() + " : " + ((Import) element).getModule().getName();
                }
                return super.getText(element);
            }

        });
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
                AddImportDialog dialog = new AddImportDialog(shell, module);
                if (0 <= dialog.open()) {
                    refreshImportTable();
                }
                setChecked(false);
            }
        };

        addButton.setImageDescriptor(GraphitiUi.getImageService().getImageDescriptorForId(YangDiagramImageProvider.DIAGRAM_TYPE_PROVIDER_ID, YangDiagramImageProvider.IMG_ADD_TOOL_PROPOSAL));
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

        deleteButton.setImageDescriptor(GraphitiUi.getImageService().getImageDescriptorForId(YangDiagramImageProvider.DIAGRAM_TYPE_PROVIDER_ID, YangDiagramImageProvider.IMG_DELETE_TOOL_PROPOSAL));
        deleteButton.setEnabled(true);
        toolBarManager.add(deleteButton);
        toolBarManager.update(true);
        section.setTextClient(toolbar);
    }
    
    protected void refreshImportTable() {
        if (null != module) {
            importTable.setInput(YangModelUtil.filter(module.getChildren(), YangModelUtil.MODEL_PACKAGE.getImport()));
        }
    }
    
    protected void refreshRevisionTable() {
        if (null != module) {
            revisionTable.setInput(module.getRevisions());
        }
    }
    
    protected Section createSection(ScrolledForm form, String title) {
        return createSection(form, title, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
    }
    
    protected Section createSection(final ScrolledForm form, String title, int styles) {
        Section section = toolkit.createSection(form.getBody(), styles);
        section.setLayout(new FillLayout(SWT.VERTICAL));
        section.setText(title);
        section.addExpansionListener(new ExpansionAdapter() {
            public void expansionStateChanged(ExpansionEvent e) {
                form.reflow(false);
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
