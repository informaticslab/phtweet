/**
 * 
 */
package org.phiresearchlab.twitter.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 *
 * @author Joel M. Rives
 * Aug 11, 2011
 */
public enum Period implements IsSerializable {
	Minute, Hour, Day, Week, Month, Year;
}
