package org.phiresearchlab.twitter.server;

import twitter4j.FilterQuery;

public interface PersistingStatusListener {

	public void startCapturing(FilterQuery filterQuery);
	
	public void stopCapturing();
	
}
