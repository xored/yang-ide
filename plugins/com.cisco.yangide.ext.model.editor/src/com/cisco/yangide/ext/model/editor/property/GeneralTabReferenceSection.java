package com.cisco.yangide.ext.model.editor.property;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

import com.cisco.yangide.core.indexing.ElementIndexType;
import com.cisco.yangide.ext.model.Module;
import com.cisco.yangide.ext.model.editor.dialog.YangElementListSelectionDialog;
import com.cisco.yangide.ext.model.editor.util.YangDiagramImageProvider;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;
import com.cisco.yangide.ext.model.editor.widget.DialogText;

public class GeneralTabReferenceSection extends YangPropertySection implements ITabbedPropertyConstants {

    private DialogText text;
    private CLabel valueLabel;
    YangElementListSelectionDialog dialog;

    @Override
    public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
        super.createControls(parent, tabbedPropertySheetPage);

        TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
        Composite composite = factory.createFlatFormComposite(parent);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(composite);
        valueLabel = factory.createCLabel(composite, "Reference:");
        GridDataFactory.fillDefaults().hint(STANDARD_LABEL_WIDTH, SWT.DEFAULT).align(SWT.END, SWT.END)
        .indent(HSPACE, VSPACE).applyTo(valueLabel);
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
        GridDataFactory.fillDefaults().hint(200, SWT.DEFAULT).align(SWT.END, SWT.END).indent(HSPACE, VSPACE)
        .applyTo(text.getControl());
    }

    @Override
    public void setInput(IWorkbenchPart part, ISelection selection) {
        super.setInput(part, selection);
        if (getEObject() != null && getDiagramTypeProvider() != null) {
            EClass reference = YangModelUtil.getConnectionReferenceObjectClass(getEObject());
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
        }
    }

    private void setValue(String firstResult) {
        text.setText(firstResult);
    }

    @Override
    protected Binding createBinding(DataBindingContext bindingContext, EObject obj) {
        if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getUses(), obj)) {
            return bindingContext.bindValue(
                    WidgetProperties.text(SWT.Modify).observeDelayed(1000, text.getTextControl()),
                    EMFProperties.value(YangModelUtil.MODEL_PACKAGE.getUses_QName()).observe(obj));
        }
        return bindingContext.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(1000, text.getTextControl()),
                EMFProperties.value(YangModelUtil.MODEL_PACKAGE.getReferenceNode_Reference()).observe(obj));
    }

    @Override
    protected boolean isApplied(Object bo) {
        return YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getUses(), bo)
                || YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getReferenceNode(), bo);
    }

}
