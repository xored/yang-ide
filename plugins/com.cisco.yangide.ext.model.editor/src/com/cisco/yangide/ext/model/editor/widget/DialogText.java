package com.cisco.yangide.ext.model.editor.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;

public abstract class DialogText {
    
    public DialogText (Composite parent) {
        createControl(parent);
    }

   private Control contents;

   private Text text;
   
   private Composite editor;

   private Button button;

   /**
    * The value of this cell editor; initially <code>null</code>.
    */
   private Object value = null;


   /**
    * Internal class for laying out the dialog.
    */
   private class DialogCellLayout extends Layout {
       public void layout(Composite editor, boolean force) {
           Rectangle bounds = editor.getClientArea();
           Point size = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
           int height = contents.computeSize(SWT.DEFAULT, SWT.DEFAULT, force).y;
           if (contents != null) {
               contents.setBounds(0, 0, bounds.width - size.x, height);
           }
           button.setBounds(bounds.width - size.x, 0, size.x, height);
       }

       public Point computeSize(Composite editor, int wHint, int hHint,
               boolean force) {
           if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT) {
               return new Point(wHint, hHint);
           }
           Point contentsSize = contents.computeSize(SWT.DEFAULT, SWT.DEFAULT,
                   force);
           Point buttonSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT,
                   force);
           Point result = new Point(buttonSize.x, contentsSize.y);
           return result;
       }
   }
   protected Control createContents(Composite parent) {
       text = new Text(parent, SWT.LEFT);
       text.setFont(parent.getFont());
       text.setBackground(parent.getBackground());
       return text;
   }
   
   protected Button createButton(Composite parent) {
       Button result = new Button(parent, SWT.DOWN);
       result.setText("..."); //$NON-NLS-1$
       return result;
   }

   protected Control createControl(Composite parent) {

       Font font = parent.getFont();
       Color bg = parent.getBackground();

       editor = new Composite(parent, SWT.BORDER);
       editor.setFont(font);
       editor.setBackground(bg);
       editor.setLayout(new DialogCellLayout());

       contents = createContents(editor);
       updateContents(value);

       button = createButton(editor);
       button.setFont(font);

       button.addSelectionListener(new SelectionAdapter() {
           public void widgetSelected(SelectionEvent event) {
               
               openDialogBox(text);
               
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
   
   public void setLayoutData (Object layoutData) {
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
