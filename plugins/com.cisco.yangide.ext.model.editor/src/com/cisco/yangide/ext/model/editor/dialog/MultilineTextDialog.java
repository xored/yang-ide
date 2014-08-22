package com.cisco.yangide.ext.model.editor.dialog;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class MultilineTextDialog extends Dialog {

    private static final int SPACING = 10;

    private FormToolkit toolkit;
    private Composite composite;
    private String originalValue;
    private String value;
    private String title;
    private Text textControl;

    public MultilineTextDialog(Shell parentShell, final String originalValue, String title) {
      super(parentShell);
      this.originalValue = originalValue;
      this.title = title;
      this.toolkit = new FormToolkit(parentShell.getDisplay());
    }

    @Override
    protected void configureShell(Shell shell) {
      super.configureShell(shell);
      shell.setText(title);
    }

    @Override
    protected Control createDialogArea(Composite parent) {

      this.composite = (Composite) super.createDialogArea(parent);
      composite.setBackground(ColorConstants.white);

      final FormLayout formLayout = new FormLayout();
      composite.setLayout(formLayout);

      FormData data;

      final Label instructionLabel = toolkit.createLabel(composite, "Specify a value for the " + title);
      data = new FormData();
      data.top = new FormAttachment(composite, SPACING);
      data.left = new FormAttachment(composite, SPACING);
      data.right = new FormAttachment(100, -SPACING);
      instructionLabel.setLayoutData(data);

      Control previousAnchor = instructionLabel;

      textControl = toolkit.createText(composite, originalValue, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER_SOLID);
      textControl.setEnabled(true);

      data = new FormData();
      data.top = new FormAttachment(previousAnchor, SPACING);
      data.left = new FormAttachment(composite, SPACING);
      data.right = new FormAttachment(100, -SPACING);
      data.height = 120;
      textControl.setLayoutData(data);

      return composite;
    }
    @Override
    protected void okPressed() {
      // store the value from the spinners so it can be set in the text control
      value = textControl.getText();
      super.okPressed();
    }

    public String getValue() {
      return value;
    }

  }
