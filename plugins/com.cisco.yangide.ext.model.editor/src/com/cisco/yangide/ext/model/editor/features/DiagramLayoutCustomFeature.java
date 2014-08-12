package com.cisco.yangide.ext.model.editor.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.layouts.LayoutAlgorithm;

import com.cisco.yangide.ext.model.editor.dialog.DiagramLayoutAlgorithmChoiceDialog;
import com.cisco.yangide.ext.model.editor.util.LayoutUtil;

public class DiagramLayoutCustomFeature extends AbstractCustomFeature {
    
    


    public DiagramLayoutCustomFeature(IFeatureProvider fp) {
        super(fp);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
     */
    @Override
    public String getDescription() {
        return "Layout diagram with Zest Layouter"; //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
     */
    @Override
    public String getName() {
        return "&Layout Diagram"; //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute(org.eclipse.graphiti.features.context.ICustomContext)
     */
    @Override
    public boolean canExecute(ICustomContext context) {
        PictogramElement[] pes = context.getPictogramElements();
        if (pes != null && pes.length == 1 && pes[0] instanceof Diagram) {
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
     */
    @Override
    public void execute(ICustomContext context) {
        // ask the user for a LayoutAlgorithmn
        Integer type = askForLayoutType();

        if (type != null) {
            LayoutUtil.layoutDiagram(getFeatureProvider(), type.intValue());
        }
    }

   
    /**
     * Simple dialog to ask the user for a {@link LayoutAlgorithm}<br/>
     * Used to test various Zest algorithmns
     * 
     * @return the chosen algorithmn
     */
    private Integer askForLayoutType() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        /** DiagramLayoutAlgorithmChoiceDialog is a simple Dialog asking for an Integer for now */
        DiagramLayoutAlgorithmChoiceDialog dialog = new DiagramLayoutAlgorithmChoiceDialog(shell);
        return dialog.open();
    }

   

}
