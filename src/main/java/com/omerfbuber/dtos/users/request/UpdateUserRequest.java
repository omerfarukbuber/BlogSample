package com.omerfbuber.dtos.users.request;

import java.util.Date;

public record UpdateUserRequest(
        long id,
        String firstName,
        String lastName,
        Date birthDate
) { }
