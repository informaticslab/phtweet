/**
 * 
 */
package org.phiresearchlab.twitter.dao;


import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;

import org.phiresearchlab.twitter.domain.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Joel M. Rives
 * May 4, 2011
 */

@Repository("categoryDAO")
public class CategoryDAO extends JpaDAO<Long, Category> {

	@Autowired
	EntityManagerFactory entityManagerFactory;
	
	@PostConstruct
	public void init() {
		super.setEntityManagerFactory(entityManagerFactory);
	}
	
    public Category findByName(String name) {
        @SuppressWarnings("unchecked")
        List<Category> results = 
            getJpaTemplate().find("select c from Category c where c.name = ?1", name);
        
        if (results.isEmpty())
            return null;
        
        return results.get(0);
    }
    
}
