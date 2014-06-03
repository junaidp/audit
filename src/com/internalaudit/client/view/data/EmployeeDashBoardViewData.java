package com.internalaudit.client.view.data;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.internalaudit.client.InternalAuditService;
import com.internalaudit.client.InternalAuditServiceAsync;
import com.internalaudit.client.view.EmployeeDashBoardView;
import com.internalaudit.shared.DashBoardDTO;

public class EmployeeDashBoardViewData {

	private InternalAuditServiceAsync rpcService = GWT.create(InternalAuditService.class);
	public void setData(final EmployeeDashBoardView employeeDashBoardView) {
		
		employeeDashBoardView.getRefresh().addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				fetchDashBoard(employeeDashBoardView);
			}});
		
		fetchDashBoard(employeeDashBoardView);
	}
	
	
	public void fetchDashBoard(final EmployeeDashBoardView employeeDashBoardView){
		rpcService.fetchDashBoard(new AsyncCallback<DashBoardDTO>(){

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(DashBoardDTO dashBoard) {
				employeeDashBoardView.populateDashBoard(dashBoard.getStrategics());
			}});
	}

}
