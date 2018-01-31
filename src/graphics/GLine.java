package graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;

import geometry.Line;
import geometry.Segment;
import gui.CanvasPanel;

@SuppressWarnings("serial")
public class GLine  extends Line2D.Float implements CanvasObject {
	private Color color;
	private int size = 1;
	private CanvasPanel canvas;
	private Line line;
	
	public GLine(Line line, CanvasPanel canvas, Color color) {
		super();
		this.line = line;
		this.color = color;
		this.canvas = canvas;
	}
	
	public void show(Graphics g){
		Segment s = line.getSegmentForBox(canvas.display.map.getSW(), canvas.display.map.getNE());
		if(s == null) return;
		
		this.setLine(canvas.getDisplayX(s.getA().getLongitude())
				, canvas.getDisplayY(s.getA().getLatitude())
				, canvas.getDisplayX(s.getB().getLongitude())
				, canvas.getDisplayY(s.getB().getLatitude()));
	
		 Graphics2D g2 = (Graphics2D) g;
		 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		 
		 g2.setStroke(new BasicStroke(size));
		 g2.setPaint(color);
		 g2.draw(this);		 
	}

}
