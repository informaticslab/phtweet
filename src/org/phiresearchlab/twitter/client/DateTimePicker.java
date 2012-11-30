/**
 * 
 */
package org.phiresearchlab.twitter.client;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 *
 * @author Joel M. Rives
 * Jul 7, 2011
 */
public class DateTimePicker extends PopupPanel {
	
	private Label title;
	
	private boolean showTime = true;
	private ValueChangeHandler<Date> changeHandler;
	private Date pickedDate;
	
	public DateTimePicker(boolean showTime) {
		super(true);
		setGlassEnabled(true);
		setShowTime(showTime);
		this.setStyleName("dateTimePicker");
		
		Panel mainPanel = createMainPanel();
		setWidget(mainPanel);
	}
	
	public DateTimePicker() {
		this(true);
	}
	
	public void setShowTime(boolean flag) {
		this.showTime = flag;
	}
	
	private Panel createBodyPanel() {
		DatePicker datePicker = new DatePicker();
		datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
			public void onValueChange(ValueChangeEvent<Date> event) {
				Date date = event.getValue();	
				Date now = new Date();
				
				if (date.after(now)) {
					Window.alert("Future date/time selections are not valid");
					return;
				}
				
				pickedDate = date;
				Window.alert("The picked date is " + pickedDate);
			}			
		});
		
		Clock clock = new Clock(120, 120);
		
		ListBox times = new ListBox();
		for (int i = 1; i < 12; i++)
			times.addItem(i + ":00 AM");
		times.addItem("12:00 PM");
		for (int i = 1; i < 12; i++)
			times.addItem(i + ":00 PM");
		times.addItem("12:00 AM");
		times.setVisibleItemCount(1);
		times.setWidth("120px");
		
		VerticalPanel timePanel = new VerticalPanel();
		timePanel.setStyleName("timePanel");
		timePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		timePanel.setSpacing(10);
		timePanel.add(clock);
		timePanel.add(times);
		
		HorizontalPanel bodyPanel = new HorizontalPanel();
		bodyPanel.setWidth("100%");
		bodyPanel.getElement().setId("datePickerBody");
		bodyPanel.add(datePicker);
		bodyPanel.add(timePanel);
		
		VerticalPanel panel = new VerticalPanel();
		panel.add(createHeaderPanel());
		panel.add(bodyPanel);
		
		return panel;
	}
	
	private Panel createFooterPanel() {
		Button cancelButton = new Button("Cancel");
		cancelButton.getElement().setId("cancelButton");
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		Button okButton = new Button("OK");
		okButton.getElement().setId("okButton");
		okButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		AbsolutePanel panel = new AbsolutePanel();
		panel.setWidth("100%");		
		panel.setStyleName("buttonPanel");
		panel.add(cancelButton, 200, 8);
		panel.add(okButton, 260, 8);
		
		return panel;
	}
	
	private Panel createHeaderPanel() {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setStyleName("dateTimeHeader");
		panel.setWidth("100%");
		
		StringBuffer buffer = new StringBuffer("Select Date");
		if (showTime)
			buffer.append(" & Time");
		
		title = new Label(buffer.toString());
		
		Image closeButton = new Image("images/popup_close_button.png");
		closeButton.addStyleName("imageButton");
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		panel.add(title);
		panel.setCellHorizontalAlignment(title, HasHorizontalAlignment.ALIGN_LEFT);
		panel.setCellVerticalAlignment(title, HasVerticalAlignment.ALIGN_BOTTOM);
		
		panel.add(closeButton);
		panel.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_RIGHT);
		panel.setCellVerticalAlignment(closeButton, HasVerticalAlignment.ALIGN_TOP);
				
		return panel;
	}
	
	private Panel createMainPanel() {
		VerticalPanel top = new VerticalPanel();
		
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("dateTimePickerMain");
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		panel.add(createBodyPanel());
		Panel footer = createFooterPanel();
		
		top.add(panel);
		top.add(footer);
		
		return top;
	}
	
}
