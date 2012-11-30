/**
 * 
 */
package org.phiresearchlab.twitter.shared;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author Joel M. Rives
 * May 11, 2011
 */
public class TermReport implements IsSerializable {
	
	private String term;
	private String color;
	private Period period;
	private List<Integer> hits;
	private List<TermAssociation> associates;
	private boolean enabled;
	
	public TermReport() { }
	
	public TermReport(String term) {
		this.term = term;
	}
	
	public TermReport(String term, String color, Period period) {
		this.term = term;
		this.color = color;
		this.period = period;
		this.enabled = true;
	}
	
	public String getTerm() {
		return term;
	}
	
	public void setTerm(String term) {
		this.term = term;
	}
	
	public Period getPeriod() {
		return period;
	}
	
	public void setPeriod(Period period) {
		this.period = period;
	}
	
	public List<Integer> getHits() {
		return hits;
	}

	public void setHits(List<Integer> hits) {
		this.hits = hits;
	}

	public List<TermAssociation> getAssociates() {
		return associates;
	}

	public void setAssociates(List<TermAssociation> associates) {
		this.associates = associates;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
