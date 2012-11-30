/**
 * 
 */
package org.phiresearchlab.twitter.dao;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.phiresearchlab.twitter.domain.Tweet;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Joel M. Rives
 * May 9, 2011
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={ "/META-INF/test/applicationContext.xml" })
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=true)
@Transactional
public class TweetDAOTest {
	
	private static final long ONE_MINUTE = 1000 * 60;
	private static final long ONE_HOUR = ONE_MINUTE * 60;

	@Resource TweetDAO tweetDAO;
	
	@Test
	public void testGetAssociationHits() throws Exception {
		final Date now = new Date();
		final String phraseOne = "One is as Two";
		
		for (int i = 0; i < 3; i++)
			tweetDAO.persist(createTweet(phraseOne));
		
		final String phraseTwo = "One is as Two is as Three";
		
		for (int i = 0; i < 3; i++)
			tweetDAO.persist(createTweet(phraseTwo));
		
		List<String> terms = new ArrayList<String>();
		terms.add("Two");
		 
		int result = tweetDAO.getAssociationHits("One", terms, now);
		assertEquals("We are expecting 6 results", 6, result);
		
		terms.add("Three");
		result = tweetDAO.getAssociationHits("One", terms, now);
		assertEquals("We are expecting 3 results", 3, result);
	}
	
	@Test
	public void testGetHitCount() throws Exception {
		final String phrase = "Tweedle Dee";
		final int count = 5;
		
		for (int i = 0; i < count; i++)
			tweetDAO.persist(createTweet(phrase));
		
		int result = tweetDAO.getHitCount(phrase);
		
		assertEquals("We are expecting " + count + " results", count, result);
	}
	
	@Test
	public void testGetNewerThanHitCount() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.set(2000, 1, 1);
		Date before = cal.getTime();
		cal.set(2010, 1, 1);
		Date after = cal.getTime();
		
		final String phrase = "Tweedle Dee";
		final long count = 5;
		
		for (int i = 0; i < count; i++)
			tweetDAO.persist(createTweet(phrase));
		
		tweetDAO.persist(createTweet(phrase, before));
		
		long result = tweetDAO.getNewerThanHitCount(phrase, after);
		
		assertEquals("We are expecting " + count + " results", count, result);		
	}
	
	@Test
	public void testBetweenHits() throws Exception {
		String phrase = "Between";
		long thirtyMinutes = 30 * ONE_MINUTE;
		Date oneHourAgo = new Date(System.currentTimeMillis() - ONE_HOUR);
		Date twoHoursAgo = new Date(System.currentTimeMillis() - (2 * ONE_HOUR));
		Date thirtyMinutesAgo = new Date(System.currentTimeMillis() - thirtyMinutes);
		Date oneHourAndThirtyMinutesAgo = new Date(System.currentTimeMillis() - ONE_HOUR - thirtyMinutes);
		
		tweetDAO.persist(createTweet(phrase, oneHourAgo));
		tweetDAO.persist(createTweet(phrase, twoHoursAgo));
		
		long result = tweetDAO.getBetweenHitCount(phrase, oneHourAndThirtyMinutesAgo, thirtyMinutesAgo);
		
		assertEquals("There can be only one!", 1, result);
	}
	
	
	private Tweet createTweet(String phrase) {
		return createTweet(phrase, new Date());
	}
	
	private Tweet createTweet(String phrase, Date createdAt) {
		Tweet tweet = new Tweet();
		
		tweet.setText("This tweet contains " + phrase + " as part of it's text");
		tweet.setCreatedAt(createdAt);
		
		return tweet;
	}
	
}
