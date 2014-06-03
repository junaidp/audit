package com.internalaudit.client.view.AuditEngagement;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.internalaudit.client.InternalAuditServiceAsync;
import com.internalaudit.client.view.DisplayAlert;
import com.internalaudit.shared.AuditEngagement;

public class AuditNotificationViewData {

	private AuditNotificationView auditNotificationView;
	private InternalAuditServiceAsync rpcService;
	private AuditEngagement selectedAuditEngagement;

	public AuditNotificationViewData(){

	}

	public void setData(AuditNotificationView auditNotificationView,
			InternalAuditServiceAsync rpcService, AuditEngagement selectedAuditEngagement) {

		this.auditNotificationView = auditNotificationView;
		this.rpcService =  rpcService;
		this.selectedAuditEngagement = selectedAuditEngagement;
		if(selectedAuditEngagement!=null && !selectedAuditEngagement.getTo().equals("")){
		displaySavedNotification();
		}

	}


	private void displaySavedNotification() {
		auditNotificationView.getCc().setText(selectedAuditEngagement.getCc());
		auditNotificationView.getTo().setText(selectedAuditEngagement.getTo());
		auditNotificationView.getMessage().setText(selectedAuditEngagement.getAuditNotification());
		auditNotificationView.disableFields();
		
	}

	public void sendMessage() {

		auditNotificationView.disableFields();
		rpcService.saveAuditNotification(selectedAuditEngagement.getAuditEngId(), auditNotificationView.getMessage().getText(), auditNotificationView.getTo().getText(), auditNotificationView.getCc().getText(), new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable arg0) {
				System.out.println("Audit Notification sending FAILED.In AuditNotificatioViewData");
			}

			@Override
			public void onSuccess(String result) {
				new DisplayAlert("Notification Sent");

			}					
		});

	}

}
