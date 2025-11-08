# WorkZen HRMS Database Design Documentation

## Overview

This document describes the database schema design for the WorkZen Human Resource Management System (HRMS). The system uses MySQL as the primary database.

## Database Schema

### 1. Employees Table (`employees`)

**Purpose**: Stores all employee information including personal details, employment information, and authentication credentials.

**Key Fields**:

- `id`: Primary key (BIGINT AUTO_INCREMENT)
- `employee_id`: Unique employee identifier (VARCHAR(50), UNIQUE)
- `email`: Unique email address for login (VARCHAR(255), UNIQUE)
- `password`: Encrypted password (VARCHAR(255))
- `first_name`, `last_name`: Employee name
- `department`: Department enum value
- `role`: Role enum value
- `status`: Employee status (ACTIVE, INACTIVE, etc.)
- `manager_id`: Self-referencing foreign key to employees table
- `salary`: Employee salary (DOUBLE)
- `date_of_joining`: Employment start date

**Relationships**:

- Self-referential: `manager_id` → `employees(id)`
- Referenced by: `attendance`, `leave_applications`, `payroll`, `performance_reviews`

**Indexes**:

- `idx_employees_email`: For fast email lookups
- `idx_employees_employee_id`: For employee ID searches
- `idx_employees_department`: For department-based queries
- `idx_employees_status`: For status filtering
- `idx_employees_manager`: For manager-subordinate relationships

---

### 2. Attendance Table (`attendance`)

**Purpose**: Tracks daily attendance records including check-in, check-out times, and work hours.

**Key Fields**:

- `id`: Primary key (BIGINT AUTO_INCREMENT)
- `employee_id`: Foreign key to employees (BIGINT)
- `date`: Attendance date (DATE)
- `check_in_time`: Check-in time (TIME)
- `check_out_time`: Check-out time (TIME)
- `status`: Attendance status (PRESENT, ABSENT, LATE, etc.)
- `work_hours`: Calculated work hours (DOUBLE)
- `overtime_hours`: Overtime hours (DOUBLE)
- `is_late`: Boolean flag for late arrival
- `late_minutes`: Minutes late (INTEGER)
- `location`: Work location (office/WFH)

**Constraints**:

- Unique constraint on `(employee_id, date)` to prevent duplicate entries

**Indexes**:

- `idx_attendance_employee`: For employee-based queries
- `idx_attendance_date`: For date-based queries
- `idx_attendance_status`: For status filtering
- `idx_attendance_employee_date`: Composite index for common queries

---

### 3. Leave Applications Table (`leave_applications`)

**Purpose**: Manages employee leave applications, approvals, and tracking.

**Key Fields**:

- `id`: Primary key (BIGINT AUTO_INCREMENT)
- `employee_id`: Foreign key to employees (BIGINT)
- `leave_type`: Type of leave (CASUAL_LEAVE, SICK_LEAVE, etc.)
- `start_date`: Leave start date (DATE)
- `end_date`: Leave end date (DATE)
- `total_days`: Total leave days (INTEGER)
- `reason`: Leave reason (TEXT)
- `status`: Application status (PENDING, APPROVED, REJECTED, CANCELLED)
- `approved_by`: Foreign key to employees (BIGINT)
- `approval_date`: Date of approval/rejection (DATE)
- `approval_remarks`: Comments from approver (TEXT)
- `is_half_day`: Boolean flag for half-day leave

**Relationships**:

- `employee_id` → `employees(id)`
- `approved_by` → `employees(id)`

**Indexes**:

- `idx_leave_employee`: For employee leave history
- `idx_leave_status`: For filtering by status
- `idx_leave_dates`: For date range queries
- `idx_leave_approved_by`: For approver-based queries

---

### 4. Payroll Table (`payroll`)

**Purpose**: Stores monthly payroll information including salary components, deductions, and net salary.

**Key Fields**:

- `id`: Primary key (BIGINT AUTO_INCREMENT)
- `employee_id`: Foreign key to employees (BIGINT)
- `salary_month`: Month for which payroll is generated (DATE)
- `basic_salary`: Basic salary component (DOUBLE)
- `hra`: House Rent Allowance (DOUBLE)
- `transport_allowance`: Transport allowance (DOUBLE)
- `medical_allowance`: Medical allowance (DOUBLE)
- `other_allowances`: Other allowances (DOUBLE)
- `overtime_amount`: Overtime payment (DOUBLE)
- `bonus`: Bonus amount (DOUBLE)
- `gross_salary`: Total gross salary (DOUBLE)
- `provident_fund`: PF deduction (DOUBLE)
- `professional_tax`: Professional tax (DOUBLE)
- `income_tax`: Income tax deduction (DOUBLE)
- `other_deductions`: Other deductions (DOUBLE)
- `total_deductions`: Total deductions (DOUBLE)
- `net_salary`: Net salary after deductions (DOUBLE)
- `days_worked`: Number of days worked (INTEGER)
- `days_on_leave`: Number of days on leave (INTEGER)
- `overtime_hours`: Total overtime hours (DOUBLE)
- `is_processed`: Processing status (BOOLEAN)
- `processed_by`: Foreign key to employees (BIGINT)

**Constraints**:

- Unique constraint on `(employee_id, salary_month)` to prevent duplicate payrolls

**Indexes**:

- `idx_payroll_employee`: For employee payroll history
- `idx_payroll_month`: For month-based queries
- `idx_payroll_employee_month`: Composite index for common queries

---

### 5. Performance Reviews Table (`performance_reviews`)

**Purpose**: Stores performance review records with ratings and feedback.

**Key Fields**:

- `id`: Primary key (BIGINT AUTO_INCREMENT)
- `employee_id`: Foreign key to employees being reviewed (BIGINT)
- `reviewer_id`: Foreign key to employees (reviewer) (BIGINT)
- `review_period_start`: Review period start date (DATE)
- `review_period_end`: Review period end date (DATE)
- `overall_rating`: Overall performance rating (DOUBLE, 1-5 scale)
- `technical_skills_rating`: Technical skills rating (DOUBLE)
- `communication_rating`: Communication rating (DOUBLE)
- `teamwork_rating`: Teamwork rating (DOUBLE)
- `leadership_rating`: Leadership rating (DOUBLE)
- `punctuality_rating`: Punctuality rating (DOUBLE)
- `strengths`: Employee strengths (TEXT)
- `areas_for_improvement`: Areas for improvement (TEXT)
- `goals`: Future goals (TEXT)
- `reviewer_comments`: Reviewer comments (TEXT)
- `employee_comments`: Employee acknowledgment comments (TEXT)
- `status`: Review status (DRAFT, SUBMITTED, COMPLETED, ACKNOWLEDGED)
- `review_date`: Date of review completion (DATE)

**Relationships**:

- `employee_id` → `employees(id)`
- `reviewer_id` → `employees(id)`

**Indexes**:

- `idx_performance_employee`: For employee review history
- `idx_performance_reviewer`: For reviewer-based queries
- `idx_performance_status`: For status filtering
- `idx_performance_period`: For period-based queries

---

## Common Fields

All tables include the following audit fields:

- `created_at`: Timestamp of record creation
- `updated_at`: Timestamp of last update (auto-updated via trigger)
- `created_by`: User who created the record
- `updated_by`: User who last updated the record
- `version`: Optimistic locking version field

## Triggers

### Update Timestamp Trigger

MySQL automatically updates the `updated_at` field using `ON UPDATE CURRENT_TIMESTAMP` whenever a record is modified. This is configured at the table level for all tables.

## Data Integrity

### Foreign Key Constraints

- All foreign key relationships use `ON DELETE CASCADE` where appropriate to maintain referential integrity
- Self-referential relationships (e.g., manager-employee) use standard foreign key constraints

### Unique Constraints

- Employee email: Unique across all employees
- Employee ID: Unique employee identifier
- Attendance: Unique per employee per date
- Payroll: Unique per employee per month

## Performance Considerations

1. **Indexing Strategy**:

   - Primary keys are automatically indexed
   - Foreign keys are indexed for join performance
   - Composite indexes on frequently queried column combinations
   - Status and date fields are indexed for filtering

2. **Query Optimization**:
   - Use appropriate indexes for common query patterns
   - Consider partitioning for large tables (attendance, payroll) by date ranges
   - Regular VACUUM and ANALYZE operations recommended

## Security Considerations

1. **Password Storage**: Passwords are hashed using BCrypt before storage
2. **Data Access**: Application-level security controls access to sensitive data
3. **Audit Trail**: Created/updated by fields track data modifications

## Backup and Recovery

- Regular database backups recommended
- Transaction logs should be archived for point-in-time recovery
- Consider replication for high availability

## Future Enhancements

Potential schema enhancements:

1. Separate table for leave balances
2. Document storage for payroll slips and performance reviews
3. Notification/audit log table
4. Department and role configuration tables
5. Holiday calendar table
6. Shift management table
