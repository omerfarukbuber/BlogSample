package com.omerfbuber.services.users;

import com.omerfbuber.dtos.users.request.ChangePasswordRequest;
import com.omerfbuber.dtos.users.request.CreateUserRequest;
import com.omerfbuber.dtos.users.request.UpdateUserRequest;
import com.omerfbuber.dtos.users.response.UserResponse;
import com.omerfbuber.entities.User;
import com.omerfbuber.repositories.users.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<List<UserResponse>> getAll() {
        var result = userRepository.findAll().stream()
                .map(user -> UserResponse.of(user)).toList();

        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<List<String>> getAllFullNames() {
        var result = userRepository.getFullNameList().orElse(Collections.emptyList());
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<UserResponse> getById(long id) {
        var result = userRepository.findById(id)
                .map(user -> UserResponse.of(user)).orElse(null);

        if (result == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<UserResponse> getByEmail(String email) {
        var result = userRepository.findByEmail(email)
                .map(user -> UserResponse.of(user)).orElse(null);
        if (result == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<UserResponse> save(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email()))
            return ResponseEntity.badRequest().build();

        if (!request.password().equals(request.confirmPassword()))
            return ResponseEntity.badRequest().build();

        User user = request.toUser();

        var entity = userRepository.save(user);

        return ResponseEntity.ok(UserResponse.of(entity));
    }

    @Override
    public ResponseEntity<Void> update(UpdateUserRequest request) {
        var entity = userRepository.findById(request.id()).orElse(null);
        if (entity == null)
            return ResponseEntity.notFound().build();

        entity.setFirstName(request.firstName());
        entity.setLastName(request.lastName());
        entity.setBirthDate(request.birthDate());

        userRepository.save(entity);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> changePassword(ChangePasswordRequest request) {
        var entity = userRepository.findByEmail(request.email()).orElse(null);
        if (entity == null)
            return ResponseEntity.notFound().build();

        if (!entity.getPassword().equals(request.password()))
            return ResponseEntity.badRequest().build();

        if (!request.confirmPassword().equals(request.newPassword()))
            return ResponseEntity.badRequest().build();

        entity.setPassword(request.newPassword());

        var result = userRepository.updateUserPassword(request.email(), request.newPassword());
        return result > 0
                ? ResponseEntity.noContent().build()
                : ResponseEntity.internalServerError().build();
    }

    @Override
    public ResponseEntity<Void> delete(long id) {
        if (!userRepository.existsById(id))
            return ResponseEntity.notFound().build();

        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
