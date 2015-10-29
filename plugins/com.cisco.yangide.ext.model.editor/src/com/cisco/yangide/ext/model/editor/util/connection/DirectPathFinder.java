/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.model.editor.util.connection;

import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Simple implementation without using obstacles.
 */
public class DirectPathFinder implements IPathFinder {
    /**
     * It isn't used in this implementation. Always returns {@code 0}.
     * 
     * @return 0
     */
    public int getSpacing() {
        return 0;
    }

    /**
     * It isn't used in this implementation
     * 
     * @param spacing the spacing to set
     */
    public void setSpacing(int spacing) {
    }

    /**
     * It isn't used in this implementation
     */
    @Override
    public void addObstacle(Rectangle rect) {
    }

    /**
     * It isn't used in this implementation
     */
    @Override
    public void removeObstacle(Rectangle rect) {
    }

    /**
     * It isn't used in this implementation
     */
    @Override
    public void updateObstacle(Rectangle oldBounds, Rectangle newBounds) {
    }

    /**
     * Returns a path containing start and end points of the positions.
     * 
     * @return a path containing start and end points of the positions
     */
    @Override
    public RoutePath find(Position start, Position end, boolean strict) {
        if (start == null || end == null) {
            return null;
        }

        PointList result = new PointList();
        result.addPoint(start.getPoint());
        result.addPoint(end.getPoint());

        double distance = start.getPoint().getDistance(end.getPoint());
        return new RoutePath(result, 0, (int) distance);
    }
}
