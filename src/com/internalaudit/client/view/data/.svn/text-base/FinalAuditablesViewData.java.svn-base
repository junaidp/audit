package com.internalaudit.client.view.data;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.internalaudit.client.InternalAuditService;
import com.internalaudit.client.InternalAuditServiceAsync;
import com.internalaudit.client.view.FinalAuditablesView;
import com.internalaudit.shared.Strategic;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.event.BeforeExpandEvent;
import com.sencha.gxt.widget.core.client.event.BeforeExpandEvent.BeforeExpandHandler;

public class FinalAuditablesViewData {
	
	private InternalAuditServiceAsync rpcService = GWT.create(InternalAuditService.class);
	
	
	public void setData(ContentPanel cp, final FinalAuditablesView finalAuditablesView, VerticalPanel vpnlFinalAuditable){
		
		cp.addBeforeExpandHandler(new BeforeExpandHandler(){

			@Override
			public void onBeforeExpand(BeforeExpandEvent event) {
				fetchFinalAuditables(finalAuditablesView);
			}});
		
		
	}
	
	public void fetchFinalAuditables(final FinalAuditablesView finalAuditablesView){
		rpcService.fetchFinalAuditables(new AsyncCallback<ArrayList<Strategic>>(){

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Fetch Final Auditables failed");
			}

			@Override
			public void onSuccess(final ArrayList<Strategic> result) {
				finalAuditablesView.getAreas().clear();
				HorizontalPanel hpnlHeading = new HorizontalPanel();
				Label lblUnitHeading = new Label("Auditable Unit");
				Label lblObjHeading = new Label("Objective");
				lblUnitHeading.setWidth("500px");
				lblObjHeading.setWidth("500px");
				hpnlHeading.add(lblUnitHeading);
				hpnlHeading.add(lblObjHeading);
				
				
				hpnlHeading.setStyleName("blue");
				hpnlHeading.setStyleName("blue");
				finalAuditablesView.getAreas().add(hpnlHeading);
				for(int i=0; i< result.size(); i++){
					Label lblObjective = new Label(result.get(i).getStrategicObjective());
					Label lblUnit = new Label(result.get(i).getAuditableUnit());
					lblObjective.setWidth("500px");
				
					lblUnit.setWidth("500px");
					HorizontalPanel hpnlMain = new HorizontalPanel();
					hpnlMain.add(lblUnit);
					hpnlMain.add(lblObjective);
					finalAuditablesView.getAreas().add(hpnlMain);
					hpnlMain.setStyleName("form-row");
				}
				
			}
			
			});
	}

}
