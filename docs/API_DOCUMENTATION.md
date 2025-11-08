# WorkZen HRMS API Documentation

## Base URL

```
http://localhost:8080/api
```

## Authentication

Most endpoints require JWT authentication. Include the token in the Authorization header:

```
Authorization: Bearer <your_jwt_token>
```

---

## Authentication APIs

### 1. Login

**POST** `/api/auth/login`

Authenticate user and receive JWT token.

**Request Body:**

```json
{
  "email": "admin@workzen.com",
  "password": "admin123"
}
```

**Response:**

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "email": "admin@workzen.com",
    "employeeId": "ADMIN001",
    "fullName": "Admin User",
    "role": "ADMIN",
    "employeeDbId": 1
  }
}
```

---

## Employee APIs

### 1. Create Employee

**POST** `/api/employees`

Create a new employee.

**Request Body:**

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@workzen.com",
  "password": "password123",
  "phoneNumber": "+1234567890",
  "dateOfBirth": "1990-01-15",
  "gender": "Male",
  "address": "123 Main St, City, State",
  "dateOfJoining": "2024-01-01",
  "department": "INFORMATION_TECHNOLOGY",
  "role": "SENIOR_DEVELOPER",
  "salary": 75000.0,
  "designation": "Senior Software Engineer",
  "managerId": 1,
  "emergencyContactName": "Jane Doe",
  "emergencyContactPhone": "+1234567891",
  "emergencyContactRelation": "Spouse",
  "bankAccountNumber": "1234567890",
  "bankName": "ABC Bank",
  "ifscCode": "ABC0001234",
  "panNumber": "ABCDE1234F",
  "aadharNumber": "123456789012"
}
```

**Response:**

```json
{
  "success": true,
  "message": "Employee created successfully",
  "data": {
    "id": 2,
    "employeeId": "INF2024011234",
    "firstName": "John",
    "lastName": "Doe",
    ...
  }
}
```

### 2. Get Employee by ID

**GET** `/api/employees/{id}`

Get employee details by database ID.

### 3. Get Employee by Employee ID

**GET** `/api/employees/employee-id/{employeeId}`

Get employee details by employee ID (e.g., "ADMIN001").

### 4. Get All Employees

**GET** `/api/employees?page=0&size=10&sortBy=id&sortDir=ASC`

Get paginated list of all employees.

**Query Parameters:**

- `page`: Page number (default: 0)
- `size`: Page size (default: 10)
- `sortBy`: Sort field (default: "id")
- `sortDir`: Sort direction - ASC or DESC (default: "ASC")

### 5. Search Employees

**GET** `/api/employees/search?searchTerm=john&page=0&size=10`

Search employees by name, email, or employee ID.

**Query Parameters:**

- `searchTerm`: Search keyword
- `page`: Page number
- `size`: Page size

### 6. Get Employees by Department

**GET** `/api/employees/department/{department}`

Get all employees in a specific department.

**Path Parameters:**

- `department`: Department enum value (e.g., "INFORMATION_TECHNOLOGY")

### 7. Get Employees by Status

**GET** `/api/employees/status/{status}`

Get all employees with a specific status.

**Path Parameters:**

- `status`: Employee status (e.g., "ACTIVE", "INACTIVE")

### 8. Get Subordinates

**GET** `/api/employees/manager/{managerId}/subordinates`

Get all subordinates of a manager.

### 9. Update Employee

**PUT** `/api/employees/{id}`

Update employee information.

**Request Body:** Same as Create Employee

### 10. Update Employee Status

**PATCH** `/api/employees/{id}/status?status=INACTIVE`

Update employee status.

**Query Parameters:**

- `status`: New status value

### 11. Delete Employee

**DELETE** `/api/employees/{id}`

Soft delete employee (sets status to TERMINATED).

---

## Attendance APIs

### 1. Mark Check-In

**POST** `/api/attendance/check-in?employeeId=1&location=Office`

Mark check-in for an employee.

**Query Parameters:**

- `employeeId`: Employee database ID
- `location`: Optional location (Office/WFH)

**Response:**

```json
{
  "success": true,
  "message": "Check-in marked successfully",
  "data": {
    "id": 1,
    "employeeId": 1,
    "employeeName": "John Doe",
    "date": "2024-01-15",
    "checkInTime": "09:15:00",
    "status": "LATE",
    "isLate": true,
    "lateMinutes": 15
  }
}
```

### 2. Mark Check-Out

**POST** `/api/attendance/check-out?employeeId=1`

Mark check-out for an employee.

### 3. Create Attendance

**POST** `/api/attendance`

Manually create attendance record.

**Request Body:**

```json
{
  "employeeId": 1,
  "date": "2024-01-15",
  "checkInTime": "09:00:00",
  "checkOutTime": "18:00:00",
  "status": "PRESENT",
  "location": "Office",
  "remarks": "Regular attendance"
}
```

### 4. Get Attendance by ID

**GET** `/api/attendance/{id}`

Get attendance record by ID.

### 5. Get Attendance by Employee

**GET** `/api/attendance/employee/{employeeId}?page=0&size=10`

Get paginated attendance records for an employee.

### 6. Get Attendance by Date Range

**GET** `/api/attendance/employee/{employeeId}/range?startDate=2024-01-01&endDate=2024-01-31`

Get attendance records for an employee within a date range.

### 7. Get Monthly Attendance

**GET** `/api/attendance/employee/{employeeId}/monthly?year=2024&month=1`

Get attendance records for a specific month.

### 8. Get Attendance by Date

**GET** `/api/attendance/date/2024-01-15`

Get all attendance records for a specific date.

### 9. Update Attendance

**PUT** `/api/attendance/{id}`

Update attendance record.

### 10. Delete Attendance

**DELETE** `/api/attendance/{id}`

Delete attendance record.

---

## Leave Application APIs

### 1. Create Leave Application

**POST** `/api/leaves`

Create a new leave application.

**Request Body:**

```json
{
  "employeeId": 1,
  "leaveType": "CASUAL_LEAVE",
  "startDate": "2024-02-01",
  "endDate": "2024-02-03",
  "reason": "Personal work",
  "isHalfDay": false
}
```

**Response:**

```json
{
  "success": true,
  "message": "Leave application created successfully",
  "data": {
    "id": 1,
    "employeeId": 1,
    "employeeName": "John Doe",
    "leaveType": "CASUAL_LEAVE",
    "startDate": "2024-02-01",
    "endDate": "2024-02-03",
    "totalDays": 3,
    "status": "PENDING"
  }
}
```

### 2. Get Leave Application by ID

**GET** `/api/leaves/{id}`

Get leave application details.

### 3. Get Leave Applications by Employee

**GET** `/api/leaves/employee/{employeeId}?page=0&size=10`

Get paginated leave applications for an employee.

### 4. Get Leave Applications by Year

**GET** `/api/leaves/employee/{employeeId}/year/{year}`

Get all leave applications for an employee in a specific year.

### 5. Get Pending Leave Applications

**GET** `/api/leaves/pending?page=0&size=10`

Get all pending leave applications.

### 6. Get Pending Approvals

**GET** `/api/leaves/approver/{approverId}/pending`

Get pending leave applications for an approver.

### 7. Approve Leave Application

**POST** `/api/leaves/{id}/approve?approverId=2&remarks=Approved`

Approve a leave application.

**Query Parameters:**

- `approverId`: ID of the approver
- `remarks`: Optional approval remarks

### 8. Reject Leave Application

**POST** `/api/leaves/{id}/reject?approverId=2&remarks=Not approved`

Reject a leave application.

### 9. Cancel Leave Application

**POST** `/api/leaves/{id}/cancel`

Cancel a leave application.

### 10. Get Used Leave Days

**GET** `/api/leaves/employee/{employeeId}/used-days?leaveType=CASUAL_LEAVE&year=2024`

Get number of used leave days for a specific leave type and year.

---

## Payroll APIs

### 1. Generate Payroll

**POST** `/api/payroll/generate?employeeId=1&salaryMonth=2024-01-01&processedById=2`

Generate payroll for an employee for a specific month.

**Query Parameters:**

- `employeeId`: Employee database ID
- `salaryMonth`: Month for payroll (format: YYYY-MM-01)
- `processedById`: Optional ID of employee processing the payroll

**Response:**

```json
{
  "success": true,
  "message": "Payroll generated successfully",
  "data": {
    "id": 1,
    "employeeId": 1,
    "employeeName": "John Doe",
    "salaryMonth": "2024-01-01",
    "basicSalary": 37500.0,
    "hra": 15000.0,
    "grossSalary": 75000.0,
    "netSalary": 65000.0,
    "daysWorked": 22,
    "daysOnLeave": 1
  }
}
```

### 2. Get Payroll by ID

**GET** `/api/payroll/{id}`

Get payroll details by ID.

### 3. Get Payroll by Employee and Month

**GET** `/api/payroll/employee/{employeeId}/month?salaryMonth=2024-01-01`

Get payroll for an employee for a specific month.

### 4. Get Payrolls by Employee

**GET** `/api/payroll/employee/{employeeId}?page=0&size=10`

Get paginated payroll history for an employee.

### 5. Get Payrolls by Year

**GET** `/api/payroll/employee/{employeeId}/year/{year}`

Get all payrolls for an employee in a specific year.

### 6. Get Payrolls by Month

**GET** `/api/payroll/month?year=2024&month=1`

Get all payrolls for a specific month across all employees.

---

## Performance Review APIs

### 1. Create Performance Review

**POST** `/api/performance-reviews`

Create a new performance review.

**Request Body:**

```json
{
  "employeeId": 1,
  "reviewerId": 2,
  "reviewPeriodStart": "2024-01-01",
  "reviewPeriodEnd": "2024-03-31",
  "technicalSkillsRating": 4.5,
  "communicationRating": 4.0,
  "teamworkRating": 4.5,
  "leadershipRating": 3.5,
  "punctualityRating": 5.0,
  "strengths": "Excellent technical skills and punctuality",
  "areasForImprovement": "Leadership skills need development",
  "goals": "Take on more leadership responsibilities",
  "reviewerComments": "Good performance overall"
}
```

**Response:**

```json
{
  "success": true,
  "message": "Performance review created successfully",
  "data": {
    "id": 1,
    "employeeId": 1,
    "employeeName": "John Doe",
    "reviewerId": 2,
    "reviewerName": "Manager Name",
    "overallRating": 4.3,
    "status": "DRAFT"
  }
}
```

### 2. Get Performance Review by ID

**GET** `/api/performance-reviews/{id}`

Get performance review details.

### 3. Get Performance Reviews by Employee

**GET** `/api/performance-reviews/employee/{employeeId}?page=0&size=10`

Get paginated performance reviews for an employee.

### 4. Get Performance Reviews by Reviewer

**GET** `/api/performance-reviews/reviewer/{reviewerId}?page=0&size=10`

Get paginated performance reviews by a reviewer.

### 5. Get Pending Reviews

**GET** `/api/performance-reviews/reviewer/{reviewerId}/pending`

Get pending reviews for a reviewer.

### 6. Submit Review

**POST** `/api/performance-reviews/{id}/submit`

Submit a draft review for completion.

### 7. Complete Review

**POST** `/api/performance-reviews/{id}/complete`

Mark a review as completed.

### 8. Acknowledge Review

**POST** `/api/performance-reviews/{id}/acknowledge?employeeComments=Thank you for the feedback`

Acknowledge a completed review by the employee.

### 9. Update Review

**PUT** `/api/performance-reviews/{id}`

Update a draft review.

### 10. Get Average Rating

**GET** `/api/performance-reviews/employee/{employeeId}/average-rating`

Get average performance rating for an employee.

---

## Common Response Format

All APIs return responses in the following format:

**Success Response:**

```json
{
  "success": true,
  "message": "Optional success message",
  "data": { ... }
}
```

**Error Response:**

```json
{
  "success": false,
  "error": "Error message describing what went wrong"
}
```

---

## Error Codes

The API uses standard HTTP status codes:

- `200 OK`: Request successful
- `201 Created`: Resource created successfully
- `400 Bad Request`: Invalid request data
- `401 Unauthorized`: Authentication required or invalid token
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server error

---

## Enum Values

### Department

- `HUMAN_RESOURCES`
- `INFORMATION_TECHNOLOGY`
- `FINANCE`
- `OPERATIONS`
- `MARKETING`
- `SALES`
- `RESEARCH_DEVELOPMENT`
- `QUALITY_ASSURANCE`
- `ADMINISTRATION`
- `LEGAL`
- `SECURITY`
- `FACILITIES`

### Role

- `SUPER_ADMIN`, `ADMIN`
- `HR_MANAGER`, `HR_EXECUTIVE`, `RECRUITER`
- `CEO`, `CTO`, `CFO`, `DEPARTMENT_HEAD`, `TEAM_LEAD`, `PROJECT_MANAGER`
- `SENIOR_DEVELOPER`, `JUNIOR_DEVELOPER`, `BUSINESS_ANALYST`, `QA_ENGINEER`, `DEVOPS_ENGINEER`, `UI_UX_DESIGNER`
- `FINANCE_MANAGER`, `ACCOUNTANT`, `PAYROLL_SPECIALIST`
- And more...

### EmployeeStatus

- `ACTIVE`
- `INACTIVE`
- `ON_LEAVE`
- `SUSPENDED`
- `TERMINATED`
- `RESIGNED`
- `PROBATION`
- `NOTICE_PERIOD`
- `RETIRED`

### AttendanceStatus

- `PRESENT`
- `ABSENT`
- `HALF_DAY`
- `LATE`
- `ON_LEAVE`
- `WORK_FROM_HOME`
- `ON_DUTY`

### LeaveType

- `CASUAL_LEAVE`
- `SICK_LEAVE`
- `EARNED_LEAVE`
- `MATERNITY_LEAVE`
- `PATERNITY_LEAVE`
- `EMERGENCY_LEAVE`
- `BEREAVEMENT_LEAVE`
- `STUDY_LEAVE`
- `SABBATICAL_LEAVE`
- `UNPAID_LEAVE`

### LeaveStatus

- `PENDING`
- `APPROVED`
- `REJECTED`
- `CANCELLED`

### ReviewStatus

- `DRAFT`
- `SUBMITTED`
- `COMPLETED`
- `ACKNOWLEDGED`

---

## Rate Limiting

Currently, there are no rate limits implemented. Consider implementing rate limiting for production use.

---

## Versioning

The API is currently at version 1.0. Future versions may be introduced with versioning in the URL path (e.g., `/api/v2/...`).

---

## Support

For API support or questions, please contact the development team.
