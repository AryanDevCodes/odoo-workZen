package com.workzen.entity;

import com.workzen.enums.Department;
import com.workzen.enums.EmployeeStatus;
import com.workzen.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "employees")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends BaseEntity implements UserDetails {
    
    @Column(unique = true, nullable = false)
    private String employeeId;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(unique = true)
    private String phoneNumber;
    
    private LocalDate dateOfBirth;
    
    private String gender;
    
    @Lob
    private String address;
    
    private LocalDate dateOfJoining;
    
    @Enumerated(EnumType.STRING)
    private Department department;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EmployeeStatus status = EmployeeStatus.ACTIVE;
    
    private Double salary;
    
    private String designation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;
    
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelation;
    
    private String bankAccountNumber;
    private String bankName;
    private String ifscCode;
    
    private String panNumber;
    private String aadharNumber;
    
    @Column(name = "profile_picture_url")
    private String profilePictureUrl;
    
    @Column(name = "is_account_non_expired")
    @Builder.Default
    private boolean accountNonExpired = true;
    
    @Column(name = "is_account_non_locked")
    @Builder.Default
    private boolean accountNonLocked = true;
    
    @Column(name = "is_credentials_non_expired")
    @Builder.Default
    private boolean credentialsNonExpired = true;
    
    @Column(name = "is_enabled")
    @Builder.Default
    private boolean enabled = true;
    
    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    
    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && status.canLogin();
    }
    
    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public boolean canManage(Employee employee) {
        return this.equals(employee.getManager()) || 
               this.getRole().isAdmin() || 
               this.getRole().isHR();
    }
}
