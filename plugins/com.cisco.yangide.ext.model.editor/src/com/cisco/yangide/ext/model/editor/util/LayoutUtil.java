package com.cisco.yangide.ext.model.editor.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.AddBendpointContext;
import org.eclipse.graphiti.features.context.impl.LayoutContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.zest.layouts.InvalidLayoutConfiguration;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.LayoutRelationship;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;
import org.eclipse.zest.layouts.exampleStructures.SimpleNode;

import com.cisco.yangide.ext.model.ContainingNode;
import com.cisco.yangide.ext.model.Node;
import com.cisco.yangide.ext.model.editor.diagram.EditorFeatureProvider;
import com.cisco.yangide.ext.model.editor.util.connection.Position;
import com.cisco.yangide.ext.model.editor.util.connection.RectilinearAvoidObstaclesPathFinder;
import com.cisco.yangide.ext.model.editor.util.connection.RoutePath;

public class LayoutUtil {

    private LayoutUtil() {
        super();
    }

    public static final double DEFAULT_DIAGRAM_LAYOUT_V_SHIFT = 10;

    /**
     * Reposition the Graphiti {@link PictogramElement}s and {@link Connection}s based on the Zest
     * {@link LayoutAlgorithm} computed locations
     * 
     * @param entities
     * @param connections
     */
    private static void updateGraphCoordinates(LayoutEntity[] entities, LayoutRelationship[] connections) {
        for (LayoutEntity entity : entities) {
            SimpleNode node = (SimpleNode) entity;
            Shape shape = (Shape) node.getRealObject();
            Double x = node.getX();
            Double y = node.getY();
            shape.getGraphicsAlgorithm().setX(x.intValue());
            shape.getGraphicsAlgorithm().setY(y.intValue());
            Double width = node.getWidth();
            Double height = node.getHeight();
            shape.getGraphicsAlgorithm().setWidth(width.intValue());
            shape.getGraphicsAlgorithm().setHeight(height.intValue());
        }
    }

    /**
     * @return a {@link Map} of {@link SimpleNode} per {@link Shape}
     */
    private static List<SimpleNode> getLayoutEntities(IFeatureProvider fp) {
        return getLayoutEntities(fp, fp.getDiagramTypeProvider().getDiagram());
    }

    private static List<SimpleNode> getLayoutEntities(IFeatureProvider fp, ContainerShape parent) {
        EList<Shape> children = parent.getChildren();
        List<SimpleNode> result = new ArrayList<SimpleNode>();
        for (Shape shape : children) {
            Object bo = fp.getBusinessObjectForPictogramElement(shape);
            if (null != bo) {
                GraphicsAlgorithm ga = shape.getGraphicsAlgorithm();
                int pos = YangModelUtil.getPositionInParent(fp.getBusinessObjectForPictogramElement(fp.getDiagramTypeProvider().getDiagram()), bo);
                SimpleNode currentEntity = new YangSimpleNode(shape, ga.getX(), ga.getY(), ga.getWidth(), ga.getHeight(), pos);
                result.add(currentEntity);
            }
        }
        return result;
    }

    private static double[] getAreaSizeAndArrangeLayout(List<SimpleNode> entities, LayoutAlgorithm layout, int width,
            int height) {
        double maxW = 0D;
        double maxH = 0D;
        double fullW = 0D;
        double fullH = 0D;

        for (SimpleNode node : entities) {
            fullW += node.getWidth();
            if (maxW < node.getWidth()) {
                maxW = node.getWidth();
            }
            fullH += node.getHeight();
            if (maxH < node.getHeight()) {
                maxH = node.getHeight();
            }
        }
        ((YangDiagramLayoutAlgorithm) layout).setMaxElementSizes(maxW, maxH);
        ((YangDiagramLayoutAlgorithm) layout).setFullElementSizes(fullW, fullH);
        double h = Math.max(1, Math.ceil(entities.size() / (width / maxW))) * maxH;
        return new double[] { width, h };
    }

    public static class YangSimpleNode extends SimpleNode {
        private int pos;

        public YangSimpleNode(Object realObject, double x, double y, double width, double height, int pos) {
            super(realObject, x, y, width, height);
            this.pos = pos;
        }
        
        public int getPositionInParent() {
            return pos;
        }
        
    }
    
    public static class YangCompositeSimpleNode {
        private int pos;
        private List<YangCompositeSimpleNode> children;
        private int x, y, width, height;
        private ContainerShape realObject;

        public YangCompositeSimpleNode(ContainerShape realObject, int pos) {
            this.realObject = realObject;
            this.x = realObject.getGraphicsAlgorithm().getX();
            this.y = realObject.getGraphicsAlgorithm().getY();
            this.height = realObject.getGraphicsAlgorithm().getHeight();
            this.width = realObject.getGraphicsAlgorithm().getWidth();
            this.pos = pos;
        }
        
        public int getPositionInParent() {
            return pos;
        }

        public List<YangCompositeSimpleNode> getChildren() {
            if (null == children) {
                children = new ArrayList<YangCompositeSimpleNode>();
            }
            return children;
        }

        public void addChild(YangCompositeSimpleNode child) {
            getChildren().add(child);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
        
        public void setLocation(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public void setSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
        
        public void updateRealObject(IFeatureProvider fp) {
            realObject.getGraphicsAlgorithm().setX(x);
            realObject.getGraphicsAlgorithm().setY(y);
            realObject.getGraphicsAlgorithm().setHeight(height);
            realObject.getGraphicsAlgorithm().setWidth(width);
            layoutContainerShapeHeader(realObject, fp);
            for (YangCompositeSimpleNode child : getChildren()) {
                child.updateRealObject(fp);
            }
        }
        
    }
    
    /*private static class LayoutEntityHeightComparator implements Comparator<LayoutEntity> {

        @Override
        public int compare(LayoutEntity o1, LayoutEntity o2) {
            if (null == o1 && null == o2) {
                return 0;
            }
            if (null != o1 && null == o2) {
                return 1;
            }
            if (null == o1 && null != o2) {
                return -1;
            }
            return o1.getHeightInLayout() > o2.getHeightInLayout() ? 1 : o1.getHeightInLayout() == o2
                    .getHeightInLayout() ? 0 : -1;
        }

    }*/
    
    private static class LayoutEntityOrderComparator implements Comparator<LayoutEntity> {

        @Override
        public int compare(LayoutEntity o1, LayoutEntity o2) {
            if (null == o1 && null == o2) {
                return 0;
            }
            if (null != o1 && null == o2) {
                return 1;
            }
            if (null == o1 && null != o2) {
                return -1;
            }
            if (o1 instanceof YangSimpleNode && o2 instanceof YangSimpleNode) {
                return ((YangSimpleNode) o1).getPositionInParent() > ((YangSimpleNode) o2).getPositionInParent() ? 1 : 
                    ((YangSimpleNode) o1).getPositionInParent() == ((YangSimpleNode) o2).getPositionInParent() ? 0 : -1;
            }
            return o1.getHeightInLayout() > o2.getHeightInLayout() ? 1 : o1.getHeightInLayout() == o2
                    .getHeightInLayout() ? 0 : -1;
        }

    }
    
    protected static int layoutCompositeSimpleNode(YangCompositeSimpleNode element, int boundsWidth) {

        int y = YangModelUIUtil.DEFAULT_TEXT_HEIGHT;
        for (YangCompositeSimpleNode sn : element.getChildren()) {
            int elementHeight = layoutCompositeSimpleNode(sn, Math.max(0, boundsWidth - 2 * YangModelUIUtil.DEFAULT_V_ALIGN)) + 2 * YangModelUIUtil.DEFAULT_H_ALIGN;
            int xmove = YangModelUIUtil.DEFAULT_V_ALIGN;
            int ymove = y + YangModelUIUtil.DEFAULT_H_ALIGN;
            sn.setLocation(xmove, ymove);
            sn.setSize(Math.max(0, boundsWidth - 2 * YangModelUIUtil.DEFAULT_V_ALIGN), Math.max(elementHeight, sn.getHeight()));
            y = y + sn.getHeight() + YangModelUIUtil.DEFAULT_H_ALIGN;
            
        }
        element.setSize(boundsWidth, Math.max(y  + 2 * YangModelUIUtil.DEFAULT_H_ALIGN, element.getHeight()));
        return y;
    }


    private static class YangDiagramLayoutAlgorithm extends GridLayoutAlgorithm {
        protected double maxW = YangModelUIUtil.DEFAULT_WIDTH;
        protected double maxH = YangModelUIUtil.DEFAULT_COMPOSITE_HEIGHT;
        protected int cols;
        protected int rows;
        protected int numChildren;
        @SuppressWarnings("unused")
        protected double fullW = YangModelUIUtil.DEFAULT_WIDTH;
        protected double fullH = YangModelUIUtil.DEFAULT_COMPOSITE_HEIGHT;
        private static final int OFFSET = 40;

        public YangDiagramLayoutAlgorithm(int styles) {
            super(styles);
            setComparator(new LayoutEntityOrderComparator());
        }

        @Override
        protected int[] calculateNumberOfRowsAndCols(int numChildren, double boundX, double boundY, double boundWidth,
                double boundHeight) {
            cols = Math.max(1, (int) Math.min(numChildren, boundWidth / (getMaxW() + OFFSET)));
            rows = Math.max(1, (int) Math.ceil(numChildren / cols)) + 1;
            this.numChildren = numChildren;

            return new int[] { cols, rows };
        }

        @Override
        protected double[] calculateNodeSize(double colWidth, double rowHeight) {
            return new double[] { getMaxW(), getMaxH() };
        }

        @Override
        protected synchronized void applyLayoutInternal(InternalNode[] entitiesToLayout,
                InternalRelationship[] relationshipsToConsider, double boundsX, double boundsY, double boundsWidth,
                double boundsHeight) {
            int index = 0;
            int totalProgress = cols + 2;
            double averageH = fullH / cols;
            double leftH = fullH;
            if (cols >= numChildren) {
                leftH = 0;
            }
            for (int j = 0; j < cols; j++) {
                double y = 0;
                do { // set at least one element in the row
                    if (index < numChildren) {
                        InternalNode sn = entitiesToLayout[index++];
                        double xmove = boundsX + j * (getMaxW() + OFFSET) + OFFSET;
                        double ymove = boundsY + y + OFFSET;
                        sn.setInternalLocation(xmove, ymove);
                        y += sn.getHeightInLayout() + OFFSET;
                        leftH -= sn.getHeightInLayout();
                    }
                } while ((j == cols - 1 || leftH / (cols - j - 1) > averageH) && index < numChildren);
                fireProgressEvent(2 + j, totalProgress);
            }
            updateLayoutLocations(entitiesToLayout);
            fireProgressEvent(totalProgress, totalProgress);
        }

        public double getMaxW() {
            return maxW;
        }

        public void setMaxElementSizes(double maxW, double maxH) {
            this.maxW = maxW;
            this.maxH = maxH;
        }

        public double getMaxH() {
            return maxH;
        }

        public void setFullElementSizes(double fullW, double fullH) {
            this.fullW = fullW;
            this.fullH = fullH;
        }

    }

    private static void setShapes(Shape cs, Rectangle r, Map<GraphicsAlgorithm, Rectangle> map) {
        map.put(cs.getGraphicsAlgorithm(), r);
        if (cs instanceof ContainerShape) { // connect inner shapes with outer rectangles on diagram
            for (Shape shape : ((ContainerShape) cs).getChildren()) {
                setShapes(shape, r, map);
            }
        }
    }

    public static void layoutDiagramConnections(IFeatureProvider fp) {
        RectilinearAvoidObstaclesPathFinder finder = new RectilinearAvoidObstaclesPathFinder();
        Map<GraphicsAlgorithm, Rectangle> map = new HashMap<GraphicsAlgorithm, Rectangle>();
        for (Shape shape : fp.getDiagramTypeProvider().getDiagram().getChildren()) {
            Rectangle r = new Rectangle(shape.getGraphicsAlgorithm().getX(), shape.getGraphicsAlgorithm().getY(), shape
                    .getGraphicsAlgorithm().getWidth(), shape.getGraphicsAlgorithm().getHeight());
            finder.addObstacle(r);
            setShapes(shape, r, map);
        }
        for (Connection connection : fp.getDiagramTypeProvider().getDiagram().getConnections()) {
            // remove all bendpoints
            ((FreeFormConnection) connection).getBendpoints().clear();
            org.eclipse.draw2d.geometry.Point start, end;
            Rectangle source = map.get(connection.getStart().getReferencedGraphicsAlgorithm());
            Rectangle target = map.get(connection.getEnd().getReferencedGraphicsAlgorithm());

            // start always with right side because of box relative anchor
            start = new org.eclipse.draw2d.geometry.Point(source.x + source.width, Graphiti.getLayoutService()
                    .getLocationRelativeToDiagram(connection.getStart()).getY() - 3);
            if (source.x < target.x) {
                end = new org.eclipse.draw2d.geometry.Point(target.x, target.y + target.height / 2);
            } else {
                end = new org.eclipse.draw2d.geometry.Point(target.x + target.width, target.y + target.height / 2);
            }
            RoutePath route = finder.find(
                    Position.create(map.get(connection.getStart().getReferencedGraphicsAlgorithm()), start),
                    Position.create(map.get(connection.getEnd().getReferencedGraphicsAlgorithm()), end), false);
            if (null != route && null != route.path) {
                for (int i = 0; i < route.path.size(); i++) {
                    AddBendpointContext context = new AddBendpointContext((FreeFormConnection) connection,
                            route.path.getPoint(i).x, route.path.getPoint(i).y, i);
                    fp.getAddBendpointFeature(context).addBendpoint(context);
                }
            }
        }
    }

    public static void layoutDiagram(IFeatureProvider fp) {

        // get the chosen LayoutAlgorithmn instance
        LayoutAlgorithm layoutAlgorithm = new YangDiagramLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);

        if (layoutAlgorithm != null) {
            try {
                // Get the array of Connection LayoutRelationships
                LayoutRelationship[] connections = new LayoutRelationship[0];//getConnectionEntities(fp.getDiagramTypeProvider().getDiagram(), map);

                // Setup the array of Shape LayoutEntity
                List<SimpleNode> diagramEntities = getLayoutEntities(fp);
                LayoutEntity[] entities = diagramEntities.toArray(new LayoutEntity[0]);
                int diagramWidth = ((EditorFeatureProvider) fp).getDiagramWidth();
                int diagramHeight = ((EditorFeatureProvider) fp).getDiagramHeight();
                double[] preferedSize = getAreaSizeAndArrangeLayout(diagramEntities, layoutAlgorithm, diagramWidth,
                        diagramHeight);

                // Apply the LayoutAlgorithmn
                layoutAlgorithm
                        .applyLayout(entities, connections, 0, 0, preferedSize[0], preferedSize[1], false, false);

                // Update the Graphiti Shapes and Connections locations
                updateGraphCoordinates(entities, connections);

            } catch (InvalidLayoutConfiguration e) {
                e.printStackTrace();
            }
        }
        layoutDiagramConnections(fp);
    }

    public static void layoutContainerShapeHeader(ContainerShape cs, IFeatureProvider fp) {
        GraphicsAlgorithm text = YangModelUIUtil.getObjectPropGA(cs, PropertyUtil.OBJECT_HEADER_TEXT_SHAPE_KEY);
        GraphicsAlgorithm type = YangModelUIUtil.getObjectPropGA(cs, PropertyUtil.BUSINESS_OBJECT_TYPE_SHAPE_KEY);
        GraphicsAlgorithm number = YangModelUIUtil.getObjectPropGA(cs, PropertyUtil.OBJECT_NUMBER_SHAPE_KEY);
        int textWidth = Math.max(0, cs.getGraphicsAlgorithm().getWidth() - YangModelUIUtil.DEFAULT_TEXT_HEIGHT);
        if (null != number) {
            IDimension dim = GraphitiUi.getUiLayoutService().calculateTextSize(((Text) number).getValue(), number.getStyle().getFont());
            number.setX(Math.max(0, cs.getGraphicsAlgorithm().getWidth() - YangModelUIUtil.DEFAULT_OBJECT_NUMBER_IND - dim.getWidth()));
            textWidth = Math.max(0, textWidth - number.getWidth());
        }
        int typeWidth = 0;
        if (null != type && type instanceof Text) {
            typeWidth = GraphitiUi.getUiLayoutService().calculateTextSize(((Text) type).getValue(), type.getStyle().getFont()).getWidth();
        }
        int nameWidth = 0;
        if (null != text && text instanceof Text) {            
            nameWidth = GraphitiUi.getUiLayoutService().calculateTextSize(((Text) text).getValue(), text.getStyle().getFont()).getWidth();
            if (0 != typeWidth && typeWidth + nameWidth + YangModelUIUtil.DEFAULT_H_ALIGN > textWidth) {
                nameWidth = (int) Math.min(nameWidth, (0.5 * Math.max(0, textWidth)));
            }
            text.setWidth(Math.min(nameWidth, textWidth));
        }
        
        if (null != type && type instanceof Text) {
            Text ga = (Text) type;
            ga.setX(nameWidth + YangModelUIUtil.DEFAULT_TEXT_HEIGHT + YangModelUIUtil.DEFAULT_H_ALIGN);
            ga.setWidth(Math.max(0, textWidth - ga.getX()));
        }      
        
        Polyline line = YangModelUIUtil.getPolyline(cs);
        if (null != line) {
            EList<Point> points = line.getPoints();
            if (1 < points.size()) {
                points.get(1).setX(cs.getGraphicsAlgorithm().getWidth());
            }
        }
        
    }

    public static void layoutContainerShapeVertical(ContainerShape cs, IFeatureProvider fp) {
        int y = YangModelUIUtil.DEFAULT_TEXT_HEIGHT;
        Object bo = fp.getBusinessObjectForPictogramElement(cs);
        if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getContainingNode(), bo)) {
            for (Node child : ((ContainingNode) bo).getChildren()) {
                PictogramElement pe = YangModelUIUtil.getBusinessObjectShape(fp, child);
                if (cs.getChildren().contains(pe)) {
                    if (pe instanceof ContainerShape) {
                        layoutContainerShapeVertical((ContainerShape) pe, fp);
                    }
                    pe.getGraphicsAlgorithm().setY(y + YangModelUIUtil.DEFAULT_H_ALIGN);
                    y = pe.getGraphicsAlgorithm().getY() + pe.getGraphicsAlgorithm().getHeight();
                }
            }
        }
        if (y + 2 * YangModelUIUtil.DEFAULT_H_ALIGN > cs.getGraphicsAlgorithm().getHeight()) {
            cs.getGraphicsAlgorithm().setHeight(y + 2 * YangModelUIUtil.DEFAULT_H_ALIGN);
        }
    }

    public static void layoutContainerShapeHorizontal(ContainerShape cs, IFeatureProvider fp) {
        if (!(cs instanceof Diagram)) {
            layoutContainerShapeHeader(cs, fp);
            for (Shape shape : cs.getChildren()) {
                if (shape instanceof ContainerShape) {
                    layoutContainerShapeHorizontal((ContainerShape) shape, fp);
                }
                if (shape.getGraphicsAlgorithm().getX() + shape.getGraphicsAlgorithm().getWidth() + 2
                        * YangModelUIUtil.DEFAULT_H_ALIGN > cs.getGraphicsAlgorithm().getWidth()) {
                    cs.getGraphicsAlgorithm().setWidth(
                            shape.getGraphicsAlgorithm().getX() + shape.getGraphicsAlgorithm().getWidth() + 2
                                    * YangModelUIUtil.DEFAULT_H_ALIGN);
                }
            }
        }
    }
    
    protected static YangCompositeSimpleNode createCompositeSimpleNode(ContainerShape cs, IFeatureProvider fp) {
        Object bo = fp.getBusinessObjectForPictogramElement(cs);
        YangCompositeSimpleNode result = new YangCompositeSimpleNode(cs, YangModelUtil.getPositionInParent(fp.getBusinessObjectForPictogramElement(cs.getContainer()), bo));
        for (Shape shape : YangModelUIUtil.filterBusinessObjectShapes(cs.getChildren())) {
            if (shape instanceof ContainerShape) {
                result.addChild(createCompositeSimpleNode((ContainerShape) shape, fp));
            }
        }
        return result;
    }

    public static void layoutContainerShape(ContainerShape cs, IFeatureProvider fp, boolean layoutConnections) {
        YangCompositeSimpleNode node = createCompositeSimpleNode(cs, fp);
        layoutCompositeSimpleNode(node, cs.getGraphicsAlgorithm().getWidth());
        node.updateRealObject(fp);
        // layoutContainerShapeHorizontal(cs, fp);
        /*layoutContainerShapeHeader(cs, fp);
        Object bo = fp.getBusinessObjectForPictogramElement(cs);
        if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getContainingNode(), bo)) {
            for (Node child : ((ContainingNode) bo).getChildren()) {
                PictogramElement pe = YangModelUIUtil.getBusinessObjectShape(fp, child);
                if (cs.getChildren().contains(pe)) {
                    if (pe instanceof ContainerShape) {
                        layoutContainerShapeVertical((ContainerShape) pe, fp);

                    }
                    pe.getGraphicsAlgorithm().setX(YangModelUIUtil.DEFAULT_V_ALIGN);
                    pe.getGraphicsAlgorithm().setY(y + YangModelUIUtil.DEFAULT_H_ALIGN);
                    pe.getGraphicsAlgorithm().setWidth(
                            cs.getGraphicsAlgorithm().getWidth() - 2 * YangModelUIUtil.DEFAULT_V_ALIGN);
                    y = pe.getGraphicsAlgorithm().getY() + pe.getGraphicsAlgorithm().getHeight();
                    if (x < pe.getGraphicsAlgorithm().getWidth()) {
                        x = pe.getGraphicsAlgorithm().getWidth();
                    }
                    layoutPictogramElement(pe, fp);
                }
            }
        }
        if (y + 2 * YangModelUIUtil.DEFAULT_H_ALIGN > cs.getGraphicsAlgorithm().getHeight()) {
            cs.getGraphicsAlgorithm().setHeight(y + 2 * YangModelUIUtil.DEFAULT_H_ALIGN);
        }*/
        if (layoutConnections) {
            layoutDiagramConnections(fp);
        }
    }

    /**
     * calls layout feature for {@link PictogramElement}
     * 
     * @param pe
     * @param fp
     */
    public static void layoutPictogramElement(PictogramElement pe, IFeatureProvider fp) {
        LayoutContext context = new LayoutContext(pe);
        fp.layoutIfPossible(context);
    }

}
