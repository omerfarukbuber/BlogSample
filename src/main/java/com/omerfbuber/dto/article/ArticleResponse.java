package com.omerfbuber.dto.article;

import com.omerfbuber.entity.Article;
import com.omerfbuber.entity.User;

import java.io.Serializable;
import java.time.LocalDateTime;

public record ArticleResponse(long id, String title, String content, UserResponse user, LocalDateTime lastModifiedAt,
                              int likeCount, int commentCount) implements Serializable {
    public static ArticleResponse of(Article article) {
        return new ArticleResponse(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                UserResponse.of(article.getUser()),
                article.getUpdatedAt() == null ? article.getCreatedAt() : article.getUpdatedAt(),
                0,
                0);
    }

    public ArticleResponse setLikeAndCommentCount(int likeCount, int commentCount) {
        return new ArticleResponse(id, title, content, user, lastModifiedAt,likeCount, commentCount);
    }
}

record UserResponse(long id, String fistName, String lastName) implements Serializable {
    static UserResponse of(User user) {
        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName());
    }
}
