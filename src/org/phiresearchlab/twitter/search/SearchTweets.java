package org.phiresearchlab.twitter.search;

import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class SearchTweets {
	
	private Twitter twitter;
	
	public  SearchTweets() {
		twitter = new TwitterFactory().getInstance();
	}
	
	public void searchRecent(String queryString) {
		Query query = new Query(queryString);
//		query.setPage(1);
		query.setRpp(100);
		query.setResultType(Query.RECENT);

		QueryResult result;
		try {
			result = twitter.search(query);
		} catch (TwitterException e) {
			System.err.println("Twitter search failed " + e.getMessage());
			e.printStackTrace();
			return;
		}
		
		List<Tweet> tweets = result.getTweets();
		System.out.println("Received " + tweets.size() + " tweets");
		
		for (Tweet tweet: tweets) {
			StringBuffer buffer = new StringBuffer(tweet.getCreatedAt().toString() + ": ");
			buffer.append("@" + tweet.getFromUser() + ": ");
			buffer.append(tweet.getText());
			System.out.println(buffer.toString());
		}
	}
	
    public static void main(String[] args) {
    	SearchTweets search = new SearchTweets();
    	search.searchRecent("headache");
    }
}
