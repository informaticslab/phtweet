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
public class RelationshipHitCount extends DomainObject {

	private static final long serialVersionUID = 4852795090264151088L;

	private String term1;
	private String term2;
	private int hits = 0;
	
	public RelationshipHitCount() { }
	
	public RelationshipHitCount(String term1, String term2) {
		this.term1 = term1;
		this.term2 = term2;
		hits = 0;
	}
	
	public void incrementHits() {
		hits++;
	}
	
	public int getHits() {
		return hits;
	}

	public void setHits(int hits) {
		this.hits = hits;
	}

	public String getTerm1() {
		return term1;
	}

	public void setTerm1(String term1) {
		this.term1 = term1;
	}

	public String getTerm2() {
		return term2;
	}

	public void setTerm2(String term2) {
		this.term2 = term2;
	}
	
}
