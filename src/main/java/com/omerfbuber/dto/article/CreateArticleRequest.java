package com.omerfbuber.dto.article;

import com.omerfbuber.entity.Article;

import java.util.Set;

public record CreateArticleRequest(String title, String content, Set<Integer> categoryIds) {
    public Article toArticle() {
        var article = new Article();
        article.setTitle(title);
        article.setContent(content);
        return article;
    }
}
