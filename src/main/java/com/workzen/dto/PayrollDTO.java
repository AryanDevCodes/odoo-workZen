package com.workzen.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollDTO {
  private Long id;
  private Long employeeId;
  private String employeeName;
  private String employeeIdString;
  private LocalDate salaryMonth;
  private Double basicSalary;
  private Double hra;
  private Double transportAllowance;
  private Double medicalAllowance;
  private Double otherAllowances;
  private Double overtimeAmount;
  private Double bonus;
  private Double grossSalary;
  private Double providentFund;
  private Double professionalTax;
  private Double incomeTax;
  private Double otherDeductions;
  private Double totalDeductions;
  private Double netSalary;
  private Integer daysWorked;
  private Integer daysOnLeave;
  private Double overtimeHours;
  private Boolean isProcessed;
  private LocalDate processedDate;
  private Long processedById;
  private String processedByName;
}
