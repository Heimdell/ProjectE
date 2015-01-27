
package moze_intel.projecte.utils;

import java.util.*;

public class Point
{
    public int x, y, z;

    public Point(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Point))
            return false;

        Point pt = (Point) obj;

        return pt.x == x && pt.y == y && pt.z == z;
    }

    public CoordinateBox box() 
    {
        return new CoordinateBox(x, y, z, x, y, z);
    }

    public boolean within(CoordinateBox box) 
    {
        return x >= box.minX && x <= box.maxX
            && y >= box.minY && y <= box.maxY
            && z >= box.minZ && z <= box.maxZ;
    }

    public List<Point> near() 
    {
        Point [] pts = new Point [] {
            new Point(x + 1, y, z),
            new Point(x - 1, y, z),
            new Point(x, y + 1, z),
            new Point(x, y - 1, z),
            new Point(x, y, z + 1),
            new Point(x, y, z - 1)
        };

        List<Point> list = new ArrayList<Point>();

        for (Point p : pts) {
            list.add(p);
        }

        return list;
    }
}