/**
 * 
 */
package org.phiresearchlab.twitter.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

/**
 *
 * @author Joel M. Rives
 * May 4, 2011
 */

@Entity
public class MinuteTweets extends DomainObject {

	private static final long serialVersionUID = 4852795090264151088L;

	@Column(nullable = false)
	private Date timestamp;

	@OneToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
	private List<Tweet> tweets;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderColumn(name = "term_hit_count_index")
	private List<TermHitCount> termHitCounts;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderColumn(name = "relationship_hit_count_index")
	private List<RelationshipHitCount> relationshipHitCounts;
	
	public MinuteTweets() { }
	
	public MinuteTweets(Date timestamp) {
		this.timestamp = timestamp;
		this.termHitCounts = new ArrayList<TermHitCount>();
		this.relationshipHitCounts = new ArrayList<RelationshipHitCount>();
		this.tweets = new ArrayList<Tweet>();
	}
	
	public void addTermHitCount(TermHitCount hitCount) {
		this.termHitCounts.add(hitCount);
	}
	
	public void addRelationshipHitCount(RelationshipHitCount hitCount) {
		this.relationshipHitCounts.add(hitCount);
	}
	
	public void addTweet(Tweet tweet) {
		tweets.add(tweet);
	}
	
	public TermHitCount findTermHitCount(String term) {
	    for (TermHitCount hitCount: termHitCounts)
	        if (hitCount.getTerm().equalsIgnoreCase(term))
	            return hitCount;
	    return null;
	}
	
	public RelationshipHitCount findRelationshipHitCount(String term1, String term2) {
		for (RelationshipHitCount hitCount: relationshipHitCounts) 
			if (contains(hitCount, term1) && contains(hitCount, term2))
				return hitCount;
		return null;
	}
	
	public List<Tweet> getTweets() {
		return tweets;
	}

	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
	}

	public List<TermHitCount> getTermHitCounts() {
		return termHitCounts;
	}

	public void setTermHitCounts(List<TermHitCount> termHitCounts) {
		this.termHitCounts = termHitCounts;
	}

    public Date getTimestamp()
    {
        return this.timestamp;
    }

    public void setTimestamp(Date timestamp)
    {
        this.timestamp = timestamp;
    }

	public List<RelationshipHitCount> getRelationshipHitCounts() {
		return relationshipHitCounts;
	}

	public void setRelationshipHitCounts(
			List<RelationshipHitCount> relationshipHitCounts) {
		this.relationshipHitCounts = relationshipHitCounts;
	}

	private boolean contains(RelationshipHitCount hitCount, String term) {
		if (hitCount.getTerm1().trim().equalsIgnoreCase(term))
			return true;
		
		if (hitCount.getTerm2().trim().equalsIgnoreCase(term))
			return true;
		
		return false;
	}

}
