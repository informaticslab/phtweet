/**
 * 
 */
package org.phiresearchlab.twitter.client;


import java.util.Date;
import java.util.List;

import org.phiresearchlab.twitter.domain.Category;
import org.phiresearchlab.twitter.domain.Tweet;
import org.phiresearchlab.twitter.shared.Period;
import org.phiresearchlab.twitter.shared.TermHistory;
import org.phiresearchlab.twitter.shared.TermReport;
import org.phiresearchlab.twitter.shared.TermState;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 *
 * @author Joel M. Rives
 * May 4, 2011
 */

@RemoteServiceRelativePath("services/twitterService")
public interface TwitterService extends RemoteService  {
	
	public void clearFilterTerms();
	
	public Category createCategory(Category category);
	
	public Tweet createTweet(Tweet tweet);
	
	public void deleteCategory(String name);
	
    public List<Category> getAllCategories();
    
	public Category getCategory(String name);
	
	public TermHistory getTermHistory(String term, Date timestamp);
	
	public List<TermState> getTermStates(String categoryName, Date timestamp, Period period);
	
	public TermReport getTermReport(String categoryName, String term, Date timestamp, Period period, int count);
	
	public List<TermReport> getTermReports(String categoryName, Date timestamp, Period period, int count);
	
	public List<TermReport> getTermAssociations(String categoryName, Date timestamp, Period period);
	
	public Category updateCategory(Category category);
	
}
