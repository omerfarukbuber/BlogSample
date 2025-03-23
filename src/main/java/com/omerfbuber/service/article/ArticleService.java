package com.omerfbuber.service.article;

import com.omerfbuber.dto.article.ArticleResponse;
import com.omerfbuber.dto.article.CreateArticleRequest;
import com.omerfbuber.result.Result;

public interface ArticleService {
    Result<ArticleResponse> getArticle(long id);
    Result<ArticleResponse> save(CreateArticleRequest request);
}
