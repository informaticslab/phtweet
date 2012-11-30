/**
 * 
 */
package org.phiresearchlab.twitter.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

/**
 *
 * @author Joel M. Rives
 * May 4, 2011
 */

@Entity
public class Location extends DomainObject {

	private static final long serialVersionUID = -4075684424699114198L;

	private String boundingBoxCoordinates;
	private String boundingBoxType;
	
	@OneToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
	private List<Location> containedWithin;
	private String country;
	private String countryCode;
	private String fullName;
	private String geometeryCoordinates;
	private String geometryType;
	private String placeId;
	private String name;
	private String placeType;
	private String streetAddress;
	private String url;
	
	public String getBoundingBoxCoordinates() {
		return boundingBoxCoordinates;
	}
	public void setBoundingBoxCoordinates(String boundingBoxCoordinates) {
		this.boundingBoxCoordinates = boundingBoxCoordinates;
	}
	public String getBoundingBoxType() {
		return boundingBoxType;
	}
	public void setBoundingBoxType(String boundingBoxType) {
		this.boundingBoxType = boundingBoxType;
	}
	public List<Location> getContainedWithin() {
		return containedWithin;
	}
	public void setContainedWithin(List<Location> containedWithin) {
		this.containedWithin = containedWithin;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getGeometeryCoordinates() {
		return geometeryCoordinates;
	}
	public void setGeometeryCoordinates(String geometeryCoordinates) {
		this.geometeryCoordinates = geometeryCoordinates;
	}
	public String getGeometryType() {
		return geometryType;
	}
	public void setGeometryType(String geometryType) {
		this.geometryType = geometryType;
	}
	public String getPlaceId() {
		return placeId;
	}
	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPlaceType() {
		return placeType;
	}
	public void setPlaceType(String placeType) {
		this.placeType = placeType;
	}
	public String getStreetAddress() {
		return streetAddress;
	}
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
