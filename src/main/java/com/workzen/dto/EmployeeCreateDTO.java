package com.workzen.dto;

import com.workzen.enums.Department;
import com.workzen.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCreateDTO {
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private String phoneNumber;
  private LocalDate dateOfBirth;
  private String gender;
  private String address;
  private LocalDate dateOfJoining;
  private Department department;
  private Role role;
  private Double salary;
  private String designation;
  private Long managerId;
  private String emergencyContactName;
  private String emergencyContactPhone;
  private String emergencyContactRelation;
  private String bankAccountNumber;
  private String bankName;
  private String ifscCode;
  private String panNumber;
  private String aadharNumber;
}
