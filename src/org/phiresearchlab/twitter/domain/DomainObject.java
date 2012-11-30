/**
 * 
 */
package org.phiresearchlab.twitter.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is the common, base class for all domain objects.
 * 
 * @author Joel M. Rives
 * Mar 21, 2011
 */

@MappedSuperclass
public abstract class DomainObject  implements Serializable, IsSerializable {
	
	private static final long serialVersionUID = -9144598611145752809L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

	@Version
    @Column(name = "version")
    private Integer version;

	public Long getId() {
        return this.id;
    }

	public void setId(Long id) {
        this.id = id;
    }

	public Integer getVersion() {
        return this.version;
    }

	public void setVersion(Integer version) {
        this.version = version;
    }

}
