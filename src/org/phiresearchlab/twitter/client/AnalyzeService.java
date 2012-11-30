package org.phiresearchlab.twitter.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("services/analyzeService")
public interface AnalyzeService extends RemoteService {

	public void startAnalysis();
	
	public void stopAnalysis();
	
}
