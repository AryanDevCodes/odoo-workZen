package com.workzen.dto;

import com.workzen.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
  private String token;
  private String email;
  private String employeeId;
  private String fullName;
  private Role role;
  private Long employeeDbId;
}
