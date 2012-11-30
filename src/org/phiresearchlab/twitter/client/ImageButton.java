/**
 * 
 */
package org.phiresearchlab.twitter.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;

/**
 *
 * @author Joel M. Rives
 * Jul 7, 2011
 */
public class ImageButton extends Image {
	
	private boolean enabled = true;
	private String upImage;
	private String downImage;
	private String hoverImage;
	private List<ClickHandler> clickHandlers = new ArrayList<ClickHandler>();

	public ImageButton(String up, String down) {
		super(up);
		
		this.upImage = up;
		this.downImage = down;
		
		addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				if (enabled)
					setUrl(downImage);
			}
		});
		
		addMouseUpHandler(new MouseUpHandler() {
			public void onMouseUp(MouseUpEvent event) {
				if (enabled)
					setUrl(upImage);
			}
		});
		
		// TODO:kill mouse move event
	}
	
	public ImageButton(String up, String down, String hover) {
		this(up, down);
		setHoverImage(hover);
	}
	
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		clickHandlers.add(handler);
		
		HandlerRegistration hr = super.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (enabled)
					for (ClickHandler handler: clickHandlers) 
						handler.onClick(event);
			}
		});
		
		return hr;
	}

	public void setEnabled(boolean flag) {
		this.enabled = flag;
		setUrl(enabled ? upImage : downImage);
	}
	
	public void setHoverImage(String hover) {
		this.hoverImage = hover;
		
		addMouseOverHandler(new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) {
				if (enabled)
					setUrl(hoverImage);
			}
		});
		
		addMouseOutHandler(new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				if (enabled)
					setUrl(upImage);
			}
		});
	}
	
}
