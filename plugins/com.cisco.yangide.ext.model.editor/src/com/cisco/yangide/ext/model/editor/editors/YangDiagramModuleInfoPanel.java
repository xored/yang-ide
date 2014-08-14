package com.cisco.yangide.ext.model.editor.editors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.cisco.yangide.ext.model.ContainingNode;
import com.cisco.yangide.ext.model.Import;
import com.cisco.yangide.ext.model.Module;
import com.cisco.yangide.ext.model.NamedNode;
import com.cisco.yangide.ext.model.Node;
import com.cisco.yangide.ext.model.Revision;
import com.cisco.yangide.ext.model.RpcIO;
import com.cisco.yangide.ext.model.editor.dialog.AddImportDialog;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;
import com.cisco.yangide.ui.internal.IYangUIConstants;
import com.cisco.yangide.ui.internal.YangUIImages;

public class YangDiagramModuleInfoPanel extends Composite {
    
    private Text nameText;
    private TableViewer importTable;
    private TableViewer revisionTable;
    private Button addImport;
    private Button deleteImport;
    private Button editImport;
    private Module module;
    private FormToolkit toolkit;
    
    private class ElementTreeContentProvider implements ITreeContentProvider {

        @Override
        public void dispose() {     
        }
        
        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {      
        }

        @Override
        public Object[] getElements(Object inputElement) {
            if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getNode(), inputElement)) {
                return filterChildren((Node) inputElement).toArray();
            }
            return null;
        }
        
        public List<Node> filterChildren(Node parent) {
            List<Node> result = new ArrayList<Node>();
            if (null != parent && YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getContainingNode(), parent)) {
                for (Node n : ((ContainingNode) parent).getChildren()) {
                    if (!YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getImport(), n)) {
                        result.add(n);
                    }
                }
            }
            return result;
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            return getElements(parentElement);
        }

        @Override
        public Object getParent(Object element) {
            if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getNode(), element)) {
                return ((Node) element).getParent();
            }
            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
            if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getNode(), element)) {
                return !filterChildren((Node) element).isEmpty();
            }
            return false;
        }
        
    }
    
    private class ElementTreeStyledLabelProvider extends LabelProvider implements  IStyledLabelProvider{

        @Override
        public StyledString getStyledText(Object element) {
            if(YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getNode(), element)) {
                StyledString result = new StyledString(((Node) element).eClass().getName());
                if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getNamedNode(), element)) {
                    result = new StyledString(((NamedNode) element).getName());
                } else {
                    result = new StyledString(YangModelUtil.getQName((Node) element));
                }
                return result;
            }
            return null;
        }
        
        @Override
        public Image getImage(Object element) {
            if(YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getAugment(), element)) {
                return YangUIImages.getImage(IYangUIConstants.IMG_AUGMENT_PROPOSAL);
            } else if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getDeviation(), element)) {
                return YangUIImages.getImage(IYangUIConstants.IMG_DEVIATION_PROPOSAL);
            } else if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getIdentity(), element)) {
                return YangUIImages.getImage(IYangUIConstants.IMG_IDENTITY_PROPOSAL);
            } else if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getExtension(), element)) {
                return YangUIImages.getImage(IYangUIConstants.IMG_EXTENSION_PROPOSAL);
            } else if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getNotification(), element)) {
                return YangUIImages.getImage(IYangUIConstants.IMG_NOTIFICATION_PROPOSAL);
            } else if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getRpcIO(), element)) {
                if (((RpcIO) element).isInput()) {
                    return YangUIImages.getImage(IYangUIConstants.IMG_RPC_INPUT_PROPOSAL);
                } else {
                    return YangUIImages.getImage(IYangUIConstants.IMG_RPC_OUTPUT_PROPOSAL);
                }
            } else if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getRpc(), element)) {
                return YangUIImages.getImage(IYangUIConstants.IMG_RPC_PROPOSAL);
            } else if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getTypedef(), element)) {
                return YangUIImages.getImage(IYangUIConstants.IMG_CUSTOM_TYPE_PROPOSAL);
            } else if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getGrouping(), element)) {
                return YangUIImages.getImage(IYangUIConstants.IMG_GROUPING_PROPOSAL);
            } else if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getContainer(), element)) {
                return YangUIImages.getImage(IYangUIConstants.IMG_CONTAINER_PROPOSAL);   
            } else if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getLeaf(), element)) {
                return YangUIImages.getImage(IYangUIConstants.IMG_LEAF_PROPOSAL);
            } else if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getUses(), element)) {
                return YangUIImages.getImage(IYangUIConstants.IMG_USES_PROPOSAL);
            }
            
            return null;
        }       
        
    }

    public YangDiagramModuleInfoPanel(Composite parent, Module module) {
        super(parent, SWT.NONE);
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
        setLayout(layout);
        GridDataFactory.fillDefaults().hint(200, SWT.DEFAULT).applyTo(this);
        this.module = module;
        
        createModuleInfoPanel();
        
    }
    
    private void createModuleInfoPanel() {
        toolkit = new FormToolkit(getDisplay());
        ScrolledForm form = toolkit.createScrolledForm(this);
        form.setText("Module Info");
        toolkit.decorateFormHeading(form.getForm());
        form.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout(1, true);
        form.getBody().setLayout(layout);
        Dialog.applyDialogFont(form.getBody());
        createGeneralSection(form);
        createImportSection(form);
        createElementTreeSection(form);
        form.reflow(true);
       
        addListeners();
    }
    
    private void createGeneralSection(ScrolledForm form) {
        Section section = createSection(form, "General");
        Composite content = toolkit.createComposite(section);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(content);
        Composite header = toolkit.createComposite(content);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(header);
        toolkit.createLabel(header, "Name: ");  
        nameText = toolkit.createText(header, "");
        if (null != module) {
            nameText.setText(module.getName());
        }
        nameText.setEditable(true);
        GridDataFactory.fillDefaults().hint(120, 20).indent(-5, 0).applyTo(nameText);
        createRevisionPanel(content);
        section.setClient(content);

    }
    
    private void createRevisionPanel(Composite parent) {    
        toolkit.createLabel(parent, "Revisions:");
        Composite revisions = toolkit.createComposite(parent);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(revisions);
        Composite table = createRevisionTable(revisions);
        GridDataFactory.fillDefaults().hint(110, 50).applyTo(table);
        refreshRevisionTable();
        // TODO: buttonPane
    }
    
    private Composite createRevisionTable(Composite parent) {
        Table t = toolkit.createTable(parent, SWT.FULL_SELECTION | SWT.V_SCROLL);
        t.setLinesVisible(false);
        t.setHeaderVisible(false);
        TableLayout tableLayout = new TableLayout();
        tableLayout.addColumnData(new ColumnWeightData(1));
        t.setLayout(tableLayout);
        revisionTable = new TableViewer(t);
        revisionTable.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        revisionTable.setContentProvider(new ArrayContentProvider());
        TableViewerColumn col = new TableViewerColumn(revisionTable, SWT.NONE);
        col.setLabelProvider(new ColumnLabelProvider() {
            
            @Override
            public String getText(Object element) {
                if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getRevision(), element)) {
                    return ((Revision) element).getDate();
                }
                return super.getText(element);
            }

        });
        return t;
    }
    

    private void createImportSection(ScrolledForm form) {
        Section section = createSection(form, "Imports");      
        Composite imports = toolkit.createComposite(section);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(imports);
        Composite table = createImportTable(imports);
        GridDataFactory.fillDefaults().hint(110, 100).applyTo(table);
        Composite comp = createImportButtonPane(imports);
        GridDataFactory.fillDefaults().hint(40, 100).applyTo(comp);
        refreshImportTable();
        section.setClient(imports);
    }
    
    private void createElementTreeSection(ScrolledForm form) {
        Section section = createSection(form, "Content"); 
        section.setLayoutData(new GridData(GridData.FILL_BOTH));
        Composite content = toolkit.createComposite(section);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(content);
        content.setLayoutData(new GridData(GridData.FILL_BOTH));
        TreeViewer tree = new TreeViewer(content, SWT.FULL_SELECTION | SWT.BORDER);
        tree.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        tree.setLabelProvider(new DelegatingStyledCellLabelProvider(new ElementTreeStyledLabelProvider()));
        tree.setContentProvider(new ElementTreeContentProvider());
        tree.setInput(module);
        section.setClient(content);
    }        

    private Composite createImportTable(Composite parent) {
        Table t = toolkit.createTable(parent, SWT.FULL_SELECTION | SWT.V_SCROLL);
        t.setLinesVisible(false);
        t.setHeaderVisible(false);
        TableLayout tableLayout = new TableLayout();
        tableLayout.addColumnData(new ColumnWeightData(1));
        t.setLayout(tableLayout);
        importTable = new TableViewer(t);
        importTable.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        importTable.setContentProvider(new ArrayContentProvider());
        TableViewerColumn col = new TableViewerColumn(importTable, SWT.NONE);
        col.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public Image getImage(Object element) {
                return YangUIImages.getImage(IYangUIConstants.IMG_IMPORT_PROPOSAL);
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
    
    private Composite createImportButtonPane(Composite parent) {
        Composite comp = toolkit.createComposite(parent, SWT.NONE);
        comp.setFont(parent.getFont());
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(comp);
        addImport = toolkit.createButton(comp, "Add", SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(addImport);

        editImport = toolkit.createButton(comp, "Edit", SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(editImport);

        deleteImport = toolkit.createButton(comp, "Del", SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(deleteImport);
        return comp;
    }

    private void refreshRevisionTable() {
        if (null != module) {
            revisionTable.setInput(module.getRevisions());
        }
    }
    
    private void refreshImportTable() {
        if (null != module) {
            importTable.setInput(YangModelUtil.filter(module.getChildren(), YangModelUtil.MODEL_PACKAGE.getImport()));
        }
    }
    
    private Section createSection(final ScrolledForm form, String title) {
        Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
        section.setLayoutData(new GridData(GridData.FILL_BOTH));
        section.setText(title);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(section);
        section.addExpansionListener(new ExpansionAdapter() {
            public void expansionStateChanged(ExpansionEvent e) {
                form.reflow(false);
            }
        });
        return section;
    }

    @SuppressWarnings("unchecked")
    private void addListeners() {
        addImport.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                AddImportDialog dialog = new AddImportDialog(shell, module);
                if (0 <= dialog.open()) {
                    refreshImportTable();
                }
            }

        });

        deleteImport.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                if (importTable.getSelection() instanceof IStructuredSelection) {
                    Iterator<Object> iter = ((IStructuredSelection) importTable.getSelection()).iterator();
                    while (iter.hasNext()) {
                        module.getChildren().remove(iter.next());
                    }
                    refreshImportTable();
                }
            }
        });

        editImport.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                if (importTable.getSelection() instanceof IStructuredSelection) {
                    Iterator<Object> iter = ((IStructuredSelection) importTable.getSelection()).iterator();
                    Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                    if (iter.hasNext()) {
                       AddImportDialog dialog = new AddImportDialog(shell, module, (Import) iter.next());
                       if (0 <= dialog.open()) {
                           importTable.refresh();
                       }  
                    } else {
                        MessageDialog.openWarning(shell, "Warning", "No module was choosen");
                    }
                }
            }

        });
    }

}
