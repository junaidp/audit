package com.internalaudit.client.presenter;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.internalaudit.client.InternalAuditServiceAsync;
import com.internalaudit.client.view.DisplayAlert;
import com.internalaudit.client.widgets.SkillsResources;
import com.internalaudit.shared.Employee;
import com.internalaudit.shared.JobCreation;
import com.internalaudit.shared.JobCreationDTO;
import com.internalaudit.shared.JobEmployeeRelation;
import com.internalaudit.shared.JobSkillRelation;
import com.internalaudit.shared.JobTimeEstimationDTO;
import com.internalaudit.shared.ResourceUse;
import com.internalaudit.shared.Softskills;
import com.internalaudit.shared.StrategicDTO;


public class JobCreationPresenter implements Presenter {


	private final InternalAuditServiceAsync rpcService;
	private final HandlerManager eventBus;
	private final Display display;
	private StrategicDTO DTO;
	private JobCreationDTO jobDTO = null;
	
	public interface Display 
	{
		Widget asWidget();
		Object getHtmlErrorMessage = null;	
		TextBox getEstimatedWeeks();
		VerticalPanel getSkillResourceContainer();
		ListBox getProposedResources();
		TextBox getRiskRating();
		StrategicDTO getStrategicDTO();
		TextBox getRelevantDept();
		TextBox getDomainText();
		TextBox getTechSkill();
		Label getHeading() ;
		Button getSaveJobCreation();
		Button getBackButton();
		 ListBox getSoftSkill();

		 ListBox getAuditHead();

		  TextBox getJobCreationId() ;

	}  

	public JobCreationPresenter(InternalAuditServiceAsync rpcService, HandlerManager eventBus, Display view, StrategicDTO strategicDTO) 
	{
		this.rpcService = rpcService;
		this.eventBus = eventBus;
		this.display = view;

		this.DTO = strategicDTO;
		
		fetchCreatedJob(rpcService, strategicDTO);

	}

	private void fetchCreatedJob(InternalAuditServiceAsync rpcService,
			StrategicDTO strategicDTO) {
		rpcService.fetchCreatedJob( strategicDTO.getStrategicId(), true, true, "jobId", new AsyncCallback<JobCreationDTO>() {

			@Override
			public void onFailure(Throwable arg0) {
				
				
			}

			@Override
			public void onSuccess(JobCreationDTO arg0) {
				if ( arg0 == null )
				{
					System.out.println("no previouis job creation recrd found for this job");
					
				}
				else {
					
					System.out.println(" Job creation found with id : "  + arg0.getJob().getJobCreationId());
					jobDTO = arg0;
					
					fetchRelatedEmpAndSetSelected();
					
					display.getRelevantDept().setText(jobDTO.getJob().getRelevantDept());

					display.getTechSkill().setText( jobDTO.getJob().getTechnical() );
					display.getRiskRating().setText(jobDTO.getJob().getRiskRating());
					display.getDomainText().setText(jobDTO.getJob().getDomainText()); 
					display.getEstimatedWeeks().setText( String.valueOf( jobDTO.getJob().getEstimatedWeeks() ));
					display.getJobCreationId().setText( String.valueOf(jobDTO.getJob().getJobCreationId()));
					
					//fill audit head listbox and select correct emo
					fetchEmployees( jobDTO.getJob().getAuditHead());
					
				}
			} 
			
			
		});
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(display.asWidget());
		bind();

	}


	private void bind() {
		
		//redaundant call :(
		fetchEmployees(0); //0 means no emp was selected

		
		display.getSaveJobCreation().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				
				JobCreationDTO dto = new JobCreationDTO();
				dto.getJob().setJobCreationId( Integer.parseInt( display.getJobCreationId().getText()));
				dto.getJob().setDomainText( display.getDomainText().getText().toString() );
				dto.getJob().setEstimatedWeeks( Integer.parseInt( display.getEstimatedWeeks().getText().toString() ));
				dto.getJob().setJobId( display.getStrategicDTO().getStrategicId());
				dto.getJob().setJobName( display.getStrategicDTO().getStrategicObjective());
//				dto.getJob().setProposedResources();
				dto.getJob().setRelevantDept( display.getRelevantDept().getText().toString() );
				dto.getJob().setRiskRating(display.getRiskRating().getText().toString() );
				dto.getJob().setTechnical( display.getTechSkill().getText().toString() );
				
				dto.getJob().setAuditHead(
						Integer.parseInt(display.getAuditHead().getValue(display.getAuditHead().getSelectedIndex()
								)
				));
				//new Employee().setEmployeeId(2);
				
				ArrayList<JobEmployeeRelation> relations = new ArrayList<JobEmployeeRelation>();
				
				for ( int i = 0; i < display.getProposedResources().getItemCount(); ++i)
				{
					if ( display.getProposedResources().isItemSelected(i))
					{
						
						JobEmployeeRelation r = new JobEmployeeRelation();
						
						JobCreation j = new JobCreation();
						j.setJobId(display.getStrategicDTO().getStrategicId());
						j.setJobCreationId( jobDTO.getJob().getJobCreationId() );
						r.setJobCreationId(j);
						
						Employee e = new Employee();
						e.setEmployeeId( Integer.parseInt(display.getProposedResources().getValue(i).split("-")[0] ));
						
						r.setEmployee(e);
						r.setId( Integer.parseInt(display.getProposedResources().getValue(i).split("-")[1] ));
						relations.add(r);
					}
						
				}
				
				
				ArrayList<JobSkillRelation> skillrelations = new ArrayList<JobSkillRelation>();
				for ( int i =0; i < display.getSoftSkill().getItemCount(); i++)
				{
					if ( display.getSoftSkill().isItemSelected(i))
					{
						
						JobSkillRelation r = new JobSkillRelation();
						
						r.setJobId( display.getStrategicDTO().getStrategicId());
						
						Softskills s = new Softskills();
						s.setSoftSkillId(Integer.parseInt(display.getSoftSkill().getValue(i)));
						r.setSoftskills(s);
						
						skillrelations.add(r);
					}
						
					
				}
				
				dto.setRelation(relations);
				dto.setJobSkillRelation(skillrelations);
				
				rpcService.saveCreatedJob(dto, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						new DisplayAlert("Saved");
						
					}
					
					@Override
					public void onFailure(Throwable caught) {
						new DisplayAlert("There was an error in saving " + caught.getMessage());
						
					}
				});
				
				

			}
		});

		display.getBackButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				History.newItem("auditScheduling");
			}
		});

		StrategicDTO dto = display.getStrategicDTO();
		
		if ( jobDTO != null )
		{

			
		}
		
		else {
			
			fetchRelatedEmpAndSetSelected();
			
			display.getRelevantDept().setText(dto.getDepartmentName());

			display.getTechSkill().setText(dto.getDepartmentName());
			display.getRiskRating().setText(dto.getRiskRating());
			display.getDomainText().setText(dto.getDomainBasedOnTab()); 
			
			//fetchEmployees();
			
			//select 'None'
			display.getAuditHead().setSelectedIndex(0);
			
			rpcService.fetchJobTime(dto.getStrategicId(), new AsyncCallback<JobTimeEstimationDTO>() {

				@Override
				public void onSuccess(JobTimeEstimationDTO result) {

					if ( result != null)
					display.getEstimatedWeeks().setText(String.valueOf(result.getJobTimeEstimation().getEstimatedWeeks()));

				}

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub

				}
			});

		}
		
		

		//fetches employees list
		//Here I'll need to pass Id, of dept...
		//
		

		rpcService.fetchResourceUseFor(dto.getStrategicId(), new AsyncCallback<ArrayList<ResourceUse>>() {

			@Override
			public void onSuccess(ArrayList<ResourceUse> result) {

				//go through all the resources, one by one
				//and the the name of resource and number of this resources reqd
				// add individual items in `result` to the container widget

				for( int i = 0; i < result.size(); ++i)
				{// where u r updating those textboxes
					//add resource info widget to the container

					SkillsResources sr = new SkillsResources();

					sr.getSkillsList().addItem(result.get(i).getSkill().getSkillName(), String.valueOf(result.get(i).getSkill().getSkillId()));

					sr.getNoOfResources().setText( String.valueOf(result.get(i).getNoOfResources()) );

					
					sr.getNoOfResources().setEnabled(false);
					sr.getSkillsList().setEnabled(false);

					// adding widgets one by one

					display.getSkillResourceContainer().add(sr);

					//some style to set the padding and margin, this prevents the unusual collapse of widgets into each other

					display.getSkillResourceContainer().addStyleName("spacer");

					sr.getSkillsList().addStyleName("lstSkillResources");

					sr.getNoOfResources().addStyleName("spacer");
				}

			}

			@Override
			public void onFailure(Throwable caught) {
				new DisplayAlert( caught.getMessage() );

			}
		});
	}

	private void fetchRelatedEmpAndSetSelected() {
		rpcService.fetchEmployeesByDeptId( display.getStrategicDTO().getDeptId(), new AsyncCallback<ArrayList<Employee>>() {

			@Override
			public void onSuccess(ArrayList<Employee> result) {

				if ( display.getProposedResources().getItemCount() == 1 )
				{
					for(int i = 0; i < result.size(); ++i)
					{
	
						display.getProposedResources().addItem( 
								result.get(i).getEmployeeName(), 
								String.valueOf(result.get(i).getEmployeeId()) +"-0" //relation id 0 for first time
								);
						
						//if ( jobDTO.getJobSkillRelation().get(i).getSoftskills().getSoftSkillName())
						
						//display.getProposedResources().setSelectedIndex(i);
					}
				}
				/*
						for (int i = 0; i < result.size(); ++i) {
							
							for (int k = 0; k < jobDTO.getRelation().size(); ++k) {
								
								if (jobDTO.getRelation().get(k).getEmployee()
										.getEmployeeId() == result.get(i)
										.getEmployeeId()) {

									display.getProposedResources().setValue(k, jobDTO.getRelation().get(k)
												.getEmployee()
												.getEmployeeId() + "-" +  jobDTO.getRelation().get(k).getId()
										);
									display.getProposedResources()
											.setSelectedIndex(k);
								}
							}
						}*/

			}

			@Override
			public void onFailure(Throwable caught) {

			}
		});
	}
	
	private void fetchEmployees(final int idToSelect){
		rpcService.fetchEmployees(new AsyncCallback<ArrayList<Employee>>(){

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(ArrayList<Employee> result) {
				
				if ( ! ( display.getAuditHead().getItemCount() > 1) ) //if NOT list already populated
				{   //populate list
					for(int i=0; i< result.size(); i++)
					{
						display.getAuditHead().addItem(result.get(i).getEmployeeName(), result.get(i).getEmployeeId()+"");
					}
				}
				
				display.getAuditHead().setSelectedIndex(0);
				
				if ( idToSelect != 0 && jobDTO != null )
				{					
						for(int i=0; i< result.size(); i++){
							
							if ( idToSelect == result.get(i).getEmployeeId() )
							{
									display.getAuditHead().setSelectedIndex(i+1);
							}
						}
				}
				
			}
			
		});
	}
	
}
