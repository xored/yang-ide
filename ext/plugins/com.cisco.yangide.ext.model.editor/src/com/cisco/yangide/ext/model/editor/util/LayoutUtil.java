package com.cisco.yangide.ext.model.editor.util;

import java.util.ArrayList;
import java.util.Collections;
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
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.services.GraphitiUi;

import com.cisco.yangide.ext.model.editor.diagram.EditorFeatureProvider;
import com.cisco.yangide.ext.model.editor.util.connection.Position;
import com.cisco.yangide.ext.model.editor.util.connection.RectilinearAvoidObstaclesPathFinder;
import com.cisco.yangide.ext.model.editor.util.connection.RoutePath;

public class LayoutUtil {

    private LayoutUtil() {
        super();
    }

    public static final double DEFAULT_DIAGRAM_LAYOUT_V_SHIFT = 10;
    protected static final Comparator<YangDiagramNode> COMPARATOR = new LayoutEntityOrderComparator();

    private static void updateGraphCoordinates(List<YangDiagramNode> nodes) {
        for (YangDiagramNode node : nodes) {
            node.updateRealObject();
        }
    }

    private static List<YangDiagramNode> getLayoutEntities(IFeatureProvider fp) {
        EList<Shape> children = fp.getDiagramTypeProvider().getDiagram().getChildren();
        List<YangDiagramNode> result = new ArrayList<YangDiagramNode>();
        for (Shape shape : children) {
            Object bo = fp.getBusinessObjectForPictogramElement(shape);
            if (null != bo) {
                int pos = YangModelUtil.getPositionInParent(
                        fp.getBusinessObjectForPictogramElement(fp.getDiagramTypeProvider().getDiagram()), bo);
                YangDiagramNode currentEntity = new YangDiagramNode(shape, pos);
                result.add(currentEntity);
            }
        }
        return result;
    }

    private static double[] getAreaSizeAndArrangeLayout(List<YangDiagramNode> entities,
            YangDiagramLayoutAlgorithm layout, int width, int height) {
        double maxW = 0D;
        double maxH = 0D;
        double fullW = 0D;
        double fullH = 0D;

        for (YangDiagramNode node : entities) {
            fullW += node.getWidth();
            if (maxW < node.getWidth()) {
                maxW = node.getWidth();
            }
            fullH += node.getHeight();
            if (maxH < node.getHeight()) {
                maxH = node.getHeight();
            }
        }
        layout.setMaxElementSizes(maxW, maxH);
        layout.setFullElementSizes(fullW, fullH);
        double h = Math.max(1, Math.ceil(entities.size() / (width / maxW))) * maxH;
        return new double[] { width, h };
    }

    public static class YangDiagramNode {
        protected int pos;
        protected Shape realObject;
        protected double x, y, width, height;

        public YangDiagramNode(Shape realObject, int pos) {
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

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public void setLocation(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }

        public void updateRealObject() {
            realObject.getGraphicsAlgorithm().setX((int) x);
            realObject.getGraphicsAlgorithm().setY((int) y);
        }
    }

    public static class YangCompositeSimpleNode extends YangDiagramNode {
        private List<YangCompositeSimpleNode> children;

        public YangCompositeSimpleNode(ContainerShape realObject, int pos) {
            super(realObject, pos);
        }

        @Override
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

        public void setSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public void updateRealObject(IFeatureProvider fp) {
            realObject.getGraphicsAlgorithm().setX((int) x);
            realObject.getGraphicsAlgorithm().setY((int) y);
            realObject.getGraphicsAlgorithm().setHeight((int) height);
            realObject.getGraphicsAlgorithm().setWidth((int) width);
            layoutContainerShapeHeader((ContainerShape) realObject, fp);
            for (YangCompositeSimpleNode child : getChildren()) {
                child.updateRealObject(fp);
            }
        }

    }

    private static class LayoutEntityOrderComparator implements Comparator<YangDiagramNode> {

        @Override
        public int compare(YangDiagramNode o1, YangDiagramNode o2) {
            if (null == o1 && null == o2) {
                return 0;
            }
            if (null != o1 && null == o2) {
                return 1;
            }
            if (null == o1 && null != o2) {
                return -1;
            }
            return o1.getPositionInParent() > o2.getPositionInParent() ? 1 : o1.getPositionInParent() == o2
                    .getPositionInParent() ? 0 : -1;
        }

    }

    protected static int layoutCompositeSimpleNode(YangCompositeSimpleNode element, int boundsWidth) {

        int y = YangModelUIUtil.DEFAULT_TEXT_HEIGHT;
        for (YangCompositeSimpleNode sn : element.getChildren()) {
            int elementHeight = layoutCompositeSimpleNode(sn,
                    Math.max(0, boundsWidth - 2 * YangModelUIUtil.DEFAULT_V_ALIGN))
                    + 2 * YangModelUIUtil.DEFAULT_H_ALIGN;
            int xmove = YangModelUIUtil.DEFAULT_V_ALIGN;
            int ymove = y + YangModelUIUtil.DEFAULT_H_ALIGN;
            sn.setLocation(xmove, ymove);
            sn.setSize(Math.max(0, boundsWidth - 2 * YangModelUIUtil.DEFAULT_V_ALIGN),
                    (int) Math.max(elementHeight, sn.getHeight()));
            y = (int) (y + sn.getHeight() + YangModelUIUtil.DEFAULT_H_ALIGN);

        }
        element.setSize(boundsWidth, (int) Math.max(y + 2 * YangModelUIUtil.DEFAULT_H_ALIGN, element.getHeight()));
        return y;
    }

    private static class YangDiagramLayoutAlgorithm {

        protected double maxW = YangModelUIUtil.DEFAULT_WIDTH;
        @SuppressWarnings("unused")
        protected double maxH = YangModelUIUtil.DEFAULT_COMPOSITE_HEIGHT;
        protected int cols;
        protected int rows;

        @SuppressWarnings("unused")
        protected double fullW = YangModelUIUtil.DEFAULT_WIDTH;
        protected double fullH = YangModelUIUtil.DEFAULT_COMPOSITE_HEIGHT;
        public static final int OFFSET = 40;

        protected int[] calculateNumberOfRowsAndCols(int numChildren, double boundX, double boundY, double boundWidth,
                double boundHeight) {
            cols = Math.max(1, (int) Math.min(numChildren, boundWidth / (getMaxW() + OFFSET)));
            rows = Math.max(1, (int) Math.ceil(numChildren / cols)) + 1;

            return new int[] { cols, rows };
        }

        public void applyLayout(List<YangDiagramNode> entitiesToLayout, double boundX, double boundY,
                double boundWidth, double boundHeight) {
            Collections.sort(entitiesToLayout, COMPARATOR);
            calculateNumberOfRowsAndCols(entitiesToLayout.size(), boundX, boundY, boundWidth, boundHeight);
            int index = 0;
            double averageH = fullH / cols;
            double leftH = fullH;
            if (cols >= entitiesToLayout.size()) {
                leftH = 0;
            }
            for (int j = 0; j < cols; j++) {
                double y = 0;
                do { // set at least one element in the row
                    if (index < entitiesToLayout.size()) {
                        YangDiagramNode sn = entitiesToLayout.get(index++);
                        double xmove = boundX + j * (getMaxW() + OFFSET) + OFFSET;
                        double ymove = boundY + y + OFFSET;
                        sn.setLocation(xmove, ymove);
                        y += sn.getHeight() + OFFSET;
                        leftH -= sn.getHeight();
                    }
                } while ((j == cols - 1 || (leftH / (cols - j - 1) > averageH && cols - j - 1 < entitiesToLayout.size()
                        - index))
                        && index < entitiesToLayout.size());

            }
        }

        public double getMaxW() {
            return maxW;
        }

        public void setMaxElementSizes(double maxW, double maxH) {
            this.maxW = maxW;
            this.maxH = maxH;
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
        YangDiagramLayoutAlgorithm layoutAlgorithm = new YangDiagramLayoutAlgorithm();

        // Setup the array of Shape LayoutEntity
        List<YangDiagramNode> diagramEntities = getLayoutEntities(fp);
        int diagramWidth = ((EditorFeatureProvider) fp).getDiagramWidth();
        int diagramHeight = ((EditorFeatureProvider) fp).getDiagramHeight();
        double[] preferedSize = getAreaSizeAndArrangeLayout(diagramEntities, layoutAlgorithm, diagramWidth,
                diagramHeight);

        // Apply the LayoutAlgorithmn
        layoutAlgorithm.applyLayout(diagramEntities, 0D, 0D, preferedSize[0], preferedSize[1]);

        updateGraphCoordinates(diagramEntities);

        layoutDiagramConnections(fp);
    }

    public static void layoutContainerShapeHeader(ContainerShape cs, IFeatureProvider fp) {
        GraphicsAlgorithm text = YangModelUIUtil.getObjectPropGA(cs, PropertyUtil.OBJECT_HEADER_TEXT_SHAPE_KEY);
        GraphicsAlgorithm type = YangModelUIUtil.getObjectPropGA(cs, PropertyUtil.BUSINESS_OBJECT_TYPE_SHAPE_KEY);
        GraphicsAlgorithm number = YangModelUIUtil.getObjectPropGA(cs, PropertyUtil.OBJECT_NUMBER_SHAPE_KEY);
        int textWidth = Math.max(0, cs.getGraphicsAlgorithm().getWidth() - YangModelUIUtil.DEFAULT_TEXT_HEIGHT);

        if (null != number) {
            IDimension dim = GraphitiUi.getUiLayoutService().calculateTextSize(((Text) number).getValue(),
                    number.getStyle().getFont());
            number.setX(Math.max(0, cs.getGraphicsAlgorithm().getWidth() - YangModelUIUtil.DEFAULT_OBJECT_NUMBER_IND
                    - dim.getWidth()));
            textWidth = Math.max(0, textWidth - number.getWidth());
        }
        int typeWidth = 0;
        if (null != type && type instanceof Text) {
            IDimension size = GraphitiUi.getUiLayoutService().calculateTextSize(((Text) type).getValue(),
                    type.getStyle().getFont());
            if (size != null) {
                typeWidth = size.getWidth();
                textWidth += YangModelUIUtil.DEFAULT_TEXT_HEIGHT;
            }
        }
        int nameWidth = 0;
        if (null != text && text instanceof Text) {
            nameWidth = GraphitiUi.getUiLayoutService()
                    .calculateTextSize(((Text) text).getValue(), text.getStyle().getFont()).getWidth();
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

    protected static YangCompositeSimpleNode createCompositeSimpleNode(ContainerShape cs, IFeatureProvider fp) {
        Object bo = fp.getBusinessObjectForPictogramElement(cs);
        YangCompositeSimpleNode result = new YangCompositeSimpleNode(cs, YangModelUtil.getPositionInParent(
                fp.getBusinessObjectForPictogramElement(cs.getContainer()), bo));
        for (Shape shape : YangModelUIUtil.filterBusinessObjectShapes(cs.getChildren())) {
            if (shape instanceof ContainerShape) {
                result.addChild(createCompositeSimpleNode((ContainerShape) shape, fp));
            }
        }
        Collections.sort(result.getChildren(), COMPARATOR);
        return result;
    }

    public static void layoutContainerShape(ContainerShape cs, IFeatureProvider fp) {
        YangCompositeSimpleNode node = createCompositeSimpleNode(cs, fp);
        layoutCompositeSimpleNode(node, cs.getGraphicsAlgorithm().getWidth());
        node.updateRealObject(fp);
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
