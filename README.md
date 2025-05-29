# Rwanda ERP Payroll System Backend

This project is a Spring Boot-based backend for a robust payroll management system designed for an Enterprise Resource Planning (ERP) solution for the Government of Rwanda. It handles employee information, employment details, deduction management, automated payroll generation, payslip approval, and notification messages, all secured with JWT authentication and role-based authorization.

---

## ✨ Features

* **Employee Management**: CRUD operations for employee personal details (First Name, Last Name, Email, Mobile, Date of Birth, Employee Code, Status).
* **Role-Based Access Control**: Assign roles (Admin, Manager, Employee) to users for granular access control.
* **Employment Management**: Manage employment details for each employee (Department, Position, Base Salary, Status, Joining Date).
* **Deduction Configuration**: Define and manage various payroll deductions by name and percentage (e.g., Employee Tax, Pension, Medical Insurance, Housing, Transport, Others).
* **Automated Payslip Generation**: Generate payslips for all active employees for a specified month and year, calculating gross and net salaries based on defined deductions and allowances.
* **Payslip Approval Workflow**: Approve generated payslips, transitioning their status from `PENDING` to `PAID`.
* **Message Generation**: Automatically generate and store notification messages for employees upon payslip approval.
* **JWT Authentication**: Secure API endpoints using JSON Web Tokens for authentication.
* **Swagger/OpenAPI Documentation**: Interactive API documentation for easy testing and understanding of endpoints.

---

## 🛠️ Technologies Used

* **Spring Boot**: Framework for building the backend application.
* **Spring Data JPA**: For database interaction and ORM.
* **Spring Security**: For authentication and authorization (JWT-based).
* **JJWT**: Java library for JSON Web Tokens.
* **MySQL**: Relational database for persistent data storage.
* **H2 Database**: In-memory database option for quick development and testing.
* **Maven**: Dependency management and build tool.
* **Lombok**: Reduces boilerplate code (e.g., getters, setters, constructors).
* **Swagger (Springdoc OpenAPI)**: API documentation.
* **Jackson**: For JSON processing.
* **Jakarta Validation (Bean Validation)**: For request DTO validation.

---

## 📂 Project Structure

```
rwanda-erp-payroll/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── rwanda/
│   │   │           └── erp/
│   │   │               └── payroll/
│   │   │                   ├── config/          # Spring Security, Web (CORS) configurations
│   │   │                   ├── controller/      # REST API Endpoints
│   │   │                   ├── dto/             # Data Transfer Objects (Request/Response)
│   │   │                   ├── exception/       # Custom Exception Handling
│   │   │                   ├── model/           # JPA Entities (Database Models)
│   │   │                   ├── repository/      # Spring Data JPA Repositories
│   │   │                   ├── security/        # JWT components (Filter, Provider, EntryPoint, UserDetailsService)
│   │   │                   └── service/         # Business Logic Layer
│   │   └── resources/
│   │       ├── application.properties # Application configurations (DB, JWT, Server Port)
│   │       └── data.sql               # Optional: Initial data seeding
│   └── test/
│       └── java/
│           └── com/
│               └── rwanda/
│                   └── erp/
│                       └── payroll/
│                           └── ...              # Unit and Integration Tests
├── pom.xml                                  # Maven Project Object Model
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

* **Java Development Kit (JDK)**: Version 17 or higher.
* **Maven**: Version 3.8.x or higher.
* **MySQL Database**: (Optional, if using MySQL) MySQL server running locally or accessible.
* **Git**: For cloning the repository.
* **Postman** or any API client for testing endpoints.

### 1. Clone the Repository

```bash
git clone <repository-url>
cd rwanda-erp-payroll
```

### 2. Database Setup

#### Using MySQL (Recommended for full features)

1.  **Create a MySQL Database**:
    ```sql
    CREATE DATABASE rwanda_erp_payroll;
    ```
2.  **Update `application.properties`**: Open `src/main/resources/application.properties` and configure your MySQL connection details.
    ```properties
    spring.application.name=employeepayroll
    server.port=8080
    
    # Spring Data JPA and Database Configuration (MySQL example)
    spring.datasource.url=jdbc:mysql://localhost:3306/rwanda_erp_payroll?useSSL=false&serverTimezone=UTC
    spring.datasource.username=root
    spring.datasource.password=Password123!
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    
    # JPA Properties
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
    spring.jpa.properties.hibernate.format_sql=true
    
    
    app.jwt-secret=your_jwt_secret
    app.jwt-expiration-milliseconds=jwt_expiration in milliseconds
    
    # Springdoc OpenAPI (Swagger UI) Configuration
    springdoc.swagger-ui.path=/swagger-ui.html
    springdoc.api-docs.path=/v3/api-docs
    springdoc.swagger-ui.operationsSorter=method
    springdoc.swagger-ui.disable-swagger-default-url=true
    
    #email configuration
    spring.mail.host=smtp.gmail.com
    spring.mail.port=587
    spring.mail.username=your_email@gmail.com
    spring.mail.password=your-app-specific-password
    spring.mail.properties.mail.smtp.auth=true
    spring.mail.properties.mail.smtp.starttls.enable=true
    ```
    **Important**: Remember to replace `your_mysql_password` with your actual MySQL password and `yourSuperSecretJwtKeyThatIsAtLeast256BitsLongAndShouldBeStrongAndRandomlyGeneratedForProductionEnvironment` with a strong, random key.

#### Using H2 In-Memory Database (for quick local development)

If you prefer to use H2 for quick testing without a separate MySQL setup, uncomment the H2 properties in `src/main/resources/application.properties` and comment out the MySQL ones:

```properties
# H2 In-Memory Database (Uncomment these for H2)
spring.datasource.url=jdbc:h2:mem:erppayrolldb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true # Enable H2 console at http://localhost:8080/h2-console
```

### 3. Build the Project

Navigate to the project root directory (`rwanda-erp-payroll`) and run:

```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```
The application will start on `http://localhost:8080` by default.

---

## 🔑 Authentication and Authorization

This project uses JWT (JSON Web Tokens) for securing its APIs.

* **Registration**: New users (employees) can be registered via the `/api/auth/register` endpoint.
* **Login**: Authenticated users can obtain a JWT token by sending their credentials to the `/api/auth/login` endpoint.
* **Token Usage**: Include the obtained JWT token in the `Authorization` header of subsequent requests as `Bearer <YOUR_JWT_TOKEN>`.
* **Roles**: The system supports `ROLE_ADMIN`, `ROLE_MANAGER`, and `ROLE_EMPLOYEE`. These roles must exist in the `roles` table in your database.
    * **Data Seeding**: If your `roles` table is empty, you can manually insert them or use a `data.sql` file in `src/main/resources` with the following content:
        ```sql
        INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
        INSERT INTO roles (name) VALUES ('ROLE_MANAGER');
        INSERT INTO roles (name) VALUES ('ROLE_EMPLOYEE');
        ```
      This `data.sql` will be executed by Spring Boot on startup if `ddl-auto` is set to `create`, `create-drop`, or `update` (for H2). For MySQL, you might need to run these inserts manually after the tables are created.

---

## 📄 API Endpoints & Documentation (Swagger UI)

All API endpoints are documented using Swagger/OpenAPI. Once the application is running, you can access the interactive documentation at:

* **Swagger UI**: `http://localhost:8080/swagger-ui.html`
* **OpenAPI Docs (JSON)**: `http://localhost:8080/v3/api-docs`

Use the "Authorize" button in Swagger UI to enter your JWT token (prefixed with `Bearer `) to test protected endpoints.

**Base API URL**: `http://localhost:8080/api`

**Key Endpoint Categories**:

* `/api/auth`: User registration and login.
* `/api/employees`: CRUD operations for employee personal details.
* `/api/employments`: CRUD operations for employee employment records.
* `/api/deductions`: CRUD operations for payroll deduction configurations.
* `/api/payslips`: Generate, approve, and retrieve payslips.
* `/api/messages`: Retrieve system-generated messages (e.g., payroll notifications).

---

## 🧪 Testing with Postman

You can use Postman (or any API client) to test the endpoints. Sample JSON request bodies for various operations (registration, login, creating employees, employments, deductions, generating/approving payslips, etc.) have been provided in previous communications.

**Remember the flow:**
1.  Register an `ADMIN` user.
2.  Login with the `ADMIN` user to get a JWT token.
3.  Use this JWT token in the `Authorization: Bearer <token>` header for all subsequent protected API calls.

---

## 💡 Future Enhancements

* **Detailed Payslip Reports**: Generate PDF reports for payslips.
* **Email Notifications**: Integrate with an actual email service to send payslip notifications.
* **Audit Logging**: Implement comprehensive audit trails for sensitive operations.
* **Unit & Integration Tests**: Expand test coverage for services and controllers.
* **Pagination and Filtering**: Add advanced querying capabilities for large datasets.
* **Frontend Integration**: Develop a user-friendly frontend application (e.g., using React, Angular, or Vue.js).

---