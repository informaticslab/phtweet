/**
 * 
 */
package org.phiresearchlab.twitter.shared;

import java.io.Serializable;

/**
 *
 * @author Joel M. Rives
 * Mar 23, 2011
 */
public abstract class BaseDTO implements Serializable {

	private static final long serialVersionUID = 3986982498568875740L;

	private Long id;
	
	public BaseDTO() {
	}
	
	public BaseDTO(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
