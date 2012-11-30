/**
 * 
 */
package org.phiresearchlab.twitter.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 *
 * @author Joel M. Rives
 * Apr 25, 2011
 */
public class JitPanel extends AbsolutePanel {

	public JitPanel() { }
	
	public JitPanel(JavaScriptObject element) {
		super((Element) element);
	}
}
