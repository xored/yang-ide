package com.cisco.yangide.ext.model.editor.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

import com.cisco.yangide.ext.model.ContainingNode;
import com.cisco.yangide.ext.model.Grouping;
import com.cisco.yangide.ext.model.Module;
import com.cisco.yangide.ext.model.Node;
import com.cisco.yangide.ext.model.Uses;
import com.cisco.yangide.ext.model.impl.ModelFactoryImpl;

public class DiagramImportSupport {

    private static boolean drawExternalLinks = false;
    
    public static Map<EObject, PictogramElement> elements = new HashMap<EObject, PictogramElement>();
    public static Map<String, Grouping> groupings = new HashMap<String, Grouping>();
    
    public static void importDiagram(Diagram diagram, IFeatureProvider fp) {
        EObject obj = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(diagram);
        if (null == obj) {
            obj = ModelFactoryImpl.eINSTANCE.createModule();
            fp.link(diagram, obj);
        }
        if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getModule(), obj)){
            elements.clear();
            groupings.clear();
            drawShapes(((Module) obj).getChildren(), diagram, fp);
            drawLines(((Module) obj).getChildren(), fp);
            YangModelUIUtil.layoutPictogramElement(diagram, fp);
            elements.clear();
            groupings.clear();
        }
    }
    
    public static void drawShapes(List<Node> list, ContainerShape cs, IFeatureProvider fp) {
        for (Node n : list) {
            int pos = cs.getGraphicsAlgorithm().getHeight();
            if (cs instanceof Diagram) {
                pos = 0;
            }
            PictogramElement pe = YangModelUIUtil.drawObject(n, cs, fp, 0, pos);
            additionalProcessing(n, pe, fp);
            
            if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getContainingNode(), n) && null != pe && pe instanceof ContainerShape) {
                drawShapes(((ContainingNode) n).getChildren(), (ContainerShape) pe, fp);
            }
        }
    }
    private static void additionalProcessing(Node n, PictogramElement pe, IFeatureProvider fp){
        elements.put(n, pe);
        if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getGrouping(), n)) {
            groupings.put(((Grouping) n).getName(), (Grouping) n);
        }
        if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getUses(), n)) {
            Uses uses = (Uses) n;
            if (null == uses.getGrouping()) {
                if (groupings.containsKey(uses.getQName())) {
                    uses.setGrouping(groupings.get(uses.getQName()));
                } else {
                    Grouping newG = ModelFactoryImpl.eINSTANCE.createGrouping();
                    newG.setName(uses.getQName());
                    uses.setGrouping(newG);
                    groupings.put(newG.getName(), newG);
                    if (drawExternalLinks) {
                        elements.put(newG, YangModelUIUtil.drawObject(newG, fp.getDiagramTypeProvider().getDiagram(), fp, 100, 100));
                    }
                }
            }
        }
    }
    public static void drawLines(List<Node> list, IFeatureProvider fp) {
        for (Node n : list) {
            if (YangModelUtil.hasConnection(n)) {
                drawLine(n, fp);
            }
            if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getContainingNode(), n)) {
                drawLines(((ContainingNode) n).getChildren(), fp);
            }
        }
    }
    
    public static void drawLine(Node n, IFeatureProvider fp) {
        Anchor start = YangModelUIUtil.getChopboxAnchor((AnchorContainer) elements.get(n));
        if (YangModelUtil.hasConnection(n)) {
            start = YangModelUIUtil.getBoxRelativeAnchor((AnchorContainer) elements.get(n));
        }
        if (null == start.getOutgoingConnections() || start.getOutgoingConnections().isEmpty()) {
            Anchor finish = null;
            if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getUses(), n)) {
                if (elements.containsKey(((Uses) n).getGrouping())) {
                    finish = YangModelUIUtil.getChopboxAnchor((AnchorContainer) elements.get(((Uses) n).getGrouping()));
                }
            }
            YangModelUIUtil.drawConnection(n, start, finish, fp);
        }
    }
   
}
