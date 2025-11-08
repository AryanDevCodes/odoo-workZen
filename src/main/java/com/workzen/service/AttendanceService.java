package com.workzen.service;

import com.workzen.dto.AttendanceDTO;
import com.workzen.entity.Attendance;
import com.workzen.entity.Employee;
import com.workzen.enums.AttendanceStatus;
import com.workzen.repository.AttendanceRepository;
import com.workzen.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

  private final AttendanceRepository attendanceRepository;
  private final EmployeeRepository employeeRepository;

  private static final LocalTime DEFAULT_CHECK_IN_TIME = LocalTime.of(9, 0);
  private static final int LATE_THRESHOLD_MINUTES = 15;

  @Transactional
  public AttendanceDTO markCheckIn(Long employeeId, String location) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

    LocalDate today = LocalDate.now();
    LocalTime now = LocalTime.now();

    // Check if attendance already exists for today
    Attendance attendance = attendanceRepository.findByEmployeeAndDate(employee, today)
        .orElse(Attendance.builder()
            .employee(employee)
            .date(today)
            .status(AttendanceStatus.ABSENT)
            .build());

    if (attendance.getCheckInTime() != null) {
      throw new RuntimeException("Check-in already marked for today");
    }

    attendance.setCheckInTime(now);
    attendance.setLocation(location);

    // Check if late
    if (now.isAfter(DEFAULT_CHECK_IN_TIME.plusMinutes(LATE_THRESHOLD_MINUTES))) {
      attendance.setLate(true);
      Duration lateDuration = Duration.between(DEFAULT_CHECK_IN_TIME, now);
      attendance.setLateMinutes((int) lateDuration.toMinutes());
      attendance.setStatus(AttendanceStatus.LATE);
    } else {
      attendance.setStatus(AttendanceStatus.PRESENT);
    }

    Attendance saved = attendanceRepository.save(attendance);
    return convertToDTO(saved);
  }

  @Transactional
  public AttendanceDTO markCheckOut(Long employeeId) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

    LocalDate today = LocalDate.now();
    LocalTime now = LocalTime.now();

    Attendance attendance = attendanceRepository.findByEmployeeAndDate(employee, today)
        .orElseThrow(() -> new RuntimeException("Check-in not marked for today"));

    if (attendance.getCheckOutTime() != null) {
      throw new RuntimeException("Check-out already marked for today");
    }

    attendance.setCheckOutTime(now);

    // Calculate work hours
    if (attendance.getCheckInTime() != null) {
      Duration workDuration = Duration.between(attendance.getCheckInTime(), now);
      double workHours = workDuration.toMinutes() / 60.0;
      attendance.setWorkHours(workHours);

      // Calculate overtime (if work hours > 8)
      if (workHours > 8.0) {
        attendance.setOvertimeHours(workHours - 8.0);
      }
    }

    Attendance saved = attendanceRepository.save(attendance);
    return convertToDTO(saved);
  }

  @Transactional
  public AttendanceDTO createAttendance(AttendanceDTO dto) {
    Employee employee = employeeRepository.findById(dto.getEmployeeId())
        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + dto.getEmployeeId()));

    // Check if attendance already exists for the date
    if (attendanceRepository.findByEmployeeAndDate(employee, dto.getDate()).isPresent()) {
      throw new RuntimeException("Attendance already exists for this date");
    }

    Attendance attendance = Attendance.builder()
        .employee(employee)
        .date(dto.getDate())
        .checkInTime(dto.getCheckInTime())
        .checkOutTime(dto.getCheckOutTime())
        .status(dto.getStatus() != null ? dto.getStatus() : AttendanceStatus.ABSENT)
        .workHours(dto.getWorkHours())
        .overtimeHours(dto.getOvertimeHours())
        .remarks(dto.getRemarks())
        .late(dto.getIsLate() != null ? dto.getIsLate() : false)
        .lateMinutes(dto.getLateMinutes())
        .location(dto.getLocation())
        .build();

    // Calculate work hours if check-in and check-out are provided
    if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null) {
      Duration workDuration = Duration.between(attendance.getCheckInTime(), attendance.getCheckOutTime());
      double workHours = workDuration.toMinutes() / 60.0;
      attendance.setWorkHours(workHours);

      if (workHours > 8.0) {
        attendance.setOvertimeHours(workHours - 8.0);
      }
    }

    Attendance saved = attendanceRepository.save(attendance);
    return convertToDTO(saved);
  }

  @Transactional(readOnly = true)
  public AttendanceDTO getAttendanceById(Long id) {
    Attendance attendance = attendanceRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Attendance not found with id: " + id));
    return convertToDTO(attendance);
  }

  @Transactional(readOnly = true)
  public Page<AttendanceDTO> getAttendanceByEmployee(Long employeeId, Pageable pageable) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
    return attendanceRepository.findByEmployee(employee, pageable)
        .map(this::convertToDTO);
  }

  @Transactional(readOnly = true)
  public List<AttendanceDTO> getAttendanceByEmployeeAndDateRange(Long employeeId, LocalDate startDate,
      LocalDate endDate) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
    return attendanceRepository.findByEmployeeAndDateBetween(employee, startDate, endDate)
        .stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<AttendanceDTO> getAttendanceByDate(LocalDate date) {
    return attendanceRepository.findByDateOrderByEmployeeName(date)
        .stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<AttendanceDTO> getMonthlyAttendance(Long employeeId, int year, int month) {
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
    return attendanceRepository.findMonthlyAttendance(employee, year, month)
        .stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  @Transactional
  public AttendanceDTO updateAttendance(Long id, AttendanceDTO dto) {
    Attendance attendance = attendanceRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Attendance not found with id: " + id));

    if (dto.getCheckInTime() != null) {
      attendance.setCheckInTime(dto.getCheckInTime());
    }
    if (dto.getCheckOutTime() != null) {
      attendance.setCheckOutTime(dto.getCheckOutTime());
    }
    if (dto.getStatus() != null) {
      attendance.setStatus(dto.getStatus());
    }
    if (dto.getRemarks() != null) {
      attendance.setRemarks(dto.getRemarks());
    }
    if (dto.getLocation() != null) {
      attendance.setLocation(dto.getLocation());
    }

    // Recalculate work hours
    if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null) {
      Duration workDuration = Duration.between(attendance.getCheckInTime(), attendance.getCheckOutTime());
      double workHours = workDuration.toMinutes() / 60.0;
      attendance.setWorkHours(workHours);

      if (workHours > 8.0) {
        attendance.setOvertimeHours(workHours - 8.0);
      } else {
        attendance.setOvertimeHours(0.0);
      }
    }

    Attendance saved = attendanceRepository.save(attendance);
    return convertToDTO(saved);
  }

  @Transactional
  public void deleteAttendance(Long id) {
    attendanceRepository.deleteById(id);
  }

  private AttendanceDTO convertToDTO(Attendance attendance) {
    AttendanceDTO dto = AttendanceDTO.builder()
        .id(attendance.getId())
        .employeeId(attendance.getEmployee().getId())
        .employeeName(attendance.getEmployee().getFullName())
        .employeeIdString(attendance.getEmployee().getEmployeeId())
        .date(attendance.getDate())
        .checkInTime(attendance.getCheckInTime())
        .checkOutTime(attendance.getCheckOutTime())
        .status(attendance.getStatus())
        .workHours(attendance.getWorkHours())
        .overtimeHours(attendance.getOvertimeHours())
        .remarks(attendance.getRemarks())
        .isLate(attendance.isLate())
        .lateMinutes(attendance.getLateMinutes())
        .location(attendance.getLocation())
        .build();
    return dto;
  }
}
