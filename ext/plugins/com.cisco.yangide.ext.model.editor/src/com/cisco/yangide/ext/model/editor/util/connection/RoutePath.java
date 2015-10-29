package com.cisco.yangide.ext.model.editor.util.connection;

import org.eclipse.draw2d.geometry.PointList;

/**
 * <pre>
 * The path information:
 *  - points list
 *  - amount of the crossings
 *  - summary length of the path segments
 * </pre>
 */
public class RoutePath
    implements Comparable<RoutePath>
{
    public final PointList path;
    public final int cross;
    public final int length;

    /**
     * Constructor.
     * 
     * @param path - points list
     * @param cross - amount of the crossings
     * @param length - summary length of the path segments
     */
    public RoutePath(PointList path, int cross, int length)
    {
        super();
        this.path = path;
        this.cross = cross;
        this.length = length;
    }

    /**
     * Compares two {@code RoutePath} objects using count of the {@link #cross} and {@link #length}.<br>
     * Priorities: numbers of intersections, length
     * 
     * @param other - the {@code RoutePath} to be compared.
     * 
     */
    @Override
    public int compareTo(RoutePath other)
    {
        return cross != other.cross ? cross - other.cross : length
            - other.length;
    }

    /**
     * Returns the smaller of two {@code RoutePath} values using {@link #compareTo(RoutePath)} 
     *
     * @param p1 - an argument.
     * @param p2 - another argument.
     * 
     * @return the smaller of two {@code RoutePath} values
     */
    public static RoutePath min(RoutePath p1, RoutePath p2)
    {
        if (p1 == null)
        {
            return p2;
        }
        if (p2 == null)
        {
            return p1;
        }
        return p1.compareTo(p2) > 0 ? p2 : p1;
    }

}
