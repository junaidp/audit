package com.internalaudit.client.view.AuditEngagement;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.internalaudit.client.InternalAuditServiceAsync;
import com.internalaudit.shared.AuditWork;
import com.internalaudit.shared.Employee;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.AccordionLayoutAppearance;

public class AuditStepContainer extends Composite {

	private VerticalPanel vpnlMain = new VerticalPanel();
	private Employee loggedInEmployee;

	public AuditStepContainer(int selectedJobId, InternalAuditServiceAsync rpcService, Employee loggedInEmployee){
		initWidget(vpnlMain);
		this.loggedInEmployee = loggedInEmployee;
		fetchAuditWorksForSelectedJob(rpcService, selectedJobId);
		Label lblHeading = new Label("Audit Steps");
		vpnlMain.add(lblHeading);
		lblHeading.setStyleName("heading");
		

	}

	private void fetchAuditWorksForSelectedJob(InternalAuditServiceAsync rpcService, final int selectedJobId) {


		rpcService.fetchApprovedAuditWorkRows(selectedJobId, new AsyncCallback<ArrayList<AuditWork>>(){

			@Override
			public void onFailure(Throwable caught) {
				System.out.println("Error in fetchAuditWorkRows in AuditStepContainer");
			}

			@Override
			public void onSuccess(ArrayList<AuditWork> result) {

				ContentPanel panel = new ContentPanel();
				panel.setWidth("900px");
				panel.setBodyBorder(false);
				AccordionLayoutContainer con = new AccordionLayoutContainer();
				panel.add(con);
				AccordionLayoutAppearance appearance = GWT.<AccordionLayoutAppearance> create(AccordionLayoutAppearance.class);
				for(int i=0; i< result.size(); i++){
					createAuditStepPanels(result.get(i), selectedJobId, con, appearance, panel);
				}
				vpnlMain.add(panel);
			}

		});
	}

	private void createAuditStepPanels(AuditWork auditWork, int selectedJobId, AccordionLayoutContainer con, AccordionLayoutAppearance appearance, ContentPanel panel) {

		ContentPanel cp = new ContentPanel(appearance);
		cp.setAnimCollapse(false);
		cp.setHeadingText(auditWork.getStepNo()+"."+auditWork.getDescription());
		VerticalPanel vpnlIdentification = new VerticalPanel();
		vpnlIdentification.setHeight("400px");
		ScrollPanel sp = new ScrollPanel();
		sp.add(new AuditStepView(auditWork, selectedJobId, loggedInEmployee));
		sp.setHeight("360px");
		cp.add(sp);
		con.add(cp);

	}


}
