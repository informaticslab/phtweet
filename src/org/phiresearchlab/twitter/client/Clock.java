package org.phiresearchlab.twitter.client;

import java.util.Date;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Image;
import org.vaadin.gwtgraphics.client.Line;

import com.google.gwt.user.client.ui.AbsolutePanel;

public class Clock extends AbsolutePanel {

	private DrawingArea canvas;
	private int centerX;
	private int centerY;
	
	public Clock(int width, int height) {
		canvas = new DrawingArea(width, height);
		centerX = width / 2;
		centerY = height / 2;
		
		Image clockFace = new Image(0, 0, width, height, "images/header/clock.png");
		canvas.add(clockFace);
		
		add(canvas);
	}
	
	public void setTime(Date timestamp) {
		int hour = timestamp.getHours();
	}
	
	private void drawHourHand(int degrees) {
		
		Line hand = new Line(centerX, centerY, 0, 0);
	}
	
}
