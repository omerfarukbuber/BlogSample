package com.omerfbuber.service.comment;

import com.omerfbuber.dto.comment.CommentResponse;
import com.omerfbuber.dto.comment.CreateCommentRequest;
import com.omerfbuber.dto.comment.UpdateCommentRequest;
import com.omerfbuber.entity.Article;
import com.omerfbuber.entity.Comment;
import com.omerfbuber.entity.User;
import com.omerfbuber.repository.ArticleRepository;
import com.omerfbuber.repository.CommentRepository;
import com.omerfbuber.result.Error;
import com.omerfbuber.result.Result;
import com.omerfbuber.service.user.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final CustomUserDetailsService customUserDetailsService;

    public CommentServiceImpl(CommentRepository commentRepository, ArticleRepository articleRepository,
                              CustomUserDetailsService customUserDetailsService) {
        this.commentRepository = commentRepository;
        this.articleRepository = articleRepository;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public Result<List<CommentResponse>> getCommentsByArticleId(long articleId) {
        var comments = commentRepository.findByArticleId(articleId);
        return Result.success(CommentResponse.of(comments));
    }

    @Override
    public Result<CommentResponse> getCommentById(long commentId) {
        var comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            log.warn("Comment with id {} not found when fetching", commentId);
            return Result.failure(Error.notFound("Comment.NotFound", "Comment could not be found"));
        }
        return Result.success(CommentResponse.of(comment));
    }

    @Override
    public Result<CommentResponse> save(CreateCommentRequest request) {

        User user = customUserDetailsService.getAuthenticatedUser();
        if (user == null) {
            log.warn("User not found when trying to like article with id {}", request.articleId());
            return Result.failure(Error.notFound("User.NotFound", "Authenticated user not found"));
        }

        Article article = articleRepository.findById(request.articleId()).orElse(null);
        if (article == null) {
            log.warn("Article not found when trying to like article with id {}", request.articleId());
            return Result.failure(Error.notFound("Article.NotFound", "Article not found"));
        }

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setArticle(article);
        comment.setContent(request.content());
        Comment entity = commentRepository.save(comment);

        return Result.created(CommentResponse.of(entity), "api/comments/" + entity.getId());
    }

    @Override
    public Result<Void> update(UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(request.id()).orElse(null);
        if (comment == null) {
            log.warn("Comment with id {} not found when updating", request.id());
            return Result.failure(Error.notFound("Comment.NotFound", "Comment could not be found"));
        }

        comment.setContent(request.content());
        commentRepository.save(comment);
        return Result.success();
    }

    @Override
    public Result<Void> delete(long commentId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            log.warn("Comment with id {} not found when deleting", commentId);
            return Result.failure(Error.notFound("Comment.NotFound", "Comment could not be found"));
        }
        comment.setDeleted(true);
        commentRepository.save(comment);

        return Result.success();
    }
}
