# WorkZen HRMS Postman Collection

This directory contains Postman collections and environment files for testing the WorkZen HRMS API.

## Files

1. **WorkZen_HRMS.postman_collection.json** - Complete API collection with all endpoints
2. **WorkZen_HRMS_Environment.postman_environment.json** - Environment variables for local development

## Setup Instructions

### 1. Import Collection and Environment

1. Open Postman
2. Click **Import** button (top left)
3. Select both files:
   - `WorkZen_HRMS.postman_collection.json`
   - `WorkZen_HRMS_Environment.postman_environment.json`
4. Click **Import**

### 2. Configure Environment

1. In Postman, click on **Environments** (left sidebar)
2. Select **WorkZen HRMS - Local**
3. Verify the following variables:
   - `base_url`: `http://localhost:8080`
   - `auth_token`: (will be auto-populated after login)
   - `employee_id`: (will be auto-populated after login)

### 3. Start the Application

Make sure your Spring Boot application is running:

```bash
mvn spring-boot:run
```

The application should be accessible at `http://localhost:8080`

### 4. Test the API

#### Step 1: Login

1. Navigate to **Authentication** → **Login**
2. The request body is pre-filled with default admin credentials:
   ```json
   {
     "email": "admin@workzen.com",
     "password": "admin123"
   }
   ```
3. Click **Send**
4. The `auth_token` will be automatically saved to the environment (via test script)

#### Step 2: Use Authenticated Endpoints

1. All other endpoints will automatically use the `auth_token` from the environment
2. The token is included in the `Authorization` header as: `Bearer {{auth_token}}`

## Collection Structure

The collection is organized into the following folders:

### 1. Authentication

- **Login** - Authenticate and get JWT token

### 2. Employees

- Create Employee
- Get Employee by ID
- Get Employee by Employee ID
- Get All Employees (paginated)
- Search Employees
- Get Employees by Department
- Get Employees by Status
- Get Subordinates
- Update Employee
- Update Employee Status
- Delete Employee

### 3. Attendance

- Mark Check-In
- Mark Check-Out
- Create Attendance
- Get Attendance by ID
- Get Attendance by Employee
- Get Attendance by Date Range
- Get Monthly Attendance
- Get Attendance by Date
- Update Attendance
- Delete Attendance

### 4. Leave Applications

- Create Leave Application
- Get Leave Application by ID
- Get Leave Applications by Employee
- Get Leave Applications by Year
- Get Pending Leave Applications
- Get Pending Approvals
- Approve Leave Application
- Reject Leave Application
- Cancel Leave Application
- Get Used Leave Days

### 5. Payroll

- Generate Payroll
- Get Payroll by ID
- Get Payroll by Employee and Month
- Get Payrolls by Employee
- Get Payrolls by Year
- Get Payrolls by Month

### 6. Performance Reviews

- Create Performance Review
- Get Performance Review by ID
- Get Performance Reviews by Employee
- Get Performance Reviews by Reviewer
- Get Pending Reviews
- Submit Review
- Complete Review
- Acknowledge Review
- Update Review
- Get Average Rating

## Testing Workflow

### Recommended Testing Order:

1. **Authentication**

   - Login to get JWT token

2. **Employees**

   - Create a new employee
   - Get employee details
   - Update employee
   - Test search and filtering

3. **Attendance**

   - Mark check-in for an employee
   - Mark check-out
   - View attendance records

4. **Leave Applications**

   - Create a leave application
   - Approve/reject leave
   - View leave history

5. **Payroll**

   - Generate payroll for an employee
   - View payroll details

6. **Performance Reviews**
   - Create a performance review
   - Submit and complete review
   - View review history

## Environment Variables

### base_url

- **Default**: `http://localhost:8080`
- **Description**: Base URL of the API server
- **Usage**: Used in all request URLs

### auth_token

- **Default**: (empty)
- **Description**: JWT authentication token
- **Auto-populated**: Yes (after successful login)
- **Usage**: Included in Authorization header for authenticated requests

### employee_id

- **Default**: (empty)
- **Description**: Current employee's database ID
- **Auto-populated**: Yes (after successful login)
- **Usage**: Used in requests that require employee ID

## Customization

### Change Base URL

To test against a different server:

1. Select **WorkZen HRMS - Local** environment
2. Update `base_url` variable:
   - Development: `http://localhost:8080`
   - Production: `https://api.workzen.com`

### Update Default Values

You can modify request bodies and query parameters in the collection:

1. Open any request
2. Update the request body or query parameters
3. Click **Save**

## Troubleshooting

### Token Not Saved

- Check that the Login request's test script is enabled
- Verify the response structure matches the expected format
- Check Postman console for errors

### 401 Unauthorized

- Verify you've logged in first
- Check that `auth_token` is set in the environment
- Ensure the token hasn't expired (default: 24 hours)
- Try logging in again

### Connection Refused

- Verify the application is running
- Check the `base_url` is correct
- Ensure the port (8080) is not blocked by firewall

### 404 Not Found

- Verify the endpoint path is correct
- Check that the application context path matches
- Ensure the controller is properly mapped

## Notes

- All authenticated endpoints require a valid JWT token
- The token is automatically included via the `Authorization` header
- Token expiration is set to 24 hours (configurable in application.properties)
- Some endpoints require specific roles (Admin, HR, etc.)
- Date formats: Use `YYYY-MM-DD` for dates, `HH:mm:ss` for times
- Pagination: Default page size is 10, can be adjusted via query parameters

## Support

For API documentation, see:

- `docs/API_DOCUMENTATION.md` - Complete API reference
- `SETUP.md` - Application setup guide
