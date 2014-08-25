package com.cisco.yangide.ext.model.editor.property;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
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
import com.cisco.yangide.ext.model.editor.dialog.YangElementListSelectionDialog;
import com.cisco.yangide.ext.model.editor.util.PropertyUtil;
import com.cisco.yangide.ext.model.editor.util.YangDiagramImageProvider;
import com.cisco.yangide.ext.model.editor.util.YangModelUIUtil;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;
import com.cisco.yangide.ext.model.editor.widget.DialogText;

public class GeneralTabReferenceSection extends GFPropertySection implements ITabbedPropertyConstants {

    private DialogText text;
    private CLabel valueLabel;
    private DataBindingContext bindingContext = new DataBindingContext();
    private Binding binding;
    YangElementListSelectionDialog dialog;
    private EObject node;

    @Override
    public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
        super.createControls(parent, tabbedPropertySheetPage);

        TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
        Composite composite = factory.createFlatFormComposite(parent);
        FormData data;

        text = new DialogText(composite, tabbedPropertySheetPage.getWidgetFactory()) {

            @Override
            protected Object openDialogBox(Text text) {
                if (null != dialog) {
                    if (IStatus.OK == dialog.open()) {
                        setValue(dialog.getValue());
                    }
                }
                return null;
            }
        };
        data = new FormData();
        data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
        data.right = new FormAttachment(100, 0);
        data.top = new FormAttachment(0, VSPACE);
        text.setLayoutData(data);

        valueLabel = factory.createCLabel(composite, "Reference:");
        data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(text.getControl(), -HSPACE);
        data.top = new FormAttachment(text.getControl(), 0, SWT.CENTER);
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
            node = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
            if (node == null) {
                return;
            }
            if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getUses(), node)) {
                binding = bindingContext.bindValue(
                        WidgetProperties.text(SWT.Modify).observeDelayed(2000, text.getTextControl()), EMFProperties
                                .value(YangModelUtil.MODEL_PACKAGE.getUses_QName()).observe(node));
            } else if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getReferenceNode(), node)) {
                binding = bindingContext.bindValue(
                        WidgetProperties.text(SWT.Modify).observeDelayed(2000, text.getTextControl()), EMFProperties
                                .value(YangModelUtil.MODEL_PACKAGE.getReferenceNode_Reference()).observe(node));
            }
            binding.updateModelToTarget();
            EClass reference = YangModelUtil.getConnectionReferenceClass(node);
            ElementIndexType indexType = ElementIndexType.GROUPING;
            String imageId = YangDiagramImageProvider.IMG_GROUPING_PROPOSAL;
            if (YangModelUtil.MODEL_PACKAGE.getIdentity().equals(reference)) {
                valueLabel.setText("Base:");
                indexType = ElementIndexType.IDENTITY;
                imageId = YangDiagramImageProvider.IMG_IDENTITY_PROPOSAL;
            } else {
                valueLabel.setText(reference.getName() + ":");
            }
            Module module = (Module) getDiagramTypeProvider().getFeatureProvider()
                    .getBusinessObjectForPictogramElement(getDiagram());
            if (null == dialog) {

                Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                dialog = new YangElementListSelectionDialog(shell, indexType, null, imageId, module, text.getText());
            } else {
                dialog.reset(indexType, null, imageId, module, text.getText());
            }

            binding.getModel().addChangeListener(new IChangeListener() {

                @Override
                public void handleChange(ChangeEvent event) {
                    if (pe instanceof ContainerShape) {
                        Shape shape = YangModelUIUtil.getBusinessObjectPropShape((ContainerShape) pe,
                                PropertyUtil.OBJECT_HEADER_TEXT_SHAPE_KEY);
                        if (null != shape) {
                            YangModelUIUtil
                                    .updatePictogramElement(getDiagramTypeProvider().getFeatureProvider(), shape);
                        }
                    }
                }
            });
        }
    }

    private void setValue(String firstResult) {
        text.setText(firstResult);
    }

}
