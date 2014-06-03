package com.internalaudit.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.internalaudit.shared.AuditEngagement;
import com.internalaudit.shared.AuditStep;
import com.internalaudit.shared.AuditWork;
import com.internalaudit.shared.DashBoardDTO;
import com.internalaudit.shared.Department;
import com.internalaudit.shared.Employee;
import com.internalaudit.shared.Exceptions;
import com.internalaudit.shared.JobAndAreaOfExpertise;
import com.internalaudit.shared.JobCreation;
import com.internalaudit.shared.JobCreationDTO;
import com.internalaudit.shared.JobEmployeeRelation;
import com.internalaudit.shared.JobTimeEstimation;
import com.internalaudit.shared.JobTimeEstimationDTO;
import com.internalaudit.shared.JobsOfEmployee;
import com.internalaudit.shared.ResourceUse;
import com.internalaudit.shared.Risk;
import com.internalaudit.shared.RiskAssesmentDTO;
import com.internalaudit.shared.RiskFactor;
import com.internalaudit.shared.SkillUpdateData;
import com.internalaudit.shared.Skills;
import com.internalaudit.shared.Strategic;
import com.internalaudit.shared.StrategicAudit;
import com.internalaudit.shared.StrategicDTO;
import com.internalaudit.shared.StrategicRisk;
import com.internalaudit.shared.User;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface InternalAuditService extends RemoteService {
	User signIn(String userid, String password) throws Exception;

	ArrayList<Employee> fetchObjectiveOwners() throws Exception;

	ArrayList<Department> fetchDepartments() throws Exception;

	String saveStrategic(Strategic strategic, HashMap<String, String> hm)
			throws Exception;

	ArrayList<RiskFactor> fetchRiskFactors() throws Exception;

	ArrayList<Strategic> fetchStrategic(HashMap<String, String> hm)
			throws Exception;

	ArrayList<RiskAssesmentDTO> fetchRiskAssesment(HashMap<String, String> hm)
			throws Exception;

	String saveRiskAssesment(HashMap<String, String> hm,
			ArrayList<StrategicRisk> strategicRisks) throws Exception;

	String sendBackStrategic(Strategic strategics) throws Exception;

	String declineStrategic(int strategicId) throws Exception;

	ArrayList<StrategicAudit> fetchStrategicAudit() throws Exception;

	DashBoardDTO fetchDashBoard() throws Exception;

	ArrayList<Strategic> fetchFinalAuditables() throws Exception;

	Boolean checkDate(Date date) throws Exception;

	ArrayList<StrategicDTO> fetchSchedulingStrategic(HashMap<String, String> hm)
			throws Exception;

	ArrayList<Skills> fetchSkills() throws Exception;

	boolean saveJobTimeEstimation(JobTimeEstimationDTO entity,
			ArrayList<SkillUpdateData> updateForSkills);

	JobTimeEstimationDTO fetchJobTime(int jobId) throws Exception;

	ArrayList<Employee> fetchEmployees() throws Exception;// Add throw Exception  here..

	public ArrayList<ResourceUse> fetchResourceUseFor(int jobId);

	ArrayList<Employee> fetchEmployeesByDeptId(int deptId);

	void saveJobAndAreaOfExpertiseState(ArrayList<JobAndAreaOfExpertise> state);

	ArrayList<JobAndAreaOfExpertise> fetchCheckBoxStateFor(int jobId);
	
	void saveCreatedJob(JobCreationDTO job);


	String getEndDate(Date value, int estimatedWeeks);

	ArrayList<JobCreationDTO> fetchCreatedJobs(boolean getEmpRelation,	boolean getSkillRelation);
	
	JobCreation fetchCreatedJob(int jobId);

	ArrayList<JobsOfEmployee> fetchEmployeesWithJobs();

	JobCreation updateEndDateForJob(int jobId, String startDate, String endDate);

	int[] getMonthsInvolved(String string, String string2);
	
	public ArrayList<AuditEngagement> fetchAllAuditEngagement(int loggedInEmployee );

	boolean updateAuditEngagement(AuditEngagement e, String fieldToUpdate);

	void syncAuditEngagementWithCreatedJobs(int loggedInEmployee);

	boolean saveRisks (ArrayList<Risk> record);

	boolean sendEmail(String body, String sendTo);

	AuditEngagement fetchAuditEngagement(int selectedJobId);

	ArrayList<Risk> fetchRisks(int auditEngId);

//	ArrayList<Object> fetchEmpForThisJob(int selectedJobId);

	ArrayList<JobEmployeeRelation> fetchEmployeeJobRelations(int selectedJobId);
	ArrayList<JobCreation> fetchJobs();
	ArrayList<JobCreation> fetchEmployeeJobs(Employee loggedInEmployee);
	ArrayList<Exceptions> fetchJobExceptions(int jobId);
	ArrayList<Exceptions> fetchEmployeeExceptions(int employeeId, int jobId);
	
	String sendException(Exceptions exception);

	void saveAuditStepAndExceptions(AuditStep step, ArrayList<Exceptions> exs);

	AuditStep getSavedAuditStep(int selectedJobId,int auditWorkId);

	ArrayList<Exceptions> getSavedExceptions(int selectedJobId);

	void saveAuditWork(ArrayList<AuditWork> records);

	void updateKickoffStatus(int auditEngId);
	ArrayList<Exceptions> fetchAuditHeadExceptions(int auditHeadId, int selectedJob);

	JobCreationDTO fetchCreatedJob(int id, boolean b, boolean c, String string);

	ArrayList<AuditWork> fetchAuditWorkRows(int selectedJobId);
	ArrayList<AuditWork> fetchApprovedAuditWorkRows(int selectedJobId);
	String saveAuditNotification(int auditEngagementId, String message, String to, String cc);
	String logOut();
	void selectYear(int year);
	int fetchNumberofPlannedJobs();
	int fetchNumberofInProgressJobs();
	int fetchNumberofCompletedJobs();
	ArrayList<String> fetchJobsKickOffNextWeek();
	int fetchNumberOfAuditObservations();
	int fetchNumberOfExceptionsInProgress();
	int fetchNumberOfExceptionsImplemented();
	int fetchNumberOfExceptionsOverdue();
	ArrayList<String> fetchEmployeesAvilbleForNext2Weeks();
	
}
