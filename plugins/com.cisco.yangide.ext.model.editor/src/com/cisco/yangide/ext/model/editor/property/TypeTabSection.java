package com.cisco.yangide.ext.model.editor.property;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

import com.cisco.yangide.core.indexing.ElementIndexType;
import com.cisco.yangide.ext.model.Module;
import com.cisco.yangide.ext.model.TypedNode;
import com.cisco.yangide.ext.model.editor.dialog.YangElementListSelectionDialog;
import com.cisco.yangide.ext.model.editor.util.PropertyUtil;
import com.cisco.yangide.ext.model.editor.util.YangDiagramImageProvider;
import com.cisco.yangide.ext.model.editor.util.YangModelUIUtil;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;
import com.cisco.yangide.ext.model.editor.widget.DialogText;

public class TypeTabSection extends GFPropertySection implements ITabbedPropertyConstants {
    private DialogText type;
    private TypedNode node;
    private DataBindingContext bindingContext = new DataBindingContext();
    private Binding binding;

    @Override
    public void createControls(Composite parent,
        TabbedPropertySheetPage tabbedPropertySheetPage) {
        super.createControls(parent, tabbedPropertySheetPage);
 
        TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
        Composite composite = factory.createFlatFormComposite(parent);
        FormData data;
        
        type = new DialogText(composite) {
            
            @Override
            protected Object openDialogBox(Text text) {
                Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                Module module = (Module) getDiagramTypeProvider().getFeatureProvider().getBusinessObjectForPictogramElement(getDiagram());
                YangElementListSelectionDialog dialog = new YangElementListSelectionDialog(shell, ElementIndexType.TYPE, null, YangDiagramImageProvider.IMG_CUSTOM_TYPE_PROPOSAL, module);
                if (IStatus.OK == dialog.open()) {
                    setType(dialog.getValue());
                }
                return null;
            }
        };
            
        data = new FormData();
        data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
        data.right = new FormAttachment(100, 0);
        data.top = new FormAttachment(0, VSPACE);
        type.setLayoutData(data);
 
        CLabel valueLabel = factory.createCLabel(composite, "Name:");
        data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(type.getControl(), -HSPACE);
        data.top = new FormAttachment(type.getControl(), 0, SWT.CENTER);
        valueLabel.setLayoutData(data);
    }
 
    @Override
    public void refresh() {
        if (null != binding) {
            binding.updateTargetToModel();
            bindingContext.removeBinding(binding);
            binding = null;
        }
        final PictogramElement pe = getSelectedPictogramElement();
        if (pe != null) {
            Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
            if (bo == null || !YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getTypedNode(), bo)) {
                return;
            }
            node = (TypedNode) bo;
            binding = bindingContext.bindValue(
                    WidgetProperties.text(SWT.Modify).observeDelayed(200, type.getTextControl()),
                    EMFProperties.value(YangModelUtil.MODEL_PACKAGE.getNamedNode_Name()).observe(node.getType()));
        }
        binding.updateModelToTarget();
        binding.getModel().addChangeListener(new IChangeListener() {

            @Override
            public void handleChange(ChangeEvent event) {
                if (pe instanceof ContainerShape) {
                    Shape shape = YangModelUIUtil.getBusinessObjectPropShape((ContainerShape) pe,
                            PropertyUtil.BUSINESS_OBJECT_TYPE_SHAPE_KEY);
                    if (null != shape) {
                        YangModelUIUtil.updatePictogramElement(getDiagramTypeProvider().getFeatureProvider(), shape);
                    }
                }
            }
        });
    }
    
    private void setType(String firstResult) {
        type.setText(firstResult); 
    }
}
