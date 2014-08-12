package com.cisco.yangide.ext.model.editor.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
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
import com.cisco.yangide.ext.model.Import;
import com.cisco.yangide.ext.model.ModelFactory;
import com.cisco.yangide.ext.model.Module;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class AddImportDialog extends ElementListSelectionDialog {
    
    private Text prefix;
    private Module module;
    private Import obj;
    private ElementIndexInfo[] list; 
    
    public AddImportDialog(Shell parent, Module module) {
        super(parent, new LabelProvider() {
            public String getText(Object element) {
                if (element instanceof ElementIndexInfo) {
                    return ((ElementIndexInfo) element).getModule() + " {" + ((ElementIndexInfo) element).getRevision() + "}";
                }
                return null;
            }
        });
        this.module = module;
        list = YangModelManager.search(null, null, null, ElementIndexType.MODULE, null, null);
        setElements(list);
        setTitle("Select imported module");
    }
    
    public AddImportDialog(Shell parent, Module node, Import obj) {
        this(parent, node);
        this.obj = obj;        
    }
    
    private ElementIndexInfo find(Import node) {
        for (ElementIndexInfo info : list) {
            if (info.getModule().equals(obj.getModule().getName()) && info.getRevision().equals(obj.getRevisionDate())) {
                return info;
            }
        }
        return null;
    }
    
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
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
        prefix = new Text(appendix, SWT.NONE);
        if (null != obj) {
            prefix.setText(obj.getPrefix());
        }
        prefix.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ElementIndexInfo info = find(obj);
        if (null != info) {
            setSelection(new Object[] {info});
        }
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
    
    public void setResultObject() {
        ElementIndexInfo choosen = (ElementIndexInfo) getFirstResult();
        if (null == obj){            
            Import result = ModelFactory.eINSTANCE.createImport();
            result.setPrefix(prefix.getText());
            result.setRevisionDate(choosen.getRevision());
            Module imported = ModelFactory.eINSTANCE.createModule();
            imported.setName(choosen.getModule());
            result.setModule(imported);
            YangModelUtil.add(module, result);
        } else {
            obj.setPrefix(prefix.getText());
            obj.setRevisionDate(choosen.getRevision());
            obj.getModule().setName(choosen.getModule());
        }
        
    }
}
