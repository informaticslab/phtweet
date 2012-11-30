/**
 * 
 */
package org.phiresearchlab.twitter.dao;


import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;

import org.phiresearchlab.twitter.domain.TermHitCount;
import org.phiresearchlab.twitter.shared.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Joel M. Rives
 * May 4, 2011
 */

@Repository("termHitCountDAO")
public class TermHitCountDAO extends JpaDAO<Long, TermHitCount> {

	@Autowired
	EntityManagerFactory entityManagerFactory;
	
	@PostConstruct
	public void init() {
		super.setEntityManagerFactory(entityManagerFactory);
	}
	
    @SuppressWarnings("unchecked")
	public List<TermHitCount> findBetween(String term, Date start, Date end) {
	    @SuppressWarnings("rawtypes")
        List list = find("select t from TermHitCount t where t.term = ?1 and t.timestamp between ?2 and ?3",
	                     term, start, end);
        
        return (List<TermHitCount>) list;
    }
    
	public List<TermHitCount> findForDay(String term, Date timestamp) {
        Date start = toBoundary(timestamp, Period.Day);
        start = new Date(start.getTime() - ONE_HOUR);
	    Date end = new Date(start.getTime() + ONE_DAY + ONE_HOUR);
	    return findBetween(term, start, end);
    }
    
	public List<TermHitCount> findForHour(String term, Date timestamp) {
        Date start = toBoundary(timestamp, Period.Hour);
	    Date end = new Date(start.getTime() + ONE_HOUR);
	    return findBetween(term, start, end);
    }
    
}
