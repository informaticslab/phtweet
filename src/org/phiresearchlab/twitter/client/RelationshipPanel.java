/**
 * 
 */
package org.phiresearchlab.twitter.client;

import java.util.Collections;
import java.util.List;

import org.phiresearchlab.twitter.shared.TermAssociation;
import org.phiresearchlab.twitter.shared.TermReport;
import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Group;
import org.vaadin.gwtgraphics.client.Shape;
import org.vaadin.gwtgraphics.client.shape.Rectangle;
import org.vaadin.gwtgraphics.client.shape.Text;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *
 * @author Joel M. Rives
 * Aug 8, 2011
 */
public class RelationshipPanel extends AbsolutePanel {
	private static final int GRID_CELL_WIDTH = 240;
	private static final int GRID_CELL_HEIGHT = 60;
	private static final int TERM_BOX_WIDTH = GRID_CELL_WIDTH;
	private static final int TERM_BOX_HEIGHT = GRID_CELL_HEIGHT;
	private static final int MEDIUM_BOX_WIDTH = 24;
	private static final int MEDIUM_BOX_HEIGHT = GRID_CELL_HEIGHT;
	private static final int STRONG_BOX_WIDTH = 40;
	private static final int STRONG_BOX_HEIGHT = GRID_CELL_HEIGHT;
	private static final int WEAK_BOX_WIDTH = 16;
	private static final int WEAK_BOX_HEIGHT = GRID_CELL_HEIGHT;
	private static final int ASSOCIATION_BUFFER = 8;
	
	private TwitterServiceAsync twitterService = GWT.create(TwitterService.class);	

	private VerticalPanel mainPanel;
	private Grid leftGrid;
	private Grid rightGrid;
	
	private int weakThreshold = 10;
	private int mediumThreshold = 20;
	
	private boolean faded = false;
	
	private int width;
	private int height;
		
	private List<TermReport> currentTermReports;
	
	/**
	 * @param width
	 * @param height
	 */
	public RelationshipPanel(int width, int height) {				
		Panel gridPanel = createGridPanel();
		Panel legendPanel = createLegend();
		
		mainPanel = new VerticalPanel();
		mainPanel.setWidth(width + "px");
		mainPanel.setHeight(height + "px");
		mainPanel.add(gridPanel);
		mainPanel.setCellHorizontalAlignment(gridPanel, HasHorizontalAlignment.ALIGN_CENTER);
		mainPanel.setCellVerticalAlignment(gridPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		mainPanel.add(legendPanel);
		mainPanel.setCellHorizontalAlignment(legendPanel, HasHorizontalAlignment.ALIGN_CENTER);
		mainPanel.setCellVerticalAlignment(legendPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		this.add(mainPanel);
	}

	@Override
	public void clear() {
	}
	
	public void show(List<TermReport> termReports, int width, int height) {
		this.width = width;
		this.height = height;
		
		currentTermReports = termReports;
		int count = 0;
		
		for (TermReport report: termReports) {
			Grid grid = count < 6 ? leftGrid : rightGrid;
			int row = count % 6;
			DrawingArea canvas = (DrawingArea) grid.getWidget(row, 0);
			canvas.clear();
			if (report.isEnabled()) {
				Group termBox = drawMainTermBox(report.getTerm(), report.getColor(), 0, 0);
				canvas.add(termBox);
				canvas.setTitle(report.getTerm());
			}
			int xPos = 0;
			canvas = (DrawingArea) grid.getWidget(row, 1);
			canvas.clear();
			if (report.isEnabled()) {
				Group termBox;
				canvas.setTitle(report.getTerm());
				Collections.sort(report.getAssociates());
				for (TermAssociation ta: report.getAssociates()) {
					if (contains(ta.getTerms(), report) && ta.getCount() > 0) {
						TermReport firstTerm = ta.getTerms().get(0);
						TermReport secondTerm = ta.getTerms().get(1);
						TermReport assocTerm = report.getTerm().equals(firstTerm.getTerm()) ? secondTerm : firstTerm;
						
						if (assocTerm.isEnabled()) {					
    						if (ta.getCount() < weakThreshold) {
    							termBox = drawWeakAssociationBox(assocTerm.getTerm(), assocTerm.getColor(), xPos, 0);
    							xPos += (WEAK_BOX_WIDTH + ASSOCIATION_BUFFER);
    						} else if (ta.getCount() < mediumThreshold) {
    							termBox = drawMediumAssociationBox(assocTerm.getTerm(), assocTerm.getColor(), xPos, 0);
    							xPos += (MEDIUM_BOX_WIDTH + ASSOCIATION_BUFFER);
    						} else {
    							termBox = drawStrongAssociationBox(assocTerm.getTerm(), assocTerm.getColor(), xPos, 0);
    							xPos += (STRONG_BOX_WIDTH + ASSOCIATION_BUFFER);
    						}
    						canvas.add(termBox);
						}
					}
				}
			}
			count++;
		}
	}
		
	public void showMessage(String message) {
	}

	private boolean contains(List<TermReport> list, TermReport report) {
		for (TermReport item: list) 
			if (item.getTerm().equals(report.getTerm()))
				return true;
		return false;
	}
	
	private Grid createGrid() {
		Grid grid = new Grid(6, 2);
		
		for (int row = 0; row < 6; row++) {
			final DrawingArea canvas = new DrawingArea(GRID_CELL_WIDTH, GRID_CELL_HEIGHT);
			canvas.getElement().getStyle().setCursor(Cursor.POINTER);
			canvas.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					if (faded) {
						unfade();
						faded = false;
					} else {
						String term = canvas.getTitle();
						fade(term);
						faded = true;
					}
				}
			});
			
			grid.setWidget(row, 0, canvas);
			grid.setWidget(row, 1, new DrawingArea(GRID_CELL_WIDTH, GRID_CELL_HEIGHT));
			if (row == 5) {
				grid.getCellFormatter().setStyleName(row, 0, "lastLeftGridCell");
				grid.getCellFormatter().setStyleName(row, 1, "lastRightGridCell");
			} else {
				grid.getCellFormatter().setStyleName(row, 0, "leftGridCell");
				grid.getCellFormatter().setStyleName(row, 1, "rightGridCell");
			}
		}
		
		grid.setStyleName("termGrid");
		
		return grid;
	}
	
	private Panel createGridPanel() {
		leftGrid = createGrid();
		rightGrid = createGrid();
		
		HorizontalPanel panel = new HorizontalPanel();
		panel.setStyleName("gridPanel");
		panel.setSpacing(40);
		panel.add(leftGrid);
		panel.add(rightGrid);
		
		return panel;
	}
	
	private Panel createLabelPanel(String line1, String line2) {
		Label label1 = new Label(line1);
		Label label2 = new Label(line2);
		VerticalPanel panel = new VerticalPanel();
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.add(label1);
		panel.add(label2);

		return panel;
	}

	private Panel createLegend() {
		Image strongImage = new Image("images/relate/strong_legend.png");
		Panel strongPanel = createLabelPanel("= strong", "(+" + mediumThreshold + ")");
		
		Image mediumImage = new Image("images/relate/medium_legend.png");
		Panel mediumPanel = createLabelPanel("= medium", "(" + weakThreshold + " to "+ mediumThreshold + ")");
		
		Image weakImage = new Image("images/relate/small_legend.png");
		Panel weakPanel = createLabelPanel("= weak", "(<" + weakThreshold + ")");
		
		HorizontalPanel panel = new HorizontalPanel();
		panel.setStyleName("legendPanel");
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.setSpacing(10);
		panel.add(strongImage);
		panel.add(strongPanel);
		panel.add(mediumImage);
		panel.add(mediumPanel);
		panel.add(weakImage);
		panel.add(weakPanel);
		
		return panel;
	}
	
	private Group drawMainTermBox(String term, String color, int x, int y) {
		return drawTermBox(term, color, x, y, TERM_BOX_WIDTH, TERM_BOX_HEIGHT);
	}
	
	private Group drawMediumAssociationBox(String term, String color, int x, int y) {
		Group box = drawAssociationBox(term, color, x, y, MEDIUM_BOX_WIDTH, MEDIUM_BOX_HEIGHT);
		return box;
	}
	
	private Group drawStrongAssociationBox(String term, String color, int x, int y) {
		Group box = drawAssociationBox(term, color, x, y, STRONG_BOX_WIDTH, STRONG_BOX_HEIGHT);
		return box;
	}
	
	private Group drawWeakAssociationBox(String term, String color, int x, int y) {
		Group box = drawAssociationBox(term, color, x, y, WEAK_BOX_WIDTH, WEAK_BOX_HEIGHT);
		return box;
	}
	
	private Group drawAssociationBox(String term, String color, int x, int y, int width, int height) {
		Rectangle box = new Rectangle(x, y, width, height);
		box.setFillColor(color);
		box.setStrokeColor(color);

		Text text = new Text(0, 0, term);
		text.setFontFamily("Verdana");
		text.setFontSize(16);
		text.setFillColor("white");
		text.setStrokeOpacity(0.0);
		
		while (text.getTextWidth() > (height - 6) || text.getTextHeight() > (width - 4))
			text.setFontSize(text.getFontSize() - 1);
		
		text.setRotation(90);
		text.setX(x + ((width - text.getTextWidth()) / 2));
		text.setY(y + ((height - text.getTextHeight()) / 2) + (width / 2));

		Group group = new Group();
		group.add(box);
		group.add(text);
		group.bringToFront(text);
		
		return group;
	}
	
	private Group drawTermBox(String term, String color, int x, int y, int width, int height) {
		Rectangle box = new Rectangle(x, y, width, height);
		box.setFillColor(color);
		box.setStrokeColor(color);
		box.setRoundedCorners(4);
		
		Text text = new Text(0, 0, term);
		text.setFontFamily("Verdana");
		text.setFontSize(30);
		text.setFillColor("white");
		text.setStrokeColor("white");		
		text.setX(x + ((width - text.getTextWidth()) / 2));
		text.setY(y + ((height - text.getTextHeight()) / 2) + 30);
		
		Group group = new Group();
		group.add(box);
		group.add(text);
		group.bringToFront(text);
		
		return group;
	}
	
	private void fade(String term) {
		TermReport report = findTermReport(term);
		
		if (null == report)
			return;
		
		for (int row = 0; row < 6; row++) {
			DrawingArea canvas = (DrawingArea) leftGrid.getWidget(row, 0);
			fade(term, canvas, report);

			canvas = (DrawingArea) leftGrid.getWidget(row, 1);
			fade(term, canvas, report);
			
			canvas = (DrawingArea) rightGrid.getWidget(row, 0);
			fade(term, canvas, report);

			canvas = (DrawingArea) rightGrid.getWidget(row, 1);
			fade(term, canvas, report);
		}
	}
	
	private void fade(String term, DrawingArea canvas, TermReport report) {
		String name = canvas.getTitle();
		
		if (name.equals(term))
			return;
		
		TermReport other = findTermReport(name);
		
		for (TermAssociation ta: report.getAssociates())
			if (ta.contains(report, other))
				return;
		
		double opacity = 0.3;
		for (int i = 0; i < canvas.getVectorObjectCount(); i++) {
			Group group = (Group) canvas.getVectorObject(i);
			Shape shape = (Shape) group.getVectorObject(0);
			shape.setFillOpacity(opacity);
			shape.setStrokeOpacity(opacity);
			shape = (Shape) group.getVectorObject(1);
			shape.setFillOpacity(opacity);
			shape.setStrokeOpacity(opacity);
		}
	}
	
	private TermReport findTermReport(String name)  {
		for (TermReport report: currentTermReports)
			if (name.equals(report.getTerm()))
				return report;
		return null;
	}
	
	private void unfade() {
		for (int row = 0; row < 6; row++) {
			DrawingArea canvas = (DrawingArea) leftGrid.getWidget(row, 0);
			unfade(canvas);

			canvas = (DrawingArea) leftGrid.getWidget(row, 1);
			unfade(canvas);
			
			canvas = (DrawingArea) rightGrid.getWidget(row, 0);
			unfade(canvas);

			canvas = (DrawingArea) rightGrid.getWidget(row, 1);
			unfade(canvas);
		}
	}
	
	private void unfade(DrawingArea canvas) {
		for (int i = 0; i < canvas.getVectorObjectCount(); i++) {
			Group group = (Group) canvas.getVectorObject(i);
			Shape shape = (Shape) group.getVectorObject(0);
			shape.setFillOpacity(1.0);
			shape.setStrokeOpacity(1.0);
			shape = (Shape) group.getVectorObject(1);
			shape.setFillOpacity(1.0);
			shape.setStrokeOpacity(0.0);
		}		
	}
	
}
