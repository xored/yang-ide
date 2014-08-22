package com.cisco.yangide.ext.model.editor.dialog;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.cisco.yangide.core.indexing.ElementIndexInfo;
import com.cisco.yangide.ext.model.Module;
import com.cisco.yangide.ext.model.editor.dialog.YangElementListSelectionDialog.Transformer;

public class ElementLabelProvider extends LabelProvider {
        
    protected Transformer transformer;
    protected Module module;

    public ElementLabelProvider(Transformer transformer) {
        this.transformer = transformer;
    }

    public ElementLabelProvider(Module module) {
        this.module = module;
        this.transformer = new Transformer() {

            @Override
            public String transform(ElementIndexInfo info) {
                if (getModule().getName().equals(info.getModule())) {
                    return info.getName();
                } else {
                    return info.getModule() + " : " + info.getName();
                }
            }


        };
    }

    @Override
    public Image getImage(Object element) {
        return super.getImage(element);
    }

    @Override
    public String getText(Object element) {
        if (element instanceof ElementIndexInfo) {
            return transformer.transform((ElementIndexInfo) element);
        }
        return null;
    }
    
    private Module getModule() {
        return module;
    }
    public void setModule(Module module) {
        this.module = module;
    }
}
