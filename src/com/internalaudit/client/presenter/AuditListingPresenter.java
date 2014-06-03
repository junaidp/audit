package com.internalaudit.client.presenter;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.internalaudit.client.InternalAuditServiceAsync;
import com.internalaudit.client.view.PopupsViewWhite;
import com.internalaudit.client.view.Scheduling.JobsSchedulingView;
import com.internalaudit.client.view.Scheduling.ResourceSchedilingView;
import com.internalaudit.client.view.Scheduling.TimeLineJobsView;
import com.internalaudit.client.view.Scheduling.TimeLineResourceView;
import com.internalaudit.client.widgets.AuditScheduling;
import com.internalaudit.shared.JobCreation;
import com.internalaudit.shared.JobsOfEmployee;

public class AuditListingPresenter implements Presenter {


	private final InternalAuditServiceAsync rpcService;
	private final HandlerManager eventBus;
	private final Display display;


	public interface Display 
	{
		Widget asWidget();
		Object getHtmlErrorMessage = null;
		Button getBtnBack();
		Anchor getResourceButton();
		Anchor getJobsButton();
	}  


	public AuditListingPresenter(InternalAuditServiceAsync rpcService, HandlerManager eventBus, Display view) 
	{
		this.rpcService = rpcService;
		this.eventBus = eventBus;
		this.display = view;
	}


	@Override
	public void go(HasWidgets container) {
		container.clear();

		container.add(display.asWidget());

		display.getJobsButton().addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				JobsSchedulingView jobSchedulingView = new JobsSchedulingView();
				
				fetchJobs(jobSchedulingView);
			}});
		
		display.getResourceButton().addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				JobsSchedulingView resourceSchedulingView = new JobsSchedulingView();
				
				fetchEmployees(resourceSchedulingView);
			}});
		
		
		

	}
	
	public void fetchEmployees(final JobsSchedulingView resourceSchedulingView){
		
		rpcService.fetchEmployeesWithJobs(new AsyncCallback<ArrayList<JobsOfEmployee>>(){

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(ArrayList<JobsOfEmployee> result) {
				new PopupsViewWhite(resourceSchedulingView);
				addHeadingResource(resourceSchedulingView);
				for ( int i = 1; i< result.size(); i++)
				{
					final ResourceSchedilingView auditScheduling = new ResourceSchedilingView();
					auditScheduling.getResourceName().setText(result.get(i).getEmployee().getEmployeeName());
					
					auditScheduling.setEmployeeId(result.get(i).getEmployee().getEmployeeId());
					resourceSchedulingView.getListContainer().add(auditScheduling);
					auditScheduling.getTimeLineContainer().add(new TimeLineResourceView(result.get(i).getJobs()));
				
				}
			
			}});
	}

	private void fetchJobs(final JobsSchedulingView jobSchedulingView) {
		rpcService.fetchJobs(new AsyncCallback<ArrayList<JobCreation>>(){

			@Override
			public void onFailure(Throwable caught) {

			}

			@Override
			public void onSuccess(ArrayList<JobCreation> result) {
				new PopupsViewWhite(jobSchedulingView);
				addHeading(jobSchedulingView);
				for ( int i =0; i< result.size(); i++)
				{
					final AuditScheduling auditScheduling = new AuditScheduling();
					auditScheduling.getJobName().setText(result.get(i).getJobName());
					auditScheduling.setEstimatedWeeks(result.get(i).getEstimatedWeeks());	

					auditScheduling.setJobId(result.get(i).getJobCreationId());
					auditScheduling.getEndDate().setText(result.get(i).getEndDate());///
					auditScheduling.getStartDate().getTextBox().setText(result.get(i).getStartDate());
					setHandlers(auditScheduling);
					jobSchedulingView.getListContainer().add(auditScheduling);
					auditScheduling.getTimeLineContainer().add(new TimeLineJobsView(result.get(i).getTimeLineDates()));
				}
			}

		});
	}

	private void addHeading(JobsSchedulingView jobSchedulingView) {
		HorizontalPanel headingPanel = new HorizontalPanel();

		Label empty = new Label("");
		empty.setWidth("100px");
		headingPanel.add(empty);

		Label jobName = new Label("Job Name");
		jobName.setWidth("160px");
		headingPanel.add(jobName);

		Label startDate = new Label("Start Date");
		headingPanel.add(startDate);
		startDate.setWidth("90px");

		Label endDate = new Label("End Date");
		headingPanel.add(endDate);
		endDate.setWidth("90px");		

		String[] names  = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"}; 

		FlexTable flexHeading = new FlexTable();
		flexHeading.setCellSpacing(0);
		flexHeading.setCellPadding(0);
		flexHeading.setBorderWidth(0);
		for ( int i = 0; i < 12 ; i++)
		{
			flexHeading.getCellFormatter().setWidth(0, i, "66px");
			Label month = new Label(names[i]);
			month.setWordWrap(false);
			flexHeading.setWidget(0, i , month);
			flexHeading.setStyleName("list-heading");

			//Months with 5 weeks
			if(month.getText().equals("Mar") || month.getText().equals("May")|| month.getText().equals("Jul") || month.getText().equals("Oct")){
				flexHeading.getCellFormatter().setWidth(0, i, "86px");
			}
			if(month.getText().equals("Dec")){
				flexHeading.getCellFormatter().setWidth(0, i, "90px");
			}
		}
		headingPanel.setStyleName("list-heading");
		headingPanel.setWidth("428px");
		jobSchedulingView.getHeadingsPanel().add(headingPanel);
		jobSchedulingView.getHeadingsPanel().add(flexHeading); 


	}
	
	
	private void addHeadingResource(JobsSchedulingView jobSchedulingView) {
		HorizontalPanel headingPanel = new HorizontalPanel();

		
		Label employeeName = new Label("Employee Name");
		employeeName.setWidth("160px");
		headingPanel.add(employeeName);

		String[] names  = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"}; 

		FlexTable flexHeading = new FlexTable();
		flexHeading.setCellSpacing(0);
		flexHeading.setCellPadding(0);
		flexHeading.setBorderWidth(0);
		for ( int i = 0; i < 12 ; i++)
		{
			flexHeading.getCellFormatter().setWidth(0, i, "66px");
			Label month = new Label(names[i]);
			month.setWordWrap(false);
			flexHeading.setWidget(0, i , month);
			flexHeading.setStyleName("list-heading");

			//Months with 5 weeks
			if(month.getText().equals("Mar") || month.getText().equals("May")|| month.getText().equals("Jul") || month.getText().equals("Oct")){
				flexHeading.getCellFormatter().setWidth(0, i, "86px");
			}
			if(month.getText().equals("Dec")){
				flexHeading.getCellFormatter().setWidth(0, i, "110px");
			}
		}
		headingPanel.setStyleName("list-heading");
		headingPanel.setWidth("300px");
		jobSchedulingView.getHeadingsPanel().add(headingPanel);
		jobSchedulingView.getHeadingsPanel().add(flexHeading); 


	}

	public void setHandlers(final AuditScheduling auditScheduling){
		
		display.getBtnBack().addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				History.newItem("auditScheduling");
			}});
		


		auditScheduling.getSaveButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				rpcService.updateEndDateForJob( 
						auditScheduling.getJobId(), 
						auditScheduling.getStartDate().getTextBox().getText(),
						auditScheduling.getEndDate().getText(), 

						new AsyncCallback<JobCreation>() {

							@Override
							public void onFailure(Throwable caught) {

							}

							@Override
							public void onSuccess(JobCreation result) {
								//						History.newItem("");
								//						History.newItem("auditListing");
								auditScheduling.getTimeLineContainer().clear();
								auditScheduling.getTimeLineContainer().add(new TimeLineJobsView(result.getTimeLineDates()));

							}

						});

			}
		});



		auditScheduling.getStartDate().addValueChangeHandler(new ValueChangeHandler<Date>() {


			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {


				rpcService.getEndDate(event.getValue(), auditScheduling.getEstimatedWeeks(), new AsyncCallback<String>() {

					@Override
					public void onSuccess(String result) {
						auditScheduling.getEndDate().setText(result);

					}

					@Override
					public void onFailure(Throwable caught) {
						System.out.println("FAIL: rpcService.getEndDate in AuditListing Presenter");
					}
				});
			}
		});
	}

}