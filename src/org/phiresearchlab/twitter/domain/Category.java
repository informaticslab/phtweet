/**
 * 
 */
package org.phiresearchlab.twitter.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

/**
 *
 * @author Joel M. Rives
 * May 4, 2011
 */

@Entity
public class Category extends DomainObject {

	private static final long serialVersionUID = 4852795090264151088L;

    @Column(length = 64, unique = true, nullable = false, updatable = false)
	private String name;

    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="category_fk")
    private List<FilterTerm> filterTerms;
	
    @Column(nullable = false, updatable = false)
    private Date created;
	
	public Category() { }
	
    public Category(String name, List<FilterTerm> terms) {
        this(name, terms, new Date());
    }
    
	public Category(String name, List<FilterTerm> terms, Date created) {
		this.name = name;
		this.filterTerms = new ArrayList<FilterTerm>(terms);
		this.created = created;
	}
	
	public FilterTerm findFilterTerm(String name) {
	    for (FilterTerm term: filterTerms)
	        if (term.getName().equals(name))
	            return term;
	    
	    return null;
	}

    public String getName()
    {
        return this.name;
    }

    public List<FilterTerm> getFilterTerms()
    {
        return this.filterTerms;
    }

    public Date getCreated()
    {
        return this.created;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setFilterTerms(List<FilterTerm> filterTerms)
    {
        this.filterTerms = filterTerms;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

}
