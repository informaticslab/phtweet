/**
 * 
 */
package org.phiresearchlab.twitter.dao;


import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.phiresearchlab.twitter.domain.Tweet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Joel M. Rives
 * May 4, 2011
 */

@Repository("tweetDAO")
public class TweetDAO extends JpaDAO<Long, Tweet> {

	@Autowired
	EntityManagerFactory entityManagerFactory;
	
	@PostConstruct
	public void init() {
		super.setEntityManagerFactory(entityManagerFactory);
	}
	
	public List<Tweet> findByPage(Long startId, Integer count) {
		@SuppressWarnings("unchecked")
		List<Tweet> results = getJpaTemplate().find("select t from Tweet t order by t.id asc limit 0, 1000");
		return results;
	}
	
	public List<Tweet> findNextUnanalyzed(Integer limit) {
		@SuppressWarnings("unchecked")
		List<Tweet> results = getJpaTemplate().find("select t from Tweet t where t.analyzed = false " +
				                                    "order by t.id asc limit 20");
		return results;
	}
	
	public int getAssociationHits(String term, String otherTerm, Date after, Date before) {
		@SuppressWarnings("unchecked")
		List<Long> results = getJpaTemplate().find("select count(text) from Tweet where " +
				                                   "text like ?1 and text like ?2 and " +
				                                   "createdAt between ?3 and ?4",
				                                   "%" + term + "%", "%" + otherTerm + "%", after, before);		

		long count = results.get(0);
		return (int) count;		
	}
	
	public int getAssociationHits(final String term, final List<String> associates, final Date after) {
		Long result = getJpaTemplate().execute(new JpaCallback<Long>() {
			public Long doInJpa(EntityManager em) throws PersistenceException {
				StringBuffer buffer = new StringBuffer("select count(text) from Tweet where text like ?1 ");
				buffer.append("and createdAt >= ?2 ");
				for (int i = 0; i < associates.size(); i++) {
					buffer.append("and text like ?" + (i + 3) + " ");
				}
				Query query = em.createQuery(buffer.toString());
				query.setParameter(1, "%" + term + "%");
				query.setParameter(2, after, TemporalType.TIMESTAMP);
				for (int i = 0; i < associates.size(); i++) {
					query.setParameter(i + 3, "%" + associates.get(i) + "%");
				}
				return (Long) query.getSingleResult();
			}
		});
		
		return (int) (long) result;
	}
	
	public int getBetweenHitCount(String term, Date after, Date before) {
		@SuppressWarnings("unchecked")
		List<Long> results = getJpaTemplate().find("select count(text) from Tweet where " +
				                                   "text like ?1 and " +
				                                   "createdAt between ?2 and ?3",
				                                   "%" + term + "%", after, before);		

		long count = results.get(0);
		return (int) count;		
	}
	
	public int getCoupletHitCount(String firstTerm, String secondTerm) {
		@SuppressWarnings("unchecked")
		List<Long> results = getJpaTemplate().find("select count(text) from Tweet where text like ?1 and text like ?2", 
				                                   "%" + firstTerm + "%",
				                                   "%" + secondTerm + "%");		

		long count = results.get(0);
		return (int) count;
	}
	
	
	public int getCoupletsNewerThanHitCount(String firstTerm, String secondTerm, Date timestamp) {
		@SuppressWarnings("unchecked")
		List<Long> results = getJpaTemplate().find("select count(text) from Tweet where " +
				                                   "text like ?1 and " +
				                                   "text like ?2 and " +
				                                   "createdAt >= ?3", 
				                                   "%" + firstTerm + "%", 
				                                   "%" + secondTerm + "%",
				                                   timestamp);		

		long count = results.get(0);
		return (int) count;
	}
	public int getHitCount(String term) {
		@SuppressWarnings("unchecked")
		List<Long> results = getJpaTemplate().find("select count(text) from Tweet where text like ?1", "%" + term + "%");		

		long count = results.get(0);
		return (int) count;
	}
	
	public int getNewerThanHitCount(String term, Date timestamp) {
		@SuppressWarnings("unchecked")
		List<Long> results = getJpaTemplate().find("select count(text) from Tweet where " +
				                                   "text like ?1 and " +
				                                   "createdAt >= ?2", 
				                                   "%" + term + "%", timestamp);		

		long count = results.get(0);
		return (int) count;
	}
}
