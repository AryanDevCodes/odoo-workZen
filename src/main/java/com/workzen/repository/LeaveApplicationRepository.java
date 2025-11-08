package com.workzen.repository;

import com.workzen.entity.Employee;
import com.workzen.entity.LeaveApplication;
import com.workzen.entity.LeaveApplication.LeaveStatus;
import com.workzen.enums.LeaveType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {

  List<LeaveApplication> findByEmployee(Employee employee);

  List<LeaveApplication> findByEmployeeAndStatus(Employee employee, LeaveStatus status);

  List<LeaveApplication> findByStatus(LeaveStatus status);

  @Query("SELECT l FROM LeaveApplication l WHERE l.employee = :employee AND " +
      "l.startDate <= :endDate AND l.endDate >= :startDate")
  List<LeaveApplication> findOverlappingLeaves(@Param("employee") Employee employee,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

  @Query("SELECT l FROM LeaveApplication l WHERE l.employee = :employee AND " +
      "YEAR(l.startDate) = :year ORDER BY l.startDate DESC")
  List<LeaveApplication> findLeavesByYear(@Param("employee") Employee employee,
      @Param("year") int year);

  @Query("SELECT l FROM LeaveApplication l WHERE l.approvedBy = :approver AND " +
      "l.status = :status ORDER BY l.createdAt DESC")
  List<LeaveApplication> findPendingApprovals(@Param("approver") Employee approver,
      @Param("status") LeaveStatus status);

  Page<LeaveApplication> findByEmployee(Employee employee, Pageable pageable);

  Page<LeaveApplication> findByStatus(LeaveStatus status, Pageable pageable);

  @Query("SELECT COUNT(l) FROM LeaveApplication l WHERE l.employee = :employee AND " +
      "l.leaveType = :leaveType AND l.status = :status AND " +
      "YEAR(l.startDate) = :year")
  long countByEmployeeAndLeaveTypeAndStatusAndYear(@Param("employee") Employee employee,
      @Param("leaveType") LeaveType leaveType,
      @Param("status") LeaveStatus status,
      @Param("year") int year);
}
