package com.workzen.repository;

import com.workzen.entity.Employee;
import com.workzen.entity.Payroll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {

  List<Payroll> findByEmployee(Employee employee);

  Optional<Payroll> findByEmployeeAndSalaryMonth(Employee employee, LocalDate salaryMonth);

  @Query("SELECT p FROM Payroll p WHERE p.employee = :employee AND " +
      "YEAR(p.salaryMonth) = :year ORDER BY p.salaryMonth DESC")
  List<Payroll> findPayrollsByYear(@Param("employee") Employee employee,
      @Param("year") int year);

  @Query("SELECT p FROM Payroll p WHERE YEAR(p.salaryMonth) = :year AND " +
      "MONTH(p.salaryMonth) = :month")
  List<Payroll> findPayrollsByMonth(@Param("year") int year,
      @Param("month") int month);

  @Query("SELECT p FROM Payroll p WHERE p.isProcessed = :isProcessed")
  List<Payroll> findByProcessedStatus(@Param("isProcessed") Boolean isProcessed);

  Page<Payroll> findByEmployee(Employee employee, Pageable pageable);

  @Query("SELECT p FROM Payroll p WHERE p.salaryMonth BETWEEN :startDate AND :endDate")
  Page<Payroll> findBySalaryMonthBetween(@Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      Pageable pageable);
}
