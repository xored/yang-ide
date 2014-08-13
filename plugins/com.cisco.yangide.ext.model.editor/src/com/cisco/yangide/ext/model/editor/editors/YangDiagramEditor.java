package com.cisco.yangide.ext.model.editor.editors;

import java.util.Iterator;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.IDiagramEditorInput;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;

import com.cisco.yangide.ext.model.Import;
import com.cisco.yangide.ext.model.Module;
import com.cisco.yangide.ext.model.Node;
import com.cisco.yangide.ext.model.editor.dialog.AddImportDialog;
import com.cisco.yangide.ext.model.editor.util.DiagramImportSupport;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class YangDiagramEditor extends DiagramEditor {

    private Text nameText;
    private TableViewer importTable;
    private Button add;
    private Button delete;
    private Button edit;
    private Module module;    private IModelChangeHandler modelChangeHandler = new IModelChangeHandler() {

        @Override
        public void nodeRemoved(Node node) {
            System.out.println("Removed " + node);
        }

        @Override
        public void nodeChanged(Node node, EAttribute attribute, Object newValue) {
            System.out.println("Changed " + node);
        }

        @Override
        public void nodeAdded(Node parent, Node child, int position) {
            System.out.println("Added " + child);
        }
    };

    @Override
    protected DiagramBehavior createDiagramBehavior() {
        return new YangDiagramBehavior(this);
    }

    @Override
    public void createPartControl(Composite parent) {
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = false;
        layout.verticalSpacing = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        layout.marginHeight = 0;
        parent.setLayout(layout);     
        
        Composite content = createModuleInfoPanel(parent);        
 
        super.createPartControl(parent);

        for (Control c : parent.getChildren()) {
            if (!content.equals(c)) {
                c.setLayoutData(new GridData(GridData.FILL_BOTH));
            }
        }
        addListeners();
    }
    
    private Composite createModuleInfoPanel(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(content);
        GridDataFactory.fillDefaults().hint(200, 20).applyTo(content);
        
        Composite header = new Composite(content, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(header);
        GridDataFactory.fillDefaults().hint(200, 20).align(SWT.FILL, SWT.FILL).indent(0, 5).applyTo(header);
        CLabel nameLabel = new CLabel(header, SWT.NONE);
        nameLabel.setText("Name: ");
        GridDataFactory.fillDefaults().hint(40, 20).applyTo(nameLabel);
        nameText = new Text(header, SWT.NONE);
        nameText.setEditable(true);
        if (null != module) {
            nameText.setText(module.getName());
        }
        GridDataFactory.fillDefaults().hint(160, 20).applyTo(nameText);
        
        createImportPane(content);
        return content;
        
    }
    
    private Composite createImportPane(Composite parent) {
        CLabel importLabel = new CLabel(parent, SWT.NONE);
        importLabel.setText("Imports:");
        GridDataFactory.fillDefaults().span(1, 2).align(SWT.FILL, SWT.END).applyTo(importLabel);
        
        Composite imports = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(imports);
        Composite table = createImportTable(imports);
        GridDataFactory.fillDefaults().hint(140, 150).applyTo(table);
        Composite comp = createImportButtonPane(imports);
        GridDataFactory.fillDefaults().hint(60, 150).applyTo(comp);
        refreshImportTable();
        return imports;
    }
    
    private Composite createImportTable(Composite parent) {
        Table t = new Table(parent, SWT.FULL_SELECTION | SWT.V_SCROLL);
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
        Composite comp = new Composite(parent, SWT.NONE);
        comp.setFont(parent.getFont());
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(comp);
        add = new Button(comp, SWT.NONE);
        add.setText("Add");
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(add);
        
        edit = new Button(comp, SWT.NONE);
        edit.setText("Edit");
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(edit);
        
        delete = new Button(comp, SWT.NONE);
        delete.setText("Del");      
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(delete);
        return comp;
    }
    
    private void refreshImportTable() {
        if (null != module) {
            importTable.setInput(YangModelUtil.filter(module.getChildren(), YangModelUtil.MODEL_PACKAGE.getImport()));
        }
    }
    
    @SuppressWarnings("unchecked")
    private void addListeners() {
        add.addSelectionListener(new SelectionAdapter() {

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

        delete.addSelectionListener(new SelectionAdapter() {
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
        
        edit.addSelectionListener(new SelectionAdapter() {

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
    
    @Override
    protected void setInput(IEditorInput input) {
        super.setInput(input);
        final URI uri = ((IDiagramEditorInput) input).getUri();
        module = ((YangDiagramEditorInput) input).getModule();
        getEditingDomain().getCommandStack().execute(new RecordingCommand(getEditingDomain()) {

            @Override
            protected void doExecute() {
                ensureDiagramResource(uri);
                importDiagram();
            }
        });
    }

    private void ensureDiagramResource(URI uri) {
        final Resource resource = getEditingDomain().getResourceSet().createResource(uri);
        getEditingDomain().getCommandStack().execute(new RecordingCommand(getEditingDomain()) {
            @Override
            protected void doExecute() {
                resource.setTrackingModification(true);
                resource.getContents().add(getDiagramTypeProvider().getDiagram());
            }
        });
    }

    private void importDiagram() {
        if (null != module) {
            final Diagram diagram = getDiagramTypeProvider().getDiagram();
            getDiagramTypeProvider().getFeatureProvider().link(diagram, module);
            DiagramImportSupport.importDiagram(diagram, getDiagramTypeProvider().getFeatureProvider());
        }

    }

    /**
     * @return
     */
    public IModelChangeHandler getModelChangeHandler() {
        return modelChangeHandler;
    }
}
