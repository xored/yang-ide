package com.cisco.yangide.ext.model.editor.property;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class GeneralTabTypeSection extends GFPropertySection implements ITabbedPropertyConstants {

    private CLabel typeText;

    @Override
    public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
        super.createControls(parent, tabbedPropertySheetPage);

        TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
        Composite composite = factory.createFlatFormComposite(parent);

        GridLayoutFactory.swtDefaults().numColumns(2).applyTo(composite);

        CLabel label = factory.createCLabel(composite, "Type:");
        GridDataFactory.fillDefaults().hint(STANDARD_LABEL_WIDTH, SWT.DEFAULT).applyTo(label);
        typeText = factory.createCLabel(composite, "");

    }

    @Override
    public void refresh() {
        PictogramElement pe = getSelectedPictogramElement();
        if (pe != null) {
            Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
            if (bo == null || !YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getNode(), bo)) {
                return;
            }
            typeText.setText(((EObject) bo).eClass().getName());
        }
    }
}
