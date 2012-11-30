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
public class Associations extends DomainObject {

	private static final long serialVersionUID = 4852795090264151088L;

	private String filterTerm;
	private String associatedTerms;
	private Date createdDate;
	
	public Associations() { }
	
	public Associations(String filterTerm, String associatedTerms, Date createdDate) {
		this.filterTerm = filterTerm;
		this.associatedTerms = associatedTerms;
		this.createdDate = createdDate;
	}

	public String getFilterTerm() {
		return filterTerm;
	}

	public void setFilterTerm(String filterTerm) {
		this.filterTerm = filterTerm;
	}

	public String getAssociatedTerms() {
		return associatedTerms;
	}

	public void setAssociatedTerms(String associatedTerms) {
		this.associatedTerms = associatedTerms;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
}
