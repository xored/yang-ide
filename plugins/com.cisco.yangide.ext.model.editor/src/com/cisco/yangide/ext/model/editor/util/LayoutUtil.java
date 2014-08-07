package com.cisco.yangide.ext.model.editor.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.LayoutContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
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
import org.eclipse.zest.layouts.exampleStructures.SimpleNode;

public class LayoutUtil {
    
    private LayoutUtil() {
        super();
    }
    
    public static final int DEFAULT_DIAGRAM_LAYOUT_TYPE = 11; //CompositeLayoutAlgorithm [RadialLayoutAlgorithm+HorizontalShift]

    /**
     * Used to keep track of the initial Connection locations for self connections<br/>
     * The self connections cannot be computed by the LayoutAlgorithmn but the Nodes will probably be moved<br/>
     * So we need to recompute the bend points locations based on the offset of the Anchor from the initial location
     * 
     * @return a {@link Map} of initial {@link Anchor} location {@link Point} per {@link Connection}s
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
     * Reposition the bendpoints based on the offset from the initial {@link Anchor} location to the new location
     * 
     * @param selves
     *            The {@link Map} of initial {@link Anchor} location {@link Point} per {@link Connection}s
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
     * Reposition the Graphiti {@link PictogramElement}s and {@link Connection}s based on the
     * Zest {@link LayoutAlgorithm} computed locations
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
            // Using FreeFormConnections with BendPoints, we reset them to the Zest computed locations
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
    private static Map<Shape, SimpleNode> getLayoutEntities(IFeatureProvider fp, ContainerShape parent, SimpleNode entity, Map<Shape, SimpleNode> map) {
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

    /**
     * @param map
     *            a {@link Map} of {@link SimpleNode} per {@link Shape} - used to link {@link SimpleRelationship} to source and target entities
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
            layout = new SpringLayoutAlgorithm(style); //SpringLayoutAlgorithmn
            break;
        case 2:
            layout = new TreeLayoutAlgorithm(style); //TreeLayoutAlgorithm
            break;
        case 3:
            layout = new GridLayoutAlgorithm(style); //GridLayoutAlgorithm
            break;
        case 4:
            layout = new HorizontalLayoutAlgorithm(style); //HorizontalLayoutAlgorithm
            break;
        case 5:
            layout = new HorizontalTreeLayoutAlgorithm(style); //HorizontalTreeLayoutAlgorithm
            break;
        case 6:
            layout = new VerticalLayoutAlgorithm(style); //VerticalLayoutAlgorithm
            break;
        case 7:
            layout = new RadialLayoutAlgorithm(style); //RadialLayoutAlgorithm
            break;
        case 8:
            layout = new DirectedGraphLayoutAlgorithm(style); //DirectedGraphLayoutAlgorithm
            break;
        case 9:
            layout = new CompositeLayoutAlgorithm(new LayoutAlgorithm[]{new DirectedGraphLayoutAlgorithm(style), new HorizontalShift(style)}); //CompositeLayoutAlgorithm [DirectedGraphLayoutAlgorithm+HorizontalShift]
            break;
        case 10:
            layout = new CompositeLayoutAlgorithm(new LayoutAlgorithm[]{new SpringLayoutAlgorithm(style), new HorizontalShift(style)}); //CompositeLayoutAlgorithm [SpringLayoutAlgorithm+HorizontalShift]
            break;
        case 11:
            layout = new CompositeLayoutAlgorithm(new LayoutAlgorithm[]{new RadialLayoutAlgorithm(style), new HorizontalShift(style)}); //CompositeLayoutAlgorithm [RadialLayoutAlgorithm+HorizontalShift]
            break;
        case 12:
            layout = new HorizontalShift(style); //HorizontalShift
            break;
        default:
            layout = new CompositeLayoutAlgorithm(new LayoutAlgorithm[]{new TreeLayoutAlgorithm(style), new HorizontalShift(style)}); //CompositeLayoutAlgorithm [TreeLayoutAlgorithm+HorizontalShift]
        }
        return layout;
    }

    /**
     * A {@link org.eclipse.zest.layouts.exampleStructures.SimpleRelationship} subclass
     * used to hold the Graphiti connection reference
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
    
    private static class DomainCompositeObjectLayoutAlgorithm extends VerticalLayoutAlgorithm {
        public DomainCompositeObjectLayoutAlgorithm() {
            super(LayoutStyles.ENFORCE_BOUNDS);
            rowPadding = 2;
        }

        @Override
        protected double[] calculateNodeSize(double colWidth, double rowHeight) {
            return new double[] {colWidth - 2 * YangModelUIUtil.DEFAULT_V_ALIGN, rowHeight - 2 * YangModelUIUtil.DEFAULT_H_ALIGN};
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
                LayoutEntity[] entities = map.values().toArray(new LayoutEntity[0]);

                // Get the diagram GraphicsAlgorithmn (we need the graph dimensions)
                GraphicsAlgorithm ga = fp.getDiagramTypeProvider().getDiagram().getGraphicsAlgorithm();

                // Apply the LayoutAlgorithmn
                layoutAlgorithm.applyLayout(entities, connections, 0, 0, ga.getWidth(), ga.getHeight(), false, false);

                // Update the Graphiti Shapes and Connections locations
                updateGraphCoordinates(entities, connections);

                // Reposition the self connections bendpoints:
                adaptSelfBendPoints(selves);

            } catch (InvalidLayoutConfiguration e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void layoutDiagram(IFeatureProvider fp) {
        layoutDiagram(fp, DEFAULT_DIAGRAM_LAYOUT_TYPE);
    }
    
    private static List<LayoutEntity> getLayoutEntities(ContainerShape cs, IFeatureProvider fp) {
        List<LayoutEntity> result= new ArrayList<LayoutEntity>();
        for (Shape shape : cs.getChildren()) {
            if (null != fp.getBusinessObjectForPictogramElement(shape) && fp.getBusinessObjectForPictogramElement(shape) != fp.getBusinessObjectForPictogramElement(cs)) {
                GraphicsAlgorithm ga = shape.getGraphicsAlgorithm();
                result.add(new SimpleNode(shape, ga.getX(), ga.getY(), ga.getWidth(), ga.getHeight()));
            }
        }
        return result;
    }
    
    public static void layoutContainerShape(ContainerShape cs, IFeatureProvider fp) {
        /*EList<Shape> elements = cs.getChildren();
        YangModelUIUtil.sortPictogramElements(elements);
        int y = 0;
        int x = 0;
        Polyline line = null;
        for (Shape sh : elements) {
            layoutPictogramElement(sh, fp);
            if (sh.getGraphicsAlgorithm() instanceof Polyline) {
                line = (Polyline) sh.getGraphicsAlgorithm();
            }
            if (null != fp.getBusinessObjectForPictogramElement(sh) && fp.getBusinessObjectForPictogramElement(sh) != fp.getBusinessObjectForPictogramElement(cs)) {
                sh.getGraphicsAlgorithm().setX(2 * YangModelUIUtil.DEFAULT_V_ALIGN);
                sh.getGraphicsAlgorithm().setY(y + YangModelUIUtil.DEFAULT_H_ALIGN);
            }
            if (x < sh.getGraphicsAlgorithm().getWidth()) {
                x = sh.getGraphicsAlgorithm().getWidth();
            }
            y = sh.getGraphicsAlgorithm().getY() + sh.getGraphicsAlgorithm().getHeight();
        }
        if (x + 3 * YangModelUIUtil.DEFAULT_V_ALIGN > cs.getGraphicsAlgorithm().getWidth()) {
            cs.getGraphicsAlgorithm().setWidth(x + 3 * YangModelUIUtil.DEFAULT_V_ALIGN);
        }
        if (y + 2 * YangModelUIUtil.DEFAULT_H_ALIGN > cs.getGraphicsAlgorithm().getHeight()) {
            cs.getGraphicsAlgorithm().setHeight(y + 2 * YangModelUIUtil.DEFAULT_H_ALIGN);
        }
        if (null != line) {
            EList<Point> points = line.getPoints();
            if (1 < points.size()) {
                points.get(1).setX(cs.getGraphicsAlgorithm().getWidth());
            }               
        }*/
        
        try {
            for(Shape shape : cs.getChildren()) {
                layoutPictogramElement(shape, fp);
            }
            LayoutAlgorithm layoutAlgorithm = new DomainCompositeObjectLayoutAlgorithm();
            
            // Get the array of Connection LayoutRelationships
            LayoutRelationship[] connections = new LayoutRelationship[]{};

            // Setup the array of Shape LayoutEntity
            LayoutEntity[] entities = getLayoutEntities(cs, fp).toArray(new LayoutEntity[0]);

            // Get the diagram GraphicsAlgorithmn (we need the graph dimensions)
            GraphicsAlgorithm ga = cs.getGraphicsAlgorithm();//fp.getDiagramTypeProvider().getDiagram().getGraphicsAlgorithm();

            // Apply the LayoutAlgorithmn
        
            layoutAlgorithm.applyLayout(entities, connections, 0, YangModelUIUtil.DEFAULT_TEXT_HEIGHT, ga.getWidth(), ga.getHeight() - YangModelUIUtil.DEFAULT_TEXT_HEIGHT, false, false);
            
         // Update the Graphiti Shapes and Connections locations
            updateGraphCoordinates(entities, connections);
        } catch (InvalidLayoutConfiguration e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }           

    }

    /**
     * calls layout feature for {@link PictogramElement}
     * @param pe
     * @param fp
     */
    public static void layoutPictogramElement(PictogramElement pe, IFeatureProvider fp) {
        LayoutContext context = new LayoutContext(pe);
        fp.layoutIfPossible(context);
    }
    
}
