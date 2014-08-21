package com.cisco.yangide.ext.model.editor.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.AddBendpointContext;
import org.eclipse.graphiti.features.context.impl.LayoutContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.zest.layouts.InvalidLayoutConfiguration;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutBendPoint;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.LayoutRelationship;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.DirectedGraphLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalShift;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.VerticalLayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.BendPoint;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;
import org.eclipse.zest.layouts.exampleStructures.SimpleNode;

import com.cisco.yangide.ext.model.ContainingNode;
import com.cisco.yangide.ext.model.Node;
import com.cisco.yangide.ext.model.editor.util.connection.Position;
import com.cisco.yangide.ext.model.editor.util.connection.RectilinearAvoidObstaclesPathFinder;
import com.cisco.yangide.ext.model.editor.util.connection.RoutePath;

public class LayoutUtil {

    private LayoutUtil() {
        super();
    }

    public static final int DEFAULT_DIAGRAM_LAYOUT_TYPE = 13;
    public static final double DEFAULT_DIAGRAM_LAYOUT_V_SHIFT = 10;
    public static final double DEFAULT_SCREEN_WIDTH = 1000;

    /**
     * Used to keep track of the initial Connection locations for self connections<br/>
     * The self connections cannot be computed by the LayoutAlgorithmn but the Nodes will probably
     * be moved<br/>
     * So we need to recompute the bend points locations based on the offset of the Anchor from the
     * initial location
     * 
     * @return a {@link Map} of initial {@link Anchor} location {@link Point} per {@link Connection}
     * s
     */
    private static Map<Connection, Point> getSelfConnections(Diagram diagram) {
        IGaService gaService = Graphiti.getGaService();
        Map<Connection, Point> selves = new HashMap<Connection, Point>();
        EList<Connection> connections = diagram.getConnections();
        for (Connection connection : connections) {
            AnchorContainer source = connection.getStart().getParent();
            AnchorContainer target = connection.getEnd().getParent();
            if (source == target) {
                GraphicsAlgorithm p = source.getGraphicsAlgorithm();
                Point start = gaService.createPoint(p.getX(), p.getY());
                selves.put(connection, start);
            }
        }
        return selves;
    }

    /**
     * Reposition the bendpoints based on the offset from the initial {@link Anchor} location to the
     * new location
     * 
     * @param selves The {@link Map} of initial {@link Anchor} location {@link Point} per
     * {@link Connection}s
     */
    private static void adaptSelfBendPoints(Map<Connection, Point> selves) {
        for (Connection connection : selves.keySet()) {
            Point p = selves.get(connection);
            FreeFormConnection ffcon = (FreeFormConnection) connection;
            EList<Point> pointList = ffcon.getBendpoints();
            AnchorContainer source = connection.getStart().getParent();
            GraphicsAlgorithm start = source.getGraphicsAlgorithm();
            int deltaX = start.getX() - p.getX();
            int deltaY = start.getY() - p.getY();
            for (int i = 0; i < pointList.size(); i++) {
                Point bendPoint = pointList.get(i);
                int x = bendPoint.getX();
                bendPoint.setX(x + deltaX);
                int y = bendPoint.getY();
                bendPoint.setY(y + deltaY);
            }
        }
    }

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

        IGaService gaService = Graphiti.getGaService();
        for (LayoutRelationship relationship : connections) {
            SimpleRelationship rel = (SimpleRelationship) relationship;
            // Using FreeFormConnections with BendPoints, we reset them to the Zest computed
            // locations
            FreeFormConnection connection = (FreeFormConnection) rel.getGraphData();
            connection.getBendpoints().clear();
            LayoutBendPoint[] bendPoints = rel.getBendPoints();
            for (LayoutBendPoint bendPoint : bendPoints) {
                Double x = bendPoint.getX();
                Double y = bendPoint.getY();
                Point p = gaService.createPoint(x.intValue(), y.intValue());
                connection.getBendpoints().add(p);
            }
        }
    }

    /**
     * @return a {@link Map} of {@link SimpleNode} per {@link Shape}
     */
    private static Map<Shape, SimpleNode> getLayoutEntities(IFeatureProvider fp) {
        return getLayoutEntities(fp, fp.getDiagramTypeProvider().getDiagram(), null, new HashMap<Shape, SimpleNode>());
    }

    private static Map<Shape, SimpleNode> getLayoutEntities(IFeatureProvider fp, ContainerShape parent,
            SimpleNode entity, Map<Shape, SimpleNode> map) {
        EList<Shape> children = parent.getChildren();
        for (Shape shape : children) {
            if (null != fp.getBusinessObjectForPictogramElement(shape)) {
                GraphicsAlgorithm ga = shape.getGraphicsAlgorithm();
                SimpleNode currentEntity = entity;
                if (null == currentEntity) { // connect inner shapes with the elements on diagram
                    currentEntity = new SimpleNode(shape, ga.getX(), ga.getY(), ga.getWidth(), ga.getHeight());
                }
                map.put(shape, currentEntity);
                if (shape instanceof ContainerShape) {
                    getLayoutEntities(fp, (ContainerShape) shape, currentEntity, map);
                }
            }
        }
        return map;
    }

    private static List<SimpleNode> filterDiagramLayoutEntities(Map<Shape, SimpleNode> map) {
        List<SimpleNode> result = new ArrayList<SimpleNode>();
        for (Map.Entry<Shape, SimpleNode> entry : map.entrySet()) {
            if (entry.getKey().getContainer() instanceof Diagram) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    private static double[] getAreaSizeAndArrangeLayout(List<SimpleNode> entities, LayoutAlgorithm layout) {
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
        if (layout instanceof VerticalLayoutAlgorithm) {
            return new double[] { maxW + 2 * YangModelUIUtil.DEFAULT_V_ALIGN,
                    fullH + entities.size() * YangModelUIUtil.DEFAULT_V_ALIGN };
        }
        if (layout instanceof HorizontalLayoutAlgorithm) {
            return new double[] { fullW + entities.size() * YangModelUIUtil.DEFAULT_V_ALIGN,
                    maxH + 2 * YangModelUIUtil.DEFAULT_H_ALIGN };
        }
        if (layout instanceof YangDiagramLayoutAlgorithm) {
            ((YangDiagramLayoutAlgorithm) layout).setMaxElementSizes(maxW, maxH);
            ((YangDiagramLayoutAlgorithm) layout).setFullElementSizes(fullW, fullH);
            double h = Math.max(1, Math.ceil(entities.size() / (DEFAULT_SCREEN_WIDTH / maxW))) * maxH;
            return new double[] { DEFAULT_SCREEN_WIDTH, h };
        }
        Double n = Math.ceil(Math.sqrt(entities.size()));
        return new double[] { (maxW + YangModelUIUtil.DEFAULT_V_ALIGN) * n,
                (maxH + YangModelUIUtil.DEFAULT_H_ALIGN) * n };
    }

    /**
     * @param map a {@link Map} of {@link SimpleNode} per {@link Shape} - used to link
     * {@link SimpleRelationship} to source and target entities
     * @return the array of {@link LayoutRelationship}s to compute
     */
    private static LayoutRelationship[] getConnectionEntities(Diagram diagram, Map<Shape, SimpleNode> map) {
        List<LayoutRelationship> liste = new ArrayList<LayoutRelationship>();
        EList<Connection> connections = diagram.getConnections();
        for (Connection connection : connections) {

            String label = null;
            EList<ConnectionDecorator> decorators = connection.getConnectionDecorators();
            for (ConnectionDecorator decorator : decorators) {
                if (decorator.getGraphicsAlgorithm() instanceof Text) {
                    label = ((Text) decorator.getGraphicsAlgorithm()).getValue();
                }
            }

            // get the SimpleNode already created from the map:
            Shape source = (Shape) connection.getStart().getParent();
            SimpleNode sourceEntity = map.get(source);
            Shape target = (Shape) connection.getEnd().getParent();
            SimpleNode targetEntity = map.get(target);

            if (source != target) { // we don't add self relations to avoid Cycle errors
                SimpleRelationship relationship = new SimpleRelationship(sourceEntity, targetEntity, (source != target));
                relationship.setGraphData(connection);
                relationship.clearBendPoints();
                relationship.setLabel(label);
                FreeFormConnection ffcon = (FreeFormConnection) connection;

                EList<Point> pointList = ffcon.getBendpoints();
                List<LayoutBendPoint> bendPoints = new ArrayList<LayoutBendPoint>();
                for (int i = 0; i < pointList.size(); i++) {
                    Point point = pointList.get(i);
                    boolean isControlPoint = (i != 0) && (i != pointList.size() - 1);
                    LayoutBendPoint bendPoint = new BendPoint(point.getX(), point.getY(), isControlPoint);
                    bendPoints.add(bendPoint);
                }
                relationship.setBendPoints(bendPoints.toArray(new LayoutBendPoint[0]));
                liste.add(relationship);
                sourceEntity.addRelationship(relationship);
                targetEntity.addRelationship(relationship);
            }
        }
        return liste.toArray(new LayoutRelationship[0]);
    }

    /**
     * @param current
     * @return
     */
    private static LayoutAlgorithm getLayoutAlgorithmn(int current) {
        LayoutAlgorithm layout;
        int style = LayoutStyles.NO_LAYOUT_NODE_RESIZING;
        switch (current) {
        case 1:
            layout = new SpringLayoutAlgorithm(style); // SpringLayoutAlgorithmn
            break;
        case 2:
            layout = new TreeLayoutAlgorithm(style); // TreeLayoutAlgorithm
            break;
        case 3:
            layout = new GridLayoutAlgorithm(style); // GridLayoutAlgorithm
            break;
        case 4:
            layout = new HorizontalLayoutAlgorithm(style); // HorizontalLayoutAlgorithm
            break;
        case 5:
            layout = new HorizontalTreeLayoutAlgorithm(style); // HorizontalTreeLayoutAlgorithm
            break;
        case 6:
            layout = new VerticalLayoutAlgorithm(style); // VerticalLayoutAlgorithm
            break;
        case 7:
            layout = new RadialLayoutAlgorithm(style); // RadialLayoutAlgorithm
            break;
        case 8:
            layout = new DirectedGraphLayoutAlgorithm(style); // DirectedGraphLayoutAlgorithm
            break;
        case 9:
            layout = new CompositeLayoutAlgorithm(new LayoutAlgorithm[] { new DirectedGraphLayoutAlgorithm(style),
                    new HorizontalShift(style) }); // CompositeLayoutAlgorithm
                                                   // [DirectedGraphLayoutAlgorithm+HorizontalShift]
            break;
        case 10:
            layout = new CompositeLayoutAlgorithm(new LayoutAlgorithm[] { new SpringLayoutAlgorithm(style),
                    new HorizontalShift(style) }); // CompositeLayoutAlgorithm
                                                   // [SpringLayoutAlgorithm+HorizontalShift]
            break;
        case 11:
            layout = new CompositeLayoutAlgorithm(new LayoutAlgorithm[] { new RadialLayoutAlgorithm(style),
                    new HorizontalShift(style) }); // CompositeLayoutAlgorithm
                                                   // [RadialLayoutAlgorithm+HorizontalShift]
            break;
        case 12:
            layout = new HorizontalShift(style); // HorizontalShift
            break;
        case 13:
            layout = new YangDiagramLayoutAlgorithm(style); // YangDiagramLayoutAlgorithm
            break;
        default:
            layout = new CompositeLayoutAlgorithm(new LayoutAlgorithm[] { new TreeLayoutAlgorithm(style),
                    new HorizontalShift(style) }); // CompositeLayoutAlgorithm
                                                   // [TreeLayoutAlgorithm+HorizontalShift]
        }
        return layout;
    }

    /**
     * A {@link org.eclipse.zest.layouts.exampleStructures.SimpleRelationship} subclass used to hold
     * the Graphiti connection reference
     */
    private static class SimpleRelationship extends org.eclipse.zest.layouts.exampleStructures.SimpleRelationship {

        private Object graphData;

        public SimpleRelationship(LayoutEntity sourceEntity, LayoutEntity destinationEntity, boolean bidirectional) {
            super(sourceEntity, destinationEntity, bidirectional);
        }

        @Override
        public Object getGraphData() {
            return graphData;
        }

        @Override
        public void setGraphData(Object o) {
            this.graphData = o;
        }
    }
    
    private static class LayoutEntityHeightComparator implements Comparator<LayoutEntity> {

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
            return o1.getHeightInLayout() > o2.getHeightInLayout() ? 1 : o1.getHeightInLayout() == o2.getHeightInLayout() ? 0 : -1;
        }
        
    }

    private static class YangDiagramLayoutAlgorithm extends GridLayoutAlgorithm {
        protected double maxW = YangModelUIUtil.DEFAULT_WIDTH;
        protected double maxH = YangModelUIUtil.DEFAULT_COMPOSITE_HEIGHT;
        protected int cols;
        protected int rows;
        protected int numChildren;
        @SuppressWarnings("unused") protected double fullW = YangModelUIUtil.DEFAULT_WIDTH;
        protected double fullH = YangModelUIUtil.DEFAULT_COMPOSITE_HEIGHT;
        private static final int OFFSET = 20;

        public YangDiagramLayoutAlgorithm(int styles) {
            super(styles);
            setComparator(new LayoutEntityHeightComparator());
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
            start = new org.eclipse.draw2d.geometry.Point(source.x + source.width, 
                    Graphiti.getLayoutService().getLocationRelativeToDiagram(connection.getStart()).getY() - 3);
            if (source.x < target.x) {                
                end = new org.eclipse.draw2d.geometry.Point(target.x, target.y + target.height / 2);
            } else {
                end = new org.eclipse.draw2d.geometry.Point(target.x + target.width, target.y + target.height / 2);
            }
            RoutePath route = finder.find(Position.create(map.get(connection.getStart().getReferencedGraphicsAlgorithm()), start),
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

    public static void layoutDiagram(IFeatureProvider fp, int type) {
        // get a map of the self connection anchor locations
        final Map<Connection, Point> selves = getSelfConnections(fp.getDiagramTypeProvider().getDiagram());

        // get the chosen LayoutAlgorithmn instance
        LayoutAlgorithm layoutAlgorithm = getLayoutAlgorithmn(type);

        if (layoutAlgorithm != null) {
            try {

                // Get the map of SimpleNode per Shapes
                Map<Shape, SimpleNode> map = getLayoutEntities(fp);

                // Get the array of Connection LayoutRelationships
                LayoutRelationship[] connections = getConnectionEntities(fp.getDiagramTypeProvider().getDiagram(), map);

                // Setup the array of Shape LayoutEntity
                List<SimpleNode> diagramEntities = filterDiagramLayoutEntities(map);
                LayoutEntity[] entities = diagramEntities.toArray(new LayoutEntity[0]);
                double[] preferedSize = getAreaSizeAndArrangeLayout(diagramEntities, layoutAlgorithm);

                // Apply the LayoutAlgorithmn
                layoutAlgorithm
                        .applyLayout(entities, connections, 0, 0, preferedSize[0], preferedSize[1], false, false);

                // Update the Graphiti Shapes and Connections locations
                updateGraphCoordinates(entities, connections);

                // Reposition the self connections bendpoints:
                adaptSelfBendPoints(selves);

            } catch (InvalidLayoutConfiguration e) {
                e.printStackTrace();
            }
        }
        layoutDiagramConnections(fp);
    }

    public static void layoutDiagram(IFeatureProvider fp) {
        layoutDiagram(fp, DEFAULT_DIAGRAM_LAYOUT_TYPE);
    }
    
    public static void layoutContainerShapeHeader(ContainerShape cs, IFeatureProvider fp) {
        //int width = cs.getGraphicsAlgorithm().getWidth();
        Shape text = YangModelUIUtil.getBusinessObjectPropShape(cs, PropertyUtil.OBJECT_HEADER_TEXT_SHAPE_KEY);
        int textWidth = 0;
        if (null != text && text.getGraphicsAlgorithm() instanceof Text) {
            Text ga = (Text) text.getGraphicsAlgorithm();
            textWidth = GraphitiUi.getUiLayoutService().calculateTextSize(ga.getValue(), ga.getStyle().getFont()).getWidth();
            ga.setWidth(textWidth);            
        }
        Shape type = YangModelUIUtil.getBusinessObjectPropShape(cs, PropertyUtil.BUSINESS_OBJECT_TYPE_SHAPE_KEY);
        if (null != type && type.getGraphicsAlgorithm() instanceof Text) {
            Text ga = (Text) type.getGraphicsAlgorithm();
            ga.setX(textWidth + YangModelUIUtil.DEFAULT_V_ALIGN + YangModelUIUtil.DEFAULT_TEXT_HEIGHT);
            //ga.setWidth(Math.max(0, width - textWidth));
        }
        
    }

    public static void layoutContainerShape(ContainerShape cs, IFeatureProvider fp) {
        int y = YangModelUIUtil.DEFAULT_TEXT_HEIGHT;
        int x = 0;
        
        layoutContainerShapeHeader(cs, fp);
        if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getContainingNode(), fp.getBusinessObjectForPictogramElement(cs))) {
            for (Node child : ((ContainingNode) fp.getBusinessObjectForPictogramElement(cs)).getChildren()) {
                PictogramElement pe = YangModelUIUtil.getBusinessObjectShape(fp, child);
                if (cs.getChildren().contains(pe)) {
                    layoutPictogramElement(pe, fp);
                    pe.getGraphicsAlgorithm().setX(YangModelUIUtil.DEFAULT_V_ALIGN);
                    pe.getGraphicsAlgorithm().setY(y + YangModelUIUtil.DEFAULT_H_ALIGN);
                    pe.getGraphicsAlgorithm().setWidth(
                            cs.getGraphicsAlgorithm().getWidth() - 2 * YangModelUIUtil.DEFAULT_V_ALIGN);
                    y = pe.getGraphicsAlgorithm().getY() + pe.getGraphicsAlgorithm().getHeight();
                    if (x < pe.getGraphicsAlgorithm().getWidth()) {
                        x = pe.getGraphicsAlgorithm().getWidth();
                    }
                }
            }
        }

        if (x + 2 * YangModelUIUtil.DEFAULT_V_ALIGN > cs.getGraphicsAlgorithm().getWidth()) {
            cs.getGraphicsAlgorithm().setWidth(x + 2 * YangModelUIUtil.DEFAULT_V_ALIGN);
        }
        if (y + 2 * YangModelUIUtil.DEFAULT_H_ALIGN > cs.getGraphicsAlgorithm().getHeight()) {
            cs.getGraphicsAlgorithm().setHeight(y + 2 * YangModelUIUtil.DEFAULT_H_ALIGN);
        }
        Polyline line = YangModelUIUtil.getPolyline(cs);
        if (null != line) {
            EList<Point> points = line.getPoints();
            if (1 < points.size()) {
                points.get(1).setX(cs.getGraphicsAlgorithm().getWidth());
            }
        }
        
        GraphicsAlgorithm number = YangModelUIUtil.getObjectNumberElement(cs);
        if (null != number) {
            number.setX(cs.getGraphicsAlgorithm().getWidth() - YangModelUIUtil.DEFAULT_OBJECT_NUMBER_IND - 10);
        }
        layoutDiagramConnections(fp);
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
