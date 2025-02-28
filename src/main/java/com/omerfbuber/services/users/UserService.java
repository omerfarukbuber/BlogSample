package com.omerfbuber.services.users;

import com.omerfbuber.dtos.users.request.ChangePasswordRequest;
import com.omerfbuber.dtos.users.request.CreateUserRequest;
import com.omerfbuber.dtos.users.request.UpdateUserRequest;
import com.omerfbuber.dtos.users.response.UserResponse;
import com.omerfbuber.results.Result;
import com.omerfbuber.services.shared.CustomUserDetails;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    Result<List<UserResponse>> getAll();
    Result<List<String>> getAllFullNames();
    Result<UserResponse> getById(long id);
    Result<UserResponse> getByEmail(String email);
    Result<UserResponse> save(CreateUserRequest request);
    Result<Void> update(UpdateUserRequest request);
    Result<Void> changePassword(ChangePasswordRequest request);
    Result<Void> delete(long id, CustomUserDetails customUserDetails);
}
