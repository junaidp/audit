package com.internalaudit.shared;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity

@Table(name="jobcreation")
public class JobCreation implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="jobcreationid")
	private int jobCreationId;
	
	@Column(name="jobid")
	private int jobId;
	
	@Column(name="jobName")
	private String jobName;
	
	@Column(name="weeks")
	private int estimatedWeeks;  // Simple Text : whatever is in the TextBox'Estimated Weeks' .. just put that in this field in Db as it is ..
	
	@Column(name="domain")
	private String domainText;  ////// Simple text :  "Strategic" in this case for job "penetrate.."
	
	@Column(name="relevantdept")
	private String relevantDept; //// Simple Text : "Business"  in this case for job "penetrate.."
	
	@Column(name="riskrating")
	private String riskRating;  /// Simple Text :  "Low/High"  whatever is in the TextBox'Risk Rating' .. just put that in this field in Db as it is ..

	@Column(name="technical")
	private String technical;// Simple Text : whatever is in the TextBox'Technical' .. just put that in this field in Db as it is ..
	
	@Column(name="startdate")
	private String startDate;

	@Column(name="enddate")
	private String endDate;
	
	@Column(name="reportStatus")
	private int reportStatus;
	
	@Column(name="auditHead")
	private int auditHead;
	
	@Column(name = "year")
	private int year;
	
	@Transient
	private TimeLineDates timeLineDates;

	
	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public int getEstimatedWeeks() {
		return estimatedWeeks;
	}

	public void setEstimatedWeeks(int estimatedWeeks) {
		this.estimatedWeeks = estimatedWeeks;
	}


	public int getJobId() {
		return jobId;
	}


	public void setJobId(int jobId) {
		this.jobId = jobId;
	}


	public int getJobCreationId() {
		return jobCreationId;
	}


	public void setJobCreationId(int jobCreationId) {
		this.jobCreationId = jobCreationId;
	}

	public String getDomainText() {
		return domainText;
	}


	public void setDomainText(String domainText) {
		this.domainText = domainText;
	}


	public String getRelevantDept() {
		return relevantDept;
	}


	public void setRelevantDept(String relevantDept) {
		this.relevantDept = relevantDept;
	}


	public String getRiskRating() {
		return riskRating;
	}


	public void setRiskRating(String riskRating) {
		this.riskRating = riskRating;
	}

	public String getTechnical() {
		return technical;
	}

	public void setTechnical(String technical) {
		this.technical = technical;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public int getReportStatus() {
		return reportStatus;
	}

	public void setReportStatus(int reportStatus) {
		this.reportStatus = reportStatus;
	}

	public int getAuditHead() {
		return auditHead;
	}

	public void setAuditHead(int auditHead) {
		this.auditHead = auditHead;
	}

	
	public TimeLineDates getTimeLineDates() {
		return timeLineDates;
	}

	public void setTimeLineDates(TimeLineDates timeLineDates) {
		this.timeLineDates = timeLineDates;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
	
}