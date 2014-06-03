/**
 * 
 */
package com.internalaudit.client.view.Scheduling;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.internalaudit.client.presenter.JobCreationPresenter.Display;
import com.internalaudit.shared.StrategicDTO;

/**
 * @author Muhammad Yaseen
 *
 */
public class JobCreationView extends Composite implements Display {
	
	@UiField TextBox domainText;
	@UiField TextBox relevantDept;
	@UiField TextBox riskRating;
	@UiField TextBox estimatedWeeks;
	@UiField VerticalPanel skillResourceContainer;
	
	@UiField TextBox techSkill;
	@UiField ListBox softSkill;
	@UiField ListBox proposedResources;
	@UiField ListBox auditHead;
	private StrategicDTO strategicDTO;
	
	@UiField
	Label heading;

	@UiField
	Button saveJobCreation;
	
	
	@UiField
	Button backButton;
	
	@UiField
	TextBox jobCreationId;
	
	private static JobCreationViewUiBinder uiBinder = GWT
			.create(JobCreationViewUiBinder.class);

	interface JobCreationViewUiBinder extends UiBinder<Widget, JobCreationView> {
	}

	public JobCreationView(StrategicDTO strategicDTO) {
		initWidget(uiBinder.createAndBindUi(this));

		this.strategicDTO = strategicDTO; 

	}

	public TextBox getEstimatedWeeks() {
		return estimatedWeeks;
	}

	public void setEstimatedWeeks(TextBox estimatedWeeks) {
		this.estimatedWeeks = estimatedWeeks;
	}

	public ListBox getProposedResources() {
		return proposedResources;
	}

	public void setProposedResources(ListBox proposedResources) {
		this.proposedResources = proposedResources;
	}

	public VerticalPanel getSkillResourceContainer() {
		return skillResourceContainer;
	}

	public void setSkillResourceContainer(VerticalPanel skillResourceContainer) {
		this.skillResourceContainer = skillResourceContainer;
	}

	public TextBox getRiskRating() {
		return riskRating;
	}

	public void setRiskRating(TextBox riskRating) {
		this.riskRating = riskRating;
	}

	public StrategicDTO getStrategicDTO() {
		return strategicDTO;
	}

	public void setStrategicDTO(StrategicDTO strategicDTO) {
		this.strategicDTO = strategicDTO;
	}

	public TextBox getDomainText() {
		return domainText;
	}

	public void setDomainText(TextBox domainText) {
		this.domainText = domainText;
	}

	public TextBox getRelevantDept() {
		return relevantDept;
	}

	public void setRelevantDept(TextBox relevantDept) {
		this.relevantDept = relevantDept;
	}

	public TextBox getTechSkill() {
		return techSkill;
	}

	public void setTechSkill(TextBox techSkill) {
		this.techSkill = techSkill;
	}
	
	public Label getHeading() {
		return heading;
	}

	public void setHeading(Label heading) {
		this.heading = heading;
	}

	public Button getSaveJobCreation() {
		return saveJobCreation;
	}

	public void setSaveJobCreation(Button saveJobCreation) {
		this.saveJobCreation = saveJobCreation;
	}

	public Button getBackButton() {
		return backButton;
	}

	public void setBackButton(Button backButton) {
		this.backButton = backButton;
	}

	public ListBox getSoftSkill() {
		return softSkill;
	}

	public void setSoftSkill(ListBox softSkill) {
		this.softSkill = softSkill;
	}


	public ListBox getAuditHead() {
		return auditHead;
	}

	public void setAuditHead(ListBox auditHead) {
		this.auditHead = auditHead;
	}

	public TextBox getJobCreationId() {
		return jobCreationId;
	}

	public void setJobCreationId(TextBox jobCreationId) {
		this.jobCreationId = jobCreationId;
	}

}
