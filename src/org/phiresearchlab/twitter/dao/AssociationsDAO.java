/**
 * 
 */
package org.phiresearchlab.twitter.dao;


import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;

import org.phiresearchlab.twitter.domain.Associations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Joel M. Rives
 * May 4, 2011
 */

@Repository("associationsDAO")
public class AssociationsDAO extends JpaDAO<Long, Associations> {

	@Autowired
	EntityManagerFactory entityManagerFactory;
	
	@PostConstruct
	public void init() {
		super.setEntityManagerFactory(entityManagerFactory);
	}
	
	public List<Associations> getBetween(String term, Date before, Date after) {
		@SuppressWarnings("unchecked")
		List<Associations> results = getJpaTemplate().find("select a from Associations a where " +
                										   "filterTerm = ?1 and " +
                                                           "createdDate between ?2 and ?3",
                                                           "%" + term + "%", after, before);		
		return results;
	}
}
