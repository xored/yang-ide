package com.cisco.yangide.ext.model.editor.property;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.cisco.yangide.ext.model.editor.dialog.MultilineTextDialog;
import com.cisco.yangide.ext.model.editor.util.Strings;
import com.cisco.yangide.ext.model.editor.util.YangTag;

public class DialogTextPropertyDescriptor extends PropertyDescriptor {
    
    private YangTag id;

    public DialogTextPropertyDescriptor(Object id, String displayName) {
        super(id, displayName);
        this.id = (YangTag) id;
    }
    
    public DialogCellEditor createPropertyEditor(Composite parent) {
        DialogCellEditor editor = new DialogCellEditor(parent) {
            
            @Override
            protected Object openDialogBox(Control cellEditorWindow) {
                Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                MultilineTextDialog dialog = new MultilineTextDialog(shell, Strings.getAsString(getValue()), id.getName());
                if (IStatus.OK == dialog.open()) {
                    setValue(dialog.getValue());
                }
                return null;
            }                      
            
        };
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

}
