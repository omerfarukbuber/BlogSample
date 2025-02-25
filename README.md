# BasicRestApi

## Overview
BasicRestApi is a RESTful API built using **Java Spring Boot**. It utilizes **PostgreSQL** as the database and follows a structured architecture with **Repository** and **Service** layers. The project implements **Result Pattern** and **ProblemDetails** for handling responses and errors effectively. **ValidationExceptionHandling** is used for request validation, leveraging Spring's built-in validation framework. Validation messages are stored in `messages.properties` and `messages_en.properties`.

## Features
- **Spring Boot** framework
- **PostgreSQL** database integration
- **Repository-Service Pattern**
- **Result Pattern** for API responses
- **ProblemDetails** for structured error handling
- **Spring Validation Framework** for request validation
- **Multilingual support for validation messages**
- **RESTful API design** following OpenAPI 3.1.0 specification

## API Endpoints
### `Users API`
| Method | Endpoint | Description |
|--------|---------|-------------|
| `GET`  | `/api/users` | Retrieve all users |
| `POST` | `/api/users` | Create a new user |
| `PUT`  | `/api/users` | Update user details |
| `GET`  | `/api/users/{id}` | Get a user by ID |
| `DELETE` | `/api/users/{id}` | Delete a user by ID |
| `PUT` | `/api/users/change_password` | Change user password |
| `GET`  | `/api/users/names` | Get a list of user names |

## Error Handling
The project uses **ProblemDetails** for error responses. Example error response:
```json
{
  "type": "https://example.com/validation-error",
  "title": "Validation Error",
  "status": 400,
  "errors": [
    { "code": "email", "description": "Email is required" }
  ]
}
```

## Installation & Setup
### Prerequisites
- Java 17+
- PostgreSQL
- Maven

### Steps
1. Clone the repository:
   ```sh
   git clone https://github.com/omerfarukbuber/BasicRestApi.git
   ```
2. Configure **PostgreSQL** connection in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/your_database
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```
3. Build and run the project:
   ```sh
   mvn spring-boot:run
   ```

## License
This project is licensed under the MIT License.

