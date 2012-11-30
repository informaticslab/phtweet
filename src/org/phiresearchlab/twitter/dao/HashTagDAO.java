/**
 * 
 */
package org.phiresearchlab.twitter.dao;


import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;

import org.phiresearchlab.twitter.domain.HashTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Joel M. Rives
 * May 4, 2011
 */

@Repository("hashTagDAO")
public class HashTagDAO extends JpaDAO<Long, HashTag> {

	@Autowired
	EntityManagerFactory entityManagerFactory;
	
	@PostConstruct
	public void init() {
		super.setEntityManagerFactory(entityManagerFactory);
	}
	
}
