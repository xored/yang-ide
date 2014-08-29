package com.cisco.yangide.ext.model.editor.widget;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

public abstract class DialogText {

    public DialogText(Composite parent, FormToolkit toolkit) {
        createControl(parent, toolkit);
    }

    private Control contents;

    private Text text;

    private Composite editor;

    private Button button;

    /**
     * The value of this cell editor; initially <code>null</code>.
     */
    private Object value = null;

    protected Control createControl(final Composite parent, FormToolkit toolkit) {
        editor = toolkit.createComposite(parent, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(editor);
        GridLayoutFactory.fillDefaults().numColumns(2).spacing(2, 0).applyTo(editor);
        text = toolkit.createText(editor, "");

        contents = text;
        updateContents(value);

        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.TOP).hint(100, -1).grab(true, false).applyTo(contents);

        button = toolkit.createButton(editor, "...", SWT.PUSH);
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                openDialogBox(text);
            }
        });
        parent.layout();
        int h = text.computeSize(-1, -1).y;
        if (h == 0) {
            h = 25;
        }
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.TOP).hint(h, h).grab(false, false).applyTo(button);
        editor.addControlListener(new ControlListener() {
            @Override
            public void controlResized(ControlEvent e) {
                int h = text.getBounds().height;
                if (((GridData) button.getLayoutData()).heightHint != h && h != 0) {
                    GridDataFactory.swtDefaults().align(SWT.FILL, SWT.TOP).hint(h, h).grab(false, false)
                    .applyTo(button);
                    editor.layout();
                    editor.getParent().layout();
                }
            }

            @Override
            public void controlMoved(ControlEvent e) {
            }
        });

        return editor;
    }

    public Text getTextControl() {
        return text;
    }

    public String getText() {
        return text.getText();
    }

    public void setText(String value) {
        text.setText(value);
    }

    public Control getControl() {
        return editor;
    }

    public void setLayoutData(Object layoutData) {
        editor.setLayoutData(layoutData);
    }

    protected abstract Object openDialogBox(Text text);

    protected void updateContents(Object value) {
        if (text == null) {
            return;
        }

        String textValue = "";//$NON-NLS-1$
        if (value != null) {
            textValue = value.toString();
        }
        text.setText(textValue);
    }

}
