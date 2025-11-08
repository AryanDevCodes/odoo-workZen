package com.workzen.service;

import com.workzen.dto.EmployeeCreateDTO;
import com.workzen.dto.EmployeeDTO;
import com.workzen.entity.Employee;
import com.workzen.enums.Department;
import com.workzen.enums.EmployeeStatus;
import com.workzen.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public EmployeeDTO createEmployee(EmployeeCreateDTO dto) {
    // Check if email already exists
    if (employeeRepository.existsByEmail(dto.getEmail())) {
      throw new RuntimeException("Employee with email " + dto.getEmail() + " already exists");
    }

    // Check if phone number already exists
    if (dto.getPhoneNumber() != null && employeeRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
      throw new RuntimeException("Employee with phone number " + dto.getPhoneNumber() + " already exists");
    }

    // Generate employee ID
    String employeeId = generateEmployeeId(dto.getDepartment(), dto.getDateOfJoining());

    // Check if employee ID already exists
    while (employeeRepository.existsByEmployeeId(employeeId)) {
      employeeId = generateEmployeeId(dto.getDepartment(), dto.getDateOfJoining());
    }

    Employee manager = null;
    if (dto.getManagerId() != null) {
      manager = employeeRepository.findById(dto.getManagerId())
          .orElseThrow(() -> new RuntimeException("Manager not found with id: " + dto.getManagerId()));
    }

    Employee employee = Employee.builder()
        .employeeId(employeeId)
        .firstName(dto.getFirstName())
        .lastName(dto.getLastName())
        .email(dto.getEmail())
        .password(passwordEncoder.encode(dto.getPassword()))
        .phoneNumber(dto.getPhoneNumber())
        .dateOfBirth(dto.getDateOfBirth())
        .gender(dto.getGender())
        .address(dto.getAddress())
        .dateOfJoining(dto.getDateOfJoining())
        .department(dto.getDepartment())
        .role(dto.getRole())
        .status(EmployeeStatus.ACTIVE)
        .salary(dto.getSalary())
        .designation(dto.getDesignation())
        .manager(manager)
        .emergencyContactName(dto.getEmergencyContactName())
        .emergencyContactPhone(dto.getEmergencyContactPhone())
        .emergencyContactRelation(dto.getEmergencyContactRelation())
        .bankAccountNumber(dto.getBankAccountNumber())
        .bankName(dto.getBankName())
        .ifscCode(dto.getIfscCode())
        .panNumber(dto.getPanNumber())
        .aadharNumber(dto.getAadharNumber())
        .build();

    Employee savedEmployee = employeeRepository.save(employee);
    return convertToDTO(savedEmployee);
  }

  @Transactional(readOnly = true)
  public EmployeeDTO getEmployeeById(Long id) {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    return convertToDTO(employee);
  }

  @Transactional(readOnly = true)
  public EmployeeDTO getEmployeeByEmployeeId(String employeeId) {
    Employee employee = employeeRepository.findByEmployeeId(employeeId)
        .orElseThrow(() -> new RuntimeException("Employee not found with employee ID: " + employeeId));
    return convertToDTO(employee);
  }

  @Transactional(readOnly = true)
  public Page<EmployeeDTO> getAllEmployees(Pageable pageable) {
    return employeeRepository.findAll(pageable)
        .map(this::convertToDTO);
  }

  @Transactional(readOnly = true)
  public Page<EmployeeDTO> searchEmployees(String searchTerm, Pageable pageable) {
    return employeeRepository.searchEmployees(searchTerm, pageable)
        .map(this::convertToDTO);
  }

  @Transactional(readOnly = true)
  public List<EmployeeDTO> getEmployeesByDepartment(Department department) {
    return employeeRepository.findByDepartment(department)
        .stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<EmployeeDTO> getEmployeesByStatus(EmployeeStatus status) {
    return employeeRepository.findByStatus(status)
        .stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<EmployeeDTO> getSubordinates(Long managerId) {
    Employee manager = employeeRepository.findById(managerId)
        .orElseThrow(() -> new RuntimeException("Manager not found with id: " + managerId));
    return employeeRepository.findSubordinates(manager, EmployeeStatus.ACTIVE)
        .stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  @Transactional
  public EmployeeDTO updateEmployee(Long id, EmployeeCreateDTO dto) {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

    // Check email uniqueness if changed
    if (!employee.getEmail().equals(dto.getEmail()) && employeeRepository.existsByEmail(dto.getEmail())) {
      throw new RuntimeException("Employee with email " + dto.getEmail() + " already exists");
    }

    // Check phone uniqueness if changed
    if (dto.getPhoneNumber() != null &&
        !dto.getPhoneNumber().equals(employee.getPhoneNumber()) &&
        employeeRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
      throw new RuntimeException("Employee with phone number " + dto.getPhoneNumber() + " already exists");
    }

    Employee manager = null;
    if (dto.getManagerId() != null) {
      manager = employeeRepository.findById(dto.getManagerId())
          .orElseThrow(() -> new RuntimeException("Manager not found with id: " + dto.getManagerId()));
    }

    employee.setFirstName(dto.getFirstName());
    employee.setLastName(dto.getLastName());
    employee.setEmail(dto.getEmail());
    if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
      employee.setPassword(passwordEncoder.encode(dto.getPassword()));
    }
    employee.setPhoneNumber(dto.getPhoneNumber());
    employee.setDateOfBirth(dto.getDateOfBirth());
    employee.setGender(dto.getGender());
    employee.setAddress(dto.getAddress());
    employee.setDateOfJoining(dto.getDateOfJoining());
    employee.setDepartment(dto.getDepartment());
    employee.setRole(dto.getRole());
    employee.setSalary(dto.getSalary());
    employee.setDesignation(dto.getDesignation());
    employee.setManager(manager);
    employee.setEmergencyContactName(dto.getEmergencyContactName());
    employee.setEmergencyContactPhone(dto.getEmergencyContactPhone());
    employee.setEmergencyContactRelation(dto.getEmergencyContactRelation());
    employee.setBankAccountNumber(dto.getBankAccountNumber());
    employee.setBankName(dto.getBankName());
    employee.setIfscCode(dto.getIfscCode());
    employee.setPanNumber(dto.getPanNumber());
    employee.setAadharNumber(dto.getAadharNumber());

    Employee updatedEmployee = employeeRepository.save(employee);
    return convertToDTO(updatedEmployee);
  }

  @Transactional
  public void deleteEmployee(Long id) {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    employee.setStatus(EmployeeStatus.TERMINATED);
    employeeRepository.save(employee);
  }

  @Transactional
  public EmployeeDTO updateEmployeeStatus(Long id, EmployeeStatus status) {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    employee.setStatus(status);
    Employee updatedEmployee = employeeRepository.save(employee);
    return convertToDTO(updatedEmployee);
  }

  private String generateEmployeeId(Department department, LocalDate dateOfJoining) {
    String deptCode = department.name().substring(0, Math.min(3, department.name().length()));
    String year = String.valueOf(dateOfJoining.getYear());
    String month = String.format("%02d", dateOfJoining.getMonthValue());
    String random = String.format("%04d", (int) (Math.random() * 10000));
    return deptCode + year + month + random;
  }

  private EmployeeDTO convertToDTO(Employee employee) {
    EmployeeDTO dto = EmployeeDTO.builder()
        .id(employee.getId())
        .employeeId(employee.getEmployeeId())
        .firstName(employee.getFirstName())
        .lastName(employee.getLastName())
        .email(employee.getEmail())
        .phoneNumber(employee.getPhoneNumber())
        .dateOfBirth(employee.getDateOfBirth())
        .gender(employee.getGender())
        .address(employee.getAddress())
        .dateOfJoining(employee.getDateOfJoining())
        .department(employee.getDepartment())
        .role(employee.getRole())
        .status(employee.getStatus())
        .salary(employee.getSalary())
        .designation(employee.getDesignation())
        .emergencyContactName(employee.getEmergencyContactName())
        .emergencyContactPhone(employee.getEmergencyContactPhone())
        .emergencyContactRelation(employee.getEmergencyContactRelation())
        .bankAccountNumber(employee.getBankAccountNumber())
        .bankName(employee.getBankName())
        .ifscCode(employee.getIfscCode())
        .panNumber(employee.getPanNumber())
        .aadharNumber(employee.getAadharNumber())
        .profilePictureUrl(employee.getProfilePictureUrl())
        .build();

    if (employee.getManager() != null) {
      dto.setManagerId(employee.getManager().getId());
      dto.setManagerName(employee.getManager().getFullName());
    }

    return dto;
  }
}
