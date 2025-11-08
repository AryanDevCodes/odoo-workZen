package com.workzen.service;

import com.workzen.dto.PayrollDTO;
import com.workzen.entity.Employee;
import com.workzen.entity.Payroll;
import com.workzen.enums.AttendanceStatus;
import com.workzen.repository.AttendanceRepository;
import com.workzen.repository.EmployeeRepository;
import com.workzen.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayrollService {

  private final PayrollRepository payrollRepository;
  private final EmployeeRepository employeeRepository;
  private final AttendanceRepository attendanceRepository;

  private static final double PF_PERCENTAGE = 0.12; // 12% of basic salary
  private static final double PROFESSIONAL_TAX = 200.0; // Fixed amount
  private static final double STANDARD_WORK_HOURS_PER_DAY = 8.0;

  @Transactional
  public PayrollDTO generatePayroll(Long employeeId, LocalDate salaryMonth, Long processedById) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

    // Check if payroll already exists for this month
    if (payrollRepository.findByEmployeeAndSalaryMonth(employee, salaryMonth).isPresent()) {
      throw new RuntimeException("Payroll already generated for this month");
    }

    Employee processedBy = null;
    if (processedById != null) {
      processedBy = employeeRepository.findById(processedById)
          .orElseThrow(() -> new RuntimeException("Processed by employee not found"));
    }

    // Calculate days worked and on leave
    YearMonth yearMonth = YearMonth.from(salaryMonth);
    LocalDate startDate = yearMonth.atDay(1);
    LocalDate endDate = yearMonth.atEndOfMonth();

    long daysWorked = attendanceRepository.countByEmployeeAndDateRangeAndStatus(
        employee, startDate, endDate, AttendanceStatus.PRESENT);
    long daysOnLeave = attendanceRepository.countByEmployeeAndDateRangeAndStatus(
        employee, startDate, endDate, AttendanceStatus.ON_LEAVE);

    // Get overtime hours
    Double avgOvertimeHours = attendanceRepository.getAverageWorkHours(employee, startDate, endDate);
    double overtimeHours = (avgOvertimeHours != null && avgOvertimeHours > STANDARD_WORK_HOURS_PER_DAY)
        ? (avgOvertimeHours - STANDARD_WORK_HOURS_PER_DAY) * 30
        : 0.0;

    // Calculate salary components
    Double basicSalary = employee.getSalary() * 0.5; // 50% of total salary
    Double hra = employee.getSalary() * 0.2; // 20% of total salary
    Double transportAllowance = employee.getSalary() * 0.1; // 10%
    Double medicalAllowance = employee.getSalary() * 0.1; // 10%
    Double otherAllowances = employee.getSalary() * 0.1; // 10%

    // Overtime calculation (assuming 1.5x hourly rate)
    double hourlyRate = basicSalary / (STANDARD_WORK_HOURS_PER_DAY * 30);
    Double overtimeAmount = overtimeHours * hourlyRate * 1.5;

    Double bonus = 0.0; // Can be configured

    // Gross salary
    Double grossSalary = basicSalary + hra + transportAllowance + medicalAllowance +
        otherAllowances + overtimeAmount + bonus;

    // Deductions
    Double providentFund = basicSalary * PF_PERCENTAGE;
    Double professionalTax = PROFESSIONAL_TAX;

    // Income tax calculation (simplified - can be enhanced)
    Double incomeTax = calculateIncomeTax(grossSalary);

    Double otherDeductions = 0.0;
    Double totalDeductions = providentFund + professionalTax + incomeTax + otherDeductions;

    // Net salary
    Double netSalary = grossSalary - totalDeductions;

    Payroll payroll = Payroll.builder()
        .employee(employee)
        .salaryMonth(salaryMonth)
        .basicSalary(basicSalary)
        .hra(hra)
        .transportAllowance(transportAllowance)
        .medicalAllowance(medicalAllowance)
        .otherAllowances(otherAllowances)
        .overtimeAmount(overtimeAmount)
        .bonus(bonus)
        .grossSalary(grossSalary)
        .providentFund(providentFund)
        .professionalTax(professionalTax)
        .incomeTax(incomeTax)
        .otherDeductions(otherDeductions)
        .totalDeductions(totalDeductions)
        .netSalary(netSalary)
        .daysWorked((int) daysWorked)
        .daysOnLeave((int) daysOnLeave)
        .overtimeHours(overtimeHours)
        .isProcessed(true)
        .processedDate(LocalDate.now())
        .processedBy(processedBy)
        .build();

    Payroll saved = payrollRepository.save(payroll);
    return convertToDTO(saved);
  }

  @Transactional(readOnly = true)
  public PayrollDTO getPayrollById(Long id) {
    Payroll payroll = payrollRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Payroll not found with id: " + id));
    return convertToDTO(payroll);
  }

  @Transactional(readOnly = true)
  public PayrollDTO getPayrollByEmployeeAndMonth(Long employeeId, LocalDate salaryMonth) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
    Payroll payroll = payrollRepository.findByEmployeeAndSalaryMonth(employee, salaryMonth)
        .orElseThrow(() -> new RuntimeException("Payroll not found for this month"));
    return convertToDTO(payroll);
  }

  @Transactional(readOnly = true)
  public Page<PayrollDTO> getPayrollsByEmployee(Long employeeId, Pageable pageable) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
    return payrollRepository.findByEmployee(employee, pageable)
        .map(this::convertToDTO);
  }

  @Transactional(readOnly = true)
  public List<PayrollDTO> getPayrollsByYear(Long employeeId, int year) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
    return payrollRepository.findPayrollsByYear(employee, year)
        .stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<PayrollDTO> getPayrollsByMonth(int year, int month) {
    return payrollRepository.findPayrollsByMonth(year, month)
        .stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  private Double calculateIncomeTax(Double grossSalary) {
    // Simplified income tax calculation
    // Can be enhanced with proper tax slabs
    if (grossSalary <= 250000) {
      return 0.0;
    } else if (grossSalary <= 500000) {
      return (grossSalary - 250000) * 0.05;
    } else if (grossSalary <= 1000000) {
      return 12500 + (grossSalary - 500000) * 0.20;
    } else {
      return 112500 + (grossSalary - 1000000) * 0.30;
    }
  }

  private PayrollDTO convertToDTO(Payroll payroll) {
    PayrollDTO dto = PayrollDTO.builder()
        .id(payroll.getId())
        .employeeId(payroll.getEmployee().getId())
        .employeeName(payroll.getEmployee().getFullName())
        .employeeIdString(payroll.getEmployee().getEmployeeId())
        .salaryMonth(payroll.getSalaryMonth())
        .basicSalary(payroll.getBasicSalary())
        .hra(payroll.getHra())
        .transportAllowance(payroll.getTransportAllowance())
        .medicalAllowance(payroll.getMedicalAllowance())
        .otherAllowances(payroll.getOtherAllowances())
        .overtimeAmount(payroll.getOvertimeAmount())
        .bonus(payroll.getBonus())
        .grossSalary(payroll.getGrossSalary())
        .providentFund(payroll.getProvidentFund())
        .professionalTax(payroll.getProfessionalTax())
        .incomeTax(payroll.getIncomeTax())
        .otherDeductions(payroll.getOtherDeductions())
        .totalDeductions(payroll.getTotalDeductions())
        .netSalary(payroll.getNetSalary())
        .daysWorked(payroll.getDaysWorked())
        .daysOnLeave(payroll.getDaysOnLeave())
        .overtimeHours(payroll.getOvertimeHours())
        .isProcessed(payroll.getIsProcessed())
        .processedDate(payroll.getProcessedDate())
        .build();

    if (payroll.getProcessedBy() != null) {
      dto.setProcessedById(payroll.getProcessedBy().getId());
      dto.setProcessedByName(payroll.getProcessedBy().getFullName());
    }

    return dto;
  }
}
