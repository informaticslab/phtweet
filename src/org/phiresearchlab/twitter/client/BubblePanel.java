/**
 * 
 */
package org.phiresearchlab.twitter.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.phiresearchlab.twitter.shared.TermHistory;
import org.phiresearchlab.twitter.shared.TermState;
import org.vaadin.gwtgraphics.client.Group;
import org.vaadin.gwtgraphics.client.VectorObject;
import org.vaadin.gwtgraphics.client.shape.Circle;
import org.vaadin.gwtgraphics.client.shape.Text;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 *
 * @author Joel M. Rives
 * May 24, 2011
 */
public class BubblePanel extends CanvasPanel {
	
	private TwitterServiceAsync twitterService = GWT.create(TwitterService.class);	
	private ToolTipPanel toolTip = ToolTipPanel.getInstance();
	
	private PHTweet controller;
	private Date currentTimestamp;
	
	public BubblePanel(PHTweet parent, int width, int height) {
		super(width, height);
		this.controller = parent;
		this.setStyleName("bubblePanel");
	}

	public void show(List<TermState> termStates, Date timestamp, int width, int height) {
		super.show(width, height);
		
		this.currentTimestamp = timestamp;
		this.toolTip.hide();
		
		List<Bubble> bubbles = generateBubbles(termStates, width, height);
		
		for (Bubble bubble: bubbles) {
			VectorObject vObject = createVectorObject(bubble, width, height);
			getCanvas().add(vObject);
		}
	}
	
	private VectorObject createVectorObject(final Bubble bubble, final int width, final int height) {
		Point point = bubble.getPoint();
		Circle circle = new Circle(point.getX(), point.getY(), (int) Math.round(bubble.getRadius()));
		circle.setFillColor(bubble.getColor());
		circle.setStrokeColor(bubble.getColor());
		circle.getElement().getStyle().setCursor(Cursor.POINTER);
		
		Group group = new Group();
		
		group.add(circle);
		
		VectorObject label = createLabel(bubble, circle);
		if (null != label)
			group.add(label);
		
		group.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int x = event.getClientX();
				int y = event.getClientY();
				int toolTipWidth = 150;
				int toolTipHeight = 120;
				
				if (x + toolTipWidth > width)
				    x -= toolTipWidth;
				
				if (y + toolTipHeight > height)
				    y -= toolTipHeight;

				TermHistory history = new TermHistory();
				history.setColor(bubble.getColor());
				history.setTerm(bubble.getTerm());
				history.setHourHits(-1);
				history.setDayHits(-1);
				history.setTotalHits(-1);
				toolTip.setPopupPosition(x, y);
				toolTip.show(history);
				
				twitterService.getTermHistory(bubble.getTerm(), currentTimestamp, new AsyncCallback<TermHistory>() {
					public void onFailure(Throwable caught) {
						Window.alert("Communication with the server has failed: " + caught.getMessage());						
					}

					public void onSuccess(TermHistory history) {
						if (!controller.viewByHour())
							history.setHourHits(-1);
						toolTip.setValues(history);
					}					
				});
			}			
		});
				
		return group;
	}
	
	private VectorObject createLabel(Bubble bubble, Circle circle) {
		if (circle.getRadius() < 10)
			return null;
		
		String text = bubble.getTerm();
		Text termLabel = new Text(0, circle.getY(), text);
		termLabel.setFontSize(((int)circle.getRadius()) / 2);
		termLabel.setFillColor("white");
		termLabel.setStrokeColor("white");
		termLabel.setFontFamily("Verdana");
		
		while (((double)termLabel.getTextWidth()) / (circle.getRadius() * 2.0) > 0.75)
			termLabel.setFontSize(termLabel.getFontSize() - 1);
		
		int labelWidth = termLabel.getTextWidth();
		termLabel.setX(circle.getX() - (labelWidth / 2));
		
		int value = bubble.getValue();
		NumberFormat form = NumberFormat.getDecimalFormat();
		text = form.format(value);
		Text countLabel = new Text(0, circle.getY() + termLabel.getTextHeight(), text);
		countLabel.setFontSize(termLabel.getFontSize() * 3 / 4);
		countLabel.setFillColor("white");
		countLabel.setStrokeColor("white");
		countLabel.setStrokeOpacity(0.0);
		countLabel.setFontFamily("Verdana");
		
		labelWidth = countLabel.getTextWidth();
		countLabel.setX(circle.getX() - (labelWidth / 2));
		
		Group label = new Group();
		label.add(termLabel);
		label.add(countLabel);
		
		return label;
	}
	
	private Point findFirstAvailable(boolean[][] grid, int boundary) {
		int half = (int) Math.round(((float)boundary) / 2.0);
		int width = grid.length;
		int height = grid[0].length;
		int xOffset = Random.nextInt(width);
		int yOffset = Random.nextInt(height);
		
		for (int h = 0;  h < height; h++) {
		    for (int w = 0; w < width; w++) {
		        int x = (w + xOffset) % width;
		        int y = (h + yOffset) % height;
		        
				if (grid[x][y])
					continue;
				
				Point point = new Point(x, y);
				
				if (isFree(grid, point, boundary)) {
					return point;
				}
			}
		}
		
		return new Point(grid.length / 2, grid[0].length / 2);
	}
	
	private List<Bubble> generateBubbles(List<TermState> termStates, int width, int height) {
		double totalArea = width * height;
		double totalValue = 0;
		
		for (TermState state: termStates) {
			if (state.isEnabled())
				totalValue += state.getHits();
		}
		
		List<Bubble> bubbles = new ArrayList<Bubble>();
		
		for (TermState state: termStates) {
			if (state.isEnabled()) {
			    int hits = state.getHits();
				double percent = hits / totalValue;
				double circleArea = totalArea * percent;
				double radius = Math.sqrt(circleArea / Math.PI);
				Bubble bubble = new Bubble(state.getTerm(), state.getColor(), state.getHits());
				bubble.setRadius(radius / 2.0);
				bubbles.add(bubble);
			}
		}
		
		return placeBubbles(bubbles, width, height);
	}
	
	private boolean isFree(boolean[][] grid, Point point, int boundary) {
		int half = (int) Math.round(((float)boundary) / 2.0);
		int left = point.getX() - half;
		int top = point.getY() - half;
		
		for (int w = left; w < left + boundary; w++) {
			for (int h = top; h < top + boundary; h++) {
			    if (0 > w || grid.length <= w)
			        return false;
			    
                if (0 > h || grid[0].length <= h)
                    return false;
                
				if (grid[w][h])
					return false;
			}
		}
		
		return true;
	}
	
	private void markGrid(boolean[][] grid, Point point, int boundary) {
		int half = (int) Math.round(((float)boundary) / 2.0);
		int top = point.getY() - half;
		int left = point.getX() - half;
		top = top < 0 ? 0 : top;
		left = left < 0 ? 0 : left;
		
		for (int w = left; w < left + boundary; w++) {
			for (int h = top; h < top + boundary; h++)
				grid[w][h] = true;
		}
	}
	
	private BoundingBox place(BoundingBox container, int width, int height) {
		int xPos = Random.nextInt(container.getWidth() - width);
		int yPos = Random.nextInt(container.getHeight() - height);
		BoundingBox box = new BoundingBox(xPos, yPos, width, height);
		container.placeNotOverlapping(box);		
		return box;
	}
	
	private List<Bubble> placeBubbles(List<Bubble> bubbles, int width, int height) {
		bubbles = sortBubbles(bubbles);		
		BoundingBox container = new BoundingBox(0, 0, width, height);
		
		for (Bubble bubble: bubbles) {
			int boundary = (int) Math.round(bubble.getRadius() * 2.25);		
			BoundingBox box = place(container, boundary, boundary);
			Point point = new Point(box.getX() + (box.getWidth() / 2), box.getY() + (box.getHeight() / 2));
			bubble.setPoint(new Point(point));
		}
		
		return bubbles;
	}
	
	private List<Bubble> sortBubbles(List<Bubble> bubbles) {
		// Since the list is small and the name fits the case, we will use a Bubble Sort
		Bubble[] list = bubbles.toArray(new Bubble[bubbles.size()]);
		boolean swapped = false;
		do {
			swapped = false;
			for (int i = 0; i < list.length - 1; i++) {
				Bubble first = list[i];
				Bubble second = list[i+1];
				if (second.getRadius() > first.getRadius()) {
					list[i] = second;
					list[i+1] = first;
					swapped = true;
				}
			}
		} while(swapped);
		
		bubbles.clear();
		for (Bubble bubble: list)
			bubbles.add(bubble);
		
		return bubbles;
	}
	
	private class Bubble {
		private String term;
		private String color;
		private int value;
		private double radius;
		private Point point;
		
		public Bubble(String term, String color, int value) {
			this.term = term;
			this.color = color;
			this.value = value;
			this.radius = 0.0;
			point = new Point(0, 0);
		}

		public String getTerm() {
			return term;
		}

		public String getColor() {
			return color;
		}

		public int getValue() {
			return value;
		}

		public double getRadius() {
			return radius;
		}

		public void setRadius(double radius) {
			this.radius = radius;
		}

		public Point getPoint() {
			return point;
		}

		public void setPoint(Point point) {
			this.point = point;
		}

	};
	
	private class Point {
		private int x;
		private int y;
		
		public Point() {
			this(0, 0);
		}
		
		public Point(int x, int y) {
			setLocation(x, y);
		}
		
		public Point(Point point) {
			setLocation(point);
		}

		private double distanceFrom(Point point) {
			int lengthX = x - point.getX();
			int lengthY = y - point.getY();
			return Math.sqrt(lengthX * lengthX + lengthY * lengthY);
		}
		
		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
		
		public void setLocation(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public void setLocation(Point point) {
			this.x = point.x;
			this.y = point.y;
		}
		
		public String toString() {
			return "[" + x + ", " + y + "]";
		}
	}
}
