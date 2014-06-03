package com.internalaudit.client.view;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.internalaudit.shared.InternalAuditConstants;

public class AuditUniverseStrategicViewHeading extends Composite {
	
	public AuditUniverseStrategicViewHeading(){
		
		final HorizontalPanel hpnlStrategic = new HorizontalPanel();
		initWidget(hpnlStrategic);
		Label lblStrategicObjective = new Label(InternalAuditConstants.STRATEGICOBJECTIVE);
		Label lblObjectiveOwner = new Label(InternalAuditConstants.STRATEGICOBJECTIVEOWNER);
		Label relevantDepartment = new Label(InternalAuditConstants.STRATEGICDEPT);
		Label objectiveAchievementDate = new Label(InternalAuditConstants.STRATEGICDATE);
		Label objectiveId = new Label(AuditConstants.OBJECTIVEID);
		
		hpnlStrategic.add(objectiveId);
		objectiveId.setWidth("20px");
		hpnlStrategic.add(lblStrategicObjective);
//		hpnlStrategic.add(lblObjectiveOwner);
		hpnlStrategic.add(relevantDepartment);
//		hpnlStrategic.add(objectiveAchievementDate);
		
		hpnlStrategic.setWidth("740px");
		lblStrategicObjective.setWidth("530px");
		lblObjectiveOwner.setWidth("180px");
		relevantDepartment.setWidth("180px");
		objectiveId.setWordWrap(false);
		objectiveAchievementDate.setWordWrap(false);
		 lblStrategicObjective.setStyleName("navybluebold");
		 lblObjectiveOwner.setStyleName("blue");
		 relevantDepartment.setStyleName("blue");
		 objectiveAchievementDate.setStyleName("blue");
		 objectiveId.setStyleName("white");
	}

}
