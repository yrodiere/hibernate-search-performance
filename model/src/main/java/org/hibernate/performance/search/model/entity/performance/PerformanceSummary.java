package org.hibernate.performance.search.model.entity.performance;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.performance.search.model.entity.Employee;

@Entity
public class PerformanceSummary {

	@Id
	@GeneratedValue
	private Integer id;

	@ManyToOne
	private Employee employee;

	private Integer year;

	private Integer maxScore;

	private Integer employeeScore;

	private PerformanceSummary() {
	}

	public PerformanceSummary(Employee employee, Integer year, Integer maxScore, Integer employeeScore) {
		this.employee = employee;
		this.year = year;
		this.maxScore = maxScore;
		this.employeeScore = employeeScore;
	}

	public Integer getYear() {
		return year;
	}
}
