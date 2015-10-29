package com.cisco.yangide.ext.model.editor.util.connection;

import org.eclipse.draw2d.geometry.Point;

/**
 * The horizontal or vertical line (probably bounded by one or both ends) not crossing an obstacle.
 *
 */
public class Highway
{
    private int start;
    private int end;
    private int position;
    private boolean type;

    /**
     * Static factory method creting horizontal {@code Highway} using point and length.
     * Use {@link Integer#MIN_VALUE} and {@link Integer#MAX_VALUE} foor creating unlimited end.
     * <p>
     * Examples:<br>
     *   <b>createHorizontal(-100, 100, 200)</b>:<br>
     *   &nbsp;&nbsp;&nbsp;&nbsp;line segment [point(-100, 100), point(100, 100)]<br>
     *   <b>createHorizontal(Integer.MIN_VALUE, 100, 200-Integer.MIN_VALUE)</b>:<br>
     *   &nbsp;&nbsp;&nbsp;&nbsp;line segment [point(-INFINITY, 200), point(100, 200)]<br>
     *   <b>createHorizontal(-200, 100, Integer.MAX_VALUE-(-200))</b>:<br>
     *   &nbsp;&nbsp;&nbsp;&nbsp;line segment [point(-200, 100), point(INFINITY, 200)]<br>
     * </p>
     *
     * @param x - x coordinate of the point
     * @param y - y coordinate of the point
     * @param length - length
     * 
     * @return horizontal {@code Highway}
     */
    public static Highway createHorizontal(int x, int y, int length)
    {
        return new Highway(x, x + length, y, true);
    }

    /**
     * Static factory method creting vertival {@code Highway} using point and length.
     * Use {@link Integer#MIN_VALUE} and {@link Integer#MAX_VALUE} foor creating unlimited end.
     * <p>
     * Examples:<br>
     *   <b>createVertical(-100, 100, 200)</b>:<br>
     *   &nbsp;&nbsp;&nbsp;&nbsp;line segment [point(-100, 100), point(-100, 300)]<br>
     *   <b>createVertical(100, Integer.MIN_VALUE, 200-Integer.MIN_VALUE)</b>:<br>
     *   &nbsp;&nbsp;&nbsp;&nbsp;line segment [point(100, -INFINITY), point(100, 200)]<br>
     *   <b>createVertical(-200, -100, Integer.MAX_VALUE-(-100))</b>:<br>
     *   &nbsp;&nbsp;&nbsp;&nbsp;line segment [point(-200, -100), point(-200, INFINITY)]<br>
     * </p>
     *
     * @param x - x coordinate of the point
     * @param y - y coordinate of the point
     * @param length - length
     * 
     * @return horizontal {@code Highway}
     */
    public static Highway createVertical(int x, int y, int length)
    {
        return new Highway(y, y + length, x, false);
    }

    /**
     * Constructor
     * 
     * @param start the start position. {@code x} in case horizontal or {@code y} otherwise
     * @param end the end position. {@code x} in case horizontal or {@code y} otherwise
     * @param position the {@code y} position in case horizontal or {@code x} otherwise
     * @param type {@code true} - horizontal, {@code false} vertical
     */
    protected Highway(int start, int end, int position, boolean type)
    {
        super();
        this.start = start;
        this.end = end;
        this.position = position;
        this.type = type;
    }

    /**
     * Gets the start position. {@code x} in case horizontal or {@code y} otherwise
     *
     * @return the start position. {@code x} in case horizontal or {@code y} otherwise
     */
    public int getStart()
    {
        return start;
    }

    /**
     * Gets the end position. {@code x} in case horizontal or {@code y} otherwise
     *
     * @return the end position. {@code x} in case horizontal or {@code y} otherwise
     */
    public int getEnd()
    {
        return end;
    }

    /**
     * Gets the {@code y} position in case horizontal or {@code x} otherwise
     *
     * @return the {@code y} position in case horizontal or {@code x} otherwise
     */
    public int getPosition()
    {
        return position;
    }

    /**
     * Whether is horisontal highway?
     *
     * @return whether is horisontal highway?
     */
    public boolean isHorizontal()
    {
        return type;
    }

    /**
     * Whether is vertical highway
     *
     * @return whether is vertical highway
     */
    public boolean isVertical()
    {
        return !type;
    }

    /**
     * Gets the start point
     *
     * @return the start point
     */
    public Point getStartPoint()
    {
        return type ? new Point(start, position) : new Point(position, start);
    }

    /**
     * Checks, whether has crossing with {@code other} highway.
     *
     * @return the start point
     */
    public boolean isIntersect(Highway other)
    {
        if (type == other.type)
        {
            return false;
        }
        return other.position >= start && other.position <= end
            && position >= other.start && position <= other.end;
    }

    /**
     * Gets the intersection point.
     * <p>
     * NOTE: Invalid in case highways has not intersection.
     * Call {@link #isIntersect(Highway)} before.
     * </p>
     *
     * @param other other hightway
     * @return the intersection point
     */
    public Point getIntersection(Highway other)
    {
        return type ? new Point(other.position, position) : new Point(position,
            other.position);
    }

    /*
     * Generated
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + end;
        result = prime * result + position;
        result = prime * result + start;
        result = prime * result + (type ? 1231 : 1237);
        return result;
    }

    /*
     * Generated
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        Highway other = (Highway)obj;
        if (end != other.end)
        {
            return false;
        }
        if (position != other.position)
        {
            return false;
        }
        if (start != other.start)
        {
            return false;
        }
        if (type != other.type)
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        String dir = type ? "HOR" : "VER";
        return String.format("%s[%d] (%d-%d)", dir, position, start, end);
    }

}
