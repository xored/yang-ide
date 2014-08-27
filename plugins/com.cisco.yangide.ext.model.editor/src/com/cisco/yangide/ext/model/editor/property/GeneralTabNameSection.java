package com.cisco.yangide.ext.model.editor.property;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

import com.cisco.yangide.ext.model.NamedNode;
import com.cisco.yangide.ext.model.editor.util.YangModelUIUtil;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class GeneralTabNameSection extends GFPropertySection implements ITabbedPropertyConstants {

    private Text nameText;
    private NamedNode node;
    private DataBindingContext bindingContext = new DataBindingContext();
    private Binding binding;
    
    @Override
    public void createControls(Composite parent,
        TabbedPropertySheetPage tabbedPropertySheetPage) {
        super.createControls(parent, tabbedPropertySheetPage);
 
        TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
        Composite composite = factory.createFlatFormComposite(parent);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(composite);
        CLabel valueLabel = factory.createCLabel(composite, "Name:");
        GridDataFactory.fillDefaults().hint(STANDARD_LABEL_WIDTH, SWT.DEFAULT).align(SWT.END, SWT.END).indent(HSPACE, VSPACE).applyTo(valueLabel);
        nameText = factory.createText(composite, "");
        GridDataFactory.fillDefaults().hint(200, SWT.DEFAULT).align(SWT.END, SWT.END).indent(HSPACE, VSPACE).applyTo(nameText);
 
       
    }
 
    @Override
    public void refresh() {
        if (null != binding && !binding.isDisposed()) {
            binding.updateTargetToModel();
            binding.dispose();
            bindingContext.removeBinding(binding);
        }
        PictogramElement pe = getSelectedPictogramElement();
        if (pe != null) {
            Object bo = Graphiti.getLinkService()
                 .getBusinessObjectForLinkedPictogramElement(pe);
            if (bo == null || !YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getNamedNode(), bo)) {
                return;
            }
            node = ((NamedNode) bo);
            binding = bindingContext.bindValue(
                    WidgetProperties.text(SWT.Modify).observeDelayed(2000, nameText), EMFProperties
                            .value(YangModelUtil.MODEL_PACKAGE.getNamedNode_Name()).observe(node));
            binding.updateModelToTarget();
            binding.getModel().addChangeListener(new IChangeListener() {
                
                @Override
                public void handleChange(ChangeEvent event) {
                    YangModelUIUtil
                    .updatePictogramElement(getDiagramTypeProvider().getFeatureProvider(), YangModelUIUtil.getBusinessObjectPropShape(getDiagramTypeProvider().getFeatureProvider(),
                            node, YangModelUtil.MODEL_PACKAGE.getNamedNode_Name()));
                }
            });
        }
    }
}
