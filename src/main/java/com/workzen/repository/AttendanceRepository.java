package com.workzen.repository;

import com.workzen.entity.Attendance;
import com.workzen.entity.Employee;
import com.workzen.enums.AttendanceStatus;
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
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    Optional<Attendance> findByEmployeeAndDate(Employee employee, LocalDate date);
    
    List<Attendance> findByEmployeeAndDateBetween(Employee employee, LocalDate startDate, LocalDate endDate);
    
    List<Attendance> findByDateAndStatus(LocalDate date, AttendanceStatus status);
    
    @Query("SELECT a FROM Attendance a WHERE a.date = :date ORDER BY a.employee.firstName")
    List<Attendance> findByDateOrderByEmployeeName(@Param("date") LocalDate date);
    
    @Query("SELECT a FROM Attendance a WHERE a.employee = :employee AND " +
           "YEAR(a.date) = :year AND MONTH(a.date) = :month ORDER BY a.date")
    List<Attendance> findMonthlyAttendance(@Param("employee") Employee employee, 
                                         @Param("year") int year, 
                                         @Param("month") int month);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.employee = :employee AND " +
           "a.date BETWEEN :startDate AND :endDate AND a.status = :status")
    long countByEmployeeAndDateRangeAndStatus(@Param("employee") Employee employee,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate,
                                            @Param("status") AttendanceStatus status);
    
    @Query("SELECT AVG(a.workHours) FROM Attendance a WHERE a.employee = :employee AND " +
           "a.date BETWEEN :startDate AND :endDate AND a.workHours > 0")
    Double getAverageWorkHours(@Param("employee") Employee employee,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate);
    
    Page<Attendance> findByEmployee(Employee employee, Pageable pageable);
    
    @Query("SELECT a FROM Attendance a WHERE a.date BETWEEN :startDate AND :endDate")
    Page<Attendance> findByDateRange(@Param("startDate") LocalDate startDate, 
                                   @Param("endDate") LocalDate endDate, 
                                   Pageable pageable);
}
