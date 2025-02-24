package com.omerfbuber.services.users;

import com.omerfbuber.dtos.users.request.ChangePasswordRequest;
import com.omerfbuber.dtos.users.request.CreateUserRequest;
import com.omerfbuber.dtos.users.request.UpdateUserRequest;
import com.omerfbuber.dtos.users.response.UserResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    ResponseEntity<List<UserResponse>> getAll();
    ResponseEntity<UserResponse> getById(long id);
    ResponseEntity<UserResponse> getByEmail(String email);
    ResponseEntity<UserResponse> save(CreateUserRequest request);
    ResponseEntity<Void> update(UpdateUserRequest request);
    ResponseEntity<Void> changePassword(ChangePasswordRequest request);
    ResponseEntity<Void> delete(long id);
}
