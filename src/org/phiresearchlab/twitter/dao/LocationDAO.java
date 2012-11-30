/**
 * 
 */
package org.phiresearchlab.twitter.dao;


import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;

import org.phiresearchlab.twitter.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Joel M. Rives
 * May 4, 2011
 */

@Repository("locationDAO")
public class LocationDAO extends JpaDAO<Long, Location> {

	@Autowired
	EntityManagerFactory entityManagerFactory;
	
	@PostConstruct
	public void init() {
		super.setEntityManagerFactory(entityManagerFactory);
	}
	
	public Location findByPlaceId(String id) {
		@SuppressWarnings("unchecked")
		List<Location> results = getJpaTemplate().find("select l from Location l where l.placeId = ?1", id);		
		
		if (results.size() == 0)
			return null;
		
		return results.get(0);
	}

}
