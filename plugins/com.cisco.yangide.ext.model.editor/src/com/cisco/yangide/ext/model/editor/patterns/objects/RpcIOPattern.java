package com.cisco.yangide.ext.model.editor.patterns.objects;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import com.cisco.yangide.ext.model.Node;
import com.cisco.yangide.ext.model.Rpc;
import com.cisco.yangide.ext.model.RpcIO;
import com.cisco.yangide.ext.model.editor.util.YangDiagramImageProvider;
import com.cisco.yangide.ext.model.editor.util.YangModelUIUtil;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class RpcIOPattern extends DomainObjectPattern {
    
    private boolean input; 
    
    public RpcIOPattern(boolean input) {
       super();
       this.input = input;
    }
    
    @Override
    public boolean canCreate(ICreateContext context) {
        Object obj = getBusinessObjectForPictogramElement(context.getTargetContainer());
        return super.canCreate(context) && null != obj && YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getRpc(), obj) && check((Rpc) obj);
    }

    @Override
    public String getCreateImageId() {
        if (input) {
            return YangDiagramImageProvider.IMG_RPC_INPUT_PROPOSAL;
        } else {
            return YangDiagramImageProvider.IMG_RPC_OUTPUT_PROPOSAL;
        }
    }

    @Override
    public String getCreateName() {
        if (input) {
            return "input";
        } else {
            return "output";
        }
    }

    @Override
    public boolean isMainBusinessObjectApplicable(Object mainBusinessObject) {
        return super.isMainBusinessObjectApplicable(mainBusinessObject) && input == ((RpcIO) mainBusinessObject).isInput();
    }

    @Override
    protected EClass getObjectEClass() {
        return YangModelUtil.MODEL_PACKAGE.getRpcIO();
    }   
    
    @Override
    public boolean canAdd(IAddContext context) {
        Object obj = getBusinessObjectForPictogramElement(context.getTargetContainer());
        return super.canAdd(context) && null != obj && YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getRpc(), obj) && check((Rpc) obj, context.getNewObject());
    }

    private boolean check(Rpc rpc, Object obj) {
        if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getRpcIO(), obj)) {
            RpcIO cur = (RpcIO) obj;
            for (Node n : YangModelUtil.filter(rpc.getChildren(), YangModelUtil.MODEL_PACKAGE.getRpcIO())) {
                if (cur != n && cur.isInput() == ((RpcIO) n).isInput()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    private boolean check(Rpc rpc) {
        for (Node n : YangModelUtil.filter(rpc.getChildren(), YangModelUtil.MODEL_PACKAGE.getRpcIO())) {
            if (input == ((RpcIO) n).isInput()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canMoveShape(IMoveShapeContext context) {
        return context.getTargetContainer() == context.getSourceContainer();
    }

    @Override
    public PictogramElement add(IAddContext context) {
        return YangModelUIUtil.drawPictogramElement(context, getFeatureProvider(), getCreateImageId(),  getCreateName());
    }

}
