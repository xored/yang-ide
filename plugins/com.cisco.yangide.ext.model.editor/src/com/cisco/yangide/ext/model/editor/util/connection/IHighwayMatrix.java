/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.model.editor.util.connection;

import org.eclipse.draw2d.geometry.Point;

/**
 * Search algorithm of a way from one highway to the another. Contains the collection of the
 * highway. For fast access to this collection of ways identifiers are used.
 * <p>
 * Depending on implementation, can do intermediate calculations at the time of change of this
 * collection (see {@link #addHighway} and {@link #removeHighway} methods).
 * </p>
 */
public interface IHighwayMatrix {
    /**
     * Finds the path
     *
     * @param idFrom the identifier of the starting highway
     * @param start the start point
     * @param idTo the identifier of the ending highway
     * @param end the end point and required direction from from this point
     * @return finded path
     * @see RoutePath
     */
    RoutePath getPath(int idFrom, Point start, int idTo, Point end);

    /**
     * Gets the highways iterator
     * 
     * @return the highways iterator
     */
    Iterable<Highway> getHighways();

    /**
     * Add the specified {@code highway} to the highways collection.
     *
     * @param highway - the highway to be added to this matrix
     * @return the identifier of the highway to be added
     */
    int addHighway(Highway highway);

    /**
     * Removes the of the specified {@code highway} from highways collection.
     * 
     * @param identifier - the identifier of the highway to be removed
     * @return the highway that was removed from the matrix
     */
    Highway removeHighway(int identifier);

    /**
     * Returns the highway using the specified {@code identifier}.
     * 
     * @param identifier - identifier of the highway to return
     * @return the highway at the specified position in this list
     * @throws RuntimeException in case invalid identifier
     */
    Highway getHighway(int identifier);

    /**
     * Returns the identifier of the specified {@code highway}, or -1 if highways collection does
     * not contain the element.
     *
     * @param highway - element to search for
     * @return the identifier of the specified {@code highway}, or -1 if highways collection does
     * not contain the element.
     * @throws NullPointerException if the specified element is {@code null}
     */
    int getIdentifier(Highway highway);
}
