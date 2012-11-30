/**
 * 
 */
package org.phiresearchlab.twitter.client;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.phiresearchlab.twitter.domain.Category;
import org.phiresearchlab.twitter.domain.FilterTerm;
import org.phiresearchlab.twitter.shared.Period;
import org.phiresearchlab.twitter.shared.TermReport;
import org.phiresearchlab.twitter.shared.TermState;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *
 * @author Joel M. Rives
 * May 4, 2011
 */
public class PHTweet implements EntryPoint {
    
    private static final String DEFAULT_CATEGORY_NAME = "General";
    private static final int TERM_TAB_TOP = 120;
	
	private enum ViewType { BubbleChart, Graph, Relationship };
    
	private TwitterServiceAsync twitterService = GWT.create(TwitterService.class);
	
	private ToolTipPanel toolTip = ToolTipPanel.getInstance();
	
	private Panel mainPanel;
	private HeaderPanel headerPanel;
	private HorizontalPanel centerPanel;
	private BubblePanel bubblePanel;
	private LineGraphPanel graphPanel;
	private RelationshipPanel relationshipPanel;
	private FooterPanel footerPanel = new FooterPanel();
	private Image termTabImage = new Image("images/terms/terms_tab.png");
	private TermsPanel termsPanel;
	private AbsolutePanel absolutePanel;
	private Image graphLabel;
	
	private Date currentTimestamp;
	private boolean viewByHour = true;
	private boolean viewByAmPm = false;
	private ViewType viewType = ViewType.BubbleChart;
	private Category currentCategory;
	private Category defaultCategory;
	
	@Override
	public void onModuleLoad() {
		mainPanel = createMainPanel();
		absolutePanel = new AbsolutePanel();
		absolutePanel.add(mainPanel);
		absolutePanel.add(termTabImage);
		absolutePanel.setWidgetPosition(termTabImage, 0, TERM_TAB_TOP);
		RootPanel.get().clear();
		RootPanel.get().add(absolutePanel);
		termsPanel = new TermsPanel(this);

		Window.enableScrolling(true);
				
		twitterService.getCategory(DEFAULT_CATEGORY_NAME, new AsyncCallback<Category>() {
            public void onFailure(Throwable caught)
            {
                Window.alert("Failed to get the default category of terms. Unable to proceed.");
            }

            public void onSuccess(Category category)
            {
                defaultCategory = category;
                home();
            }
		    
		});
	}
	
	Date getCurrentTimestamp() {
		return this.currentTimestamp;
	}
	
	@SuppressWarnings("deprecation")
	void home() {
		viewByHour = true;
		viewType = ViewType.BubbleChart;
		currentCategory = defaultCategory;
		Date now = new Date();
		this.currentTimestamp = new Date(now.getYear(), now.getMonth(), now.getDate(), now.getHours(), 0);
		selectBubbleChart();
	}
	
	void launchHelp() {
		notImplemented("Help");
	}
	
	void launchSettings() {
		notImplemented("Settings");
	}
	
	void positionTermTab(int x, int y) {
	    absolutePanel.setWidgetPosition(termTabImage, x, y);
	}
	
	void selectBubbleChart() {
		viewType = ViewType.BubbleChart;
        viewByAmPm = false;
		centerPanel.clear();
        centerPanel.setStyleName("bubbleCentralPanel");
        termTabImage.setVisible(true);
        toolTip.hide();
		
		AbsolutePanel filler = new AbsolutePanel();
		filler.setWidth("40px");

        int width = Window.getClientWidth() - 82;
        int height = Window.getClientHeight() - headerPanel.getOffsetHeight() - footerPanel.getOffsetHeight() - 22;
        bubblePanel = new BubblePanel(this, width, height);
		
//		centerPanel.add(filler);
        centerPanel.add(bubblePanel);
        
        update();
	}
	
	void selectGraph() {
		viewType = ViewType.Graph;
		viewByAmPm = true;
		centerPanel.clear();
		centerPanel.setStyleName("graphCentralPanel");
        toolTip.hide();
		
        AbsolutePanel filler = new AbsolutePanel();
        filler.setWidth("10px");
        
        centerPanel.add(filler);

        graphLabel = new Image(viewByHour ? "images/graph/tweets_per_hour.png" : "images/graph/tweets_per_day.png");
        graphLabel.setStyleName("graphLabel");
		centerPanel.add(graphLabel);
		centerPanel.setCellVerticalAlignment(graphLabel, HasVerticalAlignment.ALIGN_MIDDLE);
		centerPanel.setCellHorizontalAlignment(graphLabel, HasHorizontalAlignment.ALIGN_CENTER);
		
        int width = Window.getClientWidth() - graphLabel.getOffsetWidth() - 2;
        int height = Window.getClientHeight() - headerPanel.getOffsetHeight() - footerPanel.getOffsetHeight() - 2;
        graphPanel = new LineGraphPanel(width, height);
        centerPanel.add(graphPanel);
        
        update();
	}
	
	void selectRelationship() {
		viewType = ViewType.Relationship;
		viewByAmPm = false;
		centerPanel.clear();
		centerPanel.setStyleName("relationshipCentralPanel");
        toolTip.hide();
		
        int width = Window.getClientWidth() - 2;
        int height = Window.getClientHeight() - headerPanel.getOffsetHeight() - footerPanel.getOffsetHeight() - 2;
		relationshipPanel = new RelationshipPanel(width, height);
		centerPanel.add(relationshipPanel);
        
        update();
	}
	
	void setCurrentCategory(Category category) {
	    currentCategory = category;
	    update();
	}
	
	@SuppressWarnings("deprecation")
	void setCurrentTimestamp(Date date) {
		this.currentTimestamp = new Date(date.getYear(), date.getMonth(), date.getDate(), date.getHours(), 0);
		update();
	}
	
	void setViewByHour(boolean flag) {
		this.viewByHour = flag;
		
		if (viewType == ViewType.Graph)
	        graphLabel.setUrl(viewByHour ? "images/graph/tweets_per_hour.png" : "images/graph/tweets_per_day.png");

		update();
	}
	
	boolean viewByAmPm() {
	    return this.viewByAmPm;
	}
	
	boolean viewByHour() {
		return this.viewByHour;
	}
		
	private Panel createMainPanel() {
		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		mainPanel.setStyleName("mainPanel");
		mainPanel.setHeight(Window.getClientHeight() + "px");

		headerPanel = new HeaderPanel(this);
		mainPanel.add(headerPanel);

		termTabImage.setStyleName("tabImage");
		termTabImage.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event)
            {
            	if (termsPanel.isShowing()) {
            		termsPanel.hide();
            		positionTermTab(0, termTabImage.getAbsoluteTop());
            	} else {
            		termsPanel.show(termTabImage.getAbsoluteTop());
            		positionTermTab(termsPanel.getOffsetWidth(), termTabImage.getAbsoluteTop());
            	}
            }	    
		});
				
		centerPanel = new HorizontalPanel();
		
		mainPanel.add(centerPanel);		
		mainPanel.add(footerPanel);

		return mainPanel;
	}
	
	private void notImplemented(String name) {
		Window.alert("The " + name + " feature is in development.");
	}
	
	private void update() {
	    if (viewType == ViewType.BubbleChart)
	        updateBubble();
	    else if (viewType == ViewType.Graph)
	        updateGraph();
	    else if (viewType == ViewType.Relationship)
	        updateRelationship();
	}
	
    private void updateBubble() {
    	bubblePanel.clear();
    	bubblePanel.showMessage("Loading...");
    	Period period = viewByHour ? Period.Hour : Period.Day;
        twitterService.getTermStates(currentCategory.getName(), currentTimestamp, period, new AsyncCallback<List<TermState>>() {
            public void onFailure(Throwable caught) {
                Window.alert("Failed to obtain data from server: " + caught.getMessage());
            }

            public void onSuccess(List<TermState> states) {
                int width = Window.getClientWidth() - termTabImage.getOffsetWidth() - 6;
                int height = Window.getClientHeight() - headerPanel.getOffsetHeight() - footerPanel.getOffsetHeight() - 6;
                bubblePanel.show(states, currentTimestamp, width, height);
            }           
        });
    }
    
    private static final int ONE_HOUR = 1000 * 60 * 60;
    private static final int ONE_DAY = ONE_HOUR * 24;

    private int queriesSent = 0;
    private int queriesReceived = 0;
    private int queriesFailed = 0;
    
    private void updateGraph() {
        graphPanel.clear();
        graphPanel.showMessage("Loading...");
        Period period = viewByHour ? Period.Hour : Period.Day;
        Date start = null;
        
        if (viewByHour) {
        	int hours = currentTimestamp.getHours();
        	int offset = hours < 12 ? (hours + 1) * ONE_HOUR : (hours - 11) * ONE_HOUR; 
        	start = new Date(currentTimestamp.getTime() - offset);
        } else {
        	start = new Date(currentTimestamp.getTime() - 13 * ONE_DAY);
        }
       
        final List<TermReport> reports = new ArrayList<TermReport>();
        queriesSent = 0;
        queriesReceived = 0;
        queriesFailed = 0;
        final int width = Window.getClientWidth() - graphLabel.getOffsetWidth() - 2;
        final int height = Window.getClientHeight() - headerPanel.getOffsetHeight() - footerPanel.getOffsetHeight() - 2;
        for (int index = 0; index < currentCategory.getFilterTerms().size(); index++) {
            FilterTerm filterTerm = currentCategory.getFilterTerms().get(index);
            if (filterTerm.getEnabled() == true && filterTerm.getName().trim().length() > 0) {
                queriesSent++;
                twitterService.getTermReport(currentCategory.getName(), filterTerm.getName(), start, period, 14, 
                    new AsyncCallback<TermReport>() {
                        public void onFailure(Throwable caught) {
                          //  Window.alert("Failed to obtain data from server: " + caught.getMessage());
                            queriesReceived++;
                            queriesFailed++;
                            if (queriesReceived == queriesSent) {
                                graphPanel.show(currentCategory, reports, currentTimestamp, width, height);
                                Window.alert(queriesFailed + " filter term queries failed");
                            }
                        }
            
                        public void onSuccess(TermReport report) {
                            reports.add(report);
                            queriesReceived++;
                            if (queriesReceived == queriesSent)
                                graphPanel.show(currentCategory, reports, currentTimestamp, width, height);
                        }
                    });
            }
        }
    }
    
    private void updateRelationship() {
        relationshipPanel.clear();
        relationshipPanel.showMessage("Loading...");
        Period period = viewByHour ? Period.Hour : Period.Day;
        twitterService.getTermAssociations(currentCategory.getName(), currentTimestamp, period, new AsyncCallback<List<TermReport>>() {
			public void onFailure(Throwable caught) {
                Window.alert("Failed to obtain data from server: " + caught.getMessage());
			}

			public void onSuccess(List<TermReport> reports) {
		        int width = Window.getClientWidth() - 2;
		        int height = Window.getClientHeight() - headerPanel.getOffsetHeight() - footerPanel.getOffsetHeight() - 2;
		        relationshipPanel.show(reports, width, height);
			}
        });
    }
    
}
