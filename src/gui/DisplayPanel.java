package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import domains.Fields.Field;
import domains.Machinery.Machine;
import geometry.Displayable;
import graphics.CanvasObject;
import graphics.Dimention;
import graphics.Map;
import tools.GoogleTools;

@SuppressWarnings("serial")
public class DisplayPanel extends JPanel{

	public static final String GROUP_FIELD = "field";
	public static final String GROUP_WP = "waypoints";
	public static final String GROUP_SEGMENT = "segment";
	
	public JLabel label = new JLabel("Please select field");

	private CanvasPanel canvas = new CanvasPanel(this);
	public Map map = null;
	public Field field;
	public Machine machine;
	
	public int zoom = 0;
	private java.util.Map<String, List<CanvasObject>> objectGroups;
	public double metersInPixel;
	
	public DisplayPanel() {
		super(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder("Display"));
		
		JPanel container = new JPanel(new BorderLayout());		
		container.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));				
		container.add(canvas);
				
		this.add(container, BorderLayout.CENTER);
		this.add(label, BorderLayout.SOUTH);
		objectGroups = new HashMap<>();
	}
	
	public void addDisplayObject(String objectGroupName, List<? extends Displayable> obj, Color color){
		if(obj == null) return;
		for(Displayable o : obj){
			addDisplayObject(objectGroupName, o, color);
		}
	}
	
	public void addDisplayObject(String objectGroupName, Displayable obj, Color color){
		if(obj == null) return;
		List<CanvasObject> list = objectGroups.get(objectGroupName);
		if(list == null){
			list = new ArrayList<>();
			objectGroups.put(objectGroupName, list);
		}
		
		list.add(canvas.createElement(obj, color));
	}
	
	public void clearDisplayObject(String objectGroup){
		canvas.removeAllElements(objectGroups.get(objectGroup));
	}

	public void render() {
		canvas.render();
	}

	public void setMapForArea(Dimention dimention) {
		zoom = GoogleTools.getBoundsZoomLevel(dimention.getNE(), dimention.getSW(), (int) canvas.getSize().getWidth(), (int)canvas.getSize().getHeight()) - 1;
		this.metersInPixel = GoogleTools.getMetersPerPixel(zoom, dimention.getCenter().getLatitude());
		this.map = new Map(dimention, canvas.getSize(), this);
	}

	public CanvasPanel getCanvas() {
		return canvas;
	}
}
