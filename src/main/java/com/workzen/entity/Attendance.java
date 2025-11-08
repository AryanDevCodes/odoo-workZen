package com.workzen.entity;

import com.workzen.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "attendance", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "date"}))
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attendance extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id")
    private Employee employee;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(name = "check_in_time")
    private LocalTime checkInTime;
    
    @Column(name = "check_out_time")
    private LocalTime checkOutTime;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AttendanceStatus status = AttendanceStatus.ABSENT;
    
    @Column(name = "work_hours")
    private Double workHours;
    
    @Column(name = "overtime_hours")
    private Double overtimeHours;
    
    private String remarks;
    
    @Column(name = "is_late")
    @Builder.Default
    private boolean isLate = false;
    
    @Column(name = "late_minutes")
    private Integer lateMinutes;
    
    @Column(name = "location")
    private String location; // For tracking work from home vs office
}
