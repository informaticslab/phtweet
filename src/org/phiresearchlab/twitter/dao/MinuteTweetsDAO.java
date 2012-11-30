/**
 * 
 */
package org.phiresearchlab.twitter.dao;


import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;

import org.phiresearchlab.twitter.domain.MinuteTweets;
import org.phiresearchlab.twitter.shared.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import twitter4j.internal.logging.Logger;

/**
 *
 * @author Joel M. Rives
 * May 4, 2011
 */

@Repository("dailyTweetsDAO")
public class MinuteTweetsDAO extends JpaDAO<Long, MinuteTweets> {
    private Logger LOG = Logger.getLogger(MinuteTweetsDAO.class);
    
	@Autowired
	EntityManagerFactory entityManagerFactory;
	
	@PostConstruct
	public void init() {
		super.setEntityManagerFactory(entityManagerFactory);
	}
	
	@SuppressWarnings("unchecked")
	public List<MinuteTweets> findBetween(Date start, Date end) {
	    @SuppressWarnings("rawtypes")
        List list = find("select m from MinuteTweets m where m.timestamp between ?1 and ?2", start, end);
	    
	    return (List<MinuteTweets>) list;
	}
	
	public MinuteTweets findByMinute(Date timestamp) {
	    timestamp = toBoundary(timestamp, Period.Minute);
	    Object result = findOne("select m from MinuteTweets m where m.timestamp = ?1", timestamp);
	    
	    return (MinuteTweets) result;
	}
	
	@SuppressWarnings("unchecked")
    public List<MinuteTweets> findForDay(Date timestamp) {
        Date start = toBoundary(timestamp, Period.Day);
        start = new Date(start.getTime() - ONE_HOUR);
	    Date end = new Date(start.getTime() + ONE_DAY + ONE_HOUR);
	    
	    @SuppressWarnings("rawtypes")
        List list = find("select m from MinuteTweets m where m.timestamp between ?1 and ?2",
	                     start, end);
	    
	    return (List<MinuteTweets>) list;
	}
	
    @SuppressWarnings("unchecked")
    public List<MinuteTweets> findForHour(Date timestamp) {
        Date start = toBoundary(timestamp, Period.Hour);
        Date end = new Date(start.getTime() + ONE_HOUR);
        
        @SuppressWarnings("rawtypes")
        List list = find("select m from MinuteTweets m where m.timestamp between ?1 and ?2",
                         start, end);
        
        return (List<MinuteTweets>) list;
    }
    
	@SuppressWarnings("unchecked")
    public List<MinuteTweets> findForMonth(Date timestamp) {
        Date start = toBoundary(timestamp, Period.Month);
	    Date end = new Date(start.getTime() + ONE_MONTH);
	    
	    @SuppressWarnings("rawtypes")
        List list = find("select m from MinuteTweets m where m.timestamp between ?1 and ?2",
	                     start, end);
	    
	    return (List<MinuteTweets>) list;
	}
	
	@SuppressWarnings("unchecked")
    public List<MinuteTweets> findPrevious(Date timestamp, Period period, int count) {
		long offset = 0;
		switch (period) {
		case Month:
			offset = count * ONE_MONTH;
			break;
		case Day:
			offset = count * ONE_DAY;
			break;
		case Hour:
			offset = count * ONE_HOUR;
			break;
		case Minute:
			offset = count * ONE_MINUTE;
			break;
		}

		Date end = timestamp;
        Date start = new Date(toBoundary(timestamp, period).getTime() - offset);
	    
        LOG.debug("select m from MinuteTweets m where m.timestamp between " + start + " and " + end);
	    
	    @SuppressWarnings("rawtypes")
        List list = find("select m from MinuteTweets m where m.timestamp between ?1 and ?2",
	                     start, end);
	    
	    return (List<MinuteTweets>) list;
	}
	
    @Override
    public void persist(MinuteTweets entity)
    {
        Date timestamp = toBoundary(entity.getTimestamp(), Period.Minute);
        entity.setTimestamp(timestamp);
        super.persist(entity);
    }

    
}
