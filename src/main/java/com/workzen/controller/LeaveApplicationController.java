package com.workzen.controller;

import com.workzen.dto.ApiResponse;
import com.workzen.dto.LeaveApplicationDTO;
import com.workzen.service.LeaveApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LeaveApplicationController {

  private final LeaveApplicationService leaveApplicationService;

  @PostMapping
  public ResponseEntity<ApiResponse<LeaveApplicationDTO>> createLeaveApplication(
      @Valid @RequestBody LeaveApplicationDTO dto) {
    try {
      LeaveApplicationDTO leave = leaveApplicationService.createLeaveApplication(dto);
      return ResponseEntity.ok(ApiResponse.success("Leave application created successfully", leave));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<LeaveApplicationDTO>> getLeaveApplicationById(@PathVariable Long id) {
    try {
      LeaveApplicationDTO leave = leaveApplicationService.getLeaveApplicationById(id);
      return ResponseEntity.ok(ApiResponse.success(leave));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/employee/{employeeId}")
  public ResponseEntity<ApiResponse<Page<LeaveApplicationDTO>>> getLeaveApplicationsByEmployee(
      @PathVariable Long employeeId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      Page<LeaveApplicationDTO> leaves = leaveApplicationService.getLeaveApplicationsByEmployee(
          employeeId, pageable);
      return ResponseEntity.ok(ApiResponse.success(leaves));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/employee/{employeeId}/year/{year}")
  public ResponseEntity<ApiResponse<List<LeaveApplicationDTO>>> getLeaveApplicationsByYear(
      @PathVariable Long employeeId,
      @PathVariable int year) {
    try {
      List<LeaveApplicationDTO> leaves = leaveApplicationService.getLeaveApplicationsByEmployeeAndYear(
          employeeId, year);
      return ResponseEntity.ok(ApiResponse.success(leaves));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/pending")
  public ResponseEntity<ApiResponse<Page<LeaveApplicationDTO>>> getPendingLeaveApplications(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      Page<LeaveApplicationDTO> leaves = leaveApplicationService.getPendingLeaveApplications(pageable);
      return ResponseEntity.ok(ApiResponse.success(leaves));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/approver/{approverId}/pending")
  public ResponseEntity<ApiResponse<List<LeaveApplicationDTO>>> getPendingApprovals(
      @PathVariable Long approverId) {
    try {
      List<LeaveApplicationDTO> leaves = leaveApplicationService.getPendingApprovals(approverId);
      return ResponseEntity.ok(ApiResponse.success(leaves));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @PostMapping("/{id}/approve")
  public ResponseEntity<ApiResponse<LeaveApplicationDTO>> approveLeaveApplication(
      @PathVariable Long id,
      @RequestParam Long approverId,
      @RequestParam(required = false) String remarks) {
    try {
      LeaveApplicationDTO leave = leaveApplicationService.approveLeaveApplication(id, approverId, remarks);
      return ResponseEntity.ok(ApiResponse.success("Leave application approved", leave));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @PostMapping("/{id}/reject")
  public ResponseEntity<ApiResponse<LeaveApplicationDTO>> rejectLeaveApplication(
      @PathVariable Long id,
      @RequestParam Long approverId,
      @RequestParam(required = false) String remarks) {
    try {
      LeaveApplicationDTO leave = leaveApplicationService.rejectLeaveApplication(id, approverId, remarks);
      return ResponseEntity.ok(ApiResponse.success("Leave application rejected", leave));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @PostMapping("/{id}/cancel")
  public ResponseEntity<ApiResponse<LeaveApplicationDTO>> cancelLeaveApplication(@PathVariable Long id) {
    try {
      LeaveApplicationDTO leave = leaveApplicationService.cancelLeaveApplication(id);
      return ResponseEntity.ok(ApiResponse.success("Leave application cancelled", leave));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/employee/{employeeId}/used-days")
  public ResponseEntity<ApiResponse<Long>> getUsedLeaveDays(
      @PathVariable Long employeeId,
      @RequestParam String leaveType,
      @RequestParam int year) {
    try {
      long usedDays = leaveApplicationService.getUsedLeaveDays(
          employeeId, com.workzen.enums.LeaveType.valueOf(leaveType), year);
      return ResponseEntity.ok(ApiResponse.success(usedDays));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }
}
