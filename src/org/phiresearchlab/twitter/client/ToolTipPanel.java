/**
 * 
 */
package org.phiresearchlab.twitter.client;

import java.util.ArrayList;
import java.util.List;

import org.phiresearchlab.twitter.shared.TermHistory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *
 * @author Joel M. Rives
 * May 25, 2011
 */
public class ToolTipPanel extends PopupPanel {
    
    private static ToolTipPanel INSTANCE = null;

	private ColorMapper colorMapper = ColorMapper.getInstance();
	
	private VerticalPanel mainPanel;
	private Label title = new Label();
	private Grid table = new Grid(3, 2);
	private List<ToolTipHandler> handlers = new ArrayList<ToolTipHandler>();

	public static ToolTipPanel getInstance() {
	    if (null == INSTANCE)
	        INSTANCE = new ToolTipPanel();
	    return INSTANCE;
	}
	
	private ToolTipPanel() {
		Image closeButton = new Image("images/popup_close_button.png");
		closeButton.setStyleName("imageButton");
		closeButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				hide();	
			}			
		});
		
		HorizontalPanel header = new HorizontalPanel();
		header.setStyleName("toolTipHeader");

		header.add(title);
		header.setCellHorizontalAlignment(title, HasHorizontalAlignment.ALIGN_LEFT);
		header.setCellVerticalAlignment(title, HasVerticalAlignment.ALIGN_BOTTOM);
		
		table.setStyleName("toolTipTable");
		table.setText(0, 0, "Previous Hour:");
		table.getCellFormatter().setStyleName(0, 0, "toolTipTableHeader");
		table.setText(1, 0, "Previous Day:");
		table.getCellFormatter().setStyleName(1, 0, "toolTipTableHeader");
		table.setText(2, 0, "Total:");
		table.getCellFormatter().setStyleName(2, 0, "toolTipTableHeader");
		
		Anchor link = new Anchor("Detailed view");
		link.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event)
            {
            	String base = GWT.getHostPageBaseURL();
            	Window.open(base + "images/Detailed_view.png", "Detailed", "");
                hide();
            }	    
		});
		
		
		mainPanel = new VerticalPanel();
		mainPanel.setStyleName("toolTipMainPanel");
		mainPanel.add(closeButton);
		mainPanel.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_RIGHT);
		mainPanel.add(header);
		mainPanel.add(table);
		mainPanel.add(link);
		mainPanel.setCellHorizontalAlignment(link, HasHorizontalAlignment.ALIGN_RIGHT);
				
		setWidget(mainPanel);
	}
	
	public void addToolTipHandler(ToolTipHandler handler) {
		handlers.add(handler);
	}
	
	public void hide() {
		for (ToolTipHandler handler: handlers) 
			handler.onToolTipHide();
		
		super.hide();
	}
	
	public void setValues(TermHistory history) {
		title.setText(history.getTerm());
		String color = colorMapper.getFaded(history.getColor());
		mainPanel.getElement().getStyle().setBackgroundColor(color);

		if (history.getHourHits() == -1)
			table.setText(0, 1, "N/A");
		else
			table.setText(0, 1, Integer.toString(history.getHourHits()));
		table.setText(1, 1, Integer.toString(history.getDayHits()));
		if (history.getTotalHits() == 0)
			table.setText(2, 1, "Not Available");
		else
			table.setText(2, 1, Integer.toString(history.getTotalHits()));

	}
	
	public void show(TermHistory history) {
		title.setText(history.getTerm());
		String color = colorMapper.getFaded(history.getColor());
		mainPanel.getElement().getStyle().setBackgroundColor(color);
		table.setText(0, 1, "Calculating");
		table.setText(1, 1, "Calculating");
		table.setText(2, 1, "Calculating");		
		super.show();
	}
	
}
