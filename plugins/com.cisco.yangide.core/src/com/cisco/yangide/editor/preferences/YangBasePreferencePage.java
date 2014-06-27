package com.cisco.yangide.editor.preferences;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class YangBasePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    @Override
    public void init(IWorkbench workbench) {
        this.noDefaultAndApplyButton();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */

    protected Control createContents(Composite parent) {
        Composite pageArea = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        pageArea.setLayout(layout);

        Label label = new Label(pageArea, SWT.NONE);
        label.setText(YangPreferencesMessages.YANGEditorPreferencePage_expandCategory);
        
        label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

        Dialog.applyDialogFont(pageArea);

        return pageArea;
    }


}
