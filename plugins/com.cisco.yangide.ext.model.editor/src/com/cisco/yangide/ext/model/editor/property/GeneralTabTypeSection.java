package com.cisco.yangide.ext.model.editor.property;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
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
        FormData data;

        typeText = factory.createCLabel(composite, "");
        data = new FormData();
        data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
        data.right = new FormAttachment(100, 0);
        data.top = new FormAttachment(0, VSPACE);
        typeText.setLayoutData(data);

        CLabel valueLabel = factory.createCLabel(composite, "Type:");
        data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(typeText, -HSPACE);
        data.top = new FormAttachment(typeText, 0, SWT.CENTER);
        valueLabel.setLayoutData(data);
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
