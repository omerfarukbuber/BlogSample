package com.omerfbuber.services.users;

import com.omerfbuber.dtos.users.request.ChangePasswordRequest;
import com.omerfbuber.dtos.users.request.CreateUserRequest;
import com.omerfbuber.dtos.users.request.UpdateUserRequest;
import com.omerfbuber.dtos.users.response.UserResponse;
import com.omerfbuber.entities.Role;
import com.omerfbuber.entities.User;
import com.omerfbuber.repositories.users.UserRepository;
import com.omerfbuber.results.Error;
import com.omerfbuber.results.Result;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CacheManager cacheManager;

    public UserServiceImpl(UserRepository userRepository, CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.cacheManager = cacheManager;
    }

    @Cacheable(value = "users", key = "'all'")
    @Override
    public Result<List<UserResponse>> getAll() {
        var result = userRepository.findAll().stream()
                .map(user -> UserResponse.of(user)).toList();

        return Result.success(result);
    }

    @Cacheable(value = "users", key = "'full-names'")
    @Override
    public Result<List<String>> getAllFullNames() {
        var result = userRepository.getFullNameList().orElse(Collections.emptyList());
        return Result.success(result);
    }

    @Cacheable(value = "users", key = "#id")
    @Override
    public Result<UserResponse> getById(long id) {
        var result = userRepository.findById(id)
                .map(user -> UserResponse.of(user)).orElse(null);

        if (result == null)
            return Result.failure(Error.notFound("User.NotFound", "User with id " + id + " not found"));

        return Result.success(result);
    }

    @Override
    public Result<UserResponse> getByEmail(String email) {
        var result = userRepository.findByEmail(email)
                .map(user -> UserResponse.of(user)).orElse(null);
        if (result == null)
            return Result.failure(Error.notFound("User.NotFound", "User with email " + email + " not found"));

        return Result.success(result);
    }

    @Override
    public Result<UserResponse> save(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email()))
            return Result.failure(Error.conflict("User.AlreadyExists", "User with email " + request.email() + " already exists"));

        if (!request.password().equals(request.confirmPassword()))
            return Result.failure(Error.problem("User.PasswordMismatch", "Passwords do not match"));

        User user = request.toUser();
        user.setRole(Role.getUserRole());

        var entity = userRepository.save(user);
        var result = Result.created(UserResponse.of(entity), "api/users/" + entity.getId());

        cacheManager.getCache("users").put(entity.getId(), result);
        cacheManager.getCache("users").evict("all");
        return result;
    }

    @CacheEvict(value = "users", key = "#request.id()")
    @Override
    public Result<Void> update(UpdateUserRequest request) {
        var entity = userRepository.findById(request.id()).orElse(null);
        if (entity == null)
            return Result.failure(Error.notFound("User.NotFound", "User with id " + request.id() + " not found"));

        entity.setFirstName(request.firstName());
        entity.setLastName(request.lastName());
        entity.setBirthDate(request.birthDate());

        userRepository.save(entity);
        cacheManager.getCache("users").evict("all");
        return Result.success();
    }

    @Override
    public Result<Void> changePassword(ChangePasswordRequest request) {
        var entity = userRepository.findByEmail(request.email()).orElse(null);
        if (entity == null)
            return Result.failure(Error.notFound("User.NotFound", "User with email " + request.email() + " not found"));

        if (!entity.getPassword().equals(request.password()))
            return Result.failure(Error.problem("User.WrongPassword", "Password is incorrect"));

        if (!request.confirmPassword().equals(request.newPassword()))
            return Result.failure(Error.problem("PasswordMismatch", "Passwords do not match"));

        entity.setPassword(request.newPassword());

        var result = userRepository.updateUserPassword(request.email(), request.newPassword());
        return result > 0
                ? Result.success()
                : Result.failure(Error.failure("Server.UpdateError", "An error occurred while updating the password"));
    }

    @CacheEvict(value = "users", key = "#id")
    @Override
    public Result<Void> delete(long id) {
        if (!userRepository.existsById(id))
            return Result.failure(Error.notFound("User.NotFound", "User with id " + id + " not found"));

        userRepository.deleteById(id);
        cacheManager.getCache("users").evict("all");
        return Result.success();
    }
}
