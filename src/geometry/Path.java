package geometry;

import java.util.ArrayList;
import java.util.List;

public class Path implements Displayable{
	private List<Point> waypoints;

	public Path() {
		waypoints = new ArrayList<>();
	}

	public Path(List<Point> waypoints) {
		this.waypoints = waypoints;
	}

	public List<Point> getWaypoints() {
		return waypoints;
	}

	public void setWaypoints(List<Point> waypoints) {
		this.waypoints = waypoints;
	}
	
	public void addWaypoint(Point p){
		waypoints.add(p);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((waypoints == null) ? 0 : waypoints.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Path other = (Path) obj;
		if (waypoints == null) {
			if (other.waypoints != null)
				return false;
		} else if (!waypoints.equals(other.waypoints))
			return false;
		return true;
	}

	public boolean containsAll(List<Point> waypoints2) {
		return waypoints.containsAll(waypoints2);
	}
	
	public double getTotalDistance(){
		double res = 0;
		for(int i= 0; i < waypoints.size() - 2; i++){
			res += waypoints.get(i).distanceTo(waypoints.get(i+1));
		}
		return res;
	}
	
}
