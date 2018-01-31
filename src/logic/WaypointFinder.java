package logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import geometry.Line;
import geometry.Path;
import geometry.Point;
import geometry.Polygon;
import geometry.Segment;
import logginig.Logger;

public class WaypointFinder {

	private static Logger logging = Logger.getLogger(WaypointFinder.class);
	
	private List<Point> waypoints = new ArrayList<>(); 
	private Path path = null;
	Map<Segment, List<Point>> intersectionsSegment = new HashMap<>();
	Map<Line, Set<Point>> intersectionsLine = new HashMap<>();
	Map<Point, Line> pointLine = new HashMap<>();
	List<Line> divisionLines = new ArrayList<>();
	
	public Segment ovf;

	private List<Segment> formSegments;
	
	public WaypointFinder(List<domains.Points.Point> points, double width) {
		
		Polygon poly = new Polygon(points);
		/*
		 *1) Для кожноі точки formPoints[n] і formPoints[n+1] отримати відоізки які вони утворюють: formSegments 
		 */
		formSegments = poly.getSegments();
		logging.info(String.format("---------------------------"));
		
		/*
		 * 2)Знайти область визначення фігури відносно відрізка base
		 * Отримаємо пряму на якій ледить відр. base
		 */
		Segment longest = null;
		for(Segment s : formSegments){
			if(longest == null || s.getLength() > longest.getLength()) longest = s; 
		}
		Line baseLine = longest.getLine().getPerprndicularAtPoint(poly.getDimention().getCenter());
		ovf = baseLine.getProjection(poly.toArray(new Point[0]));
		
		/*
		 * 3) Поділити відрізок ovf на devidor, знайти координати точок поділу: devisionPoints[u-1]
		 */
		int sections = (int) (ovf.getLength() / width);
		if(ovf.getLength() % width != 0){
			sections++;
		}
		List<Point> devisionPoints = ovf.devideSegment(sections);
		logging.info(String.format("---------------------------"));
				
		/*
		 * 4) Для кожної з прямих devisionLines[m] для кожної з ліній на яких лежать відрізки formSegments[n] Визначити точки перетину які вони утвоюють...
		 * Для кожної точки перетину визначити чи належить вона відрізку formSegments[n]
		 */				
		
		for(Point dp : devisionPoints){
			
			logging.info(String.format("\nCreating perpendicular for devision point %s", dp));
			Line dl = ovf.getLine().getPerprndicularAtPoint(dp);
			divisionLines.add(dl);
			Set<Point> linePoint;
			if(intersectionsLine.get(dl) == null){
				linePoint = new HashSet<>();
				intersectionsLine.put(dl, linePoint);
			}else{
				linePoint = intersectionsLine.get(dl);
			}
			
			logging.info(String.format("\nDevision line %s", dl));
			for(Segment segment : formSegments){
				List<Point> segmentPoint;
				if(intersectionsSegment.get(segment) == null){
					segmentPoint = new ArrayList<>();
					intersectionsSegment.put(segment, segmentPoint);
				}else{
					segmentPoint = intersectionsSegment.get(segment);
				}
				
				logging.debug(String.format("  Find intersection point with segment line %s:\t", segment));
				Point p = segment.getLine().getIntersectionWithLine(dl);
				if(p == null){
					logging.info(String.format("  Parallel"));
					continue;
				}
				if(segment.contains(p)){
					logging.info(String.format("  Belongs to segment\n"));
					segmentPoint.add(p);
					linePoint.add(p);
					pointLine.put(p, dl);
					waypoints.add(p);
				}else{
					logging.info(String.format("  Does not belongs to segment\n"));
				}
			}			
						
		}
		
		if(waypoints.size() > 1){
			path = new Path();
		}			

		//TODO Consider changing method of collection resulting points
		Point startPoint = waypoints.get(0);
		addSequence(startPoint);
					
		logging.info(String.format("---------------------------"));
				
		for(Point p : waypoints){
			logging.info(p.toString());
		}	
	}


	private void addSequence(Point start) {		
		Line l = pointLine.get(start);
		path.addWaypoint(start);
		if (start == null || path.containsAll(waypoints)) return;
		
		Iterator<Point> iterator = intersectionsLine.get(l).iterator();
		while(iterator.hasNext()){
			Point next = iterator.next();
			if(!next.equals(start)) {
				path.addWaypoint(next);
								
				if(divisionLines.indexOf(l) < divisionLines.size() - 1){
					Point nearest = next.getNearest(intersectionsLine.get(divisionLines.get(divisionLines.indexOf(l) + 1) ).toArray(new Point[0]));
					addSequence(nearest);
				}
			}
		}
	}


	public List<Point> getWaypoints() {
		return waypoints;
	}


	public Path getPath() {
		return path;
	}
}
