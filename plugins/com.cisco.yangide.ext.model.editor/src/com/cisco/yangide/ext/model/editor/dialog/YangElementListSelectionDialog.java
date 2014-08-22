package com.cisco.yangide.ext.model.editor.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.cisco.yangide.core.indexing.ElementIndexInfo;
import com.cisco.yangide.core.indexing.ElementIndexType;
import com.cisco.yangide.core.model.YangModelManager;
import com.cisco.yangide.editor.editors.YangScanner;
import com.cisco.yangide.ext.model.Import;
import com.cisco.yangide.ext.model.Module;
import com.cisco.yangide.ext.model.editor.util.Strings;
import com.cisco.yangide.ext.model.editor.util.YangDiagramImageProvider;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class YangElementListSelectionDialog extends ElementListSelectionDialog {
    
    protected class ElementComparator implements Comparator<Object> {

        @Override
        public int compare(Object o1, Object o2) {
            if (null == o1 && null == o2) {
                return 0;
            }
            if (null != o1 && null == o2) {
                return 1;
            }
            if (null == o1 && null != o2) {
                return -1;
            }
            if (o1 instanceof String && o2 instanceof String) {
                return ((String) o1).compareTo((String) o2);
            }
            if (o1 instanceof String && !(o2 instanceof String)) {
                return -1;
            }
            if (!(o1 instanceof String) && o2 instanceof String) {
                return 1;
            }
            if (o1 instanceof ElementIndexInfo && o2 instanceof ElementIndexInfo) {
                if (((ElementIndexInfo) o1).getModule().equals(((ElementIndexInfo) o2).getModule())) {
                    return ((ElementIndexInfo) o1).getName().compareTo(((ElementIndexInfo) o2).getName());
                }
                else {
                    return ((ElementIndexInfo) o1).getModule().compareTo(((ElementIndexInfo) o2).getModule());
                }
            }
            return o1.toString().compareTo(o2.toString());
        }
        
    }
    
    private Module module;
    private HashMap<String, String> imports = new HashMap<String, String>();
    private String value;
    
    public interface Transformer {
        public String transform(ElementIndexInfo info);
    }
    protected Object[] list; 


    public YangElementListSelectionDialog(Shell parent, ElementIndexType indexType, IFile file, String imageId, Module module, Transformer transformer) {
        super(parent, new ElementLabelProvider(transformer));
        reset(indexType, file, imageId, module);
    }
    
    public YangElementListSelectionDialog(Shell parent, ElementIndexType indexType, IFile file, String imageId, Module module) {
        super(parent, new ElementLabelProvider(module));
        reset(indexType, file, imageId, module);       
    }
    
    public void reset(ElementIndexType indexType, IFile file, String imageId, Module m) {
        setTitle("Select element");
        setAllowDuplicates(false);
        setModule(m);
        setList(indexType, file, m);
        setImage(GraphitiUi.getImageService().getImageForId(YangDiagramImageProvider.DIAGRAM_TYPE_PROVIDER_ID, imageId));
    }
    
    public void setList(ElementIndexType indexType, IFile file, Module module) {
        List<Object> result = new ArrayList<Object>();
        if (ElementIndexType.TYPE.equals(indexType)) {
            result.addAll(Arrays.asList(YangScanner.getTypes()));
        }
        result.addAll(Arrays.asList(YangModelManager.search(null, null, null, indexType, null == file ? null : file.getProject(), null)));
        list = filterElements(result).toArray();
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
    
    protected List<Object> filterElements(List<Object> elements) {
        List<Object> result = new ArrayList<Object>();
        for (Object element : elements) {
            if (!(element instanceof ElementIndexInfo) || hasImport(element)) {
                result.add(element);
            }
        }
        return result;
    }
    
    protected boolean hasImport(Object element) {
        if (element instanceof ElementIndexInfo) {
            return imports.containsKey(((ElementIndexInfo) element).getModule()) || ((ElementIndexInfo) element).getModule().equals(module.getName());
        }
        return false;
    }

    protected void setResultObject() {
        Object result = getFirstResult();
        if (result instanceof ElementIndexInfo) {
            ElementIndexInfo choosen = (ElementIndexInfo) result;
            if (null == getModule() || choosen.getModule().equals(getModule().getName())) {
                value = choosen.getName();
            } else {
                value = (imports.containsKey(choosen.getModule()) ? imports.get(choosen.getModule()) + ":" : "") + choosen.getName();
            }
        } else {
            value = Strings.getAsString(result);
        }
    }
    
    public String getValue() {
        return value;
    }
    
    protected void setModule(Module module) {
        this.module = module;
        for (EObject i : YangModelUtil.filter(module.getChildren(), YangModelUtil.MODEL_PACKAGE.getImport())) {
            imports.put(((Import) i).getModule(), ((Import) i).getPrefix());
        }
    }
    
    protected Module getModule() {
        return module;
    }
    

}
