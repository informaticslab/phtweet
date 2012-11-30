/**
 * 
 */
package org.phiresearchlab.twitter.client;

import java.util.Date;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;

/**
 *
 * @author Joel M. Rives
 * May 12, 2011
 */
public class HeaderPanel extends HorizontalPanel {
	
	private static final long ONE_HOUR = 1000 * 60 * 60;
    private static final long HALF_DAY = ONE_HOUR * 12;
	private static final long ONE_DAY = ONE_HOUR * 24;
	
	ListBox listBox;
	private ImageButton backButton;
	private ImageButton nextButton;
	private Label dateLabel;
	private Label timeLabel;
	private Image dateTimeButton;
	private ImageButton nowButton;
	private Image hourDayToggle;
	private DateTimePicker dateTimePicker = new DateTimePicker();
	
	private PHTweet controller;

	public HeaderPanel(PHTweet controller) {
		this.controller = controller;
		
		setStyleName("headerPanel");
		setHorizontalAlignment(ALIGN_CENTER);
		setVerticalAlignment(ALIGN_MIDDLE);
		
		add(createHomeButton());
		add(new Image("images/header/divider.png"));
		add(createSelectAViewPanel());
		add(new Image("images/header/divider.png"));
		add(createSelectDateTimePanel());
		add(new Image("images/header/divider.png"));
		add(createViewByPanel());
		add(new Image("images/header/divider.png"));
		add(createControlPanel());
		
		setTime(new Date());
	}
	
	private Panel createControlPanel() {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setSpacing(6);
		
		Image settingsButton = createImageButton("images/header/settings_icon.png");
		settingsButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				controller.launchSettings();
			}
		});
		
		Image helpButton = createImageButton("images/header/help_icon.png");
		helpButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				controller.launchHelp();
			}
		});
		
		panel.add(settingsButton);
		panel.add(helpButton);

		return panel;
	}
	
	private Image createHomeButton() {
		Image homeButton = createImageButton("images/header/PHTweet.png");
		homeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				controller.home();
				listBox.setItemSelected(0, true);
                hourDayToggle.setUrl("images/header/hour_view.png");
			}
		});
		return homeButton;
	}
	
	private Image createImageButton(String path) {
		Image button = new Image(path);
		button.setStyleName("imageButton");
		return button;
	}
	
	private Panel createSelectAViewPanel() {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setSpacing(6);
		
		listBox = new ListBox();
		listBox.setVisibleItemCount(1); // Makes it a drop-down list
		listBox.addItem("Bubble");
		listBox.addItem("Graph");
		listBox.addItem("Relationship");
		
		listBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				int index = listBox.getSelectedIndex();
				
				switch (index) {
				case 0:
					controller.selectBubbleChart();
					break;
				case 1:
					controller.selectGraph();
					break;
				case 2:
					controller.selectRelationship();
					break;
				}
				
				setTime(controller.getCurrentTimestamp());
			}			
		});
		
		panel.add(new Label("Select a view:"));
		panel.add(listBox);
		
		return panel;
	}
	
	private Panel createSelectDateTimePanel() {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setSpacing(16);
		panel.setWidth("480px");
		panel.setVerticalAlignment(ALIGN_MIDDLE);
		
		backButton = new ImageButton("images/header/back_arrow.png", 
				                     "images/header/back_arrow_disabled.png", 
				                     "images/header/back_arrow_hover.png");
		backButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
			    if (controller.viewByHour()) 
			        goBack(controller.viewByAmPm() ? HALF_DAY : ONE_HOUR);
			    else
			        goBack(controller.viewByAmPm() ? ONE_DAY * 12 : ONE_DAY);
			}
		});
		
		nextButton = new ImageButton("images/header/next_arrow.png",
				                     "images/header/next_arrow_disabled.png",
				                     "images/header/next_arrow_hover.png");
		nextButton.setEnabled(false);
		nextButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
                if (controller.viewByHour()) 
                    goForward(controller.viewByAmPm() ? HALF_DAY : ONE_HOUR);
                else
                    goForward(controller.viewByAmPm() ? ONE_DAY * 12 : ONE_DAY);
			}
		});
		
		timeLabel = new Label("Time");
		timeLabel.setStyleName("timeLabel");
		dateLabel = new Label("Date");
		dateLabel.setStyleName("dateLabel");
		
		HorizontalPanel navPanel = new HorizontalPanel();
		navPanel.setSpacing(6);
		navPanel.setVerticalAlignment(ALIGN_MIDDLE);
		
		navPanel.add(backButton);
		navPanel.add(nextButton);
		navPanel.add(timeLabel);
		navPanel.add(dateLabel);
		
		dateTimeButton = createImageButton("images/header/date_time_icon.png");
		dateTimeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dateTimePicker.center();
				dateTimePicker.show();
			}
		});
		
		nowButton = new ImageButton("images/header/now_button.png",
				                    "images/header/now_button_disabled.png",
				                    "images/header/now_button_hover.png");
		nowButton.setEnabled(false);
		nowButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				goToNow();
			}
		});
		
		panel.add(navPanel);
		panel.add(dateTimeButton);
		panel.setCellHorizontalAlignment(dateTimeButton, HasHorizontalAlignment.ALIGN_RIGHT);
		panel.add(nowButton);
		panel.setCellHorizontalAlignment(nowButton, HasHorizontalAlignment.ALIGN_RIGHT);
		
		return panel;
	}
	
	private Panel createViewByPanel() {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setSpacing(6);
		panel.setVerticalAlignment(ALIGN_MIDDLE);
		
		Label viewBy = new Label("View by:");
		
		hourDayToggle = createImageButton("images/header/hour_view.png");
		hourDayToggle.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (controller.viewByHour()) {
                    hourDayToggle.setUrl("images/header/day_view.png");
                    controller.setViewByHour(false);
                } else {
                    hourDayToggle.setUrl("images/header/hour_view.png");
                    controller.setViewByHour(true);
                }
                setTime(controller.getCurrentTimestamp());
            }
		});
		
		panel.add(viewBy);
		panel.add(hourDayToggle);

		return panel;
	}
	
	private void goBack(long time) {
		Date earlier = new Date(controller.getCurrentTimestamp().getTime() - time);
		goToDate(earlier);
	}
	
	private void goForward(long time) {
		Date later = new Date(controller.getCurrentTimestamp().getTime() + time);
		goToDate(later);
	}
	
	@SuppressWarnings("deprecation")
	private void goToDate(Date date) {
		Date now = new Date();
		if (date.after(now))
			date = now;
		setTime(date);
		controller.setCurrentTimestamp(date);
		if (controller.viewByHour()) {
		    boolean flag = date.getHours() < now.getHours() || date.getDay() < now.getDay();
			nextButton.setEnabled(flag);
			nowButton.setEnabled(flag);
		} else {
			nextButton.setEnabled(date.getDay() < now.getDay());
			nowButton.setEnabled(date.getDay() < now.getDay());
		}
	}
	
	private void goToNow() {
		goToDate(new Date());
	}
	
	private DateTimeFormat hourFormat = DateTimeFormat.getFormat("h:00");
	private DateTimeFormat amPmFormat = DateTimeFormat.getFormat("a");
	private DateTimeFormat dayFormat = DateTimeFormat.getFormat("EEE, MMM d, yyyy");
	
	private void setTime(Date date) {
		String day = dayFormat.format(date);

		if (controller.viewByHour()) {
		    if (controller.viewByAmPm()) {
	            timeLabel.setText("");
	            String amPm = amPmFormat.format(date);
	            dateLabel.setText(day + " (" + amPm + ")");	            
		    } else {
    			Date nextHour = new Date(date.getTime() + ONE_HOUR);
    			String first = hourFormat.format(date);
    			String second = hourFormat.format(nextHour);
    			String amPm = amPmFormat.format(date);
    			timeLabel.setText(first + " to " + second + amPm);
    			dateLabel.setText("on " + day);
		    }
		} else {
			timeLabel.setText("");
			dateLabel.setText(day);
		}
	}
	
}
