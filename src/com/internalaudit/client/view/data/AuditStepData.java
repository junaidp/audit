package com.internalaudit.client.view.data;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.internalaudit.client.InternalAuditService;
import com.internalaudit.client.InternalAuditServiceAsync;
import com.internalaudit.client.view.DisplayAlert;
import com.internalaudit.client.view.AuditEngagement.AuditStepView;
import com.internalaudit.client.widgets.ExceptionRow;
import com.internalaudit.shared.AuditStep;
import com.internalaudit.shared.AuditWork;
import com.internalaudit.shared.Employee;
import com.internalaudit.shared.Exceptions;
import com.internalaudit.shared.InternalAuditConstants;

public class AuditStepData {

	private InternalAuditServiceAsync rpcService = GWT.create(InternalAuditService.class);

	public AuditStepData() {


	}

	public void saveAuditStepAndException(AuditStep step, ArrayList<Exceptions> exs, final int status)
	{
		rpcService.saveAuditStepAndExceptions( step, exs, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable arg0) {

				System.out.println("Fail RPC:saveAuditStepAndException  In Audit Step Data");

			}

			@Override
			public void onSuccess(Void arg0) {
				if(status == 0){
					new DisplayAlert("Audit Step Saved");
				}
				else if(status == 1){
					new DisplayAlert("Audit Step Approved");
				}
				else if(status == 2){
					new DisplayAlert("Audit Step Rejected");
				}
			}

		});

	}

	public void getSavedAuditStep(final AuditStepView auditStepView,
			int selectedJobId, final AuditWork auditWork, final VerticalPanel exceptions, final Employee loggedInEmployee) {

		rpcService.getSavedAuditStep( selectedJobId, auditWork.getAuditWorkId(), new AsyncCallback<AuditStep>() {

			@Override
			public void onFailure(Throwable arg0) {

				Window.alert("Fail getting saved audit step");

			}

			@Override
			public void onSuccess(AuditStep auditStep) {
				if(auditStep.getAuditStepId()!=0){
					////CHECK WHO IS LOGGEDIN

					if(auditWork.getJobCreationId().getAuditHead() == loggedInEmployee.getEmployeeId()){
						auditStepView.supervisorView();
						if(auditStep.getStatus() == InternalAuditConstants.APPROVED || auditStep.getStatus() == InternalAuditConstants.REJECTED){
							auditStepView.getApprove().setEnabled(false);
							auditStepView.getReject().setEnabled(false);
						}

					}else{
						auditStepView.getApprovalButtonsPanel().setVisible(false);
						auditStepView.getSave().setVisible(true);
						if(auditStep.getStatus() == InternalAuditConstants.APPROVED || auditStep.getStatus() == InternalAuditConstants.INITIATED){
							auditStepView.disableFields();
							auditStepView.getSave().setEnabled(false);
						}
					}

					///

					auditStepView.getPerformance().setText( auditStep.getProceducePerformance() );
					auditStepView.getPopulation().setText( auditStep.getPopulation() );
					auditStepView.getSample().setText( auditStep.getSampleSelected() );
					auditStepView.getSelection().setText( auditStep.getSelectionBasis() );
					//this is how I set id, when needed for update
					auditStepView.getAuditStepId().setText( String.valueOf( auditStep.getAuditStepId())) ;

					if ( "Satisfactory".equals( auditStep.getConclusion() ) )
						auditStepView.getConclusion().setSelectedIndex(0);
					else{
						auditStepView.getConclusion().setSelectedIndex(1);
					}
					displayExceptions(exceptions, auditStep.getExceptions());

				}
			}
		});

	}

	public void getSavedExceptions(final VerticalPanel exceptions, int selectedJobId) {

		rpcService.getSavedExceptions( selectedJobId, new AsyncCallback<ArrayList<Exceptions>>() {

			@Override
			public void onFailure(Throwable arg0) {
				//error with fail
				Window.alert("Fail getting saved ex");

			}

			@Override
			public void onSuccess(ArrayList<Exceptions> arg0) {

				//				displayExceptions(exceptions, arg0);
			}



		});

	}

	private void displayExceptions(final VerticalPanel exceptions,	ArrayList<Exceptions> arg0) {
		exceptions.clear();

		for ( Exceptions e : arg0)
		{

			ExceptionRow row = new ExceptionRow();//employee
			row.getExId().setText( String.valueOf(e.getExceptionId()));
			row.getException().setText( String.valueOf(e.getDetail()));
			exceptions.add(row);
		}
	}




}
