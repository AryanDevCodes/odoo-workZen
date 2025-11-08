package com.workzen.controller;

import com.workzen.dto.ApiResponse;
import com.workzen.dto.AttendanceDTO;
import com.workzen.service.AttendanceService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AttendanceController {

  private final AttendanceService attendanceService;

  @PostMapping("/check-in")
  public ResponseEntity<ApiResponse<AttendanceDTO>> markCheckIn(
      @RequestParam Long employeeId,
      @RequestParam(required = false) String location) {
    try {
      AttendanceDTO attendance = attendanceService.markCheckIn(employeeId, location);
      return ResponseEntity.ok(ApiResponse.success("Check-in marked successfully", attendance));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @PostMapping("/check-out")
  public ResponseEntity<ApiResponse<AttendanceDTO>> markCheckOut(@RequestParam Long employeeId) {
    try {
      AttendanceDTO attendance = attendanceService.markCheckOut(employeeId);
      return ResponseEntity.ok(ApiResponse.success("Check-out marked successfully", attendance));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @PostMapping
  public ResponseEntity<ApiResponse<AttendanceDTO>> createAttendance(
      @Valid @RequestBody AttendanceDTO dto) {
    try {
      AttendanceDTO attendance = attendanceService.createAttendance(dto);
      return ResponseEntity.ok(ApiResponse.success("Attendance created successfully", attendance));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<AttendanceDTO>> getAttendanceById(@PathVariable Long id) {
    try {
      AttendanceDTO attendance = attendanceService.getAttendanceById(id);
      return ResponseEntity.ok(ApiResponse.success(attendance));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/employee/{employeeId}")
  public ResponseEntity<ApiResponse<Page<AttendanceDTO>>> getAttendanceByEmployee(
      @PathVariable Long employeeId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      Page<AttendanceDTO> attendance = attendanceService.getAttendanceByEmployee(employeeId, pageable);
      return ResponseEntity.ok(ApiResponse.success(attendance));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/employee/{employeeId}/range")
  public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getAttendanceByDateRange(
      @PathVariable Long employeeId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    try {
      List<AttendanceDTO> attendance = attendanceService.getAttendanceByEmployeeAndDateRange(
          employeeId, startDate, endDate);
      return ResponseEntity.ok(ApiResponse.success(attendance));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/employee/{employeeId}/monthly")
  public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getMonthlyAttendance(
      @PathVariable Long employeeId,
      @RequestParam int year,
      @RequestParam int month) {
    try {
      List<AttendanceDTO> attendance = attendanceService.getMonthlyAttendance(employeeId, year, month);
      return ResponseEntity.ok(ApiResponse.success(attendance));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/date/{date}")
  public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getAttendanceByDate(
      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    try {
      List<AttendanceDTO> attendance = attendanceService.getAttendanceByDate(date);
      return ResponseEntity.ok(ApiResponse.success(attendance));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<AttendanceDTO>> updateAttendance(
      @PathVariable Long id,
      @Valid @RequestBody AttendanceDTO dto) {
    try {
      AttendanceDTO attendance = attendanceService.updateAttendance(id, dto);
      return ResponseEntity.ok(ApiResponse.success("Attendance updated successfully", attendance));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteAttendance(@PathVariable Long id) {
    try {
      attendanceService.deleteAttendance(id);
      return ResponseEntity.ok(ApiResponse.success("Attendance deleted successfully", null));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(e.getMessage()));
    }
  }
}
