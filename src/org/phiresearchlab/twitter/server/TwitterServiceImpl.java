/**
 * 
 */
package org.phiresearchlab.twitter.server;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.phiresearchlab.twitter.client.TwitterService;
import org.phiresearchlab.twitter.dao.CategoryDAO;
import org.phiresearchlab.twitter.dao.FilterTermDAO;
import org.phiresearchlab.twitter.dao.LocationDAO;
import org.phiresearchlab.twitter.dao.MinuteTweetsDAO;
import org.phiresearchlab.twitter.dao.TermHitCountDAO;
import org.phiresearchlab.twitter.dao.TweetDAO;
import org.phiresearchlab.twitter.domain.Category;
import org.phiresearchlab.twitter.domain.FilterTerm;
import org.phiresearchlab.twitter.domain.Location;
import org.phiresearchlab.twitter.domain.MinuteTweets;
import org.phiresearchlab.twitter.domain.RelationshipHitCount;
import org.phiresearchlab.twitter.domain.TermHitCount;
import org.phiresearchlab.twitter.domain.Tweet;
import org.phiresearchlab.twitter.shared.Period;
import org.phiresearchlab.twitter.shared.TermAssociation;
import org.phiresearchlab.twitter.shared.TermHistory;
import org.phiresearchlab.twitter.shared.TermReport;
import org.phiresearchlab.twitter.shared.TermState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import twitter4j.FilterQuery;

/**
 *
 * @author Joel M. Rives
 * May 4, 2011
 */

@Service("twitterService")
public class TwitterServiceImpl implements TwitterService {
	
	private static final long ONE_MINUTE = 1000 * 60;
	private static final long ONE_HOUR = ONE_MINUTE * 60;
	private static final long ONE_DAY = ONE_HOUR * 24;
	private static final long ONE_WEEK = ONE_DAY * 7;
	private static final long ONE_YEAR = ONE_WEEK * 52;
		
	private static long lastStartTime = 0L;
	
	private static Logger LOG = Logger.getLogger(TwitterServiceImpl.class);
	
	@Autowired private TweetDAO tweetDAO;
	@Autowired private FilterTermDAO filterTermDAO;
	@Autowired private CategoryDAO categoryDAO;	
	@Autowired private LocationDAO locationDAO;
    @Autowired private MinuteTweetsDAO minuteTweetsDAO;
	@Autowired private TermHitCountDAO termHitCountDAO;
	@Autowired private PersistingStatusListener statusListener;

	@PostConstruct
	public void init() {
		startCapturing();
	}
	
	@PreDestroy
	public void shutDown() {
		LOG.info("PreDestroy");
		stopCapturing();
	}
	
	public void clearFilterTerms() {
		stopCapturing();
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Category createCategory(Category category) {
	    LOG.info("Creating new category " + category.getName());
		categoryDAO.persist(category);
		startCapturing();
		return export(categoryDAO.findById(category.getId()));
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Tweet createTweet(Tweet tweet) {
//	    Location location = tweet.getLocation();
//	    if (null != location && null == location.getId())
//	        locationDAO.persist(location);
//	    
//	    tweetDAO.persist(tweet);
	    
	    MinuteTweets minuteTweets = minuteTweetsDAO.findByMinute(tweet.getCreatedAt());
	    
	    if (null == minuteTweets) {
	        minuteTweets = new MinuteTweets(tweet.getCreatedAt());
	        minuteTweetsDAO.persist(minuteTweets);
	    }
	    
	    updateTermCounts(minuteTweets, tweet);
	    updateRelationshipCounts(minuteTweets, tweet);
	    minuteTweets.addTweet(null);
	    
	    return tweet;
	}
	
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void deleteCategory(String name) {
        LOG.info("Deleting category " + name);
        Category category = categoryDAO.findByName(name);
        
        if (null == category)
            return;
        
        categoryDAO.remove(category);
        startCapturing();
	}
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Category> getAllCategories() {
        List<Category> list = categoryDAO.findAll();
        List<Category> exportable = new ArrayList<Category>();
        
        for (Category category: list)
            exportable.add(export(category));
        
        return exportable;
    }
	
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Category getCategory(String name) {
        return export(categoryDAO.findByName(name));
	}
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public TermHistory getTermHistory(String term, Date timestamp) {
    	FilterTerm filterTerm = filterTermDAO.findByName(term);
    	
    	if (null == filterTerm)
    		return null;
    	
    	Date lastHour = new Date(timestamp.getTime() - ONE_HOUR);
    	Date lastDay = new Date(timestamp.getTime() - ONE_DAY);
    	
    	List<TermHitCount> lastHourHits = termHitCountDAO.findForHour(term, lastHour);
    	List<TermHitCount> lastDayHits = termHitCountDAO.findForDay(term, lastDay);
    	
    	TermHistory history = new TermHistory(term, filterTerm.getColor());
    	
	    for (TermHitCount hitCount: lastHourHits) {
	       	history.incrementHourHits(hitCount.getHits());
	    }
	
	    for (TermHitCount hitCount: lastDayHits) {
        	history.incrementDayHits(hitCount.getHits());
	    }
    	
//    	history.setTotalHits(tweetDAO.getHitCount(term));
    	history.setTotalHits(0);
    	
    	return history;
    }
	
	
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<TermState> getTermStates(String categoryName, Date timestamp, Period period) {
    	LOG.info("Entering getTermStates");
		List<TermState> termStates = new ArrayList<TermState>();

		Category category = categoryDAO.findByName(categoryName);
        List<FilterTerm> filterTerms = category.getFilterTerms();
        Set<FilterTerm> filterSet = new HashSet<FilterTerm>(filterTerms);
        
        List<MinuteTweets> minuteTweetsList = period == Period.Hour ? minuteTweetsDAO.findForHour(timestamp)
        		                                                    : minuteTweetsDAO.findForDay(timestamp);
		
		for (FilterTerm filterTerm: filterSet) {
			String term = filterTerm.getName().trim();
			if (term.length() > 0) {
				TermState termState = createTermState(filterTerm, minuteTweetsList);
				termState.setPeriod(period);
				termState.setEnabled(filterTerm.getEnabled() == true);
				termStates.add(termState);
			}			
		}
		
		LOG.info("Leaving getTermStates");
		return termStates;
	}

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public TermReport getTermReport(String categoryName, String term, Date start, Period period, int count) {
		Category category = categoryDAO.findByName(categoryName);
        List<FilterTerm> filterTerms = category.getFilterTerms();
        FilterTerm filterTerm = null;
        
        for (FilterTerm ft: filterTerms)
        	if (ft.getName().equals(term))
        		filterTerm = ft;
    	
        List<TermHitCount> hitCountList = getTermHitCounts(term, start, period, count);
    	TermReport report = new TermReport();
    	List<Integer> hits = new ArrayList<Integer>(count);
    	
    	for (int i = 0; i < count; i++)
    		hits.add(0);
    	
		for (TermHitCount hitCount: hitCountList) {	    	
	    	if (Period.Hour == period)
	    		getHourlyHits(hitCount, start, hits);
	    	else
	    		getDailyHits(hitCount, start, hits);
		}

        System.out.println(term + ":");
        for (Integer value: hits)
            System.out.print(value + ", ");
        System.out.println();

    	report.setTerm(term);
    	report.setPeriod(period);
    	report.setHits(hits);
    	report.setColor(filterTerm.getColor());
    	report.setEnabled(filterTerm.getEnabled() == true);

    	return report;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<TermReport> getTermReports(String categoryName, Date start, Period period, int count) {
    	List<TermReport> termReports = new ArrayList<TermReport>();
    	
		Category category = categoryDAO.findByName(categoryName);
        List<FilterTerm> filterTerms = category.getFilterTerms();
        Set<FilterTerm> filterSet = new HashSet<FilterTerm>(filterTerms);

        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        Date end = start;
        
        if (Period.Hour == period) {
        	int hour = cal.get(Calendar.HOUR_OF_DAY);
        	cal.set(year, month, day, hour, 0, 0);
        	start = cal.getTime();
        	cal.add(Calendar.HOUR_OF_DAY, count);
        	end = cal.getTime();
        } else {
        	cal.set(year, month, day);
        	start = cal.getTime();
        	cal.add(Calendar.DAY_OF_MONTH, count);
        	end = cal.getTime();
        }

        LOG.info("Finding minute tweets between " + start + " and " + end);
        
        List<MinuteTweets> minuteTweetsList;
		try {
			minuteTweetsList = minuteTweetsDAO.findBetween(start, end);
		} catch (Exception e) {
			LOG.error("Data access failed", e);
			throw new RuntimeException("Data access failed");
		}
		
		LOG.info("Found " + minuteTweetsList.size() + " entries");

		for (FilterTerm filterTerm: filterSet) {
			String term = filterTerm.getName().trim();
			System.out.println(term + ":");
			if (term.length() > 0) {
				TermReport report = createTermReport(term, start, period, minuteTweetsList, count);
		    	report.setColor(filterTerm.getColor());
				report.setPeriod(period);
				report.setEnabled(filterTerm.getEnabled() == true);
				termReports.add(report);
				for (Integer hits: report.getHits())
					System.out.print(hits + ", ");
				System.out.println();
			}			
		}

		System.out.println();

		return termReports;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public List<TermReport> getTermAssociations(String categoryName, Date timestamp, Period period) {
		Category category = categoryDAO.findByName(categoryName);
        List<FilterTerm> filterTerms = category.getFilterTerms();
        Set<FilterTerm> filterSet = new HashSet<FilterTerm>(filterTerms);
        
        List<MinuteTweets> minuteTweetsList = period == Period.Hour ? minuteTweetsDAO.findForHour(timestamp)
                                                                    : minuteTweetsDAO.findForDay(timestamp);

        List<TermReport> termReports = new ArrayList<TermReport>();
		List<TermAssociation> associations = new ArrayList<TermAssociation>();
		
        for (FilterTerm filterTerm: filterSet) {
        	String name = filterTerm.getName().trim();
        	if (name.length() > 0) {
        		TermReport report = new TermReport(name, filterTerm.getColor(), period);
        		report.setAssociates(associations);
        		report.setEnabled(filterTerm.getEnabled() == true);
        		termReports.add(report);
        	}
        }
        
        for (TermReport report: termReports) {
        	for (TermReport otherReport: termReports) {
        		if (report.getTerm().equals(otherReport.getTerm())) 
        			continue;
        		
				if (null == findAssociation(associations, report.getTerm(), otherReport.getTerm())) {
					int hits = getAssociationHits(minuteTweetsList, report.getTerm(), otherReport.getTerm());
					if (hits > 0) {
						TermAssociation ta = new TermAssociation();
						ta.addTerm(report);
						ta.addTerm(otherReport);
						ta.setCount(hits);
						associations.add(ta);
					}
				}
			}
        }
        
        Collections.sort(associations);
               
        StringBuffer buffer = new StringBuffer("Associations: \n");
        for (TermAssociation ta: associations)
        	buffer.append("   " + ta.getTerms().get(0).getTerm() + ":" + ta.getTerms().get(1).getTerm() + " (" + ta.getCount() + ") \n");
        System.out.println(buffer.toString());
        
        return termReports;
	}
	
	private int getAssociationHits(List<MinuteTweets> minuteTweetList, String term1, String term2) {
		int hits = 0;
		
		for (MinuteTweets minuteTweets: minuteTweetList) {
			RelationshipHitCount hitCount = minuteTweets.findRelationshipHitCount(term1, term2);
			if (null != hitCount)
				hits += hitCount.getHits();
		}
		
		return hits;
	}
	
	private boolean contains(List<TermReport> list, String term) {
		for (TermReport report: list)
			if (report.getTerm().equalsIgnoreCase(term))
				return true;
		return false;
	}
	
	private TermAssociation findAssociation(List<TermAssociation> associations, String firstTerm, String secondTerm) {
		for (TermAssociation ta: associations) {
			List<TermReport> terms = ta.getTerms();
			if (contains(terms, firstTerm) && contains(terms, secondTerm))
				return ta;
		}
		
		return null;
	}
	
	public TermReport getTermAssociations(FilterTerm filterTerm, List<String> filterTermList, Period period) {
		List<TermAssociation> associations = new ArrayList<TermAssociation>();
		List<String> associates = new ArrayList<String>();
		associates.add(filterTerm.getName());
		
		
		List<String> otherTerms = new ArrayList<String>(filterTermList);
		otherTerms.remove(filterTerm.getName());
		
		TermReport report = new TermReport(filterTerm.getName(), filterTerm.getColor(), period);
		report.setAssociates(associations);
		
		return report;
		
	}
	
	public void startCapturing() {
		if ((System.currentTimeMillis() - lastStartTime) < 1000 * 5)
			return;
		
		List<FilterTerm> trackList = filterTermDAO.findAll();
		
		if (trackList.isEmpty())
			return;

        // Put the filter terms in a Set to guarantee uniqueness.
        Set<String> termSet = new HashSet<String>();
        for (FilterTerm term: trackList) {
            String name = term.getName();
            if (null != name && name.trim().length() > 0)
                termSet.add(name.trim());
        }

        long followArray[] = { };
        String[] trackArray = termSet.toArray(new String[termSet.size()]);
        
        StringBuffer buffer = new StringBuffer("Tracking: ");
        for (String name: trackArray)
            buffer.append(name + ", ");
        System.out.println(buffer.toString());
        	
        statusListener.startCapturing(new FilterQuery(0, followArray, trackArray));
        LOG.info("Started capturing Tweets...");
	}
	
	public void stopCapturing() {
		statusListener.stopCapturing();
        LOG.info("Stopped capturing Tweets...");
	}
	
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Category updateCategory(Category category) {
        LOG.info("Updating category " + category.getName());
        if (null == category.getId()) 
            return createCategory(category);
        
        Category original = categoryDAO.findById(category.getId());
        
        if (null == original)
            return createCategory(category);
        
        List<FilterTerm> filterTerms = new ArrayList<FilterTerm>(original.getFilterTerms());
        original.getFilterTerms().clear();
        original.setFilterTerms(category.getFilterTerms());
        
        for (FilterTerm term: filterTerms)
        	filterTermDAO.remove(term);
        
        startCapturing();
        return category;
    }
    
    private TermReport createTermReport(String term, Date start, Period period, List<MinuteTweets> list, int count) {
    	TermReport report = new TermReport();
    	List<Integer> hits = new ArrayList<Integer>(count);
    	
    	for (int i = 0; i < count; i++)
    		hits.add(0);
    	
    	if (Period.Hour == period)
    		getHourlyHits(term, start, list, hits);
    	else
    		getDailyHits(term, start, list, hits);
    		
    	
    	report.setTerm(term);
    	report.setHits(hits);
    	return report;
    }
    
    private void getDailyHits(String term, Date start, List<MinuteTweets> list, List<Integer> hits) {
    	for (MinuteTweets mt: list) {
    		TermHitCount hitCount = findHitCount(term, mt.getTermHitCounts());
    		if (null != hitCount) {
    			getDailyHits(hitCount, start, hits);
    		}
    	}
    }
    
    private void getDailyHits(TermHitCount hitCount, Date start, List<Integer> hits) {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(start);
    	int startYear = cal.get(Calendar.YEAR);
    	int maxDays = cal.getActualMaximum(Calendar.DAY_OF_YEAR);
    	int startDay = cal.get(Calendar.DAY_OF_YEAR);

    	cal.setTime(hitCount.getTimestamp());
			int year = cal.get(Calendar.YEAR);
		int day = cal.get(Calendar.DAY_OF_YEAR);
		
		// This assumes the day range does not span multiple years
		if (year > startYear)
			day += maxDays;
		
		int index = day - startDay;
		hits.set(index, hits.get(index) + hitCount.getHits());    	
    }
    
    private void getHourlyHits(String term, Date start, List<MinuteTweets> list, List<Integer> hits) {
    	for (MinuteTweets mt: list) {
    		TermHitCount hitCount = findHitCount(term, mt.getTermHitCounts());
    		if (null != hitCount) {
    			getHourlyHits(hitCount, start, hits);
    		}
    	}
    }
    
    private void getHourlyHits(TermHitCount hitCount, Date start, List<Integer> hits) {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(start);
    	int startDay = cal.get(Calendar.DAY_OF_YEAR);
    	int startHour = cal.get(Calendar.HOUR_OF_DAY);
		cal.setTime(hitCount.getTimestamp());
		int day = cal.get(Calendar.DAY_OF_YEAR);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int modHour = (day - startDay) * 24 + hour;
		int index = modHour - startHour;
		
		if (index < 0 || index >= hits.size()) {
			LOG.info("startDay: " + startDay + 
					 ", startHour: " + startHour + 
					 ", day: " + day + 
					 ", hour: " + hour + 
					 ", index: " + index);
			return;
		}
		
		hits.set(index, hits.get(index) + hitCount.getHits());
    }
    
    private List<TermHitCount> getTermHitCounts(String term, Date start, Period period, int count) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        Date end = start;
        
        if (Period.Hour == period) {
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            cal.set(year, month, day, hour, 0, 0);
            start = cal.getTime();
            cal.add(Calendar.HOUR_OF_DAY, count);
            end = cal.getTime();
        } else {
            cal.set(year, month, day);
            start = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, count);
            end = cal.getTime();
        }

        List<TermHitCount> hitCountList;
        try {
            hitCountList = termHitCountDAO.findBetween(term, start, end);
        } catch (Exception e) {
            LOG.error("Data access failed", e);
            throw new RuntimeException("Data access failed");
        }
        
        return hitCountList;
    }
    
    private TermState createTermState(FilterTerm filterTerm, List<MinuteTweets> list) {
    	String term = filterTerm.getName().trim();
    	TermState termState = new TermState(term);
    	int hits = 0;
    	
    	for (MinuteTweets mt: list) {
    		for (TermHitCount thc: mt.getTermHitCounts())
    			if (term.equalsIgnoreCase(thc.getTerm().trim()))
    				hits += thc.getHits();
    	}
    	
    	termState.setColor(filterTerm.getColor());
    	termState.setHits(hits);
    	
    	return termState;
    }
	
    private Category export(Category category) {
        List<FilterTerm> persistent = category.getFilterTerms();
        List<FilterTerm> serializable = new ArrayList<FilterTerm>();
        
        for (FilterTerm term: persistent) 
            serializable.add(term);
        
        category.setFilterTerms(serializable);
        return category;
    }
    
   
    private TermHitCount findHitCount(String term, List<TermHitCount> list) {
    	for (TermHitCount hitCount: list)
    		if (hitCount.getTerm().trim().equalsIgnoreCase(term))
    			return hitCount;
    	return null;
    }
    
	private void updateRelationshipCounts(MinuteTweets minuteTweets, Tweet tweet) {
        List<FilterTerm> filterTerms = filterTermDAO.findAll();
        Set<FilterTerm> filterSet = new HashSet<FilterTerm>(filterTerms);
	    
        for (FilterTerm filterTerm: filterSet) {
            String name = filterTerm.getName().trim();
            if (name.length() == 0)
            	continue;
            
            if (tweet.getText().contains(name)) {
	            for (FilterTerm otherTerm: filterSet) {
	            	String otherName = otherTerm.getName().trim();
	            	if (otherName.length() == 0)
	            		continue;
	            	
	            	if (name.equals(otherName))
	            		continue;
	            	
	            	if (tweet.getText().contains(otherName)) {
	            		RelationshipHitCount hitCount = minuteTweets.findRelationshipHitCount(name, otherName);
	            		if (null == hitCount) {
	            			hitCount = new RelationshipHitCount(name, otherName);
	            			minuteTweets.addRelationshipHitCount(hitCount);
	            		}
	            		hitCount.incrementHits();
	            	}
	            }
            }
        }		
	}
		
	private void updateTermCounts(MinuteTweets minuteTweets, Tweet tweet) {
        List<FilterTerm> filterTerms = filterTermDAO.findAll();
        Set<FilterTerm> filterSet = new HashSet<FilterTerm>(filterTerms);
	    
        for (FilterTerm filterTerm: filterSet) {
            String term = filterTerm.getName().trim().toUpperCase();
            if (term.length() > 0) {
                if (tweet.getText().toUpperCase().contains(term)) {
                    TermHitCount hitCount = minuteTweets.findTermHitCount(term);
                    if (null == hitCount){
                        hitCount = new TermHitCount(filterTerm.getName(), minuteTweets.getTimestamp());
                        minuteTweets.addTermHitCount(hitCount);
                    }
                    hitCount.incrementHits();
                }
            }
        }
	}
	
}
