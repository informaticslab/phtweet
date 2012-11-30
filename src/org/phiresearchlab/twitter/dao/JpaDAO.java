/**
 * 
 */
package org.phiresearchlab.twitter.dao;

import java.lang.reflect.ParameterizedType;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.phiresearchlab.twitter.shared.Period;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.support.JpaDaoSupport;

/**
 * 
 * @author Joel M. Rives
 * Mar 21, 2011
 */

public abstract class JpaDAO<K, E> extends JpaDaoSupport {
	static final long ONE_MINUTE = 1000 * 60; // milliseconds
    static final long ONE_HOUR = ONE_MINUTE * 60; // milliseconds
    static final long ONE_DAY = ONE_HOUR * 24; // milliseconds
    static final long ONE_MONTH = ONE_DAY * 31; // milliseconds
    
	protected Class<E> entityClass;
	
	@SuppressWarnings("unchecked")
	public JpaDAO() {
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		this.entityClass = (Class<E>) genericSuperclass.getActualTypeArguments()[1];
	}
	
	public List<E> findAll() {
		List<E> results = getJpaTemplate().execute(new JpaCallback<List<E>>() {
			@SuppressWarnings("unchecked")
			public List<E> doInJpa(EntityManager em) throws PersistenceException {
				Query q = em.createQuery("SELECT h FROM " + entityClass.getName() + " h");
				return (List<E>) q.getResultList();
			}			
		});
		
		return results;
	}
	
	public E findById(K id) {
		return getJpaTemplate().find(entityClass, id);
	}

	public E flush(E entity) {
		getJpaTemplate().flush();
		return entity;
	}
	
	public E merge(E entity) {
		return getJpaTemplate().merge(entity);
	}
	
	public void persist(E entity) {
		getJpaTemplate().persist(entity);
	}
	
	public void refresh(E entity) {
		getJpaTemplate().refresh(entity);
	}
	
	public void remove(E entity) {
		getJpaTemplate().remove(entity);
	}
	
	@SuppressWarnings("rawtypes")
    protected List find(String query, Object...objects) {
	    List results = getJpaTemplate().find(query, objects);
	    return results;
	}
	
    protected Object findOne(String query, Object...objects) {
        @SuppressWarnings("rawtypes")
        List results = getJpaTemplate().find(query, objects);
        
        if (results.isEmpty())
            return null;
        
        return results.get(0);
    }
    	
    protected Date toBoundary(Date timestamp, Period period) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(timestamp);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = period == Period.Day || period == Period.Hour || period == Period.Minute ? cal.get(Calendar.DAY_OF_MONTH) : 0;
        int hour = period == Period.Hour || period == Period.Minute ? cal.get(Calendar.HOUR_OF_DAY) : 0;
        int minute = period == Period.Minute ? cal.get(Calendar.MINUTE) : 0;
        cal.clear();
        cal.set(year, month, day, hour, minute);
        return cal.getTime();
    }

}
