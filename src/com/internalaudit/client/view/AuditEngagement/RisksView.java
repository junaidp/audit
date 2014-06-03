package com.internalaudit.client.view.AuditEngagement;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.rpc.client.RpcService;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.internalaudit.client.InternalAuditServiceAsync;
import com.internalaudit.client.view.DisplayAlert;
import com.internalaudit.client.widgets.RiskRow;
import com.internalaudit.shared.JobCreation;
import com.internalaudit.shared.Risk;

public class RisksView extends Composite {

	private static RisksViewUiBinder uiBinder = GWT
			.create(RisksViewUiBinder.class);

	private InternalAuditServiceAsync rpcService;

	
	@UiField
	Button saveRisks;

	@UiField
	Button addMore;

	@UiField
	VerticalPanel riskRows;

	private int auditEngId;

	interface RisksViewUiBinder extends UiBinder<Widget, RisksView> {
	}

	public RisksView(final int auditEngId,
			final InternalAuditServiceAsync rpcService) {
		initWidget(uiBinder.createAndBindUi(this));

		this.rpcService = rpcService;
		this.auditEngId = auditEngId;

		getRiskInfo(auditEngId);

		setHandlers(auditEngId, rpcService);
	}

	private void setHandlers(final int auditEngId,
			final InternalAuditServiceAsync rpcService) {
		addMore.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {

				riskRows.add(new RiskRow());

			}
		});

		saveRisks.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {

				saveRiskstoDb(auditEngId, rpcService);
			}

			private void saveRiskstoDb(final int auditEngId,
					final InternalAuditServiceAsync rpcService) {
				ArrayList<Risk> records = new ArrayList<Risk>();

				for ( int i = 0; i < riskRows.getWidgetCount(); i++)
				{
					RiskRow current = ( (RiskRow) ( riskRows.getWidget(i) ));
					
					Risk risk = new Risk();
					risk.setAuditEngageId(auditEngId);
					risk.setRiskId( Integer.parseInt( current.getRiskId().getText() ));
		
					risk.setDescription(current.getDescription().getText());
					risk.setExistingControl(current.getControl().getText());
					
					current.disableFields();saveRisks.setEnabled(false);
					records.add( risk );
				}

				rpcService.saveRisks(records, new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable arg0) {

						new DisplayAlert("Fail" + arg0.getMessage());

					}

					@Override
					public void onSuccess(Boolean arg0) {

						new DisplayAlert("Risks Saved");

					}

				});
			}
		});
	}

	private void getRiskInfo(int auditEngId2) {

		rpcService.fetchRisks(auditEngId2, new AsyncCallback<ArrayList<Risk>>() {

			@Override
			public void onFailure(Throwable arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(ArrayList<Risk> r) {
				
				riskRows.clear();
				
				for ( int i = 0; i < r.size(); i++)
				{
					RiskRow current = new RiskRow();
					if(r.get(i)!= null &&  r.get(i).getRiskId()!=0){
						current.disableFields();
					}
					current.getRiskId().setText( String.valueOf(r.get(i).getRiskId()) );
		
					current.getDescription().setText( r.get(i).getDescription() );
					current.getControl().setText( r.get(i).getExistingControl() );
				
					riskRows.add( current );
				}
				
				
			}

		});

	}
	
	public void disableFields(){
		addMore.setEnabled(false);
		saveRisks.setEnabled(false);
		
	}

}
