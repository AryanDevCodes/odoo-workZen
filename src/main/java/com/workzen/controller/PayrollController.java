package com.workzen.controller;

import com.workzen.dto.ApiResponse;
import com.workzen.dto.PayrollDTO;
import com.workzen.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PayrollController {

  private final PayrollService payrollService;

  @PostMapping("/generate")
  public ResponseEntity<ApiResponse<PayrollDTO>> generatePayroll(
      @RequestParam Long employeeId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate salaryMonth,
      @RequestParam(required = false) Long processedById) {
    try {
      PayrollDTO payroll = payrollService.generatePayroll(employeeId, salaryMonth, processedById);
      return ResponseEntity.ok(ApiResponse.success("Payroll generated successfully", payroll));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<PayrollDTO>> getPayrollById(@PathVariable Long id) {
    try {
      PayrollDTO payroll = payrollService.getPayrollById(id);
      return ResponseEntity.ok(ApiResponse.success(payroll));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/employee/{employeeId}/month")
  public ResponseEntity<ApiResponse<PayrollDTO>> getPayrollByEmployeeAndMonth(
      @PathVariable Long employeeId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate salaryMonth) {
    try {
      PayrollDTO payroll = payrollService.getPayrollByEmployeeAndMonth(employeeId, salaryMonth);
      return ResponseEntity.ok(ApiResponse.success(payroll));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/employee/{employeeId}")
  public ResponseEntity<ApiResponse<Page<PayrollDTO>>> getPayrollsByEmployee(
      @PathVariable Long employeeId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      Page<PayrollDTO> payrolls = payrollService.getPayrollsByEmployee(employeeId, pageable);
      return ResponseEntity.ok(ApiResponse.success(payrolls));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/employee/{employeeId}/year/{year}")
  public ResponseEntity<ApiResponse<List<PayrollDTO>>> getPayrollsByYear(
      @PathVariable Long employeeId,
      @PathVariable int year) {
    try {
      List<PayrollDTO> payrolls = payrollService.getPayrollsByYear(employeeId, year);
      return ResponseEntity.ok(ApiResponse.success(payrolls));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/month")
  public ResponseEntity<ApiResponse<List<PayrollDTO>>> getPayrollsByMonth(
      @RequestParam int year,
      @RequestParam int month) {
    try {
      List<PayrollDTO> payrolls = payrollService.getPayrollsByMonth(year, month);
      return ResponseEntity.ok(ApiResponse.success(payrolls));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }
}
