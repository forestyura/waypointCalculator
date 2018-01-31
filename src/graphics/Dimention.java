package graphics;

import java.util.ArrayList;

import geometry.Line;
import geometry.Point;
import geometry.Segment;

public class Dimention{
	private Segment vOvf, hOvf, diagonal, squareDiagonal;
	private Point SW, NE, center;
	
	public Dimention(ArrayList<Point> points) {
		//rough center of mass represents average point concentration
		Point centerDummy = Point.getCenterOfMass(points.toArray(new Point[0]));
		//Segments progected on rough center of mass
    	Segment vOvfDummy = Line.getVertical(centerDummy).getProjection(points.toArray(new Point[0]));
		Segment hOvfDummy = Line.getHorizontal(centerDummy).getProjection(points.toArray(new Point[0]));
		
		double latMin, lonMin, latMax, lonMax;
		if(vOvfDummy.getA().getLatitude() < vOvfDummy.getB().getLatitude() ){
			latMin = vOvfDummy.getA().getLatitude();
			latMax = vOvfDummy.getB().getLatitude();
		}else{
			latMin = vOvfDummy.getB().getLatitude();
			latMax = vOvfDummy.getA().getLatitude();
		}
		
		if(hOvfDummy.getA().getLongitude() < hOvfDummy.getB().getLongitude() ){
			lonMin = hOvfDummy.getA().getLongitude();
			lonMax = hOvfDummy.getB().getLongitude();
		}else{
			lonMin = hOvfDummy.getB().getLongitude();
			lonMax = hOvfDummy.getA().getLongitude();
		}		
		
		this.SW = new Point(latMin, lonMin);
		this.NE = new Point(latMax, lonMax);
		this.diagonal = new Segment(SW, NE);
		
		this.center = Point.getCenterOfMass(diagonal.getA(), diagonal.getB());
		
		this.hOvf = Line.getHorizontal(center).getProjection(diagonal.getA(), diagonal.getB());
		this.vOvf = Line.getVertical(center).getProjection(diagonal.getA(), diagonal.getB());
		
		if (vOvf.getLength() == hOvf.getLength()){
			this.squareDiagonal = diagonal;
		}
		
		if(vOvf.getLength() < hOvf.getLength()){
			//Latitude is 2 times less then Longitude
			double factor = ((NE.getLongitude() - SW.getLongitude())/2)/2;
			this.squareDiagonal = new Segment(
					  new Point(center.getLatitude() - factor, SW.getLongitude())
					, new Point(center.getLatitude() + factor, NE.getLongitude()));
		}else{
			double factor = ((NE.getLatitude() - SW.getLatitude())/2)*2;
			this.squareDiagonal = new Segment(
					  new Point(SW.getLatitude(), center.getLongitude() - factor)
					, new Point(NE.getLatitude(), center.getLongitude() + factor)
					);
		}
	}
	
	public Segment getvOvf() {
		return hOvf;
	}

	public Segment gethOvf() {
		return vOvf;
	}
	
	public Segment getDiagonal() {
		return diagonal;
	}

	public Segment getSquareDiagonal() {
		return squareDiagonal;
	}

	public Point getSW(){
		return SW;
	}
	
	public Point getNE(){
		return NE;
	}

	public Point getCenter() {
		return center;
	}

}
