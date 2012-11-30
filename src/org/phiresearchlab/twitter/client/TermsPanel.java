/**
 * 
 */
package org.phiresearchlab.twitter.client;

import java.util.List;

import org.phiresearchlab.twitter.domain.Category;
import org.phiresearchlab.twitter.domain.FilterTerm;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *
 * @author Joel M. Rives
 * Jul 18, 2011
 */
public class TermsPanel extends PopupPanel {
    private static final String EMPTY_TEXT = "Enter term";
    private static final String NEW_CATEGORY = "New category...";
    
    private TwitterServiceAsync twitterService = GWT.create(TwitterService.class);
        
    private PHTweet controller;
    private ListBox categorySelector = new ListBox();
    private Grid termsPanel = new Grid(13, 4);
    private Button okButton = new Button("OK");
    
    private Category currentCategory;
    private Category savedCategory;
	private List<Category> categories;
	private ColorMapper colorMap = ColorMapper.getInstance();
	private boolean modified = false;
	private boolean allEnabled = true;

	public TermsPanel(PHTweet phtweet) {
		super(false);
		this.controller = phtweet;
		
        Panel workPanel = createWorkPanel();
		AbsolutePanel mainPanel = new AbsolutePanel();
		mainPanel.add(workPanel);
		
		setWidget(mainPanel);
		this.setStyleName("termsPanel");
		
		fetchCategories();
	}
	
    @Override
    public void hide()
    {
    	if (modified) {
    		if (Window.confirm("There are changes to the category. Would you like to save them?"))
    			updateCurrentCategory();
    	}
        controller.positionTermTab(0, 120);
        super.hide();
    }

    public void show(final int top)
    {
        fetchCategories();
        setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
              setPopupPosition(0, top);
            }
        });
    }
    
    private void cancel() {
    	modified = false;
    	currentCategory = savedCategory;
    	okButton.setEnabled(false);
    	hide();
    }

    private Panel createAddRemovePanel() {
		ImageButton addRemoveButton = new ImageButton("images/terms/add_remove_category.png",
				                                      "images/terms/add_remove_category.png",
				                                      "images/terms/add_remove_category_hover.png");
		
		HorizontalPanel panel = new HorizontalPanel();
		panel.setSpacing(4);
		panel.setStyleName("termsAddRemovePanel");
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.add(addRemoveButton);
		panel.add(new Label("Add/remove category"));

		return panel;
	}
    
	private Panel createButtonPanel() {
		Button cancelButton = new Button("Cancel");
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				cancel();	
			}			
		});
		
		okButton.setEnabled(false);
		okButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
			    if (modified)
			        updateCurrentCategory();
			    modified = false;
			    okButton.setEnabled(false);
				hide();	
			}			
		});
		
		HorizontalPanel panel = new HorizontalPanel();
		panel.setStyleName("termsButtonPanel");
		panel.add(cancelButton);
		panel.add(okButton);
		panel.setSpacing(6);
		
		return panel;
	}
	
	private Panel createCategorySelectPanel() {
	    Label categorySelectLabel = new Label("Term Category:");
	    categorySelectLabel.setStyleName("categorySelectLabel");
	    
	    categorySelector.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event)
            {
                int index = categorySelector.getSelectedIndex();
                String name = categorySelector.getItemText(index);
                
                if (name.equals(currentCategory.getName()))
                    return;
                
                if (modified) {
                    if (Window.confirm("The current category has been modified." +
                                       "Would you like to save the changes?"))
                        updateCurrentCategory();
                }
                
//                if (name.equals(NEW_CATEGORY)) {
//                    String newName = Window.prompt("Enter name for new category", "");
//                    if (newName.length() == 0)
//                        return;
//                    Category category = new Category(newName, new ArrayList<FilterTerm>());
//                    categories.add(category);
//                    populateCategorySelector(categories);
//                    setCategory(category);
//                    return;
//                }
                
                for (Category category: categories) {
                    if (category.getName().equals(name)) {
                        setCategory(category);
                    }
                }
            }	        
	    });
	    
	    HorizontalPanel panel = new HorizontalPanel();
	    panel.setStyleName("categorySelectPanel");
	    panel.setSpacing(6);
        panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        panel.add(categorySelectLabel);
        panel.add(categorySelector);
        
	    return panel;
	}
	
	private void createTermPanel(final int row, String color) {
	    Image image = new Image("images/terms/" + color + ".png");
	    image.setTitle(color);
	    
	    final TextBox textEntry = new TextBox();
	    textEntry.setVisibleLength(32);
	    textEntry.setMaxLength(255);    
        textEntry.setStyleName("emptyTermEntry");
        textEntry.setText(EMPTY_TEXT);
        
        textEntry.addBlurHandler(new BlurHandler() {
            public void onBlur(BlurEvent event)
            {
                String text = textEntry.getText();
                if (null == text || text.length() == 0) {
                    text = EMPTY_TEXT;
                    textEntry.setText(EMPTY_TEXT);
                    textEntry.setStyleName("emptyTermEntry");
                }
                if (!text.equals(textEntry.getTitle())) {
                    rowModified(row);
                }
            }            
        });
        
        textEntry.addFocusHandler(new FocusHandler() {
            public void onFocus(FocusEvent event)
            {
                String text = textEntry.getText();
                textEntry.setTitle(text);
                if (text.equals(EMPTY_TEXT)) {
                    textEntry.setText("");
                    textEntry.setStyleName("termEntry");                    
                }
                textEntry.selectAll();
            }           
        });
        
        textEntry.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent event)
            {
                String text = textEntry.getText();
                if (!text.equals(textEntry.getTitle())) {
                    rowModified(row);
                }
            }            
        });
 
        textEntry.addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event)
            {
                // What to do here???
            }            
        });
        
        CheckBox toggle = new CheckBox();
        toggle.setVisible(false);
        toggle.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event)
            {
                boolean checked = ((CheckBox) event.getSource()).getValue();
                String term = textEntry.getText();
                for (FilterTerm filterTerm: currentCategory.getFilterTerms())
                    if (filterTerm.getName().equals(term)) {
                        filterTerm.setEnabled(checked);
                        textEntry.setStyleName(checked ? "termEntry" : "emptyTermEntry");
                    }
                modified = true;
                okButton.setEnabled(true);
            }            
        });
 	    
	    final Image deleteButton = new Image("images/terms/delete_term.png");
	    deleteButton.setVisible(false);
	    deleteButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                textEntry.setStyleName("emptyTermEntry");
                textEntry.setText("Enter term");
                deleteButton.setVisible(false);
                modified = true;
                okButton.setEnabled(true);
            }           
	    });
	    
	    termsPanel.setWidget(row, 0, image);
	    termsPanel.setWidget(row, 1, textEntry);
	    termsPanel.getCellFormatter().setStyleName(row, 1, "termTextColumn");
	    termsPanel.setWidget(row, 2, toggle);
	    termsPanel.getCellFormatter().setStyleName(row, 2, "termEnableColumn");
	    termsPanel.setWidget(row, 3, deleteButton);
	}
	
	private Panel createTermsPanel() {
        createTermPanel(1, "teal");
        createTermPanel(2, "pink");
        createTermPanel(3, "gold");
        createTermPanel(4, "yellow_green");
        createTermPanel(5, "blue");
        createTermPanel(6, "orange");
        createTermPanel(7, "dk_green");
        createTermPanel(8, "red");
        createTermPanel(9, "gray");
        createTermPanel(10, "purple");
        createTermPanel(11, "brown");
        createTermPanel(12, "bright_green");
		
        Label show = new Label("Show");
		show.setStyleName("allTermToggle");
		show.addClickHandler(new ClickHandler(){
            public void onClick(ClickEvent event) {
            	allEnabled = !allEnabled;
        		for (int i = 1; i < 13; i++) {
        			TextBox textEntry = (TextBox) termsPanel.getWidget(i, 1);
        			CheckBox toggle = (CheckBox) termsPanel.getWidget(i, 2);
        			String term = textEntry.getText();
                    for (FilterTerm filterTerm: currentCategory.getFilterTerms())
                        if (filterTerm.getName().equals(term)) {
                            filterTerm.setEnabled(allEnabled);
                            textEntry.setStyleName(allEnabled ? "termEntry" : "emptyTermEntry");
                        }
        			toggle.setValue(allEnabled);
        		}
        		modified = true;
        		okButton.setEnabled(true);
            }			
		});
		
		termsPanel.setWidget(0, 2, show);

        return termsPanel;
	}
	
	private Panel createWorkPanel() {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("termsWorkPanel");
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		Panel addRemovePanel = createAddRemovePanel();
		panel.add(addRemovePanel);
		panel.setCellHorizontalAlignment(addRemovePanel, HasHorizontalAlignment.ALIGN_RIGHT);
		panel.add(createCategorySelectPanel());
		panel.add(createTermsPanel());
		panel.add(createButtonPanel());
		
		return panel;
	}
	
    private void fetchCategories() {
        twitterService.getAllCategories(new AsyncCallback<List<Category>>() {
            public void onFailure(Throwable caught)
            {
                Window.alert("Failed to get the list of term categories: " + caught.getMessage());   
            }

            public void onSuccess(List<Category> result)
            {
                categories = result;
                populateCategorySelector(categories);
                setCategory(null == currentCategory ? categories.get(0) : currentCategory);
            }           
        });
    }
    
	private void populateCategorySelector(List<Category> list) {
	    categorySelector.clear();
	    
	    for (Category category: list)
	        categorySelector.addItem(category.getName());
	    
//	    categorySelector.addItem(NEW_CATEGORY);
	    categorySelector.setVisibleItemCount(1);
	    categorySelector.setSelectedIndex(0);
	}
	
    private void rowModified(int row) {
        modified = true;
        okButton.setEnabled(true);
        CheckBox toggle = (CheckBox) termsPanel.getWidget(row, 2);
        toggle.setVisible(true);
        toggle.setValue(true);
        Image deleteButton = (Image) termsPanel.getWidget(row, 3);
        deleteButton.setVisible(true);
    }
    
	private void setCategory(Category category) {
	    if (null != currentCategory && category.getName().equals(currentCategory.getName()))
	        return;
	    
	    if (null != currentCategory) {
	        modified = true;
	        okButton.setEnabled(true);
	    }
	    
	    savedCategory = currentCategory;
	    currentCategory = category;
	    
	    for (int i = 0; i < categorySelector.getItemCount(); i++) 
	        if (category.getName().equals(categorySelector.getItemText(i)))
	            categorySelector.setItemSelected(i, true);
	    
	    List<FilterTerm> list = category.getFilterTerms();
	    for (int i = 0; i < 12; i++) {
	        FilterTerm term = i < list.size() ? list.get(i) : null;
	        setTerm(i+1, term);
	    }
	}
	
	private void setTerm(int row, FilterTerm term) {
	    TextBox textEntry = (TextBox) termsPanel.getWidget(row, 1);
	    CheckBox toggle = (CheckBox) termsPanel.getWidget(row, 2);
	    Image deleteButton = (Image) termsPanel.getWidget(row, 3);
	    
	    if (null == term || term.getName().length() == 0) {
	        textEntry.setStyleName("emptyTermEntry");
	        textEntry.setText("Enter term");
	        toggle.setVisible(false);
	        deleteButton.setVisible(false);
	    } else {
            textEntry.setStyleName(term.getEnabled() ? "termEntry" : "emptyTermEntry");
            textEntry.setText(term.getName());
            toggle.setVisible(true);
            toggle.setValue(term.getEnabled());
            deleteButton.setVisible(true);
	    }
	}
	
	private void updateCurrentCategory() {
	    List<FilterTerm> filterTerms = currentCategory.getFilterTerms();
	    filterTerms.clear();
	    
	    for (int i = 1; i < 13; i++) {
            Image image = (Image) termsPanel.getWidget(i, 0);
            TextBox textEntry = (TextBox) termsPanel.getWidget(i, 1);
            CheckBox toggle = (CheckBox) termsPanel.getWidget(i, 2);
            String term = textEntry.getText();
            if (term.equals(EMPTY_TEXT))
                term = "";
//            if (term.length() > 0 && !term.equals(textEntry.getTitle()))
//                toggle.setValue(true);
            String color = colorMap.getHex(image.getTitle());
            FilterTerm filterTerm = new FilterTerm(term, color);
            filterTerm.setEnabled(toggle.getValue());
            filterTerms.add(filterTerm);
	    }
	    
	    currentCategory.setFilterTerms(filterTerms);
	    
        twitterService.updateCategory(currentCategory, new AsyncCallback<Category>() {
            public void onFailure(Throwable caught)
            {
                Window.alert("Failed to update category " + currentCategory.getName() + ": " + 
                                caught.getMessage());                    
            }

            public void onSuccess(Category result)
            {
                boolean found = false;
            	modified = false;
                for (Category category: categories) {
                    if (category.getName().equals(result.getName())) {
                        category.setFilterTerms(result.getFilterTerms());
                        found = true;
                    }
                }
                if (!found)
                    categories.add(result);
                currentCategory = null;
                setCategory(result);
                controller.setCurrentCategory(currentCategory);
            }	            
        });
	}
	
}
