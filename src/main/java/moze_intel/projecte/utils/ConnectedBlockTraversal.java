
package moze_intel.projecte.utils;

import java.util.*;

public abstract class ConnectedBlockTraversal<Point> 
{
	Queue<Point> result = new LinkedList<Point>();
	Queue<Point> points = new LinkedList<Point>();
	Set<Point> traversed = new HashSet<Point>();

	public abstract boolean traversable(Point pt);
	public abstract List<Point> near(Point pt);

	public Queue<Point> runFrom(Point start) 
	{
		return runFromWithCapacity(start, 1000);
	}

	public Queue<Point> runFromWithCapacity(Point start, int cap)
	{
		if (traversable(start))
			addPoint(start);

		for (; cap >= 0 && !points.isEmpty();) 
		{
			cap--;
			Point pt = points.poll();

			for (Point neighbor : near(pt)) 
			{
				if (traversable(neighbor) && !traversed.contains(neighbor)) {
					addPoint(neighbor);
				}
			}
		}

		return result;
	}

	private void addPoint(Point pt) 
	{
		result.add(pt);
		points.add(pt);
		traversed.add(pt);
	}
}