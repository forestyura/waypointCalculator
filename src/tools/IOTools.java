package tools;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import graphics.Dimention;
import logginig.Logger;

public class IOTools {
	private static Logger logger = Logger.getLogger(IOTools.class);
	
	/**
	 * Returns image of map which fits given polygon on canvas size. Or returns default image 
	 * @param pattern
	 * @param canvasSize
	 * @param defaultPath
	 * @return
	 */
	public static BufferedImage getMapImage(String pattern, Dimention ovf, Dimension canvasSize, int zoom, String defaultPath) {
		BufferedImage result = new BufferedImage(1240, 1240, BufferedImage.TYPE_INT_RGB);
		BufferedImage img;
		if(ovf == null || canvasSize == null) {				
			img = readImageFromUrl(defaultPath);
			result = (img == null) ? result : img; 
		}else{
			double lat = ovf.getCenter().getLatitude();
	        double lon = ovf.getCenter().getLongitude();
	        
			int size_w = 620;
			int size_h = 620;
        
        	String url = String.format(pattern, lat, lon, zoom, size_w, size_h);
        	img = readImageFromUrl(url);
        	logger.info("Loaded map image from: " + url);
        	
        	result = (img == null) ? readImageFromUrl(defaultPath) :  img;	    			
		}			

		return result;
	}
	
	/** Returns an ImageIcon, or null if the path was invalid. 
	 * @throws IOException */
	public static BufferedImage readImageFromUrl(String url){
	    if (url != null) {
	        try {
	        	if(url.startsWith("http")){
	        		return ImageIO.read(new URL(url));
	        	}else{
	        		return ImageIO.read(IOTools.class.getClassLoader().getResourceAsStream(url));
	        	}				
			} catch (IOException | IllegalArgumentException e) {
				logger.info("Cannot read URL[" + new File(url) + "]: " + e.getMessage());
				StackTraceElement[] stuck = e.getStackTrace();
				for(int i = 0; i < stuck.length; i++){
					logger.info("\tat " + stuck[i].toString());
				}
			}
	    } else {
	        System.err.println("Image URL is null");
	    	return null;
	    }
		return null;
	}
}
