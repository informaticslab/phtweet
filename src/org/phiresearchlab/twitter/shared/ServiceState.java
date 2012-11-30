/**
 * 
 */
package org.phiresearchlab.twitter.shared;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 *
 * @author Joel M. Rives
 * May 6, 2011
 */
public class ServiceState implements IsSerializable {

	private boolean running;
	private long captureRate;
	private List<TermState> filterTerms;
	private boolean communicationProblem;
	private Date timestamp;
	
	public ServiceState() { 
		this.communicationProblem = false;
	}
	
	public ServiceState(List<TermState> filterTerms, long captureRate, boolean running) {
		this.filterTerms = filterTerms;
		this.captureRate = captureRate;
		this.running = running;
		this.communicationProblem = false;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public long getCaptureRate() {
		return captureRate;
	}

	public void setCaptureRate(long captureRate) {
		this.captureRate = captureRate;
	}

	public boolean hasCommunicationProblem() {
		return communicationProblem;
	}

	public void setCommunicationProblem(boolean communicationProblem) {
		this.communicationProblem = communicationProblem;
	}

	public List<TermState> getFilterTerms() {
		return filterTerms;
	}

	public void setFilterTerms(List<TermState> filterTerms) {
		this.filterTerms = filterTerms;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
