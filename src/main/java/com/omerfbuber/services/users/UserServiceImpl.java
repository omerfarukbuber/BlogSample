package com.omerfbuber.services.users;

import com.omerfbuber.dtos.users.request.ChangePasswordRequest;
import com.omerfbuber.dtos.users.request.CreateUserRequest;
import com.omerfbuber.dtos.users.request.UpdateUserRequest;
import com.omerfbuber.dtos.users.response.UserResponse;
import com.omerfbuber.entities.Permission;
import com.omerfbuber.entities.Role;
import com.omerfbuber.entities.User;
import com.omerfbuber.repositories.users.UserRepository;
import com.omerfbuber.results.Error;
import com.omerfbuber.results.Result;
import com.omerfbuber.services.shared.CustomUserDetails;
import com.omerfbuber.services.shared.CustomUserDetailsService;
import com.omerfbuber.services.shared.PasswordHasher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CacheManager cacheManager;
    private final PasswordHasher passwordHasher;
    private final CustomUserDetailsService customUserDetailsService;

    public UserServiceImpl(UserRepository userRepository, CacheManager cacheManager, PasswordHasher passwordHasher, CustomUserDetailsService customUserDetailsService) {
        this.userRepository = userRepository;
        this.cacheManager = cacheManager;
        this.passwordHasher = passwordHasher;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Cacheable(value = "users", key = "'all'")
    @Override
    public Result<List<UserResponse>> getAll() {
        log.info("Fetching all users from database");
        var result = userRepository.findAll().stream()
                .map(UserResponse::of).toList();
        log.info("Fetched {} users", result.size());
        return Result.success(result);
    }

    @Cacheable(value = "users", key = "'full-names'")
    @Override
    public Result<List<String>> getAllFullNames() {
        log.info("Fetching all user full names");
        var result = userRepository.getFullNameList().orElse(Collections.emptyList());
        log.info("Fetched {} full names", result.size());
        return Result.success(result);
    }

    @Cacheable(value = "users", key = "#id")
    @Override
    public Result<UserResponse> getById(long id) {
        log.info("Fetching user with ID: {}", id);
        var result = userRepository.findById(id)
                .map(UserResponse::of).orElse(null);

        if (result == null) {
            log.warn("User with ID: {} not found", id);
            return Result.failure(Error.notFound("User.NotFound", "User with id " + id + " not found"));
        }

        log.info("User with ID: {} found", id);
        return Result.success(result);
    }

    @Override
    public Result<UserResponse> getByEmail(String email) {
        log.info("Fetching user with email: {}", email);
        var result = userRepository.findByEmail(email)
                .map(UserResponse::of).orElse(null);

        if (result == null) {
            log.warn("User with email: {} not found", email);
            return Result.failure(Error.notFound("User.NotFound", "User with email " + email + " not found"));
        }

        log.info("User with email: {} found", email);
        return Result.success(result);
    }

    @Override
    public Result<UserResponse> save(CreateUserRequest request) {
        log.info("Attempting to create user with email: {}", request.email());
        if (userRepository.existsByEmail(request.email())) {
            log.warn("User with email: {} already exists", request.email());
            return Result.failure(Error.conflict("User.AlreadyExists", "User with email " + request.email() + " already exists"));
        }

        if (!request.password().equals(request.confirmPassword())) {
            log.warn("Password mismatch for email: {}", request.email());
            return Result.failure(Error.problem("User.PasswordMismatch", "Passwords do not match"));
        }

        User user = request.toUser();
        user.setPassword(passwordHasher.hash(request.password()));
        user.setRole(Role.getUserRole());
        var entity = userRepository.save(user);
        log.info("User with email: {} created successfully", request.email());

        var result = Result.created(UserResponse.of(entity), "api/users/" + entity.getId());
        Objects.requireNonNull(cacheManager.getCache("users")).put(entity.getId(), result);
        Objects.requireNonNull(cacheManager.getCache("users")).evict("all");
        return result;
    }

    @CacheEvict(value = "users", key = "#request.id()")
    @Override
    public Result<Void> update(UpdateUserRequest request) {
        log.info("Updating user with ID: {}", request.id());
        var entity = userRepository.findById(request.id()).orElse(null);
        if (entity == null) {
            log.warn("User with ID: {} not found", request.id());
            return Result.failure(Error.notFound("User.NotFound", "User with id " + request.id() + " not found"));
        }

        entity.setFirstName(request.firstName());
        entity.setLastName(request.lastName());
        entity.setBirthDate(request.birthDate());
        userRepository.save(entity);
        log.info("User with ID: {} updated successfully", request.id());

        Objects.requireNonNull(cacheManager.getCache("users")).evict("all");
        return Result.success();
    }

    @Override
    public Result<Void> changePassword(ChangePasswordRequest request) {
        log.info("Changing password for email: {}", request.email());
        var entity = userRepository.findByEmail(request.email()).orElse(null);
        if (entity == null) {
            log.warn("User with email: {} not found", request.email());
            return Result.failure(Error.notFound("User.NotFound", "User with email " + request.email() + " not found"));
        }

        if (!passwordHasher.verify(request.password(), entity.getPassword())) {
            log.warn("Incorrect password attempt for email: {}", request.email());
            return Result.failure(Error.problem("User.WrongPassword", "Password is incorrect"));
        }

        if (!request.confirmPassword().equals(request.newPassword())) {
            log.warn("New Passwords mismatched" );
            return Result.failure(Error.problem("PasswordMismatch", "Passwords do not match"));
        }

        entity.setPassword(passwordHasher.hash(request.newPassword()));
        var result = userRepository.updateUserPassword(request.email(), request.newPassword());

        if (result > 0) {
            log.info("Password changed successfully for email: {}", request.email());
            return Result.success();
        } else {
            log.error("Error updating password for email: {}", request.email());
            return Result.failure(Error.failure("Server.UpdateError", "An error occurred while updating the password"));
        }
    }

    @CacheEvict(value = "users", key = "#id")
    @Override
    public Result<Void> delete(long id, CustomUserDetails customUserDetails) {
        final String UserDeleteSelf = "User.Delete.Self";
        final String UserDeleteAny = "User.Delete.Any";

        log.info("Deleting user with ID: {}", id);

        if (!userRepository.existsById(id)) {
            log.warn("User with ID: {} not found", id);
            return Result.failure(Error.notFound("User.NotFound", "User with id " + id + " not found"));
        }

        if (customUserDetails.getUser().getId() == id
                && !customUserDetailsService.containsPermission(customUserDetails, UserDeleteSelf)) {
            log.warn("User with ID: {} not authorized for delete self", id);
            return Result.failure(Error.forbidden("User.NotAuthorized", "User not authorized for delete self."));
        }

        if (customUserDetails.getUser().getId() != id
                && !customUserDetailsService.containsPermission(customUserDetails, UserDeleteAny)) {
            log.warn("User with ID: {} not authorized for delete any", id);
            return Result.failure(Error.forbidden("User.NotAuthorized", "User not authorized for delete any."));
        }

        userRepository.deleteById(id);
        log.info("User with ID: {} deleted successfully", id);
        Objects.requireNonNull(cacheManager.getCache("users")).evict("all");
        return Result.success();
    }
}
