package com.internalaudit.client.view;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.internalaudit.client.presenter.MainPresenter.Display;
import com.internalaudit.client.view.Reporting.ReportingView;
import com.internalaudit.client.view.Scheduling.AuditSchedulingView;
import com.internalaudit.shared.User;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.widget.core.client.PlainTabPanel;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.info.Info;

public class MainView extends Composite implements Display {

	private AuditPlanningView auditPlanningView = new AuditPlanningView();
	private User loggedInUser;
	private Anchor logOut = new Anchor("Logout");
	private ListBox listYears = new ListBox();
	private Label welcome = new Label("");
	private VerticalPanel vpnlAuditScheduing = new VerticalPanel();
	private VerticalPanel vpnlAuditEngagement = new VerticalPanel();
	private VerticalPanel vpnlDashBoard = new VerticalPanel();
	private VerticalPanel reportingView = new VerticalPanel();
	private int selectedYear;
	
	PlainTabPanel panel = new PlainTabPanel();

	public MainView(User loggedInUser, int selectedYear){

		this.loggedInUser = loggedInUser;
		this.selectedYear = selectedYear;

		VerticalPanel vp = new VerticalPanel();
		HorizontalPanel hpnl = new HorizontalPanel();

		vp.add(hpnl);

		//	 SelectionHandler<Widget> handler = new SelectionHandler<Widget>() {
		//	      @Override
		//	      public void onSelection(SelectionEvent<Widget> event) {
		//	        TabPanel panel = (TabPanel) event.getSource();
		//	        Widget w = event.getSelectedItem();
		//	        TabItemConfig config = panel.getConfig(w);
		//	        Info.display("Message", "'" + config.getText() + "' Selected");
		//	      }
		//	    };

		logOut.setStyleName("logout");

		panel.setWidth("1000px");
		if(loggedInUser.getEmployeeId().getFromInternalAuditDept().equalsIgnoreCase("yes")){
			panel.add(new EmployeeDashBoardView(), "WorkItems");
			panel.add(auditPlanningView, "Audit Planning");
			panel.add(vpnlAuditScheduing, "Audit Scheduling");
			panel.add(vpnlAuditEngagement, "Audit Engagement");
			panel.add(vpnlDashBoard, "DashBoard");
		}else{
			panel.setActiveWidget(reportingView);
		}
		panel.add(reportingView, "Reporting");
		
		hpnl.add(panel);
		hpnl.add(selectYear());
		hpnl.add(welcome); // Welcome <name>
		welcome.setWordWrap(false);
		hpnl.add(logOut); // logout link
		hpnl.setSpacing(2);
		
		initWidget(vp);
	}
	
	public Widget selectYear(){
		HorizontalPanel hpnlYear = new HorizontalPanel();
		VerticalPanel vpnlYear = new VerticalPanel();
		Label lblSelectYear = new Label("Year");
		vpnlYear.add(lblSelectYear);
		vpnlYear.add(listYears);
		hpnlYear.add(vpnlYear);
		
		listYears.addItem("2014");
		listYears.addItem("2015");
		listYears.addItem("2016");
		listYears.addItem("2017");
		listYears.addItem("2018");
		listYears.addItem("2019");
		listYears.addItem("2020");
		if(selectedYear!=0){
			for(int i=0; i< listYears.getItemCount(); i++){
				if(Integer.parseInt(listYears.getValue(i)) == selectedYear){
					listYears.setSelectedIndex(i);
				}
			}
		}
		return hpnlYear;
		
		
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}

	public Anchor getLogOut() {
		return logOut;
	}

	public void setLogOut(Anchor logOut) {
		this.logOut = logOut;
	}

	public Label getWelcome() {
		return welcome;
	}

	public void setWelcome(Label welcome) {
		this.welcome = welcome;
	}

	public VerticalPanel getVpnlAuditScheduing() {
		return vpnlAuditScheduing;
	}

	public void setVpnlAuditScheduing(VerticalPanel vpnlAuditScheduing) {
		this.vpnlAuditScheduing = vpnlAuditScheduing;
	}

	public PlainTabPanel getPanel() {
		return panel;
	}

	public void setPanel(PlainTabPanel panel) {
		this.panel = panel;
	}

	public VerticalPanel getVpnlAuditEngagement() {
		return vpnlAuditEngagement;
	}

	public void setVpnlAuditEngagement(VerticalPanel vpnlAuditEngagement) {
		this.vpnlAuditEngagement = vpnlAuditEngagement;
	}

	public VerticalPanel getReportingView() {
		return reportingView;
	}

	public void setReportingView(VerticalPanel reportingView) {
		this.reportingView = reportingView;
	}

	public ListBox getListYears() {
		return listYears;
	}

	public void setListYears(ListBox listYears) {
		this.listYears = listYears;
	}

	public VerticalPanel getVpnlDashBoard() {
		return vpnlDashBoard;
	}

	public void setVpnlDashBoard(VerticalPanel vpnlDashBoard) {
		this.vpnlDashBoard = vpnlDashBoard;
	}



}
