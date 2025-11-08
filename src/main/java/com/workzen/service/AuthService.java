package com.workzen.service;

import com.workzen.dto.AuthRequest;
import com.workzen.dto.AuthResponse;
import com.workzen.entity.Employee;
import com.workzen.repository.EmployeeRepository;
import com.workzen.util.JwtUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService implements UserDetailsService {

  private final EmployeeRepository employeeRepository;
  private final JwtUtil jwtUtil;
  private final AuthenticationManager authenticationManager;

  public AuthService(EmployeeRepository employeeRepository, 
                     JwtUtil jwtUtil, 
                     @Lazy AuthenticationManager authenticationManager) {
    this.employeeRepository = employeeRepository;
    this.jwtUtil = jwtUtil;
    this.authenticationManager = authenticationManager;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Employee employee = employeeRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("Employee not found with email: " + email));
    return employee;
  }

  public AuthResponse authenticate(AuthRequest request) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    } catch (BadCredentialsException e) {
      throw new RuntimeException("Invalid email or password");
    }

    Employee employee = employeeRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new RuntimeException("Employee not found"));

    Map<String, Object> extraClaims = new HashMap<>();
    extraClaims.put("employeeId", employee.getEmployeeId());
    extraClaims.put("role", employee.getRole().name());
    extraClaims.put("employeeDbId", employee.getId());

    String token = jwtUtil.generateToken(employee, extraClaims);

    return AuthResponse.builder()
        .token(token)
        .email(employee.getEmail())
        .employeeId(employee.getEmployeeId())
        .fullName(employee.getFullName())
        .role(employee.getRole())
        .employeeDbId(employee.getId())
        .build();
  }
}
