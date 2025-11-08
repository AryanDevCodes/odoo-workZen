package com.workzen.dto;

import com.workzen.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {
  private Long id;
  private Long employeeId;
  private String employeeName;
  private String employeeIdString;
  private LocalDate date;
  private LocalTime checkInTime;
  private LocalTime checkOutTime;
  private AttendanceStatus status;
  private Double workHours;
  private Double overtimeHours;
  private String remarks;
  private Boolean isLate;
  private Integer lateMinutes;
  private String location;
}
