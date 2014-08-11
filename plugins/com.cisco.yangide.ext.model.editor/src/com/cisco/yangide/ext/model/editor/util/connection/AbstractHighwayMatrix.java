package com.cisco.yangide.ext.model.editor.util.connection;

import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

/**
 * Base implemention of the {@link IHighwayMatrix}. Do not contain the search logic.
 * Uses the {@link List} as collection of the highways and position on list as identifier.
 * 
 * @author Ruslan
 *
 */
public abstract class AbstractHighwayMatrix
    implements IHighwayMatrix
{
    private final List<HighwayPtr> highways;

    /**
     * Constructor. Creates the instance using list of the highways.
     * 
     * @param highways - highways list
     * 
     * @see IHighwayMatrix
     * @see Highway
     */
    protected AbstractHighwayMatrix(List<Highway> highways)
    {
        this.highways = Lists.newArrayList();
        for (Highway highway : highways)
        {
            addHighway(highway);
        }
    }

    /**
     * Creates new HighwayPtr using specified {@code highway} and adds it to the end of highway pointers list.
     * 
     * @param highway - the highway to be added to this matrix
     * 
     * @return the index of the highway to be added 
     */
    @Override
    public int addHighway(Highway highway)
    {
        if (highway == null)
        {
            return -1;
        }
        int index = highways.size();
        highways.add(new HighwayPtr(highway, index));
        return index;
    }

    /**
     * Removes the highway pointer at the specified position in the highway pointers list
     * 
     * @param index - the index of the highway to be removed
     * 
     * @return the highway that was removed from the matrix
     */
    @Override
    public Highway removeHighway(int index)
    {
        HighwayPtr ptrHighway = highways.remove(index);
        for (int pos = index; pos < highways.size(); pos++)
        {
            ptrHighway.shiftToLeft();
        }
        return ptrHighway.highway;
    }

    /**
     * Creates the points list using specified {@code path}.
     *
     * @param path - array of the highways indexes
     * @param start - start point (first element on the returned points list)
     * @param end - end point (last element on the returned points list)
     * 
     * @return the points list
     */
    protected PointList getPoints(int[] path, Point start, Point end)
    {
//        System.out.println("Path: " + Arrays.toString(path));

        PointList result = new PointList();
        result.addPoint(start);

        Highway current = path.length == 0 ? null : getHighway(path[0]);
        for (int index = 1; index < path.length; index++)
        {
            Highway next = getHighway(path[index]);
            result.addPoint(current.getIntersection(next));
            current = next;
        }

        result.addPoint(end);
        return result;
    }

    /**
     * Gets the highways iterator
     * 
     * @return the highways iterator
     */
    @Override
    public Iterable<Highway> getHighways()
    {
        return new Iterable<Highway>()
        {
            @Override
            public Iterator<Highway> iterator()
            {
                return Iterators.transform(highways.iterator(),
                    HighwayPtr.TO_HIGHWAY);
            }
        };
    }

    /**
     * Gets the size of the highways list
     *
     * @return the size of the highways list
     */
    protected int getSize()
    {
        return highways.size();
    }

    /**
     * Returns the highway at the specified position in list.
     * 
     * @param index - index of the highway to return
     * 
     * @return the highway at the specified position in this list
     * 
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (<tt>index &lt; 0 || index &gt;= getSize()</tt>)
     */
    @Override
    public Highway getHighway(int index)
    {
        return highways.get(index).highway;
    }

    /**
     * Returns the index of the specified {@code highway}, or -1 if highways list does not contain the element.
     *
     * @param highway - element to search for
     * 
     * @return the index of the specified {@code highway}, or -1 if highways list does not contain the element.
     * 
     * @throws NullPointerException if the specified element is {@code null}
     */
    @Override
    public int getIdentifier(Highway highway)
    {
        for (int index = 0; index < highways.size(); index++)
        {
            if (highways.get(index).highway.equals(highway))
            {
                return index;
            }
        }
        return -1;
    }

    /**
     * Returns the highway pointer at the specified position in list.
     * 
     * @param index - index of the highway to return
     * 
     * @return the highway pointer at the specified position in this list
     * 
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (<tt>index &lt; 0 || index &gt;= getSize()</tt>)
     *         
     * @see HighwayPtr
     */
    protected HighwayPtr getHighwayPtr(int index)
    {
        return highways.get(index);
    }

    /**
     * Returns the index of the specified {@code highway} pointer
     *
     * @param highway - element to search for
     * 
     * @return the index of the specified {@code highway} pointer
     * 
     * @throws NullPointerException if the specified element is {@code null}
     */
    protected int getIndex(HighwayPtr highway)
    {
        return highway.index;
    }

    /**
     * Wrapper on the {@link Highway}. Contains index of the highway in the highways list 
     * for quick operation of the get index of element.
     * 
     * Overrides {@code hashCode()} and {@code equals()} for quick operations with hash map and hash set.
     * 
     * @author Ruslan
     *
     */
    protected static class HighwayPtr
    {
        /**
         * the wrapped element
         */
        public final Highway highway;
        /**
         * the index on the highways list
         */
        public int index;

        /**
         * Create HighwayPtr using specified {@code highway} and {@code index
         * }
         * @param highway - the highway
         * @param index - the index
         */
        public HighwayPtr(Highway highway, int index)
        {
            super();
            this.highway = highway;
            this.index = index;
        }

        /**
         * Decrements the index. 
         * For internal use only. Uses in the {@link #removeHighway()} method.
         *
         */
        private void shiftToLeft()
        {
            index--;
        }

        /**
         * Returns the index
         * 
         * @return the index
         */
        @Override
        public int hashCode()
        {
            return index;
        }

        /**
         * Compares the indexes (after standart checks of the {@code null} and type).
         */
        @Override
        public boolean equals(Object obj)
        {
            if (obj == this)
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
            HighwayPtr other = (HighwayPtr)obj;
            return other.index == index;
        }

        /**
         * Returns the string in the format: &lt;index&gt;:&nbsp;&lt;highway.ToString()&gt;
         */
        @Override
        public String toString()
        {
            return String.format("%d: %s", index, highway);
        }

        /**
         * Transformer from {@link HighwayPtr} to the {@link Highway}
         * 
         * @see Function
         */
        private static Function<HighwayPtr, Highway> TO_HIGHWAY =
            new Function<HighwayPtr, Highway>()
            {
                public Highway apply(HighwayPtr input)
                {
                    return input.highway;
                }
            };
    }

}
