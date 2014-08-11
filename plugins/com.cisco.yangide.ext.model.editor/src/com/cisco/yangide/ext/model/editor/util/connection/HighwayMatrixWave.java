package com.cisco.yangide.ext.model.editor.util.connection;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

import com.google.common.collect.Lists;

/**
 * The implementation of the {@link IHighwayMatrix} using wave algorithm.
 *
 */
public class HighwayMatrixWave
    extends AbstractHighwayMatrix
    implements IHighwayMatrix
{
    /**
     * Constructor.<br>
     * Creates the instance, using starting set of the highways.
     * Before a call of the {@link #getPath(int, Point, int, Point)} this set can be 
     * changed using {@link #addHighway(Highway)} or {@link #removeHighway(int)}.
     * 
     * @param highways - starting set of the highways.
     */
    public HighwayMatrixWave(List<Highway> highways)
    {
        super(highways);
    }

    /**
     * Looks for a path, using algorithm of a wave.<br>
     * If path is not found, then {@code null} will be returned.<br>
     * Returned path contain the {@code start} and {@code end} points.
     * 
     * @param idFrom
     *              the identifier of the starting highway
     * @param start
     *              the start point
     * @param idTo
     *              the identifier of the ending highway
     * @param end
     *              the end point and required direction from from this point
     *
     * @return finded path or @{code null}
     */
    @Override
    public RoutePath getPath(int indexFrom, Point start, int indexTo, Point end)
    {
        if (indexFrom == indexTo)
        {
            PointList points = new PointList();
            points.addPoint(start);
            points.addPoint(end);
            int length = (int)end.getDistance(start);
            return new RoutePath(points, 0, length);
        }

        // Init vertex info (neighbours, wave label)
        List<Vertex> vertexes = Lists.newArrayList();
        for (int index = 0; index < getSize(); index++)
        {
            vertexes.add(new Vertex(getHighwayPtr(index)));
        }

        // Init Wave
        LinkedList<Vertex> front = Lists.newLinkedList();
        Vertex vertexFrom = vertexes.get(indexFrom);
        Vertex vertexTo = vertexes.get(indexTo);

        vertexFrom.setStartPoint(start);
        front.add(vertexFrom);

        // Start wave
        while (!front.isEmpty())
        {
            Vertex vertex = front.removeFirst();
            if (vertex == vertexTo)
            {
                int[] path = vertex.getPath(vertexes, indexFrom, indexTo);
                PointList points = getPoints(path, start, end);
                Point last = vertex.right.point;
                int length = vertex.length + (int)end.getDistance(last);
                return new RoutePath(points, points.size() - 2, length);
            }

            int step = vertex.label + 1;
            for (Integer idxNeighbour : vertex.neighbours)
            {
                Vertex neighbour = vertexes.get(idxNeighbour);
                if (neighbour.updateLabel(vertex, step))
                {
                    front.add(neighbour);
                }
            }
        }

        return null;
    }

    /**
     * Vertex of the graph. Contains the collection of the neighbour vertexes and
     * also the data needed for algorithm work.
     * 
     * NOTE: for internal use only.
     * 
     * @author Ruslan
     *
     */
    private class Vertex
    {
        private static final int UNDEFINED = -1;

        private final Highway highway;
        private final List<Integer> neighbours = Lists.newArrayList();
        private int label = UNDEFINED;
        private int length = Integer.MAX_VALUE;

        private Cross left;
        private Cross right;

        public Vertex(HighwayPtr ptrHighway)
        {
            this.highway = ptrHighway.highway;
            for (int index = 0; index < getSize(); index++)
            {
                if (ptrHighway.index != index
                    && ptrHighway.highway.isIntersect(getHighway(index)))
                {
                    neighbours.add(index);
                }
            }
        }

        public void setStartPoint(Point point)
        {
            left = new Cross(null, point, false);
            right = new Cross(null, point, true);
            length = 0;
            label = 0;
        }

        public boolean updateLabel(Vertex from, int step)
        {
            boolean result = label == UNDEFINED;
            if (result || label == step)
            {
                Point cross = from.highway.getIntersection(highway);

                int newLeftLength = calcLength(cross, from.left.point);
                int newRightLength = calcLength(cross, from.right.point);

                int newLength =
                    Math.min(newLeftLength, newRightLength) + from.length;

                if (newLength < length)
                {
                    length = newLength;
                    left = new Cross(from, cross, false);
                    right = new Cross(from, cross, true);
                    label = step;
                }
                else if (newLength == length)
                {
                    if (less(cross, left.point))
                    {
                        left = new Cross(from, cross, false);
                    }
                    else if (more(cross, right.point))
                    {
                        right = new Cross(from, cross, true);
                    }
                    label = step;
                }
            }
            return result;
        }

        private int calcLength(Point cross, Point point)
        {
            return highway.isHorizontal() ? Math.abs(point.y - cross.y)
                : Math.abs(point.x - cross.x);
        }

        private boolean less(Point p1, Point p2)
        {
            return highway.isHorizontal() ? p1.x < p2.x : p1.y < p2.y;
        }

        private boolean more(Point p1, Point p2)
        {
            return highway.isHorizontal() ? p1.x > p2.x : p1.y > p2.y;
        }

        public int[] getPath(List<Vertex> vertexes, int indexFrom, int indexTo)
        {
            int[] result = new int[label + 1];
            result[label] = indexTo;
            result[0] = indexFrom;
            fillPath(result, label - 1, true, vertexes);
            return result;
        }

        protected void fillPath(int[] result, int index, boolean useRight,
            List<Vertex> vertexes)
        {
            if (index > 0)
            {
                Vertex from = useRight ? right.from : left.from;
                int idxFrom = vertexes.indexOf(from);
                result[index--] = idxFrom;

                useRight = useRight ? right.useRight : left.useRight;
                from.fillPath(result, index, useRight, vertexes);
            }
        }

        private class Cross
        {
            public final Vertex from;
            public final boolean useRight;
            public final Point point;

            public Cross(Vertex from, Point point, boolean useRight)
            {
                this.from = from;
                this.point = point;
                this.useRight = useRight;
            }
        }

    }

}
