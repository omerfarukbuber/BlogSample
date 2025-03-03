package com.omerfbuber.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(

        @Email(message = "{validation.email.invalid}")
        @NotBlank(message = "{validation.email.notBlank}")
        String email,

        @NotBlank(message = "{validation.password.notBlank}")
        @Size(min = 6, message = "{validation.password.size}")
        String password,

        @NotBlank(message = "{validation.newPassword.notBlank}")
        @Size(min = 6, message = "{validation.newPassword.size}")
        String newPassword,

        @NotBlank(message = "{validation.confirmPassword.notBlank}")
        @Size(min = 6, message = "{validation.confirmPassword.size}")
        String confirmPassword) {
}
