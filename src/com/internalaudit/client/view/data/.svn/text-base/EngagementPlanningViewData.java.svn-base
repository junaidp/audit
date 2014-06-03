package com.internalaudit.client.view.data;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.internalaudit.client.InternalAuditServiceAsync;
import com.internalaudit.client.view.AuditEngagement.ActivityObjectiveView;
import com.internalaudit.client.view.AuditEngagement.AssignmentObjectiveView;
import com.internalaudit.client.view.AuditEngagement.EngagementPlanningView;
import com.internalaudit.client.view.AuditEngagement.ProcessUnderstandingView;
import com.internalaudit.shared.AuditEngagement;

public class EngagementPlanningViewData {

	public EngagementPlanningViewData() {

	}

	InternalAuditServiceAsync rpcService;


	public void setData(InternalAuditServiceAsync rpcService) {
		this.rpcService = rpcService;
	}

	public void fetchCreatedJob(final EngagementPlanningView engagementPlanningView,	int selectedJobId) {


		rpcService.fetchAuditEngagement( selectedJobId , new AsyncCallback<AuditEngagement>() {

			@Override
			public void onFailure(Throwable arg0) {

			}

			@Override
			public void onSuccess(AuditEngagement record) {
				if(record!=null){
					
					
					engagementPlanningView.getProcess().setText( record.getProcess()==null?"": record.getProcess() );
					engagementPlanningView.getActivityObjective().setText(  record.getActivityObj()==null?"": record.getActivityObj() );
					engagementPlanningView.getAssignmentObjective().setText( record.getAssignmentObj()==null?"": record.getAssignmentObj() );		
					if(!record.getProcess().equals("") &&  !record.getActivityObj().equals("") && !record.getAssignmentObj().equals("")){
						engagementPlanningView.disableFields();
					}
				}
			}

		});

	}
}

