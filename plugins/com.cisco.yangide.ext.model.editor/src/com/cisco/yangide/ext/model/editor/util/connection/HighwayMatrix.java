package com.cisco.yangide.ext.model.editor.util.connection;

import java.util.Arrays;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;

/**
 * Draft
 *
 * TODO: Not implemented
 */
public class HighwayMatrix
    extends AbstractHighwayMatrix
    implements IHighwayMatrix
{
    private Cell[][] matrix;

    public HighwayMatrix(List<Highway> highways)
    {
        super(highways);
    }

    @Override
    public int addHighway(Highway highway)
    {
        matrix = null;
        return super.addHighway(highway);
    }

    @Override
    public Highway removeHighway(int index)
    {
        matrix = null;
        return super.removeHighway(index);
    }

    @Override
    public RoutePath getPath(int indexFrom, Point start, int indexTo, Point end)
    {
//        if (indexFrom == indexTo)
//        {
//            return new int[] { indexTo };
//        }
//
//        Cell cell = getMatrix()[indexFrom][indexTo];
//        return cell == null ? null : cell.path;
        return null;
    }

    protected Cell[][] getMatrix()
    {
        if (matrix == null)
        {
            matrix = build();
        }
        return matrix;
    }

    protected Cell[][] build()
    {
        Cell[][] result = new Cell[getSize()][getSize()];
        for (int index = 0; index < result.length; index++)
        {
            addToMatrix(result, index);
        }
        return result;
    }

    protected void addToMatrix(Cell[][] result, int newIndex)
    {
        for (int crossIndex = 0; crossIndex < newIndex; crossIndex++)
        {
            if (newIndex != crossIndex && hasPath(result, newIndex, crossIndex))
            {
                result[newIndex][crossIndex] = new Cell(crossIndex);
                result[crossIndex][newIndex] = new Cell(newIndex);
                updateMatrix(result, newIndex, crossIndex);
                updateMatrix(result, crossIndex, newIndex);
            }
        }
    }

    private boolean hasPath(Cell[][] result, int id1, int id2)
    {
        Highway highway1 = getHighway(id1);
        Highway highway2 = getHighway(id2);
        return result[id1][id2] != null || highway1.isIntersect(highway2);
    }

    protected void updateMatrix(Cell[][] result, int dest, int src)
    {
        for (int index = 0; index < result.length; index++)
        {
            if (index == dest)
            {
                continue;
            }

            Cell cell = result[src][index];
            int size = cell == null ? -1 : cell.path.length;

            Cell backCell = result[index][src];
            int backSize = backCell == null ? -1 : backCell.path.length;

            assert size == backSize;

            if (cell != null /* && backCell != null */)
            {
                int[] path = new int[cell.path.length + 1];
                path[0] = src;
                cell.copyPath(path, 1);
                result[dest][index] = new Cell(path);

                int[] backPath = new int[backCell.path.length + 1];
                backPath[backPath.length - 1] = dest;
                backCell.copyPath(backPath, 0);
                result[index][dest] = new Cell(backPath);
            }
        }
    }

    public static class Cell
    {
        public final int[] path;

        public Cell(int path)
        {
            this(new int[] { path });
        }

        public Cell(int[] path)
        {
            this.path = path;
        }

        /**
         * Copies {@code path} of this cell to the specified destination array.
         *  
         * @param dest - the destination array.
         * @param offset - starting position in the destination data.
         */
        public void copyPath(int[] dest, int offset)
        {
            System.arraycopy(path, 0, dest, offset, path.length);
        }

        @Override
        public String toString()
        {
            return Arrays.toString(path);
        }
    }

}
