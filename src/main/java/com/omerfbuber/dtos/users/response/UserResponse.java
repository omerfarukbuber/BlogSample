package com.omerfbuber.dtos.users.response;

import com.omerfbuber.entities.User;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public record UserResponse(
        long id,
        String firstName,
        String lastName,
        String email,
        Date birthDate
) implements Serializable {
    
    public static UserResponse of(User user) {
        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getBirthDate());
    }

    public static List<UserResponse> of(List<User> users) {
        if (users == null)
            return Collections.emptyList();

        return users.stream().map(user -> UserResponse.of(user)).collect(Collectors.toList());
    }
}
