/**
 * 
 */
package org.phiresearchlab.twitter.client;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.shape.Text;

import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 *
 * @author Joel M. Rives
 * Aug 1, 2011
 */
public abstract class CanvasPanel extends AbsolutePanel {
	
	private DrawingArea canvas;
	
	public CanvasPanel(int width, int height) {
		canvas = new DrawingArea(width, height);
		add(canvas);
	}
	
	public void clear() {
		canvas.clear();
	}
	
	public void show(int width, int height) {
		clear();
		DrawingArea canvas = getCanvas();

		if (width != canvas.getWidth() || height != canvas.getHeight()) {
			canvas.setHeight(height);
			canvas.setWidth(width);
		}
	}
	
	public void showMessage(String message) {
		Text text = new Text(0, 0, message);
		text.setFillColor("black");
		text.setStrokeColor("black");
		text.setFontFamily("Verdana");
		text.setFontSize(20);
		int x = (canvas.getWidth() / 2) - (text.getOffsetWidth() / 2);
		int y = (canvas.getHeight() / 3) - (text.getOffsetHeight() / 2);
		text.setX(x);
		text.setY(y);
		canvas.add(text);
	}
	
	protected DrawingArea getCanvas() {
		return canvas;
	}
	
}
