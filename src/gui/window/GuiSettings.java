package gui.window;

import java.awt.Color;

public class GuiSettings {
	private Color figureFillColor = Color.GRAY;
	private Color figurePointColor = Color.GRAY;
	private Color segmentLineColor = Color.GRAY;
	private Color ovfColor = Color.GRAY;
	private Color figureBaseColor = Color.GRAY;
	private Color intersectionColor = Color.GRAY;
	
	private byte displayMask = 0;

	public GuiSettings() {
	}

	public Color getFigureFillColor() {
		return figureFillColor;
	}

	public void setFigureFillColor(Color figureFillColor) {
		this.figureFillColor = figureFillColor;
	}

	public Color getFigurePointColor() {
		return figurePointColor;
	}

	public void setFigurePointColor(Color figurePointColor) {
		this.figurePointColor = figurePointColor;
	}

	public Color getSegmentLineColor() {
		return segmentLineColor;
	}

	public void setSegmentLineColor(Color segmentLineColor) {
		this.segmentLineColor = segmentLineColor;
	}

	public Color getOvfColor() {
		return ovfColor;
	}

	public void setOvfColor(Color ovfColor) {
		this.ovfColor = ovfColor;
	}

	public Color getFigureBaseColor() {
		return figureBaseColor;
	}

	public void setFigureBaseColor(Color figureBaseColor) {
		this.figureBaseColor = figureBaseColor;
	}

	public Color getIntersectionColor() {
		return intersectionColor;
	}

	public void setIntersectionColor(Color intersectionColor) {
		this.intersectionColor = intersectionColor;
	}

	public byte getDisplayMask() {
		return displayMask;
	}

	public void setDisplayMask(byte displayMask) {
		this.displayMask = displayMask;
	}
}
