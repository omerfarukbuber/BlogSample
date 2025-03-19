package com.omerfbuber.service.articlelike;

import com.omerfbuber.dto.articlelike.DislikeRequest;
import com.omerfbuber.dto.articlelike.LikeRequest;
import com.omerfbuber.dto.articlelike.LikeResponse;
import com.omerfbuber.entity.Article;
import com.omerfbuber.entity.ArticleLike;
import com.omerfbuber.entity.User;
import com.omerfbuber.repository.ArticleLikeRepository;
import com.omerfbuber.repository.ArticleRepository;
import com.omerfbuber.result.Error;
import com.omerfbuber.result.Result;
import com.omerfbuber.service.user.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ArticleLikeServiceImpl implements ArticleLikeService {

    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleRepository articleRepository;
    private final CustomUserDetailsService customUserDetailsService;

    public ArticleLikeServiceImpl(ArticleLikeRepository articleLikeRepository, ArticleRepository articleRepository,
                                  CustomUserDetailsService customUserDetailsService) {
        this.articleLikeRepository = articleLikeRepository;
        this.articleRepository = articleRepository;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public Result<LikeResponse> likeArticle(LikeRequest request) {

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

        if (articleLikeRepository.existsByArticleIdAndUserId(article.getId(), user.getId())) {
            log.warn("Article with id {} already liked", article.getId());
            return Result.failure(Error.conflict("Article.AlreadyLiked", "Article already liked"));
        }

        ArticleLike articleLike = new ArticleLike();
        articleLike.setArticle(article);
        articleLike.setUser(user);
        articleLikeRepository.save(articleLike);

        int likeCount = articleLikeRepository.countByArticleId(article.getId());
        return Result.success(new LikeResponse(article.getId(), likeCount));
    }

    @Override
    public Result<LikeResponse> dislikeArticle(DislikeRequest request) {

        User user = customUserDetailsService.getAuthenticatedUser();
        if (user == null) {
            log.warn("User not found when trying to dislike article with id {}", request.articleId());
            return Result.failure(Error.notFound("User.NotFound", "Authenticated user not found"));
        }

        Article article = articleRepository.findById(request.articleId()).orElse(null);
        if (article == null) {
            log.warn("Article not found when trying to dislike article with id {}", request.articleId());
            return Result.failure(Error.notFound("Article.NotFound", "Article not found"));
        }

        if (!articleLikeRepository.existsByArticleIdAndUserId(article.getId(), user.getId())) {
            log.warn("Article with id {} not liked before", article.getId());
            return Result.failure(Error.failure("Article.NotLikedBefore", "Article not liked before"));
        }

        articleLikeRepository.deleteByArticleIdAndUserId(article.getId(), user.getId());

        int likeCount = articleLikeRepository.countByArticleId(article.getId());
        return Result.success(new LikeResponse(article.getId(), likeCount));
    }

    @Override
    public Result<LikeResponse> getLikeCount(long articleId) {
        Article article = articleRepository.findById(articleId).orElse(null);
        if (article == null) {
            log.warn("Article not found when trying to get like count of article with id {}", articleId);
            return Result.failure(Error.notFound("Article.NotFound", "Article not found"));
        }

        int likeCount = articleLikeRepository.countByArticleId(article.getId());
        return Result.success(new LikeResponse(article.getId(), likeCount));
    }
}
