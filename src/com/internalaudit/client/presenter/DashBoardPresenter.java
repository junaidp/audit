package com.internalaudit.client.presenter;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.internalaudit.client.InternalAuditServiceAsync;
import com.internalaudit.client.event.MainEvent;
import com.internalaudit.client.view.DashBoardAuditJobs;
import com.internalaudit.client.view.DashBoardAuditObservations;
import com.internalaudit.client.view.DashBoardResourceManagement;
import com.internalaudit.client.view.LoadingPopup;
import com.internalaudit.shared.Employee;
import com.internalaudit.shared.Exceptions;
import com.internalaudit.shared.User;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.PasswordField;
import com.sencha.gxt.widget.core.client.form.TextField;


public class DashBoardPresenter implements Presenter 

{
	private final InternalAuditServiceAsync rpcService;
	private final HandlerManager eventBus;
	private final Display display;
	
	public interface Display 
	{
		Widget asWidget();
		Object getHtmlErrorMessage = null;
		DashBoardAuditJobs getDashBoardAuditJobs();
		DashBoardAuditObservations getDashBoardAuditObservations();
		DashBoardResourceManagement getDashBoardResourceManagement();
	}  

	public DashBoardPresenter(InternalAuditServiceAsync rpcService, HandlerManager eventBus, Employee employee, Display view) 
	{
		this.rpcService = rpcService;
		this.eventBus = eventBus;
		this.display = view;
	}

	public void go(HasWidgets container) 
	{
		container.clear();
		container.add(display.asWidget());
		bind();
	}

	private void bind() {
		
		final DecoratedPopupPanel popLoading = new DecoratedPopupPanel();
	    Label lblPopup = new Label("");
	    lblPopup.setWidth("200px");
		VerticalPanel hpn =  new VerticalPanel();
	    hpn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    hpn.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
	    Image loading = new Image("images/spinning.gif");
	    hpn.add(loading);
	    hpn.add(lblPopup);
	    popLoading.setWidget(hpn);
	    popLoading.setGlassEnabled(true);
	    popLoading.center();
	    popLoading.setStyleName("whiteBackground");
	    
		rpcService.fetchNumberofPlannedJobs(new AsyncCallback<Integer>(){

			@Override
			public void onFailure(Throwable arg0) {
				popLoading.removeFromParent();
			}

			@Override
			public void onSuccess(Integer numberOfPlannedJobs) {
				display.getDashBoardAuditJobs().getPlannedJobs().setText(numberOfPlannedJobs+"");
			}});
		
		rpcService.fetchNumberofInProgressJobs(new AsyncCallback<Integer>(){

			@Override
			public void onFailure(Throwable arg0) {
				popLoading.removeFromParent();
			}

			@Override
			public void onSuccess(Integer numberOfInProgressJobs) {
				display.getDashBoardAuditJobs().getInProgressJobs().setText(numberOfInProgressJobs+"");
			}});
		
		rpcService.fetchNumberofCompletedJobs(new AsyncCallback<Integer>(){

			@Override
			public void onFailure(Throwable arg0) {
				popLoading.removeFromParent();
			}

			@Override
			public void onSuccess(Integer numberOfCompletedJobs) {
				display.getDashBoardAuditJobs().getCompletedJobs().setText(numberOfCompletedJobs+"");
			}});
		
		rpcService.fetchJobsKickOffNextWeek(new AsyncCallback<ArrayList<String>>(){

			@Override
			public void onFailure(Throwable arg0) {
				popLoading.removeFromParent();
			}

			@Override
			public void onSuccess(ArrayList<String> kickOffNextWeek) {
				for(int i=0; i< kickOffNextWeek.size(); i++){
					Label job = new Label("("+kickOffNextWeek.get(i)+")");
					job.setStyleName("navybluebold");
					display.getDashBoardAuditJobs().getVpnlDueKickOffNextWeek().add(job);
					
				}
			}});
		
		rpcService.fetchNumberOfAuditObservations(new AsyncCallback<Integer>(){

			@Override
			public void onFailure(Throwable arg0) {
				popLoading.removeFromParent();
			}

			@Override
			public void onSuccess(Integer noOfAuditObservations) {
				display.getDashBoardAuditObservations().getNoOfAuditObservations().setText(noOfAuditObservations+"");
			}});
		
		rpcService.fetchNumberOfExceptionsInProgress(new AsyncCallback<Integer>(){

			@Override
			public void onFailure(Throwable arg0) {
				popLoading.removeFromParent();
			}

			@Override
			public void onSuccess(Integer noOfExceptionsInProgress) {
				display.getDashBoardAuditObservations().getImplementationInProgress().setText(noOfExceptionsInProgress+"");
			}});
		
		rpcService.fetchNumberOfExceptionsImplemented(new AsyncCallback<Integer>(){

			@Override
			public void onFailure(Throwable arg0) {
				popLoading.removeFromParent();
			}

			@Override
			public void onSuccess(Integer noOfExceptionsImplemented) {
				display.getDashBoardAuditObservations().getImplemented().setText(noOfExceptionsImplemented+"");
			}});
		
		rpcService.fetchNumberOfExceptionsOverdue(new AsyncCallback<Integer>(){

			@Override
			public void onFailure(Throwable arg0) {
				popLoading.removeFromParent();
			}

			@Override
			public void onSuccess(Integer noOfExceptionsOverdue) {
				display.getDashBoardAuditObservations().getOverdue().setText(noOfExceptionsOverdue+"");
			}});
		
		
		rpcService.fetchEmployeesAvilbleForNext2Weeks(new AsyncCallback<ArrayList<String>>(){

			@Override
			public void onFailure(Throwable arg0) {
				popLoading.removeFromParent();
			}

			@Override
			public void onSuccess(ArrayList<String> employeesAvilbleForNext2Weeks) {
				for(int i=0; i< employeesAvilbleForNext2Weeks.size(); i++){
					Label employee = new Label("("+employeesAvilbleForNext2Weeks.get(i)+")");
					employee.setStyleName("navybluebold");
					display.getDashBoardResourceManagement().getVpnlResources().add(employee);
					
				}
				popLoading.removeFromParent();
			}});
		
		
	}

	


}


