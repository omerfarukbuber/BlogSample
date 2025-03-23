package com.omerfbuber.controller;

import com.omerfbuber.dto.comment.CommentResponse;
import com.omerfbuber.dto.comment.CreateCommentRequest;
import com.omerfbuber.dto.comment.UpdateCommentRequest;
import com.omerfbuber.extension.ResponseEntityExtension;
import com.omerfbuber.service.comment.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable long id) {
        return ResponseEntityExtension.okOrProblem(commentService.getCommentById(id));
    }

    @GetMapping("/article/{id}")
    public ResponseEntity<List<CommentResponse>> getByArticleId(@PathVariable long id) {
        return ResponseEntityExtension.okOrProblem(commentService.getCommentsByArticleId(id));
    }

    @PostMapping
    public ResponseEntity<CommentResponse> create (@RequestBody CreateCommentRequest request) {
        return ResponseEntityExtension.createdOrProblem(commentService.save(request));
    }

    @PutMapping
    public ResponseEntity<Void> update (@RequestBody UpdateCommentRequest request) {
        return ResponseEntityExtension.noContentOrProblem(commentService.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete (@PathVariable long id) {
        return ResponseEntityExtension.noContentOrProblem(commentService.delete(id));
    }
}
