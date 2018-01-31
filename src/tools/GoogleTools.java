package tools;

import geometry.Point;
import logginig.Logger;

public class GoogleTools {
	public static Logger logger = Logger.getLogger(GoogleTools.class);
	public final static int RADIUS = 6378137;
	public final static int GLOBE_WIDTH = 256; // a constant in Google's map projection
	public final static int ZOOM_MAX = 21;
	public final static String URL_PATTERN = "https://maps.googleapis.com/maps/api/staticmap?center=%s,%s&zoom=%d&size=%dx%d&scale=2&maptype=hybrid&format=jpg";


	public static int getBoundsZoomLevel(Point northeast,Point southwest, int width, int height) {
	    double latFraction = (latRad(northeast.getLatitude()) - latRad(southwest.getLatitude())) / Math.PI;
	    double lngDiff = northeast.getLongitude() - southwest.getLongitude();
	    double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;
	    double latZoom = zoom(height, GLOBE_WIDTH, latFraction);
	    double lngZoom = zoom(width, GLOBE_WIDTH, lngFraction);
	    double zoom = Math.min(Math.min(latZoom, lngZoom),ZOOM_MAX);
	    return (int)(zoom);
	}
	private static double latRad(double lat) {
	    double sin = Math.sin(lat * Math.PI / 180);
	    double radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
	    return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
	}
	private static double zoom(double mapPx, double worldPx, double fraction) {
	    final double LN2 = .693147180559945309417;
	    return (Math.log(mapPx / worldPx / fraction) / LN2);
	}	
	
//	public static double getMetersPerPixel(int zoom, double latitude){
//		return 156543.03392 * Math.cos(latitude * Math.PI / 180) / Math.pow(2, zoom);
//		WRONG
//	}

	//TODO Stroke width is calculated incorrectly
	public static double getMetersPerPixel(int zoom, double latitude){
		double factor = 0.009330692;
		for(int refZoom = 24; refZoom > zoom + 1; refZoom--){
			factor = factor * 2;
		}
		
		return factor * Math.cos(latitude * Math.PI / 180) ;
		
	}
}
