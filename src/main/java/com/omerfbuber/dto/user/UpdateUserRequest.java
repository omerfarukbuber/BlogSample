package com.omerfbuber.dto.user;

import jakarta.validation.constraints.*;

import java.util.Date;

public record UpdateUserRequest(
        @Positive(message = "{validation.id.positive}")
        long id,

        @NotBlank(message = "{validation.firstName.notBlank}")
        @Size(min = 2, max = 50, message = "{validation.firstName.size}")
        String firstName,

        @NotBlank(message = "{validation.lastName.notBlank}")
        @Size(min = 2, max = 50, message = "{validation.lastName.size}")
        String lastName,

        @NotNull(message = "{validation.birthDate.notNull}")
        @Past(message = "{validation.birthDate.past}")
        Date birthDate
) { }
