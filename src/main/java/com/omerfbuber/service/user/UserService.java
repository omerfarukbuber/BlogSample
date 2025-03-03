package com.omerfbuber.service.user;

import com.omerfbuber.dto.user.ChangePasswordRequest;
import com.omerfbuber.dto.user.CreateUserRequest;
import com.omerfbuber.dto.user.UpdateUserRequest;
import com.omerfbuber.dto.user.UserResponse;
import com.omerfbuber.result.Result;
import com.omerfbuber.service.shared.CustomUserDetails;

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
