package com.internalaudit.client.view;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.internalaudit.client.view.data.ConsolidationViewData;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.google.gwt.user.client.ui.Button;

public class ConsolidationHeadingView extends Composite{
	private VerticalPanel vpnlData;
	private ContentPanel cp;
	private ConsolidationViewData consolidationViewData = new ConsolidationViewData();
	private Button submit;
	
	
	public VerticalPanel getVpnlData() {
		return vpnlData;
	}
	public void setVpnlData(VerticalPanel vpnlData) {
		this.vpnlData = vpnlData;
	}
	public ConsolidationHeadingView(ContentPanel cp) {
		vpnlData = new VerticalPanel();
		this.cp = cp;
		
		VerticalPanel verticalPanel = new VerticalPanel();
		initWidget(verticalPanel);
		verticalPanel.setHeight("35px");
		
		submit = new Button("Submit");
		submit.setWidth("100px");
		HorizontalPanel hpnlButton = new HorizontalPanel();
		HorizontalPanel hpnlButtonSpace = new HorizontalPanel();
		hpnlButtonSpace.setWidth("900px");
		hpnlButton.add(hpnlButtonSpace);
//		hpnlButton.add(submit);
		
		verticalPanel.add(hpnlButton);
		consolidationViewData.setData(cp, vpnlData, submit);
		submit.setWidth("100px");
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		verticalPanel.add(horizontalPanel);
		horizontalPanel.setSize("800px", "100%");
		
		Label lblArea = new Label("Objective");
		lblArea.setWordWrap(false);
		lblArea.setStyleName("blue");
		horizontalPanel.add(lblArea);
		lblArea.setWidth("255px");
		
		Label lblRiskUnit = new Label("Risk Rating");
		lblRiskUnit.setStyleName("blue");
		horizontalPanel.add(lblRiskUnit);
		lblRiskUnit.setWidth("165px");
		
		Label lblAuditaleRating = new Label("Auditable Unit");
		lblAuditaleRating.setStyleName("blue");
		horizontalPanel.add(lblAuditaleRating);
		lblAuditaleRating.setWidth("180px");
		
		Label lblTab = new Label("");
		lblTab.setStyleName("blue");
		horizontalPanel.add(lblTab);
		lblTab.setWidth("100px");
		
	
		verticalPanel.add(vpnlData);
		vpnlData.setSize("800px", "50%");
	}
	public ContentPanel getCp() {
		return cp;
	}
	public void setCp(ContentPanel cp) {
		this.cp = cp;
	}
	public ConsolidationViewData getConsolidationViewData() {
		return consolidationViewData;
	}
	public void setConsolidationViewData(ConsolidationViewData consolidationViewData) {
		this.consolidationViewData = consolidationViewData;
	}
	
	
}
