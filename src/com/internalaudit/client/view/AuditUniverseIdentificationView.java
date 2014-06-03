package com.internalaudit.client.view;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.internalaudit.shared.InternalAuditConstants;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.PlainTabPanel;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.button.IconButton;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.AccordionLayoutAppearance;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.ExpandMode;
import com.sencha.gxt.widget.core.client.event.BeforeExpandEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.BeforeExpandEvent.BeforeExpandHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.info.Info;

public class AuditUniverseIdentificationView extends Composite {

	private static AuditUniverseIdentificationViewUiBinder uiBinder = GWT
			.create(AuditUniverseIdentificationViewUiBinder.class);

	interface AuditUniverseIdentificationViewUiBinder extends
	UiBinder<Widget, AuditUniverseIdentificationView> {
	}
	@UiField VerticalPanel mainPanel;
	private boolean  headingAdded = false;
	private VerticalPanel vpnlStrategic ;
	private VerticalPanel vpnlOperation ;
	private VerticalPanel vpnlReporting ;
	private VerticalPanel vpnlCompliance ;
	
	private ArrayList<AuditUniverseStrategicView> strategicList = new ArrayList<AuditUniverseStrategicView>();
	ContentPanel cp;
	private AuditUniverseStrategicView  auditUniverseStrategicView;

	public AuditUniverseIdentificationView(ContentPanel cp) {
		this.cp = cp;
		
		initWidget(uiBinder.createAndBindUi(this));
		bind();
		
		
	}

	private void bind() {


		auditUniverseIdentificationTabs();
//		btnAmend.setTitle("Send back for amendments");
	}

	public void setHandlers(){

		
	}

	

	private Widget flexPanelLayoutStrategic(final int tab) {
		ScrollPanel strategicPanel = new ScrollPanel();
		final VerticalPanel vpnlStrategic = new VerticalPanel();
		strategicPanel.add(vpnlStrategic);
		strategicPanel.setHeight("350px");
		final HorizontalPanel hpnlStrategic = new HorizontalPanel();
		hpnlStrategic.setWidth("800px");
		final TextButton btnAdd = new TextButton("+");
		final HorizontalPanel hpnlButtonsInitiator = new HorizontalPanel();
		final HorizontalPanel hpnlButtonsApprovar = new HorizontalPanel();
		HorizontalPanel hpnlSpace = new HorizontalPanel();
		hpnlSpace.setWidth("650px");
		hpnlButtonsInitiator.add(hpnlSpace);
		hpnlButtonsInitiator.setSpacing(2);
		hpnlButtonsInitiator.setVisible(false);
		hpnlButtonsApprovar.add(hpnlSpace);
		hpnlButtonsApprovar.setSpacing(2);
		hpnlButtonsApprovar.setVisible(false);
		final VerticalPanel vpnlStrategicData = new VerticalPanel();
		vpnlStrategic.add(btnAdd);
		vpnlStrategic.add(vpnlStrategicData);
		vpnlStrategic.add(hpnlButtonsInitiator);
		vpnlStrategic.add(hpnlButtonsApprovar);
		vpnlStrategicData.add(hpnlStrategic);
		auditUniverseStrategicView = new AuditUniverseStrategicView();
		auditUniverseStrategicView.getAuditUniverseStrategicViewData().fetchDepartments();
		auditUniverseStrategicView.getAuditUniverseStrategicViewData().fetchObjectiveOwners();

		
		
		
		vpnlStrategicData.clear();
		strategicList.clear();
		auditUniverseStrategicView.getAuditUniverseStrategicViewData().fetchStrategic(vpnlStrategicData,  hpnlButtonsInitiator, hpnlButtonsApprovar, btnAdd, tab );

//		cp.addBeforeExpandHandler(new BeforeExpandHandler(){
//
//			@Override
//			public void onBeforeExpand(BeforeExpandEvent event) {
//				vpnlStrategicData.clear();
//				strategicList.clear();
//				auditUniverseStrategicView.getAuditUniverseStrategicViewData().fetchStrategic(vpnlStrategicData,  hpnlButtonsInitiator, hpnlButtonsApprovar, btnAdd, tab );
//
//			}});
		
		btnAdd.addSelectHandler(new SelectHandler(){

			@Override
			public void onSelect(SelectEvent event) {
				btnAdd.setEnabled(false);
				auditUniverseStrategicView = new AuditUniverseStrategicView();
				auditUniverseStrategicView.getHpnlButtonsApprovar().setVisible(false);
				auditUniverseStrategicView.getHpnlButtonInitiator().setVisible(true);
				auditUniverseStrategicView.getAuditUniverseStrategicViewData().fetchDepartmentsForNewRecord(auditUniverseStrategicView);
				auditUniverseStrategicView.getAuditUniverseStrategicViewData().fetchObjectiveOwnersForNewRecord(auditUniverseStrategicView);

				vpnlStrategicData.add(auditUniverseStrategicView);
				auditUniverseStrategicView.getBtnSave().addClickHandler(new ClickHandler(){

					@Override
					public void onClick(ClickEvent event) {
						auditUniverseStrategicView.getAuditUniverseStrategicViewData().saveStrategic(auditUniverseStrategicView, vpnlStrategicData, hpnlButtonsInitiator, hpnlButtonsApprovar, btnAdd, "save", tab, auditUniverseStrategicView.getBtnSave());
					}});
				
					auditUniverseStrategicView.getBtnSubmit().addClickHandler(new ClickHandler(){

					@Override
					public void onClick(ClickEvent event) {
						auditUniverseStrategicView.getAuditUniverseStrategicViewData().saveStrategic(auditUniverseStrategicView, vpnlStrategicData, hpnlButtonsInitiator, hpnlButtonsApprovar, btnAdd, "submit", tab, auditUniverseStrategicView.getBtnSubmit());
					}});
		}});
	return strategicPanel;
	}

	public void auditUniverseIdentificationTabs(){

		VerticalPanel vp = new VerticalPanel();

		
		final PlainTabPanel panel = new PlainTabPanel();
		panel.setWidth("1000px");
		vpnlStrategic = new VerticalPanel();
		vpnlOperation = new VerticalPanel();
		vpnlReporting = new VerticalPanel();
		vpnlCompliance = new VerticalPanel();
		
		panel.add(vpnlStrategic, "Strategic");
		panel.add(vpnlOperation, "Operations");
		vpnlStrategic.add(flexPanelLayoutStrategic(0));
		panel.add(vpnlReporting, "Reporting");
		panel.add(vpnlCompliance, "Compliance");
		
	panel.addSelectionHandler(new SelectionHandler<Widget>(){

			@Override
			public void onSelection(SelectionEvent<Widget> event) {
				 TabPanel panel = (TabPanel) event.getSource();
			        Widget w = event.getSelectedItem();
			        TabItemConfig config = panel.getConfig(w);
			        if(config.getText().equalsIgnoreCase("strategic")){
			        	vpnlStrategic.clear();
			        	vpnlStrategic.add(flexPanelLayoutStrategic(0));
			        }
			        else if(config.getText().equalsIgnoreCase("operations")){
			        	vpnlOperation.clear();
			        	vpnlOperation.add(flexPanelLayoutStrategic(1));
				        }
			        else if(config.getText().equalsIgnoreCase("Reporting")){
			        	vpnlReporting.clear();
			        	vpnlReporting.add(flexPanelLayoutStrategic(2));
			        }
			        else if(config.getText().equalsIgnoreCase("Compliance")){
			        	vpnlCompliance.clear();
			        	vpnlCompliance.add(flexPanelLayoutStrategic(3));
				        }
					
			        
			}});
		vp.add(panel);
		mainPanel.add(vp);
	}

}