package com.omerfbuber.dtos.users.request;

public record ChangePasswordRequest(String email, String password, String newPassword, String confirmPassword) {
}
