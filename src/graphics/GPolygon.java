package graphics;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import geometry.Point;
import geometry.Polygon;
import gui.CanvasPanel;

@SuppressWarnings("serial")
public class GPolygon extends java.awt.Polygon implements CanvasObject {
	private Color color;
	private CanvasPanel canvas;
	private geometry.Polygon polygon;
	
	public GPolygon(Polygon polygon, CanvasPanel canvas, Color color) {
		super();
		this.color = color;
		this.polygon = polygon;
		this.canvas = canvas;		 
	}

	@Override
	public void show(Graphics g) {
		this.reset();
		for(Point p : polygon){
			 this.addPoint(canvas.getDisplayX(p.getLongitude()), canvas.getDisplayY(p.getLatitude()));
		 }
		
		 Graphics2D g2 = (Graphics2D) g;
		 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		 g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f));
		 g2.setPaint(color);
		 
		 g2.fill(this);
		 g2.setPaint(new Color(color.getRed(), color.getGreen(), color.getBlue(), 255));
		 g2.draw(this);
	}
	

}
