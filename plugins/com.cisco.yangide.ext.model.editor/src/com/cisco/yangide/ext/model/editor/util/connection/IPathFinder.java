package com.cisco.yangide.ext.model.editor.util.connection;

import org.eclipse.draw2d.geometry.Rectangle;

/**
 *
 * Interface for find the path between obstacles
 *
 */
public interface IPathFinder
{
    /**
     * Adds an obstacle with the given bounds to the obstacles.
     * 
     * @param rect
     *            the bounds of this obstacle
     */
    public void addObstacle(Rectangle rect);

    /**
     * Removes the obstacle with the rectangle's bounds from the routing.
     * 
     * @param rect
     *            the bounds of the obstacle to remove
     */
    public void removeObstacle(Rectangle rect);

    /**
     * Updates the position of an existing obstacle.
     * 
     * @param oldBounds
     *            the old bounds(used to find the obstacle)
     * @param newBounds
     *            the new bounds
     */
    public void updateObstacle(Rectangle oldBounds, Rectangle newBounds);

    /**
     * Finds the path
     *
     * @param start
     *              the start position
     * @param end
     *              the end position
     * @param strict
     *              the flag
     *
     * @return finded path
     * 
     * @see RoutePath
     */
    public RoutePath find(Position start, Position end, boolean strict);

    /**
     * Gets the spacing maintained between figure and path.
     * @return the spacing maintained between figure and path.
     */
    int getSpacing();

    /**
     * Sets the spacing maintained between figure and path.
     * @param spacing the spacing to set
     */
    void setSpacing(int spacing);

}
