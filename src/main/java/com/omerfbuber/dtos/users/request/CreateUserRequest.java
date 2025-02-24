package com.omerfbuber.dtos.users.request;

import com.omerfbuber.entities.User;

import java.util.Date;

public record CreateUserRequest (
    String firstName,
    String lastName,
    String email,
    String password,
    String confirmPassword,
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
