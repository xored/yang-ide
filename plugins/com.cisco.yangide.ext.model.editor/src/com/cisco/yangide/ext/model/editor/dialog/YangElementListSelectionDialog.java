package com.cisco.yangide.ext.model.editor.dialog;

import org.eclipse.core.resources.IFile;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.cisco.yangide.core.indexing.ElementIndexInfo;
import com.cisco.yangide.core.indexing.ElementIndexType;
import com.cisco.yangide.core.model.YangModelManager;
import com.cisco.yangide.ext.model.Module;
import com.cisco.yangide.ext.model.editor.util.YangDiagramImageProvider;

public class YangElementListSelectionDialog extends ElementListSelectionDialog {
    
    private Module module;
    private String value;
    
    public interface Transformer {
        public String transform(ElementIndexInfo info);
    }
    protected ElementIndexInfo[] list; 


    public YangElementListSelectionDialog(Shell parent, ElementIndexType indexType, IFile file, String imageId, Module module, Transformer transformer) {
        super(parent, new ElementLabelProvider(transformer));
        reset(indexType, file, imageId, module);
        setTitle("Select element");
    }
    
    public YangElementListSelectionDialog(Shell parent, ElementIndexType indexType, IFile file, String imageId, Module module) {
        super(parent, new ElementLabelProvider(module));
        reset(indexType, file, imageId, module);
        setTitle("Select element");
    }
    
    public void reset(ElementIndexType indexType, IFile file, String imageId, Module module) {
        this.module = module;
        setList(indexType, file, module);
        setImage(GraphitiUi.getImageService().getImageForId(YangDiagramImageProvider.DIAGRAM_TYPE_PROVIDER_ID, imageId));
    }
    
    public void setList(ElementIndexType indexType, IFile file, Module module) {
        list = YangModelManager.search(null, null, null, indexType, null == file ? null : file.getProject(), null);
        setElements(list);
    }
    @Override
    protected void okPressed() {
        computeResult();
        if (null == getFirstResult()) {
            
            MessageDialog.openWarning(getShell(), "Warning", "No element was choosen");
        } else {
            setResultObject();
            super.okPressed();
        }
    }
    
    protected void setResultObject() {
        ElementIndexInfo choosen = (ElementIndexInfo) getFirstResult();
        if (null == getModule() || choosen.getModule().equals(getModule().getName())) {
            value = choosen.getName(); 
        } else {
            value = choosen.getModule() + ":" + choosen.getName();
        }
    }
    
    public String getValue() {
        return value;
    }
    
    protected Module getModule() {
        return module;
    }
    

}
