package com.internalaudit.client.view.data;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.internalaudit.client.InternalAuditService;
import com.internalaudit.client.InternalAuditServiceAsync;
import com.internalaudit.client.view.AuditnTrail;
import com.internalaudit.shared.StrategicAudit;

public class AuditTrailViewData {

	private InternalAuditServiceAsync rpcService = GWT.create(InternalAuditService.class);
	public void setData(final AuditnTrail auditTrailView) {
		
		auditTrailView.getRefresh().addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				fetchStrategicAudit(auditTrailView);
			}});
		
		fetchStrategicAudit(auditTrailView);
	}
	
	
	public void fetchStrategicAudit(final AuditnTrail auditTrailView){
		rpcService.fetchStrategicAudit(new AsyncCallback<ArrayList<StrategicAudit>>(){

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(ArrayList<StrategicAudit> strategicAudits) {
				auditTrailView.populateStrategicAudit(strategicAudits);
			}});
	}

}
