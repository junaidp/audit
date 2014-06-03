package com.internalaudit.client.presenter;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.internalaudit.client.InternalAuditServiceAsync;
import com.internalaudit.client.event.JobCreationEvent;
import com.internalaudit.client.event.JobTimeEstimationEvent;
import com.internalaudit.shared.StrategicDTO;

public class JobListingPresenter implements Presenter {

	private final InternalAuditServiceAsync rpcService;
	private final HandlerManager eventBus;
	private final Display display;	

	public interface Display 
	{
		Widget asWidget();
		Object getHtmlErrorMessage = null;
		VerticalPanel getJobListContainer();
		String getCallingFrom();
	}
	
	public JobListingPresenter(InternalAuditServiceAsync rpcService, HandlerManager eventBus, Display view) 
	{
		this.rpcService = rpcService;
		this.eventBus = eventBus;
		this.display = view;
		
		fetchTextForJobLinks();
	}



	private void fetchTextForJobLinks() {
		
		HashMap<String,String> hm = new HashMap<String,String>();
		
		final String caller = display.getCallingFrom();
		
		rpcService.fetchSchedulingStrategic(hm, new AsyncCallback<ArrayList<StrategicDTO>>() {
			
			@Override
			public void onSuccess(final ArrayList<StrategicDTO> result) {
				
				//now we have links. gotta add to view
				Anchor jobLink;
				for( int i = 0; i < result.size(); ++i )
				{
					final StrategicDTO dto = result.get(i);
					
					jobLink = new Anchor();
					jobLink.setHeight("50px");
					jobLink.setStyleName("jobListingText");
					jobLink.setText(result.get(i).getStrategicObjective());
					
										
					jobLink.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							
							if ( caller == "jobTimeEst" )
							{ 
								
								eventBus.fireEvent( new JobTimeEstimationEvent(dto));
							}
							
							if ( caller == "jobCreation" )
							{
								eventBus.fireEvent( new JobCreationEvent(dto));
							}
						}
					});
					
					display.getJobListContainer().add(jobLink);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
		



	public void go(HasWidgets container) 
	{
		container.clear();
		container.add(display.asWidget());
		bind();
	}



	private void bind() {
		// TODO Auto-generated method stub
		
	}
}
