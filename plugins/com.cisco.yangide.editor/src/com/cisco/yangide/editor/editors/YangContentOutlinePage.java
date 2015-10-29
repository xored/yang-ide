/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.editor.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.core.dom.ASTCompositeNode;
import com.cisco.yangide.core.dom.ASTNamedNode;
import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.AugmentationSchema;
import com.cisco.yangide.core.dom.ContrainerSchemaNode;
import com.cisco.yangide.core.dom.Deviation;
import com.cisco.yangide.core.dom.ExtensionDefinition;
import com.cisco.yangide.core.dom.GroupingDefinition;
import com.cisco.yangide.core.dom.IdentitySchemaNode;
import com.cisco.yangide.core.dom.LeafSchemaNode;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.ModuleImport;
import com.cisco.yangide.core.dom.NotificationDefinition;
import com.cisco.yangide.core.dom.RpcDefinition;
import com.cisco.yangide.core.dom.RpcInputNode;
import com.cisco.yangide.core.dom.RpcOutputNode;
import com.cisco.yangide.core.dom.SubModuleInclude;
import com.cisco.yangide.core.dom.TypeDefinition;
import com.cisco.yangide.core.dom.TypeReference;
import com.cisco.yangide.core.dom.UsesNode;
import com.cisco.yangide.ui.internal.IYangUIConstants;
import com.cisco.yangide.ui.internal.YangUIImages;

public class YangContentOutlinePage extends ContentOutlinePage {
    
    private YangEditor editor;
    
    private class YangOutlineStyledLabelProvider extends LabelProvider implements  IStyledLabelProvider{

        @Override
        public StyledString getStyledText(Object element) {
            if(element instanceof ASTNode) {
                StyledString result = new StyledString(((ASTNode) element).getNodeName());
                if (element instanceof ASTCompositeNode) {
                    result = new StyledString(((ASTCompositeNode)element).getName()).append(getStyledTypeName((ASTCompositeNode) element));
                } else if (element instanceof ASTNamedNode) {
                    result = new StyledString(((ASTNamedNode)element).getName());
                }
                return result;
            }
            return null;
        }

        private StyledString getStyledTypeName(ASTCompositeNode node) {
            for (ASTNode n : ((ASTCompositeNode) node).getChildren()) {
                if (n instanceof TypeReference) {
                    return new StyledString(" : " + ((TypeReference)n).getName(), StyledString.DECORATIONS_STYLER);
                }
            }
            return new StyledString();
        }
        
        @Override
        public Image getImage(Object element) {
            if(element instanceof AugmentationSchema) {
                return YangUIImages.getImage(IYangUIConstants.IMG_AUGMENT_PROPOSAL);
            } else if (element instanceof Deviation) {
                return YangUIImages.getImage(IYangUIConstants.IMG_DEVIATION_PROPOSAL);
            } else if (element instanceof IdentitySchemaNode) {
                return YangUIImages.getImage(IYangUIConstants.IMG_IDENTITY_PROPOSAL);
            } else if (element instanceof ExtensionDefinition) {
                return YangUIImages.getImage(IYangUIConstants.IMG_EXTENSION_PROPOSAL);
            } else if (element instanceof NotificationDefinition) {
                return YangUIImages.getImage(IYangUIConstants.IMG_NOTIFICATION_PROPOSAL);
            } else if (element instanceof RpcInputNode) {
                return YangUIImages.getImage(IYangUIConstants.IMG_RPC_INPUT_PROPOSAL);
            } else if (element instanceof RpcOutputNode) {
                return YangUIImages.getImage(IYangUIConstants.IMG_RPC_OUTPUT_PROPOSAL);
            } else if (element instanceof RpcDefinition) {
                return YangUIImages.getImage(IYangUIConstants.IMG_RPC_PROPOSAL);
            } else if (element instanceof ModuleImport) {
                return YangUIImages.getImage(IYangUIConstants.IMG_IMPORT_PROPOSAL);
            } else if (element instanceof TypeReference) {
                return YangUIImages.getImage(IYangUIConstants.IMG_TYPE_PROPOSAL);
            } else if (element instanceof TypeDefinition) {
                return YangUIImages.getImage(IYangUIConstants.IMG_CUSTOM_TYPE_PROPOSAL);
            } else if (element instanceof GroupingDefinition) {
                return YangUIImages.getImage(IYangUIConstants.IMG_GROUPING_PROPOSAL);
            } else if (element instanceof SubModuleInclude) {
                return YangUIImages.getImage(IYangUIConstants.IMG_SUBMODULE_PROPOSAL);
            } else if (element instanceof ContrainerSchemaNode) {
                return YangUIImages.getImage(IYangUIConstants.IMG_CONTAINER_PROPOSAL);   
            } else if (element instanceof LeafSchemaNode) {
                return YangUIImages.getImage(IYangUIConstants.IMG_LEAF_PROPOSAL);
            } else if (element instanceof UsesNode) {
                return YangUIImages.getImage(IYangUIConstants.IMG_USES_PROPOSAL);
            } else if (element instanceof SubModuleInclude) {
                return YangUIImages.getImage(IYangUIConstants.IMG_SUBMODULE_PROPOSAL);
            } else if (element instanceof Module) {
                return YangUIImages.getImage(IYangUIConstants.IMG_MODULE_PROPOSAL);
            }
            
            return null;
        }       
        
    }
    
  
    private class YangOutlineContentProvider implements ITreeContentProvider {

        @Override
        public void dispose() {     
        }
        
        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {      
        }

        @Override
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof ASTNode) {
                return filterChildren((ASTNode) inputElement).toArray();
            }
            return null;
        }
        
        public List<ASTNode> filterChildren(ASTNode parent) {
            List<ASTNode> result = new ArrayList<ASTNode>();
            if (null != parent && parent instanceof ASTCompositeNode) {
                for (ASTNode n : ((ASTCompositeNode)parent).getChildren()) {
                    if (n.isShowedInOutline()) {
                        result.add(n);
                    }
                }
            }
            return result;
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            return getElements(parentElement);
        }

        @Override
        public Object getParent(Object element) {
            if (element instanceof ASTNode) {
                return ((ASTNode) element).getParent();
            }
            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
            if (element instanceof ASTNode) {
                return !filterChildren((ASTNode) element).isEmpty();
            }
            return false;
        }
        
    }
    
    public YangContentOutlinePage(YangEditor e) {
        super();
        editor = e;
    }

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		getTreeViewer().setContentProvider(new YangOutlineContentProvider());
		getTreeViewer().setLabelProvider(new DelegatingStyledCellLabelProvider(new YangOutlineStyledLabelProvider()));
		getTreeViewer().setInput(getOutlineRoot());
	}

    private ASTNode getOutlineRoot() {
        try {
            return editor.getModule();
        } catch (YangModelException e) {
            return null;
        }
	}
    
    public void selectNode(ASTNode node) {
        ASTNode s = getFirstNodeInTree(node);
        if (null != s) {
            ISelection selected = getTreeViewer().getSelection();            
            if (selected instanceof IStructuredSelection && !((IStructuredSelection)selected).toList().contains(s)) {
                getTreeViewer().setSelection(new StructuredSelection(s), true);
            }            
        }
    }
    private ASTNode getFirstNodeInTree (ASTNode node) {
        ASTNode result = node;
        while (null != result && !result.isShowedInOutline()) {
            result = result.getParent();
        }
        return result;
    }
    
    public void updateOutline() {         
        Display d = getControl().getDisplay();
        if (d != null) {
            d.asyncExec(new Runnable() {
                public void run() {
                    getTreeViewer().setInput(getOutlineRoot());
                }
            });
        }
        
    }

}
