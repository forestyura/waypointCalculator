package geometry;

import java.util.ArrayList;
import java.util.List;

import calculator.App;
import logginig.Logger;

public class Line implements Displayable{

	private double A;
	private double B;
	private double C;
	private static Logger logger = Logger.getLogger(Line.class);	
	
	private Line(double A, double B, double C) {
		super();
		this.A = A;
		this.B = B;
		this.C = C;
	}	

	/**
	 * Creates the Line containing the segment<br><br>
	 * Details:<br>
	 * (y-y1)/(y2-y1)=(x-x1)/(x2-x1)<br>
	 * (y1-y2)*x + (x2-x1)*y +(x1y2-x2y1)=0<br>
	 * Ax+By+C=0<br>
	 * y=-A/B*x - C/B<br>
	 * A=y1-y2<br>
	 * B=x2-x1<br>
	 * C=x1y2 - x2y1<br>
	 * @param segment for which line should be found
	 * @return Line containing the segment
	 */
	public Line(Segment segment) {
		logger .info(String.format("  Geting line for segment %s", segment));
		double x1,x2,y1,y2;
		x1= segment.getA().getLongitude();
		x2= segment.getB().getLongitude();
		y1= segment.getA().getLatitude();
		y2= segment.getB().getLatitude();
		
		this.A = y1 - y2;
		logger.trace(String.format("  A = y1 - y2 = %f - %f = %f", y1, y2, A));
		
		this.B = x2-x1;
		logger.trace(String.format("  B = x2 - x1 = %f - %f = %f", x2, x1, B));
		
		this.C = x1*y2 - x2*y1;
		logger.trace(String.format("  C = x1y2 - x2y1 = %f * %f - %f * %f = %f", y1, x2, y2, x1, C));
	}
	
	/**
	 * Returns new Line perpendicular to given that crosses specified Point<br>
	 * Source: http://www.pm298.ru/reshenie/peter.php<br>
	 * <br>
	 * A2=-B1<br>
	 * B2=A1<br>
	 * C2=-A2*x-B2*y<br>
	 * @param p - Point thetis being crossed by a perpendicular
	 * @return new Line perpendicular to original
	 */
	public Line getPerprndicularAtPoint(Point p){
		logger.trace(String.format("\n  Obtaining perpendicular for %s at %s: ", this, p));
		double Al = this.B;
		double Bl = -1.0 * this.A ;
		double Cl = (-1.0) * Al * p.getLongitude() - Bl * p.getLatitude() ;
		Line res = new Line(Al, Bl, Cl);
		logger.trace(String.format("  Perpendicular is %s", res));
		return res;
	}
	
	/**
	 * Finds an intersection point of <b>this</b> line with given line<br>
	 * <br>Details:<br><br>
	 * Point should belongs to both Lines so this can be described by system:<br>
	 * A1x + B1y + C1 = 0<br>
     * A2x + B2y + C2 = 0<br>
     * Using <a href="https://en.wikipedia.org/wiki/Cramer's_rule#Applications">Cramer's rule</a> we can find a solution<br>
     * source: http://e-maxx.ru/algo/lines_intersection
     * 
	 * @param l - the other line to find intersection with
	 * @return Point value where these line intersects or null if they are parallel
	 */
	public Point getIntersectionWithLine(Line l){
		logger.trace(String.format("\n  Obtaining intersectionpoint for lines:"));
		logger.trace(String.format("  \\/%s", this));
		logger.trace(String.format("  /\\%s", l));
		if((this.A * l.getB() - l.getA() * this.B) == 0){
			logger.trace(String.format("null"));
			return null; //Parallel
		}
		double lon = Point.round( -1.0 * (this.C * l.getB() - l.getC() * this.B) / (this.A * l.getB() - l.getA() * this.B), App.COORDINATE_PRECISION);
		double lat = Point.round( -1.0 * (this.A * l.getC() - l.getA() * this.C) / (this.A * l.getB() - l.getA() * this.B), App.COORDINATE_PRECISION);
		Point p = new Point(lat, lon);
		logger.trace(String.format("   =%s", p));
		return p;			
	}
	
	/**
	 * Segment should be used to display the line, as line is not real entity <br> 
	 * Two points are used to describe a box where the Line should be displayed<br>
	 * @param a is a coordinate of it's bottom left corner
	 * @param b is a coordinate of it's top right corner
	 * @return Segment which represents a line
	 */
	public Segment getSegmentForBox(Point a, Point b){
		
		Double x1,y1,x2,y2;
		
		x1 = a.getLatitude();
		y1 = -1 * A/B * x1 - C/B;
		
		x2 = b.getLatitude();
		y2 = -1 * A/B * x2 - C/B;
		
//		if(y1.isNaN() || y1.isInfinite() || y1 > b.getY() || y1 < a.getY()){
		if(y1.isNaN() || y1.isInfinite()){
			y1 = b.getLatitude();
			x1 = -1 * B/A * y1 - C/A;
			
			if(x1 > b.getLatitude() || x1 < a.getLatitude()){
				return null;
			}		
		}
//		if( y2.isNaN() || y2.isInfinite() || y2 > b.getY() || y2 < a.getY()){
		if( y2.isNaN() || y2.isInfinite()){
			y2 = a.getLatitude();
			x2 = -1 * B/A * y2 - C/A;
			
			if( x2 > b.getLatitude()  || x2 < a.getLatitude()){
				return null;
			}
		}
				
		return new Segment(new Point(x1,y1), new Point(x2,y2));
	}
	
	public Point getProjection(Point p){
		return this.getPerprndicularAtPoint(p).getIntersectionWithLine(this);
	}
	
	/**
	 * Calculate determination area of the list of Point on given Line<br>
	 * Basically searches a projection of each point of the list on the Line and calculates the outer points<br>
	 * @param list - the List of the points for which determination area is calculated
	 * @return An instance of a Segment containing the most outer points of projection on Line
	 */
	public Segment getProjection(Point ...list){
		Point maxP = null, maxD = null;
		double distance, max = 0;
		List<Point> proj = new ArrayList<>();
		for(Point p : list){
			proj.add(this.getProjection(p));
		}
		for(Point p : proj){
			for(Point d : proj){
				distance = p.distanceTo(d);
				if(distance > max){
					max = distance;
					maxP = p;
					maxD = d;
				}
			}
		}
		return new Segment(maxP, maxD);
	}
	
	/**
	 * Generates an instance of Horizontal line that contains Point(0,0)
	 * A=0, B=1, C=0
	 * 
	 * @param p - Point which in crossed by vertical
	 * @return an instance of the Line
	 */
	public static Line getHorizontal(Point p){
		double A = 0, B = 1, C;
		C = Point.round( (-1.0) * A * p.getLongitude() - B * p.getLatitude() , App.COORDINATE_PRECISION);
		return new Line(A, B, C);
	}
	
	/**
	 * Generates an instance of Vertical line that contains Point(0,0)
	 * A=1, B=0, C=0
	 * 
	 * @param p - Point which in crossed by vertical
	 * @return an instance of the Line
	 */
	public static Line getVertical(Point p){
		double A = 1, B = 0, C;
		C =  (-1.0) * A * p.getLongitude() - B * p.getLatitude();
		return new Line(A, B, C); 
	}

	public double getA() {
		return A;
	}

	public void setA(double a) {
		A = a;
	}

	public double getB() {
		return B;
	}

	public void setB(double b) {
		B = b;
	}

	public double getC() {
		return C;
	}

	public void setC(double c) {
		C = c;
	}

	@Override
	public String toString() {
		return "Line [A=" + A + ", B=" + B + ", C=" + C + "]";
	}
}