package com.cisco.yangide.ext.model.editor.dialog;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public class DiagramLayoutAlgorithmChoiceDialog extends ElementListSelectionDialog {
    
    private static final List<String> layouts = Arrays.asList("Spring Layout", "Tree Layout", "Grid Layout", "Horizontal Layout", "Horizontal Tree Layout", "Vertical Layout", "Radial Layout", "Directed Graph Layout", "Composite Layout [Directed Graph + Horizontal Shift]", "Composite Layout [Spring Layout + Horizontal Shift]", "Composite Layout [Radial Layout + Horizontal Shift]", "Composite Layout [Tree Layout + Horizontal Shift]");

    public DiagramLayoutAlgorithmChoiceDialog(Shell parent) {
        super(parent, new LabelProvider() {
            public String getText(Object element) {
                Integer idx = (Integer) element;
                return layouts.get(idx - 1);
            }
        });
        Object[] elements = new Object[layouts.size()];
        for (int i = 0; i < layouts.size(); i++) {
            elements[i] = Integer.valueOf(i + 1);
        }
        setElements(elements);
        setTitle("Select Layout");
        setMultipleSelection(false);
    }

    @Override
    public int open() {
        int result = super.open();
        if (result < 0)
            return result;
        return (Integer) getFirstResult();
    }

}
