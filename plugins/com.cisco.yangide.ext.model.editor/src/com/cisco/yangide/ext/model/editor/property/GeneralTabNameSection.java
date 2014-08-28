package com.cisco.yangide.ext.model.editor.property;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EObject;
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

import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class GeneralTabNameSection extends YangPropertySection implements ITabbedPropertyConstants {
    private Text nameText;

    @Override
    public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
        super.createControls(parent, tabbedPropertySheetPage);

        TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
        Composite composite = factory.createFlatFormComposite(parent);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(composite);
        CLabel valueLabel = factory.createCLabel(composite, "Name:");
        GridDataFactory.fillDefaults().hint(STANDARD_LABEL_WIDTH, SWT.DEFAULT).align(SWT.END, SWT.END)
                .indent(HSPACE, VSPACE).applyTo(valueLabel);
        nameText = factory.createText(composite, "");
        GridDataFactory.fillDefaults().hint(200, SWT.DEFAULT).align(SWT.END, SWT.END).indent(HSPACE, VSPACE)
                .applyTo(nameText);

    }

    @Override
    protected boolean isApplied(Object bo) {
        return YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getNamedNode(), bo);
    }

    @Override
    protected Binding createBinding(DataBindingContext bindingContext, EObject obj) {
        return bindingContext.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(500, nameText), EMFProperties
                .value(YangModelUtil.MODEL_PACKAGE.getNamedNode_Name()).observe(obj));
    }

}
