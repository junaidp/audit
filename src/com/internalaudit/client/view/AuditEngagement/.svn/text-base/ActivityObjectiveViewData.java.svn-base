package com.internalaudit.client.view.AuditEngagement;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.internalaudit.client.InternalAuditServiceAsync;
import com.internalaudit.shared.AuditEngagement;
import com.internalaudit.shared.JobCreation;

public class ActivityObjectiveViewData {
	
	public void setData(ActivityObjectiveView activityObjectiveView,
			InternalAuditServiceAsync rpcService, int selectedJobId) {
		
		
		activityObjectiveHandlers(activityObjectiveView, rpcService, selectedJobId);
		
	}

	private void activityObjectiveHandlers(
			final ActivityObjectiveView activityObjectiveView,
			final InternalAuditServiceAsync rpcService,
			final int selectedJobId) 
	{
		activityObjectiveView.getSubmit().addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				//Window.alert("Saving"+activityObjectiveView.getActivityObjective().getText()+" Data to JobID:"+selectedJobId+" via rpc from here: ");
				
				String toSave = activityObjectiveView.getActivityObjective().getText();
				int jobCreationId = selectedJobId;
				
				
				AuditEngagement e = new AuditEngagement();
				e.setActivityObj(toSave);
				
				JobCreation j = new JobCreation();
				j.setJobCreationId(jobCreationId);
				e.setJobCreation(j);
				
				rpcService.updateAuditEngagement(e, "activityObj", new AsyncCallback<Boolean>() {
					
					@Override
					public void onSuccess(Boolean arg0) {
						Window.alert("success");
					}
					
					@Override
					public void onFailure(Throwable arg0) {
						Window.alert("fail");
						
					}
				});
				
				
				// call rpc from here.. and save this data to this selectedJobId.. in our new table under column  assignmentObjective
			}});

	}
		
}
