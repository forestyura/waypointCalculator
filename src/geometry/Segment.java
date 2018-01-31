package geometry;

import java.util.ArrayList;
import java.util.List;

import calculator.App;
import logginig.Logger;

public class Segment implements Displayable{
	private Point a;
	private Point b;
	private double length;
	private static Logger logger = Logger.getLogger(Segment.class);
	
	public Segment() {
		super();
	}
	public Segment(Point a, Point b) {
		super();
		this.a = a;
		this.b = b;
		length = Point.round(a.distanceTo(b), App.COORDINATE_PRECISION + 2);
		logger.info(String.format("  Segment created: %s", this));
	}
	
	/*
	 * 1) Поділити відрізок з координатами x1,x2 на n частин
	 */
	public List<Point> devideSegment(int n){
		logger.info(String.format("\nDeviding segment %s into %d parts", this, n));
		List<Point> subSegment = new ArrayList<>();
		for(int i = 1; i <= n-1; i++){
			logger.info(String.format("  Calculating devition point %d", i));
			double R = 1.0*i / (n - i);
			logger.info(String.format("    Ratio is %d/%d = %f", i, n, R));
			double xM = (this.a.getLatitude() + R * this.b.getLatitude()) / (1 + R); 
			double yM = (this.a.getLongitude() + R * this.b.getLongitude()) / (1 + R);
			
			Point p = new Point(xM, yM);
			logger.info(String.format("    Got point %s", p));
			subSegment.add(p);
			}
		return subSegment;
	}
	
	public Line getLine(){
		return new Line(this);
	}
		
	/*
	 * Чи належить точка відрізку?
	 * чкщо сума відстаней до початку і кінця відрізку дорівнює довжині відрізку то ледить 
	 */
	public boolean contains(Point p){
		logger .info(String.format("\n  Does %s contains %s?\t", this, p));
		double da = p.distanceTo(a);
		double db = p.distanceTo(b);
		boolean bool = Math.abs(length - (da + db)) < 0.01; //MAGIC
		logger.trace(String.format("  %f = %f?", length, da  + db));
		logger.debug(String.format("  diff = %f", length - (da  + db)));
		return bool;
	}
//	public boolean contains(GeoPoint c){
//		logger .debug(String.format("\n  Does %s contains %s?\t", this, c));
//		Vector AB = a.getVector(b);
//		Vector AC = a.getVector(c);
//		Vector ABxAC = AB.crossProduct(AC);
//		logger.debug("Cross product: " + ABxAC);
//		logger.debug("\tLength: " + ABxAC.getLength());
//		if(!ABxAC.isZeroVector()){
//			return false;
//		}
//		
//		double Kab = GeoPoint.round(AB.dotProduct(AB), 1);
//		double Kac = GeoPoint.round(AB.dotProduct(AC), 1);
//		double diff = Kab - Kac;
//		logger.debug(String.format("\tKab = %f diff = %f ?", Kab, diff));
//
//		return Kac >= 0 && diff >= 0;
//	}
	
	public Point getA() {
		return a;
	}

	public Point getB() {
		return b;
	}

	public double getLength() {
		return length;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Segment other = (Segment) obj;
		if((this.a.equals(other.a) && this.b.equals(other.b)) || (this.a.equals(other.b) && this.b.equals(other.a)) ){
			return true;
		}else{
			return false;
		}
	}
	@Override
	public String toString() {
		return "Segment [a=" + a + ", b=" + b + ", length=" + length + "]";
	}	
}