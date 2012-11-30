/**
 * 
 */
package org.phiresearchlab.twitter.domain;

import javax.persistence.Entity;

/**
 *
 * @author Joel M. Rives
 * May 4, 2011
 */

@Entity
public class HashTag extends DomainObject {

	private static final long serialVersionUID = 4852795090264151088L;

	private Integer end;
	private Integer start;
	private String text;	
	
	public Integer getEnd() {
		return end;
	}
	public void setEnd(Integer end) {
		this.end = end;
	}
	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
}
