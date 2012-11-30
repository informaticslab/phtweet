/**
 * 
 */
package org.phiresearchlab.twitter.domain;

import java.util.Date;

import javax.persistence.Entity;

/**
 *
 * @author Joel M. Rives
 * May 4, 2011
 */

@Entity
public class TermHitCount extends DomainObject {

	private static final long serialVersionUID = 4852795090264151088L;

	private String term;
	private int hits;
	private Date timestamp;
	
	public TermHitCount() { }
	
    public TermHitCount(String term) {
        this(term, 0);
    }
    
    public TermHitCount(String term, Date timestamp) {
        this(term, 0);
        this.timestamp = timestamp;
    }
    
	public TermHitCount(String term, int hits) {
		this.term = term;
		this.hits = hits;
	}
	
	public void incrementHits() {
	    this.hits++;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public int getHits() {
		return hits;
	}

	public void setHits(int hits) {
		this.hits = hits;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
		
}
