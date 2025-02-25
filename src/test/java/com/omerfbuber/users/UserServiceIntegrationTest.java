package com.omerfbuber.users;

import com.omerfbuber.basicrestapi.BasicRestApiApplication;
import com.omerfbuber.dtos.users.request.ChangePasswordRequest;
import com.omerfbuber.dtos.users.request.CreateUserRequest;
import com.omerfbuber.dtos.users.request.UpdateUserRequest;
import com.omerfbuber.dtos.users.response.UserResponse;
import com.omerfbuber.repositories.users.UserRepository;
import com.omerfbuber.results.Result;
import com.omerfbuber.services.users.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {BasicRestApiApplication.class})
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    private CreateUserRequest createUserRequest;

    @BeforeEach
    void setUp() {
        createUserRequest = new CreateUserRequest(
                "John", "Doe", "john.doe@example.com",
                "password123", "password123", new Date()
        );
    }

    @Test
    void getAll_ShouldReturnListOfUsers_WhenUsersExist() {
        // Arrange
        CreateUserRequest request2 = new CreateUserRequest(
                "Jane", "Doe", "jane.doe@example.com",
                "password123", "password123", new Date());
        userService.save(createUserRequest);
        userService.save(request2);

        // Act
        Result<List<UserResponse>> result = userService.getAll();

        // Assert
        assertTrue(result.isSuccess());
        assertNotNull(result.getValue());
        assertTrue(result.getValue().size() >= 2);
    }

    @Test
    void getAllFullNames_ShouldReturnListOfFullNames_WhenUsersExist() {
        // Arrange
        CreateUserRequest request2 = new CreateUserRequest(
                "Jane", "Doe", "jane.doe@example.com",
                "password123", "password123", new Date());
        userService.save(createUserRequest);
        userService.save(request2);

        // Act
        Result<List<String>> result = userService.getAllFullNames();

        // Assert
        assertTrue(result.isSuccess());
        assertNotNull(result.getValue());
        assertTrue(result.getValue().size() >= 2);
        assertTrue(result.getValue().contains("John Doe"));
        assertTrue(result.getValue().contains("Jane Doe"));
    }

    @Test
    void getById_ShouldReturnUser_WhenUserExists() {
        var savedUser = userService.save(createUserRequest).getValue();
        Result<UserResponse> result = userService.getById(savedUser.id());
        assertTrue(result.isSuccess());
        assertEquals(savedUser.email(), result.getValue().email());
    }

    @Test
    void getById_ShouldFail_WhenUserDoesNotExist() {
        Result<UserResponse> result = userService.getById(999L);
        assertFalse(result.isSuccess());
        assertEquals("User.NotFound", result.getError().code());
    }

    @Test
    void getByEmail_ShouldReturnUser_WhenUserExists() {
        // Arrange
        var savedUser = userService.save(createUserRequest).getValue();

        // Act
        Result<UserResponse> result = userService.getByEmail(savedUser.email());

        // Assert
        assertTrue(result.isSuccess());
        assertNotNull(result.getValue());
        assertEquals(savedUser.email(), result.getValue().email());
        assertEquals(savedUser.firstName(), result.getValue().firstName());
        assertEquals(savedUser.lastName(), result.getValue().lastName());
    }

    @Test
    void save_ShouldPersistUser_WhenValidRequest() {
        Result<UserResponse> result = userService.save(createUserRequest);
        assertTrue(result.isSuccess());
        assertNotNull(result.getValue());
        assertTrue(userRepository.existsByEmail("john.doe@example.com"));
    }

    @Test
    void save_ShouldFail_WhenEmailAlreadyExists() {
        userService.save(createUserRequest);
        Result<UserResponse> result = userService.save(createUserRequest);
        assertFalse(result.isSuccess());
        assertEquals("User.AlreadyExists", result.getError().code());
    }

    @Test
    void save_ShouldFail_WhenPasswordsDoNotMatch() {
        CreateUserRequest invalidRequest = new CreateUserRequest(
                "Jane", "Doe", "jane.doe@example.com",
                "password123", "differentPassword", new Date()
        );
        Result<UserResponse> result = userService.save(invalidRequest);
        assertFalse(result.isSuccess());
        assertEquals("User.PasswordMismatch", result.getError().code());
    }

    @Test
    void update_ShouldModifyUser_WhenValidRequest() {
        // Arrange
        Result<UserResponse> saveResult = userService.save(createUserRequest);
        assertTrue(saveResult.isSuccess());

        UpdateUserRequest updateRequest = new UpdateUserRequest(saveResult.getValue().id(),
                "John", "Smith", new Date());

        // Act
        Result<Void> result = userService.update(updateRequest);

        // Assert
        assertTrue(result.isSuccess());
        Result<UserResponse> updatedUserResult = userService.getById(saveResult.getValue().id());
        assertTrue(updatedUserResult.isSuccess());
        assertEquals("Smith", updatedUserResult.getValue().lastName());
    }

    @Test
    void changePassword_ShouldUpdatePassword_WhenValidRequest() {
        userService.save(createUserRequest);
        ChangePasswordRequest request = new ChangePasswordRequest(
                "john.doe@example.com", "password123", "newPassword123", "newPassword123"
        );
        Result<Void> result = userService.changePassword(request);
        assertTrue(result.isSuccess());
    }

    @Test
    void changePassword_ShouldFail_WhenCurrentPasswordIsIncorrect() {
        userService.save(createUserRequest);
        ChangePasswordRequest request = new ChangePasswordRequest(
                "john.doe@example.com", "wrongPassword", "newPassword123", "newPassword123"
        );
        Result<Void> result = userService.changePassword(request);
        assertFalse(result.isSuccess());
        assertEquals("User.WrongPassword", result.getError().code());
    }

    @Test
    void delete_ShouldRemoveUser_WhenUserExists() {
        var savedUser = userService.save(createUserRequest).getValue();
        Result<Void> result = userService.delete(savedUser.id());
        assertTrue(result.isSuccess());
        assertFalse(userRepository.existsById(savedUser.id()));
    }

    @Test
    void delete_ShouldFail_WhenUserDoesNotExist() {
        Result<Void> result = userService.delete(999L);
        assertFalse(result.isSuccess());
        assertEquals("User.NotFound", result.getError().code());
    }
}
