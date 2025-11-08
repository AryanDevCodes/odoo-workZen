-- WorkZen HRMS Database Initialization Script
-- MySQL Database Schema

-- Create Database (run this separately if needed)
-- CREATE DATABASE IF NOT EXISTS workzen_hrms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE workzen_hrms;

-- ============================================
-- EMPLOYEES TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS employees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(50) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) UNIQUE,
    date_of_birth DATE,
    gender VARCHAR(20),
    address TEXT,
    date_of_joining DATE,
    department VARCHAR(50),
    role VARCHAR(50),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    salary DOUBLE,
    designation VARCHAR(100),
    manager_id BIGINT,
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    emergency_contact_relation VARCHAR(50),
    bank_account_number VARCHAR(50),
    bank_name VARCHAR(100),
    ifsc_code VARCHAR(20),
    pan_number VARCHAR(20),
    aadhar_number VARCHAR(20),
    profile_picture_url VARCHAR(500),
    is_account_non_expired TINYINT(1) DEFAULT 1,
    is_account_non_locked TINYINT(1) DEFAULT 1,
    is_credentials_non_expired TINYINT(1) DEFAULT 1,
    is_enabled TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (manager_id) REFERENCES employees(id) ON DELETE SET NULL,
    INDEX idx_employees_email (email),
    INDEX idx_employees_employee_id (employee_id),
    INDEX idx_employees_department (department),
    INDEX idx_employees_status (status),
    INDEX idx_employees_manager (manager_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- ATTENDANCE TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    date DATE NOT NULL,
    check_in_time TIME,
    check_out_time TIME,
    status VARCHAR(50) DEFAULT 'ABSENT',
    work_hours DOUBLE,
    overtime_hours DOUBLE,
    remarks TEXT,
    is_late TINYINT(1) DEFAULT 0,
    late_minutes INT,
    location VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    UNIQUE KEY unique_employee_date (employee_id, date),
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    INDEX idx_attendance_employee (employee_id),
    INDEX idx_attendance_date (date),
    INDEX idx_attendance_status (status),
    INDEX idx_attendance_employee_date (employee_id, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- LEAVE_APPLICATIONS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS leave_applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    leave_type VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_days INT NOT NULL,
    reason TEXT,
    status VARCHAR(50) DEFAULT 'PENDING',
    approved_by BIGINT,
    approval_date DATE,
    approval_remarks TEXT,
    is_half_day TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES employees(id) ON DELETE SET NULL,
    INDEX idx_leave_employee (employee_id),
    INDEX idx_leave_status (status),
    INDEX idx_leave_dates (start_date, end_date),
    INDEX idx_leave_approved_by (approved_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- PAYROLL TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS payroll (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    salary_month DATE NOT NULL,
    basic_salary DOUBLE NOT NULL,
    hra DOUBLE,
    transport_allowance DOUBLE,
    medical_allowance DOUBLE,
    other_allowances DOUBLE,
    overtime_amount DOUBLE,
    bonus DOUBLE,
    gross_salary DOUBLE NOT NULL,
    provident_fund DOUBLE,
    professional_tax DOUBLE,
    income_tax DOUBLE,
    other_deductions DOUBLE,
    total_deductions DOUBLE NOT NULL,
    net_salary DOUBLE NOT NULL,
    days_worked INT,
    days_on_leave INT,
    overtime_hours DOUBLE,
    is_processed TINYINT(1) DEFAULT 0,
    processed_date DATE,
    processed_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    UNIQUE KEY unique_employee_month (employee_id, salary_month),
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    FOREIGN KEY (processed_by) REFERENCES employees(id) ON DELETE SET NULL,
    INDEX idx_payroll_employee (employee_id),
    INDEX idx_payroll_month (salary_month),
    INDEX idx_payroll_employee_month (employee_id, salary_month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- PERFORMANCE_REVIEWS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS performance_reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    reviewer_id BIGINT NOT NULL,
    review_period_start DATE NOT NULL,
    review_period_end DATE NOT NULL,
    overall_rating DOUBLE,
    technical_skills_rating DOUBLE,
    communication_rating DOUBLE,
    teamwork_rating DOUBLE,
    leadership_rating DOUBLE,
    punctuality_rating DOUBLE,
    strengths TEXT,
    areas_for_improvement TEXT,
    goals TEXT,
    reviewer_comments TEXT,
    employee_comments TEXT,
    status VARCHAR(50) DEFAULT 'DRAFT',
    review_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewer_id) REFERENCES employees(id) ON DELETE RESTRICT,
    INDEX idx_performance_employee (employee_id),
    INDEX idx_performance_reviewer (reviewer_id),
    INDEX idx_performance_status (status),
    INDEX idx_performance_period (review_period_start, review_period_end)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- SAMPLE DATA (Optional - for testing)
-- ============================================
-- Insert a default admin user (password: admin123)
-- Password hash for 'admin123' using BCrypt
INSERT INTO employees (
    employee_id, first_name, last_name, email, password, 
    date_of_joining, department, role, status, salary, designation,
    is_enabled
) VALUES (
    'ADMIN001', 'Admin', 'User', 'admin@workzen.com', 
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    CURDATE(), 'ADMINISTRATION', 'ADMIN', 'ACTIVE', 100000.0, 'System Administrator',
    1
) ON DUPLICATE KEY UPDATE email = email;
