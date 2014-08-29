/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
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

import com.cisco.yangide.ext.model.TaggedNode;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;
import com.cisco.yangide.ext.model.editor.util.YangTag;

/**
 * @author Konstantin Zaitsev
 * @date Aug 29, 2014
 */
public class GeneralTabDescriptionSection extends YangPropertySection implements ITabbedPropertyConstants {
    private Text descriptionText;

    @Override
    public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
        super.createControls(parent, tabbedPropertySheetPage);

        TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
        Composite composite = factory.createFlatFormComposite(parent);
        GridLayoutFactory.swtDefaults().numColumns(2).applyTo(composite);
        CLabel valueLabel = factory.createCLabel(composite, "Description:");
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.TOP).hint(STANDARD_LABEL_WIDTH, SWT.DEFAULT)
                .applyTo(valueLabel);
        descriptionText = factory.createText(composite, "", SWT.MULTI | SWT.WRAP);
        GridDataFactory.swtDefaults().hint(200, 100).applyTo(descriptionText);
    }

    @Override
    protected boolean isApplied(Object bo) {
        return YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getTaggedNode(), bo)
                && YangModelUtil.getTag(YangTag.DESCRIPTION, (TaggedNode) bo) != null;
    }

    @Override
    protected Binding createBinding(DataBindingContext bindingContext, EObject obj) {
        return bindingContext.bindValue(
                WidgetProperties.text(SWT.Modify).observeDelayed(150, descriptionText),
                EMFProperties.value(YangModelUtil.MODEL_PACKAGE.getTag_Value()).observe(
                        YangModelUtil.getTag(YangTag.DESCRIPTION, (TaggedNode) obj)));
    }

}
