package org.phiresearchlab.twitter.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AnalyzeServiceAsync {
	
	void startAnalysis(AsyncCallback<Void> callback);
	
	void stopAnalysis(AsyncCallback<Void> callback);

}
