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
public class TermHistory implements IsSerializable {
	
	private String term;
	private String color;
	private int hourHits;
	private int dayHits;
	private int totalHits;
	
	public TermHistory() { }
	
	public TermHistory(String term, String color) {
		this.term = term;
		this.color = color;
		this.hourHits = 0;
		this.dayHits = 0;
		this.totalHits = 0;
	}
	
	public void incrementTotalHits(int amount) {
		totalHits += amount;
	}

	public void incrementHourHits(int amount) {
		hourHits += amount;
	}

	public void incrementDayHits(int amount) {
		dayHits += amount;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public int getHourHits() {
		return hourHits;
	}

	public void setHourHits(int hourHits) {
		this.hourHits = hourHits;
	}

	public int getDayHits() {
		return dayHits;
	}

	public void setDayHits(int dayHits) {
		this.dayHits = dayHits;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getTotalHits() {
		return totalHits;
	}

	public void setTotalHits(int totalHits) {
		this.totalHits = totalHits;
	}

}
