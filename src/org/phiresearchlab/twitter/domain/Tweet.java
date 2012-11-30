/**
 * 
 */
package org.phiresearchlab.twitter.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author Joel M. Rives
 * May 4, 2011
 */

@Entity
public class Tweet extends DomainObject {

	private static final long serialVersionUID = 2685275550944374680L;

	private String contributors;
	private Date createdAt;
	private Double latitude;
	private Double longitude;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private List<HashTag> hashTags;

	private Long statusId;
    private Long inReplyToStatusId;
    private Long inReplyToUserId;
    
    @OneToOne
    private Location location;
    
    private Long retweetCount;
    private String source;
    private String text;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<URLEntity> urlEntities;
    
    private Long userId;
    private String userMentions;
    private Boolean favorited;
    private Boolean retweeted;
    private Boolean truncated;
    private Boolean analyzed;
	
    public String getContributors() {
		return contributors;
	}
	public void setContributors(String contributors) {
		this.contributors = contributors;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public List<HashTag> getHashTags() {
		return hashTags;
	}
	public void setHashTags(List<HashTag> hashTags) {
		this.hashTags = hashTags;
	}
	public Long getStatusId() {
		return statusId;
	}
	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}
	public Long getInReplyToStatusId() {
		return inReplyToStatusId;
	}
	public void setInReplyToStatusId(Long inReplyToStatusId) {
		this.inReplyToStatusId = inReplyToStatusId;
	}
	public Long getInReplyToUserId() {
		return inReplyToUserId;
	}
	public void setInReplyToUserId(Long inReplyToUserId) {
		this.inReplyToUserId = inReplyToUserId;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location place) {
		this.location = place;
	}
	public Long getRetweetCount() {
		return retweetCount;
	}
	public void setRetweetCount(Long retweetCount) {
		this.retweetCount = retweetCount;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<URLEntity> getUrlEntities() {
		return urlEntities;
	}
	public void setUrlEntities(List<URLEntity> urlEntities) {
		this.urlEntities = urlEntities;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserMentions() {
		return userMentions;
	}
	public void setUserMentions(String userMentions) {
		this.userMentions = userMentions;
	}
	public Boolean getFavorited() {
		return favorited;
	}
	public void setFavorited(Boolean favorited) {
		this.favorited = favorited;
	}
	public Boolean getRetweeted() {
		return retweeted;
	}
	public void setRetweeted(Boolean retweeted) {
		this.retweeted = retweeted;
	}
	public Boolean getTruncated() {
		return truncated;
	}
	public void setTruncated(Boolean truncated) {
		this.truncated = truncated;
	}
	public Boolean getAnalyzed() {
		return analyzed;
	}
	public void setAnalyzed(Boolean analyzed) {
		this.analyzed = analyzed;
	}
    
}
