package com.workzen.controller;

import com.workzen.dto.ApiResponse;
import com.workzen.dto.EmployeeCreateDTO;
import com.workzen.dto.EmployeeDTO;
import com.workzen.enums.Department;
import com.workzen.enums.EmployeeStatus;
import com.workzen.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmployeeController {

  private final EmployeeService employeeService;

  @PostMapping
  public ResponseEntity<ApiResponse<EmployeeDTO>> createEmployee(@Valid @RequestBody EmployeeCreateDTO dto) {
    try {
      EmployeeDTO employee = employeeService.createEmployee(dto);
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(ApiResponse.success("Employee created successfully", employee));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<EmployeeDTO>> getEmployeeById(@PathVariable Long id) {
    try {
      EmployeeDTO employee = employeeService.getEmployeeById(id);
      return ResponseEntity.ok(ApiResponse.success(employee));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/employee-id/{employeeId}")
  public ResponseEntity<ApiResponse<EmployeeDTO>> getEmployeeByEmployeeId(@PathVariable String employeeId) {
    try {
      EmployeeDTO employee = employeeService.getEmployeeByEmployeeId(employeeId);
      return ResponseEntity.ok(ApiResponse.success(employee));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping
  public ResponseEntity<ApiResponse<Page<EmployeeDTO>>> getAllEmployees(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "ASC") Sort.Direction sortDir) {
    try {
      Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir, sortBy));
      Page<EmployeeDTO> employees = employeeService.getAllEmployees(pageable);
      return ResponseEntity.ok(ApiResponse.success(employees));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/search")
  public ResponseEntity<ApiResponse<Page<EmployeeDTO>>> searchEmployees(
      @RequestParam String searchTerm,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      Page<EmployeeDTO> employees = employeeService.searchEmployees(searchTerm, pageable);
      return ResponseEntity.ok(ApiResponse.success(employees));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/department/{department}")
  public ResponseEntity<ApiResponse<List<EmployeeDTO>>> getEmployeesByDepartment(
      @PathVariable Department department) {
    try {
      List<EmployeeDTO> employees = employeeService.getEmployeesByDepartment(department);
      return ResponseEntity.ok(ApiResponse.success(employees));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<ApiResponse<List<EmployeeDTO>>> getEmployeesByStatus(
      @PathVariable EmployeeStatus status) {
    try {
      List<EmployeeDTO> employees = employeeService.getEmployeesByStatus(status);
      return ResponseEntity.ok(ApiResponse.success(employees));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/manager/{managerId}/subordinates")
  public ResponseEntity<ApiResponse<List<EmployeeDTO>>> getSubordinates(
      @PathVariable Long managerId) {
    try {
      List<EmployeeDTO> subordinates = employeeService.getSubordinates(managerId);
      return ResponseEntity.ok(ApiResponse.success(subordinates));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<EmployeeDTO>> updateEmployee(
      @PathVariable Long id,
      @Valid @RequestBody EmployeeCreateDTO dto) {
    try {
      EmployeeDTO employee = employeeService.updateEmployee(id, dto);
      return ResponseEntity.ok(ApiResponse.success("Employee updated successfully", employee));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<ApiResponse<EmployeeDTO>> updateEmployeeStatus(
      @PathVariable Long id,
      @RequestParam EmployeeStatus status) {
    try {
      EmployeeDTO employee = employeeService.updateEmployeeStatus(id, status);
      return ResponseEntity.ok(ApiResponse.success("Employee status updated successfully", employee));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
    try {
      employeeService.deleteEmployee(id);
      return ResponseEntity.ok(ApiResponse.success("Employee deleted successfully", null));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }
}
