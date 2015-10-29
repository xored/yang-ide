package com.cisco.yangide.ext.model.editor.util.connection;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * <pre>
 * The position information:
 *  - point
 *  - direction
 *  - assigned obstacle
 * </pre>
 *
 */
public class Position
{
    /**
     * Enumeration of the rectilinear directions (UP, RIGHT, DOWN, LEFT)
     */
    public enum Direction
    {
        UP,
        RIGHT,
        DOWN,
        LEFT
    }

    private Point point;

    private final Direction direction;

    private final Rectangle obstacle;

    /**
     * Create new {@code Position} using specified {@code point}, {@code direction} and {@code obstacle}
     * 
     * @param point - the ray start point
     * @param direction - the ray direction
     * @param obstacle - the area assigned with figure - source of the ray
     */
    private Position(Point point, Direction direction, Rectangle obstacle)
    {
        this.point = point;
        this.direction = direction;
        this.obstacle = obstacle;
    }

    /**
     * Gets the point
     * @return the point
     */
    public Point getPoint()
    {
        return point;
    }

    /**
     * Gets the direction
     * @return the direction
     */
    public Direction getDirection()
    {
        return direction;
    }

    /**
     * Gets the obstacle: the area assigned with figure - source of the ray
     * @return the obstacle: the area assigned with figure - source of the ray
     */
    public Rectangle getObstacle()
    {
        return obstacle;
    }

    /**
     * Checks, is {@code direction} not specified or horizontal
     * @return {@code true} in case direction not specified or horizontal and {@code false} otherwise
     */
    public boolean isHorizontal()
    {
        return direction == null || direction == Direction.RIGHT
            || direction == Direction.LEFT;
    }

    /**
     * Checks, is {@code direction} not specified or vertical
     * @return {@code true} in case direction not specified or vertical and {@code false} otherwise
     */
    public boolean isVertical()
    {
        return direction == null || direction == Direction.UP
            || direction == Direction.DOWN;
    }

    /**
     * Factory method for create new instance of the {@code Position} using {@code point} and {@code obstacle}.
     * Calculates the rectangle side nearest to a point and sets the ray direction to opposite.
     * 
     * If {@code obstacle} is {@code null} then Ray with empty direction will created.
     *
     * @param obstacle
     *          the area assigned with figure - source of the ray
     * @param point
     *          the point of ray
     *
     * @return new instance of the {@code Position}
     */
    public static Position create(Rectangle obstacle, Point point)
    {
        if (obstacle == null)
        {
            return new Position(point, null, null);
        }

        int minDistance = Math.abs(obstacle.x() - point.x());
        Direction direction = Direction.LEFT;

        int distance = Math.abs(obstacle.right() - point.x());
        if (distance < minDistance)
        {
            minDistance = distance;
            direction = Direction.RIGHT;
        }

        distance = Math.abs(obstacle.y() - point.y());
        if (distance < minDistance)
        {
            minDistance = distance;
            direction = Direction.UP;
        }

        distance = Math.abs(obstacle.bottom() - point.y());
        if (distance < minDistance)
        {
            minDistance = distance;
            direction = Direction.DOWN;
        }

        return new Position(point.getCopy(), direction, obstacle);
    }

    /**
     * @return a copy of this Position
     */
    public Position getCopy()
    {
        return new Position(point.getCopy(), direction, obstacle);
    }

    /**
     * Shifts the start point by the specified {@code length} using the current direction.  
     *
     * @param length - a length
     * @return <code>this</code> for convenience
     */
    public Position moveOnDirection(int length)
    {
        if (direction == null)
        {
            return this;
        }

        switch (direction)
        {
        case LEFT:
            point.translate(-length, 0);
            break;
        case RIGHT:
            point.translate(length, 0);
            break;
        case UP:
            point.translate(0, -length);
            break;
        case DOWN:
            point.translate(0, length);
            break;
        }
        return this;
    }

    /**
     * Returns the string in the format: "(&lt;x&gt;,&lt;y&gt;)&nbsp;&lt;direction&gt;"
     */
    @Override
    public String toString()
    {
        return String.format("(%d,%d) %s", point.x, point.y, direction);
    }

}
