package graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Path2D;

import geometry.Path;
import graphics.strokes.ShapeStroke;
import gui.CanvasPanel;
import tools.GoogleTools;

@SuppressWarnings("serial")
public class GPath extends Path2D.Float implements CanvasObject {

	private CanvasPanel canvas;
	private Path waypoints;
	private Color color;

	public GPath(Path waypoints, CanvasPanel jaCanvas, Color color) {
		super();
		this.waypoints = waypoints;
		this.canvas = jaCanvas;
		this.color = color;
	}

	//TODO Rewrite this method to add line overlay(by implementing strokes)
	@Override
	public void show(Graphics g) {
		this.reset();
		if(waypoints == null || waypoints.getWaypoints().size() < 2){
			return;
		}
		
		int brushSize = (int) (canvas.display.machine.workWidth
				                       * GoogleTools.getMetersPerPixel(canvas.display.zoom - 1
				, canvas.display.map.center.getLatitude()));
		
		this.moveTo(canvas.getDisplayX(waypoints.getWaypoints().get(0).getLongitude()), 
				canvas.getDisplayY(waypoints.getWaypoints().get(0).getLatitude()));
		for(int i=1; i < waypoints.getWaypoints().size(); i++){
			if(waypoints.getWaypoints().get(i) == null) continue;
			this.lineTo(canvas.getDisplayX(waypoints.getWaypoints().get(i).getLongitude()), 
					canvas.getDisplayY(waypoints.getWaypoints().get(i).getLatitude()));			
		 }
		 Graphics2D g2 = (Graphics2D) g;
		 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		 
		 ShapeStroke arrowStroke = new ShapeStroke(new Shape[] {(Shape) createArrow(brushSize, brushSize * 3)}, 300);
//		 g2.setPaint(Color.BLACK);
//		 g2.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//		 g2.draw(this);
			
		 g2.setPaint(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
		 g2.setStroke(new BasicStroke(brushSize - 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		 g2.draw(this);
			
		 g2.setPaint(Color.BLACK);
		 g2.setStroke(arrowStroke);
		 g2.draw(this);
		
	}
	
	private Path2D.Double createArrow(int base, int length) {        
        Path2D.Double path = new Path2D.Double();
        path.moveTo(0, -base/2);
        path.lineTo(0, base/2);
        path.lineTo(length, 0);
        path.lineTo(0, -base/2);        
        return path;
    }	

}
