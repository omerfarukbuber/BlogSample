# BasicRestApi

BasicRestApi is a REST API built with Java Spring Boot, using PostgreSQL as the database. The project follows a layered architecture with Repository and Service layers. It provides user authentication, role-based access control, caching, validation, and logging. The API is documented with Swagger-UI and follows best practices such as using the Result pattern, ProblemDetails, and global exception handling. Validation is implemented using Spring's validation framework, with localized validation messages stored in `messages.properties` and `messages_en.properties`. The project also includes both unit and integration tests.

## Features

- Java Spring Boot REST API
- PostgreSQL database
- Repository and Service layers
- JWT Authentication & Authorization
- Refresh Token System
- Redis Caching
- Logging with SLF4J
- Result pattern implementation
- ProblemDetail-based global exception handling
- Validation using Spring Validation
- Localized validation messages
- Unit and integration tests
- Swagger-UI documentation

## Technologies Used

- **Spring Boot** - Framework for building the REST API
- **Spring Data JPA** - Database interaction
- **Spring Security** - JWT-based authentication
- **PostgreSQL** - Relational database
- **Redis** - Caching
- **SLF4J** - Logging
- **Spring Validation** - Request validation
- **ProblemDetail** - Error handling
- **JUnit, Mockito** - Unit and integration testing
- **Swagger-UI** - API documentation

## Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/omerfarukbuber/BasicRestApi.git
   cd BasicRestApi
   ```

2. Configure the database in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/basic_rest_api
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

   spring.cache.type=redis
   spring.data.redis.host=localhost
   spring.data.redis.port=6379

   logging.level.org.springframework=INFO
   logging.level.com.omerfbuber=DEBUG
   logging.file.name=logs/application.log
   logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} - [%level] %msg%n

   #expiration as minutes
   security.jwt.expiration=60
   security.jwt.secret=super-duper-secret-value-that-should-be-in-user-secrets
   #refresh token expiration as days
   security.jwt.refreshtoken.expiration=7
   ```

3. Build and run the application:
   ```sh
   mvn clean install
   mvn spring-boot:run
   ```

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|---------|-------------|
| `POST` | `/api/auth/login` | Login and get access token |
| `POST` | `/api/auth/refresh` | Refresh access token with refresh token |
| `POST` | `/api/auth/logout` | Logout and delete refresh token |

### Users
| Method | Endpoint | Description |
|--------|---------|-------------|
| `GET` | `/api/users` | Get all users |
| `GET` | `/api/users/{id}` | Get user by ID |
| `GET` | `/api/users/names` | Get full names of all users |
| `POST` | `/api/users` | Create a new user |
| `PUT` | `/api/users` | Update an existing user |
| `PUT` | `/api/users/change_password` | Change user password |
| `DELETE` | `/api/users/{id}` | Delete user by ID |

## Running Tests

To run unit and integration tests:
```sh
mvn test
```

## Documentation

Swagger-UI is available at:
```sh
http://localhost:8080/swagger-ui/index.html
```
http://localhost:8080/swagger-ui.html



## License

This project is open-source and available under the [MIT License](LICENSE).

