package geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import calculator.App;
import domains.Points;
import logginig.Logger;
import tools.GoogleTools;

public class Point implements Displayable{
	//public static final Point HOME = new Point(50.392621, 30.496226);
	protected static Logger logger = Logger.getLogger(Point.class);
	
	private double latitude;
	private double longitude;

	public Point(double latitude, double longitude) {
		super();
		this.latitude = round(latitude,App.COORDINATE_PRECISION);
		this.longitude = round(longitude,App.COORDINATE_PRECISION);
	}
	
	public static Point parsePoint(String latlong) {
		String[] split = latlong.split(", ");
		return new Point(round(Double.parseDouble(split[0]) ,App.COORDINATE_PRECISION)
				, round(Double.parseDouble(split[1]) ,App.COORDINATE_PRECISION));
	}
	
	public double distanceTo(Point p){
		
		double R = GoogleTools.RADIUS; // metres
		double φ1 = Math.toRadians(this.getLatitude());
		double φ2 = Math.toRadians(p.getLatitude());
		double Δφ = Math.toRadians(p.getLatitude() - this.getLatitude());
		double Δλ = Math.toRadians(p.getLongitude() - this.getLongitude());

		double a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
		        Math.cos(φ1) * Math.cos(φ2) *
		        Math.sin(Δλ/2) * Math.sin(Δλ/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

		double d = R * c;
		
		return d;
	}
	
	public Point moveTo(double direction, double distance){
		double dist = distance / GoogleTools.RADIUS;  
		double brng = toRad(direction);  
		
		double lat1 = toRad(this.getLatitude());
		double lon1 = toRad(this.getLongitude());
		
		double lat2 = Math.asin(Math.sin(lat1) * Math.cos(dist) + 
		                     Math.cos(lat1) * Math.sin(dist) * Math.cos(brng));
		
		double lon2 = lon1 + Math.atan2(Math.sin(brng) * Math.sin(dist) *
		                             Math.cos(lat1), 
		                             Math.cos(dist) - Math.sin(lat1) *
		                             Math.sin(lat2));
		
		if (Double.isNaN(lat2) || Double.isNaN(lon2)) return null;
		
		return new Point(toDeg(lat2), toDeg(lon2));
	}
	
	public static double toRad(double degree){
		return degree * Math.PI / 180;
	}
	
	public static double toDeg(double radians) {
		return radians * 180 / Math.PI;
	}

	public static Comparator<Point> getPointComparator(Line base){
		return new Comparator<Point>() {
			@Override
			public int compare(Point o1, Point o2) {
				Point intersection = base.getIntersectionWithLine(base.getPerprndicularAtPoint(o1));
				if(!intersection.equals(base.getIntersectionWithLine(base.getPerprndicularAtPoint(o2)))){
					logger.info("Impossible condition");
				}
				double diff = o1.distanceTo(intersection) - o2.distanceTo(intersection);
				return diff < 0 ? -1 : diff > 0 ? 1 : 0;
			}
		};
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	
	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
//	public Vector getVector(Point p){
//		CartesianPoint cOther = new CartesianPoint(GoogleTools.RADIUS, p);
//		CartesianPoint cThis = new CartesianPoint(GoogleTools.RADIUS, this);
//		
//		double componentA = cOther.x - cThis.x;
//		double componentB = cOther.y - cThis.y;
//		double componentC = cOther.z - cThis.z;
//		
//		return new Vector(componentA, componentB, componentC);
//		
//		return new Vector(0, p.latitude - this.latitude, p.longitude - this.longitude);
//	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
			return false;
		if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
			return false;
		return true;
	}

	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	@Override
	public String toString() {
		return "[" + latitude + ", " + longitude + "]";
	}
	
	public static Point getCenterOfMass(Point ...points){
		double sumLat = 0, sumLong = 0;
		for (int i = 0; i < points.length; i++) {
			sumLat += points[i].getLatitude();
			sumLong += points[i].getLongitude();
		}
		return new Point(sumLat/points.length, sumLong/points.length);
	}

	public Point getNearest(Point[] array) {
		if (array == null || array.length != 2) return null;
		double length1 = this.distanceTo(array[0]);
		double length2 = this.distanceTo(array[1]);
		
		return (length1 > length2) ? array[1] : array[0];
	}

	public static List<Point> createCollection(Collection<domains.Points.Point> collection) {
		List<Point> points = new ArrayList<>();
		for(Points.Point p : collection){
			points.add(new Point(p.lat, p.lon));
		}
		return points;
	}
}