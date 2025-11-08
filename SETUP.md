# WorkZen HRMS Setup Guide

## Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher (or MariaDB 10.3+)
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

## Database Setup

### 1. Install MySQL

Install MySQL on your system if not already installed.

**On macOS with Homebrew:**

```bash
brew install mysql
brew services start mysql
```

**On Linux (Ubuntu/Debian):**

```bash
sudo apt-get update
sudo apt-get install mysql-server
sudo systemctl start mysql
```

**On Windows:**
Download and install MySQL from https://dev.mysql.com/downloads/mysql/

### 2. Create Database

```sql
CREATE DATABASE IF NOT EXISTS workzen_hrms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE workzen_hrms;
```

Or using command line:

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS workzen_hrms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

### 3. Run Database Scripts

Execute the initialization script:

```bash
mysql -u root -p workzen_hrms < database/init.sql
```

Or connect to MySQL and run:

```bash
mysql -u root -p
USE workzen_hrms;
SOURCE database/init.sql;
```

### 4. Update Database Configuration

Update `src/main/resources/application.properties` with your database credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/workzen_hrms?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
```

## Application Setup

### 1. Clone/Download the Project

Ensure you have the project files in your workspace.

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run the Application

**Option 1: Using Maven**

```bash
mvn spring-boot:run
```

**Option 2: Using Java**

```bash
java -jar target/workzen-hrms-0.0.1-SNAPSHOT.jar
```

**Option 3: Using IDE**

- Open the project in your IDE
- Run `WorkZenHrmsApplication.java` as a Spring Boot application

### 4. Verify Application is Running

The application should start on `http://localhost:8080`

You can test the login endpoint:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@workzen.com","password":"admin123"}'
```

## Default Admin User

After running the database initialization script, a default admin user is created:

- **Email**: `admin@workzen.com`
- **Password**: `admin123`
- **Employee ID**: `ADMIN001`

**Note**: Change the default password after first login in production!

## API Testing

### Using cURL

**Login:**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@workzen.com","password":"admin123"}'
```

**Get Employees (with token):**

```bash
curl -X GET http://localhost:8080/api/employees \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Using Postman

1. Import the API collection (if available)
2. Set the base URL: `http://localhost:8080/api`
3. Login first to get JWT token
4. Use the token in Authorization header for subsequent requests

## Project Structure

```
workzen-hrms/
├── src/
│   ├── main/
│   │   ├── java/com/workzen/
│   │   │   ├── config/          # Security and JPA configuration
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── enums/           # Enum classes
│   │   │   ├── repository/       # JPA repositories
│   │   │   ├── service/          # Business logic services
│   │   │   └── util/             # Utility classes (JWT)
│   │   └── resources/
│   │       └── application.properties
│   └── test/                     # Test files
├── database/
│   ├── init.sql                  # Database initialization script
│   └── DATABASE_DESIGN.md       # Database design documentation
├── docs/
│   └── API_DOCUMENTATION.md      # API documentation
├── pom.xml                       # Maven dependencies
└── README.md                     # Project README
```

## Configuration

### JWT Configuration

Update JWT secret and expiration in `application.properties`:

```properties
jwt.secret=YourSecretKeyHere
jwt.expiration=86400000  # 24 hours in milliseconds
```

### Server Port

Change server port if needed:

```properties
server.port=8080
```

## Troubleshooting

### Database Connection Issues

1. Verify MySQL is running:

   ```bash
   mysql -u root -p -e "SELECT VERSION();"
   ```

2. Check database credentials in `application.properties`

3. Ensure database `workzen_hrms` exists:

   ```bash
   mysql -u root -p -e "SHOW DATABASES LIKE 'workzen_hrms';"
   ```

4. If connection fails, try:
   ```bash
   mysql -u root -p
   ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'your_password';
   FLUSH PRIVILEGES;
   ```

### Port Already in Use

If port 8080 is already in use:

1. Change port in `application.properties`:

   ```properties
   server.port=8081
   ```

2. Or stop the process using port 8080

### JWT Token Issues

1. Verify JWT secret is set correctly
2. Check token expiration time
3. Ensure token is included in Authorization header with "Bearer " prefix

### Build Issues

1. Clean and rebuild:

   ```bash
   mvn clean install
   ```

2. Check Java version:

   ```bash
   java -version  # Should be 21 or higher
   ```

3. Update Maven dependencies:
   ```bash
   mvn dependency:resolve
   ```

## Development Tips

1. **Enable SQL Logging**: Already enabled in `application.properties` for debugging
2. **Use Hibernate DDL**: Set `spring.jpa.hibernate.ddl-auto=update` for automatic schema updates
3. **Database Migrations**: Consider using Flyway or Liquibase for production
4. **API Testing**: Use Postman or create integration tests

## Next Steps

1. Review the API documentation in `docs/API_DOCUMENTATION.md`
2. Review the database design in `database/DATABASE_DESIGN.md`
3. Test all endpoints using Postman or cURL
4. Customize configurations as needed
5. Add more business logic as required

## Support

For issues or questions, refer to:

- API Documentation: `docs/API_DOCUMENTATION.md`
- Database Design: `database/DATABASE_DESIGN.md`
- Project README: `README.md`
