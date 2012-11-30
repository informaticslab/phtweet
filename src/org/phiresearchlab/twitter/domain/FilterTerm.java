/**
 * 
 */
package org.phiresearchlab.twitter.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author Joel M. Rives
 * May 4, 2011
 */

@Entity
public class FilterTerm extends DomainObject {

	private static final long serialVersionUID = 4852795090264151088L;

    @ManyToOne
    @JoinColumn(name="category_fk", insertable=false, updatable=false)
	private Category category;

    @Column(nullable = false, updatable = false)
    private String name;
	
    @Column(length = 8, nullable = false, updatable = false)
    private String color;
	
    private Date started;
    
    @Column(nullable = false)
    private Boolean enabled = true;
	
	public FilterTerm() { }
	
    public FilterTerm(String name, String color) {
        this(name, color, new Date());
    }
    
	public FilterTerm(String name, String color, Date started) {
		this.name = name;
		this.color = color;
		this.started = started;
	}

    @Override
    public boolean equals(Object obj)
    {
        return name.equals(obj);
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    public Category getCategory()
    {
        return this.category;
    }

    public void setCategory(Category category)
    {
        this.category = category;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Date getStarted() {
		return started;
	}

	public void setStarted(Date started) {
		this.started = started;
	}

    public Boolean getEnabled()
    {
        return this.enabled;
    }

    public void setEnabled(Boolean enabled)
    {
        this.enabled = enabled;
    }
	
}
