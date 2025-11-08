package com.workzen.service;

import com.workzen.dto.LeaveApplicationDTO;
import com.workzen.entity.Employee;
import com.workzen.entity.LeaveApplication;
import com.workzen.entity.LeaveApplication.LeaveStatus;
import com.workzen.enums.LeaveType;
import com.workzen.repository.EmployeeRepository;
import com.workzen.repository.LeaveApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveApplicationService {

  private final LeaveApplicationRepository leaveApplicationRepository;
  private final EmployeeRepository employeeRepository;

  @Transactional
  public LeaveApplicationDTO createLeaveApplication(LeaveApplicationDTO dto) {
    Employee employee = employeeRepository.findById(dto.getEmployeeId())
        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + dto.getEmployeeId()));

    // Validate dates
    if (dto.getStartDate().isAfter(dto.getEndDate())) {
      throw new RuntimeException("Start date cannot be after end date");
    }

    if (dto.getStartDate().isBefore(LocalDate.now())) {
      throw new RuntimeException("Cannot apply for leave in the past");
    }

    // Check for overlapping leaves
    List<LeaveApplication> overlappingLeaves = leaveApplicationRepository.findOverlappingLeaves(
        employee, dto.getStartDate(), dto.getEndDate());
    if (!overlappingLeaves.isEmpty()) {
      throw new RuntimeException("Leave application overlaps with existing leave");
    }

    // Calculate total days
    long days = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1;
    if (dto.getIsHalfDay() != null && dto.getIsHalfDay()) {
      days = 1;
    }

    LeaveApplication leaveApplication = LeaveApplication.builder()
        .employee(employee)
        .leaveType(dto.getLeaveType())
        .startDate(dto.getStartDate())
        .endDate(dto.getEndDate())
        .totalDays((int) days)
        .reason(dto.getReason())
        .status(LeaveStatus.PENDING)
        .isHalfDay(dto.getIsHalfDay() != null ? dto.getIsHalfDay() : false)
        .build();

    LeaveApplication saved = leaveApplicationRepository.save(leaveApplication);
    return convertToDTO(saved);
  }

  @Transactional(readOnly = true)
  public LeaveApplicationDTO getLeaveApplicationById(Long id) {
    LeaveApplication leaveApplication = leaveApplicationRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Leave application not found with id: " + id));
    return convertToDTO(leaveApplication);
  }

  @Transactional(readOnly = true)
  public Page<LeaveApplicationDTO> getLeaveApplicationsByEmployee(Long employeeId, Pageable pageable) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
    return leaveApplicationRepository.findByEmployee(employee, pageable)
        .map(this::convertToDTO);
  }

  @Transactional(readOnly = true)
  public List<LeaveApplicationDTO> getLeaveApplicationsByEmployeeAndYear(Long employeeId, int year) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
    return leaveApplicationRepository.findLeavesByYear(employee, year)
        .stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public Page<LeaveApplicationDTO> getPendingLeaveApplications(Pageable pageable) {
    return leaveApplicationRepository.findByStatus(LeaveStatus.PENDING, pageable)
        .map(this::convertToDTO);
  }

  @Transactional(readOnly = true)
  public List<LeaveApplicationDTO> getPendingApprovals(Long approverId) {
    Employee approver = employeeRepository.findById(approverId)
        .orElseThrow(() -> new RuntimeException("Approver not found with id: " + approverId));
    return leaveApplicationRepository.findPendingApprovals(approver, LeaveStatus.PENDING)
        .stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  @Transactional
  public LeaveApplicationDTO approveLeaveApplication(Long id, Long approverId, String remarks) {
    LeaveApplication leaveApplication = leaveApplicationRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Leave application not found with id: " + id));

    Employee approver = employeeRepository.findById(approverId)
        .orElseThrow(() -> new RuntimeException("Approver not found with id: " + approverId));

    if (leaveApplication.getStatus() != LeaveStatus.PENDING) {
      throw new RuntimeException("Leave application is not in pending status");
    }

    leaveApplication.setStatus(LeaveStatus.APPROVED);
    leaveApplication.setApprovedBy(approver);
    leaveApplication.setApprovalDate(LocalDate.now());
    leaveApplication.setApprovalRemarks(remarks);

    LeaveApplication saved = leaveApplicationRepository.save(leaveApplication);
    return convertToDTO(saved);
  }

  @Transactional
  public LeaveApplicationDTO rejectLeaveApplication(Long id, Long approverId, String remarks) {
    LeaveApplication leaveApplication = leaveApplicationRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Leave application not found with id: " + id));

    Employee approver = employeeRepository.findById(approverId)
        .orElseThrow(() -> new RuntimeException("Approver not found with id: " + approverId));

    if (leaveApplication.getStatus() != LeaveStatus.PENDING) {
      throw new RuntimeException("Leave application is not in pending status");
    }

    leaveApplication.setStatus(LeaveStatus.REJECTED);
    leaveApplication.setApprovedBy(approver);
    leaveApplication.setApprovalDate(LocalDate.now());
    leaveApplication.setApprovalRemarks(remarks);

    LeaveApplication saved = leaveApplicationRepository.save(leaveApplication);
    return convertToDTO(saved);
  }

  @Transactional
  public LeaveApplicationDTO cancelLeaveApplication(Long id) {
    LeaveApplication leaveApplication = leaveApplicationRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Leave application not found with id: " + id));

    if (leaveApplication.getStatus() == LeaveStatus.APPROVED &&
        leaveApplication.getStartDate().isBefore(LocalDate.now())) {
      throw new RuntimeException("Cannot cancel an approved leave that has already started");
    }

    leaveApplication.setStatus(LeaveStatus.CANCELLED);
    LeaveApplication saved = leaveApplicationRepository.save(leaveApplication);
    return convertToDTO(saved);
  }

  @Transactional(readOnly = true)
  public long getUsedLeaveDays(Long employeeId, LeaveType leaveType, int year) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
    return leaveApplicationRepository.countByEmployeeAndLeaveTypeAndStatusAndYear(
        employee, leaveType, LeaveStatus.APPROVED, year);
  }

  private LeaveApplicationDTO convertToDTO(LeaveApplication leaveApplication) {
    LeaveApplicationDTO dto = LeaveApplicationDTO.builder()
        .id(leaveApplication.getId())
        .employeeId(leaveApplication.getEmployee().getId())
        .employeeName(leaveApplication.getEmployee().getFullName())
        .employeeIdString(leaveApplication.getEmployee().getEmployeeId())
        .leaveType(leaveApplication.getLeaveType())
        .startDate(leaveApplication.getStartDate())
        .endDate(leaveApplication.getEndDate())
        .totalDays(leaveApplication.getTotalDays())
        .reason(leaveApplication.getReason())
        .status(leaveApplication.getStatus())
        .isHalfDay(leaveApplication.isHalfDay())
        .build();

    if (leaveApplication.getApprovedBy() != null) {
      dto.setApprovedById(leaveApplication.getApprovedBy().getId());
      dto.setApprovedByName(leaveApplication.getApprovedBy().getFullName());
    }
    if (leaveApplication.getApprovalDate() != null) {
      dto.setApprovalDate(leaveApplication.getApprovalDate());
    }
    if (leaveApplication.getApprovalRemarks() != null) {
      dto.setApprovalRemarks(leaveApplication.getApprovalRemarks());
    }

    return dto;
  }
}
