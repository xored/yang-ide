package com.cisco.yangide.ext.model.editor.features;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public class DiagramLayoutAlgorithmChoiceDialog extends ElementListSelectionDialog {

    public DiagramLayoutAlgorithmChoiceDialog(Shell parent) {
        super(parent, new LabelProvider() {
            public String getText(Object element) {
                Integer idx = (Integer) element;
                return DiagramLayoutCustomFeature.layouts.get(idx - 1);
            }
        });
        Object[] elements = new Object[DiagramLayoutCustomFeature.layouts.size()];
        for (int i = 0; i < DiagramLayoutCustomFeature.layouts.size(); i++) {
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
