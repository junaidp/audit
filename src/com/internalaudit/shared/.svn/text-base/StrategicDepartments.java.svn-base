package com.internalaudit.shared;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

	
	@Entity

	@Table(name="strategicdepartments")
	public class StrategicDepartments   implements Serializable {

		@Id 
		@GeneratedValue(strategy=GenerationType.AUTO)
		@Column(name="strategicDepartmentId")
		private int strategicDepartmentId;
		
		
		private int strategic;
		
		@JoinColumn(name = "department")
		@ManyToOne(fetch = FetchType.LAZY)
		private Department department;

		public int getStrategicDepartmentId() {
			return strategicDepartmentId;
		}

		public void setStrategicDepartmentId(int strategicDepartmentId) {
			this.strategicDepartmentId = strategicDepartmentId;
		}

		
		public Department getDepartment() {
			return department;
		}

		public void setDepartment(Department department) {
			this.department = department;
		}

		public int getStrategic() {
			return strategic;
		}

		public void setStrategic(int strategic) {
			this.strategic = strategic;
		}

}
