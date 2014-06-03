package com.internalaudit.database;

//import java.lang.invoke.VolatileCallSite;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Minutes;
import org.joda.time.DateTime;
import com.google.gwt.dev.util.DefaultTextOutput;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.internalaudit.shared.AuditEngagement;
import com.internalaudit.shared.AuditStep;
import com.internalaudit.shared.AuditWork;
import com.internalaudit.shared.DashBoardDTO;
import com.internalaudit.shared.Department;
import com.internalaudit.shared.Employee;
import com.internalaudit.shared.Exceptions;
import com.internalaudit.shared.HibernateDetachUtility;
import com.internalaudit.shared.InternalAuditConstants;
import com.internalaudit.shared.JobAndAreaOfExpertise;
import com.internalaudit.shared.JobCreation;
import com.internalaudit.shared.JobCreationDTO;
import com.internalaudit.shared.JobEmployeeRelation;
import com.internalaudit.shared.JobSkillRelation;
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
import com.internalaudit.shared.StrategicDepartments;
import com.internalaudit.shared.StrategicRisk;
import com.internalaudit.shared.TimeLineDates;
import com.internalaudit.shared.User;
public class MySQLRdbHelper {

	private Session session;

	private SessionFactory sessionFactory;
	Logger logger;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public User getAuthentication(String userid, String password) throws Exception
	{
		fetchJobExceptions(0,2014);/// CHANGE DATE TO AUTO
		logger = Logger.getLogger("com.internalaudit.database.MySQLRdbHelper.class");
		logger.setLevel(Level.DEBUG);
		User users = null;
		Session session = sessionFactory.openSession();

		Criteria crit = session.createCriteria(User.class);
		crit.add(Restrictions.eq("name", userid));
		crit.add(Restrictions.eq("password", password));
		crit.createAlias("employeeId", "emp");
		crit.createAlias("emp.countryId", "empcoun");
		crit.createAlias("emp.cityId", "empcit");
		crit.createAlias("emp.departmentId", "empdep");
		crit.createAlias("emp.reportingTo", "empRep");
		crit.createAlias("empRep.userId", "empRepUser");
		crit.createAlias("empRep.reportingTo", "empRepRep");
		crit.createAlias("empRepRep.userId", "empRepRepUser");
		List rsList = crit.list();
		for(Iterator it=rsList.iterator();it.hasNext();	)
		{
			users = (User)it.next();
			System.out.println(users.getPassword());
		}
		session.close();
		return users;
	}

	public ArrayList<Employee> fetchEmployees() throws Exception {//Add this Throw Exception in all methods

		Session session = null;
		ArrayList<Employee> employees = new ArrayList<Employee>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Employee.class);
			crit.createAlias("cityId", "city");
			crit.createAlias("countryId", "countryId");
			crit.createAlias("departmentId", "department");
			crit.createAlias("userId", "user");
			crit.add(Restrictions.ne("employeeId", 0));
			List rsList = crit.list();
			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				Employee employee =  (Employee)it.next();
				employees.add(employee);
			}

			return employees;// Return BEFORE catch Statement..

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchEmployees", ex.getMessage()), ex);
			throw new Exception("Exception occured in fetchEmployees");//Add this Line Accordingly in all method
		}
		finally{
			session.close();
		}
	}



	public ArrayList<Department> fetchDepartments() {
		Session session = null;
		ArrayList<Department> departments = new ArrayList<Department>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Department.class);
			List rsList = crit.list();
			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				Department department =  (Department)it.next();
				departments.add(department);
			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchDepartments", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return departments;
	}


	public JobTimeEstimationDTO fetchJobTime(int jobId, int year) {

		Session session = null;
		JobTimeEstimationDTO record = null;
		try
		{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(JobTimeEstimation.class);

			//crit.add( Restrictions.eq("jobTimeEstimationId", jobId) );

			crit.add( Restrictions.eq("jobId", jobId) );
			crit.add( Restrictions.eq("year", year) );
			List rsList = crit.list();

			// u fetching from resourceuse table?
			//yes  /where its mentioned

			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				record =  new JobTimeEstimationDTO();
				JobTimeEstimation jobTimeEstimation = (JobTimeEstimation) it.next();
				record.setJobTimeEstimation(jobTimeEstimation);
				HibernateDetachUtility.nullOutUninitializedFields(jobTimeEstimation, HibernateDetachUtility.SerializationType.SERIALIZATION);


				record.setResourceUse( fetchResourceUse(jobId));
			}
		}
		catch(Exception ex)
		{
			logger.warn(String.format("Exception occured in fetchDepartments", ex.getMessage()), ex);
		}
		finally
		{
			session.close();
		}

		return record;	
	}
	public ArrayList<ResourceUse> fetchResourceUse(int jobId) {

		Session session = null;

		ArrayList<ResourceUse> records = new ArrayList<ResourceUse>();
		try
		{//come here
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(ResourceUse.class);
			crit.createAlias("skillId", "skill");
			crit.add( Restrictions.eq("jobId", jobId) );
			crit.setMaxResults(2); //should be equal to num of skills
			//:) new tpo me
			List rsList = crit.list();
			// i think its fine ... continue
			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				records.add((ResourceUse)it.next());
			}
		}
		catch(Exception ex)
		{
			logger.warn(String.format("Exception occured in resource use", ex.getMessage()), ex);
		}
		finally
		{
			session.close();
		}

		return records;	
	}



	public ArrayList<Skills> fetchSkills() {
		Session session = null;
		ArrayList<Skills> skills = new ArrayList<Skills>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Skills.class);
			List rsList = crit.list();
			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				Skills skill =  (Skills)it.next();
				skills.add(skill);
			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchSkills", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return skills;
	}


	public String saveStrategic(Strategic strategic, User loggedInUser, HashMap<String, String> hm, int year) {

		try{
			session = sessionFactory.openSession();
			String todo = hm.get("todo");
			String tab = hm.get("tab");
			saveOneStrategic(strategic, loggedInUser, todo, tab, year);

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in saveStrategic", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return "added";
	}

	public Employee fetchSupervisor(int userId){
		Session session = null;
		Employee employee = null;
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Employee.class);
			crit.createAlias("userId", "user");
			crit.add(Restrictions.eq("employeeId", userId));
			if(crit.list().size()>0){
				employee = (Employee) crit.list().get(0);
			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchSupervisor", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return employee.getReportingTo();
	}


	public void saveOneStrategic(Strategic strategic, User loggedInUser, String todo, String tab, int year){
		Session session = null;
		Transaction tr = null;
		try{
			int selectedTab = Integer.parseInt(tab);
			session = sessionFactory.openSession();
			tr = (Transaction) session.beginTransaction();
			Strategic clientSideStrategic = strategic;

			if(strategic.getId() == 0 ){
				if(todo.equals("save")){
					initiateStrategic(strategic, loggedInUser, clientSideStrategic);
				}else{
					submitStrategic(strategic, loggedInUser, clientSideStrategic);
				}
			}else{
				strategic = (Strategic) session.get(Strategic.class, strategic.getId());
				if(todo.equals("amend")){
					ammendStrategic(strategic, loggedInUser, clientSideStrategic);
				}
				else if(todo.equals("save")){
					initiateStrategic(strategic, loggedInUser, clientSideStrategic);
				}else{
					Employee initiatedBy = strategic.getInitiatedBy(); 
					//					if(strategic.getStatus().equals("submitted")){//CHANGE
					if(strategic.getStatus().equals("submitted") || strategic.getPhase()==3){
						approveStrategic(strategic, loggedInUser, clientSideStrategic, initiatedBy);

					}else{
						strategic.setAuditableUnit(clientSideStrategic.getAuditableUnit());
						submitStrategic(strategic, loggedInUser, clientSideStrategic);
					}
				}
			}

			strategic.setRating(clientSideStrategic.getRating());
			strategic.setComments(clientSideStrategic.getComments());
			strategic.setAuditableUnit(clientSideStrategic.getAuditableUnit());
			strategic.setStrategicDepartments(clientSideStrategic.getStrategicDepartments());
			strategic.setDate(new Date());
			strategic.setTab(selectedTab);
			strategic.setYear(year);  // year Added
			session.saveOrUpdate(strategic);


			tr.commit();
			saveStrategicAudit(strategic);
			if(strategic.getPhase() ==1){
				saveDepartments(strategic);
			}


		}catch(Exception ex){
			tr.rollback();
			logger.warn(String.format("Exception occured in saveOneStrategic", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
	}

	private void saveDepartments(Strategic strategic) {
		Session session = null;
		try{
			session = sessionFactory.openSession();
			for(int i=0; i< strategic.getStrategicDepartments().size(); i++){
				strategic.getStrategicDepartments().get(i).setStrategic(strategic.getId());
				if(! strategicDepartmentAlreadySaved(strategic.getId(), strategic.getStrategicDepartments().get(i).getDepartment().getDepartmentId())){
					session.saveOrUpdate(strategic.getStrategicDepartments().get(i));
					session.flush();
				}
			}


		}catch(Exception ex){

			logger.warn(String.format("Exception occured in saveDepartmentsStrategic", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}

	}

	private boolean strategicDepartmentAlreadySaved(int strategicId, int departmentId) {
		Session session = null;
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(StrategicDepartments.class);
			crit.createAlias("department", "dept");
			crit.add(Restrictions.eq("strategic", strategicId));
			crit.add(Restrictions.eq("dept.departmentId", departmentId));

			if (crit.list().size()>0){
				return true;
			}


		}catch(Exception ex){

			logger.warn(String.format("Exception occured in strategicDepartmentAlreadySaved", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return false;
	}

	public void saveStrategicAudit(Strategic strategic){
		Session session = null;
		try{
			session = sessionFactory.openSession();
			Transaction tr = session.beginTransaction();
			StrategicAudit stragicAudit = new StrategicAudit();
			stragicAudit.setAcheivementDate(strategic.getAcheivementDate());
			stragicAudit.setApprovedBy(strategic.getApprovedBy());
			stragicAudit.setAssignedTo(strategic.getAssignedTo());
			stragicAudit.setAuditableUnit(strategic.getAuditableUnit());
			stragicAudit.setComments(strategic.getComments());
			stragicAudit.setInitiatedBy(strategic.getInitiatedBy());
			stragicAudit.setNextPhase(strategic.getNextPhase()+"");
			stragicAudit.setObjectiveOwner(strategic.getObjectiveOwner());
			stragicAudit.setPhase(strategic.getPhase()+"");
			stragicAudit.setRating(strategic.getRating());
			stragicAudit.setRelevantDepartment(strategic.getRelevantDepartment());
			RiskFactor riskFactor = (RiskFactor) session.get(RiskFactor.class,strategic.getRiskFactor().getRiskId());
			stragicAudit.setRiskFactor(riskFactor);
			stragicAudit.setStatus(strategic.getStatus());
			stragicAudit.setStrategicObjective(strategic.getStrategicObjective());
			stragicAudit.setDate(strategic.getDate());

			session.save(stragicAudit);
			tr.commit();
		}
		catch(Exception ex){
			logger.warn(String.format("Exception occured in saveStrategicAudit", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
	}

	private void approveStrategic(Strategic strategic, User loggedInUser,
			Strategic clientSideStrategic, Employee initiatedBy) {
		strategic.setStatus("initiated");
		strategic.setPhase(clientSideStrategic.getNextPhase());
		strategic.setAssignedTo(initiatedBy);
		strategic.setApprovedBy(loggedInUser.getEmployeeId());
		strategic.setAuditableUnit(clientSideStrategic.getAuditableUnit());
		strategic.setObjectiveOwner(clientSideStrategic.getObjectiveOwner());
		strategic.setStrategicObjective(clientSideStrategic.getStrategicObjective());
		strategic.setAcheivementDate(clientSideStrategic.getAcheivementDate());
		strategic.setRelevantDepartment(clientSideStrategic.getRelevantDepartment());
		strategic.setAudit(clientSideStrategic.isAudit());
	}

	private void submitStrategic(Strategic strategic, User loggedInUser, Strategic clientSideStrategic) {
		strategic.setStatus("submitted");
		Employee supervisor = loggedInUser.getEmployeeId().getReportingTo();
		strategic.setAssignedTo(supervisor);
		Employee emp = new Employee();
		emp.setEmployeeId(0);
		strategic.setApprovedBy(emp);
		strategic.setInitiatedBy(loggedInUser.getEmployeeId());
		strategic.setAudit(clientSideStrategic.isAudit());
	}

	private void initiateStrategic(Strategic strategic, User loggedInUser, Strategic clientSideStrategic) {
		strategic.setStatus("saved");
		Employee emp = new Employee();
		emp.setEmployeeId(0);
		strategic.setApprovedBy(emp);
		strategic.setInitiatedBy(loggedInUser.getEmployeeId());
		strategic.setAssignedTo(strategic.getInitiatedBy());
		strategic.setStrategicObjective(clientSideStrategic.getStrategicObjective());
		strategic.setAcheivementDate(clientSideStrategic.getAcheivementDate());
		strategic.setObjectiveOwner(clientSideStrategic.getObjectiveOwner());
		strategic.setRelevantDepartment(clientSideStrategic.getRelevantDepartment());
		strategic.setAudit(clientSideStrategic.isAudit());
	}

	private void ammendStrategic(Strategic strategic, User loggedInUser, Strategic clientSideStrategic) {
		strategic.setStatus("amend");
		Employee emp = new Employee();
		emp.setEmployeeId(0);
		strategic.setApprovedBy(emp);
		strategic.setAssignedTo(strategic.getInitiatedBy());
		strategic.setAudit(clientSideStrategic.isAudit());
	}



	public ArrayList<RiskFactor> fetchRiskFactors() {
		Session session = null;
		ArrayList<RiskFactor> riskFactors = new ArrayList<RiskFactor>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(RiskFactor.class);
			List rsList = crit.list();
			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				RiskFactor riskFactor =  (RiskFactor)it.next();
				riskFactors.add(riskFactor);
			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchRiskFactors", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return riskFactors;
	}

	public ArrayList<Strategic> fetchStrategic(HashMap<String, String> hm, int employeeId) {
		Session session = null;
		String phase = hm.get("phase");
		int tab = Integer.parseInt(hm.get("tab"));
		int year = Integer.parseInt(hm.get("year"));
		int phaseValue = Integer.parseInt(phase);
		ArrayList<Strategic> strategics = new ArrayList<Strategic>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Strategic.class);
			crit.createAlias("objectiveOwner", "owner");
			crit.createAlias("owner.cityId", "city");
			crit.createAlias("owner.countryId", "country");
			crit.createAlias("owner.userId", "user");
			crit.createAlias("owner.departmentId", "departmentOwner");
			crit.createAlias("owner.reportingTo", "ownRep");
			crit.createAlias("ownRep.reportingTo", "ownReprep");
			crit.createAlias("ownRep.userId", "ownRepuserId");
			crit.createAlias("relevantDepartment", "department");
			crit.createAlias("riskFactor", "risk");
			crit.createAlias("initiatedBy", "initiated");
			crit.createAlias("assignedTo", "assigned");
			crit.createAlias("assigned.userId", "assignedUser");
			crit.createAlias("assigned.reportingTo", "assignedReporting");
			crit.createAlias("initiated.userId", "initiatedUser");
			crit.createAlias("initiated.reportingTo", "initiatedReporting");
			crit.createAlias("initiatedReporting.userId", "initiatedReportingUser");
			crit.createAlias("approvedBy", "approveby");
			crit.createAlias("approveby.userId", "approvedUser");
			crit.createAlias("approveby.reportingTo", "approvedReposrting");
			crit.createAlias("approvedReposrting.userId", "approvedReposrtingUser");

			//			Disjunction disc = Restrictions.disjunction();
			//			disc.add(Restrictions.eq("assigned.employeeId", employeeId));
			//			disc.add(Restrictions.eq("initiated.employeeId", employeeId));
			//			
			//			crit.add(disc);
			crit.add(Restrictions.ge("phase", phaseValue));
			crit.add(Restrictions.ne("status", "deleted"));
			crit.add(Restrictions.eq("year", year));//    Year check added

			if(phaseValue == 1 || phaseValue == 2 ){
				crit.add(Restrictions.eq("tab", tab));
			}



			List rsList = crit.list();

			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				Strategic strategic =  (Strategic)it.next();
				strategic.setStrategicDepartments(fetchStrategicdepartments(strategic, session));
				strategic.setLoggedInUser(employeeId);
				////////////Dont sent those which are SAVED and are not belong to loggedInUser
				if(strategic.getStatus().equalsIgnoreCase("saved") && strategic.getInitiatedBy().getEmployeeId()!= employeeId){
					//DO not send this strategy 
				}else{
					strategics.add(strategic);
				}


			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchStrategics", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return strategics;
	}

	private ArrayList<StrategicDepartments> fetchStrategicdepartments(Strategic strategic, Session session) {
		ArrayList<StrategicDepartments> strategicDepartments = new ArrayList<StrategicDepartments>();
		try{
			Criteria crit = session.createCriteria(StrategicDepartments.class);
			crit.createAlias("department", "dept");
			crit.add(Restrictions.eq("strategic",strategic.getId() ));
			List rsList = crit.list();
			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				StrategicDepartments strategicDept =  (StrategicDepartments)it.next();
				strategicDepartments.add(strategicDept);
			}

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchStrategicdepartments", ex.getMessage()), ex);

		}
		finally{

		}
		return strategicDepartments;
	}

	public ArrayList<StrategicDTO> fetchSchdulingStrategic(HashMap<String, String> hm, int year) {
		Session session = null;

		ArrayList<StrategicDTO> strategics = new ArrayList<StrategicDTO>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Strategic.class);
			crit.add(Restrictions.eq("year", year));
			List rsList = crit.list();

			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				Strategic strategic =  (Strategic)it.next();
				StrategicDTO strategicDTO = new StrategicDTO();
				strategicDTO.setAuditableUnit(strategic.getAuditableUnit());
				strategicDTO.setStrategicObjective(strategic.getStrategicObjective());
				strategicDTO.setRiskRating(strategic.getRating());				
				strategicDTO.setDepartmentName(strategic.getRelevantDepartment().getDepartmentName());
				strategicDTO.setTab(strategic.getTab());
				strategicDTO.setDeptId(strategic.getRelevantDepartment().getDepartmentId());
				strategicDTO.setStrategicId( strategic.getId() );
				strategicDTO.setJobName( strategic.getStrategicObjective());

				strategics.add(strategicDTO);
			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchStrategics", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return strategics;
	}


	public ArrayList<RiskAssesmentDTO> fetchRiskAssesment(HashMap<String, String> hm, int employeeId, int year) {

		int tab = Integer.parseInt(hm.get("tab"));
		Session session = null;
		ArrayList<RiskAssesmentDTO> riskAssesmentDTOs = new ArrayList<RiskAssesmentDTO>();
		ArrayList<Strategic> strategics = new ArrayList<Strategic>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Strategic.class);
			crit.createAlias("objectiveOwner", "owner");
			crit.createAlias("owner.cityId", "city");
			crit.createAlias("owner.countryId", "country");
			crit.createAlias("owner.userId", "user");
			crit.createAlias("owner.departmentId", "departmentOwner");
			crit.createAlias("owner.reportingTo", "ownRep");
			crit.createAlias("ownRep.reportingTo", "ownReprep");
			crit.createAlias("ownRep.userId", "ownRepuserId");
			crit.createAlias("relevantDepartment", "department");
			crit.createAlias("riskFactor", "risk");
			crit.createAlias("initiatedBy", "initiated");
			crit.createAlias("assignedTo", "assigned");
			crit.createAlias("assigned.userId", "assignedUser");
			crit.createAlias("assigned.reportingTo", "reportingAssignedTo");
			crit.createAlias("initiated.userId", "initiatedUser");
			crit.createAlias("approvedBy", "approveby");
			crit.createAlias("approveby.userId", "approvedUser");
			crit.createAlias("approveby.reportingTo", "approvedReposrting");
			crit.createAlias("approvedReposrting.userId", "approvedReposrtingUser");
			//			crit.add(Restrictions.eq("assigned.employeeId", employeeId));
			//			Disjunction disc = Restrictions.disjunction();
			//			disc.add(Restrictions.eq("assigned.employeeId", employeeId));
			//			disc.add(Restrictions.eq("initiated.employeeId", employeeId));
			//			
			//			crit.add(disc);
			//			crit.add(Restrictions.eq("phase", "RiskAssesment"));
			crit.add(Restrictions.ge("phase", 2));
			crit.add(Restrictions.ne("status", "deleted"));
			crit.add(Restrictions.eq("tab", tab));
			crit.add(Restrictions.eq("year", year));//year added

			List rsList = crit.list();

			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				Strategic strategic =  (Strategic)it.next();
				strategic.setLoggedInUser(employeeId);
				RiskAssesmentDTO riskAssesmentDTO = new RiskAssesmentDTO();
				strategics.add(strategic);
				riskAssesmentDTO.setStrategic(strategic);
				riskAssesmentDTO.setStrategicRisks(fetchRiskStrategic(strategic.getId()));
				////////////Dont sent those which are SAVED and are not belong to loggedInUser
				if(strategic.getStatus().equalsIgnoreCase("saved") && strategic.getInitiatedBy().getEmployeeId()!= employeeId){
					//DO not send this strategy 
				}else{
					riskAssesmentDTOs.add(riskAssesmentDTO);
				}
			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchStrategics", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return riskAssesmentDTOs;
	}

	public ArrayList<StrategicRisk> fetchRiskStrategic(int strategicId){

		Session session = null;
		ArrayList<StrategicRisk> strategicRisks = new ArrayList<StrategicRisk>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(StrategicRisk.class);
			crit.createAlias("strategicId", "strategic");


			crit.createAlias("strategic.objectiveOwner", "owner");
			crit.createAlias("owner.cityId", "city");
			crit.createAlias("owner.countryId", "country");
			crit.createAlias("owner.userId", "user");
			crit.createAlias("owner.departmentId", "departmentOwner");
			crit.createAlias("owner.reportingTo", "ownRep");
			crit.createAlias("ownRep.reportingTo", "ownReprep");
			crit.createAlias("ownRep.userId", "ownRepuserId");
			crit.createAlias("strategic.relevantDepartment", "department");
			crit.createAlias("strategic.riskFactor", "risk");
			crit.createAlias("strategic.initiatedBy", "initiated");
			crit.createAlias("strategic.assignedTo", "assigned");
			crit.createAlias("assigned.userId", "assignedUser");
			crit.createAlias("initiated.userId", "initiatedUser");
			crit.createAlias("strategic.approvedBy", "approveby");
			crit.createAlias("approveby.userId", "approvedUser");
			crit.createAlias("approveby.reportingTo", "approvedReposrting");
			crit.createAlias("approvedReposrting.userId", "approvedReposrtingUser");

			crit.add(Restrictions.eq("strategic.id", strategicId));
			List rsList = crit.list();

			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				StrategicRisk strategicRisk =  (StrategicRisk)it.next();
				strategicRisks.add(strategicRisk);
			}


		}catch(Exception ex){
			logger.info(String.format("Exception occured in fetchRiskStrategic", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}

		return strategicRisks;

	}

	//	public ArrayList<StrategicRisk> fetchRiskStrategicBk(int employeeId){
	//
	//		Session session = null;
	//		ArrayList<StrategicRisk> strategicRisks = new ArrayList<StrategicRisk>();
	//		try{
	//			session = sessionFactory.openSession();
	//			Criteria crit = session.createCriteria(StrategicRisk.class);
	//			crit.createAlias("strategicId", "strategic");
	//
	//
	//			crit.createAlias("strategic.objectiveOwner", "owner");
	//			crit.createAlias("owner.cityId", "city");
	//			crit.createAlias("owner.countryId", "country");
	//			crit.createAlias("owner.userId", "user");
	//			crit.createAlias("owner.departmentId", "departmentOwner");
	//			crit.createAlias("owner.reportingTo", "ownRep");
	//			crit.createAlias("ownRep.reportingTo", "ownReprep");
	//			crit.createAlias("ownRep.userId", "ownRepuserId");
	//			crit.createAlias("strategic.relevantDepartment", "department");
	//			crit.createAlias("strategic.riskFactor", "risk");
	//			crit.createAlias("strategic.initiatedBy", "initiated");
	//			crit.createAlias("strategic.assignedTo", "assigned");
	//			crit.createAlias("assigned.userId", "assignedUser");
	//			crit.createAlias("initiated.userId", "initiatedUser");
	//			crit.createAlias("strategic.approvedBy", "approveby");
	//			crit.createAlias("approveby.userId", "approvedUser");
	//			crit.createAlias("approveby.reportingTo", "approvedReposrting");
	//			crit.createAlias("approvedReposrting.userId", "approvedReposrtingUser");
	//
	//			crit.add(Restrictions.eq("assigned.employeeId", employeeId));
	//			List rsList = crit.list();
	//
	//			for(Iterator it=rsList.iterator();it.hasNext();)
	//			{
	//				StrategicRisk strategicRisk =  (StrategicRisk)it.next();
	//				strategicRisks.add(strategicRisk);
	//			}
	//
	//
	//		}catch(Exception ex){
	//			logger.info(String.format("Exception occured in fetchRiskStrategic", ex.getMessage()), ex);
	//
	//		}
	//		finally{
	//			session.close();
	//		}
	//
	//		return strategicRisks;
	//
	//	}


	public void saveStrategicRisk(StrategicRisk strategicRisk){
		Session session = null;
		try{
			session = sessionFactory.openSession();

			Criteria crit = session.createCriteria(StrategicRisk.class);
			crit.createAlias("strategicId", "strategic");
			crit.createAlias("riskFactorId", "riskFactor");
			crit.add(Restrictions.eq("strategic.id", strategicRisk.getStrategicId().getId()));
			crit.add(Restrictions.eq("riskFactor.riskId", strategicRisk.getRiskFactorId().getRiskId()));
			if (crit.list().size()>0){
				Transaction tr1 = session.beginTransaction();
				StrategicRisk savedStrategicRisk = (StrategicRisk) crit.list().get(0);
				savedStrategicRisk.setRating(strategicRisk.getRating());
				savedStrategicRisk.setComments(strategicRisk.getComments());
				session.update(savedStrategicRisk);
				tr1.commit();
			}else{
				Transaction tr = session.beginTransaction();
				session.save(strategicRisk);
				tr.commit();
			}


		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchStrategics", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
	}

	public String saveRiskAssesment(ArrayList<StrategicRisk> strategicRisks, User loggedInUser, HashMap<String, String> hm) {
		String todo = hm.get("todo");
		String tab = hm.get("tab");
		int year =  Integer.parseInt(hm.get("year"));
		saveOneStrategic(strategicRisks.get(0).getStrategicId(), loggedInUser, todo, tab, year);

		for(int i=0; i< strategicRisks.size(); i++){
			saveStrategicRisk(strategicRisks.get(i));

		}
		return "risk assesment saved";

	}

	public String amendStrategic(Strategic strategic, User loggedInUser, int year) {
		Session session = null;
		try{

			session = sessionFactory.openSession();
			Transaction tr = (Transaction) session.beginTransaction();
			strategic.setInitiatedBy(loggedInUser.getEmployeeId());
			Employee supervisor = loggedInUser.getEmployeeId().getReportingTo();
			strategic.setInitiatedBy(supervisor);
			strategic.setYear(year);// Added year
			session.saveOrUpdate(strategic);
			tr.commit();

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in amendStrategic", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}

		return "strategic updated";
	}

	public String declineStrategic(int strategicId) {
		Session session = null;
		try{
			session = sessionFactory.openSession();
			Strategic strategic = (Strategic) session.get(Strategic.class, strategicId);
			Transaction tr = session.beginTransaction();
			strategic.setStatus("deleted");
			tr.commit();

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in declineStrategic", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return null;
	}

	public ArrayList<StrategicAudit> fetchStrategicAidit(int year) {
		Session session = null;
		ArrayList<StrategicAudit> strategicAudits = new ArrayList<StrategicAudit>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(StrategicAudit.class);
			crit.createAlias("objectiveOwner", "owner");
			crit.createAlias("owner.cityId", "city");
			crit.createAlias("owner.countryId", "country");
			crit.createAlias("owner.userId", "user");
			crit.createAlias("owner.departmentId", "departmentOwner");
			crit.createAlias("owner.reportingTo", "ownRep");
			crit.createAlias("ownRep.reportingTo", "ownReprep");
			crit.createAlias("ownRep.userId", "ownRepuserId");
			crit.createAlias("relevantDepartment", "department");
			crit.createAlias("riskFactor", "risk");
			crit.createAlias("initiatedBy", "initiated");
			crit.createAlias("assignedTo", "assigned");
			crit.createAlias("assigned.userId", "assignedUser");
			crit.createAlias("assigned.reportingTo", "assignedReporting");
			crit.createAlias("initiated.userId", "initiatedUser");
			crit.createAlias("initiated.reportingTo", "initiatedReporting");
			crit.createAlias("initiatedReporting.userId", "initiatedReportingUser");
			crit.createAlias("approvedBy", "approveby");
			crit.createAlias("approveby.userId", "approvedUser");
			crit.createAlias("approveby.reportingTo", "approvedReposrting");
			crit.createAlias("approvedReposrting.userId", "approvedReposrtingUser");
			crit.add(Restrictions.eq("year", year));
			List rsList = crit.list();

			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				StrategicAudit strategicAudit =  (StrategicAudit)it.next();
				strategicAudits.add(strategicAudit);
			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchStrategicAudits", ex.getMessage()), ex);
			System.out.println("Exception occured in fetchStrategicAudits "+ ex.getMessage());
		}
		finally{
			session.close();
		}
		return strategicAudits;
	}

	public DashBoardDTO fetchDashBoard(User loggedInUser, int year){

		Session session = null;
		DashBoardDTO dashBoardDTO = new DashBoardDTO();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Strategic.class);
			crit.createAlias("objectiveOwner", "owner");
			crit.createAlias("owner.cityId", "city");
			crit.createAlias("owner.countryId", "country");
			crit.createAlias("owner.userId", "user");
			crit.createAlias("owner.departmentId", "departmentOwner");
			crit.createAlias("owner.reportingTo", "ownRep");
			crit.createAlias("ownRep.reportingTo", "ownReprep");
			crit.createAlias("ownRep.userId", "ownRepuserId");
			crit.createAlias("relevantDepartment", "department");
			crit.createAlias("riskFactor", "risk");
			crit.createAlias("initiatedBy", "initiated");
			crit.createAlias("assignedTo", "assigned");
			crit.createAlias("assigned.userId", "assignedUser");
			crit.createAlias("assigned.reportingTo", "assignedReporting");
			crit.createAlias("initiated.userId", "initiatedUser");
			crit.createAlias("initiated.reportingTo", "initiatedReporting");
			crit.createAlias("initiatedReporting.userId", "initiatedReportingUser");
			crit.createAlias("approvedBy", "approveby");
			crit.createAlias("approveby.userId", "approvedUser");
			crit.createAlias("approveby.reportingTo", "approvedReposrting");
			crit.createAlias("approvedReposrting.userId", "approvedReposrtingUser");
			crit.add(Restrictions.eq("assigned.employeeId", loggedInUser.getEmployeeId().getEmployeeId()));
			crit.add(Restrictions.eq("year", year));

			List rsList = crit.list();

			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				Strategic strategic =  (Strategic)it.next();
				dashBoardDTO.getStrategics().add(strategic);

			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchDashBoard", ex.getMessage()), ex);
			System.out.println("Exception occured in fetchDashBoard "+ ex.getMessage());
		}
		finally{
			session.close();
		}

		return dashBoardDTO;
	}

	public ArrayList<Strategic> fetchFinalAuditables(int year) {

		Session session = null;
		ArrayList<Strategic> strategics = new ArrayList<Strategic>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Strategic.class);
			crit.createAlias("objectiveOwner", "owner");
			crit.createAlias("owner.cityId", "city");
			crit.createAlias("owner.countryId", "country");
			crit.createAlias("owner.userId", "user");
			crit.createAlias("owner.departmentId", "departmentOwner");
			crit.createAlias("owner.reportingTo", "ownRep");
			crit.createAlias("ownRep.reportingTo", "ownReprep");
			crit.createAlias("ownRep.userId", "ownRepuserId");
			crit.createAlias("relevantDepartment", "department");
			crit.createAlias("riskFactor", "risk");
			crit.createAlias("initiatedBy", "initiated");
			crit.createAlias("assignedTo", "assigned");
			crit.createAlias("assigned.userId", "assignedUser");
			crit.createAlias("assigned.reportingTo", "assignedReporting");
			crit.createAlias("initiated.userId", "initiatedUser");
			crit.createAlias("initiated.reportingTo", "initiatedReporting");
			crit.createAlias("initiatedReporting.userId", "initiatedReportingUser");
			crit.createAlias("approvedBy", "approveby");
			crit.createAlias("approveby.userId", "approvedUser");
			crit.createAlias("approveby.reportingTo", "approvedReposrting");
			crit.createAlias("approvedReposrting.userId", "approvedReposrtingUser");
			crit.add(Restrictions.ne("status", "deleted"));
			crit.add(Restrictions.eq("audit", true));
			crit.add(Restrictions.eq("year", year));//yr added

			List rsList = crit.list();

			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				Strategic strategic =  (Strategic)it.next();
				strategics.add(strategic);
			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchStrategics", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return strategics;
	}

	public boolean saveJobTimeEstimation(JobTimeEstimationDTO entity, ArrayList<SkillUpdateData> updateForSkills, int year) {

		Session session =null;
		try{

			session = sessionFactory.openSession();

			//JobTimeEstimationDTO prev = fetchJobTime(entity.getJobTimeEstimation().getJobId());

			Transaction tr = session.beginTransaction();

			//check if enough hours are available
			for ( int i = 0; i < updateForSkills.size(); i++)
			{
				updateSkillData( updateForSkills.get(i));
			}

			//checks if enough Employees are available to get the job done
			for ( int i = 0; i < entity.getResourceUse().size(); ++i) {

				if ( !checkEnoughResources( entity.getResourceUse().get(i) ) ) { 

					return false;
				}
			}
			JobTimeEstimation jobTimeEstimation = entity.getJobTimeEstimation();
			jobTimeEstimation.setYear(year);
			session.saveOrUpdate(jobTimeEstimation);

			//impl save or update.
			tr.commit();

			for( int i = 0; i < entity.getResourceUse().size(); ++i)
			{
				entity.getResourceUse().get(i).setJobId( entity.getJobTimeEstimation().getJobId());
				saveResourceNum(entity.getResourceUse().get(i));
			}


		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchStrategics", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}

		return true;
	}

	private boolean checkEnoughResources(ResourceUse resourceUse) {

		Session session = null;
		ArrayList<Employee> employees = new ArrayList<Employee>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Employee.class);
			crit.createAlias("cityId", "city");
			crit.createAlias("countryId", "countryId");
			crit.createAlias("departmentId", "department");
			crit.createAlias("userId", "user");

			crit.add(Restrictions.eq("department.departmentId", resourceUse.getSkill().getSkillId()));

			int resources = crit.list().size();

			return resources >= resourceUse.getNoOfResources() ;

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchEmployees", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return false;

	}

	private boolean updateSkillData(SkillUpdateData skillUpdateData) {

		Session session =null;
		try{

			//impl save or update
			session = sessionFactory.openSession();

			Criteria crit = session.createCriteria(Skills.class);


			crit.add( Restrictions.eq("skillId", skillUpdateData.getSkillId()) );

			List<Skills> rsList = crit.list();

			Transaction tr = session.beginTransaction();

			for(Iterator<Skills> it = rsList.iterator(); it.hasNext() ; )
			{
				Skills prev = ( ( Skills ) it.next() );

				if ( prev != null) //update
				{
					if ( prev.getAvailableHours() < skillUpdateData.getHoursToSubt())
					{
						System.out.println("More than available hours asked");
						return false;	
					}

					prev.setAvailableHours( prev.getAvailableHours() - skillUpdateData.getHoursToSubt() );

					session.saveOrUpdate(prev);
					tr.commit();

					return true;
				}

			}

			// jNow IT hours are 17 .. now add another resource with IT hours more than 17 
			// then it should stop the user..

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in saveResourceNum", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}

		return false;

	}

	private boolean saveResourceNum(ResourceUse entity) {

		Session session =null;
		try{

			//impl save or update
			session = sessionFactory.openSession();

			Criteria crit = session.createCriteria(ResourceUse.class);

			crit.createAlias("skillId", "skills");

			crit.add( Restrictions.eq("jobId", entity.getJobId()) );
			crit.add( Restrictions.eq("skills.skillId", entity.getSkill().getSkillId()) );

			List<ResourceUse> rsList = crit.list();

			Transaction tr = session.beginTransaction();

			for(Iterator<ResourceUse> it = rsList.iterator(); it.hasNext() ; )
			{

				ResourceUse prev = ( ( ResourceUse ) it.next() );

				if ( prev != null) //update
				{
					prev.setNoOfResources(entity.getNoOfResources());

					session.saveOrUpdate(prev);

					tr.commit();

					return true;
				}

				//save new


			}
			session.save(entity);
			tr.commit();

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in saveResourceNum", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}

		return false;
	}

	public ArrayList<ResourceUse> fetchResourceUseFor(int jobId, int year) {

		Session session = null;
		ArrayList<ResourceUse> records = new ArrayList<ResourceUse>();


		try
		{
			session = sessionFactory.openSession();

			Criteria crit = session.createCriteria(ResourceUse.class);
			// sould this be jobId// yes ::(
			crit.add( Restrictions.eq("jobId", jobId) );
			crit.add( Restrictions.eq("year", year) );
			crit.createAlias("skillId", "skill");
			List<ResourceUse> rsList = crit.list();



			for(Iterator<ResourceUse> it = rsList.iterator(); it.hasNext() ; )
			{

				records.add( ( ResourceUse ) it.next() );
			}
		}
		catch(Exception ex)
		{
			logger.warn(String.format("Exception occured in fetchResourceUseFor ", ex.getMessage()), ex);
		}
		finally
		{
			session.close();
		}
		// come here on debug..
		return records;	
	}

	public ArrayList<Employee> fetchEmployeesByDeptId(int deptId) {
		Session session = null;
		ArrayList<Employee> employees = new ArrayList<Employee>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Employee.class);
			crit.createAlias("departmentId", "department");
			crit.add(Restrictions.eq("department.departmentId", deptId ) );//lets see what it 
			crit.createAlias("cityId", "city");
			crit.createAlias("countryId", "countryId");

			crit.createAlias("userId", "user");
			List rsList = crit.list();// .. ?run
			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				Employee employee =  (Employee)it.next();
				employees.add(employee);
			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchEmployees", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return employees;
	}

	public void saveJobAndAreaOfExpertiseState(ArrayList<JobAndAreaOfExpertise> state) {


		Session session =null;
		try{


			for(int j = 0; j < state.size(); ++j)
			{
				session = sessionFactory.openSession();
				Transaction tr = session.beginTransaction();
				JobAndAreaOfExpertise jobAndAreaOfExpertise = state.get(j);
				Criteria crit = session.createCriteria(JobAndAreaOfExpertise.class);
				crit.createAlias("areaOfExpertiseId", "areaOfExpertise");
				crit.add(Restrictions.eq("jobId", jobAndAreaOfExpertise.getJobId()));
				crit.add(Restrictions.eq("areaOfExpertise.departmentId", jobAndAreaOfExpertise.getAreaOfExpertise().getDepartmentId()));

				if(crit.list().size()>0){
					List rsList = crit.list();
					JobAndAreaOfExpertise jobAndAreaOfExpertisePrevious = null;
					for(Iterator it=rsList.iterator();it.hasNext();)
					{
						jobAndAreaOfExpertisePrevious =  (JobAndAreaOfExpertise)it.next();
					}
					jobAndAreaOfExpertisePrevious.setIsChecked(jobAndAreaOfExpertise.getIsChecked());
					session.update(jobAndAreaOfExpertisePrevious);
				}
				else{

					session.save(jobAndAreaOfExpertise); 

				}
				tr.commit();
			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in saveResourceNum", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}


	}

	public ArrayList<JobAndAreaOfExpertise> fetchCheckBoxStateFor(int jobId) {
		//this is the fetch code .. in your auditPanel mehtiod
		Session session = null;
		ArrayList<JobAndAreaOfExpertise> checkBoxStates = new ArrayList<JobAndAreaOfExpertise>();

		try{

			session = sessionFactory.openSession();

			Criteria crit = session.createCriteria(JobAndAreaOfExpertise.class);
			crit.createAlias("areaOfExpertiseId", "areaOfExpertise");
			crit.add(Restrictions.eq("jobId", jobId ) );

			List rsList = crit.list();

			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				JobAndAreaOfExpertise j =  (JobAndAreaOfExpertise)it.next();
				checkBoxStates.add(j);
			}
		}
		catch(Exception ex)
		{
			logger.warn(String.format("Exception occured in fetchCheckBoxStateFor", ex.getMessage()), ex);

		}

		finally{
			session.close();
		}

		return checkBoxStates;

	}

	public boolean saveCreatedJob(JobCreationDTO job, int year) {

		Session session =null;
		try{

			session = sessionFactory.openSession();
			Transaction tr = session.beginTransaction();
			JobCreation jobCreation = job.getJob();
			jobCreation.setYear(year);
			session.saveOrUpdate(job.getJob());

			for ( int i = 0; i < job.getRelation().size(); ++i)
				saveJobEmployeeRelation(job.getRelation().get(i) );

			for ( int i = 0; i < job.getJobSkillRelation().size(); ++i)
				saveJobSkillRelation(job.getJobSkillRelation().get(i) );
			tr.commit();

		}catch(Exception ex){

			logger.warn(String.format("Exception occured in saving created job", ex.getMessage()), ex);
			return false;
		}
		finally{
			session.close();
		}

		return true;
	}

	private boolean saveJobEmployeeRelation(JobEmployeeRelation jobEmployeeRelation) {

		Session session =null;
		try{
			//job.setEmployeeId(new Employee());

			session = sessionFactory.openSession();
			Transaction tr = session.beginTransaction();
			session.saveOrUpdate(jobEmployeeRelation); 
			tr.commit();

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in saving emp relation job", ex.getMessage()), ex);
			return false;
		}
		finally{
			session.close();

		}

		return true;

	}



	private boolean saveJobSkillRelation(JobSkillRelation rel) {

		Session session =null;
		try{
			//job.setEmployeeId(new Employee());

			session = sessionFactory.openSession();
			Transaction tr = session.beginTransaction();
			session.saveOrUpdate(rel); 
			tr.commit();

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in saving skill relation job", ex.getMessage()), ex);
			return false;
		}
		finally{
			session.close();

		}

		return true;

	}


	public ArrayList<JobCreationDTO> fetchCreatedJobs( boolean getEmpRelation, boolean getSkillRelation, int year ) {
		Session session = null;
		ArrayList<JobCreationDTO> jobs = new ArrayList<JobCreationDTO>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(JobCreation.class);
			crit.add(Restrictions.eq("year", year));
			List rsList = crit.list();
			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				JobCreationDTO jobDTO = new JobCreationDTO();


				jobDTO.setJob( (JobCreation)it.next( ) ) ;

				//get emp associated with this job

				if ( getEmpRelation )
				{
					jobDTO.setRelation( fetchEmployeeJobRelations( jobDTO.getJob().getJobCreationId() ) );
				}

				if ( getSkillRelation )
				{
					jobDTO.setJobSkillRelation( fetchJobSkillRelations( jobDTO.getJob().getJobCreationId() ) );
				}

				//get skills reqd for this job
				jobs.add(jobDTO);
			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in  fetchCreatedJobs", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return jobs;
	}

	public JobCreationDTO fetchCreatedJob( int id, boolean getEmpRelation, boolean getSkillRelation, String idType ) {
		Session session = null;
		JobCreationDTO job = null;
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(JobCreation.class);

			crit.add(Restrictions.eq(idType, id ) );

			List rsList = crit.list();

			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				job = new JobCreationDTO();


				job.setJob( (JobCreation)it.next( ) ) ;

				//get emp associated with this job

				if ( getEmpRelation )
				{
					job.setRelation( fetchEmployeeJobRelations( job.getJob().getJobCreationId() ) );
				}

				if ( getSkillRelation )
				{
					job.setJobSkillRelation( fetchJobSkillRelations( job.getJob().getJobCreationId() ) );
				}

			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in  fetchCreatedJobs", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return job;
	}

	public JobCreation fetchCreatedJob( int jobcreationId ) {
		Session session = null;

		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(JobCreation.class);
			crit.add(Restrictions.eq("jobCreationId", jobcreationId));

			List rsList = crit.list();

			for(Iterator it=rsList.iterator();it.hasNext();)					

				return (JobCreation)it.next( ) ;


		}catch(Exception ex){
			logger.warn(String.format("Exception occured in  fetchCreatedJobs", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return null;
	}



	public ArrayList<JobEmployeeRelation> fetchEmployeeJobRelations(int jobCreationId) {
		Session session = null;
		ArrayList<JobEmployeeRelation> relations = new ArrayList<JobEmployeeRelation>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(JobEmployeeRelation.class);

			crit.createAlias("jobCreationId", "jobcreation");
			//crit.createAlias("jobCreationId", "jobcreation");
			crit.add(Restrictions.eq("jobcreation.jobCreationId", jobCreationId));

			crit.createAlias("employeeId", "employee");
			crit.createAlias("employee.countryId", "employeeCount");
			crit.createAlias("employee.reportingTo", "employeeRep");
			crit.createAlias("employeeRep.countryId", "employeeRCount");
			crit.createAlias("employee.userId", "employeeUser");

			List rsList = crit.list();
			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				JobEmployeeRelation job =  (JobEmployeeRelation)it.next();
				relations.add(job);
			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in  fetchCreatedJobs", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return relations;
	}

	private ArrayList<JobSkillRelation> fetchJobSkillRelations(int jobCreationId) {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<JobsOfEmployee> fetchEmployeesWithJobs() {
		Session session = null;

		ArrayList<JobsOfEmployee> employees = new  ArrayList<JobsOfEmployee>();

		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Employee.class);
			crit.createAlias("cityId", "city");
			crit.createAlias("countryId", "countryId");
			crit.createAlias("departmentId", "department");
			crit.createAlias("userId", "user");
			List rsList = crit.list();
			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				JobsOfEmployee jobForEmp = new JobsOfEmployee();

				Employee employee =  (Employee)it.next();
				HibernateDetachUtility.nullOutUninitializedFields(employee, HibernateDetachUtility.SerializationType.SERIALIZATION);

				jobForEmp.setEmployee(employee);

				jobForEmp.setJobs( getAllJobsForEmployee( employee.getEmployeeId()) );

				employees.add(jobForEmp);
			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchEmployees", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return employees;
	}

	private ArrayList<JobCreation> getAllJobsForEmployee(int employeeId) {
		Session session = null;
		ArrayList<JobCreation> relations = new ArrayList<JobCreation>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(JobEmployeeRelation.class);
			//crit.add(Restrictions.eq("employeeId", employeeId));
			crit.createAlias("employeeId", "employee");
			crit.createAlias("jobCreationId", "jobCreation");

			crit.add(Restrictions.eq("employee.employeeId", employeeId));
			crit.createAlias("employee.countryId", "employeeCount");
			crit.createAlias("employee.reportingTo", "employeeRep");
			crit.createAlias("employeeRep.countryId", "employeeRCount");
			crit.createAlias("employee.userId", "employeeUser");
			//			crit.createAlias("employeeUser.countryId", "employeeUCount");

			List rsList = crit.list();   

			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				JobEmployeeRelation job =  (JobEmployeeRelation)it.next();
				TimeLineDates dates = getMonthsInvolved(job.getJobCreationId().getStartDate(), job.getJobCreationId().getEndDate());
				job.getJobCreationId().setTimeLineDates(dates);
				HibernateDetachUtility.nullOutUninitializedFields(job, HibernateDetachUtility.SerializationType.SERIALIZATION);
				relations.add(job.getJobCreationId());


			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in  fetchCreatedJobs", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return relations;
	}

	public JobCreation updateEndDateForJob(int jobId, String startDate, String endDate) {


		Session session = null;
		JobCreation prevCreated = null;
		try{

			session = sessionFactory.openSession();

			prevCreated  = fetchCreatedJob(jobId);

			if ( prevCreated != null )
			{
				prevCreated.setEndDate(endDate);
				prevCreated.setStartDate(startDate);
				TimeLineDates dates = getMonthsInvolved(prevCreated.getStartDate(), prevCreated.getEndDate());
				prevCreated.setTimeLineDates(dates);
			}

			Transaction tr = session.beginTransaction();
			session.saveOrUpdate(prevCreated);
			tr.commit();



		}catch(Exception ex){
			logger.warn(String.format("Exception occured in  updating date", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return prevCreated;
	}

	public ArrayList<AuditEngagement> fetchAllAuditEngagement(int loggedInEmployee, int year  ) {
		Session session = null;
		ArrayList<AuditEngagement> records = new ArrayList<AuditEngagement>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(AuditEngagement.class);
			crit.add(Restrictions.eq("year", year));
			ArrayList<Integer> jobIds = fetchjobEmployee(loggedInEmployee);
			crit.createAlias("jobCreation", "jobcreation");
			//////////FETCHING ONLY JOBS OF LOGGEDIN EMPLOYEE
			if(jobIds.size()<=0){
				return null;
			}
			Disjunction disc = Restrictions.disjunction();
			for(int i=0; i< jobIds.size(); i++){
				disc.add(Restrictions.eq("jobcreation.jobCreationId", jobIds.get(i)));
			}
			crit.add(disc);

			//////////
			List rsList = crit.list();

			for(Iterator it=rsList.iterator();it.hasNext();)					

				records.add( (AuditEngagement)it.next( ) );


		}catch(Exception ex){
			logger.warn(String.format("Exception occured in  fetchAuditEngagement", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return records;
	}

	public AuditEngagement fetchAuditEngagement( int jobCreationId, int year ) {
		Session session = null;
		AuditEngagement record= null;

		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(AuditEngagement.class);

			crit.createAlias("jobCreation", "jobcreation");
			crit.add(Restrictions.eq("jobcreation.jobCreationId" , jobCreationId));
			crit.add(Restrictions.eq("year" , year));

			List rsList = crit.list();

			for(Iterator it=rsList.iterator();it.hasNext();)					

				return (AuditEngagement)it.next( ) ;


		}catch(Exception ex){
			logger.warn(String.format("Exception occured in  fetchAuditEngagement", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return record;
	}

	public boolean updateAuditEngagement(AuditEngagement e, String fieldToUpdate, int year) {

		Session session = null;

		try{

			session = sessionFactory.openSession();

			AuditEngagement prevCreated  = fetchAuditEngagement( e.getJobCreation().getJobCreationId(), year );

			if ( prevCreated != null )
			{
				//check which field  needs to be updated

				if ( "jobstatus".equals(fieldToUpdate))

					prevCreated.setJobStatus(e.getJobStatus());

				else if ( "objectives".equals(fieldToUpdate))
				{

					prevCreated.setAssignmentObj(e.getAssignmentObj());

					//else if ( "activityObj".equals(fieldToUpdate))

					prevCreated.setActivityObj(e.getActivityObj());

					prevCreated.setProcess( e.getProcess() );
				}
			}

			Transaction tr = session.beginTransaction();
			session.saveOrUpdate(prevCreated);
			tr.commit();

			return true;

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in  updating date", ex.getMessage()), ex);
			return false;
		}
		finally{
			session.close();
		}
	}

	private boolean saveAuditEngagement(AuditEngagement record) {

		Session session =null;
		try{

			session = sessionFactory.openSession();
			Transaction tr = session.beginTransaction();
			record.setCc("");
			record.setTo("");
			record.setAuditNotification("");
			record.setActivityObj("");
			record.setAssignmentObj("");
			record.setProcess("");
			session.save(record); 
			tr.commit();

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in saving skill relation job", ex.getMessage()), ex);
			return false;
		}
		finally{
			session.close();

		}

		return true;

	}


	public void syncAuditEngagementWithCreatedJobs(int loggedInEmployee, int year) {

		/* Ok, here we need to do.
		 * 
		 * Take id's of jobcreation. then check if auditengement has at least one record
		 * with that jobcreationid.
		 * 
		 * If not, then we take that jobcreation id and put that in auditengagement table
		 * 
		 */

		ArrayList<Integer> jobIds = fetchjobEmployee(loggedInEmployee);

		Session session = null;

		try{

			session = sessionFactory.openSession();

			Criteria critJobCreation = session.createCriteria(JobCreation.class, "job");
			critJobCreation.add(Restrictions.eq("year", year));
			critJobCreation.setProjection(Projections.property("job.jobCreationId"));

			List<Integer> idsJob = critJobCreation.list();

			Criteria critAuditEng = session.createCriteria(AuditEngagement.class, "auditEng");
			critAuditEng.add(Restrictions.eq("year", year));
			critAuditEng.createAlias("auditEng.jobCreation", "jobcreation");
			//////////FETCHING ONLY JOBS OF LOGGEDIN EMPLOYEE
			if(jobIds.size()<=0){
				return ;
			}
			Disjunction disc = Restrictions.disjunction();
			for(int i=0; i< jobIds.size(); i++){
				disc.add(Restrictions.eq("jobcreation.jobCreationId", jobIds.get(i)));
			}
			critAuditEng.add(disc);

			//////////
			critAuditEng.setProjection(Projections.property("jobcreation.jobCreationId"));

			List<Integer> idsEng = critAuditEng.list();

			//filter those jobcreationid's which are NOT in auditEngage table

			ArrayList<Integer> needToBeSynced = new ArrayList<Integer>();


			for( Iterator<Integer> i = idsJob.iterator(); i.hasNext(); )
			{
				int currentId = i.next();

				if ( ! idsEng.contains(currentId) ) 
					needToBeSynced.add(currentId);

			}

			for(int i =0; i < needToBeSynced.size(); ++i )
			{
				AuditEngagement newRecord = new AuditEngagement();

				newRecord.setJobStatus("Not Started");
				JobCreation jobCreation = new JobCreation();
				jobCreation.setJobCreationId( needToBeSynced.get(i) );
				newRecord.setJobCreation(jobCreation);

				saveAuditEngagement(newRecord);

			}

			//			Transaction tr = session.beginTransaction();
			//
			//			tr.commit();
			//			

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in  updating date", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}

	}

	private void saveRisk(Risk record) {

		Session session =null;
		try{

			session = sessionFactory.openSession();
			Transaction tr = session.beginTransaction();
			session.saveOrUpdate(record); 
			tr.commit();

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in saving skill relation job", ex.getMessage()), ex);

		}
		finally{
			session.close();

		}
	}

	public boolean saveRisks(ArrayList<Risk> records, int year) {


		for(Risk r : records)
		{
			r.setYear(year);
			saveRisk( r ) ;

		}

		return true;

	}

	public boolean sendEmail(String body, String sendTo, String cc, String subject) {

		final String username = "hyphenconsult@gmail.com";
		final String password = "ilzhkshpmtqduzuc";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		javax.mail.Session sessionMail = javax.mail.Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {



			Message message = new MimeMessage(sessionMail);
			message.setFrom(new InternetAddress("hyphenconsult@gmail.com"));
			if(cc.equals("")){
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(sendTo));
			}else{
				//////
				String addresses[] = { sendTo, cc};

				InternetAddress[] addressTo = new InternetAddress[addresses.length];
				for (int i = 0; i < addresses.length; i++)
				{
					addressTo[i] = new InternetAddress(addresses[i]);
				}

				message.setRecipients(Message.RecipientType.TO, addressTo);
				/////
			}

			message.setSubject(subject);
			message.setContent(body, "text/html");


			Transport.send(message);

			System.out.println("email sent");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}

		return false;
	}

	public ArrayList<Risk> fetchRisks(int auditEngId, int year) {
		Session session = null;
		ArrayList<Risk> record=  new ArrayList<Risk>();

		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Risk.class);

			//crit.createAlias("jobCreation", "jobcreation");

			crit.add(Restrictions.eq("auditEngageId" , auditEngId));
			crit.add(Restrictions.eq("year" , year));

			List rsList = crit.list();

			for(Iterator it=rsList.iterator();it.hasNext();)					

				record.add( (Risk)it.next( ) ) ;


		}catch(Exception ex){
			logger.warn(String.format("Exception occured in  fetchAuditEngagement", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return record;
	}

	public ArrayList<Employee> fetchEmpForThisJob(int selectedJobId) {
		Session session = null;
		ArrayList<Employee> employees = new ArrayList<Employee>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Employee.class);
			crit.createAlias("cityId", "city");
			crit.createAlias("countryId", "countryId");
			crit.createAlias("departmentId", "department");
			crit.createAlias("userId", "user");
			List rsList = crit.list();
			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				Employee employee =  (Employee)it.next();
				employees.add(employee);
			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchEmployees", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return employees;
	}

	public ArrayList<JobCreation> fetchJobs(int year) {
		Session session = null;
		ArrayList<JobCreation> jobsList = new ArrayList<JobCreation>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(JobCreation.class);
			crit.add(Restrictions.eq("year", year));
			List rsList = crit.list();
			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				JobCreation jobCreation =  (JobCreation)it.next();
				TimeLineDates dates = getMonthsInvolved(jobCreation.getStartDate(), jobCreation.getEndDate());
				jobCreation.setTimeLineDates(dates);
				jobCreation.setReportStatus(fetchJobStatus(jobCreation.getJobCreationId()));
				HibernateDetachUtility.nullOutUninitializedFields(jobCreation, HibernateDetachUtility.SerializationType.SERIALIZATION);

				jobsList.add(jobCreation);
			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchJobs", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return jobsList;
	}


	public TimeLineDates getMonthsInvolved(String startDate, String endDate)
	{

		TimeLineDates timeLineDates= new TimeLineDates();
		if ( startDate == null || endDate == null ){
			timeLineDates.setEndWeek(0);
			timeLineDates.setStartWeek(0);
			timeLineDates.setFormattedEndDate("");
			timeLineDates.setFormattedStartDat("");
			return timeLineDates;
		}

		Date end = null;
		Date start = null;
		SimpleDateFormat fmt = new SimpleDateFormat("dd, MM, yyyy");
		SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");

		try {
			end = fmt.parse(endDate);
			start = fmt.parse(startDate);
		} catch (ParseException e) {

			e.printStackTrace();
		}

		String formattedStartDate = format.format(start);
		String formattedEndDate = format.format(end);

		Calendar c = Calendar.getInstance();		
		c.setTime(start);
		int startWeek = c.get(Calendar.WEEK_OF_YEAR);
		c.setTime(end);
		int endWeek = c.get(Calendar.WEEK_OF_YEAR);

		timeLineDates.setEndWeek(endWeek);
		timeLineDates.setStartWeek(startWeek);
		timeLineDates.setFormattedEndDate(formattedEndDate);
		timeLineDates.setFormattedStartDat(formattedStartDate);
		return timeLineDates;


	}

	private int fetchJobStatus(int jobId){
		Session session = null;
		ArrayList<Exceptions> exceptions = new ArrayList<Exceptions>();
		int status=0;
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Exceptions.class);
			crit.add(Restrictions.eq("jobCreationId", jobId));
			List rsList = crit.list();
			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				Exceptions exception =  (Exceptions)it.next();
				exceptions.add(exception);
			}

			for(int i=0; i< exceptions.size(); i++){
				if(exceptions.get(i).getInitialStatus()==null || !exceptions.get(i).getInitialStatus().equalsIgnoreCase("Approved")){
					//					&&(	exceptions.get(i).getResponsiblePerson() == null  || exceptions.get(i).getResponsiblePerson().getEmployeeId()==0)){
					status = 1;
					break;
				}else if(exceptions.get(i).getManagementComments()==null || exceptions.get(i).getManagementComments().equals("")){
					status = 2;

				}else{
					if(status ==0 )status = 3;
				}
			}

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchJobs", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return status;

	}

	public ArrayList<Exceptions> fetchJobExceptions(int jobId, int year) {

		Session session = null;
		ArrayList<Exceptions> exceptions = new ArrayList<Exceptions>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Exceptions.class);
			crit.add(Restrictions.eq("year", year));
			crit.createAlias("responsiblePerson", "employee");
			crit.createAlias("employee.countryId", "employeeCount");
			crit.createAlias("employee.reportingTo", "employeeRep");
			crit.createAlias("employeeRep.countryId", "employeeRCount");
			crit.createAlias("employee.userId", "employeeUser");

			crit.createAlias("divisionHead", "divHead");
			crit.createAlias("divHead.countryId", "divHeadCount");
			crit.createAlias("divHead.reportingTo", "divHeadRep");
			//			crit.createAlias("divHead.countryId", "divHeadRCount");
			crit.createAlias("divHead.userId", "divHeadUser");
			if(jobId !=0){
				crit.add(Restrictions.eq("jobCreationId", jobId));
			}
			List rsList = crit.list();
			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				Exceptions exception =  (Exceptions)it.next();
				HibernateDetachUtility.nullOutUninitializedFields(exception, HibernateDetachUtility.SerializationType.SERIALIZATION);

				exceptions.add(exception);
			}
			if(jobId ==0){
				sendEmailNotifications(exceptions);
			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchEceptions", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return exceptions;
	}

	private void sendEmailNotifications(ArrayList<Exceptions> exceptions) {
		Date todaysDate  = new Date();
		for(int i=0; i< exceptions.size(); i++){

			long diff = getDatesDiff(todaysDate, exceptions.get(i).getImplementaionDate());



			if(diff>0 && diff <7 && exceptions.get(i).getEmailSent()==0){

				String date = exceptions.get(i).getImplementaionDate().toLocaleString();
				String implenDate =  date.substring(0, 13);
				String managementsMessage = "Dear " + exceptions.get(i).getResponsiblePerson().getEmployeeName() +" " +" <br></br> <br></br>"
						+ " Less than a week remaining In implemting the <br></br> <br></br>"
						+ " Exception :" + exceptions.get(i).getDetail()+"<br></br> <br></br>"
						+ " For Job :" + exceptions.get(i).getJobName()+"<br></br> <br></br>"
						+ " Due Date :" + implenDate+"<br></br> <br></br>";



				sendEmail(managementsMessage, exceptions.get(i).getResponsiblePerson().getEmail(),"","Exceptions Implementation Date");
				updateException(exceptions.get(i));
			}
		}
		//		sendEmail("test", "junaidp@gmail.com");

	}

	private void updateException(Exceptions exception) {
		Session session = null;
		try{
			session = sessionFactory.openSession();
			Transaction tr = session.beginTransaction();
			exception.setEmailSent(1);
			session.update(exception);
			tr.commit();
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in updating exception", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}

	}

	private long getDatesDiff(Date implementaionDate, Date todaysDate) {

		long daysBw = Days.daysBetween( new DateTime(implementaionDate), new DateTime(todaysDate) ).getDays();
		return daysBw;

	}

	public String sendException(Exceptions exception, int year) {
		Session session = null;
		try{
			session = sessionFactory.openSession();
			String jobName = fetchJobName(exception.getJobCreationId(), session);
			int auditHead  = fetchAuditHead(exception.getJobCreationId(), session);
			Transaction tr = session.beginTransaction();

			exception.setJobName(jobName);
			if(exception.getAuditHead()==0){
				exception.setAuditHead(auditHead);
			}
			exception.setYear(year);
			session.update(exception);
			tr.commit();
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in send exceptoin", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return "exception sent";	
	}

	private int fetchAuditHead(int jobId, Session session) {
		Criteria crit = session.createCriteria(JobCreation.class);
		crit.add(Restrictions.eq("jobCreationId", jobId));
		JobCreation jobCreation = (JobCreation) crit.list().get(0);
		return jobCreation.getAuditHead();
	}

	private String fetchJobName(int jobId, Session session){
		Criteria crit = session.createCriteria(JobCreation.class);
		crit.add(Restrictions.eq("jobCreationId", jobId));
		JobCreation jobCreation = (JobCreation) crit.list().get(0);
		return jobCreation.getJobName();


	}

	public ArrayList<Exceptions> fetchEmployeeExceptions(int employeeId, int jobId, int year) {
		Session session = null;
		ArrayList<Exceptions> exceptions = new ArrayList<Exceptions>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Exceptions.class);
			crit.add(Restrictions.eq("year", "year"));
			crit.createAlias("responsiblePerson", "employee");
			crit.add(Restrictions.eq("employee.employeeId", employeeId));
			crit.add(Restrictions.eq("initialStatus", "Approved"));
			crit.add(Restrictions.eq("jobCreationId", jobId));
			crit.createAlias("employee.countryId", "employeeCount");
			crit.createAlias("employee.reportingTo", "employeeRep");
			crit.createAlias("employeeRep.countryId", "employeeRCount");
			crit.createAlias("employee.userId", "employeeUser");
			List rsList = crit.list();
			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				Exceptions exception =  (Exceptions)it.next();
				HibernateDetachUtility.nullOutUninitializedFields(exception, HibernateDetachUtility.SerializationType.SERIALIZATION);

				exceptions.add(exception);
			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchEceptions", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return exceptions;
	}

	public void saveAuditStepAndExceptions(AuditStep auditstep, ArrayList<Exceptions> exceptions, int year) {

		//save audit step

		saveAuditStep( auditstep, exceptions, year );

		//		for ( Exceptions exception : exceptions)
		//			saveException(exception);

		//save execptions
	}

	//private void saveExcetion(Exceptions e) {
	//		
	//		Session session =null;
	//		try{
	//			
	//			session = sessionFactory.openSession();
	//			Transaction tr = session.beginTransaction();
	//			session.saveOrUpdate(e); 
	//			tr.commit();
	//
	//		}catch(Exception ex){
	//			logger.warn(String.format("Exception occured in saving skill relation job", ex.getMessage()), ex);
	//			
	//		}
	//		finally{
	//			session.close();
	//			
	//		}
	//		
	//	}

	private void saveException(Exceptions exception, Session session, int auditStepId) {

		try{

			Employee responsiblePerson = new Employee();
			responsiblePerson.setEmployeeId(0);
			Employee divisionHead = new Employee();
			divisionHead.setEmployeeId(0);
			exception.setDivisionHead(divisionHead);
			exception.setResponsiblePerson(responsiblePerson);
			exception.setAuditStep(auditStepId);
			session.saveOrUpdate(exception); 
			session.flush();

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in saving skill relation job", ex.getMessage()), ex);

		}
		finally{
			//			session.close();

		}

	}

	private void saveAuditStep(AuditStep auditstep, ArrayList<Exceptions> exceptions, int year) {

		Session session =null;
		Transaction tr = null;
		try{

			session = sessionFactory.openSession();
			tr = session.beginTransaction();
			auditstep.setYear(year);
			session.saveOrUpdate(auditstep); 
			session.flush();
			for ( Exceptions exception : exceptions)
				saveException(exception, session, auditstep.getAuditStepId());
			tr.commit();

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in saveAuditStep", ex.getMessage()), ex);
			tr.rollback();
		}
		finally{
			session.close();

		}

	}

	public AuditStep getSavedAuditStep(int jobid, int auditWorkId, int year) {
		Session session = null;
		AuditStep record = new AuditStep();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(AuditStep.class);
			//crit.createAlias("responsiblePerson", "responsible");
			crit.add(Restrictions.eq("year", year));
			crit.add(Restrictions.eq("jobId", jobid));
			crit.createAlias("auditWork", "audWork");
			crit.createAlias("audWork.jobCreationId", "jobCreationId");
			crit.add(Restrictions.eq("audWork.auditWorkId", auditWorkId));

			List rsList = crit.list();
			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				AuditStep auditStep = (AuditStep)it.next();
				auditStep.setExceptions(getSavedExceptions(auditStep.getAuditStepId(), year));
				HibernateDetachUtility.nullOutUninitializedFields(auditStep, HibernateDetachUtility.SerializationType.SERIALIZATION);

				return auditStep;				
			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchEceptions", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return record;
	}

	public ArrayList<Exceptions> getSavedExceptions(int auditStepId, int year) {
		Session session = null;
		ArrayList<Exceptions> records = new ArrayList<Exceptions>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Exceptions.class);
			crit.add(Restrictions.eq("year", year));
			crit.createAlias("responsiblePerson", "responsible");
			//			crit.createAlias("auditStep", "audStep");
			crit.add(Restrictions.eq("auditStep", auditStepId));
			//			crit.add(Restrictions.eq("jobCreationId", selectedJobId));
			crit.createAlias("divisionHead", "division");

			crit.createAlias("responsible.countryId", "employeeCount");
			crit.createAlias("responsible.reportingTo", "employeeRep");
			crit.createAlias("employeeRep.countryId", "employeeRCount");
			crit.createAlias("responsible.userId", "employeeUser");

			crit.createAlias("divisionHead.countryId", "employeeCount1");
			crit.createAlias("divisionHead.reportingTo", "employeeRep1");
			crit.createAlias("employeeRep1.countryId", "employeeRCount1");
			crit.createAlias("divisionHead.userId", "employeeUser1");


			List rsList = crit.list();
			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				Exceptions exception = (Exceptions)it.next();
				HibernateDetachUtility.nullOutUninitializedFields(exception, HibernateDetachUtility.SerializationType.SERIALIZATION);
				records.add(exception);	

			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchEceptions", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return records;
	}

	public void saveAuditWork(ArrayList<AuditWork> auditWorks, int year) {

		for ( AuditWork auditWork : auditWorks )
		{
			auditWork.setYear(year);
			saveAuditWork( auditWork );
		}
	}

	private void saveAuditWork(AuditWork auditWork) {

		Session session =null;
		try{

			session = sessionFactory.openSession();
			Transaction tr = session.beginTransaction();
			session.saveOrUpdate(auditWork); 
			tr.commit();

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in saving skill relation job", ex.getMessage()), ex);

		}
		finally{
			session.close();

		}

	}

	public void updateKickoffStatus(int auditEngId, int year) {
		Session session = null;

		try{

			session = sessionFactory.openSession();

			AuditEngagement prevCreated  = fetchAuditEngagement( auditEngId, year );

			if ( prevCreated != null )
			{
				prevCreated.setJobStatus("In Progress");


				Transaction tr = session.beginTransaction();
				session.saveOrUpdate(prevCreated);
				tr.commit();
			}

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in  updating date", ex.getMessage()), ex);
		}
		finally{
			session.close();

		}	
	}

	public ArrayList<Exceptions> fetchAuditHeadExceptions(int auditHeadId, int selectedJob, int year) {
		Session session = null;
		ArrayList<Exceptions> exceptions = new ArrayList<Exceptions>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Exceptions.class);
			crit.add(Restrictions.eq("year", year));
			crit.createAlias("responsiblePerson", "employee");
			crit.add(Restrictions.eq("auditHead", auditHeadId));
			crit.add(Restrictions.eq("jobCreationId", selectedJob));
			crit.add(Restrictions.eq("jobCreationId", selectedJob));

			crit.createAlias("employee.countryId", "employeeCount");
			crit.createAlias("employee.reportingTo", "employeeRep");
			crit.createAlias("employeeRep.countryId", "employeeRCount");
			crit.createAlias("employee.userId", "employeeUser");
			crit.add(Restrictions.ne("managementComments", ""));
			List rsList = crit.list();
			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				Exceptions exception =  (Exceptions)it.next();
				HibernateDetachUtility.nullOutUninitializedFields(exception, HibernateDetachUtility.SerializationType.SERIALIZATION);

				exceptions.add(exception);
			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchAuditHeadEx", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return exceptions;
	}

	public ArrayList<AuditWork> fetchAuditWorkRows(int jocreationid) {

		Session session = null;
		ArrayList<AuditWork> rows = new ArrayList<AuditWork>();
		try{
			session = sessionFactory.openSession();

			Criteria crit = session.createCriteria(AuditWork.class);
			crit.createAlias("jobCreationId", "jobCreation");
			crit.add(Restrictions.eq("jobCreation.jobCreationId", jocreationid));

			List rsList = crit.list();

			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				AuditWork row =  (AuditWork)it.next();
				HibernateDetachUtility.nullOutUninitializedFields(row, HibernateDetachUtility.SerializationType.SERIALIZATION);

				rows.add(row);
			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchAuditWorkRows", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return rows;	}

	public ArrayList<AuditWork> fetchApprovedAuditWorkRows(int selectedJobId) {
		Session session = null;
		ArrayList<AuditWork> rows = new ArrayList<AuditWork>();
		try{
			session = sessionFactory.openSession();

			Criteria crit = session.createCriteria(AuditWork.class);
			crit.createAlias("jobCreationId", "jobCreation");
			crit.add(Restrictions.eq("jobCreation.jobCreationId", selectedJobId));
			crit.add(Restrictions.eq("status", InternalAuditConstants.APPROVED));

			List rsList = crit.list();

			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				AuditWork row =  (AuditWork)it.next();
				HibernateDetachUtility.nullOutUninitializedFields(row, HibernateDetachUtility.SerializationType.SERIALIZATION);

				rows.add(row);
			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchApprovedExceptions", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return rows;	}


	public ArrayList<Integer> fetchjobEmployeeWithApprovedAuditStep(int employeeId){

		Session session = null;
		ArrayList<Integer> employeeJobs = new ArrayList<Integer>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(JobEmployeeRelation.class);
			crit.createAlias("employeeId", "employee");
			crit.add(Restrictions.eq("employee.employeeId", employeeId));
			List rsList = crit.list();
			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				JobEmployeeRelation jobEmployeeRelation =  (JobEmployeeRelation)it.next();
				int job = jobEmployeeRelation.getJobCreationId().getJobCreationId();
				if(isJobAuditStepApproved(job)){/////// FOR JOBS WHICH AUDIT STEP IS APPROVED
					employeeJobs.add(job);
				}
				HibernateDetachUtility.nullOutUninitializedFields(jobEmployeeRelation, HibernateDetachUtility.SerializationType.SERIALIZATION);

			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchJobs", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return employeeJobs;
	}

	public ArrayList<Integer> fetchjobEmployee(int employeeId){

		Session session = null;
		ArrayList<Integer> employeeJobs = new ArrayList<Integer>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(JobEmployeeRelation.class);
			crit.createAlias("employeeId", "employee");
			crit.add(Restrictions.eq("employee.employeeId", employeeId));
			List rsList = crit.list();
			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				JobEmployeeRelation jobEmployeeRelation =  (JobEmployeeRelation)it.next();
				int job = jobEmployeeRelation.getJobCreationId().getJobCreationId();
				employeeJobs.add(job);

				HibernateDetachUtility.nullOutUninitializedFields(jobEmployeeRelation, HibernateDetachUtility.SerializationType.SERIALIZATION);

			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchJobs", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return employeeJobs;
	}

	public ArrayList<JobCreation> fetchjobEmployeeEntity(int employeeId){

		Session session = null;
		ArrayList<JobCreation> employeeJobs = new ArrayList<JobCreation>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(JobEmployeeRelation.class);
			crit.createAlias("employeeId", "employee");
			crit.createAlias("jobCreationId", "jobCreation");
			crit.add(Restrictions.eq("employee.employeeId", employeeId));
			List rsList = crit.list();
			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				JobEmployeeRelation jobEmployeeRelation =  (JobEmployeeRelation)it.next();
				JobCreation job = jobEmployeeRelation.getJobCreationId();
				employeeJobs.add(job);

				HibernateDetachUtility.nullOutUninitializedFields(jobEmployeeRelation, HibernateDetachUtility.SerializationType.SERIALIZATION);

			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchJobs", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return employeeJobs;
	}

	public boolean isJobAuditStepApproved(int jobId){
		Session session = null;
		boolean jobAuditStepApproved = false;
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(AuditStep.class);
			crit.add(Restrictions.eq("jobId", jobId));
			List rsList = crit.list();
			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				jobAuditStepApproved = true;
				AuditStep auditStep =  (AuditStep)it.next();
				int status = auditStep.getStatus();
				if(status != InternalAuditConstants.APPROVED){
					jobAuditStepApproved = false;
				}
				HibernateDetachUtility.nullOutUninitializedFields(auditStep, HibernateDetachUtility.SerializationType.SERIALIZATION);

			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchJobs", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return jobAuditStepApproved;

	}

	// FETCH EMPLYEE JOBS FOR which audit steps are approved
	public ArrayList<JobCreation> fetchEmployeeJobs(Employee loggedInEmployee) {
		Session session = null;
		ArrayList<Integer> jobIds = fetchjobEmployeeWithApprovedAuditStep(loggedInEmployee.getEmployeeId());
		ArrayList<JobCreation> jobsList = new ArrayList<JobCreation> ();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(JobCreation.class);
			Disjunction disc = Restrictions.disjunction();
			if(jobIds.size()<=0 && loggedInEmployee.getFromInternalAuditDept().equalsIgnoreCase("yes")){
				return null;
			}
			for(int i=0; i< jobIds.size(); i++){
				disc.add(Restrictions.eq("jobCreationId", jobIds.get(i)));
			}
			crit.add(disc);
			List rsList = crit.list();
			for(Iterator it=rsList.iterator();it.hasNext();)
			{
				JobCreation jobCreation =  (JobCreation)it.next();
				jobCreation.setReportStatus(fetchJobStatus(jobCreation.getJobCreationId()));
				HibernateDetachUtility.nullOutUninitializedFields(jobCreation, HibernateDetachUtility.SerializationType.SERIALIZATION);

				jobsList.add(jobCreation);
			}
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchJobs", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return jobsList;
	}

	public String saveAuditNotification(int auditEngagementId, String message,
			String to, String cc, int year) {
		Session session = null;
		try{
			session = sessionFactory.openSession();
			AuditEngagement auditEngagement =  (AuditEngagement) session.get(AuditEngagement.class, auditEngagementId);
			auditEngagement.setAuditNotification(message);
			auditEngagement.setTo(to);
			auditEngagement.setCc(cc);
			auditEngagement.setYear(year);
			session.flush();
		}catch(Exception ex){
			logger.warn(String.format("Exception occured in saveAuditNotification", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		sendEmail(message, to, cc, "Audit Notification");
		return "Audit Notification saved";
	}

	public int fetchNumberofPlannedJobs(int year) {
		Session session = null;
		int num =0;
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Strategic.class);
			crit.add(Restrictions.eq("year", year));
			crit.setProjection(Projections.rowCount());
			num = (Integer) crit.uniqueResult();

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchNumberofPlannedJobs", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return num;
	}

	public int fetchNumberofInProgressJobs(int year) {
		Session session = null;
		int num =0;
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(AuditEngagement.class);
			crit.add(Restrictions.eq("year", year));
			crit.add(Restrictions.eq("jobStatus", "In Progress"));
			crit.setProjection(Projections.rowCount());
			num = (Integer) crit.uniqueResult();

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchNumberofInProgressJobs", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return num;
	}

	public int fetchNumberofCompletedJobs(int year) {
		Session session = null;
		int num =0;
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(JobCreation.class);
			crit.add(Restrictions.eq("year", year));
			crit.add(Restrictions.eq("reportStatus", 3));
			crit.setProjection(Projections.rowCount());
			num = (Integer) crit.uniqueResult();

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchNumberofComletedJobs", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return num;
	}

	public ArrayList<String> fetchJobsKickOffNextWeek(int year) {
		Session session = null;
		ArrayList<String> jobs =new ArrayList<String>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(JobCreation.class);



			crit.add(Restrictions.eq("year", year));
			List rsList = crit.list();
			for(Iterator it=rsList.iterator();it.hasNext();){
				JobCreation job = (JobCreation) it.next();
				DateFormat df = new SimpleDateFormat("MM/dd/yy");
				Date date = df.parse(job.getStartDate());
				DateTime enteredDate = new DateTime(date); 
				DateTime currentDate = new DateTime(); //current date
				int week = enteredDate.getWeekOfWeekyear();
				int currentWeek = currentDate.getWeekOfWeekyear();
				if(week-currentWeek == 1 ){
					jobs.add(job.getJobName());
				}
			}

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchNumberofComletedJobs", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return jobs;
	}

	public int fetchNumberOfAufitObservations(int year) {
		Session session = null;
		int num =0;
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Exceptions.class);
			crit.add(Restrictions.eq("year", year));
			crit.setProjection(Projections.rowCount());
			num = (Integer) crit.uniqueResult();

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchNumberOfAufitObservations", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return num;
	}

	public int fetchNumberOfExceptionsInProgress(int year) {
		Session session = null;
		int num =0;
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Exceptions.class);
			crit.add(Restrictions.eq("year", year));
			crit.add(Restrictions.ne("finalStatus", "Approved"));
			//			crit.add(Restrictions.isNotEmpty("managementComments"));
			crit.add(Restrictions.isNotNull("managementComments"));
			crit.setProjection(Projections.rowCount());
			num = (Integer) crit.uniqueResult();

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchNumberOfAufitObservations", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return num;
	}

	public int fetchNumberOfExceptionsImplemented(int year) {
		Session session = null;
		int num =0;
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Exceptions.class);
			crit.add(Restrictions.eq("year", year));
			crit.add(Restrictions.eq("isImplemented", 1));
			crit.setProjection(Projections.rowCount());
			num = (Integer) crit.uniqueResult();

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchNumberOfAufitObservations", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return num;
	}

	public int fetchNumberOfExceptionsOverdue(int year) {
		Session session = null;
		int num =0;
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Exceptions.class);
			//			crit.add(Restrictions.eq("year", year));
			crit.add(Restrictions.eq("isImplemented", 0));
			crit.add(Restrictions.le("implementaionDate", new Date()));
			crit.setProjection(Projections.rowCount());
			num = (Integer) crit.uniqueResult();

		}catch(Exception ex){
			logger.warn(String.format("Exception occured in fetchNumberOfAufitObservations", ex.getMessage()), ex);

		}
		finally{
			session.close();
		}
		return num;
	}

	public ArrayList<String> fetchEmployeesAvilbleForNext2Weeks(int year) {
		Session session = null;
		int num =0;
		ArrayList<String> employees = new ArrayList<String>();
		try{
			session = sessionFactory.openSession();
			Criteria crit = session.createCriteria(Employee.class);
			crit.add(Restrictions.ne("employeeId", 0));
			List rsList = crit.list();
			for(Iterator it=rsList.iterator();it.hasNext();)
			{

				Employee employee =  (Employee)it.next();
				ArrayList<JobCreation> jobs = fetchjobEmployeeEntity(employee.getEmployeeId());
				if(jobs.size()<1){
					employees.add(employee.getEmployeeName());
				}
				else{
					boolean available = true;
					for(int i=0; i< jobs.size(); i++){
						DateFormat df = new SimpleDateFormat("MM/dd/yy");
						Date startDate = df.parse(jobs.get(i).getStartDate());
						Date endDate = df.parse(jobs.get(i).getEndDate());
						DateTime start = new DateTime(startDate); 
						DateTime end = new DateTime(endDate); 
						int startWeek = start.getWeekOfWeekyear();
						int endWeek = end.getWeekOfWeekyear();
						DateTime currentDate = new DateTime(); 
						int currentWeek = currentDate.getWeekOfWeekyear();
						int nextWeek = currentWeek+1;
						if(startWeek == currentWeek || startWeek == nextWeek || endWeek == currentWeek || endWeek == nextWeek){
							available = false;
							break;
						}else if(startWeek < currentWeek && endWeek > nextWeek){
							available = false;
							break;
						}
					}
					if(available){
						employees.add(employee.getEmployeeName());
					}
				}
			}

	}catch(Exception ex){
		logger.warn(String.format("Exception occured in fetchNumberOfAufitObservations", ex.getMessage()), ex);

	}
	finally{
		session.close();
	}
	return employees;
}

}
