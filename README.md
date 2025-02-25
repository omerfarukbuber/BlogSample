# BasicRestApi

BasicRestApi is a REST API built with Java Spring Boot, using PostgreSQL as the database. The project follows a layered architecture with Repository and Service layers. It utilizes the Result pattern, ProblemDetail for error handling, and global exception handling. Validation is implemented using Spring's validation framework, with localized validation messages stored in `messages.properties` and `messages_en.properties`. The project also includes both unit and integration tests.

## Features

- Java Spring Boot REST API
- PostgreSQL database
- Repository and Service layers
- Result pattern implementation
- ProblemDetail-based global exception handling
- Validation using Spring Validation
- Localized validation messages
- Unit and integration tests
- OpenAPI documentation

## Technologies Used

- **Spring Boot** - Framework for building the REST API
- **Spring Data JPA** - Database interaction
- **PostgreSQL** - Relational database
- **Spring Validation** - Request validation
- **ProblemDetail** - Error handling
- **JUnit, Mockito** - Unit and integration testing
- **OpenAPI** - API documentation

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
   ```

3. Build and run the application:
   ```sh
   mvn clean install
   mvn spring-boot:run
   ```

## API Endpoints

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

## License

This project is open-source and available under the [MIT License](LICENSE).

