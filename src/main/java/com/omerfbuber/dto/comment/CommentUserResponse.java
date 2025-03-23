package com.omerfbuber.dto.comment;

import com.omerfbuber.entity.User;

import java.io.Serializable;

public record CommentUserResponse(long id, String firstName, String lastName) implements Serializable {
    public static CommentUserResponse of(User user) {
        return new CommentUserResponse(user.getId(), user.getFirstName(), user.getLastName());
    }
}
