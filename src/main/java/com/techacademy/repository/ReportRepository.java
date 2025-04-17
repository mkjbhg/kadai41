package com.techacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;


//4.15

	import java.time.LocalDate;
	import java.util.Optional;
	import org.springframework.data.jpa.repository.JpaRepository;
	import com.techacademy.entity.Report;
	import com.techacademy.entity.Employee;
	import java.util.List;

	public interface ReportRepository extends JpaRepository<Report, Integer> {

// employeeとreportDateが一致するレコードを検索する

		List<Report> findByReportDateAndEmployee(LocalDate date, Employee employee);
		List<Report> findByEmployee(Employee employee);

	}
