package com.internalaudit.client.view;

import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.Label;

public class LoadingPopup {
	private DecoratedPopupPanel popupLoading;

	
	public  void display() {
		popupLoading = new DecoratedPopupPanel ();
		popupLoading.setSize("100%", "100%");
		popupLoading.setWidget(new Label("Loading..."));
		popupLoading.setGlassEnabled(true);
		popupLoading.center();
	}


	public void remove(){
		popupLoading.removeFromParent();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
