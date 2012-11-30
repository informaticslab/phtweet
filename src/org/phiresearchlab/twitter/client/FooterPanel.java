/**
 * 
 */
package org.phiresearchlab.twitter.client;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 *
 * @author Joel M. Rives
 * May 12, 2011
 */
public class FooterPanel extends HorizontalPanel {

	public FooterPanel() {
		setStyleName("footerPanel");
		
		Image logo = new Image("images/footer/logo_footer.png");
		Label label = new Label("A Prototype of the Informatics R&D Laboratory, CDC Public Health Informatics & Technology Program Office");
		
		HorizontalPanel main = new HorizontalPanel();
	
		main.add(logo);
		main.add(label);
		
		add(main);
		setCellHorizontalAlignment(main, ALIGN_CENTER);
	}
	
}
