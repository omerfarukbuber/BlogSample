package com.omerfbuber.user;

import com.omerfbuber.dto.user.ChangePasswordRequest;
import com.omerfbuber.dto.user.CreateUserRequest;
import com.omerfbuber.dto.user.UpdateUserRequest;
import com.omerfbuber.dto.user.UserResponse;
import com.omerfbuber.entity.User;
import com.omerfbuber.repository.UserRepository;
import com.omerfbuber.result.Result;
import com.omerfbuber.service.shared.CustomUserDetails;
import com.omerfbuber.service.shared.CustomUserDetailsService;
import com.omerfbuber.util.PasswordHasher;
import com.omerfbuber.service.user.UserService;
import com.omerfbuber.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private PasswordHasher passwordHasher;
    @Mock
    private CustomUserDetailsService customUserDetailsService;

    private UserService userService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, cacheManager, passwordHasher, customUserDetailsService);
    }

    @Test
    void getAll_ShouldReturnListOfUsers_WhenUsersExist() {
        //Arrange
        List<User> users = List.of(
                new User(1L, "john.doe@example.com", "hashedpassword123", "John", "Doe", LocalDateTime.now()),
                new User(2L, "jane.doe@example.com", "securepassword456", "Jane", "Doe", LocalDateTime.now()),
                new User(3L, "alice.smith@example.com", "mypassword789", "Alice", "Smith", LocalDateTime.now())
        );

        when(userRepository.findAll()).thenReturn(users);

        //Act
        Result<List<UserResponse>> result = userService.getAll();

        //Assert
        assertTrue(result.isSuccess());
        assertEquals(3, result.getValue().size());
        assertEquals("John", result.getValue().get(0).firstName());
        assertEquals("Jane", result.getValue().get(1).firstName());
        assertEquals("Alice", result.getValue().get(2).firstName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAll_ShouldReturnListOfUsers_WhenUsersNotExist() {
        //Arrange
        List<User> users = List.of();
        when(userRepository.findAll()).thenReturn(users);

        //Act
        Result<List<UserResponse>> result = userService.getAll();

        //Assert
        assertTrue(result.isSuccess());
        assertTrue(result.getValue().isEmpty());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllFullNames_ShouldReturnListOfUsers_WhenUsersExist() {
        List<String> userNames = List.of(
                "John Doe", "Jane Doe", "Alice Smith"
        );
        when(userRepository.getFullNameList()).thenReturn(Optional.of(userNames));

        Result<List<String>> result = userService.getAllFullNames();

        assertTrue(result.isSuccess());
        assertEquals(3, result.getValue().size());
        assertEquals("John Doe", result.getValue().get(0));
        assertEquals("Jane Doe", result.getValue().get(1));
        assertEquals("Alice Smith", result.getValue().get(2));
        verify(userRepository, times(1)).getFullNameList();
    }

    @Test
    void getAllFullNames_ShouldReturnListOfUsers_WhenUsersNotExist() {
        when(userRepository.getFullNameList()).thenReturn(Optional.empty());

        Result<List<String>> result = userService.getAllFullNames();

        assertTrue(result.isSuccess());
        assertTrue(result.getValue().isEmpty());

        verify(userRepository, times(1)).getFullNameList();
    }

    @Test
    void getById_ShouldReturnUser_WhenUserExists() {
        long userId = 1L;
        var user = new User(userId, "john.doe@example.com",
                "hashedpassword123", "John", "Doe", LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Result<UserResponse> result = userService.getById(userId);

        assertTrue(result.isSuccess());
        assertEquals("John", result.getValue().firstName());
        assertEquals("Doe", result.getValue().lastName());
        verify(userRepository, times(1)).findById(userId);

    }

    @Test
    void getById_ShouldReturnFailure_WhenUserNotExist() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Result<UserResponse> result = userService.getById(userId);

        assertTrue(result.isFailure());
        assertEquals("User.NotFound", result.getError().code());
        assertEquals("User with id " + userId + " not found", result.getError().description());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void save_ShouldReturnConflict_WhenUserEmailAlreadyExists() {
        // Arrange
        String email = "test@example.com";
        CreateUserRequest request = new CreateUserRequest("John", "Doe", email, "password123", "password123", LocalDateTime.now());
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act
        Result<UserResponse> result = userService.save(request);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("User.AlreadyExists", result.getError().code());
        assertEquals("User with email " + email + " already exists", result.getError().description());

        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    public void save_ShouldReturnPasswordMismatch_WhenPasswordsDoNotMatch() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest("John", "Doe", "test@example.com", "password123", "password456", LocalDateTime.now());
        when(userRepository.existsByEmail(request.email())).thenReturn(false);

        // Act
        Result<UserResponse> result = userService.save(request);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("User.PasswordMismatch", result.getError().code());
        assertEquals("Passwords do not match", result.getError().description());

        verify(userRepository, times(1)).existsByEmail(request.email());
    }

    @Test
    public void save_ShouldReturnCreated_WhenUserIsSavedSuccessfully() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest("John", "Doe", "test@example.com", "password123", "password123", LocalDateTime.now());
        User savedUser = new User(1L, "test@example.com", "password123", "John", "Doe", LocalDateTime.now());
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(cacheManager.getCache("users")).thenReturn(mock(Cache.class));

        // Act
        Result<UserResponse> result = userService.save(request);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("api/users/1", result.getCreatedUri());
        assertEquals(savedUser.getEmail(), result.getValue().email());

        verify(userRepository, times(1)).existsByEmail(request.email());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void update_ShouldReturnNotFound_WhenUserDoesNotExist() {
        // Arrange
        long userId = 1L;
        UpdateUserRequest request = new UpdateUserRequest(userId, "John", "Doe", LocalDateTime.now());
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Result<Void> result = userService.update(request);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("User.NotFound", result.getError().code());
        assertEquals("User with id " + userId + " not found", result.getError().description());
    }

    @Test
    public void update_ShouldReturnSuccess_WhenUserIsFoundAndUpdated() {
        // Arrange
        long userId = 1L;
        UpdateUserRequest request = new UpdateUserRequest(userId, "John", "Doe", LocalDateTime.now());
        User existingUser = new User(userId, "oldEmail@example.com", "oldPassword", "Old", "Name", LocalDateTime.now());
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        when(cacheManager.getCache("users")).thenReturn(mock(Cache.class));

        // Act
        Result<Void> result = userService.update(request);

        // Assert
        assertTrue(result.isSuccess());

        assertEquals("John", existingUser.getFirstName());
        assertEquals("Doe", existingUser.getLastName());
        assertEquals(request.birthDate(), existingUser.getBirthDate());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));

    }

    @Test
    public void changePassword_ShouldReturnNotFound_WhenUserDoesNotExist() {
        // Arrange
        String email = "test@example.com";
        ChangePasswordRequest request = new ChangePasswordRequest(email, "oldPassword", "newPassword", "newPassword");
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        Result<Void> result = userService.changePassword(request);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("User.NotFound", result.getError().code());
        assertEquals("User with email " + email + " not found", result.getError().description());

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void changePassword_ShouldReturnWrongPassword_WhenPasswordIsIncorrect() {
        // Arrange
        String email = "test@example.com";
        ChangePasswordRequest request = new ChangePasswordRequest(email, "wrongPassword", "newPassword", "newPassword");
        User existingUser = new User(1L, email, "correctPassword", "John", "Doe", LocalDateTime.now());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(passwordHasher.verify(request.password(), existingUser.getPassword())).thenReturn(false);

        // Act
        Result<Void> result = userService.changePassword(request);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("User.WrongPassword", result.getError().code());
        assertEquals("Password is incorrect", result.getError().description());

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void changePassword_ShouldReturnPasswordMismatch_WhenPasswordsDoNotMatch() {
        // Arrange
        String email = "test@example.com";
        ChangePasswordRequest request = new ChangePasswordRequest(email, "correctPassword", "newPassword", "differentPassword");
        User existingUser = new User(1L, email, "correctPassword", "John", "Doe", LocalDateTime.now());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(passwordHasher.verify(request.password(), existingUser.getPassword())).thenReturn(true);

        // Act
        Result<Void> result = userService.changePassword(request);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("PasswordMismatch", result.getError().code());
        assertEquals("Passwords do not match", result.getError().description());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void changePassword_ShouldReturnSuccess_WhenPasswordIsUpdated() {
        // Arrange
        String email = "test@example.com";
        String newPassword = "newPassword";
        String hashedPassword = "hashedNewPassword";
        ChangePasswordRequest request = new ChangePasswordRequest(email, "correctPassword", newPassword, newPassword);
        User existingUser = new User(1L, email, "correctPassword", "John", "Doe", LocalDateTime.now());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(userRepository.updateUserPassword(email, hashedPassword)).thenReturn(1);
        when(passwordHasher.verify(request.password(), existingUser.getPassword())).thenReturn(true);
        when(passwordHasher.hash(request.newPassword())).thenReturn(hashedPassword);

        // Act
        Result<Void> result = userService.changePassword(request);

        // Assert
        assertTrue(result.isSuccess());
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).updateUserPassword(email, hashedPassword);
    }

    @Test
    public void changePassword_ShouldReturnFailure_WhenPasswordUpdateFails() {
        // Arrange
        String email = "test@example.com";
        String newPassword = "newPassword";
        String hashedPassword = "hashedNewPassword";
        ChangePasswordRequest request = new ChangePasswordRequest(email, "correctPassword", newPassword, newPassword);
        User existingUser = new User(1L, email, "correctPassword", "John", "Doe", LocalDateTime.now());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(userRepository.updateUserPassword(email, hashedPassword)).thenReturn(0);
        when(passwordHasher.verify(request.password(), existingUser.getPassword())).thenReturn(true);
        when(passwordHasher.hash(request.newPassword())).thenReturn(hashedPassword);

        // Act
        Result<Void> result = userService.changePassword(request);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Server.UpdateError", result.getError().code());
        assertEquals("An error occurred while updating the password", result.getError().description());
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).updateUserPassword(email, passwordHasher.hash(request.newPassword()));
    }

    @Test
    void delete_ShouldReturnFailure_WhenUserNotFound() {
        // Arrange
        long id = 1L;
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userRepository.existsById(id)).thenReturn(false);

        // Act
        Result<Void> result = userService.delete(id, userDetails);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("User.NotFound", result.getError().code());
        assertEquals("User with id " + id + " not found", result.getError().description());
        verify(userRepository, times(1)).existsById(id);
        verify(userRepository, never()).deleteById(id);
    }

    @Test
    void delete_ShouldReturnFailure_WhenUserTriesToDeleteSelfWithoutPermission() {
        // Arrange
        long id = 1L;
        User user = new User();
        user.setId(id);
        CustomUserDetails userDetails = new CustomUserDetails(user);

        when(userRepository.existsById(id)).thenReturn(true);
        when(customUserDetailsService.containsPermission(userDetails, "User.Delete.Self")).thenReturn(false);

        // Act
        Result<Void> result = userService.delete(id, userDetails);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("User.NotAuthorized", result.getError().code());
        assertEquals("User not authorized for delete self.", result.getError().description());
        verify(userRepository, times(1)).existsById(id);
        verify(userRepository, never()).deleteById(id);
    }

    @Test
    void delete_ShouldReturnFailure_WhenUserTriesToDeleteAnotherUserWithoutPermission() {
        // Arrange
        long id = 1L;
        User user = new User();
        user.setId(2L);
        CustomUserDetails userDetails = new CustomUserDetails(user);

        when(userRepository.existsById(id)).thenReturn(true);
        when(customUserDetailsService.containsPermission(userDetails, "User.Delete.Any")).thenReturn(false);

        // Act
        Result<Void> result = userService.delete(id, userDetails);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("User.NotAuthorized", result.getError().code());
        assertEquals("User not authorized for delete any.", result.getError().description());
        verify(userRepository, times(1)).existsById(id);
        verify(userRepository, never()).deleteById(id);
    }

    @Test
    void delete_ShouldReturnSuccess_WhenUserDeletesSelfWithPermission() {
        // Arrange
        long id = 1L;
        User user = new User();
        user.setId(id);
        CustomUserDetails userDetails = new CustomUserDetails(user);

        when(userRepository.existsById(id)).thenReturn(true);
        when(customUserDetailsService.containsPermission(userDetails, "User.Delete.Self")).thenReturn(true);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(null);
        Cache cache = mock(Cache.class);
        when(cacheManager.getCache("users")).thenReturn(cache);

        // Act
        Result<Void> result = userService.delete(id, userDetails);

        // Assert
        assertTrue(result.isSuccess());
        verify(userRepository, times(1)).existsById(id);
        verify(userRepository, times(1)).save(any(User.class));
        verify(cache, times(1)).evict("all");
    }

    @Test
    void delete_ShouldReturnSuccess_WhenUserDeletesAnotherUserWithPermission() {
        // Arrange
        long id = 1L;
        User user = new User();
        user.setId(2L);
        CustomUserDetails userDetails = new CustomUserDetails(user);

        when(userRepository.existsById(id)).thenReturn(true);
        when(customUserDetailsService.containsPermission(userDetails, "User.Delete.Any")).thenReturn(true);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(null);
        Cache cache = mock(Cache.class);
        when(cacheManager.getCache("users")).thenReturn(cache);

        // Act
        Result<Void> result = userService.delete(id, userDetails);

        // Assert
        assertTrue(result.isSuccess());
        verify(userRepository, times(1)).existsById(id);
        verify(userRepository, times(1)).save(any(User.class));
        verify(cache, times(1)).evict("all");
    }
}
