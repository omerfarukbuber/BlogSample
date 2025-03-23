package com.omerfbuber.dto.comment;

public record CreateCommentRequest(long articleId, String content) {
}
