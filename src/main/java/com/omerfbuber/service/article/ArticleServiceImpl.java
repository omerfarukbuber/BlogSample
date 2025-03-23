package com.omerfbuber.service.article;

import com.omerfbuber.dto.article.ArticleResponse;
import com.omerfbuber.dto.article.CreateArticleRequest;
import com.omerfbuber.entity.Article;
import com.omerfbuber.repository.ArticleLikeRepository;
import com.omerfbuber.repository.ArticleRepository;
import com.omerfbuber.repository.CategoryRepository;
import com.omerfbuber.repository.CommentRepository;
import com.omerfbuber.result.Error;
import com.omerfbuber.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final CommentRepository commentRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository, CategoryRepository categoryRepository,
                              ArticleLikeRepository articleLikeRepository, CommentRepository commentRepository) {
        this.articleRepository = articleRepository;
        this.categoryRepository = categoryRepository;
        this.articleLikeRepository = articleLikeRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public Result<ArticleResponse> getArticle(long id) {
        Article article = articleRepository.findById(id).orElse(null);
        if (article == null) {
            log.warn("Article with id {} not found when fetching", id);
            return Result.failure(Error.notFound("Article.NotFound", "Article could not be found"));
        }

        int likeCount = articleLikeRepository.countByArticleId(id);
        int commentCount = commentRepository.countByArticleId(id);

        ArticleResponse articleResponse = ArticleResponse.of(article).setLikeAndCommentCount(likeCount, commentCount);
        return Result.success(articleResponse);
    }

    @Override
    public Result<ArticleResponse> save(CreateArticleRequest request) {
        var categories = categoryRepository.findCategoriesByIdIn(request.categoryIds());
        if (categories.isEmpty()){
            log.warn("At least one category id is required when saving article");
            return Result.failure(Error.failure("Categories.Empty", "At least one category id is required"));
        }

        Article article = request.toArticle();
        article.setCategory(categories);
        Article entity = articleRepository.save(article);
        ArticleResponse articleResponse = ArticleResponse.of(entity);

        return Result.created(articleResponse, "api/articles/" + articleResponse.id());
    }
}
