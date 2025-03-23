package com.omerfbuber.service.comment;

import com.omerfbuber.dto.comment.CommentResponse;
import com.omerfbuber.dto.comment.CreateCommentRequest;
import com.omerfbuber.dto.comment.UpdateCommentRequest;
import com.omerfbuber.result.Result;

import java.util.List;

public interface CommentService {
    Result<List<CommentResponse>> getCommentsByArticleId(long articleId);
    Result<CommentResponse> getCommentById(long commentId);
    Result<CommentResponse> save(CreateCommentRequest request);
    Result<Void> update(UpdateCommentRequest request);
    Result<Void> delete(long commentId);
}
