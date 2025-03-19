package com.omerfbuber.dto.comment;

import com.omerfbuber.entity.Comment;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record CommentResponse(long id, long articleId, CommentUserResponse user,
                              LocalDateTime lastModifiedAt, boolean isModified, boolean isDeleted,
                              String content) implements Serializable {
    public static CommentResponse of(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getArticle().getId(),
                CommentUserResponse.of(comment.getUser()),
                comment.getUpdatedAt() == null ? comment.getCreatedAt() : comment.getUpdatedAt(),
                comment.getUpdatedAt() != null,
                comment.isDeleted(),
                comment.getContent());
    }

    public static List<CommentResponse> of(List<Comment> comments) {
        return comments.stream()
                .map(CommentResponse::of)
                .collect(Collectors.toList());
    }
}
