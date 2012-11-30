/**
 * 
 */
package org.phiresearchlab.twitter.client;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.phiresearchlab.twitter.domain.Category;
import org.phiresearchlab.twitter.domain.FilterTerm;
import org.phiresearchlab.twitter.shared.Period;
import org.phiresearchlab.twitter.shared.TermReport;
import org.vaadin.gwtgraphics.client.Group;
import org.vaadin.gwtgraphics.client.Image;
import org.vaadin.gwtgraphics.client.Line;
import org.vaadin.gwtgraphics.client.Shape;
import org.vaadin.gwtgraphics.client.VectorObject;
import org.vaadin.gwtgraphics.client.shape.Circle;
import org.vaadin.gwtgraphics.client.shape.Rectangle;
import org.vaadin.gwtgraphics.client.shape.Text;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 *
 * @author Joel M. Rives
 * Aug 1, 2011
 */
public class LineGraphPanel extends CanvasPanel implements ToolTipHandler {
	
	private static final int LEFT_BAR_WIDTH = 60;
	private static final int BOTTOM_BAR_HEIGHT = 40;
	
	private static final long ONE_DAY = 1000 * 60 * 60 * 24;
	
	private boolean showAM = true;
	private int verticalScale;
	private Date currentTimestamp;
	private ColorMapper colorMap = ColorMapper.getInstance();
	private Map<String, Group> termGroupMap = new HashMap<String, Group>();
	private boolean faded = false;
	private Category currentCategory;
	private List<TermReport> currentTermReports;

	public LineGraphPanel(int width, int height) {
        super(width, height);
	}
	
	public void onToolTipHide() {
		unfade();
	}
	
	@SuppressWarnings("deprecation")
	public void show(Category category, List<TermReport> termReports, Date timestamp, int width, int height) {
		super.show(width, height);
		
		currentCategory = category;
		currentTermReports = termReports;
	    currentTimestamp = timestamp;
	    showAM = timestamp.getHours() < 12;
		Period period = termReports.get(0).getPeriod();		
		verticalScale = calculateMaxHits(category, termReports, period, width);		
		createGrid(period, width, height);
		
		for (TermReport report: termReports) {
            FilterTerm filterTerm = category.findFilterTerm(report.getTerm());
            if (filterTerm.getEnabled() == true) 
                drawLineGraph(period, report, width, height);
		}
	}
	
	@SuppressWarnings("deprecation")
	private int calculateMaxHits(Category category, List<TermReport> termReports, Period period, int width) {
		int maxHits = 0;
		Date now = new Date();
		int firstHour = showAM ? 0 : 12;
		int currentHour = now.getHours();
		int minutes = now.getMinutes();
		
		for (TermReport report: termReports) {
		    FilterTerm filterTerm = category.findFilterTerm(report.getTerm());
		    if (filterTerm.getEnabled() == true) {
    			for (int i = 0; i < report.getHits().size(); i++) {
    				int hits = report.getHits().get(i);
    				int hour = firstHour + i;
    				
    				if (period == Period.Hour && hour == (currentHour + 1) && minutes > 0) {
    	    				hits = 60 / minutes * hits;
    	    		}

    				if (hits > maxHits)
    					maxHits = hits;
    			}
		    }
		}

		return maxHits;
	}
	
	private void createGrid(Period period, int width, int height) {
		Rectangle matte = new Rectangle(0, 0, width, height);
		matte.setFillColor("#fad887");
		getCanvas().add(matte);
		
		Rectangle grid = new Rectangle(LEFT_BAR_WIDTH, 0, width - LEFT_BAR_WIDTH, height - BOTTOM_BAR_HEIGHT);
		getCanvas().add(grid);
		
		createHorizontalLines(width, height);
		createVerticalLines(period, width, height);
	}
	
	private void createHorizontalLines(int width, int height) {
		int verticalIncrement = (height - BOTTOM_BAR_HEIGHT) / 15;
		int verticalScaleIncrement = verticalScale / 14;
		int left = LEFT_BAR_WIDTH - 10;
		int bottom = height - BOTTOM_BAR_HEIGHT;
		
		for (int i = 0; i < 15; i++) {
			int y = bottom - (i * verticalIncrement);
			Line line = new Line(left, y, width, y);
			line.setStrokeWidth(1);
			line.setStrokeColor("gray");
			getCanvas().add(line);
			
			Text label = new Text(0, y + 4, "" + (i * verticalScaleIncrement));
			label.setFontFamily("Verdana");
			label.setFontSize(10);
			label.setX(left - label.getTextWidth() - 4);
			getCanvas().add(label);
		}		
	}
	
	private void createVerticalLines(Period period, int width, int height) {
		int horizontalIncrement = (width - LEFT_BAR_WIDTH) / 14;
		int left = LEFT_BAR_WIDTH;
		int bottom = height - BOTTOM_BAR_HEIGHT + 10;
		Date date = new Date(currentTimestamp.getTime() - (13 * ONE_DAY));
		
		for (int i = 0; i < 14; i++) {
			int x = left + (i * horizontalIncrement);
			Line line = new Line(x, bottom, x, 0);
			line.setStrokeWidth(1);
			line.setStrokeColor("gray");
			getCanvas().add(line);

			String text = "";
			if (period == Period.Hour) {
    			text = i + ":00" + (showAM ? "AM" : "PM");
    			if (i == 0)
    				text = "12:00" + (showAM ? "AM" : "PM");
    			if (i == 12) 
    				text = "12:00" + (showAM ? "PM" : "AM");
    			if (i == 13)
    				continue;
			} else {
			    int currentMonth = date.getMonth() + 1;
			    int currentDay = date.getDate();
			    text = currentMonth + "/" + currentDay;
			    date = new Date(date.getTime() + ONE_DAY);
			}
			
			Text label = new Text(x - 4, bottom + 10, text);
			label.setFontFamily("Verdana");
			label.setFontSize(10);
			getCanvas().add(label);
		}
	}
	
	private Group createLabel(int x, int y, final TermReport report) {
		if (0 == x || 0 == y)
			return null;

	    String href = "images/graph/" + colorMap.getColor(report.getColor()) + ".png";
	    Image image = new Image(x + 10, y - 11, 100, 22, href);
		
	    Text label = new Text(0, y + 4, report.getTerm());
		label.setFontFamily("Verdana");
		label.setFontSize(12);
		label.setStrokeOpacity(0.0);
		label.setFillColor("white");
		label.setX(x + ((100 - label.getTextWidth()) / 2) + 10);
		
		Group labelGroup = new Group();
		labelGroup.add(image);
		labelGroup.add(label);
		labelGroup.bringToFront(label);
		labelGroup.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (faded) {
					unfade();
					faded = false;
				} else {
					fade(report.getTerm());
					faded = true;
				}
			}				
		});
	    labelGroup.getElement().getStyle().setCursor(Cursor.POINTER);
		
		return labelGroup;
	}
	
	@SuppressWarnings("deprecation")
	private void drawLineGraph(Period period, final TermReport report, int width, int height) {
		int horizontalIncrement = (width - LEFT_BAR_WIDTH) / 14;
		double offsetHeight = ((double)(height - BOTTOM_BAR_HEIGHT)) * 14 / 15;
		double verticalIncrement = offsetHeight / verticalScale;
		int left = LEFT_BAR_WIDTH;
		int bottom = height - BOTTOM_BAR_HEIGHT;
		int previousX = 0;
		int previousY = 0;
		Date now = new Date();
		Group  termGroup = new Group();
		int max = period == Period.Day ? 14 : 13;
		
		for (int i = 0; i < max; i++) {
			int hour = i + (showAM ? 0 : 12);
			int index = i;
			int horizontalOffset = 0;
			
			int hits = report.getHits().get(index);

			if (period == Period.Hour) {
    			if (currentTimestamp.getYear() == now.getYear() && 
    			    currentTimestamp.getMonth() == now.getMonth() &&
    			    currentTimestamp.getDate() == now.getDate() &&
    			    hour > now.getHours()) {
    				if (hour > now.getHours() + 1)
    					continue;
    				
    				int minutes = now.getMinutes();
    					
    				if (0 == minutes)
    					continue;
    					
    				double minuteIncrement = ((double) horizontalIncrement) / 60.0;
    				horizontalOffset = (int) (minuteIncrement * (double) (60 - minutes));
    				hits = 60 / minutes * hits;
    			}
			}
			
			int x = left + (i * horizontalIncrement) - horizontalOffset;
			int y = bottom - ((int) (hits * verticalIncrement));
			Circle dot = new Circle(x, y, 5);
			dot.setFillColor(report.getColor());
			dot.setStrokeColor(report.getColor());
			dot.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					if (faded) {
						unfade();
						faded = false;
					} else {
						fade(report.getTerm());
						faded = true;
					}
				}				
			});
			termGroup.add(dot);
			
			if (0 != previousX) {
				Line line = new Line(previousX, previousY, x, y);
				line.setStrokeColor(report.getColor());
				line.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						if (faded) {
							unfade();
							faded = false;
						} else {
							fade(report.getTerm());
							faded = true;
						}
					}				
				});
				termGroup.add(line);
			}
			
		    termGroup.getElement().getStyle().setCursor(Cursor.POINTER);
			
			previousX = x;
			previousY = y;
		}
		
		Group labelGroup = createLabel(previousX, previousY, report);
		
		if (null != labelGroup)
			termGroup.add(labelGroup);
		
		termGroupMap.put(report.getTerm(), termGroup);
		getCanvas().add(termGroup);		
	}
	
	private void fade(String term) {
		for (String key: termGroupMap.keySet()) {
			if (key.equals(term))
				continue;
			
			double opacity = 0.3;
			Group termGroup = termGroupMap.get(key);
			for (int i = 0; i < termGroup.getVectorObjectCount(); i++) {
				VectorObject vo = termGroup.getVectorObject(i);
				if (vo instanceof Group) {
					Group group = (Group) vo;
					Image image = (Image) group.getVectorObject(0);
					image.getElement().getStyle().setOpacity(opacity);
					Text label = (Text) group.getVectorObject(1);
					label.setFillOpacity(opacity);
				} else if (vo instanceof Shape) {
					Shape shape = (Shape) vo;
					shape.setFillOpacity(opacity);
					shape.setStrokeOpacity(opacity);
				} else if (vo instanceof Line) {
					Line line = (Line) vo;
					line.setStrokeOpacity(opacity);
				}
			}
		}
	}
	
	private void unfade() {
		for (String key: termGroupMap.keySet()) {
			Group termGroup = termGroupMap.get(key);
			getCanvas().remove(termGroup);
		}
		
		int width = getCanvas().getWidth();
		int height = getCanvas().getHeight();
	    Period period = currentTermReports.get(0).getPeriod();     

		
		for (TermReport report: currentTermReports) {
            FilterTerm filterTerm = currentCategory.findFilterTerm(report.getTerm());
            if (filterTerm.getEnabled() == true) 
                drawLineGraph(period, report, width, height);
        }

	}
	
}
