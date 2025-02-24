package com.omerfbuber.dtos.users.request;

import com.omerfbuber.entities.User;
import jakarta.validation.constraints.*;

import java.util.Date;

public record CreateUserRequest (
        @NotBlank(message = "{validation.firstName.notBlank}")
        @Size(min = 2, max = 50, message = "{validation.firstName.size}")
        String firstName,

        @NotBlank(message = "{validation.lastName.notBlank}")
        @Size(min = 2, max = 50, message = "{validation.lastName.size}")
        String lastName,

        @Email(message = "{validation.email.invalid}")
        @NotBlank(message = "{validation.email.notBlank}")
        String email,

        @NotBlank(message = "{validation.password.notBlank}")
        @Size(min = 6, message = "{validation.password.size}")
        String password,

        @NotBlank(message = "{validation.confirmPassword.notBlank}")
        @Size(min = 6, message = "{validation.confirmPassword.size}")
        String confirmPassword,

        @NotNull(message = "{validation.birthDate.notNull}")
        @Past(message = "{validation.birthDate.past}")
        Date birthDate
) {
    public User toUser()
    {
        return new User(
                null,
                email,
                password,
                firstName,
                lastName,
                birthDate
        );
    }
}
