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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 *
 * @author Joel M. Rives
 * May 4, 2011
 */

public interface TwitterServiceAsync  {
	
	public void clearFilterTerms(AsyncCallback<Void> callback);
	
	public void createCategory(Category category, AsyncCallback<Category> callback);
	
	public void createTweet(Tweet tweet, AsyncCallback<Tweet> callback);
	
	public void deleteCategory(String name, AsyncCallback<Void> callback);
	
    public void getAllCategories(AsyncCallback<List<Category>> callback);
    
	public void getCategory(String name, AsyncCallback<Category> callback);
	
	public void getTermHistory(String term, Date timestamp, AsyncCallback<TermHistory> callback);
	
	public void getTermStates(String categoryName, Date timestamp, Period period, AsyncCallback<List<TermState>> callback);
	
	public void getTermReport(String categoryName, String term, Date timestamp, Period period, int count, AsyncCallback<TermReport> callback);
	
	public void getTermReports(String categoryName, Date timestamp, Period period, int count, AsyncCallback<List<TermReport>> callback);
	
	public void getTermAssociations(String categoryName, Date timestamp, Period period, AsyncCallback<List<TermReport>> callback);
	
	public void updateCategory(Category category, AsyncCallback<Category> callback);
	
}
