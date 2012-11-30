/**
 * 
 */
package org.phiresearchlab.twitter.domain;

import javax.persistence.Entity;

/**
 *
 * @author Joel M. Rives
 * May 4, 2011
 */

@Entity
public class URLEntity extends DomainObject {

	private static final long serialVersionUID = 4685006899796756672L;

	private String displayURL;
	private Integer end;
	private String expandedURL;
	private Integer start;
	private String url;
	
	public String getDisplayURL() {
		return displayURL;
	}
	public void setDisplayURL(String displayURL) {
		this.displayURL = displayURL;
	}
	public Integer getEnd() {
		return end;
	}
	public void setEnd(Integer end) {
		this.end = end;
	}
	public String getExpandedURL() {
		return expandedURL;
	}
	public void setExpandedURL(String expandedURL) {
		this.expandedURL = expandedURL;
	}
	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
