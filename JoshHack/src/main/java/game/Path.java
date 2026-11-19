package game;

import java.util.ArrayList;
import java.util.List;

public class Path {

	private static PathFinder pf = new PathFinder();
	
	private List<Point> points;
	public List<Point> points() {
		return points == null ? null : new ArrayList<Point>(points);
	}
	
	public Path(Creature creature, int x, int y){
		points = pf.findPath(creature, new Point(creature.x, creature.y, creature.z), new Point(x, y, creature.z), 300);
	}
}
