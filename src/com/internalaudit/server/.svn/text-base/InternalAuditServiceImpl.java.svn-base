package com.internalaudit.server;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;

import org.joda.time.format.DateTimeFormat;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibm.icu.text.SimpleDateFormat;
import com.internalaudit.client.InternalAuditService;
import com.internalaudit.database.MySQLRdbHelper;
import com.internalaudit.shared.AuditEngagement;
import com.internalaudit.shared.DashBoardDTO;
import com.internalaudit.shared.Department;
import com.internalaudit.shared.Employee;

import com.internalaudit.shared.JobAndAreaOfExpertise;
import com.internalaudit.shared.JobCreation;
import com.internalaudit.shared.JobCreationDTO;

import com.internalaudit.shared.AuditStep;
import com.internalaudit.shared.AuditWork;
import com.internalaudit.shared.Exceptions;
import com.internalaudit.shared.InternalAuditConstants;
import com.internalaudit.shared.JobEmployeeRelation;
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
import com.internalaudit.shared.TimeOutException;
import com.internalaudit.shared.User;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class InternalAuditServiceImpl extends RemoteServiceServlet implements
		InternalAuditService {
	HttpSession session ;
	@Override
	
	public User signIn(String userid, String password) throws Exception {
		//String result = "";
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
		"applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		User user = (User) rdbHelper.getAuthentication(userid, password);
		
		///
		//
		if(user!=null)
		{
			session=getThreadLocalRequest().getSession(true);

			session.setAttribute("user", user);
			session.setAttribute("year", 2014);//TODO: Need to get Current Year here.. in this format  i.e  2014,2015..
			session.setMaxInactiveInterval(InternalAuditConstants.TIMEOUT);
		}
		return user;

	}
	@Override
	public ArrayList<Employee> fetchObjectiveOwners() throws Exception {
		if(isLoggedIn()){
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
				MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
				return rdbHelper.fetchEmployees();
		}else{
			
			throw new TimeOutException(InternalAuditConstants.LOGGEDOUT);
			
		}	
	}
	@Override
	public ArrayList<Department> fetchDepartments() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
				MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
				return rdbHelper.fetchDepartments();
	}
	
	@Override
	public ArrayList<Skills> fetchSkills() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
				MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
				return rdbHelper.fetchSkills();
	}
	
	@Override
	public String saveStrategic(Strategic strategic, HashMap<String, String> hm)
			throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
				MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
				User loggedInUser = (User) session.getAttribute("user");
				int year = (Integer) session.getAttribute("year");
				return rdbHelper.saveStrategic(strategic, loggedInUser, hm, year);
	}
	@Override
	public ArrayList<RiskFactor> fetchRiskFactors() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
				MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
				return rdbHelper.fetchRiskFactors();
	}
	@Override
	public ArrayList<Strategic> fetchStrategic(HashMap<String, String> hm) throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
				MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
				User loggedInUser = (User) session.getAttribute("user");
				int year = (Integer) session.getAttribute("year");
				 hm.put("year", year+"");
				return rdbHelper.fetchStrategic(hm, loggedInUser.getEmployeeId().getEmployeeId());
	}
	@Override
	public String saveRiskAssesment(HashMap<String, String> hm,ArrayList<StrategicRisk> strategicRisks)
			throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
				MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
				User loggedInUser = (User) session.getAttribute("user");
				int year = (Integer) session.getAttribute("year");
				hm.put("year", year+"");
				return rdbHelper.saveRiskAssesment(strategicRisks, loggedInUser, hm);
	}
	
	@Override
	public String sendBackStrategic(Strategic strategics) throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
				MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
				User loggedInUser = (User) session.getAttribute("user");
				int year = (Integer) session.getAttribute("year");
				
				return rdbHelper.amendStrategic(strategics, loggedInUser, year );
	}
	@Override
	public ArrayList<RiskAssesmentDTO> fetchRiskAssesment(HashMap<String, String> hm) throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
				MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
				User loggedInUser = (User) session.getAttribute("user");
				int year = (Integer) session.getAttribute("year");
				return rdbHelper.fetchRiskAssesment(hm, loggedInUser.getEmployeeId().getEmployeeId(), year);
	}
	@Override
	public String declineStrategic(int strategicId) throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
				MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
				User loggedInUser = (User) session.getAttribute("user");
				return rdbHelper.declineStrategic(strategicId);
	}
	@Override
	public ArrayList<StrategicAudit> fetchStrategicAudit() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
				MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
				int year = (Integer) session.getAttribute("year");
				return rdbHelper.fetchStrategicAidit(year);
	}
	@Override
	public DashBoardDTO fetchDashBoard() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
				MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
				User loggedInUser = (User) session.getAttribute("user");
				int year = (Integer) session.getAttribute("year");
				return rdbHelper.fetchDashBoard(loggedInUser, year);
	}
	@Override
	public ArrayList<Strategic> fetchFinalAuditables() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
				MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
				int year = (Integer) session.getAttribute("year");
				return rdbHelper.fetchFinalAuditables(year);
	}
	@Override
	public ArrayList<StrategicDTO> fetchSchedulingStrategic(
			HashMap<String, String> hm) throws Exception {
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
				MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
				int year = (Integer) session.getAttribute("year");
				return rdbHelper.fetchSchdulingStrategic(hm, year);
	}
	@Override
	public Boolean checkDate(Date date) throws Exception {
		DateTime enteredDate = new DateTime(date); 
		DateTime currentDate = new DateTime(); //current date
		if(currentDate.isAfter(enteredDate) ){
			return false;
		}else 
			return true;
	}
	

	@Override
	public JobTimeEstimationDTO fetchJobTime(int jobId) throws Exception {
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		return rdbHelper.fetchJobTime(jobId, year);
	}
	@Override
	public ArrayList<Employee> fetchEmployees() throws Exception{ // Add throw Exception  here..
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		return rdbHelper.fetchEmployees();
	}
	@Override
	public ArrayList<ResourceUse> fetchResourceUseFor(int jobId) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		
		return rdbHelper.fetchResourceUseFor(jobId, year);
	}
	@Override
	public ArrayList<Employee> fetchEmployeesByDeptId(int deptId) {
		
			ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
					MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
					return rdbHelper.fetchEmployeesByDeptId(deptId);
					
	}
	@Override
	public void saveJobAndAreaOfExpertiseState(ArrayList<JobAndAreaOfExpertise> state) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		
		rdbHelper.saveJobAndAreaOfExpertiseState(state);
	}
	@Override
	public ArrayList<JobAndAreaOfExpertise> fetchCheckBoxStateFor(int jobId) {
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		
		return rdbHelper.fetchCheckBoxStateFor(jobId);
		
	}
	@Override
	public void saveCreatedJob(JobCreationDTO job) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		rdbHelper.saveCreatedJob(job, year);		
	}
	
	@Override
	public ArrayList<JobCreationDTO> fetchCreatedJobs( boolean getEmpRelation, boolean getSkillRelation) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		return rdbHelper.fetchCreatedJobs(getEmpRelation, getSkillRelation, year);		
	}
	
	@Override
	public ArrayList<JobsOfEmployee> fetchEmployeesWithJobs() {
	
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		
		 ArrayList<JobsOfEmployee> test = rdbHelper.fetchEmployeesWithJobs();
		 
		 return test;
		
	}
	
	@Override
	public String getEndDate(Date startDate, int estWeeks)
	{
		
		DateTime enddate = new DateTime(startDate);
		
		enddate = enddate.plusWeeks(estWeeks);
				
		return enddate.toString(DateTimeFormat.forPattern("dd, MM, yyyy"));	
	}

	@Override
	public int[] getMonthsInvolved(String startDate, String endDate)
	{
		
		if ( startDate == null || endDate == null )
			return new int[] {0,0};
		
		Date end = null;
		Date start = null;
		SimpleDateFormat fmt = new SimpleDateFormat("dd, MM, yyyy");
		
		try {
			end = fmt.parse(endDate);
			start = fmt.parse(startDate);
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
	
				
		Calendar c = Calendar.getInstance();

		
		c.setTime(start);
		
		int startMonth = c.get(Calendar.MONTH) + 1;


		c.setTime(end);

		
		int endMonth = c.get(Calendar.MONTH) + 1;
		

		return new int[] { startMonth, endMonth };
		

	}
	
	@Override
	public JobCreation updateEndDateForJob(int jobId, String startDate, String endDate) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		
		return rdbHelper.updateEndDateForJob( jobId, startDate,  endDate);
		
	}
	@Override
	public boolean saveJobTimeEstimation(JobTimeEstimationDTO entity, ArrayList<SkillUpdateData> updateForSkills) {
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		return rdbHelper.saveJobTimeEstimation( entity, updateForSkills, year );
	}
	@Override
	public ArrayList<AuditEngagement> fetchAllAuditEngagement(int loggedInEmployee) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		return rdbHelper.fetchAllAuditEngagement(loggedInEmployee, year);
	}
	@Override
	public JobCreation fetchCreatedJob(int jobId) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		
		return rdbHelper.fetchCreatedJob(jobId);
	}
	@Override
	public boolean updateAuditEngagement(AuditEngagement e, String fieldToUpdate) {

		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		return rdbHelper.updateAuditEngagement(e, fieldToUpdate, year);

	}
	@Override
	public void syncAuditEngagementWithCreatedJobs(int loggedInEmployee) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		rdbHelper.syncAuditEngagementWithCreatedJobs(loggedInEmployee, year);
		
		
	}
	@Override
	public boolean saveRisks(ArrayList<Risk> records) {
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		return rdbHelper.saveRisks(records, year);
	}
	
	@Override
	public boolean sendEmail(String body, String sendTo) {

		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		
		return rdbHelper.sendEmail(body, sendTo, "","");

	}
	@Override
	public AuditEngagement fetchAuditEngagement(int selectedJobId) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		return rdbHelper.fetchAuditEngagement(selectedJobId, year);
	}
	@Override
	public ArrayList<Risk> fetchRisks(int auditEngId) {
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		return rdbHelper.fetchRisks(auditEngId, year);
	}
	
//	@Override
//	public ArrayList<Object> fetchEmpForThisJob(int selectedJobId) {
//		
//		/*ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
//		
//		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
//		*/
//		
//		return null;//rdbHelper.fetchEmpForThisJob(selectedJobId);
//	}
	@Override
	public ArrayList<JobEmployeeRelation> fetchEmployeeJobRelations(
			int selectedJobId) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		
		return rdbHelper.fetchEmployeeJobRelations(selectedJobId);
	}
	@Override
	public ArrayList<JobCreation> fetchJobs() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		return rdbHelper.fetchJobs(year);
	}
	
	@Override
	public ArrayList<Exceptions> fetchJobExceptions(int jobId) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		return rdbHelper.fetchJobExceptions(jobId, year);
	}
	@Override
	public String sendException(Exceptions exception) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		return rdbHelper.sendException(exception, year);
	}
	@Override
	public ArrayList<Exceptions> fetchEmployeeExceptions(int employeeId, int jobId) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		return rdbHelper.fetchEmployeeExceptions(employeeId, jobId, year);
	}
	@Override
	public void saveAuditStepAndExceptions(AuditStep auditstep,
			ArrayList<Exceptions> exs) {
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		rdbHelper.saveAuditStepAndExceptions(auditstep, exs, year );
		
	}
	@Override
	public AuditStep getSavedAuditStep(int jobid, int auditWorkId) {

		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		return rdbHelper.getSavedAuditStep(jobid, auditWorkId, year );
		
		
	}
	@Override
	public ArrayList<Exceptions> getSavedExceptions(int selectedJobId) {
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		return rdbHelper.getSavedExceptions(selectedJobId, year );
		
		
	}
	@Override
	public void saveAuditWork(ArrayList<AuditWork> records) {
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		rdbHelper.saveAuditWork( records, year );
		
	}
	@Override
	public void updateKickoffStatus(int auditEngId) {
	
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		rdbHelper.updateKickoffStatus( auditEngId, year );
		
	}

	@Override
	public ArrayList<Exceptions> fetchAuditHeadExceptions(int auditHeadId,int selectedJob) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		return rdbHelper.fetchAuditHeadExceptions(auditHeadId, selectedJob, year);
	}

	@Override
	public JobCreationDTO fetchCreatedJob(int id, boolean b, boolean c,	String string) {

		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		return rdbHelper.fetchCreatedJob( id, b, c ,string );
		
	}
	@Override
	public ArrayList<AuditWork> fetchAuditWorkRows(int jocreationid) {
	
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		
		return rdbHelper.fetchAuditWorkRows( jocreationid );
		
	}
	@Override
	public ArrayList<AuditWork> fetchApprovedAuditWorkRows(int selectedJobId) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		
		return rdbHelper.fetchApprovedAuditWorkRows( selectedJobId );
	}
	@Override
	public ArrayList<JobCreation> fetchEmployeeJobs(Employee loggedInEmployee) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		
		return rdbHelper.fetchEmployeeJobs( loggedInEmployee );
	}
	@Override
	public String saveAuditNotification(int auditEngagementId,String message, String to, String cc) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		
		return rdbHelper.saveAuditNotification( auditEngagementId, message, to, cc , year);
	}

	
	private boolean isLoggedIn(){
		HttpSession session = getThreadLocalRequest().getSession(true);
		if(session.getAttribute("user") == null){
			return false;
		}else{
			return true;
		}
	}
	
	public String logOut(){
		getThreadLocalRequest().getSession().invalidate();
		return "loggedOut";
	}
	@Override
	public void selectYear(int year) {
		session.setAttribute("year", year);
	}
	@Override
	public int fetchNumberofPlannedJobs() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		
		return rdbHelper.fetchNumberofPlannedJobs(year);
	}
	@Override
	public int fetchNumberofInProgressJobs() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		return rdbHelper.fetchNumberofInProgressJobs(year);
	}
	@Override
	public int fetchNumberofCompletedJobs() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		return rdbHelper.fetchNumberofCompletedJobs(year);
	}
	@Override
	public ArrayList<String> fetchJobsKickOffNextWeek() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		return rdbHelper.fetchJobsKickOffNextWeek(year);
	}
	@Override
	public int fetchNumberOfAuditObservations() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		return rdbHelper.fetchNumberOfAufitObservations(year);
	}
	@Override
	public int fetchNumberOfExceptionsInProgress() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		return rdbHelper.fetchNumberOfExceptionsInProgress(year);
	}
	@Override
	public int fetchNumberOfExceptionsImplemented() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		return rdbHelper.fetchNumberOfExceptionsImplemented(year);
	}
	@Override
	public int fetchNumberOfExceptionsOverdue() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		return rdbHelper.fetchNumberOfExceptionsOverdue(year);
	}
	@Override
	public ArrayList<String> fetchEmployeesAvilbleForNext2Weeks() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		MySQLRdbHelper rdbHelper = (MySQLRdbHelper) ctx.getBean("ManagerExams");
		int year = (Integer) session.getAttribute("year");
		return rdbHelper.fetchEmployeesAvilbleForNext2Weeks(year);
	}
	

}
