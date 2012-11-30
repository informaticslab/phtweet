/**
 * 
 */
package org.phiresearchlab.twitter.dao;


import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;

import org.phiresearchlab.twitter.domain.FilterTerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Joel M. Rives
 * May 4, 2011
 */

@Repository("filterTermDAO")
public class FilterTermDAO extends JpaDAO<Long, FilterTerm> {

    @Autowired
	EntityManagerFactory entityManagerFactory;
	
	@PostConstruct
	public void init() {
		super.setEntityManagerFactory(entityManagerFactory);
	}
	
    public FilterTerm findByName(String name) {
        @SuppressWarnings("unchecked")
        List<FilterTerm> results = 
            getJpaTemplate().find("select t from FilterTerm t where t.name = ?1", name);
        
        if (results.isEmpty())
            return null;
        
        return results.get(0);
    }
    
}
