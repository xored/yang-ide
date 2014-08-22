package com.cisco.yangide.ext.model.editor.util.connection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

public class RectilinearAvoidObstaclesPathFinder implements IPathFinder {
    /**
     * The default spacing maintained between figure and path.
     *
     * @see #getSpacing()
     * @see #setSpacing(int)
     */
    public static final int DEFAULT_SPACING = 5;

    /**
     * The spacing maintained between figure and path.
     */
    private int spacing = DEFAULT_SPACING;

    private IHighwayMatrix matrix;
    private Set<Rectangle> obstacles = new HashSet<>();
    private List<Rectangle> temporaryObstaclesStore = null;

    /**
     * The empty constructor.
     */
    public RectilinearAvoidObstaclesPathFinder() {
    }

    @Override
    public void addObstacle(Rectangle rect) {
        if (!isSmallObstacle(rect)) {
            obstacles.add(addSpacing(rect));
            matrix = null;
        }
    }

    @Override
    public void removeObstacle(Rectangle rect) {
        if (!isSmallObstacle(rect)) {
            obstacles.remove(addSpacing(rect));
            matrix = null;
        }
    }

    protected boolean isSmallObstacle(Rectangle rect) {
        return rect.width() <= 1 && rect.height() <= 1;
    }

    protected Rectangle addSpacing(Rectangle rect) {
        if (rect != null) {
            return rect.getCopy().translate(-spacing, -spacing).resize(spacing * 2, spacing * 2);
        }
        return null;
    }

    @Override
    public void updateObstacle(Rectangle oldBounds, Rectangle newBounds) {
        obstacles.remove(oldBounds);
        obstacles.remove(newBounds);
    }

    @Override
    public RoutePath find(Position start, Position end, boolean strict) {
        // System.out.println(String.format("From: %s, To: %s", start, end));

        start = start.getCopy().moveOnDirection(spacing);
        end = end.getCopy().moveOnDirection(spacing);

        if (isDirect(start, end)) {
            PointList points = new PointList();
            points.addPoint(start.getPoint());
            points.addPoint(end.getPoint());
            int length = (int) end.getPoint().getDistance(start.getPoint());
            return new RoutePath(points, 0, length);
        }

        filterObstacles(start, strict);
        filterObstacles(end, strict);
        try {
            IHighwayMatrix matrix = getMatrix();
            RoutePath minPath = null;

            for (Highway highwayFrom : getHighways(start, strict)) {
                int fromId = matrix.addHighway(highwayFrom);
                for (Highway highwayTo : getHighways(end, strict)) {
                    int toId = matrix.addHighway(highwayTo);
                    RoutePath candidate = matrix.getPath(fromId, start.getPoint(), toId, end.getPoint());
                    minPath = RoutePath.min(minPath, candidate);

                    matrix.removeHighway(toId);
                }
                matrix.removeHighway(fromId);
            }

            return minPath;
        } finally {
            restoreObstacles();
        }
    }

    protected void filterObstacles(Position pos, boolean strict) {
        if (!strict) {
            if (temporaryObstaclesStore == null) {
                temporaryObstaclesStore = new ArrayList<>();
            }
            Rectangle owner = addSpacing(pos.getObstacle());
            Point point = pos.getPoint();

            for (Iterator<Rectangle> it = obstacles.iterator(); it.hasNext();) {
                Rectangle obstacle = it.next();
                if (!obstacle.equals(owner) && obstacle.contains(point)) {
                    it.remove();
                    temporaryObstaclesStore.add(obstacle);
                    // TODO: to remove highways from a matrix
                    matrix = null;
                }
            }
        }
    }

    protected void restoreObstacles() {
        if (temporaryObstaclesStore != null) {
            for (Rectangle obstacle : temporaryObstaclesStore) {
                addObstacle(obstacle);
                // TODO: to add highways to the matrix
            }
        }
        temporaryObstaclesStore = null;
    }

    protected boolean isDirect(Position start, Position end) {
        Point ptStart = start.getPoint();
        Point ptEnd = end.getPoint();

        if (ptStart.x == ptEnd.x) {
            return Math.abs(ptStart.y - ptEnd.y) <= spacing * 2;
        }
        if (ptStart.y == ptEnd.y) {
            return Math.abs(ptStart.x - ptEnd.x) <= spacing * 2;
        }
        return false;
    }

    protected List<Highway> getHighways(Position position, boolean strict) {
        List<Highway> highways = new ArrayList<>();

        // In case position direction is undefined it horizontal and vertical at the same time
        if (position.isVertical()) {
            Rectangle owner = strict ? addSpacing(position.getObstacle()) : null;
            Highway highway = createVerHighway(position.getPoint(), owner);
            if (highway != null) {
                highways.add(highway);
            }
        }

        if (position.isHorizontal()) {
            Rectangle owner = strict ? addSpacing(position.getObstacle()) : null;
            Highway highway = createHorHighway(position.getPoint(), owner);
            if (highway != null) {
                highways.add(highway);
            }
        }

        return highways;
    }

    protected IHighwayMatrix getMatrix() {
        if (matrix == null) {
            matrix = createMatrix();
        }
        return matrix;
    }

    protected IHighwayMatrix createMatrix() {
        List<Highway> highways = new ArrayList<>();
        for (Rectangle obstacle : obstacles) {
            highways.add(createVerHighway(obstacle.getLeft(), obstacle));
            highways.add(createHorHighway(obstacle.getTop(), obstacle));
            highways.add(createVerHighway(obstacle.getRight(), obstacle));
            highways.add(createHorHighway(obstacle.getBottom(), obstacle));
        }
        return new HighwayMatrixWave(highways);
    }

    private Highway createHorHighway(Point point, Rectangle owner) {
        int minX = Integer.MIN_VALUE, maxX = Integer.MAX_VALUE;
        for (Rectangle obstacle : obstacles) {
            if (owner != null && !owner.equals(obstacle) && obstacle.contains(point)) {
                return null;
            }
            if (point.y() > obstacle.y() && point.y() < obstacle.bottom()) {
                if (point.x() >= obstacle.right()) {
                    minX = Math.max(minX, obstacle.right());
                } else if (point.x() <= obstacle.x()) {
                    maxX = Math.min(maxX, obstacle.x());
                }
            }
        }
        return Highway.createHorizontal(minX, point.y(), maxX - minX);
    }

    private Highway createVerHighway(Point point, Rectangle owner) {
        int minY = Integer.MIN_VALUE, maxY = Integer.MAX_VALUE;
        for (Rectangle obstacle : obstacles) {
            if (owner != null && !owner.equals(obstacle) && obstacle.contains(point)) {
                return null;
            }
            if (point.x() > obstacle.x() && point.x() < obstacle.right()) {
                if (point.y() >= obstacle.bottom()) {
                    minY = Math.max(minY, obstacle.bottom());
                } else if (point.y() <= obstacle.y()) {
                    maxY = Math.min(maxY, obstacle.y());
                }
            }
        }
        return Highway.createVertical(point.x(), minY, maxY - minY);
    }

    /**
     * Gets the spacing maintained between figure and path.<br>
     * <b>Default: </b> 10.
     *
     * @return the spacing maintained between figure and path.
     */
    @Override
    public int getSpacing() {
        return spacing;
    }

    /**
     * Sets the spacing maintained between figure and path.
     *
     * @param spacing the spacing to set
     */
    @Override
    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

}
