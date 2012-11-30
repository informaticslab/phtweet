/**
 * 
 */
package org.phiresearchlab.twitter.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 *
 * @author Joel M. Rives
 * May 12, 2011
 */
public class TermState implements IsSerializable {
	
	private String term;
	private String color;
	private Period period;
	private int hits = 0;
	private boolean enabled;
	
	public TermState() { }
	
	public TermState(String term) { 
		this.term = term;
	}

	public TermState(String term, String color, Period period, int hits) {
		this.term = term;
		this.color = color;
		this.period = period;
		this.hits = hits;
		this.enabled = true;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public int getHits() {
		return hits;
	}

	public void setHits(int hits) {
		this.hits = hits;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
