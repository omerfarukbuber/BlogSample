package com.omerfbuber.user;

import com.omerfbuber.BlogSample;
import com.omerfbuber.dto.user.ChangePasswordRequest;
import com.omerfbuber.dto.user.CreateUserRequest;
import com.omerfbuber.dto.user.UpdateUserRequest;
import com.omerfbuber.dto.user.UserResponse;
import com.omerfbuber.entity.Permission;
import com.omerfbuber.entity.Role;
import com.omerfbuber.repository.UserRepository;
import com.omerfbuber.result.Result;
import com.omerfbuber.service.shared.CustomUserDetails;
import com.omerfbuber.service.shared.CustomUserDetailsService;
import com.omerfbuber.service.user.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {BlogSample.class})
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private CreateUserRequest createUserRequest;

    @BeforeEach
    void setUp() {
        createUserRequest = new CreateUserRequest(
                "John", "Doe", "john.doe@example.com",
                "password123", "password123", LocalDateTime.now()
        );
    }

    @Test
    void getAll_ShouldReturnListOfUsers_WhenUsersExist() {
        // Arrange
        CreateUserRequest request2 = new CreateUserRequest(
                "Jane", "Doe", "jane.doe@example.com",
                "password123", "password123", LocalDateTime.now());
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
                "password123", "password123", LocalDateTime.now());
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
                "password123", "differentPassword", LocalDateTime.now()
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
                "John", "Smith", LocalDateTime.now());

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
    void delete_ShouldRemoveUser_WhenUserExistsAndHasPermission() {
        CreateUserRequest request2 = new CreateUserRequest(
                "Jane", "Doe", "jane.doe@example.com",
                "password123", "password123", LocalDateTime.now());
        var savedUser = userService.save(createUserRequest).getValue();
        var user = userRepository.findById(savedUser.id()).get();
        user.getRole().setPermissions(Set.of(new Permission("User.Delete.Any")));
        userRepository.save(user);
        var deleteUser = userService.save(request2).getValue();

        var customUserDetail = new CustomUserDetails(user);
        Result<Void> result = userService.delete(deleteUser.id(), customUserDetail);


        assertTrue(result.isSuccess());
        assertFalse(userRepository.existsById(deleteUser.id()));
    }

    @Test
    void delete_ShouldSucceed_WhenUserDeletesSelfWithPermission() {
        var savedUser = userService.save(createUserRequest).getValue();

        var customUserDetail = (CustomUserDetails) customUserDetailsService.loadUserByUsername(savedUser.email());
        Result<Void> result = userService.delete(savedUser.id(), customUserDetail);

        assertTrue(result.isSuccess());
        assertFalse(userRepository.existsById(savedUser.id()));
    }

    @Test
    void delete_ShouldFail_WhenUserTriesToDeleteSelfWithoutPermission() {
        var savedUser = userService.save(createUserRequest).getValue();
        var user = userRepository.findById(savedUser.id()).get();
        user.setRole(Role.getEmptyRole());
        userRepository.save(user);

        var customUserDetail = new CustomUserDetails(user);
        Result<Void> result = userService.delete(savedUser.id(), customUserDetail);

        assertFalse(result.isSuccess());
        assertEquals("User.NotAuthorized", result.getError().code());
        assertEquals("User not authorized for delete self.", result.getError().description());
    }

    @Test
    void delete_ShouldFail_WhenUserTriesToDeleteAnotherUserWithoutPermission() {
        CreateUserRequest request2 = new CreateUserRequest(
                "Jane", "Doe", "jane.doe@example.com",
                "password123", "password123", LocalDateTime.now());
        var savedUser = userService.save(createUserRequest).getValue();
        var deleteUser = userService.save(request2).getValue();

        var customUserDetail = (CustomUserDetails) customUserDetailsService.loadUserByUsername(savedUser.email());
        Result<Void> result = userService.delete(deleteUser.id(), customUserDetail);

        assertFalse(result.isSuccess());
        assertEquals("User.NotAuthorized", result.getError().code());
        assertEquals("User not authorized for delete any.", result.getError().description());
    }

    @Test
    void delete_ShouldFail_WhenUserDoesNotExist() {
        var savedUser = userService.save(createUserRequest).getValue();
        var customUserDetail = (CustomUserDetails) customUserDetailsService.loadUserByUsername(savedUser.email());
        Result<Void> result = userService.delete(999L, customUserDetail);
        assertFalse(result.isSuccess());
        assertEquals("User.NotFound", result.getError().code());
    }
}
