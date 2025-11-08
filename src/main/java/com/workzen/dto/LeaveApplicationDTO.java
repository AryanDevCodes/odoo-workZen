package com.workzen.dto;

import com.workzen.entity.LeaveApplication;
import com.workzen.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveApplicationDTO {
  private Long id;
  private Long employeeId;
  private String employeeName;
  private String employeeIdString;
  private LeaveType leaveType;
  private LocalDate startDate;
  private LocalDate endDate;
  private Integer totalDays;
  private String reason;
  private LeaveApplication.LeaveStatus status;
  private Long approvedById;
  private String approvedByName;
  private LocalDate approvalDate;
  private String approvalRemarks;
  private Boolean isHalfDay;
}
